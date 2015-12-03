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
import org.junit.Test;

import de.radiohacks.frinmeba.modelshort.ISTeM;
import de.radiohacks.frinmeba.modelshort.OSTeM;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestSendTextMessage extends JerseyTest {

	/*
	 * @PUT
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Consumes(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/sendtextmessage") public OSTeM sendTextMessage(ISTeM in);
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

	final static String functionurl = "user/sendtextmessage";

	// Text Message
	final static String textmessage_org = "Das+ist+ein+Test!";
	final static String textmessage = Base64.encodeBase64String(textmessage_org
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
		helperDatabase help = new helperDatabase();
		help.CreateActiveUser(username_org, username, password_org, email_org);
	}

	private OSTeM callTarget(ISTeM in) {
		WebTarget target = ClientBuilder.newClient().target(
				TestConfig.URL + functionurl);
		Response response = target.request()
				.buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
				.invoke();
		return response.readEntity(OSTeM.class);
	}

	@Test
	public void testSendTextMessageUpNoValues() {
		ISTeM in = new ISTeM();
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testSendTextMessagePasswordMessage() {
		ISTeM in = new ISTeM();
		in.setPW(password);
		in.setTM(textmessage);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testSendTextMessageUserMessage() {
		ISTeM in = new ISTeM();
		in.setUN(username);
		in.setTM(textmessage);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testSendTextMessageUserPassword() {
		ISTeM in = new ISTeM();
		in.setPW(password);
		in.setUN(username);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.NO_TEXTMESSAGE_GIVEN, out.getET());
	}

	@Test
	public void testSendTextMessageMessage() {
		ISTeM in = new ISTeM();
		in.setTM(textmessage);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testSendTextMessageUserPasswordMessage() {
		ISTeM in = new ISTeM();
		in.setPW(password);
		in.setUN(username);
		in.setTM(textmessage);
		OSTeM out = callTarget(in);
		Assert.assertNotNull(out.getTID());
	}

	@Test
	public void testSendTextMessageUserWrongPasswordMessage() {
		ISTeM in = new ISTeM();
		in.setPW(Base64.encodeBase64String("ZZZ".getBytes(Charset
				.forName(Constants.CharacterSet))));
		in.setUN(username);
		in.setTM(textmessage);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.WRONG_PASSWORD, out.getET());
	}

	@Test
	public void testSendTextMessageWrongUserPasswordMessage() {
		ISTeM in = new ISTeM();
		in.setPW(password);
		in.setUN(Base64.encodeBase64String("ZZZ".getBytes(Charset
				.forName(Constants.CharacterSet))));
		in.setTM(textmessage);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
	}

	@Test
	public void testSendTextMessageWrongUserWrongPasswordMessage() {
		ISTeM in = new ISTeM();
		in.setPW(Base64.encodeBase64String("XXX".getBytes(Charset
				.forName(Constants.CharacterSet))));
		in.setUN(Base64.encodeBase64String("ZZZ".getBytes(Charset
				.forName(Constants.CharacterSet))));
		in.setTM(textmessage);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
	}

	@Test
	public void testSendTextMessageUser() {
		ISTeM in = new ISTeM();
		in.setUN(username);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testSendTextMessagePassword() {
		ISTeM in = new ISTeM();
		in.setPW(password);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testSendTextMessageEncodimgErrorUser() {
		ISTeM in = new ISTeM();
		in.setPW(password);
		in.setUN("$%&1234");
		in.setTM(textmessage);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testSendTextMessageEncodimgErrorPassword() {
		ISTeM in = new ISTeM();
		in.setPW("$%&1234");
		in.setUN(username);
		in.setTM(textmessage);
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testSendTextMessageEncodimgErrorTextMessage() {
		ISTeM in = new ISTeM();
		in.setPW(password);
		in.setUN(username);
		in.setTM("$%&1234");
		OSTeM out = callTarget(in);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}
}