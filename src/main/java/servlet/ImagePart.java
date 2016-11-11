package servlet;

import hu.vkrissz.bme.raytracer.RayTracer;
import hu.vkrissz.bme.raytracer.model.RenderPart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectOutputStream;

@WebServlet("/ImagePart")
public class ImagePart extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RenderPart renderPart = RayTracer.render(request.getInputStream());
        try (ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream())) {
            oos.writeObject(renderPart);
        }
    }
}
