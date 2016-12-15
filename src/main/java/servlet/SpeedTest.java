package servlet;

import bean.ImageSplitterBean;
import com.google.gson.Gson;
import config.Config;
import hu.vkrissz.bme.raytracer.model.InputParams;
import measure.ElapsedTime;

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

        ElapsedTime parseWorld = new ElapsedTime(ElapsedTime.JSON_PARSE_WORLD);
        InputParams inputParams = new Gson().fromJson(req.getParameter("data"), InputParams.class);
        parseWorld.end();
        Config config = imageSplitterBean.getConfigFromUrl("http://pastebin.com/raw/aKzEqfL0");
        System.out.println(new Gson().toJson(config));
        ImageSplitterBean.PARTS_PER_REQUEST = config.getPartsPerRequest();
        ImageSplitterBean.PIXELS_PER_REQUEST = config.getPixelsPerRequest();
        ImageSplitterBean.MAX_CONNECTIONS = config.getMaxConnections();
        BufferedImage bufferedImage = imageSplitterBean.getImage(inputParams);
        ElapsedTime compress = new ElapsedTime(ElapsedTime.COMPRESS_STREAM);
        ImageIO.write(bufferedImage, "PNG", resp.getOutputStream());
        compress.end();

    }
}
