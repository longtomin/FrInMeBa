/**
 * Copyright @ 2015, Thomas Schreiner, thomas1.schreiner@googlemail.com
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
package de.radiohacks.frinmeba.services;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.glassfish.jersey.internal.util.Base64;

import de.radiohacks.frinmeba.database.Check;
import de.radiohacks.frinmeba.model.jaxb.C;
import de.radiohacks.frinmeba.model.jaxb.CNM;
import de.radiohacks.frinmeba.model.jaxb.IAckCD;
import de.radiohacks.frinmeba.model.jaxb.IAckMD;
import de.radiohacks.frinmeba.model.jaxb.IAdUC;
import de.radiohacks.frinmeba.model.jaxb.ICrCh;
import de.radiohacks.frinmeba.model.jaxb.IDMFC;
import de.radiohacks.frinmeba.model.jaxb.IDeCh;
import de.radiohacks.frinmeba.model.jaxb.IFMFC;
import de.radiohacks.frinmeba.model.jaxb.IGImM;
import de.radiohacks.frinmeba.model.jaxb.IGMI;
import de.radiohacks.frinmeba.model.jaxb.IGTeM;
import de.radiohacks.frinmeba.model.jaxb.IGViM;
import de.radiohacks.frinmeba.model.jaxb.IICIc;
import de.radiohacks.frinmeba.model.jaxb.IIMIC;
import de.radiohacks.frinmeba.model.jaxb.IIUIc;
import de.radiohacks.frinmeba.model.jaxb.ILiUs;
import de.radiohacks.frinmeba.model.jaxb.IReUC;
import de.radiohacks.frinmeba.model.jaxb.ISIcM;
import de.radiohacks.frinmeba.model.jaxb.ISImM;
import de.radiohacks.frinmeba.model.jaxb.ISShT;
import de.radiohacks.frinmeba.model.jaxb.ISTeM;
import de.radiohacks.frinmeba.model.jaxb.ISU;
import de.radiohacks.frinmeba.model.jaxb.ISViM;
import de.radiohacks.frinmeba.model.jaxb.ISiUp;
import de.radiohacks.frinmeba.model.jaxb.M;
import de.radiohacks.frinmeba.model.jaxb.MI;
import de.radiohacks.frinmeba.model.jaxb.MIB;
import de.radiohacks.frinmeba.model.jaxb.OAckCD;
import de.radiohacks.frinmeba.model.jaxb.OAckMD;
import de.radiohacks.frinmeba.model.jaxb.OAdUC;
import de.radiohacks.frinmeba.model.jaxb.OCN;
import de.radiohacks.frinmeba.model.jaxb.OCrCh;
import de.radiohacks.frinmeba.model.jaxb.ODMFC;
import de.radiohacks.frinmeba.model.jaxb.ODeCh;
import de.radiohacks.frinmeba.model.jaxb.OFMFC;
import de.radiohacks.frinmeba.model.jaxb.OGImM;
import de.radiohacks.frinmeba.model.jaxb.OGMI;
import de.radiohacks.frinmeba.model.jaxb.OGTeM;
import de.radiohacks.frinmeba.model.jaxb.OGViM;
import de.radiohacks.frinmeba.model.jaxb.OICIc;
import de.radiohacks.frinmeba.model.jaxb.OIMIC;
import de.radiohacks.frinmeba.model.jaxb.OIUIc;
import de.radiohacks.frinmeba.model.jaxb.OLiCh;
import de.radiohacks.frinmeba.model.jaxb.OLiUs;
import de.radiohacks.frinmeba.model.jaxb.OReUC;
import de.radiohacks.frinmeba.model.jaxb.OSIcM;
import de.radiohacks.frinmeba.model.jaxb.OSImM;
import de.radiohacks.frinmeba.model.jaxb.OSShT;
import de.radiohacks.frinmeba.model.jaxb.OSTeM;
import de.radiohacks.frinmeba.model.jaxb.OSU;
import de.radiohacks.frinmeba.model.jaxb.OSViM;
import de.radiohacks.frinmeba.model.jaxb.OSiUp;
import de.radiohacks.frinmeba.model.jaxb.OU;
import de.radiohacks.frinmeba.model.jaxb.ShT;
import de.radiohacks.frinmeba.model.jaxb.U;

public class User {

	private int id = 0;
	private Connection con;
	private String LastError = new String();

	private static final Logger LOGGER = Logger.getLogger(User.class);

	public User(Connection conin, String uname) {
		LOGGER.debug("Start User with Connection and Username = " + uname);
		this.con = conin;
		fillUserinfo(base64Decode(uname));
		LOGGER.debug("End User with Connection and Username = " + uname);
	}

	public User(Connection conin) {
		LOGGER.debug("Start User with Connection without Username");
		this.con = conin;
		LOGGER.debug("End User with Connection without Username");
	}

	public int getID() {
		return id;
	}

	public String getLastError() {
		return LastError;
	}

	public String base64Encode(String token) {
		byte[] encodedBytes = Base64.encode(token.getBytes());
		return new String(encodedBytes);
	}

	public String base64Decode(String token) {
		byte[] decodedBytes = Base64.decode(token.getBytes());
		return new String(decodedBytes);
	}

	public boolean auth(String user, String pw) {
		LOGGER.debug("Start auth with user = " + user + " and pw = " + pw);

		boolean ret = false;

		ResultSet resultSet = null;
		Statement statement = null;

		try {
			Check actcheck = new Check(con);
			if (actcheck.checkValueMust(user)) {
				if (actcheck.checkValueMust(pw)) {

					statement = con.createStatement();
					resultSet = statement
							.executeQuery("select ID, Username, Password, Active from Users where B64Username = '"
									+ user + "'");
					String encodedpw = base64Decode(pw);
					if (resultSet.next()) {
						if (resultSet.getBoolean("Active")) {
							String dbpw = resultSet.getString("Password");
							if (dbpw.equals(encodedpw)) {
								ret = true;
								int ownUSerID = resultSet.getInt("ID");

								long currentTime = System.currentTimeMillis() / 1000L;
								String updateMessage = "UPDATE Users SET AuthenticationTime = ? where ID = ?";
								PreparedStatement prepSt = con
										.prepareStatement(updateMessage);
								prepSt.setLong(1, currentTime);
								prepSt.setInt(2, ownUSerID);
								prepSt.executeUpdate();
							} else {
								LastError = Constants.WRONG_PASSWORD;
							}
						} else {
							LastError = Constants.USER_NOT_ACTIVE;
						}
					} else {
						LastError = Constants.NONE_EXISTING_USER;
					}
				} else {
					LastError = actcheck.getLastError();
				}
			} else {
				LastError = actcheck.getLastError();
			}
		} catch (SQLException e) {
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End authenticate with returnvalue " + ret);
		return ret;
	}

	public void fillUserinfo(String username) {
		LOGGER.debug("Start fillUserinfo with Username = " + username);
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			resultSet = statement
					.executeQuery("select ID from Users where Username = '"
							+ username + "'");

			if (resultSet.next()) {
				this.id = resultSet.getInt("ID");
			}
		} catch (SQLException e) {
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End fillUserinfo with id = " + this.id);
	}

	public void signUp(ISiUp in, OSiUp out) {
		LOGGER.debug("Start signUp with In = " + in.toString());

		int key = -1;
		ResultSet rsfind = null;
		ResultSet rscreate = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			rsfind = statement
					.executeQuery("select ID from Users where B64Username = '"
							+ in.getUN() + "' limit 1");

			if (rsfind.next()) {
				out.setET(Constants.USER_ALREADY_EXISTS);
				out.setUID(rsfind.getInt("ID"));
			} else {
				long currentTime = System.currentTimeMillis() / 1000L;

				statement
						.executeUpdate(
								"insert into Users(Username, B64Username, Password, Email, SignupDate, Status, AuthenticationTime) values ('"
										+ base64Decode(in.getUN())
										+ "', '"
										+ in.getUN()
										+ "', '"
										+ in.getPW()
										+ "', '"
										+ in.getE()
										+ "', '"
										+ currentTime
										+ "', '"
										+ "0"
										+ "', '"
										+ currentTime + "')",
								Statement.RETURN_GENERATED_KEYS);
				rscreate = statement.getGeneratedKeys();
				if (rscreate != null && rscreate.next()) {
					key = rscreate.getInt(1);
					out.setUID(key);
				}
				out.setUN(base64Decode(in.getUN()));
				out.setSU("SUCCESSFUL");
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (rsfind != null) {
					rsfind.close();
				}
				if (rscreate != null) {
					rscreate.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End signUp with Out = " + out.toString());
	}

	public void listUser(ILiUs in, OLiUs out) {
		LOGGER.debug("Start listUser with In = " + in.toString());
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			if (in.getS() != null && !in.getS().isEmpty()) {
				resultSet = statement
						.executeQuery("select * from Users where Active = 1 and Username like '%"
								+ in.getS() + "%'");
			} else {
				resultSet = statement
						.executeQuery("select * from Users where Active = 1");
			}

			while (resultSet.next()) {
				if (this.id != resultSet.getInt("Id")) {
					U u = new U();
					u.setUN(resultSet.getString("Username"));
					u.setE(resultSet.getString("Email"));
					u.setUID(resultSet.getInt("Id"));
					u.setLA(resultSet.getLong("AuthenticationTime"));
					u.setICID(resultSet.getInt("IconID"));
					out.getU().add(u);
				}
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End listUser with Out = " + out.toString());
	}

	public void listChat(OLiCh out) {
		LOGGER.debug("Start listChat");
		ResultSet rsusertochats = null;
		Statement stusertochats = null;
		ResultSet rschats = null;
		Statement stchats = null;
		ResultSet rsusers = null;
		Statement stusers = null;

		try {
			stusertochats = con.createStatement();
			rsusertochats = stusertochats
					.executeQuery("select ChatID from UserToChats where UserID= "
							+ this.id);

			if (rsusertochats != null) {
				while (rsusertochats.next()) {

					C outchat = new C();
					stchats = con.createStatement();
					rschats = stchats
							.executeQuery("Select Chatname, OwningUserID, IconID from Chats where ID = "
									+ rsusertochats.getInt("ChatID"));

					int rscount = 0;
					if (rschats != null) {
						while (rschats.next()) {
							outchat.setCID(rsusertochats.getInt("ChatID"));
							outchat.setCN(rschats.getString("Chatname"));
							outchat.setICID(rschats.getInt("IconID"));
							OU outOwingUser = new OU();
							outOwingUser
									.setOUID(rschats.getInt("OwningUserID"));

							stusers = con.createStatement();
							rsusers = stusers
									.executeQuery("Select Username from Users where ID = "
											+ rschats.getInt("OwningUserID"));
							if (rsusers != null) {
								while (rsusers.next()) {
									outOwingUser.setOUN(rsusers
											.getString("Username"));
								}
							}
							outchat.setOU(outOwingUser);
							rscount++;
						}
					}
					if (rscount > 0) {
						out.getC().add(outchat);
					} else {
						out.setET(Constants.NO_ACTIVE_CHATS);
					}
				}
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (rsusertochats != null) {
					rsusertochats.close();
				}
				if (stusertochats != null) {
					stusertochats.close();
				}
				if (rschats != null) {
					rschats.close();
				}
				if (stchats != null) {
					stchats.close();
				}
				if (rsusers != null) {
					rsusers.close();
				}
				if (stusers != null) {
					stusers.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End listChat");
	}

	public void createChat(ICrCh in, OCrCh out, String username) {
		LOGGER.debug("Start createChat with In = " + in.toString());
		int key = -1;
		ResultSet resultSet = null;
		Statement stchat = null;
		Statement stuser = null;

		try {

			stchat = con.createStatement();
			// TODO Check first if Chat already exists, idempotent?

			/* First we create a chat room */
			stchat.executeUpdate(
					"insert into Chats(Chatname, OwningUserId) values ('"
							+ in.getCN() + "', '" + this.id + "')",
					Statement.RETURN_GENERATED_KEYS);
			resultSet = stchat.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				key = resultSet.getInt(1);
			}
			out.setCN(in.getCN());
			out.setCID(key);

			/* Now we have to add the Owning user to his own chat room */
			stuser = con.createStatement();
			stuser.executeUpdate("insert into UserToChats(UserID, ChatID) values ('"
					+ this.id + "', '" + key + "')");
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (stchat != null) {
					stchat.close();
				}
				if (stuser != null) {
					stuser.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End createChat with Out = " + out.toString());
	}

	// Done In & Out
	public void addUserToChat(IAdUC in, OAdUC out) {
		LOGGER.debug("Start addUserToChat with In = " + in.toString());
		ResultSet resultSet = null;
		ResultSet resultSet2 = null;
		Statement statement = null;

		if (this.id == in.getUID()) {
			out.setET(Constants.CHAT_OWNER_NOT_ADDED);
		} else {
			try {
				statement = con.createStatement();

				/* Check first if the Owning User is sending the Request */
				resultSet = statement
						.executeQuery("select OwningUserID from Chats where ID = '"
								+ in.getCID() + "'");

				if (resultSet.next()) {
					if (this.id == resultSet.getInt("OwningUserID")) {
						/* The Owning User is adding the new User to the Chat */
						/* Check first if user is already in the Chat */
						resultSet2 = statement
								.executeQuery("select UserID from UserToChats where ChatID = '"
										+ in.getCID() + "'");

						if (resultSet2.next()) {
							boolean dup = false;
							while (resultSet2.next()) {
								if (in.getUID() == resultSet2.getInt("UserID")) {
									dup = true;
								}
							}
							if (dup == true) {
								out.setET(Constants.USER_ALREADY_IN_CHAT);
							} else {
								/*
								 * User not in the Chat so add the User to the
								 * Chat Room
								 */
								statement
										.executeUpdate("insert into UserToChats(UserID, ChatID) values ('"
												+ in.getUID()
												+ "', '"
												+ in.getCID() + "')");

								out.setR(Constants.USER_ADDED);
							}
						} else {
							/*
							 * User not in the Chat so add the User to the Chat
							 * Room
							 */
							statement
									.executeUpdate("insert into UserToChats(UserID, ChatID) values ('"
											+ in.getUID()
											+ "', '"
											+ in.getCID() + "')");

							out.setR(Constants.USER_ADDED);
						}
					} else {
						out.setET(Constants.NOT_CHAT_OWNER);
					}
				}
			} catch (SQLException e) {
				out.setET(Constants.DB_ERROR);
				LOGGER.error(e);
			} finally {
				try {
					if (resultSet != null) {
						resultSet.close();
					}
					if (resultSet2 != null) {
						resultSet2.close();
					}
					if (statement != null) {
						statement.close();
					}
				} catch (SQLException e) {
					LOGGER.error(e);
				}
			}
		}
		LOGGER.debug("End addUserToChat with Out = " + out.toString());
	}

	public void sendTextMessage(ISTeM in, OSTeM out) {
		LOGGER.debug("Start sendTextMessage with In = " + in.toString());
		int key = -1;

		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			// TODO first check if Message already exists, idempotent?
			/* First we create a chat room */
			statement.executeUpdate(
					"insert into Text (Text) values ('" + in.getTM() + "')",
					Statement.RETURN_GENERATED_KEYS);
			resultSet = statement.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				key = resultSet.getInt(1);
			}
			out.setTID(key);
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End sendTextMessage with Out = " + out.toString());
	}

	public void sendImageMessage(ISImM in, OSImM out) {
		LOGGER.debug("Start sendImageMessage with In = " + in.toString());
		int key = -1;
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			// TODO first check if Message already exists, idempotent?
			/* First we create a chat room */
			statement.executeUpdate(
					"insert into Image (Image, MD5Sum) values ('" + in.getImM()
							+ "', '" + in.getImMD5() + "')",
					Statement.RETURN_GENERATED_KEYS);
			resultSet = statement.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				key = resultSet.getInt(1);
			}
			out.setImID(key);
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End sendImageMessage with Out = " + out.toString());
	}

	public void sendVideoMessage(ISViM in, OSViM out) {
		LOGGER.debug("Start sendVideoMessage with In = " + in.toString());
		int key = -1;
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			// TODO first check if Message already exists, idempotent?
			/* First we create a chat room */
			statement.executeUpdate(
					"insert into Video (Video, MD5Sum) values ('" + in.getVM()
							+ "', '" + in.getVMD5() + "')",
					Statement.RETURN_GENERATED_KEYS);
			resultSet = statement.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				key = resultSet.getInt(1);
			}
			out.setVID(key);
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End sendVideoMessage with Out = " + out.toString());
	}

	public void removeUserFromChat(IReUC in, OReUC out) {
		LOGGER.debug("Start removeUserFromChat with In = " + in.toString());

		ResultSet resultSetU2Cid = null;
		Statement statementU2Cid = null;
		Statement statementDelete = null;
		ResultSet resultDelete = null;
		ResultSet resultOwningUser = null;
		Statement statementOwningUser = null;

		if (this.id == in.getUID()) {
			out.setET(Constants.CHAT_OWNER_NOT_REMOVED);
		} else {
			try {
				/* Check first if the Owning User is sending the Request */
				statementOwningUser = con.createStatement();
				resultOwningUser = statementOwningUser
						.executeQuery("select OwningUserID from Chats where ID = '"
								+ in.getCID() + "'");

				if (resultOwningUser.next()) {
					if (this.id == resultOwningUser.getInt("OwningUserID")) {

						statementU2Cid = con.createStatement();
						resultSetU2Cid = statementU2Cid
								.executeQuery("select id from UserToChats where ChatID = '"
										+ in.getCID()
										+ "' and UserID = '"
										+ in.getUID() + "'");

						IDMFC idelete = new IDMFC();
						ODMFC odelete = new ODMFC();
						boolean deleteError = false;

						if (resultSetU2Cid != null) {
							while (resultSetU2Cid.next()) {
								statementDelete = con.createStatement();
								resultDelete = statementDelete
										.executeQuery("select ID from Messages where UsertoChatID = '"
												+ resultSetU2Cid.getInt("ID")
												+ "'");
								if (resultDelete != null) {
									while (resultDelete.next()) {

										idelete.setMID(resultDelete
												.getInt("ID"));
										deleteMessageFromChat(idelete, odelete);

										if (odelete.getET() != null
												&& !odelete.getET().isEmpty()) {
											deleteError = true;
										}
									}
								}
							}
						}
						if (deleteError == false) {
							out.setR("REMOVED");
						}
					} else {
						out.setET(Constants.NOT_CHAT_OWNER);
					}
				}
			} catch (SQLException e) {
				out.setET(Constants.DB_ERROR);
				LOGGER.error(e);
			} finally {
				try {
					if (resultSetU2Cid != null) {
						resultSetU2Cid.close();
					}
					if (statementU2Cid != null) {
						statementU2Cid.close();
					}
					if (statementDelete != null) {
						statementDelete.close();
					}

				} catch (SQLException e) {
					LOGGER.error(e);
				}
			}
		}
		LOGGER.debug("End removeUserFromChat with Out = " + out.toString());
	}

	@SuppressWarnings("resource")
	public void insertMessageIntoChat(IIMIC in, OIMIC out) {
		LOGGER.debug("Start insertMessageIntoChat with In = " + in.toString());
		int key = -1;
		ResultSet resultSet = null;
		Statement statement = null;
		ResultSet resultSet2 = null;
		Statement statement2 = null;
		List<Integer> generatedRows = new ArrayList<Integer>(1);
		int originMsgID = 0;

		try {
			statement = con.createStatement();
			boolean typefound = false;
			long currentTime = System.currentTimeMillis() / 1000L;

			// TODO Check if message is already inserted in the Chat,
			// idempotent?
			/* First we search all Users in the given Chat */
			resultSet = statement
					.executeQuery("select id, UserID from UserToChats where ChatID = '"
							+ in.getCID() + "'");

			if (resultSet != null) {
				while (resultSet.next()) {
					if (in.getMT().equalsIgnoreCase(Constants.TYP_TEXT)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, TextMsgID) values ('"
												+ this.id
												+ "', '"
												+ Constants.TYP_TEXT
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '" + in.getMID() + "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.id == resultSet.getInt("UserID")) {
								originMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					if (in.getMT().equalsIgnoreCase(Constants.TYP_IMAGE)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, ImageMsgID) values ('"
												+ this.id
												+ "', '"
												+ Constants.TYP_IMAGE
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '" + in.getMID() + "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.id == resultSet.getInt("UserID")) {
								originMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					if (in.getMT().equalsIgnoreCase(Constants.TYP_CONTACT)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, ContactMsgID) values ('"
												+ this.id
												+ "', '"
												+ Constants.TYP_CONTACT
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '" + in.getMID() + "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.id == resultSet.getInt("UserID")) {
								originMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					if (in.getMT().equalsIgnoreCase(Constants.TYP_LOCATION)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, LocationMsgID) values ('"
												+ this.id
												+ "', '"
												+ Constants.TYP_LOCATION
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '" + in.getMID() + "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.id == resultSet.getInt("UserID")) {
								originMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					if (in.getMT().equalsIgnoreCase(Constants.TYP_FILE)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, FileMsgID) values ('"
												+ this.id
												+ "', '"
												+ Constants.TYP_FILE
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '" + in.getMID() + "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.id == resultSet.getInt("UserID")) {
								originMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					if (in.getMT().equalsIgnoreCase(Constants.TYP_VIDEO)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, VideoMsgID) values ('"
												+ this.id
												+ "', '"
												+ Constants.TYP_VIDEO
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '" + in.getMID() + "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.id == resultSet.getInt("UserID")) {
								originMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					int tmpi = resultSet.getInt("UserID");
					/*
					 * Message inserted for Restore but Readtimestamp set to
					 * SendTimestamp
					 */
					if (tmpi == this.id) {

						String updateReadMessage = "UPDATE Messages SET ReadTimestamp = ? where ID = ?";
						PreparedStatement prepReadSt = con
								.prepareStatement(updateReadMessage);
						prepReadSt.setLong(1, currentTime);
						prepReadSt.setInt(2, key);
						prepReadSt.executeUpdate();
						String updateShowMessage = "UPDATE Messages SET ShowTimestamp = ? where ID = ?";
						PreparedStatement prepShowSt = con
								.prepareStatement(updateShowMessage);
						prepShowSt.setLong(1, currentTime);
						prepShowSt.setInt(2, key);
						prepShowSt.executeUpdate();
					}
				}
				if (typefound == true) {
					out.setSdT(currentTime);
					out.setMID(originMsgID);
					// Now set the originMsgID to group all Messages for the
					// showTimestamp
					String updateOriginMsgID = "UPDATE Messages SET OriginMsgID = ? where ID = ?";
					for (Integer id1 : generatedRows) {
						PreparedStatement prepOriginSt = con
								.prepareStatement(updateOriginMsgID);
						prepOriginSt.setLong(1, originMsgID);
						prepOriginSt.setInt(2, id1);
						prepOriginSt.executeUpdate();
					}

				} else {
					out.setET(Constants.TYPE_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (resultSet2 != null) {
					resultSet2.close();
				}
				if (statement2 != null) {
					statement2.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End insertMessageIntoChat with Out = " + out.toString());
	}

	public void getMessagesFromChat(IFMFC in, OFMFC out) {
		LOGGER.debug("Start getMessagesFromChat with In = " + in.toString());
		ResultSet resultSet = null;
		Statement statement = null;
		Statement st2 = null;
		ResultSet rs2 = null;
		Statement st3 = null;
		ResultSet rs3 = null;
		Statement sttotal = null;
		ResultSet rstotal = null;
		Statement stread = null;
		ResultSet rsread = null;
		Statement stshow = null;
		ResultSet rsshow = null;

		try {
			statement = con.createStatement();

			/* First we search all UsersToChat IDs with this.id and given Chatid */
			resultSet = statement
					.executeQuery("select id from UserToChats where ChatID = '"
							+ in.getCID() + "' and UserID = '" + this.id + "'");

			if (resultSet != null) {
				while (resultSet.next()) {
					String query = new String();
					long tmptimestamp = in.getRdT();
					if (in.getRdT() == 0) {
						query += "select * from Messages where UsertoChatID = "
								+ resultSet.getInt("ID")
								+ " and ReadTimeStamp = 0";
					} else {
						query += "select * from Messages where UsertoChatID = "
								+ resultSet.getInt("ID")
								+ " and (SendTimestamp > " + tmptimestamp
								+ " or ReadTimeStamp = 0)";
					}

					st2 = con.createStatement();
					rs2 = st2.executeQuery(query);
					if (rs2 != null) {
						while (rs2.next()) {
							M msg = new M();
							msg.setMID(rs2.getInt("ID"));
							msg.setMT(rs2.getString("MessageTyp"));
							msg.setSdT(rs2.getLong("SendTimestamp"));
							msg.setShT(rs2.getLong("ShowTimestamp"));
							msg.setOMID(rs2.getInt("OriginMsgID"));
							OU owingu = new OU();
							owingu.setOUID(rs2.getInt("OwningUserID"));

							st3 = con.createStatement();
							rs3 = st3
									.executeQuery("select Username from Users where ID = '"
											+ rs2.getInt("OwningUserID") + "'");

							if (rs3 != null) {
								while (rs3.next()) {
									owingu.setOUN(rs3.getString("Username"));
								}
							}

							msg.setOU(owingu);

							if (msg.getMT()
									.equalsIgnoreCase(Constants.TYP_TEXT)) {
								msg.setTMID(rs2.getInt("TextMsgID"));
							} else if (msg.getMT().equalsIgnoreCase(
									Constants.TYP_IMAGE)) {
								msg.setIMID(rs2.getInt("ImageMsgID"));
							} else if (msg.getMT().equalsIgnoreCase(
									Constants.TYP_CONTACT)) {
								msg.setCMID(rs2.getInt("ContactMsgID"));
							} else if (msg.getMT().equalsIgnoreCase(
									Constants.TYP_LOCATION)) {
								msg.setLMID(rs2.getInt("LocationMsgID"));
							} else if (msg.getMT().equalsIgnoreCase(
									Constants.TYP_FILE)) {
								msg.setFMID(rs2.getInt("FileMsgID"));
							} else if (msg.getMT().equalsIgnoreCase(
									Constants.TYP_VIDEO)) {
								msg.setVMID(rs2.getInt("VideoMsgID"));
							}

							/*
							 * Now set the Read Time Stamp
							 */
							if (rs2.getLong("ReadTimestamp") == 0) {
								long readTime = System.currentTimeMillis() / 1000L;

								String updateMessage = "UPDATE Messages SET TempReadTimestamp = ? where ID = ?";
								PreparedStatement prepSt = con
										.prepareStatement(updateMessage);
								prepSt.setLong(1, readTime);
								prepSt.setInt(2, rs2.getInt("ID"));
								prepSt.executeUpdate();
								msg.setRdT(readTime);
							} else {
								msg.setRdT(rs2.getLong("ReadTimestamp"));
							}
							out.getM().add(msg);
						}
					}
				}
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (st2 != null) {
					st2.close();
				}
				if (rs2 != null) {
					rs2.close();
				}
				if (st3 != null) {
					st3.close();
				}
				if (rs3 != null) {
					rs3.close();
				}
				if (sttotal != null) {
					sttotal.close();
				}
				if (rstotal != null) {
					rstotal.close();
				}
				if (stread != null) {
					stread.close();
				}
				if (rsread != null) {
					rsread.close();
				}
				if (stshow != null) {
					stshow.close();
				}
				if (rsshow != null) {
					rsshow.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End getMessagesFromChat with out = " + out.toString());
	}

	public void getTextMessages(IGTeM in, OGTeM out) {
		LOGGER.debug("Start getTextMessages with In = " + in.toString());
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();

			/* First we create a chat room */
			resultSet = statement
					.executeQuery("select Text from Text where ID = "
							+ in.getTextID());
			if (resultSet != null) {
				if (resultSet.next()) {
					out.setTM(resultSet.getString("Text"));
				}
			} else {
				out.setET(Constants.NONE_EXISTING_MESSAGE);
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End getTextMessages with Out = " + out.toString());
	}

	public void getImageMessages(IGImM in, OGImM out) {
		LOGGER.debug("Start getImageMessages with In = " + in.toString());
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();

			/* First we create a chat room */
			resultSet = statement
					.executeQuery("select Image, MD5Sum from Image where ID = "
							+ in.getIID());
			if (resultSet != null) {
				if (resultSet.next()) {
					out.setIM(resultSet.getString("Image"));
					out.setIMD5(resultSet.getString("MD5Sum"));
				}
			} else {
				out.setET(Constants.NONE_EXISTING_MESSAGE);
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End getImageMessages with Out = " + out.toString());
	}

	public void getVideoMessages(IGViM in, OGViM out) {
		LOGGER.debug("Start getImageMessages with In = " + in.toString());
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();

			/* First we create a chat room */
			resultSet = statement
					.executeQuery("select Video, MD5Sum from Video where ID = "
							+ in.getVID());
			if (resultSet != null) {
				if (resultSet.next()) {
					out.setVM(resultSet.getString("Video"));
					out.setVMD5(resultSet.getString("MD5Sum"));
				}
			} else {
				out.setET(Constants.NONE_EXISTING_MESSAGE);
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End getVideoMessages with Out = " + out.toString());
	}

	public void checkNew(OCN out) {
		LOGGER.debug("Start checkNew");

		ResultSet rsCheckNewChat = null;
		Statement stCheckNewChat = null;
		ResultSet rsCheckNewMessages = null;
		Statement stCheckNewMessages = null;
		ResultSet rsCheckNewMessagesChat = null;
		Statement stCheckNewMessagesChat = null;
		Statement stCheckNewChatUpdate = null;
		ResultSet rsCheckNewChatUpdate = null;

		try {
			stCheckNewChat = con.createStatement();

			// First we check for unreaded chats
			rsCheckNewChat = stCheckNewChat
					.executeQuery("SELECT c.Chatname, c.ID, u.Username, c.IconID, u.ID FROM "
							+ "Chats c, Users u WHERE c.OwningUserID = u.ID and c.id IN "
							+ "(SELECT ChatID FROM UserToChats WHERE UserID = "
							+ this.id + " and ReadTimestamp = 0)");
			if (rsCheckNewChat != null) {
				while (rsCheckNewChat.next()) {
					// We have a userToChatID now identifiy the Chat

					C oNC = new C();
					OU oNCOU = new OU();
					oNCOU.setOUID(rsCheckNewChat.getInt("u.ID"));
					oNCOU.setOUN(rsCheckNewChat.getString("u.Username"));
					oNC.setOU(oNCOU);
					oNC.setCID(rsCheckNewChat.getInt("c.ID"));
					oNC.setCN(rsCheckNewChat.getString("c.Chatname"));
					oNC.setICID(rsCheckNewChat.getInt("c.IconID"));

					out.getC().add(oNC);
				}
			}

			// Setze TempReadTimeStamp bei UserToChats
			String updateMessage = "UPDATE UserToChats SET TempReadTimestamp = ? where ID = ?";
			long readTime = System.currentTimeMillis() / 1000L;
			stCheckNewChatUpdate = con.createStatement();
			rsCheckNewChatUpdate = stCheckNewChatUpdate
					.executeQuery("SELECT * FROM UserToChats WHERE UserID = "
							+ this.id + " and ReadTimestamp = 0");
			if (rsCheckNewChatUpdate != null) {
				while (rsCheckNewChatUpdate.next()) {

					PreparedStatement prepSt = con
							.prepareStatement(updateMessage);
					prepSt.setLong(1, readTime);
					prepSt.setInt(2, rsCheckNewChatUpdate.getInt("ID"));
					prepSt.executeUpdate();
				}
			}

			stCheckNewMessages = con.createStatement();

			// First we check for unreaded chats
			rsCheckNewMessages = stCheckNewMessages
					.executeQuery("SELECT count(*), UserToChatID FROM Messages WHERE ReadTimestamp = 0 AND UsertoChatID IN (SELECT ID FROM UserToChats WHERE UserID = "
							+ this.id + ") group by UserToChatID");
			if (rsCheckNewMessages != null) {
				while (rsCheckNewMessages.next()) {
					// We have a userToChatID now identifiy the Chat

					CNM oNM = new CNM();
					oNM.setNOM(rsCheckNewMessages.getInt(1));

					stCheckNewMessagesChat = con.createStatement();

					// First we check for unreaded chats
					rsCheckNewMessagesChat = stCheckNewMessagesChat
							.executeQuery("SELECT Chatname, ID "
									+ "FROM Chats WHERE ID " + "IN ( "
									+ "SELECT ChatID " + "FROM UserToChats "
									+ "WHERE ID = "
									+ rsCheckNewMessages.getInt("UserToChatID")
									+ ")");
					if (rsCheckNewMessagesChat != null) {
						while (rsCheckNewMessagesChat.next()) {
							oNM.setCID(rsCheckNewMessagesChat.getInt("ID"));
							oNM.setCN(rsCheckNewMessagesChat
									.getString("Chatname"));
						}
					}
					out.getCNM().add(oNM);
				}
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (rsCheckNewChat != null) {
					rsCheckNewChat.close();
				}
				if (rsCheckNewMessages != null) {
					rsCheckNewMessages.close();
				}
				if (rsCheckNewMessagesChat != null) {
					rsCheckNewMessagesChat.close();
				}
				if (stCheckNewChat != null) {
					stCheckNewChat.close();
				}
				if (stCheckNewMessages != null) {
					stCheckNewMessages.close();
				}
				if (stCheckNewMessagesChat != null) {
					stCheckNewMessagesChat.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End checkNew");
	}

	public void setShowTimeStamp(ISShT in, OSShT out) {
		LOGGER.debug("Start setShowTimeStamp with In = " + in.toString());

		long currentTime = System.currentTimeMillis() / 1000L;

		String showUpdate = "UPDATE Messages SET ShowTimestamp = ? where ID = ";

		for (int i = 0; i < in.getMID().size(); i++) {
			if (i == in.getMID().size() - 1) {
				showUpdate += in.getMID().get(i);
			} else {
				showUpdate += in.getMID().get(i) + " OR ID = ";
			}
		}

		try {
			PreparedStatement prepSt = con.prepareStatement(showUpdate);
			prepSt.setLong(1, currentTime);
			prepSt.executeUpdate();
			for (int j = 0; j < in.getMID().size(); j++) {
				ShT s = new ShT();
				s.setMID(in.getMID().get(j));
				s.setT(currentTime);
				out.getShT().add(s);
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		}
		LOGGER.debug("End setShowTimeStamp with Out = " + out.toString());
	}

	public void deleteMessageFromChat(IDMFC in, ODMFC out) {
		LOGGER.debug("Start deleteMessageFromChat with In = " + in.toString());
		ResultSet rsmessageinchat = null;
		Statement stmessageinchat = null;
		Statement streused = null;
		ResultSet rsreused = null;

		try {
			stmessageinchat = con.createStatement();

			rsmessageinchat = stmessageinchat
					.executeQuery("SELECT * FROM Messages WHERE ID = "
							+ in.getMID());
			if (rsmessageinchat != null) {
				while (rsmessageinchat.next()) {
					/* We have found the Message */

					String msgType = rsmessageinchat.getString("MessageTyp");
					deleteMsg(con, in.getMID());
					out.setMID(in.getMID());

					if (msgType.equalsIgnoreCase(Constants.TYP_TEXT)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("TextMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where TextMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deleteContent(con, Constants.TYP_TEXT,
											delid);
								}
							}
						}
					} else if (msgType.equalsIgnoreCase(Constants.TYP_IMAGE)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("ImageMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where ImageMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deleteContent(con, Constants.TYP_IMAGE,
											delid);
								}
							}
						}

					} else if (msgType.equalsIgnoreCase(Constants.TYP_VIDEO)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("VideoMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where VideoMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deleteContent(con, Constants.TYP_VIDEO,
											delid);
								}
							}
						}

					} else if (msgType.equalsIgnoreCase(Constants.TYP_FILE)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("FileMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where FileMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deleteContent(con, Constants.TYP_FILE,
											delid);
								}
							}
						}

					} else if (msgType.equalsIgnoreCase(Constants.TYP_LOCATION)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("LocationMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where LocationMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deleteContent(con, Constants.TYP_LOCATION,
											delid);
								}
							}
						}

					} else if (msgType.equalsIgnoreCase(Constants.TYP_CONTACT)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("ContactMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where ContactMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deleteContent(con, Constants.TYP_CONTACT,
											delid);
								}
							}
						}
					}
				}
			}

		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (rsmessageinchat != null) {
					rsmessageinchat.close();
				}
				if (stmessageinchat != null) {
					stmessageinchat.close();
				}
				if (rsreused != null) {
					rsreused.close();
				}
				if (streused != null) {
					streused.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End deleteMessageFromChat with Out = " + out.toString());
	}

	private int deleteMsg(Connection con, int id) throws SQLException {

		String sql = "DELETE FROM Messages WHERE ID = ? ";
		PreparedStatement pstmt = null;

		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, id);
		return pstmt.executeUpdate();
	}

	private boolean deleteContent(Connection con, String msgType, int id)
			throws SQLException {

		boolean ret = false;
		String sql = null;
		PreparedStatement pstmt = null;
		Statement st1 = con.createStatement();
		ResultSet rs1 = null;

		if (msgType.equalsIgnoreCase(Constants.TYP_TEXT)) {
			sql = "DELETE FROM Text WHERE ID = ? ";
		} else if (msgType.equalsIgnoreCase(Constants.TYP_IMAGE)) {
			rs1 = st1.executeQuery("Select * from Image where ID = " + id);
			if (rs1 != null && rs1.next()) {
				String filename = rs1.getString("Image");
				File file = new File((new Constants()).getUploadFolderImage()
						+ File.separatorChar + filename);
				if (file.exists()) {
					file.delete();
				}
			}
			sql = "DELETE FROM Image WHERE ID = ? ";
		} else if (msgType.equalsIgnoreCase(Constants.TYP_VIDEO)) {
			rs1 = st1.executeQuery("Select * from Video where ID = " + id);
			if (rs1 != null && rs1.next()) {
				String filename = rs1.getString("Video");
				File file = new File((new Constants()).getUploadFolderVideo()
						+ File.separatorChar + filename);
				if (file.exists()) {
					file.delete();
				}
			}
			sql = "DELETE FROM Video WHERE ID = ? ";
		} else if (msgType.equalsIgnoreCase(Constants.TYP_LOCATION)) {
			sql = "DELETE FROM Location WHERE ID = ? ";
		} else if (msgType.equalsIgnoreCase(Constants.TYP_FILE)) {
			rs1 = st1.executeQuery("Select * from File where ID = " + id);
			if (rs1 != null && rs1.next()) {
				String filename = rs1.getString("File");
				File file = new File((new Constants()).getUploadFolderFiles()
						+ File.separatorChar + filename);
				if (file.exists()) {
					file.delete();
				}
			}
			sql = "DELETE FROM Files WHERE ID = ? ";
		} else if (msgType.equalsIgnoreCase(Constants.TYP_CONTACT)) {
			sql = "DELETE FROM Contact WHERE ID = ? ";
		}

		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, id);
		pstmt.executeUpdate();

		if (st1 != null) {
			st1.close();
		}
		if (rs1 != null) {
			rs1.close();
		}

		return ret;
	}

	public void acknowledgeMessageDownload(IAckMD in, OAckMD out) {

		LOGGER.debug("Start acknowledgeMessageDownload with In = "
				+ in.toString());
		ResultSet rsmessageinchat = null;
		Statement stmessageinchat = null;
		Statement stcontent = null;
		ResultSet rscontent = null;

		out.setACK(Constants.ACKNOWLEDGE_FALSE);

		try {
			stmessageinchat = con.createStatement();

			rsmessageinchat = stmessageinchat
					.executeQuery("SELECT * FROM Messages WHERE ID = "
							+ in.getMID());
			if (rsmessageinchat != null) {
				while (rsmessageinchat.next()) {
					/* We have found the Message, now check the Owner */

					String msgType = rsmessageinchat.getString("MessageTyp");

					if (msgType.equalsIgnoreCase(Constants.TYP_TEXT)) {
						stcontent = con.createStatement();
						rscontent = stcontent
								.executeQuery("select * from Text where ID = "
										+ rsmessageinchat.getInt("TextMsgID"));
						if (rscontent != null) {
							while (rscontent.next()) {
								String msg = base64Decode(rscontent
										.getString("Text"));

								int hashCode = msg.hashCode();

								if (hashCode == Integer.valueOf(in.getACK())) {
									out.setACK(Constants.ACKNOWLEDGE_TRUE);
									out.setMID(in.getMID());
								}
							}
						}

					} else if (msgType.equalsIgnoreCase(Constants.TYP_IMAGE)) {
						stcontent = con.createStatement();
						rscontent = stcontent
								.executeQuery("select * from Image where ID = "
										+ rsmessageinchat.getInt("ImageMsgID"));
						if (rscontent != null) {
							while (rscontent.next()) {
								String dback = rscontent.getString("MD5Sum");
								if (in.getACK().equals(dback)) {
									out.setACK(Constants.ACKNOWLEDGE_TRUE);
									out.setMID(in.getMID());
								}
							}
						}
					} else if (msgType.equalsIgnoreCase(Constants.TYP_VIDEO)) {
						stcontent = con.createStatement();
						rscontent = stcontent
								.executeQuery("select * from Video where ID = "
										+ rsmessageinchat.getInt("VideoMsgID"));
						if (rscontent != null) {
							while (rscontent.next()) {
								String dback = rscontent.getString("MD5Sum");
								if (in.getACK().equals(dback)) {
									out.setACK(Constants.ACKNOWLEDGE_TRUE);
									out.setMID(in.getMID());
								}
							}
						}

					}

					// Now if the Acknowledge is valid update Readtimestamp in
					// the Database.
					if (out.getACK().equalsIgnoreCase(
							Constants.ACKNOWLEDGE_TRUE)) {
						String sqlUpdateRead = "UPDATE Messages SET ReadTimestamp = ? WHERE ID = ?";
						PreparedStatement pstmt = null;

						pstmt = con.prepareStatement(sqlUpdateRead);
						pstmt.setLong(1,
								rsmessageinchat.getLong("TempReadTimestamp"));
						pstmt.setInt(2, in.getMID());
						pstmt.executeUpdate();
					}
				}
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (rsmessageinchat != null) {
					rsmessageinchat.close();
				}
				if (stmessageinchat != null) {
					stmessageinchat.close();
				}
				if (rscontent != null) {
					rscontent.close();
				}
				if (stcontent != null) {
					stcontent.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
	}

	public void acknowledgeChatDownload(IAckCD in, OAckCD out) {

		LOGGER.debug("Start acknowledgeMessageDownload with In = "
				+ in.toString());
		ResultSet rsuserinchat = null;
		Statement stuserinchat = null;
		Statement stcontent = null;
		ResultSet rscontent = null;

		out.setACK(Constants.ACKNOWLEDGE_FALSE);

		try {
			stuserinchat = con.createStatement();

			rsuserinchat = stuserinchat
					.executeQuery("select * from UserToChats where ChatID = "
							+ in.getCID() + " and UserID = " + this.id);
			if (rsuserinchat != null) {
				while (rsuserinchat.next()) {
					/* We have found the Message, now check the Owner */

					stcontent = con.createStatement();
					rscontent = stcontent
							.executeQuery("select * from Chats where ID = "
									+ in.getCID());
					if (rscontent != null) {
						while (rscontent.next()) {
							String chatName = rscontent.getString("Chatname");

							int hashCode = chatName.hashCode();
							String tmpack = base64Decode(in.getACK());

							if (hashCode == Integer.valueOf(tmpack)) {
								out.setACK(Constants.ACKNOWLEDGE_TRUE);
							}
						}
					}

					// Now if the Acknowledge is valid update Readtimestamp in
					// the Database.
					if (out.getACK().equalsIgnoreCase(
							Constants.ACKNOWLEDGE_TRUE)) {
						String sqlUpdateRead = "UPDATE UserToChats SET ReadTimestamp = ? WHERE ID = ?";
						PreparedStatement pstmt = null;

						pstmt = con.prepareStatement(sqlUpdateRead);
						pstmt.setLong(1,
								rsuserinchat.getLong("TempReadTimestamp"));
						pstmt.setInt(2, rsuserinchat.getInt("ID"));
						pstmt.executeUpdate();
					}
				}
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (rsuserinchat != null) {
					rsuserinchat.close();
				}
				if (stuserinchat != null) {
					stuserinchat.close();
				}
				if (rscontent != null) {
					rscontent.close();
				}
				if (stcontent != null) {
					stcontent.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
	}

	public void getMessageInformation(IGMI in, OGMI out) {
		LOGGER.debug("Start getMessageInformation with In = " + in.toString());
		ResultSet rsoriginmsgid = null;
		Statement storiginmsgid = null;
		Statement stdetailinfo = null;
		ResultSet rsdetailinfo = null;

		try {
			storiginmsgid = con.createStatement();

			// String messageIDQuery = "SELECT * FROM Messages WHERE ID = ";
			//
			// for (int i = 0; i < in.getMID().size(); i++) {
			// if (i == in.getMID().size() - 1) {
			// messageIDQuery += in.getMID().get(i);
			// } else {
			// messageIDQuery += in.getMID().get(i) + " OR ID = ";
			// }
			// }

			String messageIDQuery = "SELECT * FROM Messages WHERE ID in ( ";

			for (int i = 0; i < in.getMID().size(); i++) {
				if (i == in.getMID().size() - 1) {
					messageIDQuery += in.getMID().get(i) + " )";
				} else {
					messageIDQuery += in.getMID().get(i) + ", ";
				}
			}

			boolean abort = false;
			rsoriginmsgid = storiginmsgid.executeQuery(messageIDQuery);
			if (rsoriginmsgid != null) {
				while (rsoriginmsgid.next()) {
					/* We have found the Message, now check the Owner */
					String sqlUserID = "Select UserID FROM UserToChats WHERE ID = ? ";
					PreparedStatement pstmtOwner = null;

					pstmtOwner = con.prepareStatement(sqlUserID);
					pstmtOwner.setInt(1, rsoriginmsgid.getInt("UsertoChatID"));
					ResultSet rsOwner = pstmtOwner.executeQuery();
					if (rsOwner != null) {
						while (rsOwner.next()) {
							if (this.id == rsOwner.getInt("UserID")) {
								MIB msgout = new MIB();
								msgout.setMID(rsoriginmsgid.getInt("ID"));
								msgout.setSD(rsoriginmsgid
										.getLong("SendTimestamp"));
								stdetailinfo = con.createStatement();
								rsdetailinfo = stdetailinfo
										.executeQuery("SELECT * from Messages where OriginMsgID = "
												+ rsoriginmsgid
														.getInt("OriginMsgID"));
								if (rsdetailinfo != null) {
									while (rsdetailinfo.next()) {
										MI msginfo = new MI();

										// Prepared Statement um UserID zu
										// ermitteln
										PreparedStatement pstmtUN = null;

										pstmtUN = con
												.prepareStatement("SELECT Username FROM Users WHERE ID in (Select UserID from UserToChats where ID in (select UserToChatID from Messages where ID = ?))");
										pstmtUN.setInt(1,
												rsdetailinfo.getInt("ID"));
										ResultSet rsUN = pstmtUN.executeQuery();
										if (rsUN != null) {
											while (rsUN.next()) {
												msginfo.setUN(rsUN
														.getString("Username"));
											}
										}

										// Prepared Statement um UserID zu
										// ermitteln
										PreparedStatement pstmtUID = null;

										pstmtUID = con
												.prepareStatement(sqlUserID);
										pstmtUID.setInt(1, rsdetailinfo
												.getInt("UsertoChatID"));
										ResultSet rsUID = pstmtUID
												.executeQuery();
										if (rsUID != null) {
											while (rsUID.next()) {
												msginfo.setUID(rsUID
														.getInt("UserID"));
											}
										}
										msginfo.setRD(rsdetailinfo
												.getLong("ReadTimestamp"));
										msginfo.setSH(rsdetailinfo
												.getLong("ShowTimestamp"));
										msgout.getMI().add(msginfo);
									}
								}
								out.getMIB().add(msgout);
							} else {
								out.setET(Constants.NOT_MESSAGE_OWNER);
								abort = true;
								break;
							}
						}
					}
					if (abort) {
						break;
					}
				}
			}
			if (abort) {
				out.getMIB().clear();
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (rsoriginmsgid != null) {
					rsoriginmsgid.close();
				}
				if (storiginmsgid != null) {
					storiginmsgid.close();
				}
				if (stdetailinfo != null) {
					stdetailinfo.close();
				}
				if (rsdetailinfo != null) {
					rsdetailinfo.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
	}

	public void deleteChat(IDeCh in, ODeCh out) {
		LOGGER.debug("Start deleteChat with In = " + in.toString());
		ResultSet rsmessageinchat = null;
		Statement stmessageinchat = null;

		try {
			// First we delete all Messages
			stmessageinchat = con.createStatement();
			rsmessageinchat = stmessageinchat
					.executeQuery("SELECT ID FROM Messages WHERE UserToChatID IN (SELECT ID FROM UserToChats WHERE UserToChats.ChatID = "
							+ in.getCID() + ")");
			if (rsmessageinchat != null) {
				IDMFC inmsg = new IDMFC();
				ODMFC outmsg = new ODMFC();
				while (rsmessageinchat.next()) {
					inmsg.setMID(rsmessageinchat.getInt(1));
					deleteMessageFromChat(inmsg, outmsg);
				}
			}
			// Now we delete the assignments of the users to the chat
			String sqlUserToChat = "DELETE FROM UserToChats WHERE ChatID = ? ";
			PreparedStatement pstmtU2C = null;

			pstmtU2C = con.prepareStatement(sqlUserToChat);
			pstmtU2C.setInt(1, in.getCID());
			pstmtU2C.executeUpdate();

			// At least we delete the chat.
			String sqlChat = "DELETE FROM Chats WHERE ID = ? ";
			PreparedStatement pstmtChat = null;

			pstmtChat = con.prepareStatement(sqlChat);
			pstmtChat.setInt(1, in.getCID());
			pstmtChat.executeUpdate();
			out.setR(Constants.CHAT_DELETED);

		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (rsmessageinchat != null) {
					rsmessageinchat.close();
				}
				if (stmessageinchat != null) {
					stmessageinchat.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
	}

	public void syncUser(ISU in, OSU out) {
		LOGGER.debug("Start syncuser with In = " + in.toString());
		ResultSet rsuser = null;
		Statement stuser = null;

		try {
			stuser = con.createStatement();

			String userIDQuery = "SELECT * FROM Users WHERE ID = ";

			for (int i = 0; i < in.getUID().size(); i++) {
				if (i == in.getUID().size() - 1) {
					userIDQuery += in.getUID().get(i);
				} else {
					userIDQuery += in.getUID().get(i) + " OR ID = ";
				}
			}
			rsuser = stuser.executeQuery(userIDQuery);
			if (rsuser != null) {
				while (rsuser.next()) {
					U uinfo = new U();
					uinfo.setLA(rsuser.getLong("AuthenticationTime"));
					uinfo.setUID(rsuser.getInt("ID"));
					uinfo.setUN(rsuser.getString("Username"));
					uinfo.setE(rsuser.getString("Email"));
					uinfo.setICID(rsuser.getInt("IconID"));
					out.getU().add(uinfo);
				}
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (rsuser != null) {
					rsuser.close();
				}
				if (stuser != null) {
					stuser.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
	}

	public void sendIconMessage(ISIcM in, OSIcM out) {
		LOGGER.debug("Start sendIconMessage with In = " + in.toString());
		int key = -1;
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			// TODO first check if Message already exists, idempotent?
			/* First we create a chat room */
			statement.executeUpdate(
					"insert into Image (Image, MD5Sum) values ('" + in.getIcM()
							+ "', '" + in.getIcMD5() + "')",
					Statement.RETURN_GENERATED_KEYS);
			resultSet = statement.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				key = resultSet.getInt(1);
			}
			out.setIcID(key);
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End sendIconMessage with Out = " + out.toString());
	}

	public void insertChatIcon(IICIc in, OICIc out) {
		LOGGER.debug("Start insertChatIcon with In = " + in.toString());
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = con.createStatement();
			/* Check first if the Owning User is sending the Request */
			resultSet = statement
					.executeQuery("select OwningUserID from Chats where ID = '"
							+ in.getCID() + "'");

			if (resultSet.next()) {
				if (this.id == resultSet.getInt("OwningUserID")) {
					statement.executeUpdate("UPDATE Chats SET IconID = "
							+ in.getIcID() + " WHERE ID = " + in.getCID());
					// Now we set the Timestamps to zero so that every client is
					// getting
					// the update
					statement
							.executeUpdate("UPDATE UserToChats SET ReadTimestamp = 0, TempReadTimestamp = 0 WHERE ChatID = "
									+ in.getCID());
					out.setR(Constants.ICON_ADDED);
				} else {
					out.setET(Constants.NOT_CHAT_OWNER);
				}
			}
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End insertChatIcon with Out = " + out.toString());
	}

	public void insertUserIcon(IIUIc in, OIUIc out) {
		LOGGER.debug("Start insertUserIcon with In = " + in.toString());
		Statement statement = null;

		try {
			statement = con.createStatement();
			// TODO first check if Message already exists, idempotent?
			/* First we create a chat room */
			statement.executeUpdate("UPDATE Users SET IconID = " + in.getIcID()
					+ " WHERE ID = " + this.id);
			out.setR(Constants.ICON_ADDED);
		} catch (SQLException e) {
			out.setET(Constants.DB_ERROR);
			LOGGER.error(e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("End insertUserIcon with Out = " + out.toString());
	}
}
