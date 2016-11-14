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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.radiohacks.frinmeba.model.jaxb.ICrCh;
import de.radiohacks.frinmeba.model.jaxb.OCrCh;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestCreateChat extends JerseyTest {
    
    /*
     * @POST
     * 
     * @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
     * 
     * @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
     * 
     * @Path("/createchat") public OCrCh createChat(@Context HttpHeaders
     * headers, ICrCh in);
     */
    
    private static final Logger LOGGER = Logger.getLogger(TestCreateChat.class
            .getName());
    
    // Username welche anzulegen ist
    final static String username_org = "Test1";
    final static String username = Base64.encodeBase64String(username_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Passwort zum User
    final static String password_org = "Test1";
    final static String password = Base64.encodeBase64String(password_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    // Email Adresse zum User
    final static String email_org = "Test1@frinme.org";
    final static String email = Base64.encodeBase64String(email_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    
    final static String functionurl = "user/createchat";
    
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
        dropDatabaseTables drop = new dropDatabaseTables();
        drop.dropTable();
        createDatabaseTables create = new createDatabaseTables();
        create.createTable();
        helperDatabase help = new helperDatabase();
        help.CreateActiveUser(username_org, username, password_org, email_org,
                help.InsertFixedImage());
        LOGGER.debug("End prepareDB");
    }
    
    @Test
    public void testCreateChatMissingChatname() {
        ICrCh in = new ICrCh();
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));
        
        target = c.target(TestConfig.URL).path(functionurl);
        LOGGER.debug(target);
        Response resp = target.request(MediaType.APPLICATION_XML)
                .buildPost(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        LOGGER.debug(resp);
        OCrCh out = resp.readEntity(OCrCh.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.MISSING_CHATNAME, out.getET());
    }
    
    @Test
    public void testCreateChatChatnameEncodingError() {
        ICrCh in = new ICrCh();
        in.setCN("1234$%");
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));
        
        target = c.target(TestConfig.URL).path(functionurl);
        LOGGER.debug(target);
        Response resp = target.request(MediaType.APPLICATION_XML)
                .buildPost(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        LOGGER.debug(resp);
        OCrCh out = resp.readEntity(OCrCh.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
    }
    
    @Test
    public void testCreateChatChatnameOK() {
        ICrCh in = new ICrCh();
        in.setCN(Base64.encodeBase64String("Testchat".getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));
        
        target = c.target(TestConfig.URL).path(functionurl);
        LOGGER.debug(target);
        Response resp = target.request(MediaType.APPLICATION_XML)
                .buildPost(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        LOGGER.debug(resp);
        OCrCh out = resp.readEntity(OCrCh.class);
        Assert.assertEquals("Testchat", out.getCN());
    }
}
