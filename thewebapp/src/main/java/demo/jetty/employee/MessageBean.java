package demo.jetty.employee;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class MessageBean implements Serializable {

    public String getMessage(String name){
        return "Hello, " + name;
    }
}
