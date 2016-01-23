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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import de.radiohacks.frinmeba.modelshort.IAckMD;
import de.radiohacks.frinmeba.modelshort.ISTeM;
import de.radiohacks.frinmeba.modelshort.OAckMD;
import de.radiohacks.frinmeba.modelshort.OSImM;
import de.radiohacks.frinmeba.modelshort.OSTeM;
import de.radiohacks.frinmeba.modelshort.OSViM;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestAcknowledgeMessageDownload extends JerseyTest {

	/*
	 * @POST
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Consumes(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/acknowledgemessagedownload") public OAckMD
	 * acknowledgeMessageDownload(IAckMD in);
	 */

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

	final static String functionurl = "user/acknowledgemessagedownload";

	final static String textmsg_org = "Test Nachnricht fuer Acknowledge";
	final static String textmsg = Base64.encodeBase64String(textmsg_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

	final static String md5sumimg_org = "e36ba04dd1ad642a6e8c74c72a4aab8c";
	final static String md5sumimg = Base64.encodeBase64String(md5sumimg_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String md5sumvid_org = "ba0623b8c7a7520092ee1ff71da0bbea";
	final static String md5sumvid = Base64.encodeBase64String(md5sumvid_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));
	final static String md5sumtxt_org = "[B@2e41b2e9";
	final static String md5sumtxt = Base64.encodeBase64String(md5sumtxt_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

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
	}

	private OAckMD callTarget(IAckMD in) {
		WebTarget target = ClientBuilder.newClient().target(
				TestConfig.URL + functionurl);
		Response response = target.request()
				.buildPost(Entity.entity(in, MediaType.APPLICATION_XML))
				.invoke();
		return response.readEntity(OAckMD.class);
	}

	private int uploadImageContent(String url) {
		WebTarget target;
		if (TestConfig.remote) {
			Client client = ClientBuilder.newBuilder()
					.register(MultiPartFeature.class).build();

			target = client.target(TestConfig.URL + url);
		} else {
			target = target(url);
		}

		final FormDataMultiPart mp = new FormDataMultiPart();

		InputStream data = this.getClass().getResourceAsStream("/test.jpg");
		final FormDataContentDisposition dispo = FormDataContentDisposition
				.name("file").fileName("test.jpg").size(1).build();

		final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		mp.bodyPart(fdp2);

		OSImM x = target.request().post(Entity.entity(mp, mp.getMediaType()),
				OSImM.class);
		return x.getImID();
	}

	private int uploadVideoContent(String url) {
		WebTarget target;
		if (TestConfig.remote) {
			Client client = ClientBuilder.newBuilder()
					.register(MultiPartFeature.class).build();

			target = client.target(TestConfig.URL + url);
		} else {
			target = target(url);
		}

		final FormDataMultiPart mp = new FormDataMultiPart();

		InputStream data = this.getClass().getResourceAsStream("/test.mp4");
		final FormDataContentDisposition dispo = FormDataContentDisposition
				.name("file").fileName("test.mp4").size(1).build();

		final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		mp.bodyPart(fdp2);

		OSViM x = target.request().post(Entity.entity(mp, mp.getMediaType()),
				OSViM.class);
		return x.getVID();
	}

	private int uploadTextContent() {
		ISTeM in = new ISTeM();
		in.setUN(username);
		in.setPW(password);
		in.setTM(textmsg);

		WebTarget target = ClientBuilder.newClient().target(
				TestConfig.URL + "user/sendtextmessage");
		Response response = target.request()
				.buildPut(Entity.entity(in, MediaType.APPLICATION_XML))
				.invoke();
		OSTeM x = response.readEntity(OSTeM.class);
		return x.getTID();
	}

	@Test
	public void testAcknowledgeMessageDownloadUpNoValues() {
		IAckMD in = new IAckMD();
		OAckMD out = callTarget(in);
		
		assertThat(out.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
	}

	@Test
	public void testAcknowledgeMessageDownloadUser() {
		IAckMD in = new IAckMD();
		in.setUN(username);
		OAckMD out = callTarget(in);

		// WebTarget target;
		// if (TestConfig.remote) {
		// target = ClientBuilder.newClient()
		// .target(TestConfig.URL + functionurl)
		// .queryParam(Constants.QPusername, username);
		// ;
		// } else {
		// target = target(functionurl).queryParam(Constants.QPusername,
		// username);
		// ;
		// }
		// OAckMD out = target.request().get(OAckMD.class);

		assertThat(out.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
	}

	@Test
	public void testAcknowledgeMessageDownloadPassword() {
		IAckMD in = new IAckMD();
		in.setPW(password);
		OAckMD out = callTarget(in);
		// WebTarget target;
		// if (TestConfig.remote) {
		// target = ClientBuilder.newClient()
		// .target(TestConfig.URL + functionurl)
		// .queryParam(Constants.QPpassword, password);
		// ;
		// } else {
		// target = target(functionurl).queryParam(Constants.QPpassword,
		// password);
		// }
		// OAckMD out = target.request().get(OAckMD.class);

		assertThat(out.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
	}

	@Test
	public void testAcknowledgeMessageDownloadUserPassword() {
		IAckMD in = new IAckMD();
		in.setUN(username);
		in.setPW(password);
		OAckMD out = callTarget(in);
		// WebTarget target;
		// if (TestConfig.remote) {
		// target = ClientBuilder.newClient()
		// .target(TestConfig.URL + functionurl)
		// .queryParam(Constants.QPpassword, password)
		// .queryParam(Constants.QPusername, username);
		// ;
		// } else {
		// target = target(functionurl).queryParam(Constants.QPpassword,
		// password).queryParam(Constants.QPusername, username);
		// }
		// OAckMD out = target.request().get(OAckMD.class);

		assertThat(out.getET(), is(Constants.NO_CONTENT_GIVEN));
	}

	@Test
	public void testAcknowledgeMessageDownloadUserPasswordNoAcknowledge() {
		IAckMD in = new IAckMD();
		in.setUN(username);
		in.setPW(Base64.encodeBase64String("XXX".getBytes(Charset
				.forName(Constants.CHARACTERSET))));
		OAckMD out = callTarget(in);
		// WebTarget target;
		// if (TestConfig.remote) {
		// target = ClientBuilder
		// .newClient()
		// .target(TestConfig.URL + functionurl)
		// .queryParam(
		// Constants.QPpassword,
		// Base64.encodeBase64String("XXX".getBytes(Charset
		// .forName(Constants.CharacterSet))))
		// .queryParam(Constants.QPusername, username);
		// } else {
		// target = target(functionurl).queryParam(
		// Constants.QPpassword,
		// Base64.encodeBase64String("XXX".getBytes(Charset
		// .forName(Constants.CharacterSet)))).queryParam(
		// Constants.QPusername, username);
		// }
		// OAckMD out = target.request().get(OAckMD.class);

		assertThat(out.getET(), is(Constants.NO_CONTENT_GIVEN));
	}

	@Test
	public void testAcknowledgeMessageDownloadUserWrongPassword() {
		IAckMD in = new IAckMD();
		in.setUN(username);
		in.setPW(Base64.encodeBase64String("XXX".getBytes(Charset
				.forName(Constants.CHARACTERSET))));
		in.setACK(md5sumimg);
		OAckMD out = callTarget(in);
		// WebTarget target;
		// if (TestConfig.remote) {
		// target = ClientBuilder
		// .newClient()
		// .target(TestConfig.URL + functionurl)
		// .queryParam(
		// Constants.QPpassword,
		// Base64.encodeBase64String("XXX".getBytes(Charset
		// .forName(Constants.CharacterSet))))
		// .queryParam(Constants.QPusername, username)
		// .queryParam(Constants.QPacknowledge, password);
		// } else {
		// target = target(functionurl)
		// .queryParam(
		// Constants.QPpassword,
		// Base64.encodeBase64String("XXX".getBytes(Charset
		// .forName(Constants.CharacterSet))))
		// .queryParam(Constants.QPusername, username)
		// .queryParam(Constants.QPacknowledge, password);
		// }
		// OAckMD out = target.request().get(OAckMD.class);

		assertThat(out.getET(), is(Constants.WRONG_PASSWORD));
	}

	@Test
	public void testAcknowledgeMessageDownloadUserEncodeFailureUser() {
		IAckMD in = new IAckMD();
		in.setUN("XXX");
		in.setPW(password);
		OAckMD out = callTarget(in);
		// WebTarget target;
		// if (TestConfig.remote) {
		// target = ClientBuilder.newClient()
		// .target(TestConfig.URL + functionurl)
		// .queryParam(Constants.QPpassword, password)
		// .queryParam(Constants.QPusername, "XXX");
		// ;
		// } else {
		// target = target(functionurl).queryParam(Constants.QPpassword,
		// password).queryParam(Constants.QPusername, "XXX");
		// ;
		// }
		// OAckMD out = target.request().get(OAckMD.class);

		assertThat(out.getET(), is(Constants.ENCODING_ERROR));
	}

	@Test
	public void testAcknowledgeMessageDownloadUserEncodeFailurePassword() {
		IAckMD in = new IAckMD();
		in.setUN(username);
		in.setPW("XXX");
		OAckMD out = callTarget(in);
		// WebTarget target;
		// if (TestConfig.remote) {
		// target = ClientBuilder.newClient()
		// .target(TestConfig.URL + functionurl)
		// .queryParam(Constants.QPpassword, "XXX")
		// .queryParam(Constants.QPusername, username);
		// ;
		// } else {
		// target = target(functionurl)
		// .queryParam(Constants.QPpassword, "XXX").queryParam(
		// Constants.QPusername, username);
		// ;
		// }
		// OAckMD out = target.request().get(OAckMD.class);

		assertThat(out.getET(), is(Constants.ENCODING_ERROR));
	}

	// TODO : Test off temporarily
	// @Test
	public void testAcknowledgeMessageDownloadUserPasswordAcknowledgeImage() {

		int msgimgid = uploadImageContent("image/upload?"
				+ Constants.QP_USERNAME + "=" + username + "&"
				+ Constants.QP_PASSWORD + "=" + password + "&"
				+ Constants.QP_ACKNOWLEDGE + "=" + md5sumimg);

		helperDatabase help = new helperDatabase();
		help.CreateChat(username_org, "Test Chat");
		int u2c = help.AddUserToChat(help.getUserID(username_org),
				help.getChatID("Test Chat"));
		int msgid = help.insertMessage(help.getUserID(username_org), u2c,
				Constants.TYP_IMAGE, msgimgid, 0, true);

		HashCode md5 = null;
		try {
			md5 = Files.hash(new File(this.getClass().getResource("/test.jpg")
					.getFile()), Hashing.md5());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String md5sumimg = new String(Base64.encodeBase64(md5.toString()
				.getBytes()), Charset.forName(Constants.CHARACTERSET));

		IAckMD in = new IAckMD();
		in.setUN(username);
		in.setPW(password);
		in.setMID(msgid);
		in.setACK(md5sumimg);
		OAckMD out = callTarget(in);
		// WebTarget target;
		// if (TestConfig.remote) {
		// target = ClientBuilder.newClient()
		// .target(TestConfig.URL + functionurl)
		// .queryParam(Constants.QPpassword, password)
		// .queryParam(Constants.QPusername, username)
		// .queryParam(Constants.QPmessageid, msgid)
		// .queryParam(Constants.QPacknowledge, md5sumimg);
		// } else {
		// target = target(functionurl)
		// .queryParam(Constants.QPpassword, password)
		// .queryParam(Constants.QPusername, username)
		// .queryParam(Constants.QPmessageid, msgid)
		// .queryParam(Constants.QPacknowledge, md5sumimg);
		// }
		// OAckMD out = target.request().get(OAckMD.class);

		assertThat(out.getACK(), is(Constants.ACKNOWLEDGE_TRUE));
	}

	// TODO : Test off temporarily
	// @Test
	public void testAcknowledgeMessageDownloadUserPasswordAcknowledgeVideo() {

		int msgvidid = uploadVideoContent("video/upload?"
				+ Constants.QP_USERNAME + "=" + username + "&"
				+ Constants.QP_PASSWORD + "=" + password + "&"
				+ Constants.QP_ACKNOWLEDGE + "=" + md5sumvid);

		helperDatabase help = new helperDatabase();
		help.CreateChat(username_org, "Test Chat");
		int u2c = help.AddUserToChat(help.getUserID(username_org),
				help.getChatID("Test Chat"));
		int msgid = help.insertMessage(help.getUserID(username_org), u2c,
				Constants.TYP_VIDEO, msgvidid, 0, true);

		HashCode md5 = null;
		try {
			md5 = Files.hash(new File(this.getClass().getResource("/test.mp4")
					.getFile()), Hashing.md5());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String md5sumimg = new String(Base64.encodeBase64(md5.toString()
				.getBytes()), Charset.forName(Constants.CHARACTERSET));

		IAckMD in = new IAckMD();
		in.setUN(username);
		in.setPW(password);
		in.setMID(msgid);
		in.setACK(md5sumimg);
		OAckMD out = callTarget(in);
		// WebTarget target;
		// if (TestConfig.remote) {
		// target = ClientBuilder.newClient()
		// .target(TestConfig.URL + functionurl)
		// .queryParam(Constants.QPpassword, password)
		// .queryParam(Constants.QPusername, username)
		// .queryParam(Constants.QPmessageid, msgid)
		// .queryParam(Constants.QPacknowledge, md5sumimg);
		// } else {
		// target = target(functionurl)
		// .queryParam(Constants.QPpassword, password)
		// .queryParam(Constants.QPusername, username)
		// .queryParam(Constants.QPmessageid, msgid)
		// .queryParam(Constants.QPacknowledge, md5sumimg);
		// }
		// OAckMD out = target.request().get(OAckMD.class);

		assertThat(out.getACK(), is(Constants.ACKNOWLEDGE_TRUE));
	}

	@Test
	public void testAcknowledgeMessageDownloadUserPasswordAcknowledgeText() {

		int msgimgid = uploadTextContent();

		helperDatabase help = new helperDatabase();
		help.CreateChat(username_org, "Test Chat");
		int u2c = help.AddUserToChat(help.getUserID(username_org),
				help.getChatID("Test Chat"));
		int msgid = help.insertMessage(help.getUserID(username_org), u2c,
				Constants.TYP_TEXT, msgimgid, 0, true);

		int hashCode = textmsg_org.hashCode();
		String sha1b64 = new String(Base64.encodeBase64(String
				.valueOf(hashCode).getBytes()),
				Charset.forName(Constants.CHARACTERSET));

		IAckMD in = new IAckMD();
		in.setUN(username);
		in.setPW(password);
		in.setMID(msgid);
		in.setACK(sha1b64);
		OAckMD out = callTarget(in);
		// WebTarget target;
		// if (TestConfig.remote) {
		// target = ClientBuilder.newClient()
		// .target(TestConfig.URL + functionurl)
		// .queryParam(Constants.QPpassword, password)
		// .queryParam(Constants.QPusername, username)
		// .queryParam(Constants.QPmessageid, msgid)
		// .queryParam(Constants.QPacknowledge, sha1b64);
		// } else {
		// target = target(functionurl)
		// .queryParam(Constants.QPpassword, password)
		// .queryParam(Constants.QPusername, username)
		// .queryParam(Constants.QPmessageid, msgid)
		// .queryParam(Constants.QPacknowledge, sha1b64);
		// }
		// OAckMD out = target.request().get(OAckMD.class);

		assertThat(out.getACK(), is(Constants.ACKNOWLEDGE_TRUE));
	}
}
