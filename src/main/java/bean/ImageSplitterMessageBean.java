package bean;

import com.google.gson.Gson;
import entity.Image;
import entity.ImageDescription;
import hu.vkrissz.bme.raytracer.model.InputParams;
import net.coobird.thumbnailator.Thumbnailator;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.awt.image.BufferedImage;

@MessageDriven
public class ImageSplitterMessageBean implements MessageListener {

    @EJB
    ImageSplitterBean imageSplitterBean;

    @EJB
    OperatorBean operatorBean;


    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            long imgID = Long.parseLong(textMessage.getText());
            ImageDescription image = operatorBean.loadImageDescriptionWithWorld(imgID);
            System.out.println("dolgozok... " + image.getId());

            InputParams inputParams = new Gson().fromJson(image.getWorld(), InputParams.class);

            BufferedImage bfImage = imageSplitterBean.getImage(inputParams);

            double scale = 200.0 / (double) Math.max(bfImage.getWidth(), bfImage.getHeight());
            BufferedImage thumbnail = Thumbnailator.createThumbnail(bfImage, (int) Math.round(scale * bfImage.getWidth()), (int) Math.round(scale * bfImage.getHeight()));
            byte[] fullImageBytes = imageSplitterBean.imageToBytes(bfImage);
            Image fullImage = new Image(fullImageBytes);
            operatorBean.storeImage(fullImage);
            byte[] thumbImageBytes = imageSplitterBean.imageToBytes(thumbnail);
            Image thumbnailImage = new Image(thumbImageBytes);
            operatorBean.storeImage(thumbnailImage);
            image = operatorBean.loadImageDescription(imgID);
            image.setImage(fullImage);
            image.setThumbnail(thumbnailImage);
            System.out.println("FINISH: " + image.getId());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
