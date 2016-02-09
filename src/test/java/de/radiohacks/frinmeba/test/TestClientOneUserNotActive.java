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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

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

import de.radiohacks.frinmeba.modelshort.IAdUC;
import de.radiohacks.frinmeba.modelshort.ICrCh;
import de.radiohacks.frinmeba.modelshort.IIMIC;
import de.radiohacks.frinmeba.modelshort.ISTeM;
import de.radiohacks.frinmeba.modelshort.ISiUp;
import de.radiohacks.frinmeba.modelshort.OAdUC;
import de.radiohacks.frinmeba.modelshort.OAuth;
import de.radiohacks.frinmeba.modelshort.OCrCh;
import de.radiohacks.frinmeba.modelshort.ODMFC;
import de.radiohacks.frinmeba.modelshort.ODeCh;
import de.radiohacks.frinmeba.modelshort.OFMFC;
import de.radiohacks.frinmeba.modelshort.OGTeM;
import de.radiohacks.frinmeba.modelshort.OIMIC;
import de.radiohacks.frinmeba.modelshort.OLiCh;
import de.radiohacks.frinmeba.modelshort.OLiUs;
import de.radiohacks.frinmeba.modelshort.OReUC;
import de.radiohacks.frinmeba.modelshort.OSTeM;
import de.radiohacks.frinmeba.modelshort.OSiUp;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;

public class TestClientOneUserNotActive extends JerseyTest {
    
    private static final Logger LOGGER = Logger.getLogger(TestClientOneUserNotActive.class.getName());

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
        dropDatabaseTables drop = new dropDatabaseTables();
        drop.dropTable();
        createDatabaseTables create = new createDatabaseTables();
        create.createTable();
    }

    // configure auskommentieren wenn localhost als Ziel gewählt werden soll.
    /*
     * @Override protected Application configure() { ResourceConfig rc = new
     * ResourceConfig(ServiceImpl.class); return rc; }
     */

    // Es gibt nur eine Testfunktion, diese muss alle Funktionen in der
    // richtigen Reihenfilge ausführen. Nachdem der Signup durch ist, ist die
    // Reihenfolge egal da immer USER_NOT_ACTIVE zurück kommt
    @Test
    public void TestOneUserNegativeTests() {
        OSiUp out1 = TestSignUpNoValues();
        assertThat("Test out1 - NO_USERNAME_OR_PASSWORD", out1.getET(),is(Constants.NO_USERNAME_OR_PASSWORD));
        OSiUp out2 = TestSignUpWithEmail();
        assertThat("Test out2 - NO_USER_OR_PASSWORD",  out2.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
        OSiUp out3 = TestSignUpWithEmailPassword();
        assertThat("Test out3 - NO_USERNAME_OR_PASSWORD", out3.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
        OSiUp out4 = TestSignUpWithEmailUser();
        assertThat("Test out4 - NO_USERNAME_OR_PASSWORD", out4.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
        OSiUp out5 = TestSignUpWithEmailUserPassword();
        assertThat("Test out5 - SUCCESSFUL", out5.getSU(), is("SUCCESSFUL"));
        OSiUp out5a = TestSignUpWithEmailUserPassword();
        assertThat("Test out5a - USER_ALREADY_EXISTS", out5a.getET(), is(Constants.USER_ALREADY_EXISTS));
        OAuth out6 = TestAuthenticateNotActive();
        assertThat("Test out6 - USER_NOT_ACTIVE", out6.getET(), is(Constants.USER_NOT_ACTIVE));
        OCrCh out7 = TestCreateChatNotActive();
        assertThat("Test out7 - USER_NOT_ACTIVE", out7.getET(), is(Constants.USER_NOT_ACTIVE));
        ODeCh out8 = TestDeleteChatNotActive();
        assertThat("Test out8 - USER_NOT_ACTIVE", out8.getET(), is(Constants.USER_NOT_ACTIVE));
        OAdUC out9 = TestAddUserToChatNotActive();
        assertThat("Test out8 - USER_NOT_ACTIVE", out9.getET(), is(Constants.USER_NOT_ACTIVE));
        OReUC out10 = TestRemoveUserFromChatNotActive();
        assertThat("Test out10 - USER_NOT_ACTIVE", out10.getET(), is(Constants.USER_NOT_ACTIVE));
        OLiUs out11 = TestListUserNotActive();
        assertThat("Test out11 - USER_NOT_ACTIVE", out11.getET(), is(Constants.USER_NOT_ACTIVE));
        OLiCh out12 = TestListChatNotActive();
        assertThat("Test out12 - USER_NOT_ACTIVE", out12.getET(), is(Constants.USER_NOT_ACTIVE));
        OSTeM out13 = TestSendTextMessageNotActive();
        assertThat("Test out13 - USER_NOT_ACTIVE", out13.getET(), is(Constants.USER_NOT_ACTIVE));
        OGTeM out14 = TestGetTextMessageNotActive();
        assertThat("Test out14 - USER_NOT_ACTIVE", out14.getET(), is(Constants.USER_NOT_ACTIVE));
        OIMIC out15 = TestInsertMessageIntoChatNotActive();
        assertThat("Test out15 - USER_NOT_ACTIVE", out15.getET(), is(Constants.USER_NOT_ACTIVE));
        OFMFC out16 = TestGetMessageFromChatNotActive();
        assertThat("Test out16 - USER_NOT_ACTIVE", out16.getET(), is(Constants.USER_NOT_ACTIVE));
        // OutCheckNewMessages out17 = TestCheckNewMessagesNotActive();
        // Assert.assertEquals(Constants.USER_NOT_ACTIVE, out17.getET());
        ODMFC out18 = TestDeleMessageFromChatNotActive();
        assertThat("Test out18 - USER_NOT_ACTIVE", out18.getET(), is(Constants.USER_NOT_ACTIVE));
    }

    private OSiUp callTarget(ISiUp in) {
        WebTarget target = ClientBuilder.newClient().target(
                TestConfig.URL + "user/signup");
        LOGGER.debug(target);
        Response response = target.request()
                .buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
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

    public OAuth TestAuthenticateNotActive() {
        WebTarget target;
        if (TestConfig.remote) {
            target = ClientBuilder.newClient()
                    .target(TestConfig.URL + "user/authenticate")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_USERNAME, username);
        } else {
            target = target("user/authenticate").queryParam(
                    Constants.QP_PASSWORD, password).queryParam(
                    Constants.QP_USERNAME, username);
        }
        return target.request().get(OAuth.class);
    }

    public OCrCh TestCreateChatNotActive() {
        WebTarget target = ClientBuilder.newClient().target(
                TestConfig.URL + "user/createchat");
        ICrCh in = new ICrCh();
        in.setCN(Base64.encodeBase64String("Testchat".getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setPW(password);
        in.setUN(username);
        Response response = target.request()
                .buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        return response.readEntity(OCrCh.class);
    }

    public ODeCh TestDeleteChatNotActive() {
        WebTarget target;
        if (TestConfig.remote) {
            target = ClientBuilder.newClient()
                    .target(TestConfig.URL + "user/deletechat")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_CHATID, 1)
                    .queryParam(Constants.QP_USERNAME, username);
        } else {
            target = target("user/deletechat")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_CHATID, 1)
                    .queryParam(Constants.QP_USERNAME, username);
        }
        return target.request().delete(ODeCh.class);
    }

    public OAdUC TestAddUserToChatNotActive() {
        WebTarget target = ClientBuilder.newClient().target(
                TestConfig.URL + "user/addusertochat");
        IAdUC in = new IAdUC();
        in.setUN(username);
        in.setPW(password);
        in.setCID(1);
        in.setUID(1);
        Response response = target.request()
                .buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        return response.readEntity(OAdUC.class);
    }

    public OReUC TestRemoveUserFromChatNotActive() {
        WebTarget target;
        if (TestConfig.remote) {
            target = ClientBuilder.newClient()
                    .target(TestConfig.URL + "user/removeuserfromchat")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_USERID, 1)
                    .queryParam(Constants.QP_CHATID, 1)
                    .queryParam(Constants.QP_USERNAME, username);
        } else {
            target = target("user/removeuserfromchat")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_USERID, 1)
                    .queryParam(Constants.QP_CHATID, 1)
                    .queryParam(Constants.QP_USERNAME, username);
        }
        return target.request().delete(OReUC.class);
    }

    public OLiUs TestListUserNotActive() {
        WebTarget target;
        if (TestConfig.remote) {
            target = ClientBuilder
                    .newClient()
                    .target(TestConfig.URL + "user/listuser")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(
                            Constants.QP_SEARCH,
                            Base64.encodeBase64String("Test".getBytes(Charset
                                    .forName(Constants.CHARACTERSET))))
                    .queryParam(Constants.QP_USERNAME, username);
        } else {
            target = target("user/listuser")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(
                            Constants.QP_SEARCH,
                            Base64.encodeBase64String("Test".getBytes(Charset
                                    .forName(Constants.CHARACTERSET))))
                    .queryParam(Constants.QP_USERNAME, username);
        }
        return target.request().get(OLiUs.class);
    }

    public OLiCh TestListChatNotActive() {
        WebTarget target;
        if (TestConfig.remote) {
            target = ClientBuilder.newClient()
                    .target(TestConfig.URL + "user/listchat")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_USERNAME, username);
        } else {
            target = target("user/listchat").queryParam(Constants.QP_PASSWORD,
                    password).queryParam(Constants.QP_USERNAME, username);
        }
        return target.request().get(OLiCh.class);
    }

    public OSTeM TestSendTextMessageNotActive() {
        WebTarget target = ClientBuilder.newClient().target(
                TestConfig.URL + "user/sendtextmessage");
        ISTeM in = new ISTeM();
        in.setPW(password);
        in.setUN(username);
        in.setTM(Base64.encodeBase64String("Text Message".getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        Response response = target.request()
                .buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        return response.readEntity(OSTeM.class);
    }

    public OGTeM TestGetTextMessageNotActive() {
        WebTarget target;
        if (TestConfig.remote) {
            target = ClientBuilder.newClient()
                    .target(TestConfig.URL + "user/gettextmessage")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_USERNAME, username)
                    .queryParam(Constants.QP_TEXTMESSAGEID, 1);
        } else {
            target = target("user/gettextmessage")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_USERNAME, username)
                    .queryParam(Constants.QP_TEXTMESSAGEID, 1);
        }
        return target.request().get(OGTeM.class);
    }

    public OIMIC TestInsertMessageIntoChatNotActive() {
        WebTarget target = ClientBuilder.newClient().target(
                TestConfig.URL + "user/insertmessageintochat");
        IIMIC in = new IIMIC();
        in.setUN(username);
        in.setPW(password);
        in.setMT(Base64.encodeBase64String(Constants.TYP_CONTACT
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(1);
        in.setMID(1);

        Response response = target.request()
                .buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        return response.readEntity(OIMIC.class);
    }

    public OFMFC TestGetMessageFromChatNotActive() {
        WebTarget target;
        if (TestConfig.remote) {
            target = ClientBuilder.newClient()
                    .target(TestConfig.URL + "user/getmessagefromchat")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_USERNAME, username)
                    .queryParam(Constants.QP_CHATID, 1)
                    .queryParam(Constants.QP_TIMESTAMP, 0);
        } else {
            target = target("user/getmessagefromchat")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_USERNAME, username)
                    .queryParam(Constants.QP_CHATID, 1)
                    .queryParam(Constants.QP_TIMESTAMP, 0);
        }
        return target.request().get(OFMFC.class);
    }

    /*
     * public OutCheckNewMessages TestCheckNewMessagesNotActive() { WebTarget
     * target; if (TestConfig.remote) { target = ClientBuilder.newClient()
     * .target(TestConfig.URL + "user/checknewmessages")
     * .queryParam(Constants.QPpassword, password)
     * .queryParam(Constants.QPusername, username); } else { target =
     * target("user/checknewmessages").queryParam( Constants.QPpassword,
     * password).queryParam( Constants.QPusername, username); } return
     * target.request().get(OutCheckNewMessages.class); }
     */

    public ODMFC TestDeleMessageFromChatNotActive() {
        WebTarget target;
        if (TestConfig.remote) {
            target = ClientBuilder.newClient()
                    .target(TestConfig.URL + "user/deletemessagefromchat")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_USERNAME, username)
                    .queryParam(Constants.QP_MESSAGEID, 1);
        } else {
            target = target("user/deletemessagefromchat")
                    .queryParam(Constants.QP_PASSWORD, password)
                    .queryParam(Constants.QP_USERNAME, username)
                    .queryParam(Constants.QP_MESSAGEID, 1);
        }
        return target.request().delete(ODMFC.class);
    }
}