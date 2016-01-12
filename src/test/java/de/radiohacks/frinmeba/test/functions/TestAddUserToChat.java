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

import de.radiohacks.frinmeba.modelshort.IAdUC;
import de.radiohacks.frinmeba.modelshort.OAdUC;
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
		help.CreateActiveUser(username_org, username, password_org, email_org, help.InsertFixedImage());
		help.CreateActiveUser(username2_org, username2, password2_org,
				email2_org, help.InsertFixedImage());
		chatid = help.CreateChat(username_org, chatname_org);

	}

	private OAdUC callTarget(IAdUC in) {
		WebTarget target = ClientBuilder.newClient().target(
				TestConfig.URL + functionurl);
		Response response = target.request()
				.buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
				.invoke();
		return response.readEntity(OAdUC.class);
	}

	@Test
	public void testAddUserToChatUpNoValues() {
		IAdUC in = new IAdUC();
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordUserIDChatID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setPW(password);
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.USER_ADDED, out.getR());
	}

	@Test
	public void testAddUserToChatUserPasswordUserID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setPW(password);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordChatID() {
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setPW(password);
		in.setCID(chatid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
	}

	@Test
	public void testAddUserToChatUserUserIDChatID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatPasswordUserIDChatID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setPW(password);
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserPassword() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setPW(password);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatUserUserID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserChatID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setCID(chatid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatPasswordUserID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setPW(password);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatPasswordChatID() {
		System.out.print("Start testAddUserToChatPasswordChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setPW(password);
		in.setCID(chatid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserIDChatID() {
		System.out.print("Start testAddUserToChatUserIDChatID\n");
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUser() {
		IAdUC in = new IAdUC();
		in.setUN(username);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatPassword() {
		IAdUC in = new IAdUC();
		in.setPW(password);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatChatID() {
		IAdUC in = new IAdUC();
		in.setCID(chatid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NO_USERNAME_OR_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatWrongUserPasswordUserIDChatID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUN(Base64.encodeBase64String("blah".getBytes(Charset
				.forName(Constants.CharacterSet))));
		in.setPW(password);
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
	}

	@Test
	public void testAddUserToChatUserWrongPasswordUserIDChatID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setPW(Base64.encodeBase64String("blah".getBytes(Charset
				.forName(Constants.CharacterSet))));
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.WRONG_PASSWORD, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordWrongUserIDChatID() {
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setPW(password);
		in.setCID(chatid);
		in.setUID(47);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NONE_EXISTING_USER, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordUserIDWrongChatID() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setPW(password);
		in.setCID(47);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordUserIDChatID_Again() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		help.AddUserToChat(userid, chatid);
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setPW(password);
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.USER_ALREADY_IN_CHAT, out.getET());
	}

	@Test
	public void testAddUserToChatUserPasswordUserIDChatID_foreignChat() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username2_org);
		int chatid2 = help.CreateChat(username2_org, "Chat2");
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setPW(password);
		in.setCID(chatid2);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.NOT_CHAT_OWNER, out.getET());

	}

	@Test
	public void testAddUserToChatUserPasswordUserIDChatID_selfAdd() {
		helperDatabase help = new helperDatabase();
		userid = help.getUserID(username_org);
		IAdUC in = new IAdUC();
		in.setUN(username);
		in.setPW(password);
		in.setCID(chatid);
		in.setUID(userid);
		OAdUC out = callTarget(in);
		Assert.assertEquals(Constants.CHAT_OWNER_NOT_ADDED, out.getET());
	}
}