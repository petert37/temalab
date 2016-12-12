package servlet;

import entity.ImageDescription;
import entity.ImageUrl;

import javax.servlet.annotation.WebServlet;

/**
 * Created by vkrissz on 2016.12.04..
 */
@WebServlet("/thumb/*")
public class AccessImageThumb extends BaseImageServlet {
    @Override
    protected byte[] getImage(ImageDescription image) {
        if (image.getThumbnail() != null)
            return image.getThumbnail().getData();
        return null;
    }

    @Override
    protected ImageUrl loadImageUrl(String id) {
        return operatorBean.loadImageUrlWithThumbnail(id);
    }
}