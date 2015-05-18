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
package de.radiohacks.frinmeba.services;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import de.radiohacks.frinmeba.model.InAcknowledgeMessageDownload;
import de.radiohacks.frinmeba.model.InAddUserToChat;
import de.radiohacks.frinmeba.model.InAuthenticate;
import de.radiohacks.frinmeba.model.InCheckNewMessages;
import de.radiohacks.frinmeba.model.InCreateChat;
import de.radiohacks.frinmeba.model.InDeleteChat;
import de.radiohacks.frinmeba.model.InDeleteMessageFromChat;
import de.radiohacks.frinmeba.model.InFetchImageMessage;
import de.radiohacks.frinmeba.model.InFetchMessageFromChat;
import de.radiohacks.frinmeba.model.InFetchTextMessage;
import de.radiohacks.frinmeba.model.InFetchVideoMessage;
import de.radiohacks.frinmeba.model.InGetMessageInformation;
import de.radiohacks.frinmeba.model.InInsertMessageIntoChat;
import de.radiohacks.frinmeba.model.InListChat;
import de.radiohacks.frinmeba.model.InListUser;
import de.radiohacks.frinmeba.model.InRemoveUserFromChat;
import de.radiohacks.frinmeba.model.InSendImageMessage;
import de.radiohacks.frinmeba.model.InSendTextMessage;
import de.radiohacks.frinmeba.model.InSendVideoMessage;
import de.radiohacks.frinmeba.model.InSetShowTimeStamp;
import de.radiohacks.frinmeba.model.InSignup;
import de.radiohacks.frinmeba.model.OutAcknowledgeMessageDownload;
import de.radiohacks.frinmeba.model.OutAddUserToChat;
import de.radiohacks.frinmeba.model.OutAuthenticate;
import de.radiohacks.frinmeba.model.OutCheckNewMessages;
import de.radiohacks.frinmeba.model.OutCreateChat;
import de.radiohacks.frinmeba.model.OutDeleteChat;
import de.radiohacks.frinmeba.model.OutDeleteMessageFromChat;
import de.radiohacks.frinmeba.model.OutFetchImageMessage;
import de.radiohacks.frinmeba.model.OutFetchMessageFromChat;
import de.radiohacks.frinmeba.model.OutFetchMessageFromChat.Message;
import de.radiohacks.frinmeba.model.OutFetchMessageFromChat.Message.OwningUser;
import de.radiohacks.frinmeba.model.OutFetchTextMessage;
import de.radiohacks.frinmeba.model.OutFetchVideoMessage;
import de.radiohacks.frinmeba.model.OutGetMessageInformation;
import de.radiohacks.frinmeba.model.OutInsertMessageIntoChat;
import de.radiohacks.frinmeba.model.OutListChat;
import de.radiohacks.frinmeba.model.OutListChat.Chat;
import de.radiohacks.frinmeba.model.OutListUser;
import de.radiohacks.frinmeba.model.OutRemoveUserFromChat;
import de.radiohacks.frinmeba.model.OutSendImageMessage;
import de.radiohacks.frinmeba.model.OutSendTextMessage;
import de.radiohacks.frinmeba.model.OutSendVideoMessage;
import de.radiohacks.frinmeba.model.OutSetShowTimeStamp;
import de.radiohacks.frinmeba.model.OutSignUp;

public class User {

	private int Id = 0;
	// private String Username;
	// private String Password;
	private Connection con;

	static final Logger logger = Logger.getLogger(User.class);

	public User(Connection conin) {
		logger.debug("Start User with Connection");
		this.con = conin;
		logger.debug("End User with Connection");
	}

	public int getID() {
		return Id;
	}

	public String base64Encode(String token) {
		byte[] encodedBytes = Base64.encodeBase64(token.getBytes());
		return new String(encodedBytes, Charset.forName(Constants.CharacterSet));
	}

	public String base64Decode(String token) {
		byte[] decodedBytes = Base64.decodeBase64(token.getBytes());
		return new String(decodedBytes, Charset.forName(Constants.CharacterSet));
	}

	public void authenticate(InAuthenticate in, OutAuthenticate out) {
		logger.debug("Start authenticate with In = " + in.toString());

		out.setAuthenticated(Constants.AUTHENTICATE_FALSE);

		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			resultSet = statement
					.executeQuery("select ID, Username, Password, Active from Users where B64Username = '"
							+ in.getUsername() + "'");
			if (resultSet.next()) {
				if (resultSet.getBoolean("Active")) {
					String dbpw = resultSet.getString("Password");
					if (dbpw.equals(in.getPassword())) {
						int ownUSerID = resultSet.getInt("ID");

						out.setAuthenticated(Constants.AUTHENTICATE_TRUE);
						out.setUsername(resultSet.getString("Username"));
						out.setUserID(ownUSerID);

						long currentTime = System.currentTimeMillis() / 1000L;
						String updateMessage = "UPDATE Users SET AuthenticationTime = ? where ID = ?";
						PreparedStatement prepSt = con
								.prepareStatement(updateMessage);
						prepSt.setLong(1, currentTime);
						prepSt.setInt(2, ownUSerID);
						prepSt.executeUpdate();
					} else {
						out.setUsername(resultSet.getString("Username"));
						out.setErrortext(Constants.WRONG_PASSWORD);
					}
				} else {
					out.setAuthenticated(Constants.AUTHENTICATE_FALSE);
					out.setErrortext(Constants.USER_NOT_ACTIVE);
				}
			} else {
				out.setAuthenticated(Constants.AUTHENTICATE_FALSE);
				out.setErrortext(Constants.NONE_EXISTING_USER);
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}

		logger.debug("End authenticate with Out = " + out.toString());
	}

	public void fillUserinfo(String username) {
		logger.debug("Start fillUserinfo with no Parameters");
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			resultSet = statement
					.executeQuery("select ID from Users where Username = '"
							+ username + "'");

			if (resultSet.next()) {
				this.Id = resultSet.getInt("ID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
		logger.debug("End fillUserinfo with no return value");
	}

	public void signUp(InSignup in, OutSignUp out) {
		logger.debug("Start signUp with In = " + in.toString());

		int key = -1;
		ResultSet rsfind = null;
		ResultSet rscreate = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			rsfind = statement
					.executeQuery("select ID from Users where B64Username = '"
							+ in.getUsername() + "' limit 1");

			if (rsfind.next()) {
				out.setErrortext(Constants.USER_ALREADY_EXISTS);
				out.setUserID(rsfind.getInt("ID"));
			} else {
				long currentTime = System.currentTimeMillis() / 1000L;

				statement
						.executeUpdate(
								"insert into Users(Username, B64Username, Password, Email, SignupDate, Status, AuthenticationTime) values ('"
										+ base64Decode(in.getUsername())
										+ "', '"
										+ in.getUsername()
										+ "', '"
										+ in.getPassword()
										+ "', '"
										+ in.getEmail()
										+ "', '"
										+ currentTime
										+ "', '"
										+ "0"
										+ "', '"
										+ currentTime
										+ "')", Statement.RETURN_GENERATED_KEYS);
				rscreate = statement.getGeneratedKeys();
				if (rscreate != null && rscreate.next()) {
					key = rscreate.getInt(1);
					out.setUserID(key);
				}
				out.setUsername(base64Decode(in.getUsername()));
				out.setSignUp("SUCCESSFUL");
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
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
				// Do nothing we are closing
			}
		}
		logger.debug("End signUp with Out = " + out.toString());
	}

	public void listUser(InListUser in, OutListUser out) {
		logger.debug("Start listUser with In = " + in.toString());
		fillUserinfo(in.getUsername());
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			if (in.getSearch() != null && !in.getSearch().isEmpty()) {
				resultSet = statement
						.executeQuery("select Username, Id, Email from Users where Active = 1 and Username like '%"
								+ in.getSearch() + "%'");
			} else {
				resultSet = statement
						.executeQuery("select Username, Id, Email from Users where Active = 1");
			}

			while (resultSet.next()) {
				if (this.Id != resultSet.getInt("Id")) {
					de.radiohacks.frinmeba.model.OutListUser.User u = new de.radiohacks.frinmeba.model.OutListUser.User();
					u.setUsername(resultSet.getString("Username"));
					u.setEmail(resultSet.getString("Email"));
					u.setUserID(resultSet.getInt("Id"));
					out.getUser().add(u);
				}
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
		logger.debug("End listUser with Out = " + out.toString());
	}

	public void listChat(InListChat in, OutListChat out) {
		logger.debug("Start listChat with In = " + in.toString());
		fillUserinfo(in.getUsername());
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
							+ this.Id);

			if (rsusertochats != null) {
				while (rsusertochats.next()) {

					Chat outchat = new Chat();
					stchats = con.createStatement();
					rschats = stchats
							.executeQuery("Select Chatname, OwningUserID from Chats where ID = "
									+ rsusertochats.getInt("ChatID"));

					int rscount = 0;
					if (rschats != null) {
						while (rschats.next()) {
							outchat.setChatID(rsusertochats.getInt("ChatID"));
							outchat.setChatname(rschats.getString("Chatname"));
							OutListChat.Chat.OwningUser outOwingUser = new OutListChat.Chat.OwningUser();
							outOwingUser.setOwningUserID(rschats
									.getInt("OwningUserID"));

							stusers = con.createStatement();
							rsusers = stusers
									.executeQuery("Select Username from Users where ID = "
											+ rschats.getInt("OwningUserID"));
							if (rsusers != null) {
								while (rsusers.next()) {
									outOwingUser.setOwningUserName(rsusers
											.getString("Username"));
								}
							}
							outchat.setOwningUser(outOwingUser);
							rscount++;
						}
					}
					if (rscount > 0) {
						out.getChat().add(outchat);
					} else {
						out.setErrortext(Constants.NO_ACTIVE_CHATS);
					}
				}
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
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
				// Do nothing we are closing
			}
		}
		logger.debug("End listChat with Out = " + out.toString());
	}

	public void createChat(InCreateChat in, OutCreateChat out) {
		logger.debug("Start createChat with In = " + in.toString());
		int key = -1;
		ResultSet resultSet = null;
		Statement stchat = null;
		Statement stuser = null;

		fillUserinfo(in.getUsername());

		try {

			stchat = con.createStatement();
			// TODO Check first if Chat already exists, idempotent?

			/* First we create a chat room */
			stchat.executeUpdate(
					"insert into Chats(Chatname, OwningUserId) values ('"
							+ in.getChatname() + "', '" + this.Id + "')",
					Statement.RETURN_GENERATED_KEYS);
			resultSet = stchat.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				key = resultSet.getInt(1);
			}
			out.setChatname(in.getChatname());
			out.setChatID(key);

			/* Now we have to add the Owning user to his own chat room */
			stuser = con.createStatement();
			stuser.executeUpdate("insert into UserToChats(UserID, ChatID) values ('"
					+ this.Id + "', '" + key + "')");
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
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
				// Do nothing we are closing
			}
		}
		logger.debug("End createChat with Out = " + out.toString());
	}

	// Done In & Out
	public void addUserToChat(InAddUserToChat in, OutAddUserToChat out) {
		logger.debug("Start addUserToChat with In = " + in.toString());
		fillUserinfo(in.getUsername());
		ResultSet resultSet = null;
		ResultSet resultSet2 = null;
		Statement statement = null;

		if (this.Id == in.getUserID()) {
			out.setErrortext(Constants.CHAT_OWNER_NOT_ADDED);
		} else {
			try {
				statement = con.createStatement();

				/* Check first if the Owning User is sending the Request */
				resultSet = statement
						.executeQuery("select OwningUserID from Chats where ID = '"
								+ in.getChatID() + "'");

				if (resultSet.next()) {
					if (this.Id == resultSet.getInt("OwningUserID")) {
						/* The Owning User is adding the new User to the Chat */
						/* Check first if user is already in the Chat */
						resultSet2 = statement
								.executeQuery("select UserID from UserToChats where ChatID = '"
										+ in.getChatID() + "'");

						if (resultSet2.next()) {
							boolean dup = false;
							while (resultSet2.next()) {
								if (in.getUserID() == resultSet2
										.getInt("UserID")) {
									dup = true;
								}
							}
							if (dup == true) {
								out.setErrortext(Constants.USER_ALREADY_IN_CHAT);
							} else {
								/*
								 * User not in the Chat so add the User to the
								 * Chat Room
								 */
								statement
										.executeUpdate("insert into UserToChats(UserID, ChatID) values ('"
												+ in.getUserID()
												+ "', '"
												+ in.getChatID() + "')");

								out.setResult(Constants.USER_ADDED);
							}
						} else {
							/*
							 * User not in the Chat so add the User to the Chat
							 * Room
							 */
							statement
									.executeUpdate("insert into UserToChats(UserID, ChatID) values ('"
											+ in.getUserID()
											+ "', '"
											+ in.getChatID() + "')");

							out.setResult(Constants.USER_ADDED);
						}
					} else {
						out.setErrortext(Constants.NOT_CHAT_OWNER);
					}
				}
			} catch (SQLException e) {
				out.setErrortext(Constants.DB_ERROR);
				e.printStackTrace();
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
					// Do nothing we are closing
				}
			}
		}
		logger.debug("End addUserToChat with Out = " + out.toString());
	}

	public void sendTextMessage(InSendTextMessage in, OutSendTextMessage out) {
		logger.debug("Start sendTextMessage with In = " + in.toString());
		int key = -1;

		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			// TODO first check if Message already exists, idempotent?
			/* First we create a chat room */
			statement.executeUpdate(
					"insert into Text (Text) values ('" + in.getTextMessage()
							+ "')", Statement.RETURN_GENERATED_KEYS);
			resultSet = statement.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				key = resultSet.getInt(1);
			}
			out.setTextID(key);
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
		logger.debug("End sendTextMessage with Out = " + out.toString());
	}

	public void sendImageMessage(InSendImageMessage in, OutSendImageMessage out) {
		logger.debug("Start sendImageMessage with In = " + in.toString());
		int key = -1;
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			// TODO first check if Message already exists, idempotent?
			/* First we create a chat room */
			statement.executeUpdate(
					"insert into Image (Image, MD5Sum) values ('"
							+ in.getImageMessage() + "', '"
							+ in.getImageMD5Hash() + "')",
					Statement.RETURN_GENERATED_KEYS);
			resultSet = statement.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				key = resultSet.getInt(1);
			}
			out.setImageID(key);
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
		logger.debug("End sendImageMessage with Out = " + out.toString());
	}

	public void sendVideoMessage(InSendVideoMessage in, OutSendVideoMessage out) {
		logger.debug("Start sendVideoMessage with In = " + in.toString());
		int key = -1;
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();
			// TODO first check if Message already exists, idempotent?
			/* First we create a chat room */
			statement.executeUpdate(
					"insert into Video (Video, MD5Sum) values ('"
							+ in.getVideoMessage() + "', '"
							+ in.getVideoMD5Hash() + "')",
					Statement.RETURN_GENERATED_KEYS);
			resultSet = statement.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				key = resultSet.getInt(1);
			}
			out.setVideoID(key);
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
		logger.debug("End sendVideoMessage with Out = " + out.toString());
	}

	public void removeUserFromChat(InRemoveUserFromChat in,
			OutRemoveUserFromChat out) {
		logger.debug("Start removeUserFromChat with In = " + in.toString());

		ResultSet resultSetU2Cid = null;
		Statement statementU2Cid = null;
		Statement statementDelete = null;

		// TODO erst pr�fen ob der User welcher den Request stellt der
		// Chat-Owner ist

		try {
			statementU2Cid = con.createStatement();
			resultSetU2Cid = statementU2Cid
					.executeQuery("select id from UserToChats where ChatID = '"
							+ in.getChatID() + "' and UserID = '"
							+ in.getUserID() + "'");

			if (resultSetU2Cid != null) {
				while (resultSetU2Cid.next()) {
					statementDelete = con.createStatement();
					String DeleteFromMessages = "DELETE FROM Messages WHERE UsertoChatID = '"
							+ resultSetU2Cid.getInt("ID") + "'";
					boolean result = statementDelete
							.execute(DeleteFromMessages);
					if (result) {
						out.setResult("REMOVED");
					}
				}
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
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
				// Do nothing we are closing
			}
		}
		logger.debug("End removeUserFromChat with Out = " + out.toString());
	}

	@SuppressWarnings("resource")
	public void insertMessageIntoChat(InInsertMessageIntoChat in,
			OutInsertMessageIntoChat out) {
		logger.debug("Start insertMessageIntoChat with In = " + in.toString());
		fillUserinfo(in.getUsername());
		int key = -1;
		ResultSet resultSet = null;
		Statement statement = null;
		ResultSet resultSet2 = null;
		Statement statement2 = null;
		List<Integer> generatedRows = new ArrayList<Integer>(1);
		int OriginMsgID = 0;

		try {
			statement = con.createStatement();
			boolean typefound = false;
			long currentTime = System.currentTimeMillis() / 1000L;

			// TODO Check if message is already inserted in the Chat,
			// idempotent?
			/* First we search all Users in the given Chat */
			resultSet = statement
					.executeQuery("select id, UserID from UserToChats where ChatID = '"
							+ in.getChatID() + "'");

			if (resultSet != null) {
				while (resultSet.next()) {
					if (in.getMessageTyp().equalsIgnoreCase(Constants.TYP_TEXT)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, TextMsgID) values ('"
												+ this.Id
												+ "', '"
												+ Constants.TYP_TEXT
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '"
												+ in.getMessageID()
												+ "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.Id == resultSet.getInt("UserID")) {
								OriginMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					if (in.getMessageTyp()
							.equalsIgnoreCase(Constants.TYP_IMAGE)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, ImageMsgID) values ('"
												+ this.Id
												+ "', '"
												+ Constants.TYP_IMAGE
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '"
												+ in.getMessageID()
												+ "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.Id == resultSet.getInt("UserID")) {
								OriginMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					if (in.getMessageTyp().equalsIgnoreCase(
							Constants.TYP_CONTACT)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, ContactMsgID) values ('"
												+ this.Id
												+ "', '"
												+ Constants.TYP_CONTACT
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '"
												+ in.getMessageID()
												+ "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.Id == resultSet.getInt("UserID")) {
								OriginMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					if (in.getMessageTyp().equalsIgnoreCase(
							Constants.TYP_LOCATION)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, LocationMsgID) values ('"
												+ this.Id
												+ "', '"
												+ Constants.TYP_LOCATION
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '"
												+ in.getMessageID()
												+ "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.Id == resultSet.getInt("UserID")) {
								OriginMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					if (in.getMessageTyp().equalsIgnoreCase(Constants.TYP_FILE)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, FileMsgID) values ('"
												+ this.Id
												+ "', '"
												+ Constants.TYP_FILE
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '"
												+ in.getMessageID()
												+ "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.Id == resultSet.getInt("UserID")) {
								OriginMsgID = key;
								generatedRows.add(key);
							} else {
								generatedRows.add(key);
							}
						}
					}
					if (in.getMessageTyp()
							.equalsIgnoreCase(Constants.TYP_VIDEO)) {
						statement2 = con.createStatement();
						statement2
								.executeUpdate(
										"insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, VideoMsgID) values ('"
												+ this.Id
												+ "', '"
												+ Constants.TYP_VIDEO
												+ "', "
												+ currentTime
												+ ", "
												+ resultSet.getInt("ID")
												+ ", '"
												+ in.getMessageID()
												+ "')",
										Statement.RETURN_GENERATED_KEYS);
						typefound = true;
						resultSet2 = statement2.getGeneratedKeys();
						if (resultSet2 != null && resultSet2.next()) {
							key = resultSet2.getInt(1);
							if (this.Id == resultSet.getInt("UserID")) {
								OriginMsgID = key;
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
					if (tmpi == this.Id) {

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
					out.setSendTimestamp(currentTime);
					out.setMessageID(key);
					// Now set the originMsgID to group all Messages for the
					// showTimestamp
					String updateOriginMsgID = "UPDATE Messages SET OriginMsgID = ? where ID = ?";
					for (Integer id : generatedRows) {
						PreparedStatement prepOriginSt = con
								.prepareStatement(updateOriginMsgID);
						prepOriginSt.setLong(1, OriginMsgID);
						prepOriginSt.setInt(2, id);
						prepOriginSt.executeUpdate();
					}

				} else {
					out.setErrortext(Constants.TYPE_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
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
				// Do nothing we are closing
			}
		}
		logger.debug("End insertMessageIntoChat with Out = " + out.toString());
	}

	public void getMessagesFromChat(InFetchMessageFromChat in,
			OutFetchMessageFromChat out) {
		logger.debug("Start getMessagesFromChat with In = " + in.toString());
		fillUserinfo(in.getUsername());
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
							+ in.getChatID() + "' and UserID = '" + this.Id
							+ "'");

			if (resultSet != null) {
				while (resultSet.next()) {
					String query = new String();
					long tmptimestamp = in.getTimeStamp();
					if (in.getTimeStamp() == 0) {
						query += "select * from Messages where UsertoChatID = "
								+ resultSet.getInt("ID")
								+ " and ReadTimeStamp = 0";
					} else {
						query += "select * from Messages where UsertoChatID = "
								+ resultSet.getInt("ID")
								+ " and (SendTimestamp > "
								+ String.valueOf(tmptimestamp)
								+ " or ReadTimeStamp = 0)";
					}

					st2 = con.createStatement();
					rs2 = st2.executeQuery(query);
					if (rs2 != null) {
						while (rs2.next()) {
							Message msg = new Message();
							msg.setMessageID(rs2.getInt("ID"));
							msg.setMessageTyp(rs2.getString("MessageTyp"));
							msg.setSendTimestamp(rs2.getLong("SendTimestamp"));
							msg.setShowTimestamp(rs2.getLong("ShowTimestamp"));
							msg.setOriginMsgID(rs2.getInt("OriginMsgID"));
							OwningUser owingu = new OwningUser();
							owingu.setOwningUserID(rs2.getInt("OwningUserID"));

							st3 = con.createStatement();
							rs3 = st3
									.executeQuery("select Username from Users where ID = '"
											+ rs2.getInt("OwningUserID") + "'");

							if (rs3 != null) {
								while (rs3.next()) {
									owingu.setOwningUserName(rs3
											.getString("Username"));
								}
							}

							msg.setOwningUser(owingu);

							if (msg.getMessageTyp().equalsIgnoreCase(
									Constants.TYP_TEXT)) {
								msg.setTextMsgID(rs2.getInt("TextMsgID"));
							} else if (msg.getMessageTyp().equalsIgnoreCase(
									Constants.TYP_IMAGE)) {
								msg.setImageMsgID(rs2.getInt("ImageMsgID"));
							} else if (msg.getMessageTyp().equalsIgnoreCase(
									Constants.TYP_CONTACT)) {
								msg.setContactMsgID(rs2.getInt("ContactMsgID"));
							} else if (msg.getMessageTyp().equalsIgnoreCase(
									Constants.TYP_LOCATION)) {
								msg.setLocationMsgID(rs2
										.getInt("LocationMsgID"));
							} else if (msg.getMessageTyp().equalsIgnoreCase(
									Constants.TYP_FILE)) {
								msg.setFileMsgID(rs2.getInt("FileMsgID"));
							} else if (msg.getMessageTyp().equalsIgnoreCase(
									Constants.TYP_VIDEO)) {
								msg.setVideoMsgID(rs2.getInt("VideoMsgID"));
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
								msg.setReadTimestamp(readTime);
							} else {
								msg.setReadTimestamp(rs2
										.getLong("ReadTimestamp"));
							}

							/*
							 * Now get the numbers for the status at the
							 * frontend
							 */
							sttotal = con.createStatement();
							rstotal = sttotal
									.executeQuery("SELECT Count(*) from Messages where OriginMsgID = "
											+ rs2.getInt("OriginMsgID"));
							if (rstotal != null) {
								while (rstotal.next()) {
									msg.setNumberTotal(rstotal.getInt(1));
								}
							}
							stread = con.createStatement();
							rsread = stread
									.executeQuery("SELECT Count(*) from Messages where OriginMsgID = "
											+ rs2.getInt("OriginMsgID")
											+ " AND ReadTimestamp > 0");
							if (rsread != null) {
								while (rsread.next()) {
									msg.setNumberRead(rsread.getInt(1));
								}
							}
							stshow = con.createStatement();
							rsshow = stshow
									.executeQuery("SELECT Count(*) from Messages where OriginMsgID = "
											+ rs2.getInt("OriginMsgID")
											+ " AND ShowTimestamp > 0");
							if (rsshow != null) {
								while (rsshow.next()) {
									msg.setNumberShow(rsshow.getInt(1));
								}
							}
							out.getMessage().add(msg);
						}
					}
				}
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
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
				// Do nothing we are closing
			}
		}
		logger.debug("End getMessagesFromChat with out = " + out.toString());
	}

	public void getTextMessages(InFetchTextMessage in, OutFetchTextMessage out) {
		logger.debug("Start getTextMessages with In = " + in.toString());
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
					out.setTextMessage(resultSet.getString("Text"));
				}
			} else {
				out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
		logger.debug("End getTextMessages with Out = " + out.toString());
	}

	public void getImageMessages(InFetchImageMessage in,
			OutFetchImageMessage out) {
		logger.debug("Start getImageMessages with In = " + in.toString());
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();

			/* First we create a chat room */
			resultSet = statement
					.executeQuery("select Image, MD5Sum from Image where ID = "
							+ in.getImageID());
			if (resultSet != null) {
				if (resultSet.next()) {
					out.setImageMessage(resultSet.getString("Image"));
					out.setImageMD5Hash(resultSet.getString("MD5Sum"));
				}
			} else {
				out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
		logger.debug("End getImageMessages with Out = " + out.toString());
	}

	public void getVideoMessages(InFetchVideoMessage in,
			OutFetchVideoMessage out) {
		logger.debug("Start getImageMessages with In = " + in.toString());
		ResultSet resultSet = null;
		Statement statement = null;

		try {
			statement = con.createStatement();

			/* First we create a chat room */
			resultSet = statement
					.executeQuery("select Video, MD5Sum from Video where ID = "
							+ in.getVideoID());
			if (resultSet != null) {
				if (resultSet.next()) {
					out.setVideoMessage(resultSet.getString("Video"));
					out.setVideoMD5Hash(resultSet.getString("MD5Sum"));
				}
			} else {
				out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
		logger.debug("End getVideoMessages with Out = " + out.toString());
	}

	public void checkNewMessages(InCheckNewMessages in, OutCheckNewMessages out) {
		logger.debug("Start checkNewMessages with In = " + in.toString());
		ResultSet rsusertochat = null;
		ResultSet rschatName = null;
		ResultSet rsmessages = null;
		Statement stusertochat = null;
		Statement stchatName = null;
		Statement stmessages = null;

		fillUserinfo(in.getUsername());

		try {
			stusertochat = con.createStatement();

			/* First we create a chat room */
			rsusertochat = stusertochat
					.executeQuery("select ID, ChatID from UserToChats where UserID = "
							+ this.Id);
			if (rsusertochat != null) {
				while (rsusertochat.next()) {
					/* We have a userToChatID now identifiy the Chat */

					OutCheckNewMessages.Chats outchat = new OutCheckNewMessages.Chats();
					outchat.setChatID(rsusertochat.getInt("ChatID"));
					stchatName = con.createStatement();
					rschatName = stchatName
							.executeQuery("select Chatname from Chats where ID ="
									+ rsusertochat.getInt("ChatID"));
					if (rschatName != null) {
						while (rschatName.next()) {
							outchat.setChatname(rschatName
									.getString("Chatname"));
						}
					}
					/* Now we find out how many messages are in the Chat */
					stmessages = con.createStatement();
					rsmessages = stmessages
							.executeQuery("select count(*) from Messages where UsertoChatID = "
									+ rsusertochat.getInt("ID")
									+ " AND ReadTimestamp = '0'");
					if (rsmessages != null) {
						while (rsmessages.next()) {
							outchat.setNumberOfMessages(rsmessages
									.getInt("COUNT(*)"));
						}
					}
					out.getChats().add(outchat);
				}
			} else {
				out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (rsusertochat != null) {
					rsusertochat.close();
				}
				if (rschatName != null) {
					rschatName.close();
				}
				if (rsmessages != null) {
					rsmessages.close();
				}
				if (stusertochat != null) {
					stusertochat.close();
				}
				if (stchatName != null) {
					stchatName.close();
				}
				if (stmessages != null) {
					stmessages.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
		logger.debug("End checkNewMessages with Out = " + out.toString());
	}

	public void setShowTimeStamp(InSetShowTimeStamp in, OutSetShowTimeStamp out) {
		logger.debug("Start setShowTimeStamp with In = " + in.toString());

		long currentTime = System.currentTimeMillis() / 1000L;

		try {
			String updateMessage = "UPDATE Messages SET ShowTimestamp = ? where ID = ?";
			PreparedStatement prepSt = con.prepareStatement(updateMessage);
			prepSt.setLong(1, currentTime);
			prepSt.setInt(2, in.getMessageID());
			prepSt.executeUpdate();
			out.setShowTimestamp(currentTime);
			out.setMessageID(in.getMessageID());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.setErrortext(Constants.DB_ERROR);
		}
		logger.debug("End setShowTimeStamp with Out = " + out.toString());
	}

	public void deleteMessageFromChat(InDeleteMessageFromChat in,
			OutDeleteMessageFromChat out) {
		logger.debug("Start deleteMessageFromChat with In = " + in.toString());
		ResultSet rsmessageinchat = null;
		Statement stmessageinchat = null;
		Statement streused = null;
		ResultSet rsreused = null;

		try {
			stmessageinchat = con.createStatement();

			rsmessageinchat = stmessageinchat
					.executeQuery("SELECT * FROM Messages WHERE ID = "
							+ in.getMessageID());
			if (rsmessageinchat != null) {
				while (rsmessageinchat.next()) {
					/* We have found the Message */

					String MsgType = rsmessageinchat.getString("MessageTyp");
					deletemsg(con, in.getMessageID());
					out.setMessageID(in.getMessageID());

					if (MsgType.equalsIgnoreCase(Constants.TYP_TEXT)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("TextMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where TextMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deletecontent(con, Constants.TYP_TEXT,
											delid);
								}
							}
						}
					} else if (MsgType.equalsIgnoreCase(Constants.TYP_IMAGE)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("ImageMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where ImageMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deletecontent(con, Constants.TYP_IMAGE,
											delid);
								}
							}
						}

					} else if (MsgType.equalsIgnoreCase(Constants.TYP_VIDEO)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("VideoMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where VideoMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deletecontent(con, Constants.TYP_VIDEO,
											delid);
								}
							}
						}

					} else if (MsgType.equalsIgnoreCase(Constants.TYP_FILE)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("FileMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where FileMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deletecontent(con, Constants.TYP_FILE,
											delid);
								}
							}
						}

					} else if (MsgType.equalsIgnoreCase(Constants.TYP_LOCATION)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("LocationMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where LocationMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deletecontent(con, Constants.TYP_LOCATION,
											delid);
								}
							}
						}

					} else if (MsgType.equalsIgnoreCase(Constants.TYP_CONTACT)) {
						streused = con.createStatement();
						int delid = rsmessageinchat.getInt("ContactMsgID");
						rsreused = streused
								.executeQuery("select count(*) from Messages where ContactMsgID = "
										+ delid);
						if (rsreused != null) {
							while (rsreused.next()) {
								if (rsreused.getInt(1) == 0) {
									/* Not used anymore = delete content */
									deletecontent(con, Constants.TYP_CONTACT,
											delid);
								}
							}
						}
					}
				}
			}

		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
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
				// Do nothing we are closing
			}
		}
		logger.debug("End deleteMessageFromChat with Out = " + out.toString());
	}

	private int deletemsg(Connection con, int id) throws SQLException {

		String SQL = "DELETE FROM Messages WHERE ID = ? ";
		PreparedStatement pstmt = null;

		pstmt = con.prepareStatement(SQL);
		pstmt.setInt(1, id);
		int retdel = pstmt.executeUpdate();

		return retdel;
	}

	private boolean deletecontent(Connection con, String msgType, int id)
			throws SQLException {

		boolean ret = false;
		String SQL = null;
		PreparedStatement pstmt = null;
		Statement st1 = con.createStatement();
		ResultSet rs1 = null;

		if (msgType.equalsIgnoreCase(Constants.TYP_TEXT)) {
			SQL = "DELETE FROM Text WHERE ID = ? ";
		} else if (msgType.equalsIgnoreCase(Constants.TYP_IMAGE)) {
			rs1 = st1.executeQuery("Select * from Image where ID = " + id);
			if (rs1 != null && rs1.next()) {
				String filename = rs1.getString("Image");
				File file = new File(Constants.SERVER_UPLOAD_LOCATION_FOLDER
						+ Constants.SERVER_IMAGE_FOLDER + filename);
				if (file.exists()) {
					file.delete();
				}
			}
			SQL = "DELETE FROM Image WHERE ID = ? ";
		} else if (msgType.equalsIgnoreCase(Constants.TYP_VIDEO)) {
			rs1 = st1.executeQuery("Select * from Video where ID = " + id);
			if (rs1 != null && rs1.next()) {
				String filename = rs1.getString("Video");
				File file = new File(Constants.SERVER_UPLOAD_LOCATION_FOLDER
						+ Constants.SERVER_VIDEO_FOLDER + filename);
				if (file.exists()) {
					file.delete();
				}
			}
			SQL = "DELETE FROM Video WHERE ID = ? ";
		} else if (msgType.equalsIgnoreCase(Constants.TYP_LOCATION)) {
			SQL = "DELETE FROM Location WHERE ID = ? ";
		} else if (msgType.equalsIgnoreCase(Constants.TYP_FILE)) {
			rs1 = st1.executeQuery("Select * from File where ID = " + id);
			if (rs1 != null && rs1.next()) {
				String filename = rs1.getString("File");
				File file = new File(Constants.SERVER_UPLOAD_LOCATION_FOLDER
						+ Constants.SERVER_FILE_FOLDER + filename);
				if (file.exists()) {
					file.delete();
				}
			}
			SQL = "DELETE FROM Files WHERE ID = ? ";
		} else if (msgType.equalsIgnoreCase(Constants.TYP_CONTACT)) {
			SQL = "DELETE FROM Contact WHERE ID = ? ";
		}

		pstmt = con.prepareStatement(SQL);
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

	public void acknowledgeMessageDownload(InAcknowledgeMessageDownload in,
			OutAcknowledgeMessageDownload out) {

		logger.debug("Start acknowledgeMessageDownload with In = "
				+ in.toString());
		ResultSet rsmessageinchat = null;
		Statement stmessageinchat = null;
		Statement stcontent = null;
		ResultSet rscontent = null;

		fillUserinfo(in.getUsername());
		out.setAcknowledge(Constants.ACKNOWLEDGE_FALSE);

		try {
			stmessageinchat = con.createStatement();

			rsmessageinchat = stmessageinchat
					.executeQuery("SELECT * FROM Messages WHERE ID = "
							+ in.getMessageID());
			if (rsmessageinchat != null) {
				while (rsmessageinchat.next()) {
					/* We have found the Message, now check the Owner */

					String MsgType = rsmessageinchat.getString("MessageTyp");

					if (MsgType.equalsIgnoreCase(Constants.TYP_TEXT)) {
						stcontent = con.createStatement();
						rscontent = stcontent
								.executeQuery("select * from Text where ID = "
										+ rsmessageinchat.getInt("TextMsgID"));
						if (rscontent != null) {
							while (rscontent.next()) {
								String Msg = base64Decode(rscontent
										.getString("Text"));

								int hashCode = Msg.hashCode();

								if (hashCode == Integer.valueOf(in
										.getAcknowledge())) {
									out.setAcknowledge(Constants.ACKNOWLEDGE_TRUE);
									out.setMessageID(in.getMessageID());
								}
							}
						}

					} else if (MsgType.equalsIgnoreCase(Constants.TYP_IMAGE)) {
						stcontent = con.createStatement();
						rscontent = stcontent
								.executeQuery("select * from Image where ID = "
										+ rsmessageinchat.getInt("ImageMsgID"));
						if (rscontent != null) {
							while (rscontent.next()) {
								String dback = rscontent.getString("MD5Sum");
								if (in.getAcknowledge().equals(dback)) {
									out.setAcknowledge(Constants.ACKNOWLEDGE_TRUE);
									out.setMessageID(in.getMessageID());
								}
							}
						}
					} else if (MsgType.equalsIgnoreCase(Constants.TYP_VIDEO)) {
						stcontent = con.createStatement();
						rscontent = stcontent
								.executeQuery("select * from Video where ID = "
										+ rsmessageinchat.getInt("VideoMsgID"));
						if (rscontent != null) {
							while (rscontent.next()) {
								String dback = rscontent.getString("MD5Sum");
								if (in.getAcknowledge().equals(dback)) {
									out.setAcknowledge(Constants.ACKNOWLEDGE_TRUE);
									out.setMessageID(in.getMessageID());
								}
							}
						}

					}

					// Now if the Acknowledge is valid update Readtimestamp in
					// the Database.
					if (out.getAcknowledge().equalsIgnoreCase(
							Constants.ACKNOWLEDGE_TRUE)) {
						String SQLUpdateRead = "UPDATE Messages SET ReadTimestamp = ? WHERE ID = ?";
						PreparedStatement pstmt = null;

						pstmt = con.prepareStatement(SQLUpdateRead);
						pstmt.setLong(1,
								rsmessageinchat.getLong("TempReadTimestamp"));
						pstmt.setInt(2, in.getMessageID());
						pstmt.executeUpdate();
					}
				}
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
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
				// Do nothing we are closing
			}
		}
	}

	public void getMessageInformation(InGetMessageInformation in,
			OutGetMessageInformation out) {
		logger.debug("Start getMessageInformation with In = " + in.toString());
		ResultSet rsoriginmsgid = null;
		Statement storiginmsgid = null;
		Statement sttotal = null;
		ResultSet rstotal = null;
		Statement stread = null;
		ResultSet rsread = null;
		Statement stshow = null;
		ResultSet rsshow = null;

		try {
			storiginmsgid = con.createStatement();

			rsoriginmsgid = storiginmsgid
					.executeQuery("SELECT * FROM Messages WHERE ID = "
							+ in.getMessageID());
			if (rsoriginmsgid != null) {
				while (rsoriginmsgid.next()) {
					/* We have found the Message, now check the Owner */
					sttotal = con.createStatement();
					rstotal = sttotal
							.executeQuery("SELECT Count(*) from Messages where OriginMsgID = "
									+ rsoriginmsgid.getInt("OriginMsgID"));
					if (rstotal != null) {
						while (rstotal.next()) {
							out.setNumberTotal(rstotal.getInt(1));
						}
					}
					stread = con.createStatement();
					rsread = stread
							.executeQuery("SELECT Count(*) from Messages where OriginMsgID = "
									+ rsoriginmsgid.getInt("OriginMsgID")
									+ " AND ReadTimestamp > 0");
					if (rsread != null) {
						while (rsread.next()) {
							out.setNumberRead(rsread.getInt(1));
						}
					}
					stshow = con.createStatement();
					rsshow = stshow
							.executeQuery("SELECT Count(*) from Messages where OriginMsgID = "
									+ rsoriginmsgid.getInt("OriginMsgID")
									+ " AND ShowTimestamp > 0");
					if (rsshow != null) {
						while (rsshow.next()) {
							out.setNumberShow(rsshow.getInt(1));
						}
					}
					out.setMessageID(in.getMessageID());
				}
			}
		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (rsoriginmsgid != null) {
					rsoriginmsgid.close();
				}
				if (storiginmsgid != null) {
					storiginmsgid.close();
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
				if (sttotal != null) {
					sttotal.close();
				}
				if (rstotal != null) {
					rstotal.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
	}

	public void deleteChat(InDeleteChat in, OutDeleteChat out) {
		logger.debug("Start deleteChat with In = " + in.toString());
		ResultSet rsmessageinchat = null;
		Statement stmessageinchat = null;

		try {
			// First we delete all Messages
			stmessageinchat = con.createStatement();
			rsmessageinchat = stmessageinchat
					.executeQuery("SELECT ID FROM Messages WHERE UserToChatID IN (SELECT ID FROM UserToChats WHERE UserToChats.ChatID = "
							+ in.getChatID() + ")");
			if (rsmessageinchat != null) {
				InDeleteMessageFromChat inmsg = new InDeleteMessageFromChat();
				OutDeleteMessageFromChat outmsg = new OutDeleteMessageFromChat();
				while (rsmessageinchat.next()) {
					inmsg.setMessageID(rsmessageinchat.getInt(1));
					deleteMessageFromChat(inmsg, outmsg);
				}
			}
			// Now we delete the assignments of the users to the chat
			String SQLUserToChat = "DELETE FROM UserToChats WHERE ChatID = ? ";
			PreparedStatement pstmtU2C = null;

			pstmtU2C = con.prepareStatement(SQLUserToChat);
			pstmtU2C.setInt(1, in.getChatID());
			pstmtU2C.executeUpdate();

			// At least we delete the chat.
			String SQLChat = "DELETE FROM Chats WHERE ID = ? ";
			PreparedStatement pstmtChat = null;

			pstmtChat = con.prepareStatement(SQLChat);
			pstmtChat.setInt(1, in.getChatID());
			pstmtChat.executeUpdate();
			out.setResult(Constants.CHAT_DELETED);

		} catch (SQLException e) {
			out.setErrortext(Constants.DB_ERROR);
			e.printStackTrace();
		} finally {
			try {
				if (rsmessageinchat != null) {
					rsmessageinchat.close();
				}
				if (stmessageinchat != null) {
					stmessageinchat.close();
				}
			} catch (SQLException e) {
				// Do nothing we are closing
			}
		}
	}
}