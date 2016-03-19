package de.radiohacks;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.xml.bind.JAXB;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import de.radiohacks.frinmeba.services.ImageImpl;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.services.VideoImpl;

@ApplicationPath("/rest")
public class MyApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> returnValue = new HashSet<Class<?>>();
        returnValue.add(ServiceImpl.class);
        returnValue.add(ImageImpl.class);
        returnValue.add(VideoImpl.class);
        returnValue.add(MultiPartFeature.class);
        returnValue.add(JAXB.class);
        return returnValue;
    }
}