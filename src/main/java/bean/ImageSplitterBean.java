package bean;

import com.google.gson.Gson;
import config.Config;
import hu.vkrissz.bme.raytracer.RayTracer;
import hu.vkrissz.bme.raytracer.model.ImageSlice;
import hu.vkrissz.bme.raytracer.model.InputParams;
import hu.vkrissz.bme.raytracer.model.RenderPart;
import measure.ElapsedTime;
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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Stateless
public class ImageSplitterBean {

    public static volatile int MAX_CONNECTIONS = 1000;
    public static volatile int PARTS_PER_REQUEST = 100;
    public static volatile int PIXELS_PER_REQUEST = 11500;

    @Resource(name = "ServerUrl")
    private String serverUrl;

    @Resource(name = "ImagePartExecutorService")
    private ManagedExecutorService executorService;

    @EJB
    private OperatorBean operatorBean;


    public byte[] imageToBytes(BufferedImage image) {
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

    public BufferedImage scale(BufferedImage source, double ratio) {
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

    public BufferedImage getCompatibleImage(int w, int h) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        return gc.createCompatibleImage(w, h);
    }

    public Config getConfigFromUrl(String url) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            request.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            request.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            HttpResponse response = client.execute(request);
            return new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public BufferedImage getImage(InputParams inputParams) {
        ElapsedTime fullImageTime = new ElapsedTime(ElapsedTime.FULL_IMAGE_RENDER);
//
//        int imagePartHeight = Math.max(1, 11600 / inputParams.imageWidth);
//        int requestCount = inputParams.imageHeight / imagePartHeight;
//        if (inputParams.imageHeight % imagePartHeight != 0) {
//            requestCount++;
//        }

        int sliceSize = PIXELS_PER_REQUEST / PARTS_PER_REQUEST;
        int side = (int) Math.round(Math.sqrt(sliceSize));
        int height = Math.min(side * 2, inputParams.imageHeight);
        int width = Math.min(Math.max(sliceSize / height, 1), inputParams.imageWidth);
        List<ImageSlice> slices = new LinkedList<>();
        for (int i = 0; i < inputParams.imageWidth; i += width) {
            for (int j = 0; j < inputParams.imageHeight; j += height) {
                slices.add(new ImageSlice(i, j, Math.min(width, inputParams.imageWidth - i), Math.min(height, inputParams.imageHeight - j)));
            }
        }

        List<RenderPart> renderParts = new ArrayList<>();

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
        int requests = 0;
        int sliceNum = slices.size();
        Random random = new Random();
        ArrayList<ImageSlice> tmpSlices = new ArrayList<>(PARTS_PER_REQUEST);
        while (slices.size() > 0) {
            tmpSlices.clear();
            for (int i = 0; i < PARTS_PER_REQUEST && slices.size() > 0; i++) {
                final int idx = random.nextInt(slices.size());
                ImageSlice slice = slices.get(idx);
                tmpSlices.add(slice);
                slices.remove(idx);
            }
            inputParams.slices = tmpSlices.toArray(new ImageSlice[tmpSlices.size()]);
            try {
                HttpPost post = new HttpPost(serverUrl);
                post.setEntity(new StringEntity(new Gson().toJson(inputParams)));
                futures.add(executorService.submit(new ImagePartRunnable(client, post, renderParts)));
                requests++;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        System.out.println("SLICES: " + sliceNum + " (" + width + "x" + height + ")" + " REQUESTS: " + requests);

        for (Future f : futures) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        BufferedImage bufferedImage = RayTracer.compose(inputParams.imageWidth, inputParams.imageHeight, renderParts);
        fullImageTime.end();
        return bufferedImage;
    }
}
