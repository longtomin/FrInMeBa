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
import de.radiohacks.frinmeba.model.jaxb.ODMFC;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestDeleteMessageFromChat extends JerseyTest {
    
    /*
     * @DELETE
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Path("/deletemessagefromchat") public ODMFC
     * deleteMessageFromChat(@Context HttpHeaders headers,
     * 
     * @QueryParam(Constants.QP_MESSAGEID) int messageID);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestDeleteMessageFromChat.class.getName());
    
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
    
    static int content_msg1;
    final static String textmnsg1_org = "Test1 Nachricht ;-) 'o)";
    final static String textmnsg1 = Base64.encodeBase64String(
            textmnsg1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    static int content_msg2;
    final static String textmnsg2_org = "Nachricht2 von Test1 ä ö ü Ä Ö Ü";
    final static String textmnsg2 = Base64.encodeBase64String(
            textmnsg2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    
    private static FrinmeDbUsers u1 = new FrinmeDbUsers();
    private static FrinmeDbUsers u2 = new FrinmeDbUsers();
    private static FrinmeDbUsers u3 = new FrinmeDbUsers();
    private static FrinmeDbText t1 = new FrinmeDbText();
    private static FrinmeDbText t2 = new FrinmeDbText();
    private static FrinmeDbImage i = new FrinmeDbImage();
    private static FrinmeDbVideo v = new FrinmeDbVideo();
    private static FrinmeDbChats c1 = new FrinmeDbChats();
    private static FrinmeDbChats c2 = new FrinmeDbChats();
    private static FrinmeDbUserToChats u2c1a = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c1b = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c2 = new FrinmeDbUserToChats();
    private static FrinmeDbMessages mt1 = new FrinmeDbMessages();
    private static FrinmeDbMessages mt2 = new FrinmeDbMessages();
    private static FrinmeDbMessages mt3 = new FrinmeDbMessages();
    private static FrinmeDbMessages mi1 = new FrinmeDbMessages();
    private static FrinmeDbMessages mi2 = new FrinmeDbMessages();
    private static FrinmeDbMessages mi3 = new FrinmeDbMessages();
    private static FrinmeDbMessages mv1 = new FrinmeDbMessages();
    private static FrinmeDbMessages mv2 = new FrinmeDbMessages();
    private static FrinmeDbMessages mv3 = new FrinmeDbMessages();
    
    final static String functionurl = "user/deletemessagefromchat";
    
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
        
        t1.setText(textmnsg1);
        session.save(t1);
        
        t2.setText(textmnsg2);
        session.save(t2);
        
        i.setImage("Testimage.jpg");
        i.setMd5sum("TEST");
        session.save(i);
        
        v.setVideo("Testcideo.mp4");
        v.setMd5sum("TEST");
        session.save(v);
        
        c1.setChatname("Test Chat 1");
        c1.setFrinmeDbUsers(u1);
        session.save(c1);
        
        c2.setChatname("Test Chat 2");
        c2.setFrinmeDbUsers(u1);
        session.save(c2);
        
        u2c1a.setFrinmeDbChats(c1);
        u2c1a.setFrinmeDbUsers(u1);
        session.save(u2c1a);
        u2c1b.setFrinmeDbChats(c1);
        u2c1b.setFrinmeDbUsers(u2);
        session.save(u2c1b);
        u2c2.setFrinmeDbChats(c2);
        u2c2.setFrinmeDbUsers(u1);
        session.save(u2c2);
        
        mt1.setFrinmeDbUsers(u1);
        mt1.setFrinmeDbUserToChats(u2c1a);
        mt1.setMessageTyp(Constants.TYP_TEXT);
        mt1.setFrinmeDbText(t1);
        session.save(mt1);
        mt1.setOriginMsgId(mt1.getId());
        session.saveOrUpdate(mt1);
        
        mt2.setFrinmeDbUsers(u2);
        mt2.setFrinmeDbUserToChats(u2c1b);
        mt2.setMessageTyp(Constants.TYP_TEXT);
        mt2.setFrinmeDbText(t1);
        mt2.setOriginMsgId(mt1.getId());
        session.save(mt2);
        
        mt3.setFrinmeDbUsers(u3);
        mt3.setFrinmeDbUserToChats(u2c2);
        mt3.setMessageTyp(Constants.TYP_TEXT);
        mt3.setFrinmeDbText(t1);
        session.save(mt3);
        mt3.setOriginMsgId(mt3.getId());
        session.saveOrUpdate(mt3);
        
        mi1.setFrinmeDbUsers(u1);
        mi1.setFrinmeDbUserToChats(u2c1a);
        mi1.setMessageTyp(Constants.TYP_IMAGE);
        mi1.setFrinmeDbImage(i);
        
        session.save(mi1);
        mi1.setOriginMsgId(mi1.getId());
        session.saveOrUpdate(mi1);
        
        mi2.setFrinmeDbUsers(u2);
        mi2.setFrinmeDbUserToChats(u2c1b);
        mi2.setMessageTyp(Constants.TYP_IMAGE);
        mi2.setFrinmeDbImage(i);
        mi2.setOriginMsgId(mi1.getId());
        session.save(mi2);
        
        mi3.setFrinmeDbUsers(u3);
        mi3.setFrinmeDbUserToChats(u2c2);
        mi3.setMessageTyp(Constants.TYP_IMAGE);
        mi3.setFrinmeDbImage(i);
        session.save(mi3);
        mi3.setOriginMsgId(mi1.getId());
        session.saveOrUpdate(mi1);
        
        mv1.setFrinmeDbUsers(u1);
        mv1.setFrinmeDbUserToChats(u2c1a);
        mv1.setMessageTyp(Constants.TYP_VIDEO);
        mv1.setFrinmeDbVideo(v);
        session.save(mv1);
        mv1.setOriginMsgId(mv1.getId());
        session.saveOrUpdate(mv1);
        
        mv2.setFrinmeDbUsers(u2);
        mv2.setFrinmeDbUserToChats(u2c1b);
        mv2.setMessageTyp(Constants.TYP_VIDEO);
        mv2.setFrinmeDbVideo(v);
        mv2.setOriginMsgId(mv1.getId());
        session.save(mv2);
        
        mv3.setFrinmeDbUsers(u3);
        mv3.setFrinmeDbUserToChats(u2c2);
        mv3.setMessageTyp(Constants.TYP_VIDEO);
        mv3.setFrinmeDbVideo(v);
        session.save(mv3);
        mv3.setOriginMsgId(mv3.getId());
        session.saveOrUpdate(mv3);
        
        session.getTransaction().commit();
        session.close();
        
        LOGGER.debug("End prepareDB");
    }
    
    @Test
    public void testDeleteMessageFromChatUserPassword() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl);
        LOGGER.debug(target);
        ODMFC out = target.request().delete(ODMFC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_MESSAGE, out.getET());
    }
    
    @Test
    public void testDeleteMessageFromChatUserPasswordMessageIDTextNotOwner() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, mt2.getId());
        LOGGER.debug(target);
        ODMFC out = target.request().delete(ODMFC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NOT_MESSAGE_OWNER, out.getET());
    }
    
    @Test
    public void testDeleteMessageFromChatUserPasswordMessageIDTextOwner() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, mt1.getId());
        LOGGER.debug(target);
        ODMFC out1 = target.request().delete(ODMFC.class);
        LOGGER.debug(out1.getMID());
        Assert.assertNotNull(out1.getMID());
        Assert.assertNull(out1.getET());
        
        WebTarget target2;
        Client c2 = ClientBuilder.newClient();
        c2.register(HttpAuthenticationFeature.basic(username2, password2));
        
        target2 = c2.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, mt2.getId());
        LOGGER.debug(target2);
        
        ODMFC out2 = target2.request().delete(ODMFC.class);
        LOGGER.debug(out2.getMID());
        Assert.assertNotNull(out2.getMID());
        Assert.assertNull(out2.getET());
    }
    
    @Test
    public void testDeleteMessageFromChatUserPasswordMessageIDImageNotOwner() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, mi2.getId());
        LOGGER.debug(target);
        ODMFC out = target.request().delete(ODMFC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NOT_MESSAGE_OWNER, out.getET());
    }
    
    @Test
    public void testDeleteMessageFromChatUserPasswordMessageIDImageOwner() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, mi1.getId());
        LOGGER.debug(target);
        ODMFC out1 = target.request().delete(ODMFC.class);
        LOGGER.debug(out1.getMID());
        Assert.assertNotNull(out1.getMID());
        Assert.assertNull(out1.getET());
        
        WebTarget target2;
        Client c2 = ClientBuilder.newClient();
        c2.register(HttpAuthenticationFeature.basic(username2, password2));
        
        target2 = c2.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, mi2.getId());
        LOGGER.debug(target2);
        ODMFC out2 = target2
                .register(HttpAuthenticationFeature.basicBuilder()
                        .credentials(username2, password2))
                .request().delete(ODMFC.class);
        LOGGER.debug(out2.getMID());
        Assert.assertNotNull(out2.getMID());
        Assert.assertNull(out2.getET());
    }
    
    @Test
    public void testDeleteMessageFromChatUserPasswordMessageIDVideoNotOwner() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username3, password3));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, mv2.getId());
        LOGGER.debug(target);
        
        ODMFC out = target.request().delete(ODMFC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NOT_MESSAGE_OWNER, out.getET());
    }
    
    @Test
    public void testDeleteMessageFromChatUserPasswordMessageIDVideoOwner() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, mv1.getId());
        LOGGER.debug(target);
        
        ODMFC out1 = target.request().delete(ODMFC.class);
        LOGGER.debug(out1.getMID());
        Assert.assertNotNull(out1.getMID());
        Assert.assertNull(out1.getET());
        
        WebTarget target2;
        Client c2 = ClientBuilder.newClient();
        c2.register(HttpAuthenticationFeature.basic(username2, password2));
        
        target2 = c2.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, mv2.getId());
        LOGGER.debug(target2);
        
        ODMFC out2 = target2.request().delete(ODMFC.class);
        LOGGER.debug(out2.getMID());
        Assert.assertNotNull(out2.getMID());
        Assert.assertNull(out2.getET());
    }
}