package servlet;

import entity.ImageDescription;

import javax.servlet.annotation.WebServlet;

/**
 * Created by vkrissz on 2016.12.04..
 */
@WebServlet("/thumb/*")
public class AccessImageThumb extends BaseImageServlet {
    @Override
    protected byte[] getImage(ImageDescription image) {
        if (image.getImage() != null)
            return image.getThumbnail().getData();
        return null;
    }
}