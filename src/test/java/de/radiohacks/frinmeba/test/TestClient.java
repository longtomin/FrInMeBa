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
package de.radiohacks.frinmeba.test;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import de.radiohacks.frinmeba.modelshort.OAdUC;
import de.radiohacks.frinmeba.modelshort.OAuth;
import de.radiohacks.frinmeba.modelshort.OCrCh;
import de.radiohacks.frinmeba.modelshort.OLiCh;
import de.radiohacks.frinmeba.modelshort.OLiUs;
import de.radiohacks.frinmeba.modelshort.OSTeM;
import de.radiohacks.frinmeba.modelshort.OSiUp;
import de.radiohacks.frinmeba.services.Constants;
import de.radiohacks.frinmeba.services.ServiceImpl;
import de.radiohacks.frinmeba.test.database.createDatabaseTables;
import de.radiohacks.frinmeba.test.database.dropDatabaseTables;
import de.radiohacks.frinmeba.test.database.helperDatabase;

public class TestClient extends JerseyTest {

	// Anzahl der User die anzulegen sind, muss mit usernames Anzahl
	// übereinstimmen.
	final static int numUsers = 5;

	// Username welche anzulegen sind, erster Wert = username, zweiter Wert =
	// Passwort, dritter Wert = Email Adresse
	final static String[][] newusers = {
			{ "Test1", "Test1", "Test1@frinme.org" },
			{ "Test2", "Test2", "Test2@frinme.org" },
			{ "Test3", "Test3", "Test3@frinme.org" },
			{ "Test4", "Test4", "Test4@frinme.org" },
			{ "Test5", "Test5", "Test5@frinme.org" } };

	// Array mit den azulegenden 1 zu 1 Chats, erster Wert ist der Eigentümer
	// /Anleger des Chats
	// zweiter wert ist der Name des Chats
	// dritter Wert ist der hinzuzufügende User zum Chat
	// Es wird angenommen, dass beim Eigentümer und hizuzufügenden User der
	// Username und das Passwort identisch sind.
	// Wenn nicht sind pro Eintrag zwei weitere Einträge mit den dazugehörigen
	// Passwörter hinzuzufügen und die CreateChat Funktion ist zu äSndern.
	final static String[][] SingleChats = new String[][] {
			{ "Test1", "Test1 1to2", "Test2" },
			{ "Test1", "Test1 1to3", "Test3" },
			{ "Test3", "Test3 3to5", "Test5" },
			{ "Test5", "Test5 5to2", "Test2" } };

	// ist das erste Array für die User welche als Mitnutzer (nicht Eigentümer)
	// im Chat enthalten sein sollen.
	// Eigentümer des Chats ist Test2
	final static String[] MultiChatUsers1 = { "Test1", "Test3", "Test4" };

	// ist das zweite Array für die User welche als Mitnutzer (nicht Eigentümer)
	// im Chat enthalten sein sollen.
	// Eigentümer des chats ist Test4
	final static String[] MultiChatUsers2 = { "Test1", "Test2", "Test3",
			"Test5" };

	// Wird in der Create Chat gefüllt und dann in der AddUserToChat ausgelesen
	// um die richtigen User in den richtigen Chats zu haben.
	// Es müssten 6 Elemente nach dem StartCreateSingeleChat und
	// StartCreateMultiChat vorhanden sein.
	List<DataAddUserToChat> AddUsers = new ArrayList<DataAddUserToChat>(1);

	int[] UserIDs = new int[numUsers];

	private helperDatabase help = new helperDatabase();

	// getTestContainerFactory und configureDeployment auskommentieren wenn
	// remote Host in URL oben als Ziel gewählt werden soll.

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

	// configure auskommentieren wenn localhost als Ziel gewählt werden soll.
	/*
	 * @Override protected Application configure() { ResourceConfig rc = new
	 * ResourceConfig(ServiceImpl.class); return rc; }
	 */

	@BeforeClass
	public static void prepareDB() {
		dropDatabaseTables drop = new dropDatabaseTables();
		drop.dropTable();
		createDatabaseTables create = new createDatabaseTables();
		create.createTable();
	}

	// Generelle Testfunktion welche von junit ausgeführt wird.
	// Hier werden alle Subfunktionen der Reihe nach aufgerufen.
	@Test
	public void test() {
		StartSignup();
		StartActivate();
		StartAuthenticate();
		StartCreateChats();
		StartCreateMultiChat();
		StartAddUsersToChat();
	}

	private void StartAuthenticate() {
		for (int i = 0; i < newusers.length; i++) {
			OAuth out = Authenticate(newusers[i][0], newusers[i][1]);
			if (out != null) {
				if (out.getET() == null || out.getET().isEmpty()) {
					UserIDs[i] = out.getUID();
				}
			}
		}
	}

	private void StartSignup() {
		for (int i = 0; i < newusers.length; i++) {
			OSiUp out = Signup(newusers[i][0], newusers[i][1], newusers[i][2]);
			if (out != null) {
				if (out.getET() == null || out.getET().isEmpty()) {
					UserIDs[i] = out.getUID();
				} else if (out.getET().equalsIgnoreCase(
						Constants.USER_ALREADY_EXISTS)) {
					UserIDs[i] = out.getUID();
				}
			}
		}
	}

	private void StartActivate() {
		for (int i = 0; i < newusers.length; i++) {
			help.ActivateUser(UserIDs[i]);
		}
	}

	private void StartCreateChats() {

		for (int i = 0; i < SingleChats.length; i++) {
			OCrCh out = CreateChat(SingleChats[i][0], SingleChats[i][0],
					SingleChats[i][1]);

			if (out != null) {
				if (out.getET() == null || out.getET().isEmpty()) {
					DataAddUserToChat d = new DataAddUserToChat(
							SingleChats[i][0], SingleChats[i][0], out.getCID());
					OAuth auth = Authenticate(SingleChats[i][2],
							SingleChats[i][2]);
					d.Users.add(auth.getUID());
					AddUsers.add(d);
				}
			}
		}
	}

	private void StartCreateMultiChat() {

		OCrCh out1 = CreateChat("Test2", "Test2", "Test1 2to1_3_4");

		if (out1 != null) {
			if (out1.getET() == null || out1.getET().isEmpty()) {

				DataAddUserToChat d = new DataAddUserToChat("Test2", "Test2",
						out1.getCID());
				for (int i = 0; i < MultiChatUsers1.length; i++) {
					OAuth auth = Authenticate(MultiChatUsers1[i],
							MultiChatUsers1[i]);
					d.Users.add(auth.getUID());
				}
				AddUsers.add(d);
			}
		}
		OCrCh out2 = CreateChat("Test4", "Test4", "Test4 4to1_2_3_5");

		if (out2 != null) {
			if (out2.getET() == null || out2.getET().isEmpty()) {

				DataAddUserToChat d = new DataAddUserToChat("Test4", "Test4",
						out2.getCID());
				for (int i = 0; i < MultiChatUsers2.length; i++) {
					OAuth auth = Authenticate(MultiChatUsers2[i],
							MultiChatUsers2[i]);
					d.Users.add(auth.getUID());
				}
				AddUsers.add(d);
			}
		}
	}

	private void StartAddUsersToChat() {
		for (DataAddUserToChat d : AddUsers) {
			for (int uid : d.Users) {
				OAdUC out = AddUserToChat(d.OwnUser, d.OwnnPassword, uid,
						d.ChatID);
				if (out != null) {
					if (out.getET() == null || out.getET().isEmpty()) {
						String x = out.getR();
					}
				}
			}
		}
	}

	private OAuth Authenticate(String username, String password) {

		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/authenticate")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password);
		} else {
			target = target("user/authenticate").queryParam(
					Constants.QPusername, username).queryParam(
					Constants.QPpassword, password);
		}
		return target.request().get(OAuth.class);
	}

	private OSiUp Signup(String username, String password, String email) {

		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/signup")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, email);
		} else {
			target = target("user/signup")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPemail, email);
		}
		return target.request().get(OSiUp.class);
	}

	private OCrCh CreateChat(String username, String password, String chatname) {

		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/createchat")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatname, chatname);
		} else {
			target = target("user/createchat")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatname, chatname);
		}
		return target.request().get(OCrCh.class);
	}

	private OLiUs ListUser(String username, String password, String search) {

		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/listuser")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatname, search);
		} else {
			target = target("user/listuser")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatname, search);

		}
		return target.request().get(OLiUs.class);
	}

	private OLiCh ListChat(String username, String password) {
		WebTarget target;
		if (TestConfig.remote) {

			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/listchat")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password);
		} else {
			target = target("user/listchat").queryParam(Constants.QPusername,
					username).queryParam(Constants.QPpassword, password);
		}
		return target.request().get(OLiCh.class);

	}

	private OAdUC AddUserToChat(String username, String password, int userid,
			int chatid) {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/addusertochat")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		} else {
			target = target("user/addusertochat")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPchatid, chatid)
					.queryParam(Constants.QPuserid, userid);
		}
		return target.request().get(OAdUC.class);
	}

	private OSTeM SendTextMessage(String username, String password,
			String TextMessage) {
		WebTarget target;
		if (TestConfig.remote) {
			target = ClientBuilder.newClient()
					.target(TestConfig.URL + "user/sendtextmessage")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPtextmessage, TextMessage);
		} else {
			target = target("user/sendtextmessage")
					.queryParam(Constants.QPusername, username)
					.queryParam(Constants.QPpassword, password)
					.queryParam(Constants.QPtextmessage, TextMessage);

		}
		return target.request().get(OSTeM.class);
	}

	private class DataAddUserToChat {
		String OwnUser;
		String OwnnPassword;
		int ChatID;
		List<Integer> Users = new ArrayList<Integer>();

		public DataAddUserToChat(String inownuser, String inownpw, int cid) {
			OwnUser = inownuser;
			OwnnPassword = inownpw;
			ChatID = cid;
		}
	}
}