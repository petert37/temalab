package servlet;

import entity.ImageDescription;

import javax.servlet.annotation.WebServlet;


@WebServlet("/img/*")
public class AccessImage extends BaseImageServlet {
    @Override
    protected byte[] getImage(ImageDescription image) {
        return image.getImage().getData();
    }
}
