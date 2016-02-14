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
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
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

import de.radiohacks.frinmeba.modelshort.IAckCD;
import de.radiohacks.frinmeba.modelshort.OAckCD;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestAcknowledgeChatDownload extends JerseyTest {

	/*
	 * @POST
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Consumes(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/acknowledgechatdownload") public OAckCD
	 * acknowledgeChatDownload(IAckCD in);
	 */

	private static final Logger LOGGER = Logger.getLogger(TestAcknowledgeChatDownload.class.getName());

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

	final static String functionurl = "user/acknowledgechatdownload";

	final static String chatname_org = "Test Nachnricht fuer Acknowledge";
	final static String chatname = Base64
			.encodeBase64String(chatname_org.getBytes(Charset.forName(Constants.CHARACTERSET)));

	static int cid;

	@Override
	protected TestContainerFactory getTestContainerFactory() {
		return new GrizzlyWebTestContainerFactory();
	}

	@Override
	protected DeploymentContext configureDeployment() {
		return ServletDeploymentContext.forServlet(new ServletContainer(new ResourceConfig(ServiceImpl.class))).build();
	}

	@Override
	protected void configureClient(ClientConfig config) {
		config.register(MultiPartFeature.class);
	}

	@BeforeClass
	public static void prepareDB() {
		dropDatabaseTables drop = new dropDatabaseTables();
		drop.dropTable();
		createDatabaseTables create = new createDatabaseTables();
		create.createTable();
		helperDatabase help = new helperDatabase();
		help.CreateActiveUser(username_org, username, password_org, email_org, help.InsertFixedImage());
		cid = help.CreateChat(username_org, chatname_org);
		help.AddUserToChat(help.getUserID(username_org), cid);

	}

	private OAckCD callTarget(IAckCD in) {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(TestConfig.URL + functionurl);
		} else {
			target = target(functionurl);
		}
		LOGGER.debug(target);
		Response response = target.request().buildPost(Entity.entity(in, MediaType.APPLICATION_XML)).invoke();
		LOGGER.debug(response);
		return response.readEntity(OAckCD.class);
	}

	@Test
	public void testAcknowledgeChatDownloadUpNoValues() {
		IAckCD in = new IAckCD();
		OAckCD out = callTarget(in);

		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAcknowledgeChatDownloadUser() {
		IAckCD in = new IAckCD();
		in.setUN(username);
		OAckCD out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAcknowledgeChatDownloadPassword() {
		IAckCD in = new IAckCD();
		in.setPW(password);
		OAckCD out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAcknowledgeChatDownloadUserPassword() {
		IAckCD in = new IAckCD();
		in.setUN(username);
		in.setPW(password);
		OAckCD out = callTarget(in);
		Assert.assertEquals(Constants.NO_CONTENT_GIVEN, out.getET());
	}

	@Test
	public void testAcknowledgeChatDownloadUserPasswordNoAcknowledge() {
		IAckCD in = new IAckCD();
		in.setUN(username);
		in.setPW(password);
		in.setCID(cid);
		OAckCD out = callTarget(in);
		Assert.assertEquals(Constants.NO_CONTENT_GIVEN, out.getET());
	}

	@Test
	public void testAcknowledgeChatDownloadUserWrongPassword() {
		int hashCode = chatname_org.hashCode();
		String sha1b64 = new String(Base64.encodeBase64(String.valueOf(hashCode).getBytes()),
				Charset.forName(Constants.CHARACTERSET));

		IAckCD in = new IAckCD();
		in.setPW(Base64.encodeBase64String("XXX".getBytes(Charset.forName(Constants.CHARACTERSET))));
		in.setUN(username);
		in.setACK(sha1b64);
		OAckCD out = callTarget(in);
		Assert.assertEquals(Constants.WRONG_PASSWORD, out.getET());
	}

	@Test
	public void testAcknowledgeChatDownloadUserEncodeFailureUser() {
		IAckCD in = new IAckCD();
		in.setUN("XXX");
		in.setPW(password);
		OAckCD out = callTarget(in);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testAcknowledgeChatDownloadUserEncodeFailurePassword() {
		IAckCD in = new IAckCD();
		in.setUN(username);
		in.setPW("XXX");
		OAckCD out = callTarget(in);
		Assert.assertEquals(Constants.ENCODING_ERROR, out.getET());
	}

	@Test
	public void testAcknowledgeChatDownloadUserPasswordAcknowledgeChat() {

		int hashCode = chatname_org.hashCode();
		String sha1b64 = new String(Base64.encodeBase64(String.valueOf(hashCode).getBytes()),
				Charset.forName(Constants.CHARACTERSET));

		IAckCD in = new IAckCD();
		in.setUN(username);
		in.setPW(password);
		in.setCID(cid);
		in.setACK(sha1b64);
		OAckCD out = callTarget(in);
		Assert.assertEquals(Constants.ACKNOWLEDGE_TRUE, out.getACK());
	}

	@Test
	public void testAcknowledgeChatDownloadUserPasswordAcknowledge() {

		int hashCode = chatname_org.hashCode();
		String sha1b64 = new String(Base64.encodeBase64(String.valueOf(hashCode).getBytes()),
				Charset.forName(Constants.CHARACTERSET));

		IAckCD in = new IAckCD();
		in.setUN(username);
		in.setPW(password);
		in.setACK(sha1b64);
		OAckCD out = callTarget(in);
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}
}
