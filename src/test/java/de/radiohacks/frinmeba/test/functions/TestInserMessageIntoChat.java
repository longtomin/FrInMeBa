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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.radiohacks.frinmeba.modelshort.IIMIC;
import de.radiohacks.frinmeba.modelshort.OIMIC;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestInserMessageIntoChat extends JerseyTest {

    /*
     * @PUT
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Consumes(MediaType.APPLICATION_XML)
     * 
     * @Path("/insertmessageintochat") public OIMIC insertMessageIntoChat(IIMIC
     * in);
     */

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

    final static String functionurl = "user/insertmessageintochat";

    static int cid;
    static int txtmsgid1;
    static int txtmsgid2;
    static int txtmsgid3;
    static int videomsgid;
    static int imagemsgid;
    static int locationmsgid;
    static int filemsgid;
    static int contactmsgid;

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
        helperDatabase help = new helperDatabase();
        help.CreateActiveUser(username1_org, username1, password1_org,
                email1_org, help.InsertFixedImage());
        help.CreateActiveUser(username2_org, username2, password2_org,
                email2_org, help.InsertFixedImage());
        help.CreateActiveUser(username3_org, username3, password3_org,
                email3_org, help.InsertFixedImage());
        help.CreateActiveUser(username4_org, username4, password4_org,
                email4_org, help.InsertFixedImage());
        help.CreateActiveUser(username5_org, username5, password5_org,
                email5_org, help.InsertFixedImage());
        cid = help.CreateChat(username1_org, "TestChat");
        txtmsgid1 = help.CreateContentMessage("TEST Text Message von User1",
                Constants.TYP_TEXT);
        txtmsgid2 = help.CreateContentMessage("TEST Text Message von User2",
                Constants.TYP_TEXT);
        txtmsgid3 = help.CreateContentMessage("TEST Text Message von User3",
                Constants.TYP_TEXT);
        imagemsgid = help
                .CreateContentMessage("Image Msg", Constants.TYP_IMAGE);
        videomsgid = help
                .CreateContentMessage("Video Msg", Constants.TYP_VIDEO);
        locationmsgid = help.CreateContentMessage("Location Msg",
                Constants.TYP_LOCATION);
        filemsgid = help.CreateContentMessage("File Msg", Constants.TYP_FILE);
        contactmsgid = help.CreateContentMessage("Contact Msg",
                Constants.TYP_CONTACT);
        help.AddUserToChat(help.getUserID(username1_org), cid);
        help.AddUserToChat(help.getUserID(username2_org), cid);
        help.AddUserToChat(help.getUserID(username3_org), cid);
        help.AddUserToChat(help.getUserID(username4_org), cid);
        help.AddUserToChat(help.getUserID(username5_org), cid);
    }

    private OIMIC callTarget(IIMIC in) {
        WebTarget target = ClientBuilder.newClient().target(
                TestConfig.URL + functionurl);
        Response response = target.request()
                .buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        return response.readEntity(OIMIC.class);
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUpNoValues() {
        IIMIC in = new IIMIC();
        OIMIC out = callTarget(in);
        Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUser() {
        IIMIC in = new IIMIC();
        in.setUN(username1);
        OIMIC out = callTarget(in);
        Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatPassword() {
        IIMIC in = new IIMIC();
        in.setPW(password1);
        OIMIC out = callTarget(in);
        Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPassword() {
        IIMIC in = new IIMIC();
        in.setUN(username1);
        in.setPW(password1);
        OIMIC out = callTarget(in);
        Assert.assertEquals(Constants.INVALID_MESSAGE_TYPE, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserWrongPasswordNoMessageType() {
        IIMIC in = new IIMIC();
        in.setUN(username1);
        in.setPW(Base64.encodeBase64String("XXX".getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        OIMIC out = callTarget(in);
        Assert.assertEquals(Constants.INVALID_MESSAGE_TYPE, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserWrongPasswordMessageType() {
        IIMIC in = new IIMIC();
        in.setUN(username1);
        in.setPW(Base64.encodeBase64String("XXX".getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        OIMIC out = callTarget(in);
        Assert.assertEquals(Constants.WRONG_PASSWORD, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserEncodeFailureUser() {
        IIMIC in = new IIMIC();
        in.setUN("XXX");
        in.setPW(password1);
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        in.setMID(contactmsgid);
        OIMIC out = callTarget(in);
        Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserEncodeFailurePassword() {
        IIMIC in = new IIMIC();
        in.setUN(username1);
        in.setPW("XXX");
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        in.setMID(contactmsgid);
        OIMIC out = callTarget(in);

        Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPasswordMessageTypeChatID() {
        IIMIC in = new IIMIC();
        in.setUN(username1);
        in.setPW(password1);
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        OIMIC out = callTarget(in);
        Assert.assertEquals(Constants.NONE_EXISTING_MESSAGE, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPasswordMessageTypeMsgID() {
        IIMIC in = new IIMIC();
        in.setUN(username1);
        in.setPW(password1);
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setMID(contactmsgid);
        OIMIC out = callTarget(in);
        Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPasswordMessageTypeMsgIDChatIDUUser1() {
        IIMIC in = new IIMIC();
        in.setUN(username1);
        in.setPW(password1);
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        in.setMID(txtmsgid1);
        OIMIC out = callTarget(in);
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPasswordMessageTypeMsgIDChatIDUUser3() {
        IIMIC in = new IIMIC();
        in.setUN(username3);
        in.setPW(password3);
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        in.setMID(txtmsgid3);
        OIMIC out = callTarget(in);
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPasswordMessageTypeMsgIDChatIDUUser2() {
        IIMIC in = new IIMIC();
        in.setUN(username2);
        in.setPW(password2);
        in.setMT(Base64.encodeBase64String(Constants.TYP_TEXT.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        in.setMID(txtmsgid2);
        OIMIC out = callTarget(in);
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPasswordMessageTypeMsgIDChatIDImage() {
        IIMIC in = new IIMIC();
        in.setUN(username2);
        in.setPW(password2);
        in.setMT(Base64.encodeBase64String(Constants.TYP_IMAGE.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        in.setMID(imagemsgid);
        OIMIC out = callTarget(in);
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPasswordMessageTypeMsgIDChatIDVideo() {
        IIMIC in = new IIMIC();
        in.setUN(username2);
        in.setPW(password2);
        in.setMT(Base64.encodeBase64String(Constants.TYP_VIDEO.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        in.setMID(videomsgid);
        OIMIC out = callTarget(in);
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPasswordMessageTypeMsgIDChatIDLocation() {
        IIMIC in = new IIMIC();
        in.setUN(username2);
        in.setPW(password2);
        in.setMT(Base64.encodeBase64String(Constants.TYP_LOCATION
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        in.setMID(locationmsgid);
        OIMIC out = callTarget(in);
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPasswordMessageTypeMsgIDChatIDFile() {
        IIMIC in = new IIMIC();
        in.setUN(username2);
        in.setPW(password2);
        in.setMT(Base64.encodeBase64String(Constants.TYP_FILE.getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        in.setMID(filemsgid);
        OIMIC out = callTarget(in);
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testInserMessageIntoChatUserPasswordMessageTypeMsgIDChatIDContact() {
        IIMIC in = new IIMIC();
        in.setUN(username1);
        in.setPW(password1);
        in.setMT(Base64.encodeBase64String(Constants.TYP_CONTACT
                .getBytes(Charset.forName(Constants.CHARACTERSET))));
        in.setCID(cid);
        in.setMID(contactmsgid);
        OIMIC out = callTarget(in);
        Assert.assertNotNull(out.getMID());
        Assert.assertNotNull(out.getSdT());
    }
}
