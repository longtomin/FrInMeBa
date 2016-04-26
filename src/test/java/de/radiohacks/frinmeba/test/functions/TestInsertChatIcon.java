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

import de.radiohacks.frinmeba.model.jaxb.IICIc;
import de.radiohacks.frinmeba.model.jaxb.OICIc;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestInsertChatIcon extends JerseyTest {

	/*
	 * @PUT
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Consumes(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/insertchaticon") public OICIc insertchaticon(IICIc in);
	 */

	private static final Logger LOGGER = Logger
			.getLogger(TestInsertChatIcon.class.getName());

	// Username welche anzulegen ist
	final static String username1_org = "Test1";
	final static String username1 = Base64.encodeBase64String(username1_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String username2_org = "Test2";
	final static String username2 = Base64.encodeBase64String(username2_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

	// Passwort zum User
	final static String password1_org = "Test1";
	final static String password1 = Base64.encodeBase64String(password1_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String password2_org = "Test2";
	final static String password2 = Base64.encodeBase64String(password2_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

	// Email Adresse zum User
	final static String email1_org = "Test1@frinme.org";
	final static String email1 = Base64.encodeBase64String(email1_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String email2_org = "Test2@frinme.org";
	final static String email2 = Base64.encodeBase64String(email2_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

	final static String functionurl = "user/insertchaticon";

	// Text Message
	static int iconid;
	static int chatid1;
	static int chatid2;

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
		LOGGER.debug("Start BeforeClass");
		dropDatabaseTables drop = new dropDatabaseTables();
		drop.dropTable();
		createDatabaseTables create = new createDatabaseTables();
		create.createTable();
		helperDatabase help = new helperDatabase();
		help.CreateActiveUser(username1_org, username1, password1_org,
				email1_org, help.InsertFixedImage());
		help.CreateActiveUser(username2_org, username2, password2_org,
				email2_org, help.InsertFixedImage());
		chatid1 = help.CreateChat(username1_org, "Chat_Test1");
		chatid2 = help.CreateChat(username2_org, "Chat-Test2");
		iconid = help.InsertFixedImage();
		LOGGER.debug("End BeforeClass");
	}

	private OICIc callTargetUser1(IICIc in) {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username1, password1));
		target = c.target(TestConfig.URL + functionurl);
		LOGGER.debug(target);
		Response response = target.request()
				.buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
				.invoke();
		LOGGER.debug(response);
		return response.readEntity(OICIc.class);
	}

	@Test
	public void testInsertChatIconUserPassword() {
		IICIc in = new IICIc();
		OICIc out = callTargetUser1(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testInsertChatIconUserPasswordIcon() {
		IICIc in = new IICIc();
		in.setIcID(iconid);
		OICIc out = callTargetUser1(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(out.getET(), Constants.NONE_EXISTING_CHAT);
	}

	@Test
	public void testInsertChatIconUserPasswordChat() {
		IICIc in = new IICIc();
		in.setCID(chatid1);
		OICIc out = callTargetUser1(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(out.getET(),
				Constants.NONE_EXISTING_CONTENT_MESSAGE);
	}

	@Test
	public void testInsertChatIconNoneExistingChat() {
		IICIc in = new IICIc();
		in.setCID(17);
		in.setIcID(iconid);
		OICIc out = callTargetUser1(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NONE_EXISTING_CHAT, out.getET());
	}

	@Test
	public void testInsertChatIconNotChatOwner() {
		IICIc in = new IICIc();
		in.setCID(chatid2);
		in.setIcID(iconid);
		OICIc out = callTargetUser1(in);
		LOGGER.debug("ET=" + out.getET());
		Assert.assertEquals(Constants.NOT_CHAT_OWNER, out.getET());
	}

	@Test
	public void testInsertChatIconUserPasswordIconChatCorrect() {
		IICIc in = new IICIc();
		in.setIcID(iconid);
		in.setCID(chatid1);
		OICIc out = callTargetUser1(in);
		LOGGER.debug("R=" + out.getR());
		Assert.assertEquals(out.getR(), Constants.ICON_ADDED);
	}
}