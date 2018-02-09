/**
 * Copyright � 2015, Thomas Schreiner, thomas1.schreiner@googlemail.com
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
package de.radiohacks.frinmeba.test.functions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.nio.charset.Charset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUsers;
import de.radiohacks.frinmeba.model.jaxb.OSIcM;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestUploadIcon extends JerseyTest {
    
    /*
     * @POST
     * 
     * @Path("/uploadicon")
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Consumes(MediaType.MULTIPART_FORM_DATA) public OSIcM uploadIcon(
     * 
     * @QueryParam(Constants.QPusername) String User,
     * 
     * @QueryParam(Constants.QPpassword) String Password,
     * 
     * @QueryParam(Constants.QPacknowledge) String Acknowledge,
     * 
     * @FormDataParam("file") InputStream fileInputStream,
     * 
     * @FormDataParam("file") FormDataContentDisposition
     * contentDispositionHeader);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestUploadIcon.class.getName());
    
    // Username welche anzulegen ist
    final static String username_org = "Test1";
    final static String username = Base64.encodeBase64String(
            username_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Passwort zum User
    final static String password_org = "Test1";
    final static String password = Base64.encodeBase64String(
            password_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Email Adresse zum User
    final static String email_org = "Test1@frinme.org";
    final static String email = Base64.encodeBase64String(
            email_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Acknowledge
    final static String acknowledge_org = "e36ba04dd1ad642a6e8c74c72a4aab8c";
    final static String acknowledge = Base64.encodeBase64String(
            acknowledge_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    
    final static String acknowledge_quadrat_org = "35d8b6fcdef12c442d1a591f0842cccd";
    final static String acknowledge_quadrat = Base64
            .encodeBase64String(acknowledge_quadrat_org
                    .getBytes(Charset.forName(Constants.CHARACTERSET)));
    
    final static String functionurl = "image/uploadicon";
    
    private static FrinmeDbUsers u = new FrinmeDbUsers();
    
    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    @Override
    protected DeploymentContext configureDeployment() {
        return ServletDeploymentContext.forServlet(
                new ServletContainer(new ResourceConfig(ServiceImpl.class)))
                .build();
    }
    
    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }
    
    @BeforeClass
    public static void prepareDB() {
        LOGGER.debug("Start BeforeClass");
        helperDatabase help = new helperDatabase();
        help.emptyDatabase();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        u.setActive(true);
        u.setB64username(username);
        u.setUsername(username_org);
        u.setPassword(password_org);
        u.setEmail(email_org);
        session.save(u);
        session.getTransaction().commit();
        session.close();
        LOGGER.debug("End BeforeClass");
    }
    
    @Test
    public void testUploadIconUpNoValues() {
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build();
        
        target = client.target(TestConfig.URL).path(functionurl);
        LOGGER.debug(target);
        
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/test.jpg");
        final FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file").fileName("test.jpg").size(1).build();
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        Response resp = target.request()
                .post(Entity.entity(mp, mp.getMediaType()));
        LOGGER.debug("Response = " + resp);
        Assert.assertEquals(resp.getStatus(), 401);
    }
    
    @Test
    public void testUploadIconUserPassword() {
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build()
                .register(HttpAuthenticationFeature.basic(username, password));
        
        target = client.target(TestConfig.URL).path(functionurl);
        LOGGER.debug(target);
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/test.jpg");
        final FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file").fileName("test.jpg").size(1).build();
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        OSIcM out = target.request().post(Entity.entity(mp, mp.getMediaType()),
                OSIcM.class);
        LOGGER.debug("ET=" + out.getET());
        assertThat(out.getET(), is(Constants.UPLOAD_FAILED));
    }
    
    @Test
    public void testUploadIconUserPasswordNoDisposition() {
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build()
                .register(HttpAuthenticationFeature.basic(username, password));
        
        target = client.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_ACKNOWLEDGE, acknowledge);
        LOGGER.debug(target);
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/test.jpg");
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart("File", data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        OSIcM out = target.request().post(Entity.entity(mp, mp.getMediaType()),
                OSIcM.class);
        LOGGER.debug("ET=" + out.getET());
        assertThat(out.getET(), is(Constants.NO_IMAGEMESSAGE_GIVEN));
    }
    
    @Test
    public void testUploadIconUserPasswordNoAcknowledge() {
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build()
                .register(HttpAuthenticationFeature.basic(username, password));
        
        target = client.target(TestConfig.URL).path(functionurl);
        LOGGER.debug(target);
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/test.jpg");
        final FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file").fileName("test.jpg").size(1).build();
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        OSIcM out = target.request().post(Entity.entity(mp, mp.getMediaType()),
                OSIcM.class);
        LOGGER.debug("ET=" + out.getET());
        assertThat(out.getET(), is(Constants.UPLOAD_FAILED));
    }
    
    @Test
    public void testUploadIconUserEncodeFailureAcknowledge() {
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build()
                .register(HttpAuthenticationFeature.basic(username, password));
        
        target = client.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_ACKNOWLEDGE, acknowledge_org);
        LOGGER.debug(target);
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/test.jpg");
        final FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file").fileName("test.jpg").size(1).build();
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        OSIcM out = target.request().post(Entity.entity(mp, mp.getMediaType()),
                OSIcM.class);
        LOGGER.debug("ET=" + out.getET());
        assertThat(out.getET(), is(Constants.ENCODING_ERROR));
    }
    
    @Test
    public void testUploadIconUserPasswordAcknowledgeNoQuadrat() {
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build()
                .register(HttpAuthenticationFeature.basic(username, password));
        
        target = client.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_ACKNOWLEDGE, acknowledge);
        LOGGER.debug(target);
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/test.jpg");
        final FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file").fileName("test.jpg").size(1).build();
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        OSIcM out = target.request().post(Entity.entity(mp, mp.getMediaType()),
                OSIcM.class);
        LOGGER.debug("ET=" + out.getET());
        assertThat(out.getET(), is(Constants.NO_QUADRAT_IMAGE));
    }
    
    @Test
    public void testUploadIconUserPasswordAcknowledge() {
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build()
                .register(HttpAuthenticationFeature.basic(username, password));
        
        target = client.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_ACKNOWLEDGE, acknowledge_quadrat);
        LOGGER.debug(target);
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/quadrat.jpg");
        final FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file").fileName("quadrat.jpg").size(1).build();
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        OSIcM out = target.request().post(Entity.entity(mp, mp.getMediaType()),
                OSIcM.class);
        LOGGER.debug(out.toString());
        LOGGER.debug("IcID=" + out.getIcID());
        LOGGER.debug("IcF=" + out.getIcF());
        assertThat(out.getIcID(), is(not(nullValue())));
        assertThat(out.getIcF(), is(not(nullValue())));
    }
}