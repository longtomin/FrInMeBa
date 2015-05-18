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

import de.radiohacks.frinmeba.model.OutSignUp;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;

public class TestSignUp extends JerseyTest {

	// Username welche anzulegen ist
	final static String functionurl = "user/signup";

	final static String username_org = "Thomas Schreiner";
	final static String username = Base64.encodeBase64String(username_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String password_org = "d38681074467c0bc147b17a9a12b9efa8cc10bcf545f5b0bccccf5a93c4a2b79";
	final static String password = Base64.encodeBase64String(password_org
			.getBytes(Charset.forName(Constants.CharacterSet)));
	final static String email_org = "thomas@frinme.org";
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

	@Test
	public void testSignUpNoValues() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(
					TestConfig.URL + functionurl);
		} else {
			target = target(functionurl);
		}
		OutSignUp out = target.request().get(OutSignUp.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD,
				out.getErrortext());
	}

	@Test
	public void testSignUpEmail() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPemail, email);
		} else {
			target = target(functionurl).queryParam(Constants.QPemail, email);
		}
		OutSignUp out = target.request().get(OutSignUp.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD,
				out.getErrortext());
	}

	@Test
	public void testSignUpUsername() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target(functionurl).queryParam(Constants.QPusername,
					username);
		}
		OutSignUp out = target.request().get(OutSignUp.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD,
				out.getErrortext());
	}

	@Test
	public void testSignUpPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password);
		} else {
			target = target(functionurl).queryParam(Constants.QPpassword,
					password);
		}
		OutSignUp out = target.request().get(OutSignUp.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD,
				out.getErrortext());
	}

	@Test
	public void testSignUpEmailUsername() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target(functionurl).queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, username);
		}
		OutSignUp out = target.request().get(OutSignUp.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD,
				out.getErrortext());
	}

	@Test
	public void testSignUpEmailPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, email);
		} else {
			target = target(functionurl).queryParam(Constants.QPpassword,
					password).queryParam(Constants.QPemail, email);
		}
		OutSignUp out = target.request().get(OutSignUp.class);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD,
				out.getErrortext());
	}

	@Test
	public void testSignUpUserPassword() {
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
		OutSignUp out = target.request().get(OutSignUp.class);
		Assert.assertEquals(Constants.INVALID_EMAIL_ADRESS, out.getErrortext());
	}

	@Test
	public void testSignUpEmailUserPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, username);
		}
		OutSignUp out = target.request().get(OutSignUp.class);
		Assert.assertEquals("SUCCESSFUL", out.getSignUp());
	}

	@Test
	public void testSignUpEncodingErrorEmail() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, "$%&")
					.queryParam(Constants.QPusername, username);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, "$%&")
					.queryParam(Constants.QPusername, username);
		}
		OutSignUp out = target.request().get(OutSignUp.class);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getErrortext());
	}

	@Test
	public void testSignUpEncodingErrorUser() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, "$%&1234");
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, "$%&1234");
		}
		OutSignUp out = target.request().get(OutSignUp.class);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getErrortext());
	}

	@Test
	public void testSignUpEncodingErrorPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QPpassword, "$%&XASD")
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, username);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QPpassword, "$%&XASD")
					.queryParam(Constants.QPemail, email)
					.queryParam(Constants.QPusername, username);
		}
		OutSignUp out = target.request().get(OutSignUp.class);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getErrortext());
	}
}