package thread;

import hu.vkrissz.bme.raytracer.model.RenderPart;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImagePartThread extends Thread {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS");

    private CloseableHttpClient client;
    private HttpContext context;
    private HttpPost httpPost;

    private List<RenderPart> list;

    public ImagePartThread(CloseableHttpClient client, HttpPost httpPost, List<RenderPart> list) {
        this.client = client;
        this.httpPost = httpPost;
        this.list = list;
        this.context = new BasicHttpContext();
        System.out.println("Render part thread " + format.format(new Date(System.currentTimeMillis())) + " " + this.getName() + "  " + this);
    }

    @Override
    public void run() {

        System.out.println("Render part thread RUN " + format.format(new Date(System.currentTimeMillis())) + " " + this.getName() + "  " + this);
        try (CloseableHttpResponse response = client.execute(httpPost, context)) {
            System.out.println("Render part READ " + format.format(new Date(System.currentTimeMillis())) + " " + this.getName() + "  " + this);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try (ObjectInputStream ois = new ObjectInputStream(entity.getContent())) {
//                    RenderPart renderPart = new RenderPart(entity.getContent());
                    RenderPart renderPart = (RenderPart) ois.readObject();
                    if (renderPart != null) {
                        list.add(renderPart);
                    }

                    System.out.println("Render part END " + format.format(new Date(System.currentTimeMillis())) + " " + this.getName() + "  " + this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
