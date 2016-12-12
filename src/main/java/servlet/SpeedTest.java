package servlet;

import bean.ImageSplitterBean;
import com.google.gson.Gson;
import hu.vkrissz.bme.raytracer.model.InputParams;

import javax.ejb.EJB;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@WebServlet("/nodb")
public class SpeedTest extends HttpServlet {

    @EJB
    ImageSplitterBean imageSplitterBean;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        InputParams inputParams = new Gson().fromJson(req.getParameter("data"), InputParams.class);

        BufferedImage bufferedImage = imageSplitterBean.getImage(inputParams);

        ImageIO.write(bufferedImage, "PNG", resp.getOutputStream());

    }
}
