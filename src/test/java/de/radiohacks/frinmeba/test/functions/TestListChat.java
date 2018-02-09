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
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUserToChats;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUsers;
import de.radiohacks.frinmeba.model.jaxb.OLiCh;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestListChat extends JerseyTest {
    
    /*
     * @GET
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Path("/listchat") public OLiCh listChats(@Context HttpHeaders headers);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestListChat.class.getName());
    
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
    
    final static String functionurl = "user/listchat";
    
    private static FrinmeDbUsers u1 = new FrinmeDbUsers();
    private static FrinmeDbChats c1 = new FrinmeDbChats();
    private static FrinmeDbChats c2 = new FrinmeDbChats();
    private static FrinmeDbChats c3 = new FrinmeDbChats();
    private static FrinmeDbImage i1 = new FrinmeDbImage();
    private static FrinmeDbUserToChats u2c1 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c2 = new FrinmeDbUserToChats();
    private static FrinmeDbUserToChats u2c3 = new FrinmeDbUserToChats();
    
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
        session.save(u1);
        i1.setImage("Testimge");
        i1.setMd5sum("TestMD5");
        session.save(i1);
        c1.setChatname("TESTCHAT1");
        c1.setFrinmeDbUsers(u1);
        c1.setFrinmeDbImage(i1);
        session.save(c1);
        c2.setChatname("TESTCHAT2");
        c2.setFrinmeDbUsers(u1);
        session.save(c2);
        c3.setChatname("TESTCHAT3");
        c3.setFrinmeDbUsers(u1);
        session.save(c3);
        u2c1.setFrinmeDbChats(c1);
        u2c1.setFrinmeDbUsers(u1);
        session.save(u2c1);
        u2c2.setFrinmeDbChats(c2);
        u2c2.setFrinmeDbUsers(u1);
        session.save(u2c2);
        u2c3.setFrinmeDbChats(c3);
        u2c3.setFrinmeDbUsers(u1);
        session.save(u2c3);
        session.getTransaction().commit();
        session.close();
        LOGGER.debug("End prepareDB");
    }
    
    @Test
    public void testListChatNoSearch() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));
        target = c.target(TestConfig.URL + functionurl);
        LOGGER.debug(target);
        OLiCh out = target.request().get(OLiCh.class);
        LOGGER.debug("C (size)=" + out.getC().size());
        Assert.assertNotNull(out.getC());
        Assert.assertNotNull(out.getC().get(0).getCN());
        Assert.assertNotNull(out.getC().get(0).getCID());
        Assert.assertNotNull(out.getC().get(0).getICID());
        Assert.assertNotNull(out.getC().get(0).getOU());
    }
}