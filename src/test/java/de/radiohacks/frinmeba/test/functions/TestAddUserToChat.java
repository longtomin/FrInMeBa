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
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUserToChats;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUsers;
import de.radiohacks.frinmeba.model.jaxb.IAdUC;
import de.radiohacks.frinmeba.model.jaxb.OAdUC;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestAddUserToChat extends JerseyTest {
    
    /*
     * @POST
     * 
     * @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
     * 
     * @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
     * 
     * @Path("/addusertochat") public OAdUC addUserToChat(@Context HttpHeaders
     * headers, IAdUC in);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestAddUserToChat.class.getName());
    
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
    
    final static String functionurl = "user/addusertochat";
    
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
    
    // Chatname
    final static String chatname_org = "Chat1";
    final static String chatname = Base64.encodeBase64String(
            chatname_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    
    // static int chatid;
    // private int userid;
    private static FrinmeDbUsers u1 = new FrinmeDbUsers();
    private static FrinmeDbUsers u2 = new FrinmeDbUsers();
    private static FrinmeDbChats c = new FrinmeDbChats();
    
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
        u1.setB64username(username);
        u1.setUsername(username_org);
        u1.setPassword(password_org);
        u1.setEmail(email_org);
        u2.setActive(true);
        u2.setB64username(username2);
        u2.setUsername(username2_org);
        u2.setPassword(password2_org);
        u2.setEmail(email2_org);
        session.save(u1);
        session.save(u2);
        c.setChatname(chatname_org);
        c.setFrinmeDbUsers(u1);
        session.save(c);
        session.getTransaction().commit();
        session.close();
        LOGGER.debug("End prepareDB");
    }
    
    private OAdUC callTarget(IAdUC in) {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));
        
        target = c.target(TestConfig.URL).path(functionurl);
        LOGGER.debug(target);
        Response response = target.request()
                .buildPost(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        LOGGER.debug(response);
        return response.readEntity(OAdUC.class);
    }
    
    @Test
    public void testAddUserToChatNoValues() {
        IAdUC in = new IAdUC();
        OAdUC out = callTarget(in);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
    }
    
    @Test
    public void testAddUserToChatUserID() {
        IAdUC in = new IAdUC();
        in.setUID(u2.getId());
        OAdUC out = callTarget(in);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
    }
    
    @Test
    public void testAddUserToChatChatID() {
        IAdUC in = new IAdUC();
        in.setCID(c.getId());
        OAdUC out = callTarget(in);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
    }
    
    @Test
    public void testAddUserToChatUserIDChatID() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.createQuery("delete from FrinmeDbUserToChats").executeUpdate();
        session.getTransaction().commit();
        session.close();
        IAdUC in = new IAdUC();
        in.setCID(c.getId());
        in.setUID(u2.getId());
        OAdUC out = callTarget(in);
        LOGGER.debug(out.getR());
        Assert.assertEquals(Constants.USER_ADDED, out.getR());
    }
    
    @Test
    public void testAddUserToChatWrongUserIDChatID() {
        IAdUC in = new IAdUC();
        in.setCID(c.getId());
        in.setUID(-1);
        OAdUC out = callTarget(in);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
    }
    
    @Test
    public void testAddUserToChatUserIDWrongChatID() {
        IAdUC in = new IAdUC();
        in.setCID(-1);
        in.setUID(u2.getId());
        OAdUC out = callTarget(in);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
    }
    
    @Test
    public void testAddUserToChatUserIDChatID_Again() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        FrinmeDbUserToChats u2c1 = new FrinmeDbUserToChats();
        u2c1.setFrinmeDbUsers(u1);
        u2c1.setFrinmeDbChats(c);
        FrinmeDbUserToChats u2c2 = new FrinmeDbUserToChats();
        u2c2.setFrinmeDbChats(c);
        u2c2.setFrinmeDbUsers(u2);
        session.save(u2c1);
        session.save(u2c2);
        session.getTransaction().commit();
        session.close();
        IAdUC in = new IAdUC();
        in.setCID(c.getId());
        in.setUID(u2.getId());
        OAdUC out = callTarget(in);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.USER_ALREADY_IN_CHAT, out.getET());
    }
    
    @Test
    public void testAddUserToChatUserIDChatID_foreignChat() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        FrinmeDbChats c2 = new FrinmeDbChats();
        c2.setChatname("Chat2");
        c2.setFrinmeDbUsers(u2);
        session.save(c2);
        session.getTransaction().commit();
        session.close();
        IAdUC in = new IAdUC();
        in.setCID(c2.getId());
        in.setUID(u2.getId());
        OAdUC out = callTarget(in);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NOT_CHAT_OWNER, out.getET());
    }
    
    @Test
    public void testAddUserToChatUserIDChatID_selfAdd() {
        IAdUC in = new IAdUC();
        in.setCID(c.getId());
        in.setUID(u1.getId());
        OAdUC out = callTarget(in);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.CHAT_OWNER_NOT_ADDED, out.getET());
    }
}