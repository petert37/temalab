package servlet;

import bean.OperatorBean;
import entity.Image;
import entity.ImageUrl;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;




@WebServlet("/img")
public class AccessImage extends HttpServlet {
    @EJB
    private OperatorBean operatorBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        ImageUrl imageUrl = operatorBean.loadImageUrl(id);
        if(imageUrl == null || imageUrl.getImage() == null){
            request.getRequestDispatcher("noMatchingImage.html").forward(request, response);
        } else {
            byte[] bytes = imageUrl.getImage().getPng();
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
