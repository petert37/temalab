package service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/request")
public class UserRequest {

    @POST
    public String httpForm(String inputJsonObj) {


        System.out.println("fuuut");
        System.out.println(inputJsonObj);


        return "{\"link\":\"http://link.hu/kep/024322\"}";
    }


}
