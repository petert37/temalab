package bean;

import hu.vkrissz.bme.raytracer.model.InputParams;
import hu.vkrissz.bme.raytracer.model.RenderPart;

import javax.ejb.Stateless;

/**
 * Created by vkrissz on 2016.11.23..
 */
@Stateless
public class RenderBean {

    public RenderPart render(InputParams params){
        return params.renderImagePart();
    }
}
