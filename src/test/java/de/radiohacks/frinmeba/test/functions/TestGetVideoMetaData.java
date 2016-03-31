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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

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
import org.junit.BeforeClass;
import org.junit.Test;

import de.radiohacks.frinmeba.model.jaxb.OGViMMD;
import de.radiohacks.frinmeba.model.jaxb.OSViM;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestGetVideoMetaData extends JerseyTest {

    /*
     * @GET
     * 
     * @Path("/getvideometadata")
     * 
     * @Produces(MediaType.APPLICATION_XML) public OGViMMD getvideometadata(
     * 
     * @QueryParam(Constants.QPusername) String User,
     * 
     * @QueryParam(Constants.QPpassword) String Password,
     * 
     * @QueryParam(Constants.QPvideoid) int videoid);
     */

    private static final Logger LOGGER = Logger
            .getLogger(TestGetVideoMetaData.class.getName());

    // Username welche anzulegen ist
    private final static String username_org = "Test1";
    private final static String username = Base64
            .encodeBase64String(username_org.getBytes(Charset
                    .forName(Constants.CHARACTERSET)));
    // Passwort zum User
    private final static String password_org = "Test1";
    private final static String password = Base64
            .encodeBase64String(password_org.getBytes(Charset
                    .forName(Constants.CHARACTERSET)));
    // Email Adresse zum User
    private final static String email_org = "Test1@frinme.org";

    private final static String functionurl = "video/getvideometadata";

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
        dropDatabaseTables drop = new dropDatabaseTables();
        drop.dropTable();
        createDatabaseTables create = new createDatabaseTables();
        create.createTable();
        helperDatabase help = new helperDatabase();
        help.CreateActiveUser(username_org, username, password_org, email_org,
                help.InsertFixedImage());
        LOGGER.debug("End BeforeClass");
    }

    private void deleteVideo(int in) {
        helperDatabase helper = new helperDatabase();
        /*
         * If the File was inserted and created by the the then use this
         */
        // helper.deleteAndDropImage(in);
        /*
         * Otherwise just delete the DB Entry and let the file delete be done
         * outside.
         */
        helper.deleteFixedVideo(in);
    }

    @Test
    public void testGetVideoMetaDataUserPassword() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));

        target = c.target(TestConfig.URL + functionurl);
        // .queryParam(Constants.QP_PASSWORD, password)
        // .queryParam(Constants.QP_USERNAME, username);
        LOGGER.debug(target);
        OGViMMD out = target.request().get(OGViMMD.class);
        LOGGER.debug("ET=" + out.getET());
        assertThat(out.getET(), is(Constants.NONE_EXISTING_CONTENT_MESSAGE));
    }

    @Test
    public void testGetVideoMetaDataUserPasswordVideoID() {

        helperDatabase help = new helperDatabase();
        OSViM O1 = help.insertVideoContent(username, password);
        int videoid = O1.getVID();
        // int videoid = insertVideo();
        System.out.println("videoid ==> " + videoid);
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));

        target = c.target(TestConfig.URL + functionurl)
        // .queryParam(Constants.QP_PASSWORD, password)
        // .queryParam(Constants.QP_USERNAME, username)
                .queryParam(Constants.QP_VIDEOID, videoid);
        LOGGER.debug(target);
        System.out.println("target ==> " + target);
        System.out.println("request ==> " + target.request());
        System.out.println("==> " + target.request().get());
        System.out.println("==> " + target.request().get(OGViMMD.class));
        OGViMMD out = target.request().get(OGViMMD.class);
        System.out.println("OUT ==> " + out);
        System.out.println("getET == > " + out.getET());
        System.out.println("getVM == > " + out.getVM());
        System.out.println("getVMD5 == > " + out.getVMD5());
        System.out.println("getVS == > " + out.getVS());
        LOGGER.debug("VS=" + out.getVS());
        assertThat(out.getVS(), is(not(nullValue())));
        deleteVideo(videoid);
    }

    @Test
    public void testGetVideoMetaDataUserPasswordWrongVideoID() {
        WebTarget target;
        Client c = ClientBuilder.newClient();
        c.register(HttpAuthenticationFeature.basic(username, password));

        target = c.target(TestConfig.URL + functionurl)
        // .queryParam(Constants.QP_PASSWORD, password)
        // .queryParam(Constants.QP_USERNAME, username)
                .queryParam(Constants.QP_VIDEOID, 107365);
        LOGGER.debug(target);
        OGViMMD out = target.request().get(OGViMMD.class);
        LOGGER.debug("ET=" + out.getET());
        assertThat(out.getET(), is(Constants.NONE_EXISTING_CONTENT_MESSAGE));
    }

}