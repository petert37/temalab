package service;

import bean.ImageBean;
import bean.OperatorBean;
import com.google.gson.Gson;
import entity.ImageDescription;
import entity.ImageUrl;
import model.PreviewImage;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.UUID;

@Path("/rayimg")
public class UserRequest {

    @EJB
    private OperatorBean operatorBean;

    @EJB
    private ImageBean imageBean;

//    @GET
//    @Path("{imgID}")
//    @Produces("image/png")
//    public byte[] getImageDescription(@PathParam("imgID") long imgID) {
//        ImageDescription image = operatorBean.loadImageDescription(imgID);
//        if (image == null) return "not exist".getBytes();
//        if (image.getPng() == null) return "in progress".getBytes();
//        return image.getPng();
//    }

    @GET
    @Path("/allImg")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllImage() {
        ArrayList<ImageUrl> images = new ArrayList<>(operatorBean.findAllDoneImageUrls());
        ArrayList<PreviewImage> pImages = new ArrayList<>();
        for (ImageUrl i : images) {
            pImages.add(new PreviewImage(i.getUid()));
        }
        return new Gson().toJson(pImages);
    }

    @POST
    public String httpForm(String inputJsonObj) {

        String response = "{\"link\":\"valami_hiba_van_az_éterben...\"}";

        try {
            ImageDescription image = new ImageDescription();
            image.setWorld(inputJsonObj);
            operatorBean.storeImageDescription(image);
            ImageUrl imageUrl = new ImageUrl(UUID.randomUUID().toString(), image);
            operatorBean.storeImageUrl(imageUrl);
            imageBean.putInMS(image.getId());

            response = "{\"link\":\"/img/" + imageUrl.getUid() + "\"}";
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return response;
    }
}
