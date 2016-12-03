package bean;

import hu.vkrissz.bme.raytracer.model.InputParams;
import hu.vkrissz.bme.raytracer.model.RenderPart;

import javax.ejb.Stateless;

@Stateless
public class RenderBean {

    public RenderPart render(InputParams params) {
        return params.renderImagePart();
    }
}
