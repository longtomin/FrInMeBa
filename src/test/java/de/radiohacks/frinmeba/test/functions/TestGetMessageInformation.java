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
import de.radiohacks.frinmeba.model.jaxb.OGMI;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestGetMessageInformation extends JerseyTest {
    
    /*
     * @GET
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Path("/getmessageinformation") public OGMI
     * getMessageInformation(@Context HttpHeaders headers,
     * 
     * @QueryParam(Constants.QP_MESSAGEID) List<Integer> messageID);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestGetMessageInformation.class.getName());
    
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
    
    final static String functionurl = "user/getmessageinformation";
    
    static int msg1;
    final static String textmnsg1_org = "Test1 Nachricht ;-) 'o)";
    final static String textmnsg1 = Base64.encodeBase64String(
            textmnsg1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    static int msg2;
    final static String textmnsg2_org = "Nachricht2 von Test1 ä ö ü Ä Ö Ü";
    final static String textmnsg2 = Base64.encodeBase64String(
            textmnsg2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    
    private static FrinmeDbUsers u1 = new FrinmeDbUsers();
    private static FrinmeDbUsers u2 = new FrinmeDbUsers();
    private static FrinmeDbUsers u3 = new FrinmeDbUsers();
    private static FrinmeDbUsers u4 = new FrinmeDbUsers();
    private static FrinmeDbUsers u5 = new FrinmeDbUsers();
    private static FrinmeDbChats c1 = new FrinmeDbChats();
    private static FrinmeDbChats c2 = new FrinmeDbChats();
    private static FrinmeDbText t1 = new FrinmeDbText();
    private static FrinmeDbText t2 = new FrinmeDbText();
    private static FrinmeDbUserToChats u2c1 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c2 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c3 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c4 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c5 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c12 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c22 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c32 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c42 = new FrinmeDbUserToChats();
    private static FrinmeDbMessages m11 = new FrinmeDbMessages();
    private static FrinmeDbMessages m12 = new FrinmeDbMessages();
    private static FrinmeDbMessages m13 = new FrinmeDbMessages();
    private static FrinmeDbMessages m14 = new FrinmeDbMessages();
    private static FrinmeDbMessages m15 = new FrinmeDbMessages();
    private static FrinmeDbMessages m22 = new FrinmeDbMessages();
    private static FrinmeDbMessages m21 = new FrinmeDbMessages();
    private static FrinmeDbMessages m23 = new FrinmeDbMessages();
    private static FrinmeDbMessages m24 = new FrinmeDbMessages();
    
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
        
        c1.setChatname("Chat-Test1");
        c1.setFrinmeDbUsers(u1);
        session.save(c1);
        c2.setChatname("Chat-Test2");
        c2.setFrinmeDbUsers(u2);
        session.save(c2);
        
        t1.setText("TEST Text Message von User1");
        session.save(t1);
        t2.setText("TEST Text Message von User2");
        session.save(t2);
        
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
        
        u2c12.setFrinmeDbChats(c2);
        u2c12.setFrinmeDbUsers(u1);
        session.save(u2c12);
        u2c22.setFrinmeDbChats(c2);
        u2c22.setFrinmeDbUsers(u2);
        session.save(u2c22);
        u2c32.setFrinmeDbChats(c2);
        u2c32.setFrinmeDbUsers(u3);
        session.save(u2c32);
        u2c42.setFrinmeDbChats(c2);
        u2c42.setFrinmeDbUsers(u4);
        session.save(u2c42);
        
        m11.setFrinmeDbUsers(u1);
        m11.setFrinmeDbUserToChats(u2c1);
        m11.setMessageTyp(Constants.TYP_TEXT);
        m11.setFrinmeDbText(t1);
        session.save(m11);
        m11.setOriginMsgId(m11.getId());
        session.saveOrUpdate(m11);
        
        m12.setFrinmeDbUsers(u1);
        m12.setFrinmeDbUserToChats(u2c2);
        m12.setMessageTyp(Constants.TYP_TEXT);
        m12.setFrinmeDbText(t1);
        m12.setOriginMsgId(m11.getId());
        session.save(m12);
        
        m13.setFrinmeDbUsers(u1);
        m13.setFrinmeDbUserToChats(u2c3);
        m13.setMessageTyp(Constants.TYP_TEXT);
        m13.setFrinmeDbText(t1);
        m13.setOriginMsgId(m11.getId());
        session.save(m13);
        
        m14.setFrinmeDbUsers(u1);
        m14.setFrinmeDbUserToChats(u2c4);
        m14.setMessageTyp(Constants.TYP_TEXT);
        m14.setFrinmeDbText(t1);
        m14.setOriginMsgId(m11.getId());
        session.save(m14);
        
        m15.setFrinmeDbUsers(u1);
        m15.setFrinmeDbUserToChats(u2c5);
        m15.setMessageTyp(Constants.TYP_TEXT);
        m15.setFrinmeDbText(t1);
        m15.setOriginMsgId(m11.getId());
        session.save(m15);
        
        m22.setFrinmeDbUsers(u2);
        m22.setFrinmeDbUserToChats(u2c22);
        m22.setMessageTyp(Constants.TYP_TEXT);
        m22.setFrinmeDbText(t2);
        session.save(m22);
        m22.setOriginMsgId(m22.getId());
        session.saveOrUpdate(m22);
        
        m21.setFrinmeDbUsers(u2);
        m21.setFrinmeDbUserToChats(u2c12);
        m21.setMessageTyp(Constants.TYP_TEXT);
        m21.setFrinmeDbText(t2);
        m21.setOriginMsgId(m22.getId());
        session.save(m21);
        
        m23.setFrinmeDbUsers(u2);
        m23.setFrinmeDbUserToChats(u2c32);
        m23.setMessageTyp(Constants.TYP_TEXT);
        m23.setFrinmeDbText(t2);
        m23.setOriginMsgId(m22.getId());
        session.save(m23);
        
        m24.setFrinmeDbUsers(u2);
        m24.setFrinmeDbUserToChats(u2c42);
        m24.setMessageTyp(Constants.TYP_TEXT);
        m24.setFrinmeDbText(t2);
        m24.setOriginMsgId(m22.getId());
        session.save(m24);
        
        session.getTransaction().commit();
        session.close();
        LOGGER.debug("End BeforeClass");
    }
    
    @Test
    public void testGetMessageInformation() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl);
        LOGGER.debug(target);
        OGMI out = target.request().get(OGMI.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_MESSAGE, out.getET());
    }
    
    @Test
    public void testGetMessageInformationUserPasswordTextmessage1() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, m11.getId());
        LOGGER.debug(target);
        OGMI out = target.request().get(OGMI.class);
        LOGGER.debug("MIB (size) =" + out.getMIB().size());
        Assert.assertNotNull(out.getMIB().size());
        Assert.assertNotNull(out.getMIB().get(0).getMI().size());
    }
    
    @Test
    public void testGetMessageInformationUserPasswordTextmessage2() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username3, password3));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, m13.getId()); // m21.getId()
        LOGGER.debug(target);
        OGMI out = target.request().get(OGMI.class);
        LOGGER.debug("MIB (size) =" + out.getMIB().size());
        Assert.assertNotNull(out.getMIB().size());
        Assert.assertNotNull(out.getMIB().get(0).getMI().size());
    }
    
    @Test
    public void testGetMessageInformationUserPasswordMultipleMessages() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, m11.getId())
                .queryParam(Constants.QP_MESSAGEID, m21.getId());
        LOGGER.debug(target);
        OGMI out = target.request().get(OGMI.class);
        LOGGER.debug("MIB (size) =" + out.getMIB().size());
        Assert.assertNotNull(out.getMIB().size());
        Assert.assertNotNull(out.getMIB().get(1).getMI().size());
    }
    
    @Test
    public void testGetMessageInformationUserPasswordMultipleMessagesNotOwner() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username5, password5));
        
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_MESSAGEID, m15.getId())
                .queryParam(Constants.QP_MESSAGEID, m22.getId());
        LOGGER.debug(target);
        OGMI out = target.request().get(OGMI.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NOT_MESSAGE_OWNER, out.getET());
    }
}
