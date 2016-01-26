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

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import de.radiohacks.frinmeba.modelshort.OGImMMD;
import de.radiohacks.frinmeba.modelshort.OSImM;
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
	 * @Produces(MediaType.APPLICATION_XML)
	 * 
	 * @Path("/createchat") public OutCreateChat
	 * CreateChat(@QueryParam(Constants.QPusername) String User,
	 * 
	 * @QueryParam(Constants.QPpassword) String Password,
	 * 
	 * @QueryParam(Constants.QPchatname) String Chatname);
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

	@BeforeClass
	public static void prepareDB() {
		dropDatabaseTables drop = new dropDatabaseTables();
		drop.dropTable();
		createDatabaseTables create = new createDatabaseTables();
		create.createTable();
		helperDatabase help = new helperDatabase();
		help.CreateActiveUser(username_org, username, password_org, email_org,
				help.InsertFixedImage());
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
	public void testGetImageMetaDataUpNoValues() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(
					TestConfig.URL + functionurl);
		} else {
			target = target(functionurl);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
	}

	@Test
	public void testGetImageMetaDataUser() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_USERNAME, username);
		} else {
			target = target(functionurl).queryParam(Constants.QP_USERNAME,
					username);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
	}

	@Test
	public void testGetImageMetaDataPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password);
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD,
					password);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
	}

	@Test
	public void testGetImageMetaDataImageID() {
		int imageid = insertImage();
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_IMAGEID, imageid);
		} else {
			target = target(functionurl).queryParam(Constants.QP_IMAGEID,
					imageid);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
		deleteImage(imageid);
	}

	@Test
	public void testGetImageMetaDataUserPassword() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_USERNAME, username);
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD,
					password).queryParam(Constants.QP_USERNAME, username);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.NONE_EXISTING_CONTENT_MESSAGE));
	}

	@Test
	public void testGetImageMetaDataUserImageID() {
		int imageid = insertImage();
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_IMAGEID, imageid);
		} else {
			target = target(functionurl).queryParam(Constants.QP_USERNAME,
					username).queryParam(Constants.QP_IMAGEID, imageid);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
		deleteImage(imageid);
	}

	@Test
	public void testGetImageMetaDataPasswordImageID() {
		int imageid = insertImage();
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_IMAGEID, imageid);
		} else {
			target = target(functionurl).queryParam(Constants.QP_PASSWORD,
					password).queryParam(Constants.QP_IMAGEID, imageid);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.NO_USERNAME_OR_PASSWORD));
		deleteImage(imageid);
	}

	@Test
	public void testGetImageMetaDataEncodingErrorUser() {
		int imageid = insertImage();
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_USERNAME, "$%&1233")
					.queryParam(Constants.QP_IMAGEID, imageid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_USERNAME, "$%&1233")
					.queryParam(Constants.QP_IMAGEID, imageid);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.ENCODING_ERROR));
		deleteImage(imageid);
	}

	@Test
	public void testGetImageMetaDataEncodingErrorPassword() {
		int imageid = insertImage();
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, "$%&1233")
					.queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_IMAGEID, imageid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QP_PASSWORD, "$%&1233")
					.queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_IMAGEID, imageid);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.ENCODING_ERROR));
		deleteImage(imageid);
	}

	@Test
	public void testGetImageMetaDataUserPasswordImageID() {
		int imageid = insertImage();
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_IMAGEID, imageid);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_IMAGEID, imageid);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getIS(), is(not(nullValue())));
		deleteImage(imageid);
	}

	@Test
	public void testGetImageMetaDataUserWrongPasswordImageID() {
		int imageid = insertImage();
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(
							Constants.QP_PASSWORD,
							Base64.encodeBase64String("XXX".getBytes(Charset
									.forName(Constants.CHARACTERSET))))
					.queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_IMAGEID, imageid);
		} else {
			target = target(functionurl)
					.queryParam(
							Constants.QP_PASSWORD,
							Base64.encodeBase64String("XXX".getBytes(Charset
									.forName(Constants.CHARACTERSET))))
					.queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_IMAGEID, imageid);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.WRONG_PASSWORD));
		deleteImage(imageid);
	}

	@Test
	public void testGetImageMetaDataUserPasswordWrongImageID() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_IMAGEID, 107365);
		} else {
			target = target(functionurl)
					.queryParam(Constants.QP_PASSWORD, password)
					.queryParam(Constants.QP_USERNAME, username)
					.queryParam(Constants.QP_IMAGEID, 107365);
		}
		OGImMMD out = target.request().get(OGImMMD.class);

		assertThat(out.getET(), is(Constants.NONE_EXISTING_CONTENT_MESSAGE));
	}

}
