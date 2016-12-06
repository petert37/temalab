package servlet;

import entity.ImageDescription;

import javax.servlet.annotation.WebServlet;


@WebServlet("/img/*")
public class AccessImage extends BaseImageServlet {
    @Override
    protected byte[] getImage(ImageDescription image) {
        if (image.getImage() != null)
            return image.getImage().getData();
        return null;
    }
}
