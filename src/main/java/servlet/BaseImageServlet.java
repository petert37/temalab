package servlet;

import bean.OperatorBean;
import entity.ImageDescription;
import entity.ImageUrl;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vkrissz on 2016.12.06..
 */
public abstract class BaseImageServlet extends HttpServlet {
    @EJB
    protected OperatorBean operatorBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String id = request.getPathInfo().substring(1);
            ImageUrl imageUrl = loadImageUrl(id);
            if (imageUrl == null || imageUrl.getImageDescription() == null) {
                request.getRequestDispatcher("/noMatchingImage.html").forward(request, response);
            } else {
                byte[] bytes = getImage(imageUrl.getImageDescription());
                if (bytes != null) {
                    response.setContentType("image/png");
                    response.getOutputStream().write(bytes);
                    response.getOutputStream().flush();
                } else {
                    request.getRequestDispatcher("/processingImage.html").forward(request, response);
                }
            }
        } else {
            request.getRequestDispatcher("/noMatchingImage.html").forward(request, response);
        }
    }

    protected abstract byte[] getImage(ImageDescription image);

    protected abstract ImageUrl loadImageUrl(String id);
}