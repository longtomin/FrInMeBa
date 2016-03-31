package de.radiohacks.frinmeba.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

@ApplicationPath("/rest")
public class FrinmebaApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> returnValue = new HashSet<Class<?>>();
        returnValue.add(ServiceImpl.class);
        returnValue.add(ImageImpl.class);
        returnValue.add(VideoImpl.class);
        returnValue.add(MultiPartFeature.class);
        returnValue.add(FrinmebaAuthFilter.class);
        return returnValue;
    }
}