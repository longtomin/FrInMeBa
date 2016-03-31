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

import java.nio.charset.Charset;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
//import org.glassfish.jersey.client.ClientConfig;
//import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import de.radiohacks.frinmeba.model.jaxb.OGImMMD;
import de.radiohacks.frinmeba.model.jaxb.OSImM;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestGetImageMetaData extends JerseyTest {

	/*
	 * @GET
	 * 
	 * @Path("/getimagemetadata")
	 * 
	 * @Produces(MediaType.APPLICATION_XML) public OGImMMD getImageMetadata(
	 * 
	 * @QueryParam(Constants.QP_USERNAME) String user,
	 * 
	 * @QueryParam(Constants.QP_PASSWORD) String password,
	 * 
	 * @QueryParam("imageid") int imageid);
	 */

	private static final Logger LOGGER = Logger
			.getLogger(TestGetImageMetaData.class.getName());

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

	final static String functionurl = "image/getimagemetadata";

	final static String md5Sum = "e36ba04dd1ad642a6e8c74c72a4aab8c";

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

	/*
	 * @Override protected void configureClient(ClientConfig config) {
	 * config.register(MultiPartFeature.class); }
	 */

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

	private int insertImage() {
		// Insert new Image in DB an Filesystem
		helperDatabase helper = new helperDatabase();
		/*
		 * Works only if a local server is used; URL url =
		 * this.getClass().getResource("/test.jpg"); File in = new
		 * File(url.getFile()); return helper.InsertAndSaveImage(in);
		 */

		/*
		 * Else use a static unixtime which is 1010101010 and a static filename
		 * which is test.jpg insert it into the db, the copy must be done
		 * outside.
		 */
		// return helper.InsertFixedImage();
		OSImM o = helper.insertImageContent(username, password);
		if ((o.getET() == null || o.getET().isEmpty()) && o.getImID() > 0) {
			return o.getImID();
		} else {
			return 0;
		}
	}

	private void deleteImage(int in) {
		helperDatabase helper = new helperDatabase();
		/*
		 * If the File was inserted and created by the the then use this
		 */
		// helper.deleteAndDropImage(in);
		/*
		 * Otherwise just delete the DB Entry and let the file delete be done
		 * outside.
		 */
		helper.deleteFixedImage(in);
	}

	@Test
	public void testGetImageMetaDataUserPassword() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username, password));

		target = c.target(TestConfig.URL).path(functionurl);
		LOGGER.debug(target);
		OGImMMD out = target.request().get(OGImMMD.class);
		LOGGER.debug("ET=" + out.getET());
		assertThat(out.getET(), is(Constants.NONE_EXISTING_CONTENT_MESSAGE));
	}

	@Test
	public void testGetImageMetaDataUserPasswordImageID() {
		int imageid = insertImage();
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username, password));

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_IMAGEID, imageid);
		LOGGER.debug(target);
		OGImMMD out = target.request().get(OGImMMD.class);
		LOGGER.debug("ET=" + out.getET());
		assertThat(out.getIS(), is(not(nullValue())));
		deleteImage(imageid);
	}

	@Test
	public void testGetImageMetaDataUserPasswordWrongImageID() {
		WebTarget target;
		Client c = ClientBuilder.newClient();
		c.register(HttpAuthenticationFeature.basic(username, password));

		target = c.target(TestConfig.URL).path(functionurl)
				.queryParam(Constants.QP_IMAGEID, 107365);
		LOGGER.debug(target);
		OGImMMD out = target.request().get(OGImMMD.class);
		LOGGER.debug("ET=" + out.getET());
		assertThat(out.getET(), is(Constants.NONE_EXISTING_CONTENT_MESSAGE));
	}

}
