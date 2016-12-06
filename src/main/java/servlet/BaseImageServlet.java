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
    private OperatorBean operatorBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String id = request.getPathInfo().substring(1);
            ImageUrl imageUrl = operatorBean.loadImageUrlWithImage(id);
            if (imageUrl == null || imageUrl.getImage() == null) {
                request.getRequestDispatcher("/noMatchingImage.html").forward(request, response);
            } else {
                byte[] bytes = getImage(imageUrl.getImage());
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
}