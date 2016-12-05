package thread;

import hu.vkrissz.bme.raytracer.model.RenderPart;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ImagePartRunnable implements Runnable {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS");

    private CloseableHttpClient client;
    private HttpContext context;
    private HttpPost httpPost;

    private List<RenderPart> list;

    public ImagePartRunnable(CloseableHttpClient client, HttpPost httpPost, List<RenderPart> list) {
        this.client = client;
        this.httpPost = httpPost;
        this.list = list;
        this.context = new BasicHttpContext();
//        System.out.println("Render part thread " + format.format(new Date(System.currentTimeMillis())) + " " + Thread.currentThread().getName() + "  " + this);
    }

    @Override
    public void run() {
        int tries = 0;
        while (tries < 30) {
            if(tries != 0){
                try {
                    Thread.sleep(tries * 5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//        System.out.println("Render part thread RUN " + format.format(new Date(System.currentTimeMillis())) + " " + Thread.currentThread().getName() + "  " + this);
            try (CloseableHttpResponse response = client.execute(httpPost, context)) {
//            System.out.println("Render part READ " + format.format(new Date(System.currentTimeMillis())) + " " + Thread.currentThread().getName() + "  " + this);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    try (ObjectInputStream ois = new ObjectInputStream(entity.getContent())) {
                        RenderPart renderPart = (RenderPart) ois.readObject();
                        if (renderPart != null) {
                            list.add(renderPart);
                            return;
                        }
//                    System.out.println("Render part END " + format.format(new Date(System.currentTimeMillis())) + " " + Thread.currentThread().getName() + "  " + this);
                    } catch (StreamCorruptedException ex) {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            tries++;
        }
    }
}
