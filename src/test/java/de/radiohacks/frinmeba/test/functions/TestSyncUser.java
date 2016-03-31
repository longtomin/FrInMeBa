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

import de.radiohacks.frinmeba.model.jaxb.OSU;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestSyncUser extends JerseyTest {

    /*
     * @GET
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Path("/syncuser") public OSU syncuser(@QueryParam(Constants.QPusername)
     * String User,
     * 
     * @QueryParam(Constants.QPpassword) String Password,
     * 
     * @QueryParam(Constants.QPuserid) List<Integer> UserID);
     */

    private static final Logger LOGGER = Logger.getLogger(TestSyncUser.class
            .getName());

    final static String username1_org = "Test1";
    final static String username1 = Base64.encodeBase64String(username1_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String password1_org = "Test1";
    final static String password1 = Base64.encodeBase64String(password1_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String email1_org = "Test1@frinme.org";
    final static String email1 = Base64.encodeBase64String(email1_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String username2_org = "Test2";
    final static String username2 = Base64.encodeBase64String(username2_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String password2_org = "Test2";
    final static String password2 = Base64.encodeBase64String(password2_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String email2_org = "Test2@frinme.org";
    final static String email2 = Base64.encodeBase64String(email2_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String username3_org = "Test3";
    final static String username3 = Base64.encodeBase64String(username3_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String password3_org = "Test3";
    final static String password3 = Base64.encodeBase64String(password3_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String email3_org = "Test3@frinme.org";
    final static String email3 = Base64.encodeBase64String(email3_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String username4_org = "Test4";
    final static String username4 = Base64.encodeBase64String(username4_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String password4_org = "Test4";
    final static String password4 = Base64.encodeBase64String(password4_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String email4_org = "Test4@frinme.org";
    final static String email4 = Base64.encodeBase64String(email4_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String username5_org = "Test5";
    final static String username5 = Base64.encodeBase64String(username5_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String password5_org = "Test5";
    final static String password5 = Base64.encodeBase64String(password5_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));
    final static String email5_org = "Test5@frinme.org";
    final static String email5 = Base64.encodeBase64String(email5_org
            .getBytes(Charset.forName(Constants.CHARACTERSET)));

    final static String functionurl = "user/syncuser";

    static int uid1 = 0;
    static int uid2 = 0;
    static int uid3 = 0;
    static int uid4 = 0;
    static int uid5 = 0;

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
        help.CreateActiveUser(username1_org, username1, password1_org,
                email1_org, help.InsertFixedImage());
        uid1 = help.getUserID(username1);
        help.CreateActiveUser(username2_org, username2, password2_org,
                email2_org, help.InsertFixedImage());
        uid2 = help.getUserID(username2);
        help.CreateActiveUser(username3_org, username3, password3_org,
                email3_org, help.InsertFixedImage());
        uid3 = help.getUserID(username3);
        help.CreateActiveUser(username4_org, username4, password4_org,
                email4_org, help.InsertFixedImage());
        uid4 = help.getUserID(username4);
        help.CreateActiveUser(username5_org, username5, password5_org,
                email5_org, help.InsertFixedImage());
        uid5 = help.getUserID(username5);
        help.InsertFixedImage();
        LOGGER.debug("End prepareDB");
    }

    @Test
    public void testSyncUserUpNoValues() {
        WebTarget target;
        target = ClientBuilder.newClient().target(TestConfig.URL + functionurl);
        LOGGER.debug(target);
        Response resp = target.request().get();
        LOGGER.debug("Response = " + resp);
        Assert.assertEquals(resp.getStatus(), 401);
    }

    @Test
    public void testSyncUserUserPassword() {
        WebTarget target;

        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));

        target = c.target(TestConfig.URL).path(functionurl);
        // .queryParam(Constants.QP_PASSWORD, password1)
        // .queryParam(Constants.QP_USERNAME, username1);

        LOGGER.debug(target);
        OSU out = target.request().get(OSU.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
    }

    @Test
    public void testSyncUserUserWrongPassword() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password5));
        target = c.target(TestConfig.URL).path(functionurl)
        // .queryParam(
        // Constants.QP_PASSWORD,
        // Base64.encodeBase64String("XXX".getBytes(Charset
        // .forName(Constants.CHARACTERSET))))
        // .queryParam(Constants.QP_USERNAME, username1)
                .queryParam(Constants.QP_USERID, uid2);
        LOGGER.debug(target);

        // HttpAuthenticationFeature feature = (HttpAuthenticationFeature)
        // HttpAuthenticationFeature.basic(username1, password1);

        // Response resp = target
        // .register(
        // HttpAuthenticationFeature.basicBuilder().credentials(
        // username1, "XXX")).request().get();

        Response resp = target.request().get();

        LOGGER.debug("Response = " + resp);
        Assert.assertEquals(resp.getStatus(), 401);
    }

    @Test
    public void testSyncUserUserEncodeFailureUser() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic("$%&123", password1));
        target = c.target(TestConfig.URL).path(functionurl)
        // .queryParam(Constants.QP_PASSWORD, password1)
        // .queryParam(Constants.QP_USERNAME, "�$%1234")
                .queryParam(Constants.QP_USERID, uid2);
        LOGGER.debug(target);
        Response resp = target
                .register(
                        HttpAuthenticationFeature.basicBuilder().credentials(
                                "§$%123", password1)).request().get();
        LOGGER.debug("Response = " + resp);
        Assert.assertEquals(resp.getStatus(), 401);
    }

    @Test
    public void testSyncUserUserPasswordTextmessage1() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        target = c.target(TestConfig.URL).path(functionurl)
        // .queryParam(Constants.QP_PASSWORD, password1)
        // .queryParam(Constants.QP_USERNAME, username1)
                .queryParam(Constants.QP_USERID, uid2);

        LOGGER.debug(target);
        OSU out = target.request().get(OSU.class);
        LOGGER.debug("ET=" + out.getET());
        Assert.assertNotNull(out.getU().size());
    }

    @Test
    public void testSyncUserUserPasswordTextmessage2() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        target = c.target(TestConfig.URL).path(functionurl)
        // .queryParam(Constants.QP_PASSWORD, password1)
        // .queryParam(Constants.QP_USERNAME, username1)
                .queryParam(Constants.QP_USERID, uid2);

        LOGGER.debug(target);
        OSU out = target.request().get(OSU.class);
        LOGGER.debug("U (size) =" + out.getU().size());
        Assert.assertNotNull(out.getU().size());
    }

    @Test
    public void testSyncUserUserPasswordMultipleUsers() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username1, password1));
        target = c.target(TestConfig.URL)
                .path(functionurl)
                // .queryParam(Constants.QP_PASSWORD, password1)
                // .queryParam(Constants.QP_USERNAME, username1)
                .queryParam(Constants.QP_USERID, uid2)
                .queryParam(Constants.QP_USERID, uid3);

        LOGGER.debug(target);
        OSU out = target.request().get(OSU.class);
        Assert.assertNotNull(out.getU().size());
    }

    @Test
    public void testSyncUserUserPasswordMultipleUsersWrongUserID() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username5, password5));
        target = c.target(TestConfig.URL)
                .path(functionurl)
                // .queryParam(Constants.QP_PASSWORD, password5)
                // .queryParam(Constants.QP_USERNAME, username5)
                .queryParam(Constants.QP_USERID, uid3)
                .queryParam(Constants.QP_USERID, 77);

        LOGGER.debug(target);
        OSU out = target.request().get(OSU.class);
        Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
    }
}