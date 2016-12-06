package bean;

import com.google.gson.Gson;
import config.Config;
import entity.*;
import entity.Image;
import hu.vkrissz.bme.raytracer.RayTracer;
import hu.vkrissz.bme.raytracer.model.InputParams;
import hu.vkrissz.bme.raytracer.model.RenderPart;
import net.coobird.thumbnailator.Thumbnailator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import thread.ImagePartRunnable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.imageio.ImageIO;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@MessageDriven
public class ImageSplitterBean implements MessageListener {


    @PostConstruct
    public void ads() {
        System.out.println("construct_onMessage");
    }

    @PreDestroy
    public void adsd() {
        System.out.println("destroy_onMessage");
    }

    private static final boolean REMOTE_CONFIG = true;

    private static int MAX_CONNECTIONS = 100;
    private static int IMAGE_DIVISION = 300;
    private static String IMAGE_PART_URL = "http://localhost:8080/ImagePart";

    //    public static final String CONFIG_URL = "http://pastebin.com/raw/uSJbJ7AX"; //localhost
    private static final String CONFIG_URL = "http://pastebin.com/raw/8D25bRdg";

    @Resource(name = "ImagePartExecutorService")
    private ManagedExecutorService executorService;

    @EJB
    private OperatorBean operatorBean;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            long imgID = Long.parseLong(textMessage.getText());
            ImageDescription image = operatorBean.loadImageDescription(imgID);
            System.out.println("dolgozok... " + image.getId());

            setupParameters();
            InputParams inputParams = new Gson().fromJson(image.getWorld(), InputParams.class);

            BufferedImage bfImage = getImage(inputParams);
            double scale = 200.0 / (double) Math.max(bfImage.getWidth(), bfImage.getHeight());
            BufferedImage thumbnail = Thumbnailator.createThumbnail(bfImage, (int) Math.round(scale * bfImage.getWidth()), (int) Math.round(scale * bfImage.getHeight()));
            byte[] fullImageBytes = imageToBytes(bfImage);
            Image fullImage = new Image(fullImageBytes);
            operatorBean.storeImage(fullImage);
            byte[] thumbImageBytes = imageToBytes(thumbnail);
            Image thumbnailImage = new Image(thumbImageBytes);
            operatorBean.storeImage(thumbnailImage);
            image = operatorBean.loadImageDescription(imgID);
            image.setImage(fullImage);
            image.setThumbnail(thumbnailImage);
            System.out.println("FINISH: " + image.getId());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private byte[] imageToBytes(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] bytes = baos.toByteArray();
            baos.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage scale(BufferedImage source, double ratio) {
        int w = (int) (source.getWidth() * ratio);
        int h = (int) (source.getHeight() * ratio);
        BufferedImage bi = getCompatibleImage(w, h);
        Graphics2D g2d = bi.createGraphics();
        double xScale = (double) w / source.getWidth();
        double yScale = (double) h / source.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
        g2d.drawRenderedImage(source, at);
        g2d.dispose();
        return bi;
    }

    private BufferedImage getCompatibleImage(int w, int h) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        return gc.createCompatibleImage(w, h);
    }

    private void setupParameters() {
        MAX_CONNECTIONS = 100;
        IMAGE_DIVISION = 300;
        IMAGE_PART_URL = "http://localhost:8080/ImagePart";
//        IMAGE_PART_URL = "http://raytrace.j.layershift.co.uk/raytrace/ImagePart";

        if (REMOTE_CONFIG) {
            Config config = getConfigFromUrl(CONFIG_URL);
            if (config != null) {
                //MAX_CONNECTIONS = config.getMaxConnections();
                IMAGE_DIVISION = config.getImageDivision();
                IMAGE_PART_URL = config.getImagePartUrl();
                System.out.println("Config from url: " + config);
            }
        }
    }

    private Config getConfigFromUrl(String url) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            return new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage getImage(InputParams inputParams) {

        int imagePartHeight = Math.max(1, 50000 / inputParams.imageWidth);
        int requestCount = inputParams.imageHeight / imagePartHeight;
        if (inputParams.imageHeight % imagePartHeight != 0) {
            requestCount++;
        }

        inputParams.startX = 0;
        inputParams.startY = 0;
        inputParams.width = inputParams.imageWidth;
        inputParams.height = imagePartHeight;

        List<RenderPart> renderParts = Collections.synchronizedList(new ArrayList<RenderPart>());

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(MAX_CONNECTIONS);
        cm.setMaxTotal(MAX_CONNECTIONS);


        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(12 * 60 * 1000)
                .setSocketTimeout(12 * 60 * 1000)
                .setConnectionRequestTimeout(12 * 60 * 1000)
                .build();
        //CloseableHttpClient client = HttpClients.custom().setConnectionManager(cm).build();
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(cm).build();

        List<Future> futures = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < requestCount; i++) {
            inputParams.startY = i * imagePartHeight;
            int remainingImageHeight = inputParams.imageHeight - inputParams.startY;
            inputParams.height = Math.min(imagePartHeight, remainingImageHeight);

            try {
                HttpPost post = new HttpPost(IMAGE_PART_URL);
                post.setEntity(new StringEntity(new Gson().toJson(inputParams)));
                futures.add(executorService.submit(new ImagePartRunnable(client, post, renderParts)));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        for (Future f : futures) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return RayTracer.compose(inputParams.imageWidth, inputParams.imageHeight, renderParts);
    }
}
