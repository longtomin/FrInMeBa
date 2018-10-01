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

import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUsers;
import de.radiohacks.frinmeba.model.jaxb.OLiUs;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.HibernateUtil;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestListUser extends JerseyTest {
    
    /*
     * @GET
     * 
     * @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
     * 
     * @Path("/listuser") public OLiUs listUsers(@Context HttpHeaders headers,
     * ILiUs in);
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(TestListUser.class.getName());
    
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
    final static String username2_org = "User1 mit Nachnamen";
    final static String username2 = Base64.encodeBase64String(
            username2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Passwort zum User
    final static String password2_org = "Test2";
    final static String password2 = Base64.encodeBase64String(
            password2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Email Adresse zum User
    final static String email2_org = "User2@frinme.org";
    final static String email2 = Base64.encodeBase64String(
            email2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Username welche anzulegen ist
    final static String username3_org = "Andreas will mitspielen";
    final static String username3 = Base64.encodeBase64String(
            username3_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Passwort zum User
    final static String password3_org = "Test3";
    final static String password3 = Base64.encodeBase64String(
            password3_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Email Adresse zum User
    final static String email3_org = "andreas@frinme.org";
    final static String email3 = Base64.encodeBase64String(
            email3_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
    
    final static String functionurl = "user/listuser";
    
    private static FrinmeDbUsers u1 = new FrinmeDbUsers();
    private static FrinmeDbUsers u2 = new FrinmeDbUsers();
    private static FrinmeDbUsers u3 = new FrinmeDbUsers();
    
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
        session.getTransaction().commit();
        session.close();
        LOGGER.debug("End prepareDB");
    }
    
    @Test
    public void testListUserUserPasswordSearchInvalidEmail() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        // Invalid Email Address
        target = c.target(TestConfig.URL).path(functionurl).queryParam(
                Constants.QP_SEARCH, Base64.encodeBase64String("Test"
                        .getBytes(Charset.forName(Constants.CHARACTERSET))));
        LOGGER.debug(target);
        OLiUs out = target.request().get(OLiUs.class);
        LOGGER.debug("U (size) =" + out.getU().size());
        Assert.assertNotNull(out.getU());
    }
    
    @Test
    public void testListUserUserPasswordEmpty() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        // Empty list returned, no search given
        target = c.target(TestConfig.URL + functionurl);
        LOGGER.debug(target);
        OLiUs out = target.request().get(OLiUs.class);
        LOGGER.debug("U (size) =" + out.getU().size());
        Assert.assertNotNull(out.getU());
    }
    
    @Test
    public void testListUserEncodingErrorSearch() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        // Encoding Error not base64 encoded
        target = c.target(TestConfig.URL + functionurl)
                .queryParam(Constants.QP_SEARCH, "$%&1233");
        LOGGER.debug(target);
        OLiUs out = target.request().get(OLiUs.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
    }
    
    @Test
    public void testListUserUserPasswordSearchOK() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        
        // Invalid Email Address
        target = c.target(TestConfig.URL).path(functionurl)
                .queryParam(Constants.QP_SEARCH, email2);
        LOGGER.debug(target);
        OLiUs out = target.request().get(OLiUs.class);
        LOGGER.debug("U (size) =" + out.getU().size());
        Assert.assertNotNull(out.getU());
    }
    
}