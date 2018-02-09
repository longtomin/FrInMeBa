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
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbMessages;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbText;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUserToChats;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUsers;
import de.radiohacks.frinmeba.model.jaxb.OReUC;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestRemoveUserFromChat extends JerseyTest {
    
    /*
     * @DELETE
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Path("/removeuserfromchat") public OReUC removeUserFromChat(@Context
     * HttpHeaders headers,
     * 
     * @QueryParam(Constants.QP_USERID) int userID,
     * 
     * @QueryParam(Constants.QP_CHATID) int chatID);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestRemoveUserFromChat.class.getName());
    
    // Username welche anzulegen ist
    final static String username1_org = "Test1";
    final static String username1 = Base64.encodeBase64String(
            username1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Passwort zum User
    final static String password1_org = "Test1";
    final static String password1 = Base64.encodeBase64String(
            password1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Email Adresse zum User
    final static String email1_org = "Test1@frinme.org";
    final static String email1 = Base64.encodeBase64String(
            email1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Username welche anzulegen ist
    final static String username2_org = "Test2";
    final static String username2 = Base64.encodeBase64String(
            username2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Passwort zum User
    final static String password2_org = "Test2";
    final static String password2 = Base64.encodeBase64String(
            password2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Email Adresse zum User
    final static String email2_org = "Test2@frinme.org";
    final static String email2 = Base64.encodeBase64String(
            email2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // static int chatid;
    // static int userid;
    // static int userid2;
    
    final static String functionurl = "user/removeuserfromchat";
    
    private static FrinmeDbUsers u1 = new FrinmeDbUsers();
    private static FrinmeDbUsers u2 = new FrinmeDbUsers();
    private static FrinmeDbChats c1 = new FrinmeDbChats();
    private static FrinmeDbUserToChats u2c1 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c2 = new FrinmeDbUserToChats();
    private static FrinmeDbText t1 = new FrinmeDbText();
    private static FrinmeDbText t2 = new FrinmeDbText();
    private static FrinmeDbText t3 = new FrinmeDbText();
    private static FrinmeDbMessages m1 = new FrinmeDbMessages();
    private static FrinmeDbMessages m2 = new FrinmeDbMessages();
    
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
        LOGGER.debug("Start BeforeClass");
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
        t1.setText("TEST Text Message von User1");
        t2.setText("TEST Text Message von User2");
        t3.setText("TEST Text Message von User3");
        session.save(t1);
        session.save(t2);
        session.save(t3);
        c1.setChatname("TestChat");
        c1.setFrinmeDbUsers(u1);
        session.save(c1);
        u2c1.setFrinmeDbChats(c1);
        u2c1.setFrinmeDbUsers(u1);
        session.save(u2c1);
        u2c2.setFrinmeDbChats(c1);
        u2c2.setFrinmeDbUsers(u2);
        session.save(u2c2);
        m1.setFrinmeDbUsers(u1);
        m1.setMessageTyp(Constants.TYP_TEXT);
        m1.setFrinmeDbUserToChats(u2c1);
        m1.setFrinmeDbText(t1);
        session.save(m1);
        m1.setOriginMsgId(m1.getId());
        session.saveOrUpdate(m1);
        m2.setFrinmeDbUsers(u1);
        m2.setMessageTyp(Constants.TYP_TEXT);
        m2.setFrinmeDbUserToChats(u2c1);
        m2.setFrinmeDbText(t1);
        m2.setOriginMsgId(m1.getId());
        session.save(m2);
        session.getTransaction().commit();
        session.close();
        LOGGER.debug("End BeforeClass");
    }
    
    @Test
    public void testRemoveUserFromChat() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl);
        
        LOGGER.debug(target);
        OReUC out = target.request().delete(OReUC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
    }
    
    @Test
    public void testRemoveUserFromChatChatID() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_CHATID, c1.getId());
        
        LOGGER.debug(target);
        OReUC out = target.request().delete(OReUC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
    }
    
    @Test
    public void testRemoveUserFromChatUserID() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_USERID, u1.getId());
        LOGGER.debug(target);
        OReUC out = target.request().delete(OReUC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
    }
    
    @Test
    public void testRemoveUserFromChatChatIDUserIDOwnerNotRemoved() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_USERID, u1.getId())
                .queryParam(Constants.QP_CHATID, c1.getId());
        LOGGER.debug(target);
        OReUC out = target.request().delete(OReUC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.CHAT_OWNER_NOT_REMOVED, out.getET());
    }
    
    public void testRemoveUserFromChatChatIDUserIDOK() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_USERID, u2.getId())
                .queryParam(Constants.QP_CHATID, c1.getId());
        LOGGER.debug(target);
        OReUC out = target.request().delete(OReUC.class);
        LOGGER.debug("R=" + out.getR());
        Assert.assertEquals("REMOVED", out.getR());
    }
    
    @Test
    public void testRemoveUserFromChatChatIDUserIDNotOwner() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username2, password2));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_USERID, u1.getId())
                .queryParam(Constants.QP_CHATID, c1.getId());
        LOGGER.debug(target);
        OReUC out = target.request().delete(OReUC.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NOT_CHAT_OWNER, out.getET());
    }
}