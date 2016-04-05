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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

import de.radiohacks.frinmeba.model.jaxb.IAdUC;
import de.radiohacks.frinmeba.model.jaxb.OAdUC;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestAddUserToChat extends JerseyTest {

	/*
	 * @PUT
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Consumes(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/addusertochat") public OAdUC AddUserToChat(IAdUC in);
	 */

	private static final Logger LOGGER = Logger
			.getLogger(TestAddUserToChat.class.getName());

	// Username welche anzulegen ist
	final static String username_org = "Test1";
	final static String username = Base64.encodeBase64String(username_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	// Passwort zum User
	final static String password_org = "Test1";
	final static String password = Base64.encodeBase64String(password_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	// Email Adresse zum User
	final static String email_org = "Test1@frinme.org";
	final static String email = Base64.encodeBase64String(email_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

	final static String functionurl = "user/addusertochat";

	// Username welche anzulegen ist
	final static String username2_org = "Test2";
	final static String username2 = Base64.encodeBase64String(username2_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	// Passwort zum User
	final static String password2_org = "Test2";
	final static String password2 = Base64.encodeBase64String(password2_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	// Email Adresse zum User
	final static String email2_org = "Test2@frinme.org";
	final static String email2 = Base64.encodeBase64String(email2_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

	// Chatname
	final static String chatname_org = "Chat1";
	final static String chatname = Base64.encodeBase64String(chatname_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

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
		LOGGER.debug("Start prepareDB");
		dropDatabaseTables drop = new dropDatabaseTables();
		drop.dropTable();
		createDatabaseTables create = new createDatabaseTables();
		create.createTable();
		helperDatabase help = new helperDatabase();
		help.CreateActiveUser(username_org, username, password_org, email_org,
				help.InsertFixedImage());
		help.CreateActiveUser(username2_org, username2, password2_org,
				email2_org, help.InsertFixedImage());
		chatid = help.CreateChat(username_org, chatname_org);
		LOGGER.debug("End prepareDB");
	}

	private OAdUC callTarget(IAdUC in) {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username, password));

		target = c.target(TestConfig.URL).path(functionurl);
		LOGGER.debug(target);
		Response response = target.request()
				.buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
				.invoke();
		LOGGER.debug(response);
		return response.readEntity(OAdUC.class);
	}

	@Test
	public void testAddUserToChatNoValues() {
		IAdUC in = new IAdUC();
		OAdUC out = callTarget(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatUserID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUID(userid);
		OAdUC out = callTarget(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatChatID() {
		IAdUC in = new IAdUC();
		in.setCID(chatid);
		OAdUC out = callTarget(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
	}

	@Test
	public void testAddUserToChatUserIDChatID() {
		helperDatabase help = new helperDatabase();
		help.DelUserToChats();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		LOGGER.debug(out.getR());
		Assert.assertEquals(Constants.USER_ADDED, out.getR());
	}

	@Test
	public void testAddUserToChatWrongUserIDChatID() {
		IAdUC in = new IAdUC();
		in.setCID(chatid);
		in.setUID(47);
		OAdUC out = callTarget(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
	}

	@Test
	public void testAddUserToChatUserIDWrongChatID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setCID(47);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatUserIDChatID_Again() {
		helperDatabase help = new helperDatabase();
		help.DelUserToChats();
		userid = help.getUserID(username_org);
		help.AddUserToChat(userid, chatid);
		userid = help.getUserID(username2_org);
		help.AddUserToChat(userid, chatid);
		IAdUC in = new IAdUC();
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.USER_ALREADY_IN_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatUserIDChatID_foreignChat() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		int chatid2 = help.CreateChat(username2_org, "Chat2");
		IAdUC in = new IAdUC();
		in.setCID(chatid2);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NOT_CHAT_OWNER, out.getET());
	}

	@Test
	public void testAddUserToChatUserIDChatID_selfAdd() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username_org);
		IAdUC in = new IAdUC();
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.CHAT_OWNER_NOT_ADDED, out.getET());
	}
}