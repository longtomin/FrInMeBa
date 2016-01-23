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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
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

import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.TestConfig;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestDownloadImage extends JerseyTest {

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

	final static String functionurl = "image/download";

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

	public int insertImage() {
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
		return helper.InsertFixedImage();
	}

	public void deleteImage(int in) {
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
	public void testDownloadImageUpNoValues() {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient().target(
					TestConfig.URL + functionurl);
		} else {
			target = target(functionurl);
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		assertThat(status, is(404));
	}

	@Test
	public void testDownloadImageUser() {
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl).path(username);
		} else {
			target = target(functionurl).path(username);
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		assertThat(status, is(404));
	}

	@Test
	public void testDownloadImagePassword() {
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl).path(password);
		} else {
			target = target(functionurl).path(password);
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		assertThat(status, is(404));
	}

	@Test
	public void testDownloadImageImageID() {
		int imageid = insertImage();
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl)
					.path(String.valueOf(imageid));
		} else {
			target = target(functionurl).path(String.valueOf(imageid));
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		assertThat(status, is(404));
		deleteImage(imageid);
	}

	@Test
	public void testDownloadImageUserPassword() {
		int imageid = insertImage();
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl).path(username)
					.path(password);
		} else {
			target = target(functionurl).path(username).path(password);
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		assertThat(status, is(404));
		deleteImage(imageid);
	}

	@Test
	public void testDownloadImageUserImageID() {
		int imageid = insertImage();
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl).path(username)
					.path(String.valueOf(imageid));
		} else {
			target = target(functionurl).path(username).path(
					String.valueOf(imageid));
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		assertThat(status, is(404));
		deleteImage(imageid);
	}

	@Test
	public void testDownloadImagePasswordImageID() {
		int imageid = insertImage();
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl).path(password)
					.path(String.valueOf(imageid));
		} else {
			target = target(functionurl).path(password).path(
					String.valueOf(imageid));
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		assertThat(status, is(404));
		deleteImage(imageid);
	}

	@Test
	public void testDownloadImageEncodingErrorUser() {
		int imageid = insertImage();
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl).path("1234$")
					.path(password).path(String.valueOf(imageid));
		} else {
			target = target(functionurl).path("1234$").path(password)
					.path(String.valueOf(imageid));
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		if (TestConfig.remote) {
			assertThat(status, is(204));
		} else {
			assertThat(status, is(404));
		}
		deleteImage(imageid);
	}

	@Test
	public void testDownloadImageEncodingErrorPassword() {
		int imageid = insertImage();
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl).path(username)
					.path("1234$").path(String.valueOf(imageid));
		} else {
			target = target(functionurl).path(username).path("1234$")
					.path(String.valueOf(imageid));
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		if (TestConfig.remote) {
			assertThat(status, is(204));
		} else {
			assertThat(status, is(404));
		}
		deleteImage(imageid);
	}

	// TODO : Test off temporarily
	// @Test
	public void testDownloadImageUserPasswordImageID() {
		int imageid = insertImage();
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl).path(username)
					.path(password).path(String.valueOf(imageid));
		} else {
			target = target(functionurl).path(username).path(password)
					.path(String.valueOf(imageid));
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		assertThat(status, is(200));
		deleteImage(imageid);
	}

	@Test
	public void testDownloadImageUserWrongPasswordImageID() {
		int imageid = insertImage();
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder
					.newClient()
					.target(TestConfig.URL + functionurl)
					.path(username)
					.path(Base64.encodeBase64String("XXX".getBytes(Charset
							.forName(Constants.CHARACTERSET))))
					.path(String.valueOf(imageid));
		} else {
			target = target(functionurl)
					.path(username)
					.path(Base64.encodeBase64String("XXX".getBytes(Charset
							.forName(Constants.CHARACTERSET))))
					.path(String.valueOf(imageid));
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		if (TestConfig.remote) {
			assertThat(status, is(204));
		} else {
			assertThat(status, is(404));
		}
		deleteImage(imageid);
	}

	@Test
	public void testDownloadImageUserPasswordWrongImageID() {
		WebTarget target;

		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + functionurl).path(username)
					.path(password).path(String.valueOf(14325));
		} else {
			target = target(functionurl).path(username).path(password)
					.path(String.valueOf(14325));
		}
		Response rsp = target.request("image/jpeg").accept("image/jpeg").head();
		int status = rsp.getStatus();

		if (TestConfig.remote) {
			assertThat(status, is(204));
		} else {
			assertThat(status, is(404));
		}

	}
}