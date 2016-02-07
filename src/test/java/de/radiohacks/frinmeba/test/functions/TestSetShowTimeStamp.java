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

import de.radiohacks.frinmeba.modelshort.ISShT;
import de.radiohacks.frinmeba.modelshort.OSShT;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestSetShowTimeStamp extends JerseyTest {

    /*
     * @POST
     * 
     * @Produces(MediaType.APPLICATION_XML)
     * 
     * @Consumes(MediaType.APPLICATION_XML)
     * 
     * @Path("/setshowtimestamp") public OSShT setShowTimeStamp(ISShT in);
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
    final static String functionurl = "user/setshowtimestamp";

    static int cid;
    static int txtmsgid1;
    static int txtmsgid2;
    static int txtmsgid3;
    static int videomsgid;
    static int imagemsgid;
    static int locationmsgid;
    static int filemsgid;
    static int contactmsgid;
    static int msg1;
    static int msg2;

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
        int uid1 = help.getUserID(username1_org);
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
        int u2c1 = help.AddUserToChat(help.getUserID(username1_org), cid);
        int u2c2 = help.AddUserToChat(help.getUserID(username2_org), cid);

        msg1 = help.insertMessage(uid1, u2c1, Constants.TYP_TEXT, txtmsgid1, 0,
                true);
        msg2 = help.insertMessage(uid1, u2c2, Constants.TYP_TEXT, txtmsgid1,
                msg1, false);

        // TODO Nachricht in einem Chat einfuegen bestimmt fuer 2 User.
        // Einmal den eigenen TimeStamp setzen.
        // Einmal versuchen die Nachricht für den User 2 den Timestamp zu
        // setzen
        // = ERROR
    }

    private OSShT callTarget(ISShT in) {
        WebTarget target = ClientBuilder.newClient().target(
                TestConfig.URL + functionurl);
        Response response = target.request()
                .buildPost(Entity.entity(in, MediaType.APPLICATION_XML))
                .invoke();
        return response.readEntity(OSShT.class);
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampUpNoValues() {
        ISShT in = new ISShT();
        OSShT out = callTarget(in);
        Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampUser() {
        ISShT in = new ISShT();
        in.setUN(username1);
        OSShT out = callTarget(in);
        Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampPassword() {
        ISShT in = new ISShT();
        in.setPW(password1);
        OSShT out = callTarget(in);
        Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampUserPassword() {
        ISShT in = new ISShT();
        in.setUN(username1);
        in.setPW(password1);
        OSShT out = callTarget(in);
        Assert.assertEquals(Constants.NONE_EXISTING_MESSAGE, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampUserWrongPassword() {
        ISShT in = new ISShT();
        in.setUN(username1);
        in.setPW(Base64.encodeBase64String("XXX".getBytes(Charset
                .forName(Constants.CHARACTERSET))));
        OSShT out = callTarget(in);
        Assert.assertEquals(Constants.WRONG_PASSWORD, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampUserEncodeFailureUser() {
        ISShT in = new ISShT();
        in.setUN("XXX");
        in.setPW(password1);
        in.getMID().add(msg1);
        OSShT out = callTarget(in);
        Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampUserEncodeFailurePassword() {
        ISShT in = new ISShT();
        in.setUN(username1);
        in.setPW("XXX");
        OSShT out = callTarget(in);
        Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampUserPasswordMsgNotRead1() {
        ISShT in = new ISShT();
        in.setUN(username1);
        in.setPW(password1);
        in.getMID().add(msg2);
        OSShT out = callTarget(in);
        Assert.assertEquals(Constants.MESSAGE_NOT_READ, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampUserPasswordMsgNotRead() {
        ISShT in = new ISShT();
        in.setUN(username1);
        in.setPW(password1);
        in.getMID().add(msg1);
        OSShT out = callTarget(in);
        Assert.assertEquals(Constants.MESSAGE_NOT_READ, out.getET());
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampUserPasswordMsgWrongMsg() {
        helperDatabase help = new helperDatabase();
        help.setTimestamp(msg2, 11, "READ");
        ISShT in = new ISShT();
        in.setUN(username1);
        in.setPW(password1);
        in.getMID().add(msg2);
        OSShT out = callTarget(in);
        Assert.assertEquals(Constants.NOT_MESSAGE_OWNER, out.getET());
        help.setTimestamp(msg2, 0, "READ");
    }

    @Test
    @Ignore("temporarily suspended")
    public void testSetShowTimeStampUserPasswordMsgRightMsg() {
        helperDatabase help = new helperDatabase();
        help.setTimestamp(msg1, 11, "READ");
        ISShT in = new ISShT();
        in.setUN(username1);
        in.setPW(password1);
        in.getMID().add(msg1);
        OSShT out = callTarget(in);
        Assert.assertNotNull(out.getShT());
        help.setTimestamp(msg1, 0, "READ");
    }
}