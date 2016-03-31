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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.nio.charset.Charset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
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
import org.junit.BeforeClass;
import org.junit.Test;

import de.radiohacks.frinmeba.model.jaxb.OSViM;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestUploadVideo extends JerseyTest {

	/*
	 * @POST
	 * 
	 * @Path("/upload")
	 * 
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Consumes(MediaType.MULTIPART_FORM_DATA) public OSViM uploadVideo(
	 * 
	 * @QueryParam(Constants.QPusername) String User,
	 * 
	 * @QueryParam(Constants.QPpassword) String Password,
	 * 
	 * @FormDataParam("file") InputStream fileInputStream,
	 * 
	 * @FormDataParam("file") FormDataContentDisposition
	 * contentDispositionHeader);
	 */

	private static final Logger LOGGER = Logger.getLogger(TestUploadVideo.class
			.getName());

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
	// Acknowledge zur
	final static String acknowledge_org = "ba0623b8c7a7520092ee1ff71da0bbea";
	final static String acknowledge = Base64.encodeBase64String(acknowledge_org
			.getBytes(Charset.forName(Constants.CHARACTERSET)));

	final static String functionurl = "video/upload";

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
		LOGGER.debug("Start BeforeClass");
		dropDatabaseTables drop = new dropDatabaseTables();
		drop.dropTable();
		createDatabaseTables create = new createDatabaseTables();
		create.createTable();
		helperDatabase help = new helperDatabase();
		help.CreateActiveUser(username_org, username, password_org, email_org,
				help.InsertFixedImage());
		LOGGER.debug("End BeforeClass");
	}

	@Test
	public void testUploadVideoUpNoValues() {
		WebTarget target;
		Client client = ClientBuilder.newBuilder()
				.register(MultiPartFeature.class).build();

		target = client.target(TestConfig.URL).path(functionurl);
		LOGGER.debug(target);

		final FormDataMultiPart mp = new FormDataMultiPart();

		InputStream data = this.getClass().getResourceAsStream("/test.mp4");
		final FormDataContentDisposition dispo = FormDataContentDisposition
				.name("file").fileName("test.mp4").size(1).build();

		final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		mp.bodyPart(fdp2);

		Response resp = target.request().post(
				Entity.entity(mp, mp.getMediaType()));
		LOGGER.debug("Response = " + resp);
		Assert.assertEquals(401, resp.getStatus());
	}

	@Test
	public void testUploadVideo() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username, password));
		c.register(MultiPartFeature.class);

		target = c.target(TestConfig.URL).path(functionurl);

		LOGGER.debug(target);
		final FormDataMultiPart mp = new FormDataMultiPart();

		InputStream data = this.getClass().getResourceAsStream("/test.mp4");
		final FormDataContentDisposition dispo = FormDataContentDisposition
				.name("file").fileName("test.mp4").size(1).build();

		final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		mp.bodyPart(fdp2);

		OSViM out = target
				.register(
						HttpAuthenticationFeature.basicBuilder().credentials(
								username, password)).request()
				.post(Entity.entity(mp, mp.getMediaType()), OSViM.class);
		LOGGER.debug("ET=" + out.getET());
		assertThat(out.getET(), is(Constants.UPLOAD_FAILED));
	}

	@Test
	public void testUploadVideoAcknowledgeNoDisposition() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username, password));
		c.register(MultiPartFeature.class);

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_ACKNOWLEDGE, acknowledge);
		LOGGER.debug(target);
		final FormDataMultiPart mp = new FormDataMultiPart();

		InputStream data = this.getClass().getResourceAsStream("/test.mp4");
		final FormDataBodyPart fdp2 = new FormDataBodyPart("File", data,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		mp.bodyPart(fdp2);

		OSViM out = target.request().post(Entity.entity(mp, mp.getMediaType()),
				OSViM.class);
		LOGGER.debug("ET=" + out.getET());
		assertThat(out.getET(), is(Constants.NO_IMAGEMESSAGE_GIVEN));
	}

	@Test
	public void testUploadVideoNoAcknowledge() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username, password));
		c.register(MultiPartFeature.class);

		target = c.target(TestConfig.URL).path(functionurl);
		LOGGER.debug(target);
		final FormDataMultiPart mp = new FormDataMultiPart();

		InputStream data = this.getClass().getResourceAsStream("/test.mp4");
		final FormDataContentDisposition dispo = FormDataContentDisposition
				.name("file").fileName("test.mp4").size(1).build();

		final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		mp.bodyPart(fdp2);

		OSViM out = target.request().post(Entity.entity(mp, mp.getMediaType()),
				OSViM.class);
		LOGGER.debug("ET=" + out.getET());
		assertThat(out.getET(), is(Constants.UPLOAD_FAILED));
	}

	@Test
	public void testUploadVideoUserWrongPassword() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username, "XXX"));
		c.register(MultiPartFeature.class);

		target = c.target(TestConfig.URL).path(functionurl)
		// .queryParam(
		// Constants.QP_PASSWORD,
		// Base64.encodeBase64String("XXX".getBytes(Charset
		// .forName(Constants.CHARACTERSET))))
		// .queryParam(Constants.QP_USERNAME, username)
				.queryParam(Constants.QP_ACKNOWLEDGE, acknowledge);
		LOGGER.debug(target);
		final FormDataMultiPart mp = new FormDataMultiPart();

		InputStream data = this.getClass().getResourceAsStream("/test.mp4");
		final FormDataContentDisposition dispo = FormDataContentDisposition
				.name("file").fileName("test.mp4").size(1).build();

		final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		mp.bodyPart(fdp2);

		Response resp = target.request().post(
				Entity.entity(mp, mp.getMediaType()));
		LOGGER.debug("Response = " + resp);
		Assert.assertEquals(401, resp.getStatus());
	}

	@Test
	public void testUploadVideoUserEncodeFailureAcknowledge() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username, password));
		c.register(MultiPartFeature.class);

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_ACKNOWLEDGE, acknowledge_org);
		LOGGER.debug(target);
		final FormDataMultiPart mp = new FormDataMultiPart();

		InputStream data = this.getClass().getResourceAsStream("/test.mp4");
		final FormDataContentDisposition dispo = FormDataContentDisposition
				.name("file").fileName("test.mp4").size(1).build();

		final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		mp.bodyPart(fdp2);

		OSViM out = target.request().post(Entity.entity(mp, mp.getMediaType()),
				OSViM.class);
		LOGGER.debug("ET=" + out.getET());
		assertThat(out.getET(), is(Constants.ENCODING_ERROR));
	}

	@Test
	public void testUploadVideoAcknowledge() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username, password));
		c.register(MultiPartFeature.class);

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_ACKNOWLEDGE, acknowledge);
		LOGGER.debug(target);
		final FormDataMultiPart mp = new FormDataMultiPart();

		InputStream data = this.getClass().getResourceAsStream("/test.mp4");
		final FormDataContentDisposition dispo = FormDataContentDisposition
				.name("file").fileName("test.mp4").size(1).build();

		final FormDataBodyPart fdp2 = new FormDataBodyPart(dispo, data,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		mp.bodyPart(fdp2);

		OSViM out = target.request().post(Entity.entity(mp, mp.getMediaType()),
				OSViM.class);
		LOGGER.debug("VID=" + out.getVID());
		LOGGER.debug("VF=" + out.getVF());
		assertThat(out.getVID(), is(not(nullValue())));
		assertThat(out.getVF(), is(not(nullValue())));
	}
}