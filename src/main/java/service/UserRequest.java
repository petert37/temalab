package service;

import bean.ImageBean;
import bean.OperatorBean;
import com.google.gson.Gson;
import config.Config;
import entity.Image;
import model.PreviewImage;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

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

    @GET
    @Path("/allImg")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllImage() {
        ArrayList<Image> images = new ArrayList<>(operatorBean.findAll());
        ArrayList<PreviewImage> pImages = new ArrayList<>();
        for (Image i : images) {
            pImages.add(new PreviewImage(i.getId(), i.getPreview(), Config.SRV_URL + "/" + i.getId()));
        }
        return new Gson().toJson(pImages);
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
