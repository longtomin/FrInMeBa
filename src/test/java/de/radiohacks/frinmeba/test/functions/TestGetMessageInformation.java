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

import de.radiohacks.frinmeba.modelshort.OGMI;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestGetMessageInformation extends JerseyTest {

	/*
	 * @GET
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/getmessageinformation") public OGMI getMessageInformation(
	 * 
	 * @QueryParam(Constants.QPusername) String User,
	 * 
	 * @QueryParam(Constants.QPpassword) String Password,
	 * 
	 * @QueryParam(Constants.QPmessageid) int MessageID);
	 */

	final static String username1_org = "Test1";
	final static String username1 = Base64.encodeBase64String(username1_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String password1_org = "Test1";
	final static String password1 = Base64.encodeBase64String(password1_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String email1_org = "Test1@frinme.org";
	final static String email1 = Base64.encodeBase64String(email1_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String username2_org = "Test2";
	final static String username2 = Base64.encodeBase64String(username2_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String password2_org = "Test2";
	final static String password2 = Base64.encodeBase64String(password2_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String email2_org = "Test2@frinme.org";
	final static String email2 = Base64.encodeBase64String(email2_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String username3_org = "Test3";
	final static String username3 = Base64.encodeBase64String(username3_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String password3_org = "Test3";
	final static String password3 = Base64.encodeBase64String(password3_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String email3_org = "Test3@frinme.org";
	final static String email3 = Base64.encodeBase64String(email3_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String username4_org = "Test4";
	final static String username4 = Base64.encodeBase64String(username4_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String password4_org = "Test4";
	final static String password4 = Base64.encodeBase64String(password4_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String email4_org = "Test4@frinme.org";
	final static String email4 = Base64.encodeBase64String(email4_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String username5_org = "Test5";
	final static String username5 = Base64.encodeBase64String(username5_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String password5_org = "Test5";
	final static String password5 = Base64.encodeBase64String(password5_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String email5_org = "Test5@frinme.org";
	final static String email5 = Base64.encodeBase64String(email5_org
			.getBytes(Charset.forName(Constants.CharacterSet)));

	final static String functionurl = "user/getmessageinformation";

	static int msg1;
	final static String textmnsg1_org = "Test1 Nachricht ;-) 'o)";
	final static String textmnsg1 = Base64.encodeBase64String(textmnsg1_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	static int msg2;
	final static String textmnsg2_org = "Nachricht2 von Test1 ä ö ü Ä Ö Ü";
	final static String textmnsg2 = Base64.encodeBase64String(textmnsg2_org
			.getBytes(Charset.forName(Constants.CharacterSet)));

	static int msgid11;
	static int msgid12;
	static int msgid13;
	static int msgid14;
	static int msgid15;

	static int msgid21;
	static int msgid22;
	static int msgid23;
	static int msgid24;
	static int msgid25;

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

		msg1 = help.CreateContentMessage(textmnsg1, Constants.TYP_TEXT);
		msg2 = help.CreateContentMessage(textmnsg2, Constants.TYP_TEXT);

		int cid = help.CreateChat(username1_org, "Test1 Chat");
		int u2c1 = help.AddUserToChat(help.getUserID(username1_org), cid);
		int u2c2 = help.AddUserToChat(help.getUserID(username2_org), cid);
		int u2c3 = help.AddUserToChat(help.getUserID(username3_org), cid);
		int u2c4 = help.AddUserToChat(help.getUserID(username4_org), cid);
		int u2c5 = help.AddUserToChat(help.getUserID(username5_org), cid);

		int cid2 = help.CreateChat(username2_org, "Test1 Chat 2");
		int u2c12 = help.AddUserToChat(help.getUserID(username1_org), cid2);
		int u2c22 = help.AddUserToChat(help.getUserID(username2_org), cid2);
		int u2c32 = help.AddUserToChat(help.getUserID(username3_org), cid2);
		int u2c42 = help.AddUserToChat(help.getUserID(username4_org), cid2);

		// User2Chat anlegen 3-5 x

		msgid11 = help.insertMessage(help.getUserID(username1_org), u2c1,
				Constants.TYP_TEXT, msg1, 0, true);
		msgid12 = help.insertMessage(help.getUserID(username1_org), u2c2,
				Constants.TYP_TEXT, msg1, msgid11, false);
		msgid13 = help.insertMessage(help.getUserID(username1_org), u2c3,
				Constants.TYP_TEXT, msg1, msgid11, false);
		msgid14 = help.insertMessage(help.getUserID(username1_org), u2c4,
				Constants.TYP_TEXT, msg1, msgid11, false);
		msgid15 = help.insertMessage(help.getUserID(username1_org), u2c5,
				Constants.TYP_TEXT, msg1, msgid11, false);

		msgid22 = help.insertMessage(help.getUserID(username2_org), u2c22,
				Constants.TYP_TEXT, msg2, 0, true);
		msgid21 = help.insertMessage(help.getUserID(username2_org), u2c12,
				Constants.TYP_TEXT, msg2, msgid22, false);
		msgid23 = help.insertMessage(help.getUserID(username2_org), u2c32,
				Constants.TYP_TEXT, msg2, msgid22, false);
		msgid24 = help.insertMessage(help.getUserID(username2_org), u2c42,
				Constants.TYP_TEXT, msg2, msgid22, false);

	}

	@Test
	public void testGetMessageInformationUpNoValues() {
		WebTarget target = ClientBuilder.newClient().target(
				TestConfig.URL + functionurl);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetMessageInformationUser() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPusername, username1);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetMessageInformationPassword() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPpassword, password1);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetMessageInformationUserTextmessage() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPusername, username1)
				.queryParam(Constants.QPmessageid, msg2);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetMessageInformationPasswordTextmessage() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPpassword, password1)
				.queryParam(Constants.QPmessageid, msgid11);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetMessageInformationUserPassword() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPpassword, password1)
				.queryParam(Constants.QPusername, username1);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertEquals(Constants.NONE_EXISTING_MESSAGE, out.getET());
	}

	@Test
	public void testGetMessageInformationUserWrongPassword() {
		WebTarget target = ClientBuilder
				.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(
						Constants.QPpassword,
						Base64.encodeBase64String("XXX".getBytes(Charset
								.forName(Constants.CharacterSet))))
				.queryParam(Constants.QPusername, username1)
				.queryParam(Constants.QPmessageid, msgid11);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertEquals(Constants.WRONG_PASSWORD, out.getET());
	}

	@Test
	public void testGetMessageInformationUserEncodeFailureUser() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPpassword, password1)
				.queryParam(Constants.QPusername, "�$%1234")
				.queryParam(Constants.QPmessageid, msgid11);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testGetMessageInformationUserEncodeFailurePassword() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPpassword, "�$%1234")
				.queryParam(Constants.QPusername, username1)
				.queryParam(Constants.QPmessageid, msgid11);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testGetMessageInformationUserPasswordTextmessage1() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPpassword, password1)
				.queryParam(Constants.QPusername, username1)
				.queryParam(Constants.QPmessageid, msgid11);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertNotNull(out.getMIB().size());
		Assert.assertNotNull(out.getMIB().get(0).getMI().size());
	}

	@Test
	public void testGetMessageInformationUserPasswordTextmessage2() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPpassword, password1)
				.queryParam(Constants.QPusername, username1)
				.queryParam(Constants.QPmessageid, msgid21);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertNotNull(out.getMIB().size());
		Assert.assertNotNull(out.getMIB().get(0).getMI().size());
	}

	@Test
	public void testGetMessageInformationUserPasswordMultipleMessages() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPpassword, password1)
				.queryParam(Constants.QPusername, username1)
				.queryParam(Constants.QPmessageid, msgid11)
				.queryParam(Constants.QPmessageid, msgid21);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertNotNull(out.getMIB().size());
		Assert.assertNotNull(out.getMIB().get(1).getMI().size());
	}

	@Test
	public void testGetMessageInformationUserPasswordMultipleMessagesNotOwner() {
		WebTarget target = ClientBuilder.newClient()
				.target(TestConfig.URL + functionurl)
				.queryParam(Constants.QPpassword, password5)
				.queryParam(Constants.QPusername, username5)
				.queryParam(Constants.QPmessageid, msgid15)
				.queryParam(Constants.QPmessageid, msgid22);
		OGMI out = target.request().get(OGMI.class);
		Assert.assertEquals(Constants.NOT_MESSAGE_OWNER, out.getET());
	}
}