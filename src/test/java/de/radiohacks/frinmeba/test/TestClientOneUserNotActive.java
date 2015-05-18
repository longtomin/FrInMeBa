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

import de.radiohacks.frinmeba.model.OutAddUserToChat;
import de.radiohacks.frinmeba.model.OutAuthenticate;
import de.radiohacks.frinmeba.model.OutCheckNewMessages;
import de.radiohacks.frinmeba.model.OutCreateChat;
import de.radiohacks.frinmeba.model.OutDeleteChat;
import de.radiohacks.frinmeba.model.OutDeleteMessageFromChat;
import de.radiohacks.frinmeba.model.OutFetchMessageFromChat;
import de.radiohacks.frinmeba.model.OutFetchTextMessage;
import de.radiohacks.frinmeba.model.OutInsertMessageIntoChat;
import de.radiohacks.frinmeba.model.OutListChat;
import de.radiohacks.frinmeba.model.OutListUser;
import de.radiohacks.frinmeba.model.OutRemoveUserFromChat;
import de.radiohacks.frinmeba.model.OutSendTextMessage;
import de.radiohacks.frinmeba.model.OutSignUp;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;

public class TestClientOneUserNotActive extends JerseyTest {

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
		OutSignUp out1 = TestSignUpNoValues();
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD,
				out1.getErrortext());
		OutSignUp out2 = TestSignUpWithEmail();
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD,
				out2.getErrortext());
		OutSignUp out3 = TestSignUpWithEmailPassword();
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD,
				out3.getErrortext());
		OutSignUp out4 = TestSignUpWithEmailUser();
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD,
				out4.getErrortext());
		OutSignUp out5 = TestSignUpWithEmailUserPassword();
		Assert.assertEquals("SUCCESSFUL", out5.getSignUp());
		OutSignUp out5a = TestSignUpWithEmailUserPassword();
		Assert.assertEquals(Constants.USER_ALREADY_EXISTS, out5a.getErrortext());
		OutAuthenticate out6 = TestAuthenticateNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out6.getErrortext());
		OutCreateChat out7 = TestCreateChatNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out7.getErrortext());
		OutDeleteChat out8 = TestDeleteChatNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out8.getErrortext());
		OutAddUserToChat out9 = TestAddUserToChatNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out9.getErrortext());
		OutRemoveUserFromChat out10 = TestRemoveUserFromChatNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out10.getErrortext());
		OutListUser out11 = TestListUserNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out11.getErrortext());
		OutListChat out12 = TestListChatNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out12.getErrortext());
		OutSendTextMessage out13 = TestSendTextMessageNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out13.getErrortext());
		OutFetchTextMessage out14 = TestGetTextMessageNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out14.getErrortext());
		OutInsertMessageIntoChat out15 = TestInsertMessageIntoChatNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out15.getErrortext());
		OutFetchMessageFromChat out16 = TestGetMessageFromChatNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out16.getErrortext());
		OutCheckNewMessages out17 = TestCheckNewMessagesNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out17.getErrortext());
		OutDeleteMessageFromChat out18 = TestDeleMessageFromChatNotActive();
		Assert.assertEquals(Constants.USER_NOT_ACTIVE, out18.getErrortext());
	}

	// Test des SignUp ohne Werte = Constants.NO_USERNAME_OR_PASSWORD
	private OutSignUp TestSignUpNoValues() {

		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(
					TestConfig.URL + "user/signup");
		} else {
			target = target("user/signup");
		}
		return target.request().get(OutSignUp.class);
	}

	public OutSignUp TestSignUpWithEmail() {

		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/signup")
					.queryParam(Constants.QPemail, email);
		} else {
			target = target("user/signup").queryParam(Constants.QPemail, email);
		}
		return target.request().get(OutSignUp.class);
	}

	// Test des SignUp ohne Werte = Constants.NO_USERNAME_OR_PASSWORD
	public OutSignUp TestSignUpWithEmailUser() {

		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/signup")
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, username);
			;
		} else {
			target = target("user/signup").queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, username);
			;
		}
		return target.request().get(OutSignUp.class);
	}

	// Test des SignUp ohne Werte = Constants.NO_USERNAME_OR_PASSWORD
	public OutSignUp TestSignUpWithEmailPassword() {

		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/signup")
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPpassword, password);
		} else {
			target = target("user/signup").queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPpassword, password);

		}
		return target.request().get(OutSignUp.class);
	}

	// Test des SignUp ohne Werte = Constants.NO_USERNAME_OR_PASSWORD
	public OutSignUp TestSignUpWithEmailUserPassword() {

		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/signup")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target("user/signup")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, username);
		}
		return target.request().get(OutSignUp.class);
	}

	public OutAuthenticate TestAuthenticateNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/authenticate")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target("user/authenticate").queryParam(
					Constants.QPpassword, password).queryParam(
					Constants.QPusername, username);
		}
		return target.request().get(OutAuthenticate.class);
	}

	public OutCreateChat TestCreateChatNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + "user/createchat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("TestXXX"
									.getBytes(Charset
											.forName(Constants.CharacterSet))))
					.queryParam(Constants.QPusername, username);
		} else {
			target = target("user/createchat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("TestXXX"
									.getBytes(Charset
											.forName(Constants.CharacterSet))))
					.queryParam(Constants.QPusername, username);
		}
		return target.request().get(OutCreateChat.class);
	}

	public OutDeleteChat TestDeleteChatNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/deletechat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatid, 1)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target("user/deletechat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatid, 1)
					.queryParam(Constants.QPusername, username);
		}
		return target.request().delete(OutDeleteChat.class);
	}

	public OutAddUserToChat TestAddUserToChatNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/addusertochat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPuserid, 1)
					.queryParam(Constants.QPchatid, 1)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target("user/addusertochat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPuserid, 1)
					.queryParam(Constants.QPchatid, 1)
					.queryParam(Constants.QPusername, username);
		}
		return target.request().get(OutAddUserToChat.class);
	}

	public OutRemoveUserFromChat TestRemoveUserFromChatNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/removeuserfromchat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPuserid, 1)
					.queryParam(Constants.QPchatid, 1)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target("user/removeuserfromchat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPuserid, 1)
					.queryParam(Constants.QPchatid, 1)
					.queryParam(Constants.QPusername, username);
		}
		return target.request().delete(OutRemoveUserFromChat.class);
	}

	public OutListUser TestListUserNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + "user/listuser")
					.queryParam(Constants.QPpassword, password)
					.queryParam(
							Constants.QPsearch,
							Base64.encodeBase64String("Test".getBytes(Charset
									.forName(Constants.CharacterSet))))
					.queryParam(Constants.QPusername, username);
		} else {
			target = target("user/listuser")
					.queryParam(Constants.QPpassword, password)
					.queryParam(
							Constants.QPsearch,
							Base64.encodeBase64String("Test".getBytes(Charset
									.forName(Constants.CharacterSet))))
					.queryParam(Constants.QPusername, username);
		}
		return target.request().get(OutListUser.class);
	}

	public OutListChat TestListChatNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/listchat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target("user/listchat").queryParam(Constants.QPpassword,
					password).queryParam(Constants.QPusername, username);
		}
		return target.request().get(OutListChat.class);
	}

	public OutSendTextMessage TestSendTextMessageNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + "user/sendtextmessage")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(
							Constants.QPtextmessage,
							Base64.encodeBase64String("Text Message"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		} else {
			target = target("user/sendtextmessage")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(
							Constants.QPtextmessage,
							Base64.encodeBase64String("Text Message"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		}
		return target.request().get(OutSendTextMessage.class);
	}

	public OutFetchTextMessage TestGetTextMessageNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/gettextmessage")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPtextmessageid, 1);
		} else {
			target = target("user/gettextmessage")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPtextmessageid, 1);
		}
		return target.request().get(OutFetchTextMessage.class);
	}

	public OutInsertMessageIntoChat TestInsertMessageIntoChatNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + "user/insertmessageintochat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPtextmessageid, 1)
					.queryParam(Constants.QPchatid, 1)
					.queryParam(
							Constants.QPmessagetype,
							Base64.encodeBase64String(Constants.TYP_TEXT
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		} else {
			target = target("user/insertmessageintochat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPtextmessageid, 1)
					.queryParam(Constants.QPchatid, 1)
					.queryParam(
							Constants.QPmessagetype,
							Base64.encodeBase64String(Constants.TYP_TEXT
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		}
		return target.request().get(OutInsertMessageIntoChat.class);
	}

	public OutFetchMessageFromChat TestGetMessageFromChatNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/getmessagefromchat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, 1)
					.queryParam(Constants.QPtimestamp, 0);
		} else {
			target = target("user/getmessagefromchat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatid, 1)
					.queryParam(Constants.QPtimestamp, 0);
		}
		return target.request().get(OutFetchMessageFromChat.class);
	}

	public OutCheckNewMessages TestCheckNewMessagesNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/checknewmessages")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target("user/checknewmessages").queryParam(
					Constants.QPpassword, password).queryParam(
					Constants.QPusername, username);
		}
		return target.request().get(OutCheckNewMessages.class);
	}

	public OutDeleteMessageFromChat TestDeleMessageFromChatNotActive() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/deletemessagefromchat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPmessageid, 1);
		} else {
			target = target("user/deletemessagefromchat")
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPmessageid, 1);
		}
		return target.request().delete(OutDeleteMessageFromChat.class);
	}
}