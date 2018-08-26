package demo.jetty.employee;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.weld.util.collections.ImmutableList;

/**
 * Created by michaelbenoit on 02.12.15.
 */
@Named
@ApplicationScoped
@Path("employees")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class EmployeesResource {

    @Inject private MessageBean messageBean;

    @Inject private BeanManager manager;

    @GET
    @Path("{id}")
    public Employee find(@PathParam("id") long id) {
        return new Employee();
    }

    @GET
    public List<Employee> all() {
        Employee employee = new Employee();
        employee.setFirstName(messageBean.getMessage( "aaaaa "));
        employee.setSurName(String.valueOf(manager));
        return ImmutableList.of(employee);
    }

    @POST
    public Response create(Employee employee) {
        try {
            return Response
                .seeOther(new URI("employees/" + employee.getId()))
                .build();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to create", e);
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id, Employee employee) {
        try {
            return Response
                .seeOther(new URI("employees/" + employee.getId()))
                .build();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to update", e);
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id) {
        try {
            return Response.ok().build();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to update", e);
        }
    }
}
