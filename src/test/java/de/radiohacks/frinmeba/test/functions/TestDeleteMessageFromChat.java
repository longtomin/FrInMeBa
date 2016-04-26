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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
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

import de.radiohacks.frinmeba.model.jaxb.ODMFC;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestDeleteMessageFromChat extends JerseyTest {

	/*
	 * @DELETE
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/deletemessagefromchat") public ODMFC
	 * deleteMessageFromChat(@Context HttpHeaders headers,
	 * 
	 * @QueryParam(Constants.QP_MESSAGEID) int messageID);
	 */

	private static final Logger LOGGER = Logger
			.getLogger(TestDeleteMessageFromChat.class.getName());

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
	final static String username3_org = "Test3";
	final static String username3 = Base64.encodeBase64String(username3_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String password3_org = "Test3";
	final static String password3 = Base64.encodeBase64String(password3_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String email3_org = "Test3@frinme.org";
	final static String email3 = Base64.encodeBase64String(email3_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

	static int content_msg1;
	final static String textmnsg1_org = "Test1 Nachricht ;-) 'o)";
	final static String textmnsg1 = Base64.encodeBase64String(textmnsg1_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	static int content_msg2;
	final static String textmnsg2_org = "Nachricht2 von Test1 ä ö ü Ä Ö Ü";
	final static String textmnsg2 = Base64.encodeBase64String(textmnsg2_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

	static int message_txt1;
	static int message_txt2;
	static int message_txt2a;
	static int message_img1;
	static int message_img2;
	static int message_img2a;
	static int message_vid1;
	static int message_vid2;
	static int message_vid2a;

	final static String functionurl = "user/deletemessagefromchat";

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
		LOGGER.debug("Start prepareDB");
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

		content_msg1 = help.CreateContentMessage(textmnsg1, Constants.TYP_TEXT);
		content_msg2 = help.CreateContentMessage(textmnsg2, Constants.TYP_TEXT);

		int cid12 = help.CreateChat(username1_org, "Test1 Chat");
		int u2c1 = help.AddUserToChat(help.getUserID(username1_org), cid12);
		int u2c2 = help.AddUserToChat(help.getUserID(username2_org), cid12);

		int cid3 = help.CreateChat(username1_org, "Test1 Chat");
		int u2c3 = help.AddUserToChat(help.getUserID(username1_org), cid3);
		// User2Chat anlegen 3-5 x

		message_txt1 = help.insertMessage(help.getUserID(username1_org), u2c1,
				Constants.TYP_TEXT, content_msg1, 0, true);
		message_txt2 = help.insertMessage(help.getUserID(username1_org), u2c2,
				Constants.TYP_TEXT, content_msg1, message_txt1, false);
		message_txt2a = help.insertMessage(help.getUserID(username3_org), u2c3,
				Constants.TYP_TEXT, content_msg1, 0, true);

		int content_img = help.InsertFixedImage();
		message_img1 = help.insertMessage(help.getUserID(username1_org), u2c1,
				Constants.TYP_IMAGE, content_img, 0, true);
		message_img2 = help.insertMessage(help.getUserID(username1_org), u2c2,
				Constants.TYP_IMAGE, content_img, message_img1, false);
		message_img2a = help.insertMessage(help.getUserID(username3_org), u2c3,
				Constants.TYP_IMAGE, content_img, message_img1, true);

		int content_vid = help.InsertFixedVideo();
		message_vid1 = help.insertMessage(help.getUserID(username1_org), u2c1,
				Constants.TYP_VIDEO, content_vid, 0, true);
		message_vid2 = help.insertMessage(help.getUserID(username1_org), u2c2,
				Constants.TYP_VIDEO, content_vid, message_vid1, false);
		message_vid2a = help.insertMessage(help.getUserID(username3_org), u2c3,
				Constants.TYP_VIDEO, content_vid, message_vid1, true);
		LOGGER.debug("End prepareDB");
	}

	@Test
	public void testDeleteMessageFromChatUserPassword() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username1, password1));

		target = c.target(TestConfig.URL).path(functionurl);
		LOGGER.debug(target);
		ODMFC out = target.request().delete(ODMFC.class);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NONE_EXISTING_MESSAGE, out.getET());
	}

	@Test
	public void testDeleteMessageFromChatUserPasswordMessageIDTextNotOwner() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username1, password1));

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_MESSAGEID, message_txt2);
		LOGGER.debug(target);
		ODMFC out = target.request().delete(ODMFC.class);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NOT_MESSAGE_OWNER, out.getET());
	}

	@Test
	public void testDeleteMessageFromChatUserPasswordMessageIDTextOwner() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username1, password1));

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_MESSAGEID, message_txt1);
		LOGGER.debug(target);
		ODMFC out1 = target.request().delete(ODMFC.class);
		LOGGER.debug(out1.getMID());
		Assert.assertNotNull(out1.getMID());
		Assert.assertNull(out1.getET());

		WebTarget target2;
		Client c2 = ClientBuilder.newClient();
		c2.register(HttpAuthenticationFeature.basic(username2, password2));

		target2 = c2.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_MESSAGEID, message_txt2);
		LOGGER.debug(target2);

		ODMFC out2 = target2.request().delete(ODMFC.class);
		LOGGER.debug(out2.getMID());
		Assert.assertNotNull(out2.getMID());
		Assert.assertNull(out2.getET());
	}

	@Test
	public void testDeleteMessageFromChatUserPasswordMessageIDImageNotOwner() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username1, password1));

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_MESSAGEID, message_img2);
		LOGGER.debug(target);
		ODMFC out = target.request().delete(ODMFC.class);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NOT_MESSAGE_OWNER, out.getET());
	}

	@Test
	public void testDeleteMessageFromChatUserPasswordMessageIDImageOwner() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username1, password1));

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_MESSAGEID, message_img1);
		LOGGER.debug(target);
		ODMFC out1 = target.request().delete(ODMFC.class);
		LOGGER.debug(out1.getMID());
		Assert.assertNotNull(out1.getMID());
		Assert.assertNull(out1.getET());

		WebTarget target2;
		Client c2 = ClientBuilder.newClient();
		c2.register(HttpAuthenticationFeature.basic(username2, password2));

		target2 = c2.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_MESSAGEID, message_img2);
		LOGGER.debug(target2);
		ODMFC out2 = target2
				.register(
						HttpAuthenticationFeature.basicBuilder().credentials(
								username2, password2)).request()
				.delete(ODMFC.class);
		LOGGER.debug(out2.getMID());
		Assert.assertNotNull(out2.getMID());
		Assert.assertNull(out2.getET());
	}

	@Test
	public void testDeleteMessageFromChatUserPasswordMessageIDVideoNotOwner() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username2, password2));

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_MESSAGEID, message_vid2a);
		LOGGER.debug(target);

		ODMFC out = target.request().delete(ODMFC.class);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NOT_MESSAGE_OWNER, out.getET());
	}

	@Test
	public void testDeleteMessageFromChatUserPasswordMessageIDVideoOwner() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username1, password1));

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_MESSAGEID, message_vid1);
		LOGGER.debug(target);

		ODMFC out1 = target.request().delete(ODMFC.class);
		LOGGER.debug(out1.getMID());
		Assert.assertNotNull(out1.getMID());
		Assert.assertNull(out1.getET());

		WebTarget target2;
		Client c2 = ClientBuilder.newClient();
		c2.register(HttpAuthenticationFeature.basic(username2, password2));

		target2 = c2.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_MESSAGEID, message_vid2);
		LOGGER.debug(target2);

		ODMFC out2 = target2.request().delete(ODMFC.class);
		LOGGER.debug(out2.getMID());
		Assert.assertNotNull(out2.getMID());
		Assert.assertNull(out2.getET());
	}
}