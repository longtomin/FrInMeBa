/**
 * Copyright @ 2015, Thomas Schreiner, thomas1.schreiner@googlemail.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package de.radiohacks.frinmeba.test.database;

import java.io.InputStream;
import java.nio.charset.Charset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.hibernate.Session;

import de.radiohacks.frinmeba.model.jaxb.OSImM;
import de.radiohacks.frinmeba.model.jaxb.OSViM;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.test.TestConfig;

public class helperDatabase {
    
    final static String videouploadurl = "video/upload";
    final static String imageuploadurl = "image/upload";
    
    public OSViM insertVideoContent(String u, String p) {
        
        final String acknowledge_org = "ba0623b8c7a7520092ee1ff71da0bbea";
        final String acknowledge = Base64.encodeBase64String(acknowledge_org
                .getBytes(Charset.forName(Constants.CHARACTERSET)));
        
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build();
        target = client.target(TestConfig.URL + videouploadurl)
                .queryParam(Constants.QP_ACKNOWLEDGE, acknowledge);
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/test.mp4");
        final FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file").fileName("test.mp4").size(1).build();
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        return target
                .register(HttpAuthenticationFeature.basicBuilder()
                        .credentials(u, p).build())
                .request()
                .post(Entity.entity(mp, mp.getMediaType()), OSViM.class);
    }
    
    public OSImM insertImageContent(String u, String p) {
        final String acknowledge_org = "e36ba04dd1ad642a6e8c74c72a4aab8c";
        final String acknowledge = Base64.encodeBase64String(acknowledge_org
                .getBytes(Charset.forName(Constants.CHARACTERSET)));
        
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build();
        target = client.target(TestConfig.URL + imageuploadurl)
                .queryParam(Constants.QP_ACKNOWLEDGE, acknowledge);
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/test.jpg");
        final FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file").fileName("test.jpg").size(1).build();
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        return target
                .register(HttpAuthenticationFeature.basicBuilder()
                        .credentials(u, p).build())
                .request()
                .post(Entity.entity(mp, mp.getMediaType()), OSImM.class);
    }
    
    public void emptyDatabase() {
        
        String DropMessages = "Delete from FrinmeDbMessages";
        String DropText = "Delete from FrinmeDbText";
        String DropUserToChats = "Delete from FrinmeDbUserToChats";
        String DropChats = "Delete from FrinmeDbChats";
        String DropContact = "Delete from FrinmeDbContact";
        String DropFile = "Delete from FrinmeDbFile";
        String DropImage = "Delete from FrinmeDbImage";
        String DropLocation = "Delete from FrinmeDbLocation";
        String DropUsers = "Delete from FrinmeDbUsers";
        String DropVideo = "Delete from FrinmeDbVideo";
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.createQuery(DropMessages).executeUpdate();
        session.createQuery(DropText).executeUpdate();
        session.createQuery(DropUserToChats).executeUpdate();
        session.createQuery(DropChats).executeUpdate();
        session.createQuery(DropUsers).executeUpdate();
        session.createQuery(DropContact).executeUpdate();
        session.createQuery(DropFile).executeUpdate();
        session.createQuery(DropImage).executeUpdate();
        session.createQuery(DropLocation).executeUpdate();
        session.createQuery(DropVideo).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}