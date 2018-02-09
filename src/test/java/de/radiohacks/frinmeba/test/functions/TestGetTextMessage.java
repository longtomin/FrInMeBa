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

import de.radiohacks.frinmeba.model.hibernate.FrinmeDbText;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUsers;
import de.radiohacks.frinmeba.model.jaxb.OGTeM;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestGetTextMessage extends JerseyTest {
    
    /*
     * @GET
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Path("/gettextmessage") public OGTeM
     * getTextMessage(@QueryParam(Constants.QPusername) String User,
     * 
     * @QueryParam(Constants.QPpassword) String Password,
     * 
     * @QueryParam(Constants.QPtextmessageid) int TextMessageID);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestGetTextMessage.class.getName());
    
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
    
    final static String functionurl = "user/gettextmessage";
    
    // static int msg1;
    final static String textmnsg1_org = "Test1 Nachricht ;-) 'o)";
    final static String textmnsg1 = Base64.encodeBase64String(
            textmnsg1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // static int msg2;
    final static String textmnsg2_org = "Nachricht2 von Test1 ä ö ü Ä Ö Ü";
    final static String textmnsg2 = Base64.encodeBase64String(
            textmnsg2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    
    private static FrinmeDbUsers u1 = new FrinmeDbUsers();
    private static FrinmeDbText t1 = new FrinmeDbText();
    private static FrinmeDbText t2 = new FrinmeDbText();
    
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
        u1.setB64username(username);
        u1.setUsername(username_org);
        u1.setPassword(password_org);
        u1.setEmail(email_org);
        session.save(u1);
        t1.setText(textmnsg1);
        t2.setText(textmnsg2);
        session.save(t1);
        session.save(t2);
        session.getTransaction().commit();
        session.close();
        LOGGER.debug("End BeforeClass");
    }
    
    @Test
    public void testGetTextMessageUserPassword() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));
        
        target = c.target(TestConfig.URL + functionurl);
        
        LOGGER.debug(target);
        OGTeM out = target.request().get(OGTeM.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_CONTENT_MESSAGE,
                out.getET());
    }
    
    @Test
    public void testGetTextMessageUserPasswordTextmessage1() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));
        
        target = c.target(TestConfig.URL + functionurl)
                .queryParam(Constants.QP_TEXTMESSAGEID, t1.getId());
        
        LOGGER.debug(target);
        OGTeM out = target.request().get(OGTeM.class);
        LOGGER.debug("TM=" + out.getTM());
        Assert.assertEquals(textmnsg1_org, out.getTM());
    }
    
    @Test
    public void testGetTextMessageUserPasswordTextmessage2() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));
        
        target = c.target(TestConfig.URL + functionurl)
                .queryParam(Constants.QP_TEXTMESSAGEID, t2.getId());
        LOGGER.debug(target);
        OGTeM out = target.request().get(OGTeM.class);
        LOGGER.debug("TM=" + out.getTM());
        Assert.assertEquals(textmnsg2_org, out.getTM());
    }
}