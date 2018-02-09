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

import java.nio.charset.Charset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
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

import de.radiohacks.frinmeba.model.hibernate.FrinmeDbChats;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbContact;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbFile;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbImage;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbLocation;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbText;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUserToChats;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUsers;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbVideo;
import de.radiohacks.frinmeba.model.jaxb.IIMIC;
import de.radiohacks.frinmeba.model.jaxb.OIMIC;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestInserMessageIntoChat extends JerseyTest {
    
    /*
     * @POST
     * 
     * @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
     * 
     * @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
     * 
     * @Path("/insertmessageintochat") public OIMIC
     * insertMessageIntoChat(@Context HttpHeaders headers, IIMIC in);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestInserMessageIntoChat.class.getName());
    
    final static String username1_org = "Test1";
    final static String username1 = Base64.encodeBase64String(
            username1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String password1_org = "Test1";
    final static String password1 = Base64.encodeBase64String(
            password1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String email1_org = "Test1@frinme.org";
    final static String email1 = Base64.encodeBase64String(
            email1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String username2_org = "Test2";
    final static String username2 = Base64.encodeBase64String(
            username2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String password2_org = "Test2";
    final static String password2 = Base64.encodeBase64String(
            password2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String email2_org = "Test2@frinme.org";
    final static String email2 = Base64.encodeBase64String(
            email2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String username3_org = "Test3";
    final static String username3 = Base64.encodeBase64String(
            username3_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String password3_org = "Test3";
    final static String password3 = Base64.encodeBase64String(
            password3_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String email3_org = "Test3@frinme.org";
    final static String email3 = Base64.encodeBase64String(
            email3_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String username4_org = "Test4";
    final static String username4 = Base64.encodeBase64String(
            username4_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String password4_org = "Test4";
    final static String password4 = Base64.encodeBase64String(
            password4_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String email4_org = "Test4@frinme.org";
    final static String email4 = Base64.encodeBase64String(
            email4_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String username5_org = "Test5";
    final static String username5 = Base64.encodeBase64String(
            username5_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String password5_org = "Test5";
    final static String password5 = Base64.encodeBase64String(
            password5_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String email5_org = "Test5@frinme.org";
    final static String email5 = Base64.encodeBase64String(
            email5_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    
    final static String functionurl = "user/insertmessageintochat";
    
    private static FrinmeDbUsers u1 = new FrinmeDbUsers();
    private static FrinmeDbUsers u2 = new FrinmeDbUsers();
    private static FrinmeDbUsers u3 = new FrinmeDbUsers();
    private static FrinmeDbUsers u4 = new FrinmeDbUsers();
    private static FrinmeDbUsers u5 = new FrinmeDbUsers();
    private static FrinmeDbChats c1 = new FrinmeDbChats();
    private static FrinmeDbText t1 = new FrinmeDbText();
    private static FrinmeDbText t2 = new FrinmeDbText();
    private static FrinmeDbText t3 = new FrinmeDbText();
    private static FrinmeDbImage i1 = new FrinmeDbImage();
    private static FrinmeDbVideo v1 = new FrinmeDbVideo();
    private static FrinmeDbFile f1 = new FrinmeDbFile();
    private static FrinmeDbContact ct1 = new FrinmeDbContact();
    private static FrinmeDbLocation l1 = new FrinmeDbLocation();
    private static FrinmeDbUserToChats u2c1 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c2 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c3 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c4 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c5 = new FrinmeDbUserToChats();
    
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
    
    @BeforeClass
    public static void prepareDB() {
        LOGGER.debug("Start prepareDB");
        helperDatabase help = new helperDatabase();
        help.emptyDatabase();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        u1.setActive(true);
        u1.setB64username(username1);
        u1.setUsername(username1_org);
        u1.setPassword(password1_org);
        u1.setEmail(email1_org);
        session.save(u1);
        u2.setActive(true);
        u2.setB64username(username2);
        u2.setUsername(username2_org);
        u2.setPassword(password2_org);
        u2.setEmail(email2_org);
        session.save(u2);
        u3.setActive(true);
        u3.setB64username(username3);
        u3.setUsername(username3_org);
        u3.setPassword(password3_org);
        u3.setEmail(email3_org);
        session.save(u3);
        u4.setActive(true);
        u4.setB64username(username4);
        u4.setUsername(username4_org);
        u4.setPassword(password4_org);
        u4.setEmail(email4_org);
        session.save(u4);
        u5.setActive(true);
        u5.setB64username(username5);
        u5.setUsername(username5_org);
        u5.setPassword(password5_org);
        u5.setEmail(email5_org);
        session.save(u5);
        
        i1.setImage("Testimage.jpg");
        i1.setMd5sum("Test");
        session.save(i1);
        
        c1.setChatname("Chat-Test1");
        c1.setFrinmeDbUsers(u1);
        session.save(c1);
        
        t1.setText("TEST Text Message von User1");
        session.save(t1);
        t2.setText("TEST Text Message von User2");
        session.save(t2);
        t3.setText("TEST Text Message von User3");
        session.save(t3);
        
        v1.setVideo("TestVideo.mp4");
        v1.setMd5sum("Test");
        session.save(v1);
        
        ct1.setContact("TEST_Contact");
        session.save(ct1);
        
        l1.setLocation("TEST_Location");
        session.save(l1);
        
        f1.setFile("Testfile.pdf");
        f1.setMd5sum("Test");
        session.save(f1);
        
        u2c1.setFrinmeDbChats(c1);
        u2c1.setFrinmeDbUsers(u1);
        session.save(u2c1);
        u2c2.setFrinmeDbChats(c1);
        u2c2.setFrinmeDbUsers(u2);
        session.save(u2c2);
        u2c3.setFrinmeDbChats(c1);
        u2c3.setFrinmeDbUsers(u3);
        session.save(u2c3);
        u2c4.setFrinmeDbChats(c1);
        u2c4.setFrinmeDbUsers(u4);
        session.save(u2c4);
        u2c5.setFrinmeDbChats(c1);
        u2c5.setFrinmeDbUsers(u5);
        session.save(u2c5);
        
        session.getTransaction().commit();
        session.close();
        LOGGER.debug("End prepareDB");
    }
    
    private OIMIC callTarget(IIMIC in, String u, String p) {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(u, p));
        target = c.target(TestConfig.URL + functionurl);
        LOGGER.debug(target);
        Response response = target.request()
                .buildPost(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        LOGGER.debug(response);
        return response.readEntity(OIMIC.class);
    }
    
    @Test
    public void testInserMessageIntoChat() {
        IIMIC in = new IIMIC();
        OIMIC out = callTarget(in, username1, password1);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.INVALID_MESSAGE_TYPE, out.getET());
    }
    
    @Test
    public void testInserMessageIntoChatMessageType() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        OIMIC out = callTarget(in, username1, password1);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeEncodingError() {
        IIMIC in = new IIMIC();
        in.setMT("XXX");
        OIMIC out = callTarget(in, username1, password1);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
    }
    
    @Test
    public void testInserMessageIntoChatChatID() {
        IIMIC in = new IIMIC();
        in.setCID(c1.getId());
        OIMIC out = callTarget(in, username1, password1);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.INVALID_MESSAGE_TYPE, out.getET());
    }
    
    @Test
    public void testInserMessageIntoChatMessageID() {
        IIMIC in = new IIMIC();
        in.setMID(t1.getId());
        OIMIC out = callTarget(in, username1, password1);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.INVALID_MESSAGE_TYPE, out.getET());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeChatID() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(c1.getId());
        OIMIC out = callTarget(in, username1, password1);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_MESSAGE, out.getET());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeMsgID() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setMID(ct1.getId());
        OIMIC out = callTarget(in, username1, password1);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
    }
    
    @Test
    public void testInserMessageIntoChatMsgIDChatID() {
        IIMIC in = new IIMIC();
        in.setMID(ct1.getId());
        in.setCID(c1.getId());
        OIMIC out = callTarget(in, username1, password1);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.INVALID_MESSAGE_TYPE, out.getET());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeMsgIDChatIDUUser1() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(c1.getId());
        in.setMID(t1.getId());
        OIMIC out = callTarget(in, username1, password1);
        LOGGER.debug("MID=" + out.getMID());
        LOGGER.debug("SdT=" + out.getSdT());
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeMsgIDChatIDUUser3() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(c1.getId());
        in.setMID(t3.getId());
        OIMIC out = callTarget(in, username3, password3);
        LOGGER.debug("MID=" + out.getMID());
        LOGGER.debug("SdT=" + out.getSdT());
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeMsgIDChatIDUUser2() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(c1.getId());
        in.setMID(t2.getId());
        OIMIC out = callTarget(in, username2, password2);
        LOGGER.debug("MID=" + out.getMID());
        LOGGER.debug("SdT=" + out.getSdT());
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeMsgIDChatIDImage() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_IMAGE
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(c1.getId());
        in.setMID(i1.getId());
        OIMIC out = callTarget(in, username2, password2);
        LOGGER.debug("MID=" + out.getMID());
        LOGGER.debug("SdT=" + out.getSdT());
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeMsgIDChatIDVideo() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_VIDEO
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(c1.getId());
        in.setMID(v1.getId());
        OIMIC out = callTarget(in, username2, password2);
        LOGGER.debug("MID=" + out.getMID());
        LOGGER.debug("SdT=" + out.getSdT());
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeMsgIDChatIDLocation() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_LOCATION
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(c1.getId());
        in.setMID(l1.getId());
        OIMIC out = callTarget(in, username2, password2);
        LOGGER.debug("MID=" + out.getMID());
        LOGGER.debug("SdT=" + out.getSdT());
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeMsgIDChatIDFile() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_FILE
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(c1.getId());
        in.setMID(f1.getId());
        OIMIC out = callTarget(in, username2, password2);
        LOGGER.debug("MID=" + out.getMID());
        LOGGER.debug("SdT=" + out.getSdT());
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }
    
    @Test
    public void testInserMessageIntoChatMessageTypeMsgIDChatIDContact() {
        IIMIC in = new IIMIC();
        in.setMT(Base64.encodeBase64String(Constants.TYP_CONTACT
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(c1.getId());
        in.setMID(ct1.getId());
        OIMIC out = callTarget(in, username1, password1);
        LOGGER.debug("MID=" + out.getMID());
        LOGGER.debug("SdT=" + out.getSdT());
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }
}
