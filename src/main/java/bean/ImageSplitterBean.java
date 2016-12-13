package bean;

import com.google.gson.Gson;
import config.Config;
import hu.vkrissz.bme.raytracer.RayTracer;
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
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.imageio.ImageIO;
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

@Stateless
public class ImageSplitterBean {

    private static int MAX_CONNECTIONS = 100;

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
            HttpResponse response = client.execute(request);
            return new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage getImage(InputParams inputParams) {
        ElapsedTime fullImageTime = new ElapsedTime(ElapsedTime.FULL_IMAGE_RENDER);

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
                HttpPost post = new HttpPost(serverUrl);
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

        BufferedImage bufferedImage = RayTracer.compose(inputParams.imageWidth, inputParams.imageHeight, renderParts);
        fullImageTime.end();
        return bufferedImage;
    }
}
