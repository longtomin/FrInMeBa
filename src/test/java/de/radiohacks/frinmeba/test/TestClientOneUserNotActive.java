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
package de.radiohacks.frinmeba.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import de.radiohacks.frinmeba.model.jaxb.ISiUp;
import de.radiohacks.frinmeba.model.jaxb.OSiUp;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestClientOneUserNotActive extends JerseyTest {
    
    private static final Logger LOGGER = Logger
            .getLogger(TestClientOneUserNotActive.class.getName());
    
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
        helperDatabase help = new helperDatabase();
        help.emptyDatabase();
    }
    
    @Test
    public void TestOneUserNegativeTests() {
        OSiUp out1 = TestSignUpNoValues();
        assertThat("Test out1 - NO_USERNAME_OR_PASSWORD", out1.getET(),
                is(Constants.NO_USERNAME_OR_PASSWORD));
        OSiUp out2 = TestSignUpWithEmail();
        assertThat("Test out2 - NO_USER_OR_PASSWORD", out2.getET(),
                is(Constants.NO_USERNAME_OR_PASSWORD));
        OSiUp out3 = TestSignUpWithEmailPassword();
        assertThat("Test out3 - NO_USERNAME_OR_PASSWORD", out3.getET(),
                is(Constants.NO_USERNAME_OR_PASSWORD));
        OSiUp out4 = TestSignUpWithEmailUser();
        assertThat("Test out4 - NO_USERNAME_OR_PASSWORD", out4.getET(),
                is(Constants.NO_USERNAME_OR_PASSWORD));
        OSiUp out5 = TestSignUpWithEmailUserPassword();
        assertThat("Test out5 - SUCCESSFUL", out5.getR(),
                is(Constants.SIGNUP_SUCCESSFUL));
        OSiUp out5a = TestSignUpWithEmailUserPassword();
        assertThat("Test out5a - USER_ALREADY_EXISTS", out5a.getET(),
                is(Constants.USER_ALREADY_EXISTS));
    }
    
    private OSiUp callTarget(ISiUp in) {
        WebTarget target = ClientBuilder.newClient()
                .target(TestConfig.URL + "user/signup");
        LOGGER.debug(target);
        Response response = target.request()
                .accept(MediaType.APPLICATION_XML_TYPE)
                .buildPost(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        LOGGER.debug(response);
        return response.readEntity(OSiUp.class);
    }
    
    // Test des SignUp ohne Werte = Constants.NO_USERNAME_OR_PASSWORD
    private OSiUp TestSignUpNoValues() {
        ISiUp in = new ISiUp();
        return callTarget(in);
    }
    
    public OSiUp TestSignUpWithEmail() {
        ISiUp in = new ISiUp();
        in.setE(email);
        return callTarget(in);
    }
    
    // Test des SignUp ohne Werte = Constants.NO_USERNAME_OR_PASSWORD
    public OSiUp TestSignUpWithEmailUser() {
        ISiUp in = new ISiUp();
        in.setE(email);
        in.setUN(username);
        return callTarget(in);
    }
    
    // Test des SignUp ohne Werte = Constants.NO_USERNAME_OR_PASSWORD
    public OSiUp TestSignUpWithEmailPassword() {
        ISiUp in = new ISiUp();
        in.setE(email);
        in.setPW(password);
        return callTarget(in);
    }
    
    // Test des SignUp ohne Werte = Constants.NO_USERNAME_OR_PASSWORD
    public OSiUp TestSignUpWithEmailUserPassword() {
        ISiUp in = new ISiUp();
        in.setE(email);
        in.setUN(username);
        in.setPW(password);
        return callTarget(in);
    }
}