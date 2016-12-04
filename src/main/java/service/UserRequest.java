package service;

import bean.ImageBean;
import bean.OperatorBean;
import config.Config;
import entity.Image;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.ws.rs.*;

@Path("/rayimg")
public class UserRequest {

    @EJB
    private OperatorBean operatorBean;

    @EJB
    private ImageBean imageBean;

    @GET
    @Path("{imgID}")
    @Produces("image/png")
    public byte[] getImage(@PathParam("imgID") long imgID) {
        Image image = operatorBean.loadImage(imgID);
        if (image == null) return "not exist".getBytes();
        if (image.getPng() == null) return "in progress".getBytes();
        return image.getPng();
    }

    @POST
    public String httpForm(String inputJsonObj) {

        String response = "{\"link\":\"valami_hiba_van_az_éterben...\"}";

        try {
            Image image = new Image();
            image.setWorld(inputJsonObj);
            operatorBean.storeImage(image);

            imageBean.putInMS(image.getId());

            response = "{\"link\":\"" + Config.SRV_URL + "/" + image.getId() + "\"}";
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return response;
    }
}
