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
import org.apache.log4j.Logger;
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

import de.radiohacks.frinmeba.modelshort.OFMFC;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestGetMessageFromChat extends JerseyTest {

	/*
	 * @GET
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/getmessagefromchat") public OFMFC
	 * getMessageFromChat(@QueryParam(Constants.QPusername) String User,
	 * 
	 * @QueryParam(Constants.QPpassword) String Password,
	 * 
	 * @QueryParam(Constants.QPchatid) int ChatID,
	 * 
	 * @QueryParam(Constants.QPtimestamp) int Timestamp);
	 */

	private static final Logger LOGGER = Logger.getLogger(TestGetMessageFromChat.class.getName());

	final static String username1_org = "Test1";
	final static String username1 = Base64
			.encodeBase64String(username1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String password1_org = "Test1";
	final static String password1 = Base64
			.encodeBase64String(password1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String email1_org = "Test1@frinme.org";
	final static String email1 = Base64
			.encodeBase64String(email1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String username2_org = "Test2";
	final static String username2 = Base64
			.encodeBase64String(username2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String password2_org = "Test2";
	final static String password2 = Base64
			.encodeBase64String(password2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String email2_org = "Test2@frinme.org";
	final static String email2 = Base64
			.encodeBase64String(email2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String username3_org = "Test3";
	final static String username3 = Base64
			.encodeBase64String(username3_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String password3_org = "Test3";
	final static String password3 = Base64
			.encodeBase64String(password3_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String email3_org = "Test3@frinme.org";
	final static String email3 = Base64
			.encodeBase64String(email3_org.getBytes(Charset.forName(Constants.CHARACTERSET)));

	final static String functionurl = "user/getmessagefromchat";

	static int cid1to2;
	static int cid3to12;

	@Override
	protected TestContainerFactory getTestContainerFactory() {
		return new GrizzlyWebTestContainerFactory();
	}

	@Override
	protected DeploymentContext configureDeployment() {
		return ServletDeploymentContext.forServlet(new ServletContainer(new ResourceConfig(ServiceImpl.class))).build();
	}

	@BeforeClass
	public static void prepareDB() {
		dropDatabaseTables drop = new dropDatabaseTables();
		drop.dropTable();
		createDatabaseTables create = new createDatabaseTables();
		create.createTable();
		helperDatabase help = new helperDatabase();
		help.CreateActiveUser(username1_org, username1, password1_org, email1_org, help.InsertFixedImage());
		help.CreateActiveUser(username2_org, username2, password2_org, email2_org, help.InsertFixedImage());
		help.CreateActiveUser(username3_org, username3, password3_org, email3_org, help.InsertFixedImage());
		cid1to2 = help.CreateChat(username1_org, "1to2");
		cid3to12 = help.CreateChat(username3_org, "3to12");
		int m1 = help.CreateContentMessage("Text1", Constants.TYP_TEXT);
		int m2 = help.CreateContentMessage("Text2", Constants.TYP_TEXT);
		int m3 = help.CreateContentMessage("Text3", Constants.TYP_TEXT);
		int m4 = help.CreateContentMessage("Text4", Constants.TYP_TEXT);
		int m5 = help.CreateContentMessage("1010101010test.jpg", Constants.TYP_IMAGE);
		int m6 = help.CreateContentMessage("1010101010test.mp4", Constants.TYP_VIDEO);
		int uid1 = help.getUserID(username1_org);
		int uid2 = help.getUserID(username2_org);
		int uid3 = help.getUserID(username3_org);
		int u2c1to2_1 = help.AddUserToChat(uid1, cid1to2);
		int u2c1to2_2 = help.AddUserToChat(uid2, cid1to2);
		int u2c3to12_1 = help.AddUserToChat(uid1, cid3to12);
		int u2c3to12_2 = help.AddUserToChat(uid2, cid3to12);
		int u2c3to12_3 = help.AddUserToChat(uid3, cid3to12);

		int messagesid1 = help.insertMessage(uid1, u2c1to2_1, Constants.TYP_TEXT, m1, 0, true);
		help.insertMessage(uid1, u2c1to2_2, Constants.TYP_TEXT, m1, messagesid1, false);
		int messagesid2 = help.insertMessage(uid3, u2c3to12_3, Constants.TYP_TEXT, m2, 0, true);
		help.insertMessage(uid3, u2c3to12_2, Constants.TYP_TEXT, m2, messagesid2, false);
		help.insertMessage(uid3, u2c3to12_1, Constants.TYP_TEXT, m2, messagesid2, false);
		int messagesid3 = help.insertMessage(uid2, u2c1to2_1, Constants.TYP_TEXT, m3, 0, true);
		help.insertMessage(uid2, u2c1to2_2, Constants.TYP_TEXT, m3, messagesid3, false);
		int messagesid4 = help.insertMessage(uid2, u2c3to12_3, Constants.TYP_TEXT, m4, 0, true);
		help.insertMessage(uid2, u2c3to12_2, Constants.TYP_TEXT, m4, messagesid4, false);
		help.insertMessage(uid2, u2c3to12_1, Constants.TYP_TEXT, m4, messagesid4, false);

		int messagesid5 = help.insertMessage(uid1, u2c1to2_1, Constants.TYP_IMAGE, m5, 0, true);
		help.insertMessage(uid1, u2c1to2_2, Constants.TYP_IMAGE, m5, messagesid5, false);
		int messagesid6 = help.insertMessage(uid1, u2c3to12_3, Constants.TYP_VIDEO, m6, 0, true);
		help.insertMessage(uid1, u2c3to12_2, Constants.TYP_VIDEO, m6, messagesid6, false);
		help.insertMessage(uid1, u2c3to12_1, Constants.TYP_VIDEO, m6, messagesid6, false);
	}

	@Test
	public void testGetMessageFromChatUpNoValues() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl);
		} else {
			target = target(functionurl);
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetMessageFromChatUser() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl).queryParam(Constants.QP_USERNAME,
					username1);
		} else {
			target = target(functionurl).queryParam(Constants.QP_USERNAME, username1);
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetMessageFromChatPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl).queryParam(Constants.QP_PASSWORD,
					password1);
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password1);
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetMessageFromChatUserPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password1).queryParam(Constants.QP_USERNAME, username1);
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password1).queryParam(Constants.QP_USERNAME,
					username1);
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testGetMessageFromChatUserWrongPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD,
							Base64.encodeBase64String("XXX".getBytes(Charset.forName(Constants.CHARACTERSET))))
					.queryParam(Constants.QP_USERNAME, username1);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QP_PASSWORD,
							Base64.encodeBase64String("XXX".getBytes(Charset.forName(Constants.CHARACTERSET))))
					.queryParam(Constants.QP_USERNAME, username1);
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertEquals(Constants.WRONG_PASSWORD, out.getET());
	}

	@Test
	public void testGetMessageFromChatUserEncodeFailureUser() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password1).queryParam(Constants.QP_USERNAME, "XXX");
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password1).queryParam(Constants.QP_USERNAME,
					"XXX");
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testGetMessageFromChatUserEncodeFailurePassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password1).queryParam(Constants.QP_USERNAME, "XXX");
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password1).queryParam(Constants.QP_USERNAME,
					"XXX");
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testGetMessageFromChatUserPasswordChatID() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password1).queryParam(Constants.QP_USERNAME, username1)
					.queryParam(Constants.QP_CHATID, cid1to2);
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password1)
					.queryParam(Constants.QP_USERNAME, username1).queryParam(Constants.QP_CHATID, cid1to2);
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertNotNull(out.getM());
	}

	@Test
	public void testGetMessageFromChatUserPasswordTimestamp() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password1).queryParam(Constants.QP_USERNAME, username1)
					.queryParam(Constants.QP_TIMESTAMP, 0);
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password1)
					.queryParam(Constants.QP_USERNAME, username1).queryParam(Constants.QP_TIMESTAMP, 0);
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testGetMessageFromChatUserPasswordChatIDTimestampToBig() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password2).queryParam(Constants.QP_USERNAME, username2)
					.queryParam(Constants.QP_TIMESTAMP, 200).queryParam(Constants.QP_CHATID, cid1to2);
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password2)
					.queryParam(Constants.QP_USERNAME, username2).queryParam(Constants.QP_TIMESTAMP, 200)
					.queryParam(Constants.QP_CHATID, cid1to2);
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertEquals(3, out.getM().size());
	}

	@Test
	public void testGetMessageFromChatUserPasswordChatIDTimestampNewMessages() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password1).queryParam(Constants.QP_USERNAME, username1)
					.queryParam(Constants.QP_TIMESTAMP, 0).queryParam(Constants.QP_CHATID, cid3to12);
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password1)
					.queryParam(Constants.QP_USERNAME, username1).queryParam(Constants.QP_TIMESTAMP, 0)
					.queryParam(Constants.QP_CHATID, cid3to12);
		}
		LOGGER.debug(target);
		OFMFC out = target.request().get(OFMFC.class);
		Assert.assertNotNull(out.getM().size());
		Assert.assertEquals(3, out.getM().size());
	}
}
