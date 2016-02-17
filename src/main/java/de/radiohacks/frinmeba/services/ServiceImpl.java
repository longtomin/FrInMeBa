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
import java.util.List;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import de.radiohacks.frinmeba.database.Check;
import de.radiohacks.frinmeba.database.MyConnection;
import de.radiohacks.frinmeba.modelshort.IAckCD;
import de.radiohacks.frinmeba.modelshort.IAckMD;
import de.radiohacks.frinmeba.modelshort.IAdUC;
import de.radiohacks.frinmeba.modelshort.IAuth;
import de.radiohacks.frinmeba.modelshort.ICN;
import de.radiohacks.frinmeba.modelshort.ICrCh;
import de.radiohacks.frinmeba.modelshort.IDMFC;
import de.radiohacks.frinmeba.modelshort.IDeCh;
import de.radiohacks.frinmeba.modelshort.IFMFC;
import de.radiohacks.frinmeba.modelshort.IGMI;
import de.radiohacks.frinmeba.modelshort.IGTeM;
import de.radiohacks.frinmeba.modelshort.IICIc;
import de.radiohacks.frinmeba.modelshort.IIMIC;
import de.radiohacks.frinmeba.modelshort.IIUIc;
import de.radiohacks.frinmeba.modelshort.ILiCh;
import de.radiohacks.frinmeba.modelshort.ILiUs;
import de.radiohacks.frinmeba.modelshort.IReUC;
import de.radiohacks.frinmeba.modelshort.ISShT;
import de.radiohacks.frinmeba.modelshort.ISTeM;
import de.radiohacks.frinmeba.modelshort.ISU;
import de.radiohacks.frinmeba.modelshort.ISiUp;
import de.radiohacks.frinmeba.modelshort.OAckCD;
import de.radiohacks.frinmeba.modelshort.OAckMD;
import de.radiohacks.frinmeba.modelshort.OAdUC;
import de.radiohacks.frinmeba.modelshort.OAuth;
import de.radiohacks.frinmeba.modelshort.OCN;
import de.radiohacks.frinmeba.modelshort.OCrCh;
import de.radiohacks.frinmeba.modelshort.ODMFC;
import de.radiohacks.frinmeba.modelshort.ODeCh;
import de.radiohacks.frinmeba.modelshort.OFMFC;
import de.radiohacks.frinmeba.modelshort.OGMI;
import de.radiohacks.frinmeba.modelshort.OGTeM;
import de.radiohacks.frinmeba.modelshort.OICIc;
import de.radiohacks.frinmeba.modelshort.OIMIC;
import de.radiohacks.frinmeba.modelshort.OIUIc;
import de.radiohacks.frinmeba.modelshort.OLiCh;
import de.radiohacks.frinmeba.modelshort.OLiUs;
import de.radiohacks.frinmeba.modelshort.OReUC;
import de.radiohacks.frinmeba.modelshort.OSShT;
import de.radiohacks.frinmeba.modelshort.OSTeM;
import de.radiohacks.frinmeba.modelshort.OSU;
import de.radiohacks.frinmeba.modelshort.OSiUp;
import de.radiohacks.frinmeba.util.IServiceUtil;

@Path("/user")
public class ServiceImpl implements IServiceUtil {

	private static final Logger LOGGER = Logger.getLogger(ServiceImpl.class.getName());

	@Override
	public OAuth authenticateUser(String user, String password) {
		LOGGER.debug("Start AuthenticateUser with User = " + user
				+ " Password = " + password);

		OAuth out = new OAuth();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {

				IAuth in = new IAuth();

				in.setPW(actuser.base64Decode(password));
				in.setUN(user);

				/* First check if the User is valid */
				actuser.authenticate(in, out);
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
					out.setA(Constants.AUTHENTICATE_FALSE);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
					out.setA(Constants.AUTHENTICATE_FALSE);
				}
			}
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				out.setA(Constants.AUTHENTICATE_FALSE);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
				out.setA(Constants.AUTHENTICATE_FALSE);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End AuthenticateUser with User = " + user
				+ " Password = " + password);
		return out;
	}

	@Override
	public OIMIC insertMessageIntoChat(IIMIC in) {

		LOGGER.debug("Start insertMessageIntoChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " ChatID = " + in.getCID()
				+ " MessageID = " + in.getMID() + " MessageType = "
				+ in.getMT());
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OIMIC out = new OIMIC();

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				if (actcheck.checkValueMust(in.getMT())) {
					IAuth inauth = new IAuth();
					OAuth outauth = new OAuth();
					inauth.setPW(actuser.base64Decode(in.getPW()));
					inauth.setUN(in.getUN());

					actuser.authenticate(inauth, outauth);

					if (outauth.getA().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setET(outauth.getET());
					} else {
						String tmp = actuser.base64Decode(in.getUN());
						in.setUN(tmp);
						tmp = actuser.base64Decode(in.getPW());
						in.setPW(tmp);
						tmp = actuser.base64Decode(in.getMT());
						in.setMT(tmp);

						/* Check if Chat exists */
						if (!actcheck.checkChatID(in.getCID())) {
							out.setET(Constants.NONE_EXISTING_CHAT);
						} else {
							/* Check if Message exists */
							if (!actcheck.checkContenMessageID(in.getMID(),
									in.getMT())) {
								out.setET(Constants.NONE_EXISTING_MESSAGE);
							} else {
								/* Check if it is a vaid Message Type */
								if (!actcheck.checkMessageType(in.getMT())) {
									out.setET(Constants.INVALID_MESSAGE_TYPE);
								} else {
									actuser.insertMessageIntoChat(in, out);
								}
							}
						}
					}
					/* Messagetype check failed */
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setET(Constants.INVALID_MESSAGE_TYPE);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setET(Constants.ENCODING_ERROR);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End insertMessageIntoChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " ChatID = " + in.getCID()
				+ " MessageID = " + in.getMID() + " MessageType = "
				+ in.getMT());
		return out;
	}

	@Override
	public OSTeM sendTextMessage(ISTeM in) {

		LOGGER.debug("Start sendTextMessage with User = " + in.getUN()
				+ " Password = " + in.getPW() + " TextMessage = " + in.getTM());

		OSTeM out = new OSTeM();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				if (actcheck.checkValueMust(in.getTM())) {

					IAuth inauth = new IAuth();
					OAuth outauth = new OAuth();
					inauth.setPW(actuser.base64Decode(in.getPW()));
					inauth.setUN(in.getUN());
					actuser.authenticate(inauth, outauth);

					if (outauth.getA().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setET(outauth.getET());
					} else {
						String tmp = actuser.base64Decode(in.getUN());
						in.setUN(tmp);
						tmp = actuser.base64Decode(in.getPW());
						in.setPW(tmp);

						actuser.sendTextMessage(in, out);
					}
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setET(Constants.NO_TEXTMESSAGE_GIVEN);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setET(Constants.ENCODING_ERROR);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End sendTextMessage with User = " + in.getUN()
				+ " Password = " + in.getPW() + " TextMessage = " + in.getTM());
		return out;
	}

	@Override
	public OSiUp signUpUser(ISiUp in) {

		LOGGER.debug("Start SingUpUser with User = " + in.getUN()
				+ " Password = " + in.getPW() + " Email = " + in.getE());

		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OSiUp out = new OSiUp();

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				if (actcheck.checkValueMust(in.getE())) {
					String tmp = actuser.base64Decode(in.getPW());
					in.setPW(tmp);
					tmp = actuser.base64Decode(in.getE());
					in.setE(tmp);

					if (!actcheck.checkEmail(in.getE())) {
						out.setET(Constants.INVALID_EMAIL_ADRESS);
					} else {
						actuser.signUp(in, out);
					}
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setET(Constants.INVALID_EMAIL_ADRESS);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setET(Constants.ENCODING_ERROR);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End SingUpUser with User = " + in.getUN()
				+ " Password = " + in.getPW() + " Email = " + in.getE());

		return out;
	}

	@Override
	public OCrCh createChat(ICrCh in) {

		LOGGER.debug("Start CreateChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " Chatname = " + in.getCN());

		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		OCrCh out = new OCrCh();
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				if (actcheck.checkValueMust(in.getCN())) {
					IAuth inauth = new IAuth();
					OAuth outauth = new OAuth();
					inauth.setPW(actuser.base64Decode(in.getPW()));
					inauth.setUN(in.getUN());
					actuser.authenticate(inauth, outauth);

					if (outauth.getA().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setET(outauth.getET());
					} else {
						String tmp = actuser.base64Decode(in.getPW());
						in.setPW(tmp);
						tmp = actuser.base64Decode(in.getUN());
						in.setUN(tmp);
						tmp = actuser.base64Decode(in.getCN());
						in.setCN(tmp);
						actuser.createChat(in, out);
					}
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setET(Constants.MISSING_CHATNAME);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setET(Constants.ENCODING_ERROR);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End CreateChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " Chatname = " + in.getCN());
		return out;
	}

	@Override
	public ODeCh deleteChat(String user, String password, int chatID) {
		LOGGER.debug("Start DeleteChat with User = " + user + " Password = "
				+ password + " ChatID = " + chatID);

		ODeCh out = new ODeCh();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {
				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(password));
				inauth.setUN(user);
				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					if (!actcheck.checkChatID(chatID)) {
						out.setET(Constants.NONE_EXISTING_CHAT);
					} else {
						IDeCh in = new IDeCh();
						in.setUN(actuser.base64Decode(user));
						in.setPW(actuser.base64Decode(password));
						in.setCID(chatID);
						actuser.deleteChat(in, out);
					}
				}
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End DeleteChat with User = " + user + " Password = "
				+ password + " ChatID = " + chatID);
		return out;
	}

	@Override
	public OAdUC addUserToChat(IAdUC in) {
		LOGGER.debug("Start AddUserToChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " ChatID = " + in.getCID()
				+ "UserID = " + in.getUID());

		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OAdUC out = new OAdUC();

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(in.getPW()));
				inauth.setUN(in.getUN());

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					if (!actcheck.checkChatID(in.getCID())) {
						out.setET(Constants.NONE_EXISTING_CHAT);
					} else {
						/* Check if Message exists */
						if (!actcheck.checkUserID(in.getUID())) {
							out.setET(Constants.NONE_EXISTING_USER);
						} else {
							String tmp = actuser.base64Decode(in.getUN());
							in.setUN(tmp);
							tmp = actuser.base64Decode(in.getPW());
							in.setPW(tmp);
							actuser.addUserToChat(in, out);
						}
					}
				}
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End AddUserToChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " ChatID = " + in.getCID()
				+ "UserID = " + in.getUID());
		return out;
	}

	@Override
	public OReUC removeUserFromChat(String user, String password, int userID,
			int chatID) {

		LOGGER.debug("Start RemoveUserFromChat with User = " + user
				+ " Password = " + password + " ChatID = " + chatID
				+ "UserID = " + userID);

		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OReUC out = new OReUC();

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {
				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(password));
				inauth.setUN(user);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					if (!actcheck.checkChatID(chatID)) {
						out.setET(Constants.NONE_EXISTING_CHAT);
					} else {
						/* Check if Message exists */
						if (!actcheck.checkUserID(userID)) {
							out.setET(Constants.NONE_EXISTING_USER);
						} else {
							IReUC in = new IReUC();
							in.setUN(actuser.base64Decode(user));
							in.setPW(actuser.base64Decode(password));
							in.setUID(userID);
							in.setCID(chatID);
							actuser.removeUserFromChat(in, out);
						}
					}
				}
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End RemoveUserFromChat with User = " + user
				+ " Password = " + password + " ChatID = " + chatID
				+ "UserID = " + userID);
		return out;
	}

	@Override
	public OLiUs listUsers(String user, String password, String search) {
		LOGGER.debug("Start ListUsers with User = " + user + " Password = "
				+ password);

		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OLiUs out = new OLiUs();

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {
				if (actcheck.checkValueCan(search)) {
					ILiUs in = new ILiUs();
					in.setPW(actuser.base64Decode(password));
					in.setUN(actuser.base64Decode(user));
					if (search != null && !search.isEmpty()) {
						in.setS(actuser.base64Decode(search));
					} else {
						in.setS("");
					}
					IAuth inauth = new IAuth();
					OAuth outauth = new OAuth();
					inauth.setPW(actuser.base64Decode(password));
					inauth.setUN(user);
					actuser.authenticate(inauth, outauth);

					if (outauth.getA().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setET(outauth.getET());
					} else {
						actuser.listUser(in, out);
					}
					/* Search check failed */
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setET(Constants.ENCODING_ERROR);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End ListUsers with User = " + user + " Password = "
				+ password);
		return out;
	}

	@Override
	public OLiCh listChats(String user, String password) {
		LOGGER.debug("Start ListChats with User = " + user + " Password = "
				+ password);

		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		OLiCh out = new OLiCh();
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {

				ILiCh in = new ILiCh();
				in.setPW(actuser.base64Decode(password));
				in.setUN(actuser.base64Decode(user));

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(password));
				inauth.setUN(user);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					actuser.listChat(in, out);
				}
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End ListChats with User = " + user + " Password = "
				+ password);
		return out;
	}

	@Override
	public OFMFC getMessageFromChat(String user, String password, int chatID,
			int timestamp) {
		LOGGER.debug("Start getMessageFromChat with User = " + user
				+ " Password = " + password + " ChatID = " + chatID);

		OFMFC out = new OFMFC();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {

				IFMFC in = new IFMFC();
				in.setCID(chatID);
				in.setPW(actuser.base64Decode(password));
				in.setUN(actuser.base64Decode(user));
				in.setRdT(timestamp);

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(password));
				inauth.setUN(user);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					if (!actcheck.checkChatID(in.getCID())) {
						out.setET(Constants.NONE_EXISTING_CHAT);
					} else {
						actuser.getMessagesFromChat(in, out);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End getMessageFromChat with User = " + user
				+ " Password = " + password + " ChatID = " + chatID);
		return out;
	}

	@Override
	public OGTeM getTextMessage(String user, String password, int textMessageID) {
		LOGGER.debug("Start getTextMessage with User = " + user
				+ " Password = " + password + " TextMessageID = "
				+ textMessageID);

		OGTeM out = new OGTeM();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {

				IGTeM in = new IGTeM();
				in.setTextID(textMessageID);
				in.setPW(actuser.base64Decode(password));
				in.setUN(actuser.base64Decode(user));

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(password));
				inauth.setUN(user);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					if (!actcheck.checkContenMessageID(textMessageID,
							Constants.TYP_TEXT)) {
						out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
					} else {
						actuser.getTextMessages(in, out);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End getTextMessage with User = " + user + " Password = "
				+ password + " TextMessageID = " + textMessageID);
		return out;
	}

	@Override
	public OCN checkNew(String user, String password) {
		LOGGER.debug("Start checkNewMessages with User = " + user
				+ " Password = " + password);

		OCN out = new OCN();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {

				ICN in = new ICN();
				in.setPW(actuser.base64Decode(password));
				in.setUN(actuser.base64Decode(user));

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(password));
				inauth.setUN(user);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					actuser.checkNew(in, out);
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End checkNewMessages with User = " + user
				+ " Password = " + password);
		return out;
	}

	@Override
	public ODMFC deleteMessageFromChat(String user, String password,
			int messageID) {

		LOGGER.debug("Start deleteMessageFromChat with User = " + user
				+ " Password = " + password + " MessageID = " + messageID);

		ODMFC out = new ODMFC();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {
				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(password));
				inauth.setUN(user);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					if (messageID > 0) {
						if (actcheck.checkMessageID(messageID)) {
							actuser.fillUserinfo(actuser.base64Decode(user));
							if (actcheck.checkOwnMessage(actuser.getID(),
									messageID)) {
								IDMFC in = new IDMFC();
								in.setUN(actuser.base64Decode(user));
								in.setPW(actuser.base64Decode(password));
								in.setMID(messageID);
								actuser.deleteMessageFromChat(in, out);
							} else {
								out.setET(Constants.NOT_MESSAGE_OWNER);
							}
						} else {
							out.setET(Constants.NONE_EXISTING_MESSAGE);
						}
					} else {
						out.setET(Constants.NONE_EXISTING_MESSAGE);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
				/* e.printStackTrace(); */
			}
		}

		LOGGER.debug("End checkNewMessages with User = " + user
				+ " Password = " + password);
		return out;
	}

	@Override
	public OSShT setShowTimeStamp(ISShT in) {
		LOGGER.debug("Start setShowTimeStamp with User = " + in.getUN()
				+ " Password = " + in.getPW() + " MessageID = " + in.getMID());

		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OSShT out = new OSShT();

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(in.getPW()));
				inauth.setUN(in.getUN());

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					actuser.fillUserinfo(actuser.base64Decode(in.getUN()));
					String tmp = actuser.base64Decode(in.getUN());
					in.setUN(tmp);
					tmp = actuser.base64Decode(in.getPW());
					in.setPW(tmp);
					boolean abort = false;
					// if (!in.getMID().isEmpty() && in.getMID().size() > 0) {
					if (!in.getMID().isEmpty()) {
						for (int i = 0; i < in.getMID().size(); i++) {
							/* Check if Message exists */
							if (!actcheck.checkMessageID(in.getMID().get(i))) {
								out.setET(Constants.NONE_EXISTING_MESSAGE);
								abort = true;
							} else {
								if (!actcheck.checkMessageIDReadTimestamp(in
										.getMID().get(i))) {
									out.setET(Constants.MESSAGE_NOT_READ);
									abort = true;
								} else {
									/* Check if it is your own Message. Do not  */
									/* update                                   */
									/* ShowTimeStamps for other people          */
									if (!actcheck
											.checkOwnMessage(actuser.getID(),
													in.getMID().get(i))) {
										out.setET(Constants.NOT_MESSAGE_OWNER);
										abort = true;
									}
								}
							}
						}
						if (!abort) {
							actuser.setShowTimeStamp(in, out);
						}
					} else {
						out.setET(Constants.NONE_EXISTING_MESSAGE);
					}
				}
			} else {
				/* Password check failed */
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End setShowTimeStamp with User = " + in.getUN()
				+ " Password = " + in.getPW() + " MessageID = " + in.getMID());
		return out;
	}

	@Override
	public OGMI getMessageInformation(String user, String password,
			List<Integer> messageID) {
		LOGGER.debug("Start getMessageInformation with User = " + user
				+ " Password = " + password);

		OGMI out = new OGMI();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {

				IGMI in = new IGMI();
				in.setPW(actuser.base64Decode(password));
				in.setUN(actuser.base64Decode(user));

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(password));
				inauth.setUN(user);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					boolean abort = false;
					//if (!messageID.isEmpty() && messageID.size() > 0) {
					if (!messageID.isEmpty()) {
						for (int i = 0; i < messageID.size(); i++) {
							if (actcheck.checkMessageID(messageID.get(i))) {
								in.getMID().add(messageID.get(i));
							} else {
								out.setET(Constants.NONE_EXISTING_MESSAGE);
								abort = true;
							}
						}
					} else {
						out.setET(Constants.NONE_EXISTING_MESSAGE);
						abort = true;
					}
					if (!abort) {
						actuser.getMessageInformation(in, out);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End getMessageInformation with User = " + user
				+ " Password = " + password);
		return out;
	}

	@Override
	public OAckMD acknowledgeMessageDownload(IAckMD in) {
		LOGGER.debug("Start acknowledgeMessageDownload with User = "
				+ in.getUN() + " Password = " + in.getPW());

		OAckMD out = new OAckMD();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				if (actcheck.checkValueMust(in.getACK())) {
					IAuth inauth = new IAuth();
					OAuth outauth = new OAuth();
					inauth.setPW(actuser.base64Decode(in.getPW()));
					inauth.setUN(in.getUN());

					actuser.authenticate(inauth, outauth);

					if (outauth.getA().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setET(outauth.getET());
					} else {
						if (in.getMID() > 0) {
							if (actcheck.checkMessageID(in.getMID())) {
								String tmp = actuser.base64Decode(in.getUN());
								in.setUN(tmp);
								tmp = actuser.base64Decode(in.getPW());
								in.setPW(tmp);
								tmp = actuser.base64Decode(in.getACK());
								in.setACK(tmp);
								actuser.acknowledgeMessageDownload(in, out);
							} else {
								out.setET(Constants.NONE_EXISTING_MESSAGE);
							}
						} else {
							out.setET(Constants.NONE_EXISTING_MESSAGE);
						}
					}
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setET(Constants.NO_CONTENT_GIVEN);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setET(Constants.ENCODING_ERROR);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End acknowledgeMessageDownload with User = " + in.getUN()
				+ " Password = " + in.getPW());
		return out;
	}

	@Override
	public OAckCD acknowledgeChatDownload(IAckCD in) {
		LOGGER.debug("Start acknowledgeChatDownload with User = " + in.getUN()
				+ " Password = " + in.getPW());

		OAckCD out = new OAckCD();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				if (actcheck.checkValueMust(in.getACK())) {
					IAuth inauth = new IAuth();
					OAuth outauth = new OAuth();
					inauth.setPW(actuser.base64Decode(in.getPW()));
					inauth.setUN(in.getUN());

					actuser.authenticate(inauth, outauth);

					if (outauth.getA().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setET(outauth.getET());
					} else {
						if (in.getCID() > 0) {
							if (actcheck.checkChatID(in.getCID())) {
								String tmp = actuser.base64Decode(in.getUN());
								in.setUN(tmp);
								tmp = actuser.base64Decode(in.getPW());
								in.setPW(tmp);
								tmp = actuser.base64Decode(in.getACK());
								in.setACK(tmp);
								actuser.acknowledgeChatDownload(in, out);
							} else {
								out.setET(Constants.NONE_EXISTING_CHAT);
							}
						} else {
							out.setET(Constants.NONE_EXISTING_CHAT);
						}
					}
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.NO_CONTENT_GIVEN)) {
						out.setET(Constants.NO_CONTENT_GIVEN);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setET(Constants.ENCODING_ERROR);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End acknowledgeChatDownload with User = " + in.getUN()
				+ " Password = " + in.getPW());
		return out;
	}

	@Override
	public OSU syncUser(String user, String password, List<Integer> userID) {
		LOGGER.debug("Start syncuser with User = " + user + " Password = "
				+ password);

		OSU out = new OSU();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(user)) {
			if (actcheck.checkValueMust(password)) {

				ISU in = new ISU();
				in.setPW(actuser.base64Decode(password));
				in.setUN(actuser.base64Decode(user));

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(password));
				inauth.setUN(user);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					boolean abort = false;
					//if (!userID.isEmpty() && userID.size() > 0) {
					if (!userID.isEmpty()) {
						for (int i = 0; i < userID.size(); i++) {
							if (actcheck.checkUserID(userID.get(i))) {
								in.getUID().add(userID.get(i));
							} else {
								out.setET(Constants.NONE_EXISTING_USER);
								abort = true;
							}
						}
					} else {
						out.setET(Constants.NONE_EXISTING_USER);
						abort = true;
					}
					if (!abort) {
						actuser.syncUser(in, out);
					}
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End getMessageInformation with User = " + user
				+ " Password = " + password);
		return out;
	}

	@Override
	public OIUIc insertUserIcon(IIUIc in) {
		LOGGER.debug("Start insertusericon with User = " + in.getUN()
				+ " Icon = " + in.getIcID());

		OIUIc out = new OIUIc();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				if (actcheck.checkContenMessageID(in.getIcID(),
						Constants.TYP_IMAGE)) {
					IAuth inauth = new IAuth();
					OAuth outauth = new OAuth();
					inauth.setPW(actuser.base64Decode(in.getPW()));
					inauth.setUN(in.getUN());
					actuser.authenticate(inauth, outauth);

					if (outauth.getA().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setET(outauth.getET());
					} else {
						String tmp = actuser.base64Decode(in.getUN());
						in.setUN(tmp);
						tmp = actuser.base64Decode(in.getPW());
						in.setPW(tmp);

						actuser.insertUserIcon(in, out);
					}
				} else {
					/* Icon not found */
					out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
				}
				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End insertchaticon with User = " + in.getUN()
				+ " Icon = " + in.getIcID());
		return out;
	}

	@Override
	public OICIc insertChatIcon(IICIc in) {
		LOGGER.debug("Start insertchaticon with User = " + in.getUN()
				+ " Icon = " + in.getIcID() + "ChatID = " + in.getCID());

		OICIc out = new OICIc();
		MyConnection mc = new MyConnection();
		Connection con = mc.getConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				if (actcheck.checkChatID(in.getCID())) {
					if (actcheck.checkContenMessageID(in.getIcID(),
							Constants.TYP_IMAGE)) {
						IAuth inauth = new IAuth();
						OAuth outauth = new OAuth();
						inauth.setPW(actuser.base64Decode(in.getPW()));
						inauth.setUN(in.getUN());
						actuser.authenticate(inauth, outauth);

						if (outauth.getA().equalsIgnoreCase(
								Constants.AUTHENTICATE_FALSE)) {
							out.setET(outauth.getET());
						} else {
							String tmp = actuser.base64Decode(in.getUN());
							in.setUN(tmp);
							tmp = actuser.base64Decode(in.getPW());
							in.setPW(tmp);

							actuser.insertChatIcon(in, out);
						}
					} else {
						/* Icon not found */
						out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
					}
				} else {
					/* Chat not found */
					out.setET(Constants.NONE_EXISTING_CHAT);
				}

				/* Password check failed */
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			/* User check failed */
		} else {
			if (actcheck.getLastError().equalsIgnoreCase(
					Constants.NO_CONTENT_GIVEN)) {
				out.setET(Constants.NO_USERNAME_OR_PASSWORD);
			} else if (actcheck.getLastError().equalsIgnoreCase(
					Constants.ENCODING_ERROR)) {
				out.setET(Constants.ENCODING_ERROR);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.debug("End insertchaticon with User = " + in.getUN()
				+ " Icon = " + in.getIcID() + "ChatID = " + in.getCID());
		return out;
	}

}
