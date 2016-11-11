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
import java.util.List;

public class ImagePartThread extends Thread {

    private CloseableHttpClient client;
    private HttpContext context;
    private HttpPost httpPost;

    private List<RenderPart> list;

    public ImagePartThread(CloseableHttpClient client, HttpPost httpPost, List<RenderPart> list) {
        this.client = client;
        this.httpPost = httpPost;
        this.list = list;
        this.context = new BasicHttpContext();
    }

    @Override
    public void run() {

        try (CloseableHttpResponse response = client.execute(httpPost, context)) {
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try (ObjectInputStream ois = new ObjectInputStream(entity.getContent())) {
//                    RenderPart renderPart = new RenderPart(entity.getContent());
                    RenderPart renderPart = (RenderPart) ois.readObject();
                    if (renderPart != null) {
                        list.add(renderPart);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
