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

import de.radiohacks.frinmeba.modelshort.OGTeM;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestGetTextMessage extends JerseyTest {

	/*
	 * @GET
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/gettextmessage") public OGTeM
	 * getTextMessage(@QueryParam(Constants.QPusername) String User,
	 * 
	 * @QueryParam(Constants.QPpassword) String Password,
	 * 
	 * @QueryParam(Constants.QPtextmessageid) int TextMessageID);
	 */

	private static final Logger LOGGER = Logger.getLogger(TestGetTextMessage.class.getName());

	// Username welche anzulegen ist
	final static String username_org = "Test1";
	final static String username = Base64
			.encodeBase64String(username_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	// Passwort zum User
	final static String password_org = "Test1";
	final static String password = Base64
			.encodeBase64String(password_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	// Email Adresse zum User
	final static String email_org = "Test1@frinme.org";
	final static String email = Base64.encodeBase64String(email_org.getBytes(Charset.forName(Constants.CHARACTERSET)));

	final static String functionurl = "user/gettextmessage";

	static int msg1;
	final static String textmnsg1_org = "Test1 Nachricht ;-) 'o)";
	final static String textmnsg1 = Base64
			.encodeBase64String(textmnsg1_org.getBytes(Charset.forName(Constants.CHARACTERSET)));
	static int msg2;
	final static String textmnsg2_org = "Nachricht2 von Test1 ä ö ü Ä Ö Ü";
	final static String textmnsg2 = Base64
			.encodeBase64String(textmnsg2_org.getBytes(Charset.forName(Constants.CHARACTERSET)));

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
		LOGGER.debug("Start BeforeClass");
		dropDatabaseTables drop = new dropDatabaseTables();
		drop.dropTable();
		createDatabaseTables create = new createDatabaseTables();
		create.createTable();
		helperDatabase help = new helperDatabase();
		help.CreateActiveUser(username_org, username, password_org, email_org, help.InsertFixedImage());
		msg1 = help.CreateContentMessage(textmnsg1, Constants.TYP_TEXT);
		msg2 = help.CreateContentMessage(textmnsg2, Constants.TYP_TEXT);
		LOGGER.debug("End BeforeClass");
	}

	@Test
	public void testGetTextMessageUpNoValues() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl);
		} else {
			target = target(functionurl);
		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetTextMessageUser() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl).queryParam(Constants.QP_USERNAME,
					username);

		} else {
			target = target(functionurl).queryParam(Constants.QP_USERNAME, username);

		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetTextMessagePassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl).queryParam(Constants.QP_PASSWORD,
					password);

		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password);

		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetTextMessageUserTextmessage() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_TEXTMESSAGEID, msg1).queryParam(Constants.QP_USERNAME, username);

		} else {
			target = target(functionurl).queryParam(Constants.QP_TEXTMESSAGEID, msg1).queryParam(Constants.QP_USERNAME,
					username);

		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetTextMessagePasswordTextmessage() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password).queryParam(Constants.QP_TEXTMESSAGEID, msg1);

		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_TEXTMESSAGEID, msg1);

		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testGetTextMessageUserPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password).queryParam(Constants.QP_USERNAME, username);

		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password).queryParam(Constants.QP_USERNAME,
					username);

		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(Constants.NONE_EXISTING_CONTENT_MESSAGE, out.getET());
	}

	@Test
	public void testGetTextMessageUserWrongPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD,
							Base64.encodeBase64String("XXX".getBytes(Charset.forName(Constants.CHARACTERSET))))
					.queryParam(Constants.QP_USERNAME, username);

		} else {
			target = target(functionurl)
					.queryParam(Constants.QP_PASSWORD,
							Base64.encodeBase64String("XXX".getBytes(Charset.forName(Constants.CHARACTERSET))))
					.queryParam(Constants.QP_USERNAME, username);

		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(Constants.WRONG_PASSWORD, out.getET());
	}

	@Test
	public void testGetTextMessageUserEncodeFailureUser() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password).queryParam(Constants.QP_USERNAME, "�$%1234");

		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password).queryParam(Constants.QP_USERNAME,
					"�$%1234");

		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testGetTextMessageUserEncodeFailurePassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, "�$%1234").queryParam(Constants.QP_USERNAME, username);

		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, "�$%1234").queryParam(Constants.QP_USERNAME,
					username);

		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testGetTextMessageUserPasswordTextmessage1() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password).queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_TEXTMESSAGEID, msg1);

		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_USERNAME, username).queryParam(Constants.QP_TEXTMESSAGEID, msg1);

		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(textmnsg1_org, out.getTM());
	}

	@Test
	public void testGetTextMessageUserPasswordTextmessage2() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password).queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_TEXTMESSAGEID, msg2);
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_USERNAME, username).queryParam(Constants.QP_TEXTMESSAGEID, msg2);
		}
		LOGGER.debug(target);
		OGTeM out = target.request().get(OGTeM.class);

		Assert.assertEquals(textmnsg2_org, out.getTM());
	}
}