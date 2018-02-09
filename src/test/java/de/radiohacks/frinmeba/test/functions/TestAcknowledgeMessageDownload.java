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
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
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
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import de.radiohacks.frinmeba.model.hibernate.FrinmeDbChats;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbImage;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbMessages;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbText;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUserToChats;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUsers;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbVideo;
import de.radiohacks.frinmeba.model.jaxb.IAckMD;
import de.radiohacks.frinmeba.model.jaxb.ISTeM;
import de.radiohacks.frinmeba.model.jaxb.OAckMD;
import de.radiohacks.frinmeba.model.jaxb.OSImM;
import de.radiohacks.frinmeba.model.jaxb.OSTeM;
import de.radiohacks.frinmeba.model.jaxb.OSViM;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestAcknowledgeMessageDownload extends JerseyTest {
    
    /*
     * @POST
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Consumes(MediaType.APPLICATION_XML)
     * 
     * @Path("/acknowledgemessagedownload") public OAckMD
     * acknowledgeMessageDownload(@Context HttpHeaders headers, IAckMD in);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestAcknowledgeMessageDownload.class.getName());
    
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
    
    final static String functionurl = "user/acknowledgemessagedownload";
    
    final static String textmsg_org = "Test Nachnricht fuer Acknowledge";
    final static String textmsg = Base64.encodeBase64String(
            textmsg_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    
    final static String md5sumimg_org = "e36ba04dd1ad642a6e8c74c72a4aab8c";
    final static String md5sumimg = Base64.encodeBase64String(
            md5sumimg_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String md5sumvid_org = "ba0623b8c7a7520092ee1ff71da0bbea";
    final static String md5sumvid = Base64.encodeBase64String(
            md5sumvid_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String md5sumtxt_org = "[B@2e41b2e9";
    final static String md5sumtxt = Base64.encodeBase64String(
            md5sumtxt_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    
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
    }
    
    private OAckMD callTarget(IAckMD in) {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));
        
        target = c.target(TestConfig.URL).path(functionurl);
        LOGGER.debug(target);
        Response response = target.request()
                .buildPost(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        LOGGER.debug(response);
        return response.readEntity(OAckMD.class);
    }
    
    private FrinmeDbImage uploadImageContent(String url) {
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build();
        
        target = client.target(TestConfig.URL + url);
        LOGGER.debug(target);
        
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/test.jpg");
        final FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file").fileName("test.jpg").size(1).build();
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        OSImM x = target
                .register(HttpAuthenticationFeature.basicBuilder()
                        .credentials(username, password).build())
                .request()
                .post(Entity.entity(mp, mp.getMediaType()), OSImM.class);
        FrinmeDbImage i = new FrinmeDbImage();
        i.setId(x.getImID());
        i.setImage(x.getImF());
        return i;
    }
    
    private FrinmeDbVideo uploadVideoContent(String url) {
        WebTarget target;
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class).build();
        
        target = client.target(TestConfig.URL + url);
        LOGGER.debug(target);
        final FormDataMultiPart mp = new FormDataMultiPart();
        
        InputStream data = this.getClass().getResourceAsStream("/test.mp4");
        final FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file").fileName("test.mp4").size(1).build();
        
        final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        mp.bodyPart(fdp2);
        
        OSViM x = target
                .register(HttpAuthenticationFeature.basicBuilder()
                        .credentials(username, password).build())
                .request()
                .post(Entity.entity(mp, mp.getMediaType()), OSViM.class);
        FrinmeDbVideo v = new FrinmeDbVideo();
        v.setId(x.getVID());
        v.setVideo(x.getVF());
        return v;
    }
    
    private FrinmeDbText uploadTextContent() {
        ISTeM in = new ISTeM();
        in.setTM(textmsg);
        WebTarget target;
        target = ClientBuilder.newClient()
                .target(TestConfig.URL + "user/sendtextmessage");
        LOGGER.debug(target);
        Response response = target
                .register(HttpAuthenticationFeature.basicBuilder()
                        .credentials(username, password).build())
                .request()
                .buildPost(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        OSTeM x = response.readEntity(OSTeM.class);
        FrinmeDbText t = new FrinmeDbText();
        t.setId(x.getTID());
        return t;
    }
    
    @Test
    public void testAcknowledgeMessageDownloadUserPasswordNoAcknowledge() {
        IAckMD in = new IAckMD();
        OAckMD out = callTarget(in);
        LOGGER.debug("ET=" + out.getET());
        assertThat(out.getET(), is(Constants.NO_CONTENT_GIVEN));
    }
    
    @Test
    public void testAcknowledgeMessageDownloadAcknowledgeImage() {
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        FrinmeDbImage msgimg = uploadImageContent(
                "image/upload?" + Constants.QP_ACKNOWLEDGE + "=" + md5sumimg);
        msgimg.setMd5sum(md5sumimg_org);
        session.save(msgimg);
        FrinmeDbChats c = new FrinmeDbChats();
        c.setChatname("Test Chat Image");
        c.setFrinmeDbUsers(u);
        session.save(c);
        FrinmeDbUserToChats u2c = new FrinmeDbUserToChats();
        u2c.setFrinmeDbChats(c);
        u2c.setFrinmeDbUsers(u);
        session.save(u2c);
        FrinmeDbMessages m = new FrinmeDbMessages();
        m.setFrinmeDbUsers(u);
        m.setMessageTyp(Constants.TYP_IMAGE);
        m.setSendTimestamp(10);
        m.setFrinmeDbUserToChats(u2c);
        m.setFrinmeDbImage(msgimg);
        session.save(m);
        
        session.getTransaction().commit();
        session.close();
        
        HashCode md5 = null;
        try {
            md5 = Files.hash(
                    new File(
                            this.getClass().getResource("/test.jpg").getFile()),
                    Hashing.md5());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String md5sumimg = new String(
                Base64.encodeBase64(md5.toString().getBytes()),
                Charset.forName(Constants.CHARACTERSET));
        
        IAckMD in = new IAckMD();
        in.setMID(m.getId());
        in.setACK(md5sumimg);
        OAckMD out = callTarget(in);
        LOGGER.debug(out.getACK());
        assertThat(out.getACK(), is(Constants.ACKNOWLEDGE_TRUE));
    }
    
    @Test
    public void testAcknowledgeMessageDownloadAcknowledgeVideo() {
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        FrinmeDbVideo msgvid = uploadVideoContent(
                "video/upload?" + Constants.QP_ACKNOWLEDGE + "=" + md5sumvid);
        msgvid.setMd5sum(md5sumvid_org);
        session.save(msgvid);
        FrinmeDbChats c = new FrinmeDbChats();
        c.setChatname("Test Chat Video");
        c.setFrinmeDbUsers(u);
        session.save(c);
        FrinmeDbUserToChats u2c = new FrinmeDbUserToChats();
        u2c.setFrinmeDbChats(c);
        u2c.setFrinmeDbUsers(u);
        session.save(u2c);
        FrinmeDbMessages m = new FrinmeDbMessages();
        m.setFrinmeDbUsers(u);
        m.setMessageTyp(Constants.TYP_VIDEO);
        m.setSendTimestamp(10);
        m.setFrinmeDbUserToChats(u2c);
        m.setFrinmeDbVideo(msgvid);
        session.save(m);
        session.getTransaction().commit();
        session.close();
        
        HashCode md5 = null;
        try {
            md5 = Files.hash(
                    new File(
                            this.getClass().getResource("/test.mp4").getFile()),
                    Hashing.md5());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String md5sumimg = new String(
                Base64.encodeBase64(md5.toString().getBytes()),
                Charset.forName(Constants.CHARACTERSET));
        
        IAckMD in = new IAckMD();
        in.setMID(m.getId());
        in.setACK(md5sumimg);
        OAckMD out = callTarget(in);
        LOGGER.debug(out.getACK());
        assertThat(out.getACK(), is(Constants.ACKNOWLEDGE_TRUE));
    }
    
    @Test
    public void testAcknowledgeMessageDownloadAcknowledgeText() {
        
        // FrinmeDbText msgtext = uploadTextContent();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        FrinmeDbText msgtext = new FrinmeDbText();
        msgtext.setText(textmsg_org);
        session.save(msgtext);
        FrinmeDbChats c = new FrinmeDbChats();
        c.setChatname("Test Chat Text");
        c.setFrinmeDbUsers(u);
        session.save(c);
        FrinmeDbUserToChats u2c = new FrinmeDbUserToChats();
        u2c.setFrinmeDbChats(c);
        u2c.setFrinmeDbUsers(u);
        session.save(u2c);
        FrinmeDbMessages m = new FrinmeDbMessages();
        m.setFrinmeDbUsers(u);
        m.setMessageTyp(Constants.TYP_TEXT);
        m.setSendTimestamp(10);
        m.setFrinmeDbUserToChats(u2c);
        m.setFrinmeDbText(msgtext);
        session.save(m);
        session.getTransaction().commit();
        session.close();
        
        int hashCode = textmsg_org.hashCode();
        String sha1b64 = new String(
                Base64.encodeBase64(String.valueOf(hashCode).getBytes()),
                Charset.forName(Constants.CHARACTERSET));
        
        IAckMD in = new IAckMD();
        in.setMID(m.getId());
        in.setACK(sha1b64);
        OAckMD out = callTarget(in);
        LOGGER.debug(out.getACK());
        assertThat(out.getACK(), is(Constants.ACKNOWLEDGE_TRUE));
    }
    
    @Test
    public void testAcknowledgeMessageDownloadAcknowledgeEncodingError() {
        
        FrinmeDbText msgtext = uploadTextContent();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        FrinmeDbChats c = new FrinmeDbChats();
        c.setChatname("Test Chat Text2");
        c.setFrinmeDbUsers(u);
        session.save(c);
        FrinmeDbUserToChats u2c = new FrinmeDbUserToChats();
        u2c.setFrinmeDbChats(c);
        u2c.setFrinmeDbUsers(u);
        session.save(u2c);
        FrinmeDbMessages m = new FrinmeDbMessages();
        m.setFrinmeDbUsers(u);
        m.setMessageTyp(Constants.TYP_TEXT);
        m.setSendTimestamp(10);
        m.setFrinmeDbUserToChats(u2c);
        m.setFrinmeDbText(msgtext);
        session.save(m);
        
        session.getTransaction().commit();
        session.close();
        
        IAckMD in = new IAckMD();
        in.setMID(m.getId());
        in.setACK("XXX");
        OAckMD out = callTarget(in);
        LOGGER.debug(out.getET());
        assertThat(out.getET(), is(Constants.ENCODING_ERROR));
    }
    
    @Test
    public void testAcknowledgeMessageDownloadMessageID() {
        
        FrinmeDbText msgtext = uploadTextContent();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        FrinmeDbChats c = new FrinmeDbChats();
        c.setChatname("Test Chat Text 3");
        c.setFrinmeDbUsers(u);
        session.save(c);
        FrinmeDbUserToChats u2c = new FrinmeDbUserToChats();
        u2c.setFrinmeDbChats(c);
        u2c.setFrinmeDbUsers(u);
        session.save(u2c);
        FrinmeDbMessages m = new FrinmeDbMessages();
        m.setFrinmeDbUsers(u);
        m.setMessageTyp(Constants.TYP_TEXT);
        m.setSendTimestamp(10);
        m.setFrinmeDbUserToChats(u2c);
        m.setFrinmeDbText(msgtext);
        session.save(m);
        
        session.getTransaction().commit();
        session.close();
        
        IAckMD in = new IAckMD();
        in.setMID(m.getId());
        OAckMD out = callTarget(in);
        LOGGER.debug(out.getACK());
        assertThat(out.getET(), is(Constants.NO_CONTENT_GIVEN));
    }
    
    @Test
    public void testAcknowledgeMessageDownloadAcknowledge() {
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        FrinmeDbChats c = new FrinmeDbChats();
        c.setChatname("Test Chat");
        c.setFrinmeDbUsers(u);
        session.save(c);
        session.getTransaction().commit();
        session.close();
        
        int hashCode = textmsg_org.hashCode();
        String sha1b64 = new String(
                Base64.encodeBase64(String.valueOf(hashCode).getBytes()),
                Charset.forName(Constants.CHARACTERSET));
        
        IAckMD in = new IAckMD();
        in.setACK(sha1b64);
        OAckMD out = callTarget(in);
        LOGGER.debug(out.getACK());
        assertThat(out.getET(), is(Constants.NONE_EXISTING_MESSAGE));
    }
}