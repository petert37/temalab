package servlet;

import bean.RenderBean;
import com.google.gson.Gson;
import hu.vkrissz.bme.raytracer.model.InputParams;
import hu.vkrissz.bme.raytracer.model.RenderPart;
import measure.ElapsedTime;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@WebServlet("/ImagePart")
public class ImagePart extends HttpServlet {

    private static final Gson gson = new Gson();

    @EJB
    RenderBean renderBean;

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uuid = UUID.randomUUID().toString() + "  " + Thread.currentThread().getName() + "  " + Thread.currentThread();
        System.out.println("Request image: " + format.format(new Date(System.currentTimeMillis())) + "  " + uuid);
        ElapsedTime parseTime = new ElapsedTime(ElapsedTime.JSON_PARSE_PART);
        InputParams params = gson.fromJson(new InputStreamReader(request.getInputStream()), InputParams.class);
        parseTime.end();
        ElapsedTime renderTime = new ElapsedTime(ElapsedTime.RENDER);
        RenderPart[] renderParts = renderBean.render(params);
        renderTime.end();
        System.out.println("Request render ended: " + format.format(new Date(System.currentTimeMillis()))+ "  " + uuid);
        ElapsedTime sendTime = new ElapsedTime(ElapsedTime.SEND_IMAGE_PART);
        try (ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream())) {
            oos.writeObject(renderParts);
            oos.flush();
        }
        sendTime.end();
        System.out.println("Request stream ended: " + format.format(new Date(System.currentTimeMillis())) + "  " + uuid);
    }
}
