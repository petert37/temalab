package servlet;

import bean.OperatorBean;
import entity.Image;
import entity.ImageUrl;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vkrissz on 2016.12.04..
 */
@WebServlet("/thumb")
public class AccessImageThumb extends HttpServlet {
    @EJB
    private OperatorBean operatorBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        ImageUrl imageUrl = operatorBean.loadImageUrl(id);
        if(imageUrl == null || imageUrl.getImage() == null){
            request.getRequestDispatcher("noMatchingImage.html").forward(request, response);
        } else {
            byte[] bytes = imageUrl.getImage().getPreview();
            if(bytes != null) {
                response.setContentType("image/png");
                response.getOutputStream().write(bytes);
                response.getOutputStream().flush();
            } else {
                request.getRequestDispatcher("processingImage.html").forward(request, response);
            }
        }
    }
}