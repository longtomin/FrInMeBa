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

import de.radiohacks.frinmeba.modelshort.OCrCh;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestCreateChat extends JerseyTest {

	/*
	 * @GET
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/createchat") public OutCreateChat
	 * CreateChat(@QueryParam(Constants.QPusername) String User,
	 * 
	 * @QueryParam(Constants.QPpassword) String Password,
	 * 
	 * @QueryParam(Constants.QPchatname) String Chatname);
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

	final static String functionurl = "user/createchat";

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
		help.CreateActiveUser(username_org, username, password_org, email_org);
	}

	@Test
	public void testCreateChatUpNoValues() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(
					TestConfig.URL + functionurl);
		} else {
			target = target(functionurl);
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testCreateChatUserPasswordChatname() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("Testchat"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("Testchat"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals("Testchat", out.getCN());
	}

	@Test
	public void testCreateChatUserWrongPasswordChatname() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(
							Constants.QPpassword,
							Base64.encodeBase64String("XXX".getBytes(Charset
									.forName(Constants.CharacterSet))))
					.queryParam(Constants.QPusername, username)
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("Testchat"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		} else {
			target = target(functionurl)
					.queryParam(
							Constants.QPpassword,
							Base64.encodeBase64String("XXX".getBytes(Charset
									.forName(Constants.CharacterSet))))
					.queryParam(Constants.QPusername, username)
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("Testchat"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.WRONG_PASSWORD, out.getET());
	}

	@Test
	public void testCreateChatUser() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target(functionurl).queryParam(Constants.QPusername,
					username);
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testCreateChatPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password);
		} else {
			target = target(functionurl).queryParam(Constants.QPpassword,
					password);
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testCreateChatChatname() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPchatname, "Testchat");
		} else {
			target = target(functionurl).queryParam(Constants.QPchatname,
					"Testchat");
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testCreateChatUserPassword() {
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
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.MISSING_CHATNAME, out.getET());
	}

	@Test
	public void testCreateChatUserChatname() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPusername, username)
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("Testchat"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		} else {
			target = target(functionurl).queryParam(Constants.QPusername,
					username).queryParam(
					Constants.QPchatname,
					Base64.encodeBase64String("Testchat".getBytes(Charset
							.forName(Constants.CharacterSet))));
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testCreateChatPasswordChatname() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("Testchat"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		} else {
			target = target(functionurl).queryParam(Constants.QPpassword,
					password).queryParam(
					Constants.QPchatname,
					Base64.encodeBase64String("Testchat".getBytes(Charset
							.forName(Constants.CharacterSet))));
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testCreateChatEncodingErrorUser() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, "$%&1233")
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("Testchat"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, "$%&1233")
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("Testchat"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testCreateChatEncodingErrorPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, "$%&1233")
					.queryParam(Constants.QPusername, username)
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("Testchat"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, "$%&1233")
					.queryParam(Constants.QPusername, username)
					.queryParam(
							Constants.QPchatname,
							Base64.encodeBase64String("Testchat"
									.getBytes(Charset
											.forName(Constants.CharacterSet))));
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testCreateChatEncodingErrorChatname() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatname, "$%&1233");
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPchatname, "$%&1233");
		}
		OCrCh out = target.request().get(OCrCh.class);

		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}
}
