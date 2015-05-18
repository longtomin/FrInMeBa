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

import java.sql.Connection;
import java.sql.SQLException;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import de.radiohacks.frinmeba.database.Check;
import de.radiohacks.frinmeba.database.MySqlConnection;
import de.radiohacks.frinmeba.model.InAcknowledgeMessageDownload;
import de.radiohacks.frinmeba.model.InAddUserToChat;
import de.radiohacks.frinmeba.model.InAuthenticate;
import de.radiohacks.frinmeba.model.InCheckNewMessages;
import de.radiohacks.frinmeba.model.InCreateChat;
import de.radiohacks.frinmeba.model.InDeleteChat;
import de.radiohacks.frinmeba.model.InDeleteMessageFromChat;
import de.radiohacks.frinmeba.model.InFetchMessageFromChat;
import de.radiohacks.frinmeba.model.InFetchTextMessage;
import de.radiohacks.frinmeba.model.InGetMessageInformation;
import de.radiohacks.frinmeba.model.InInsertMessageIntoChat;
import de.radiohacks.frinmeba.model.InListChat;
import de.radiohacks.frinmeba.model.InListUser;
import de.radiohacks.frinmeba.model.InRemoveUserFromChat;
import de.radiohacks.frinmeba.model.InSendTextMessage;
import de.radiohacks.frinmeba.model.InSetShowTimeStamp;
import de.radiohacks.frinmeba.model.InSignup;
import de.radiohacks.frinmeba.model.OutAcknowledgeMessageDownload;
import de.radiohacks.frinmeba.model.OutAddUserToChat;
import de.radiohacks.frinmeba.model.OutAuthenticate;
import de.radiohacks.frinmeba.model.OutCheckNewMessages;
import de.radiohacks.frinmeba.model.OutCreateChat;
import de.radiohacks.frinmeba.model.OutDeleteChat;
import de.radiohacks.frinmeba.model.OutDeleteMessageFromChat;
import de.radiohacks.frinmeba.model.OutFetchMessageFromChat;
import de.radiohacks.frinmeba.model.OutFetchTextMessage;
import de.radiohacks.frinmeba.model.OutGetMessageInformation;
import de.radiohacks.frinmeba.model.OutInsertMessageIntoChat;
import de.radiohacks.frinmeba.model.OutListChat;
import de.radiohacks.frinmeba.model.OutListUser;
import de.radiohacks.frinmeba.model.OutRemoveUserFromChat;
import de.radiohacks.frinmeba.model.OutSendTextMessage;
import de.radiohacks.frinmeba.model.OutSetShowTimeStamp;
import de.radiohacks.frinmeba.model.OutSignUp;
import de.radiohacks.frinmeba.util.ServiceUtil;

@Path("/user")
public class ServiceImpl implements ServiceUtil {

	static final Logger logger = Logger.getLogger(ServiceImpl.class);

	@Override
	public OutAuthenticate AuthenticateUser(String User, String Password) {
		logger.debug("Start AuthenticateUser with User = " + User
				+ " Password = " + Password);

		OutAuthenticate out = new OutAuthenticate();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				InAuthenticate in = new InAuthenticate();

				in.setPassword(actuser.base64Decode(Password));
				// in.setPassword(actuser.base64Decode(Password));
				in.setUsername(User);

				/* First check if the User is valid */
				actuser.authenticate(in, out);
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
					out.setAuthenticated(Constants.AUTHENTICATE_FALSE);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
					out.setAuthenticated(Constants.AUTHENTICATE_FALSE);
				}
			}
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				out.setAuthenticated(Constants.AUTHENTICATE_FALSE);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
				out.setAuthenticated(Constants.AUTHENTICATE_FALSE);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End AuthenticateUser with User = " + User
				+ " Password = " + Password);
		return out;
	}

	@Override
	public OutInsertMessageIntoChat insertMessageIntoChat(String User,
			String Password, int ChatID, int MessageID, String MessageType) {

		logger.debug("Start insertMessageIntoChat with User = " + User
				+ " Password = " + Password + " ChatID = " + ChatID
				+ " MessageID = " + MessageID + " MessageType = " + MessageType);
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OutInsertMessageIntoChat out = new OutInsertMessageIntoChat();

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				if (actcheck.checkValueMust(MessageType)) {
					InInsertMessageIntoChat in = new InInsertMessageIntoChat();
					in.setUsername(actuser.base64Decode(User));
					in.setPassword(actuser.base64Decode(Password));
					in.setChatID(ChatID);
					in.setMessageID(MessageID);
					in.setMessageTyp(actuser.base64Decode(MessageType));

					InAuthenticate inauth = new InAuthenticate();
					OutAuthenticate outauth = new OutAuthenticate();
					inauth.setPassword(actuser.base64Decode(Password));
					inauth.setUsername(User);

					actuser.authenticate(inauth, outauth);

					if (outauth.getAuthenticated().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setErrortext(outauth.getErrortext());
					} else {
						/* Check if Chat exists */
						if (!actcheck.CheckChatID(in.getChatID())) {
							out.setErrortext(Constants.NONE_EXISTING_CHAT);
						} else {
							/* Check if Message exists */
							if (!actcheck.CheckContenMessageID(
									in.getMessageID(), in.getMessageTyp())) {
								out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
							} else {
								/* Check if it is a vaid Message Type */
								if (!actcheck.CheckMessageType(in
										.getMessageTyp())) {
									out.setErrortext(Constants.INVALID_MESSAGE_TYPE);
								} else {
									actuser.insertMessageIntoChat(in, out);
								}
							}
						}
					}
					// Messagetype check failed
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setErrortext(Constants.INVALID_MESSAGE_TYPE);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setErrortext(Constants.ENCODING_ERROR);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End insertMessageIntoChat with User = " + User
				+ " Password = " + Password + " ChatID = " + ChatID
				+ " MessageID = " + MessageID + " MessageType = " + MessageType);
		return out;
	}

	@Override
	public OutSendTextMessage sendTextMessage(String User, String Password,
			String TextMessage) {

		logger.debug("Start sendTextMessage with User = " + User
				+ " Password = " + Password + " TextMessage = " + TextMessage);

		OutSendTextMessage out = new OutSendTextMessage();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				if (actcheck.checkValueMust(TextMessage)) {

					InSendTextMessage in = new InSendTextMessage();
					in.setPassword(actuser.base64Decode(Password));
					in.setUsername(actuser.base64Decode(User));
					in.setTextMessage(TextMessage);

					InAuthenticate inauth = new InAuthenticate();
					OutAuthenticate outauth = new OutAuthenticate();
					inauth.setPassword(actuser.base64Decode(Password));
					inauth.setUsername(User);
					actuser.authenticate(inauth, outauth);

					if (outauth.getAuthenticated().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setErrortext(outauth.getErrortext());
					} else {
						actuser.sendTextMessage(in, out);
					}
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setErrortext(Constants.NO_TEXTMESSAGE_GIVEN);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setErrortext(Constants.ENCODING_ERROR);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End sendTextMessage with User = " + User + " Password = "
				+ Password + " TextMessage = " + TextMessage);
		return out;
	}

	@Override
	public OutSignUp SingUpUser(String User, String Password, String Email) {

		logger.debug("Start SingUpUser with User = " + User + " Password = "
				+ Password + " Email = " + Email);

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OutSignUp out = new OutSignUp();

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				if (actcheck.checkValueMust(Email)) {
					InSignup in = new InSignup();
					in.setUsername(User);
					in.setPassword(actuser.base64Decode(Password));
					in.setEmail(actuser.base64Decode(Email));

					if (!actcheck.checkEmail(in.getEmail())) {
						out.setErrortext(Constants.INVALID_EMAIL_ADRESS);
					} else {
						actuser.signUp(in, out);
					}
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setErrortext(Constants.INVALID_EMAIL_ADRESS);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setErrortext(Constants.ENCODING_ERROR);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End SingUpUser with User = " + User + " Password = "
				+ Password + " Email = " + Email);

		return out;
	}

	@Override
	public OutCreateChat CreateChat(String User, String Password,
			String Chatname) {

		logger.debug("Start CreateChat with User = " + User + " Password = "
				+ Password + " Chatname = " + Chatname);

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		OutCreateChat out = new OutCreateChat();
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				if (actcheck.checkValueMust(Chatname)) {
					InCreateChat in = new InCreateChat();
					in.setPassword(actuser.base64Decode(Password));
					in.setUsername(actuser.base64Decode(User));
					in.setChatname(actuser.base64Decode(Chatname));

					InAuthenticate inauth = new InAuthenticate();
					OutAuthenticate outauth = new OutAuthenticate();
					inauth.setPassword(actuser.base64Decode(Password));
					inauth.setUsername(User);
					actuser.authenticate(inauth, outauth);

					if (outauth.getAuthenticated().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setErrortext(outauth.getErrortext());
					} else {
						actuser.createChat(in, out);
					}
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setErrortext(Constants.MISSING_CHATNAME);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setErrortext(Constants.ENCODING_ERROR);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End CreateChat with User = " + User + " Password = "
				+ Password + " Chatname = " + Chatname);
		return out;
	}

	@Override
	public OutDeleteChat DeleteChat(String User, String Password, int ChatID) {
		logger.debug("Start DeleteChat with User = " + User + " Password = "
				+ Password + " ChatID = " + ChatID);

		OutDeleteChat out = new OutDeleteChat();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				InDeleteChat in = new InDeleteChat();
				in.setChatID(ChatID);
				in.setPassword(actuser.base64Decode(Password));
				in.setUsername(actuser.base64Decode(User));

				InAuthenticate inauth = new InAuthenticate();
				OutAuthenticate outauth = new OutAuthenticate();
				inauth.setPassword(actuser.base64Decode(Password));
				inauth.setUsername(User);
				actuser.authenticate(inauth, outauth);

				if (outauth.getAuthenticated().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setErrortext(outauth.getErrortext());
				} else {
					/* Check if Chat exists */
					if (!actcheck.CheckChatID(in.getChatID())) {
						out.setErrortext(Constants.NONE_EXISTING_CHAT);
					} else {
						actuser.deleteChat(in, out);
					}
				}
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End DeleteChat with User = " + User + " Password = "
				+ Password + " ChatID = " + ChatID);
		return out;
	}

	@Override
	public OutAddUserToChat AddUserToChat(String User, String Password,
			int UserID, int ChatID) {
		logger.debug("Start AddUserToChat with User = " + User + " Password = "
				+ Password + " ChatID = " + ChatID + "UserID = " + UserID);

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OutAddUserToChat out = new OutAddUserToChat();

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				InAddUserToChat in = new InAddUserToChat();
				in.setChatID(ChatID);
				in.setUserID(UserID);
				in.setPassword(actuser.base64Decode(Password));
				in.setUsername(actuser.base64Decode(User));

				InAuthenticate inauth = new InAuthenticate();
				OutAuthenticate outauth = new OutAuthenticate();
				inauth.setPassword(actuser.base64Decode(Password));
				inauth.setUsername(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getAuthenticated().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setErrortext(outauth.getErrortext());
				} else {
					/* Check if Chat exists */
					if (!actcheck.CheckChatID(in.getChatID())) {
						out.setErrortext(Constants.NONE_EXISTING_CHAT);
					} else {
						/* Check if Message exists */
						if (!actcheck.CheckUserID(in.getUserID())) {
							out.setErrortext(Constants.NONE_EXISTING_USER);
						} else {
							actuser.addUserToChat(in, out);
						}
					}
				}
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End AddUserToChat with User = " + User + " Password = "
				+ Password + " ChatID = " + ChatID + "UserID = " + UserID);
		return out;
	}

	@Override
	public OutRemoveUserFromChat RemoveUserFromChat(String User,
			String Password, int ChatID, int UserID) {

		logger.debug("Start RemoveUserFromChat with User = " + User
				+ " Password = " + Password + " ChatID = " + ChatID
				+ "UserID = " + UserID);

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OutRemoveUserFromChat out = new OutRemoveUserFromChat();

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				InRemoveUserFromChat in = new InRemoveUserFromChat();
				in.setChatID(ChatID);
				in.setUserID(UserID);
				in.setPassword(actuser.base64Decode(Password));
				in.setUsername(actuser.base64Decode(User));

				InAuthenticate inauth = new InAuthenticate();
				OutAuthenticate outauth = new OutAuthenticate();
				inauth.setPassword(actuser.base64Decode(Password));
				inauth.setUsername(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getAuthenticated().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setErrortext(outauth.getErrortext());
				} else {
					/* Check if Chat exists */
					if (!actcheck.CheckChatID(in.getChatID())) {
						out.setErrortext(Constants.NONE_EXISTING_CHAT);
					} else {
						/* Check if Message exists */
						if (!actcheck.CheckUserID(in.getUserID())) {
							out.setErrortext(Constants.NONE_EXISTING_USER);
						} else {
							actuser.removeUserFromChat(in, out);
						}
					}
				}
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End RemoveUserFromChat with User = " + User
				+ " Password = " + Password + " ChatID = " + ChatID
				+ "UserID = " + UserID);
		return out;
	}

	@Override
	public OutListUser ListUsers(String User, String Password, String search) {
		logger.debug("Start ListUsers with User = " + User + " Password = "
				+ Password);

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OutListUser out = new OutListUser();

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				if (actcheck.checkValueCan(search)) {
					InListUser in = new InListUser();
					in.setPassword(actuser.base64Decode(Password));
					in.setUsername(actuser.base64Decode(User));
					if (search != null && !search.isEmpty()) {
						in.setSearch(actuser.base64Decode(search));
					} else {
						in.setSearch("");
					}
					InAuthenticate inauth = new InAuthenticate();
					OutAuthenticate outauth = new OutAuthenticate();
					inauth.setPassword(actuser.base64Decode(Password));
					inauth.setUsername(User);
					actuser.authenticate(inauth, outauth);

					if (outauth.getAuthenticated().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setErrortext(outauth.getErrortext());
					} else {
						actuser.listUser(in, out);
					}
					// Search check failed
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setErrortext(Constants.ENCODING_ERROR);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End ListUsers with User = " + User + " Password = "
				+ Password);
		return out;
	}

	@Override
	public OutListChat ListChats(String User, String Password) {
		logger.debug("Start ListChats with User = " + User + " Password = "
				+ Password);

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		OutListChat out = new OutListChat();
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				InListChat in = new InListChat();
				in.setPassword(actuser.base64Decode(Password));
				in.setUsername(actuser.base64Decode(User));

				InAuthenticate inauth = new InAuthenticate();
				OutAuthenticate outauth = new OutAuthenticate();
				inauth.setPassword(actuser.base64Decode(Password));
				inauth.setUsername(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getAuthenticated().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setErrortext(outauth.getErrortext());
				} else {
					actuser.listChat(in, out);
				}
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End ListChats with User = " + User + " Password = "
				+ Password);
		return out;
	}

	@Override
	public OutFetchMessageFromChat getMessageFromChat(String User,
			String Password, int ChatID, int Timestamp) {
		logger.debug("Start getMessageFromChat with User = " + User
				+ " Password = " + Password + " ChatID = " + ChatID);

		OutFetchMessageFromChat out = new OutFetchMessageFromChat();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				InFetchMessageFromChat in = new InFetchMessageFromChat();
				in.setChatID(ChatID);
				in.setPassword(actuser.base64Decode(Password));
				in.setUsername(actuser.base64Decode(User));
				in.setTimeStamp(Timestamp);

				InAuthenticate inauth = new InAuthenticate();
				OutAuthenticate outauth = new OutAuthenticate();
				inauth.setPassword(actuser.base64Decode(Password));
				inauth.setUsername(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getAuthenticated().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setErrortext(outauth.getErrortext());
				} else {
					/* Check if Chat exists */
					if (!actcheck.CheckChatID(in.getChatID())) {
						out.setErrortext(Constants.NONE_EXISTING_CHAT);
					} else {
						actuser.getMessagesFromChat(in, out);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End getMessageFromChat with User = " + User
				+ " Password = " + Password + " ChatID = " + ChatID);
		return out;
	}

	@Override
	public OutFetchTextMessage getTextMessage(String User, String Password,
			int TextMessageID) {
		logger.debug("Start getTextMessage with User = " + User
				+ " Password = " + Password + " TextMessageID = "
				+ TextMessageID);

		OutFetchTextMessage out = new OutFetchTextMessage();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				InFetchTextMessage in = new InFetchTextMessage();
				in.setTextID(TextMessageID);
				in.setPassword(actuser.base64Decode(Password));
				in.setUsername(actuser.base64Decode(User));

				InAuthenticate inauth = new InAuthenticate();
				OutAuthenticate outauth = new OutAuthenticate();
				inauth.setPassword(actuser.base64Decode(Password));
				inauth.setUsername(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getAuthenticated().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setErrortext(outauth.getErrortext());
				} else {
					/* Check if Chat exists */
					if (!actcheck.CheckContenMessageID(TextMessageID,
							Constants.TYP_TEXT)) {
						out.setErrortext(Constants.NONE_EXISTING_CONTENT_MESSAGE);
					} else {
						actuser.getTextMessages(in, out);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End getTextMessage with User = " + User + " Password = "
				+ Password + " TextMessageID = " + TextMessageID);
		return out;
	}

	@Override
	public OutCheckNewMessages checkNewMessages(String User, String Password) {
		logger.debug("Start checkNewMessages with User = " + User
				+ " Password = " + Password);

		OutCheckNewMessages out = new OutCheckNewMessages();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				InCheckNewMessages in = new InCheckNewMessages();
				in.setPassword(actuser.base64Decode(Password));
				in.setUsername(actuser.base64Decode(User));

				InAuthenticate inauth = new InAuthenticate();
				OutAuthenticate outauth = new OutAuthenticate();
				inauth.setPassword(actuser.base64Decode(Password));
				inauth.setUsername(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getAuthenticated().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setErrortext(outauth.getErrortext());
				} else {
					/* Check if Chat exists */
					actuser.checkNewMessages(in, out);
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End checkNewMessages with User = " + User
				+ " Password = " + Password);
		return out;
	}

	@Override
	public OutDeleteMessageFromChat deleteMessageFromChat(String User,
			String Password, int MessageID) {

		logger.debug("Start deleteMessageFromChat with User = " + User
				+ " Password = " + Password);

		OutDeleteMessageFromChat out = new OutDeleteMessageFromChat();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				InDeleteMessageFromChat in = new InDeleteMessageFromChat();
				in.setPassword(actuser.base64Decode(Password));
				in.setUsername(actuser.base64Decode(User));
				in.setMessageID(MessageID);

				InAuthenticate inauth = new InAuthenticate();
				OutAuthenticate outauth = new OutAuthenticate();
				inauth.setPassword(actuser.base64Decode(Password));
				inauth.setUsername(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getAuthenticated().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setErrortext(outauth.getErrortext());
				} else {
					if (MessageID > 0) {
						if (actcheck.CheckMessageID(MessageID)) {
							actuser.fillUserinfo(in.getUsername());
							if (actcheck.CheckOwnMessage(actuser.getID(),
									in.getMessageID())) {
								actuser.deleteMessageFromChat(in, out);
							} else {
								out.setErrortext(Constants.NOT_MESSAGE_OWNER);
							}	
						} else {
							out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
						}
					} else {
						out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End checkNewMessages with User = " + User
				+ " Password = " + Password);
		return out;
	}

	@Override
	public OutSetShowTimeStamp setShowTimeStamp(String User, String Password,
			int MessageID) {
		logger.debug("Start setShowTimeStamp with User = " + User
				+ " Password = " + Password + " MessageID = " + MessageID);

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OutSetShowTimeStamp out = new OutSetShowTimeStamp();

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				InSetShowTimeStamp in = new InSetShowTimeStamp();
				in.setUsername(actuser.base64Decode(User));
				in.setPassword(actuser.base64Decode(Password));
				in.setMessageID(MessageID);

				InAuthenticate inauth = new InAuthenticate();
				OutAuthenticate outauth = new OutAuthenticate();
				inauth.setPassword(actuser.base64Decode(Password));
				inauth.setUsername(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getAuthenticated().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setErrortext(outauth.getErrortext());
				} else {
					/* Check if Message exists */
					if (!actcheck.CheckMessageID(in.getMessageID())) {
						out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
					} else {
						if (!actcheck.CheckMessageIDReadTimestamp(in
								.getMessageID())) {
							out.setErrortext(Constants.MESSAGE_NOT_READ);
						} else {
							// Check if it is your own Message. Do not update
							// ShowTimeStamps for other people
							actuser.fillUserinfo(in.getUsername());
							if (actcheck.CheckOwnMessage(actuser.getID(),
									in.getMessageID())) {
								actuser.setShowTimeStamp(in, out);
							} else {
								out.setErrortext(Constants.NOT_MESSAGE_OWNER);
							}
						}
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End setShowTimeStamp with User = " + User
				+ " Password = " + " MessageID = " + MessageID);
		return out;
	}

	@Override
	public OutGetMessageInformation getMessageInformation(String User,
			String Password, int MessageID) {
		logger.debug("Start getMessageInformation with User = " + User
				+ " Password = " + Password);

		OutGetMessageInformation out = new OutGetMessageInformation();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				InGetMessageInformation in = new InGetMessageInformation();
				in.setPassword(actuser.base64Decode(Password));
				in.setUsername(actuser.base64Decode(User));
				in.setMessageID(MessageID);

				InAuthenticate inauth = new InAuthenticate();
				OutAuthenticate outauth = new OutAuthenticate();
				inauth.setPassword(actuser.base64Decode(Password));
				inauth.setUsername(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getAuthenticated().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setErrortext(outauth.getErrortext());
				} else {
					if (MessageID > 0) {
						if (actcheck.CheckMessageID(MessageID)) {
							actuser.getMessageInformation(in, out);
						} else {
							out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
						}
					} else {
						out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End getMessageInformation with User = " + User
				+ " Password = " + Password);
		return out;
	}

	@Override
	public OutAcknowledgeMessageDownload acknowledgeMessageDownload(
			String User, String Password, int MessageID, String Acknowledge) {
		logger.debug("Start acknowledgeMessageDownload with User = " + User
				+ " Password = " + Password);

		OutAcknowledgeMessageDownload out = new OutAcknowledgeMessageDownload();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				if (actcheck.checkValueMust(Acknowledge)) {
					InAcknowledgeMessageDownload in = new InAcknowledgeMessageDownload();
					in.setPassword(actuser.base64Decode(Password));
					in.setUsername(actuser.base64Decode(User));
					in.setMessageID(MessageID);
					in.setAcknowledge(actuser.base64Decode(Acknowledge));

					InAuthenticate inauth = new InAuthenticate();
					OutAuthenticate outauth = new OutAuthenticate();
					inauth.setPassword(actuser.base64Decode(Password));
					inauth.setUsername(User);

					actuser.authenticate(inauth, outauth);

					if (outauth.getAuthenticated().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setErrortext(outauth.getErrortext());
					} else {
						if (MessageID > 0) {
							if (actcheck.CheckMessageID(MessageID)) {
								actuser.acknowledgeMessageDownload(in, out);
							} else {
								out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
							}
						} else {
							out.setErrortext(Constants.NONE_EXISTING_MESSAGE);
						}
					}
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setErrortext(Constants.NO_CONTENT_GIVEN);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setErrortext(Constants.ENCODING_ERROR);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setErrortext(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setErrortext(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setErrortext(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End acknowledgeMessageDownload with User = " + User
				+ " Password = " + Password);
		return out;
	}
}