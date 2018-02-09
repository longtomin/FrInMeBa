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
import de.radiohacks.frinmeba.model.jaxb.OCN;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestCheckNew extends JerseyTest {
    
    /*
     * @GET
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Path("/checknewmessages") public OCN
     * checkNewMessages(@QueryParam(Constants.QPusername) String User,
     * 
     * @QueryParam(Constants.QPpassword) String Password);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestCheckNew.class.getName());
    
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
    
    final static String functionurl = "user/checknew";
    
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
    public void testCheckNewMessagesUserPassword() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        FrinmeDbText t = new FrinmeDbText();
        t.setText("Test Nachricht");
        session.save(t);
        FrinmeDbChats c = new FrinmeDbChats();
        c.setChatname("TestChat");
        c.setFrinmeDbUsers(u);
        session.save(c);
        FrinmeDbUserToChats u2c = new FrinmeDbUserToChats();
        u2c.setFrinmeDbChats(c);
        u2c.setFrinmeDbUsers(u);
        session.save(u2c);
        FrinmeDbMessages m = new FrinmeDbMessages();
        m.setFrinmeDbUsers(u);
        m.setFrinmeDbUserToChats(u2c);
        m.setMessageTyp(de.radiohacks.frinmeba.services.Constants.TYP_TEXT);
        m.setFrinmeDbText(t);
        m.setReadTimestamp(0);
        m.setTempReadTimestamp(0);
        session.save(m);
        m.setOriginMsgId(m.getId());
        session.saveOrUpdate(m);
        
        session.getTransaction().commit();
        session.close();
        
        WebTarget target;
        
        target = ClientBuilder.newClient().target(TestConfig.URL + functionurl);
        LOGGER.debug(target);
        OCN out = target
                .register(HttpAuthenticationFeature.basicBuilder()
                        .credentials(username, password).build())
                .request().get(OCN.class);
        LOGGER.debug(out.getC());
        Assert.assertNotNull(out.getC());
    }
}