package de.radiohacks.frinmeba.services;

import java.sql.Connection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;

import de.radiohacks.frinmeba.database.MyConnection;

@Provider
@PreMatching
public class FrinmebaAuthFilter implements ContainerRequestFilter {
    /**
     * Apply the filter : check input request, validate or not with user auth
     * 
     * @param containerRequest
     *            The request from Tomcat server
     */
    @Override
    public void filter(ContainerRequestContext containerRequest)
            throws WebApplicationException {
        
        String method = containerRequest.getMethod();
        String path = containerRequest.getUriInfo().getPath(true);
        
        // Allow Signup without credentials
        if (method.equals("POST") && path.endsWith("user/signup")) {
            return;
        }
        
        // Get the authentification passed in HTTP headers parameters
        String auth = containerRequest.getHeaderString("authorization");
        
        if (auth == null || auth.isEmpty()) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        } else {
            auth = auth.replaceFirst("[B|b]asic ", "");
            
            // Decode the Base64 into byte[]
            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(auth);
            
            // If the decode fails in any case
            if (decodedBytes == null || decodedBytes.length == 0) {
                throw new WebApplicationException(Status.UNAUTHORIZED);
            }
            
            // Now we can convert the byte[] into a splitted array :
            // - the first one is login,
            // - the second one password
            String[] lap = new String(decodedBytes).split(":", 2);
            
            // If login or password fail
            if (lap == null || lap.length != 2) {
                throw new WebApplicationException(Status.UNAUTHORIZED);
            }
            
            MyConnection mc = new MyConnection();
            Connection con = mc.getConnection();
            User actuser = new User(con);
            
            if (!actuser.auth(lap[0], lap[1])) {
                // containerRequest.abortWith(Response
                // .status(Response.Status.UNAUTHORIZED)
                // .entity(actuser.getLastError()).build());
                throw new WebApplicationException(Status.UNAUTHORIZED);
            }
            containerRequest.getHeaders().add(Constants.USERNAME, lap[0]);
        }
        return;
    }
}