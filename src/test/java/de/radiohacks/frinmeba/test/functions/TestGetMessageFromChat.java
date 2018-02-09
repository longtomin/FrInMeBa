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
import javax.ws.rs.client.WebTarget;

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
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbImage;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbMessages;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbText;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUserToChats;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUsers;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbVideo;
import de.radiohacks.frinmeba.model.jaxb.OFMFC;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestGetMessageFromChat extends JerseyTest {
    
    /*
     * @GET
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Path("/getmessagefromchat") public OFMFC
     * getMessageFromChat(@QueryParam(Constants.QPusername) String User,
     * 
     * @QueryParam(Constants.QPpassword) String Password,
     * 
     * @QueryParam(Constants.QPchatid) int ChatID,
     * 
     * @QueryParam(Constants.QPtimestamp) int Timestamp);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestGetMessageFromChat.class.getName());
    
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
    
    final static String functionurl = "user/getmessagefromchat";
    
    private static FrinmeDbUsers u1 = new FrinmeDbUsers();
    private static FrinmeDbUsers u2 = new FrinmeDbUsers();
    private static FrinmeDbUsers u3 = new FrinmeDbUsers();
    private static FrinmeDbChats cid1to2 = new FrinmeDbChats();
    private static FrinmeDbChats cid3to12 = new FrinmeDbChats();
    private static FrinmeDbText t1 = new FrinmeDbText();
    private static FrinmeDbText t2 = new FrinmeDbText();
    private static FrinmeDbImage i1 = new FrinmeDbImage();
    private static FrinmeDbVideo v1 = new FrinmeDbVideo();
    private static FrinmeDbUserToChats u2c1to2_1 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c1to2_2 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c3to12_1 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c3to12_2 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c3to12_3 = new FrinmeDbUserToChats();
    private static FrinmeDbMessages m12a_1 = new FrinmeDbMessages();
    private static FrinmeDbMessages m12a_2 = new FrinmeDbMessages();
    private static FrinmeDbMessages m312a_1 = new FrinmeDbMessages();
    private static FrinmeDbMessages m312a_2 = new FrinmeDbMessages();
    private static FrinmeDbMessages m312a_3 = new FrinmeDbMessages();
    private static FrinmeDbMessages m12b_1 = new FrinmeDbMessages();
    private static FrinmeDbMessages m12b_2 = new FrinmeDbMessages();
    private static FrinmeDbMessages m312b_1 = new FrinmeDbMessages();
    private static FrinmeDbMessages m312b_2 = new FrinmeDbMessages();
    private static FrinmeDbMessages m312b_3 = new FrinmeDbMessages();
    private static FrinmeDbMessages m12c_1 = new FrinmeDbMessages();
    private static FrinmeDbMessages m12c_2 = new FrinmeDbMessages();
    private static FrinmeDbMessages m312c_1 = new FrinmeDbMessages();
    private static FrinmeDbMessages m312c_2 = new FrinmeDbMessages();
    private static FrinmeDbMessages m312c_3 = new FrinmeDbMessages();
    
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
        
        cid1to2.setChatname("Chat-Test1");
        cid1to2.setFrinmeDbUsers(u1);
        session.save(cid1to2);
        cid3to12.setChatname("Chat-Test2");
        cid3to12.setFrinmeDbUsers(u3);
        session.save(cid3to12);
        
        t1.setText("TEST Text Message von User1");
        session.save(t1);
        t2.setText("TEST Text Message von User2");
        session.save(t2);
        
        i1.setImage("Testimage.jpg");
        i1.setMd5sum("Test");
        session.save(i1);
        
        v1.setVideo("Testvideo.mp4");
        v1.setMd5sum("Test");
        session.save(v1);
        
        u2c1to2_1.setFrinmeDbChats(cid1to2);
        u2c1to2_1.setFrinmeDbUsers(u1);
        session.save(u2c1to2_1);
        u2c1to2_2.setFrinmeDbChats(cid1to2);
        u2c1to2_2.setFrinmeDbUsers(u2);
        session.save(u2c1to2_2);
        
        u2c3to12_1.setFrinmeDbChats(cid3to12);
        u2c3to12_1.setFrinmeDbUsers(u1);
        session.save(u2c3to12_1);
        u2c3to12_2.setFrinmeDbChats(cid3to12);
        u2c3to12_2.setFrinmeDbUsers(u2);
        session.save(u2c3to12_2);
        u2c3to12_3.setFrinmeDbChats(cid1to2);
        u2c3to12_3.setFrinmeDbUsers(u3);
        session.save(u2c3to12_3);
        
        m12a_1.setFrinmeDbUsers(u1);
        m12a_1.setFrinmeDbUserToChats(u2c1to2_1);
        m12a_1.setMessageTyp(Constants.TYP_TEXT);
        m12a_1.setFrinmeDbText(t1);
        session.save(m12a_1);
        m12a_1.setOriginMsgId(m12a_1.getId());
        session.saveOrUpdate(m12a_1);
        
        m12a_2.setFrinmeDbUsers(u1);
        m12a_2.setFrinmeDbUserToChats(u2c1to2_2);
        m12a_2.setMessageTyp(Constants.TYP_TEXT);
        m12a_2.setFrinmeDbText(t1);
        m12a_2.setOriginMsgId(m12a_1.getId());
        session.save(m12a_2);
        
        m312a_1.setFrinmeDbUsers(u3);
        m312a_1.setFrinmeDbUserToChats(u2c3to12_3);
        m312a_1.setMessageTyp(Constants.TYP_TEXT);
        m312a_1.setFrinmeDbText(t1);
        session.save(m312a_1);
        m312a_1.setOriginMsgId(m312a_1.getId());
        session.saveOrUpdate(m312a_1);
        
        m312a_2.setFrinmeDbUsers(u3);
        m312a_2.setFrinmeDbUserToChats(u2c3to12_1);
        m312a_2.setMessageTyp(Constants.TYP_TEXT);
        m312a_2.setFrinmeDbText(t1);
        m312a_2.setOriginMsgId(m312a_1.getId());
        session.save(m312a_2);
        
        m312a_3.setFrinmeDbUsers(u3);
        m312a_3.setFrinmeDbUserToChats(u2c3to12_2);
        m312a_3.setMessageTyp(Constants.TYP_TEXT);
        m312a_3.setFrinmeDbText(t1);
        m312a_3.setOriginMsgId(m312a_1.getId());
        session.save(m312a_3);
        
        m12b_1.setFrinmeDbUsers(u2);
        m12b_1.setFrinmeDbUserToChats(u2c1to2_1);
        m12b_1.setMessageTyp(Constants.TYP_TEXT);
        m12b_1.setFrinmeDbText(t1);
        session.save(m12b_1);
        m12b_1.setOriginMsgId(m12b_1.getId());
        session.saveOrUpdate(m12b_1);
        
        m12b_2.setFrinmeDbUsers(u2);
        m12b_2.setFrinmeDbUserToChats(u2c1to2_2);
        m12b_2.setMessageTyp(Constants.TYP_TEXT);
        m12b_2.setFrinmeDbText(t1);
        m12b_2.setOriginMsgId(m12b_1.getId());
        session.save(m12b_2);
        
        m312b_1.setFrinmeDbUsers(u2);
        m312b_1.setFrinmeDbUserToChats(u2c3to12_2);
        m312b_1.setMessageTyp(Constants.TYP_TEXT);
        m312b_1.setFrinmeDbText(t1);
        session.save(m312b_1);
        m312b_1.setOriginMsgId(m312b_1.getId());
        session.saveOrUpdate(m312b_1);
        
        m312b_2.setFrinmeDbUsers(u2);
        m312b_2.setFrinmeDbUserToChats(u2c3to12_1);
        m312b_2.setMessageTyp(Constants.TYP_TEXT);
        m312b_2.setFrinmeDbText(t1);
        m312b_2.setOriginMsgId(m312b_1.getId());
        session.save(m312b_2);
        
        m312b_3.setFrinmeDbUsers(u2);
        m312b_3.setFrinmeDbUserToChats(u2c3to12_3);
        m312b_3.setMessageTyp(Constants.TYP_TEXT);
        m312b_3.setFrinmeDbText(t1);
        m312b_3.setOriginMsgId(m312b_1.getId());
        session.save(m312b_3);
        
        m12c_1.setFrinmeDbUsers(u1);
        m12c_1.setFrinmeDbUserToChats(u2c1to2_1);
        m12c_1.setMessageTyp(Constants.TYP_IMAGE);
        m12c_1.setFrinmeDbImage(i1);
        session.save(m12c_1);
        m12c_1.setOriginMsgId(m12c_1.getId());
        session.saveOrUpdate(m12c_1);
        
        m12c_2.setFrinmeDbUsers(u1);
        m12c_2.setFrinmeDbUserToChats(u2c1to2_2);
        m12c_2.setMessageTyp(Constants.TYP_IMAGE);
        m12c_2.setFrinmeDbImage(i1);
        m12c_2.setOriginMsgId(m12c_1.getId());
        session.save(m12c_2);
        
        m312c_1.setFrinmeDbUsers(u1);
        m312c_1.setFrinmeDbUserToChats(u2c3to12_2);
        m312c_1.setMessageTyp(Constants.TYP_VIDEO);
        m312c_1.setFrinmeDbVideo(v1);
        session.save(m312c_1);
        m312c_1.setOriginMsgId(m312c_1.getId());
        session.saveOrUpdate(m312c_1);
        
        m312c_2.setFrinmeDbUsers(u1);
        m312c_2.setFrinmeDbUserToChats(u2c3to12_1);
        m312c_2.setMessageTyp(Constants.TYP_VIDEO);
        m312c_2.setFrinmeDbVideo(v1);
        m312c_2.setOriginMsgId(m312c_1.getId());
        session.save(m312c_2);
        
        m312c_3.setFrinmeDbUsers(u1);
        m312c_3.setFrinmeDbUserToChats(u2c3to12_3);
        m312c_3.setMessageTyp(Constants.TYP_VIDEO);
        m312c_3.setFrinmeDbVideo(v1);
        m312c_3.setOriginMsgId(m312c_1.getId());
        session.save(m312c_3);
        
        session.getTransaction().commit();
        session.close();
        LOGGER.debug("End prepareDB");
    }
    
    @Test
    public void testGetMessageFromChatUserPassword() {
        WebTarget target;
        
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL + functionurl);
        LOGGER.debug(target);
        OFMFC out = target.request().get(OFMFC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
    }
    
    @Test
    public void testGetMessageFromChatUserPasswordChatID() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL + functionurl)
                .queryParam(Constants.QP_CHATID, cid1to2.getId());
        LOGGER.debug(target);
        OFMFC out = target.request().get(OFMFC.class);
        LOGGER.debug("M (size) =" + out.getM().size());
        Assert.assertNotNull(out.getM());
    }
    
    @Test
    public void testGetMessageFromChatUserPasswordTimestamp() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL + functionurl)
                .queryParam(Constants.QP_TIMESTAMP, 0);
        LOGGER.debug(target);
        OFMFC out = target.request().get(OFMFC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
    }
    
    @Test
    public void testGetMessageFromChatUserPasswordChatIDTimestampToBig() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL + functionurl)
                .queryParam(Constants.QP_TIMESTAMP, 200)
                .queryParam(Constants.QP_CHATID, cid1to2.getId());
        LOGGER.debug(target);
        OFMFC out = target.request().get(OFMFC.class);
        LOGGER.debug("M (size) =" + out.getM().size());
        Assert.assertEquals(3, out.getM().size());
    }
    
    @Test
    public void testGetMessageFromChatUserPasswordChatIDTimestampNewMessages() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL + functionurl)
                .queryParam(Constants.QP_TIMESTAMP, 0)
                .queryParam(Constants.QP_CHATID, cid3to12.getId());
        LOGGER.debug(target);
        OFMFC out = target.request().get(OFMFC.class);
        LOGGER.debug("M (size) =" + out.getM().size());
        Assert.assertNotNull(out.getM().size());
        Assert.assertEquals(3, out.getM().size());
    }
}
