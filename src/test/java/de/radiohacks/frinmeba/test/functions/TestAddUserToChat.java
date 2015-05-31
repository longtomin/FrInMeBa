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

import de.radiohacks.frinmeba.modelshort.OAdUC;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestAddUserToChat extends JerseyTest {

	/*
	 * @GET
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/addusertochat") public OutAddUserToChat
	 * AddUserToChat(@QueryParam(Constants.QPusername) String User,
	 * 
	 * @QueryParam(Constants.QPpassword) String Password,
	 * 
	 * @QueryParam(Constants.QPuserid) int UserID,
	 * 
	 * @QueryParam(Constants.QPchatid) int ChatID);
	 */

	// Username welche anzulegen ist
	final static String username_org = "Test1";
	final static String username = Base64.encodeBase64String(username_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	// Passwort zum User
	final static String password_org = "Test1";
	final static String password = Base64.encodeBase64String(password_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	// Email Adresse zum User
	final static String email_org = "Test1@frinme.org";
	final static String email = Base64.encodeBase64String(email_org
			.getBytes(Charset.forName(Constants.CharacterSet)));

	final static String functionurl = "user/addusertochat";

	// Username welche anzulegen ist
	final static String username2_org = "Test2";
	final static String username2 = Base64.encodeBase64String(username2_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	// Passwort zum User
	final static String password2_org = "Test2";
	final static String password2 = Base64.encodeBase64String(password2_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	// Email Adresse zum User
	final static String email2_org = "Test2@frinme.org";
	final static String email2 = Base64.encodeBase64String(email2_org
			.getBytes(Charset.forName(Constants.CharacterSet)));

	// Chatname
	final static String chatname_org = "Chat1";
	final static String chatname = Base64.encodeBase64String(chatname_org
			.getBytes(Charset.forName(Constants.CharacterSet)));

	static int chatid;
	private int userid;

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
		System.out.print("Start prepareDB\n");
		dropDatabaseTables drop = new dropDatabaseTables();
		drop.dropTable();
		createDatabaseTables create = new createDatabaseTables();
		create.createTable();
		helperDatabase help = new helperDatabase();
		help.CreateActiveUser(username_org, username, password_org, email_org);
		help.CreateActiveUser(username2_org, username2, password2_org,
				email2_org);
		chatid = help.CreateChat(username_org, chatname_org);

	}

	@Test
	public void testAddUserToChatUpNoValues() {
		System.out.print("Start testAddUserToChatUpNoValues\n");
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(
					TestConfig.URL + functionurl);
		} else {
			target = target(functionurl);
		}
		OAdUC out = target.request().get(OAdUC.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordUserIDChatID() {
		System.out.print("Start testAddUserToChatUserPasswordUserIDChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.USER_ADDED, out.getR());
	}

	@Test
	public void testAddUserToChatUserPasswordUserID() {
		System.out.print("Start testAddUserToChatUserPasswordUserID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordChatID() {
		System.out.print("Start testAddUserToChatUserPasswordChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
	}

	@Test
	public void testAddUserToChatUserUserIDChatID() {
		System.out.print("Start testAddUserToChatUserUserIDChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatPasswordUserIDChatID() {
		System.out.print("Start testAddUserToChatPasswordUserIDChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserPassword() {
		System.out.print("Start testAddUserToChatUserPassword\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username);

		} else {
			target = target(functionurl).queryParam(Constants.QPpassword,
					password).queryParam(Constants.QPusername, username);

		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatUserUserID() {
		System.out.print("Start testAddUserToChatUserUserID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl).queryParam(Constants.QPusername,
					username).queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserChatID() {
		System.out.print("Start testAddUserToChatUserChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid);
		} else {
			target = target(functionurl).queryParam(Constants.QPusername,
					username).queryParam(Constants.QPchatid, chatid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatPasswordUserID() {
		System.out.print("Start testAddUserToChatPasswordUserID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl).queryParam(Constants.QPpassword,
					password).queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatPasswordChatID() {
		System.out.print("Start testAddUserToChatPasswordChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatid, chatid);
		} else {
			target = target(functionurl).queryParam(Constants.QPpassword,
					password).queryParam(Constants.QPchatid, chatid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserIDChatID() {
		System.out.print("Start testAddUserToChatUserIDChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl).queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUser() {
		System.out.print("Start testAddUserToChatUser\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target(functionurl).queryParam(Constants.QPusername,
					username);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatPassword() {
		System.out.print("Start testAddUserToChatPassword\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password);
		} else {
			target = target(functionurl).queryParam(Constants.QPpassword,
					password);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserID() {
		System.out.print("Start testAddUserToChatUserID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl).queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatChatID() {
		System.out.print("Start testAddUserToChatChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPchatid, chatid);
		} else {
			target = target(functionurl).queryParam(Constants.QPchatid, chatid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatWrongUserPasswordUserIDChatID() {
		System.out
				.print("Start testAddUserToChatWrongUserPasswordUserIDChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(
							Constants.QPusername,
							Base64.encodeBase64String("blah".getBytes(Charset
									.forName(Constants.CharacterSet))))
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(
							Constants.QPusername,
							Base64.encodeBase64String("blah".getBytes(Charset
									.forName(Constants.CharacterSet))))
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
	}

	@Test
	public void testAddUserToChatUserWrongPasswordUserIDChatID() {
		System.out
				.print("Start testAddUserToChatUserWrongPasswordUserIDChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(
							Constants.QPpassword,
							Base64.encodeBase64String("blah".getBytes(Charset
									.forName(Constants.CharacterSet))))
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl)
					.queryParam(
							Constants.QPpassword,
							Base64.encodeBase64String("blah".getBytes(Charset
									.forName(Constants.CharacterSet))))
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.WRONG_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordWrongUserIDChatID() {
		System.out
				.print("Start testAddUserToChatUserPasswordWrongUserIDChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, 47);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, 47);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordUserIDWrongChatID() {
		System.out
				.print("Start testAddUserToChatUserPasswordUserIDWrongChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, 47)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, 47)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordUserIDChatID_Again() {
		System.out
				.print("Start testAddUserToChatUserPasswordUserIDChatID_Again\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		help.AddUserToChat(userid, chatid);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.USER_ALREADY_IN_CHAT, out.getET());

	}

	@Test
	public void testAddUserToChatUserPasswordUserIDChatID_foreignChat() {
		System.out
				.print("Start testAddUserToChatUserPasswordUserIDChatID_foreignChat\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		int chatid2 = help.CreateChat(username2_org, "Chat2");
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid2)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid2)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.NOT_CHAT_OWNER, out.getET());

	}

	@Test
	public void testAddUserToChatUserPasswordUserIDChatID_selfAdd() {
		System.out
				.print("Start testAddUserToChatUserPasswordUserIDChatID_selfAdd\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username_org);
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		}
		OAdUC out = target.request().get(OAdUC.class);
		Assert.assertEquals(Constants.CHAT_OWNER_NOT_ADDED, out.getET());

	}
}