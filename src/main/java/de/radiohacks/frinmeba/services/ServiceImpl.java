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
import de.radiohacks.frinmeba.database.MySqlConnection;
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
import de.radiohacks.frinmeba.modelshort.IIMIC;
import de.radiohacks.frinmeba.modelshort.ILiCh;
import de.radiohacks.frinmeba.modelshort.ILiUs;
import de.radiohacks.frinmeba.modelshort.IReUC;
import de.radiohacks.frinmeba.modelshort.ISShT;
import de.radiohacks.frinmeba.modelshort.ISTeM;
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
import de.radiohacks.frinmeba.modelshort.OIMIC;
import de.radiohacks.frinmeba.modelshort.OLiCh;
import de.radiohacks.frinmeba.modelshort.OLiUs;
import de.radiohacks.frinmeba.modelshort.OReUC;
import de.radiohacks.frinmeba.modelshort.OSShT;
import de.radiohacks.frinmeba.modelshort.OSTeM;
import de.radiohacks.frinmeba.modelshort.OSiUp;
import de.radiohacks.frinmeba.util.ServiceUtil;

@Path("/user")
public class ServiceImpl implements ServiceUtil {

	static final Logger logger = Logger.getLogger(ServiceImpl.class);

	@Override
	public OAuth AuthenticateUser(String User, String Password) {
		logger.debug("Start AuthenticateUser with User = " + User
				+ " Password = " + Password);

		OAuth out = new OAuth();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				IAuth in = new IAuth();

				in.setPW(actuser.base64Decode(Password));
				// in.setPW(actuser.base64Decode(Password));
				in.setUN(User);

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End AuthenticateUser with User = " + User
				+ " Password = " + Password);
		return out;
	}

	@Override
	public OIMIC insertMessageIntoChat(IIMIC in) {

		logger.debug("Start insertMessageIntoChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " ChatID = " + in.getCID()
				+ " MessageID = " + in.getMID() + " MessageType = "
				+ in.getMT());
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
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
						if (!actcheck.CheckChatID(in.getCID())) {
							out.setET(Constants.NONE_EXISTING_CHAT);
						} else {
							/* Check if Message exists */
							if (!actcheck.CheckContenMessageID(in.getMID(),
									in.getMT())) {
								out.setET(Constants.NONE_EXISTING_MESSAGE);
							} else {
								/* Check if it is a vaid Message Type */
								if (!actcheck.CheckMessageType(in.getMT())) {
									out.setET(Constants.INVALID_MESSAGE_TYPE);
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
						out.setET(Constants.INVALID_MESSAGE_TYPE);
					} else if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setET(Constants.ENCODING_ERROR);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End insertMessageIntoChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " ChatID = " + in.getCID()
				+ " MessageID = " + in.getMID() + " MessageType = "
				+ in.getMT());
		return out;
	}

	@Override
	public OSTeM sendTextMessage(ISTeM in) {

		logger.debug("Start sendTextMessage with User = " + in.getUN()
				+ " Password = " + in.getPW() + " TextMessage = " + in.getTM());

		OSTeM out = new OSTeM();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
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
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End sendTextMessage with User = " + in.getUN()
				+ " Password = " + in.getPW() + " TextMessage = " + in.getTM());
		return out;
	}

	@Override
	// public OSiUp SingUpUser(String User, String Password, String Email) {
	public OSiUp SingUpUser(ISiUp in) {

		logger.debug("Start SingUpUser with User = " + in.getUN()
				+ " Password = " + in.getPW() + " Email = " + in.getE());

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OSiUp out = new OSiUp();

		if (actcheck.checkValueMust(in.getUN())) {
			if (actcheck.checkValueMust(in.getPW())) {
				if (actcheck.checkValueMust(in.getE())) {
					String tmp = actuser.base64Decode(in.getPW());
					in.setPW(tmp);
					// tmp = actuser.base64Decode(in.getUN());
					// in.setUN(tmp);
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
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End SingUpUser with User = " + in.getUN()
				+ " Password = " + in.getPW() + " Email = " + in.getE());

		return out;
	}

	@Override
	public OCrCh CreateChat(ICrCh in) {

		logger.debug("Start CreateChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " Chatname = " + in.getCN());

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
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
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End CreateChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " Chatname = " + in.getCN());
		return out;
	}

	@Override
	public ODeCh DeleteChat(String User, String Password, int ChatID) {
		logger.debug("Start DeleteChat with User = " + User + " Password = "
				+ Password + " ChatID = " + ChatID);

		ODeCh out = new ODeCh();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);
				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					if (!actcheck.CheckChatID(ChatID)) {
						out.setET(Constants.NONE_EXISTING_CHAT);
					} else {
						IDeCh in = new IDeCh();
						in.setUN(actuser.base64Decode(User));
						in.setPW(actuser.base64Decode(Password));
						in.setCID(ChatID);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End DeleteChat with User = " + User + " Password = "
				+ Password + " ChatID = " + ChatID);
		return out;
	}

	@Override
	public OAdUC AddUserToChat(IAdUC in) {
		logger.debug("Start AddUserToChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " ChatID = " + in.getCID()
				+ "UserID = " + in.getUID());

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
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
					if (!actcheck.CheckChatID(in.getCID())) {
						out.setET(Constants.NONE_EXISTING_CHAT);
					} else {
						/* Check if Message exists */
						if (!actcheck.CheckUserID(in.getUID())) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End AddUserToChat with User = " + in.getUN()
				+ " Password = " + in.getPW() + " ChatID = " + in.getCID()
				+ "UserID = " + in.getUID());
		return out;
	}

	@Override
	public OReUC RemoveUserFromChat(String User, String Password, int UserID,
			int ChatID) {

		logger.debug("Start RemoveUserFromChat with User = " + User
				+ " Password = " + Password + " ChatID = " + ChatID
				+ "UserID = " + UserID);

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OReUC out = new OReUC();

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					if (!actcheck.CheckChatID(ChatID)) {
						out.setET(Constants.NONE_EXISTING_CHAT);
					} else {
						/* Check if Message exists */
						if (!actcheck.CheckUserID(UserID)) {
							out.setET(Constants.NONE_EXISTING_USER);
						} else {
							IReUC in = new IReUC();
							in.setUN(actuser.base64Decode(User));
							in.setPW(actuser.base64Decode(Password));
							in.setUID(UserID);
							in.setCID(ChatID);
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
	public OLiUs ListUsers(String User, String Password, String search) {
		logger.debug("Start ListUsers with User = " + User + " Password = "
				+ Password);

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OLiUs out = new OLiUs();

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				if (actcheck.checkValueCan(search)) {
					ILiUs in = new ILiUs();
					in.setPW(actuser.base64Decode(Password));
					in.setUN(actuser.base64Decode(User));
					if (search != null && !search.isEmpty()) {
						in.setS(actuser.base64Decode(search));
					} else {
						in.setS("");
					}
					IAuth inauth = new IAuth();
					OAuth outauth = new OAuth();
					inauth.setPW(actuser.base64Decode(Password));
					inauth.setUN(User);
					actuser.authenticate(inauth, outauth);

					if (outauth.getA().equalsIgnoreCase(
							Constants.AUTHENTICATE_FALSE)) {
						out.setET(outauth.getET());
					} else {
						actuser.listUser(in, out);
					}
					// Search check failed
				} else {
					if (actcheck.getLastError().equalsIgnoreCase(
							Constants.ENCODING_ERROR)) {
						out.setET(Constants.ENCODING_ERROR);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End ListUsers with User = " + User + " Password = "
				+ Password);
		return out;
	}

	@Override
	public OLiCh ListChats(String User, String Password) {
		logger.debug("Start ListChats with User = " + User + " Password = "
				+ Password);

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		OLiCh out = new OLiCh();
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				ILiCh in = new ILiCh();
				in.setPW(actuser.base64Decode(Password));
				in.setUN(actuser.base64Decode(User));

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);

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
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End ListChats with User = " + User + " Password = "
				+ Password);
		return out;
	}

	@Override
	public OFMFC getMessageFromChat(String User, String Password, int ChatID,
			int Timestamp) {
		logger.debug("Start getMessageFromChat with User = " + User
				+ " Password = " + Password + " ChatID = " + ChatID);

		OFMFC out = new OFMFC();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				IFMFC in = new IFMFC();
				in.setCID(ChatID);
				in.setPW(actuser.base64Decode(Password));
				in.setUN(actuser.base64Decode(User));
				in.setRdT(Timestamp);

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					if (!actcheck.CheckChatID(in.getCID())) {
						out.setET(Constants.NONE_EXISTING_CHAT);
					} else {
						actuser.getMessagesFromChat(in, out);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End getMessageFromChat with User = " + User
				+ " Password = " + Password + " ChatID = " + ChatID);
		return out;
	}

	@Override
	public OGTeM getTextMessage(String User, String Password, int TextMessageID) {
		logger.debug("Start getTextMessage with User = " + User
				+ " Password = " + Password + " TextMessageID = "
				+ TextMessageID);

		OGTeM out = new OGTeM();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				IGTeM in = new IGTeM();
				in.setTextID(TextMessageID);
				in.setPW(actuser.base64Decode(Password));
				in.setUN(actuser.base64Decode(User));

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					if (!actcheck.CheckContenMessageID(TextMessageID,
							Constants.TYP_TEXT)) {
						out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
					} else {
						actuser.getTextMessages(in, out);
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End getTextMessage with User = " + User + " Password = "
				+ Password + " TextMessageID = " + TextMessageID);
		return out;
	}

	@Override
	public OCN checkNew(String User, String Password) {
		logger.debug("Start checkNewMessages with User = " + User
				+ " Password = " + Password);

		OCN out = new OCN();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				ICN in = new ICN();
				in.setPW(actuser.base64Decode(Password));
				in.setUN(actuser.base64Decode(User));

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					/* Check if Chat exists */
					actuser.checkNew(in, out);
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End checkNewMessages with User = " + User
				+ " Password = " + Password);
		return out;
	}

	@Override
	public ODMFC deleteMessageFromChat(String User, String Password,
			int MessageID) {

		logger.debug("Start deleteMessageFromChat with User = " + User
				+ " Password = " + Password + " MessageID = " + MessageID);

		ODMFC out = new ODMFC();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {
				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					if (MessageID > 0) {
						if (actcheck.CheckMessageID(MessageID)) {
							actuser.fillUserinfo(actuser.base64Decode(User));
							if (actcheck.CheckOwnMessage(actuser.getID(),
									MessageID)) {
								IDMFC in = new IDMFC();
								in.setUN(actuser.base64Decode(User));
								in.setPW(actuser.base64Decode(Password));
								in.setMID(MessageID);
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
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End checkNewMessages with User = " + User
				+ " Password = " + Password);
		return out;
	}

	@Override
	public OSShT setShowTimeStamp(ISShT in) {
		logger.debug("Start setShowTimeStamp with User = " + in.getUN()
				+ " Password = " + in.getPW() + " MessageID = " + in.getMID());

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
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
					/* Check if Message exists */
					if (!actcheck.CheckMessageID(in.getMID())) {
						out.setET(Constants.NONE_EXISTING_MESSAGE);
					} else {
						if (!actcheck.CheckMessageIDReadTimestamp(in.getMID())) {
							out.setET(Constants.MESSAGE_NOT_READ);
						} else {
							// Check if it is your own Message. Do not update
							// ShowTimeStamps for other people
							actuser.fillUserinfo(actuser.base64Decode(in
									.getUN()));
							if (actcheck.CheckOwnMessage(actuser.getID(),
									in.getMID())) {
								String tmp = actuser.base64Decode(in.getUN());
								in.setUN(tmp);
								tmp = actuser.base64Decode(in.getPW());
								in.setPW(tmp);
								actuser.setShowTimeStamp(in, out);
							} else {
								out.setET(Constants.NOT_MESSAGE_OWNER);
							}
						}
					}
				}
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End setShowTimeStamp with User = " + in.getUN()
				+ " Password = " + in.getPW() + " MessageID = " + in.getMID());
		return out;
	}

	@Override
	public OGMI getMessageInformation(String User, String Password,
			List<Integer> MessageID) {
		logger.debug("Start getMessageInformation with User = " + User
				+ " Password = " + Password);

		OGMI out = new OGMI();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				IGMI in = new IGMI();
				in.setPW(actuser.base64Decode(Password));
				in.setUN(actuser.base64Decode(User));

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					boolean abort = false;
					if (!MessageID.isEmpty() && MessageID.size() > 0) {
						for (int i = 0; i < MessageID.size(); i++) {
							if (actcheck.CheckMessageID(MessageID.get(i))) {
								in.getMID().add(MessageID.get(i));
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
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End getMessageInformation with User = " + User
				+ " Password = " + Password);
		return out;
	}

	@Override
	public OAckMD acknowledgeMessageDownload(IAckMD in) {
		logger.debug("Start acknowledgeMessageDownload with User = "
				+ in.getUN() + " Password = " + in.getPW());

		OAckMD out = new OAckMD();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
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
							if (actcheck.CheckMessageID(in.getMID())) {
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
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End acknowledgeMessageDownload with User = " + in.getUN()
				+ " Password = " + in.getPW());
		return out;
	}

	@Override
	public OAckCD acknowledgeChatDownload(IAckCD in) {
		logger.debug("Start acknowledgeChatDownload with User = " + in.getUN()
				+ " Password = " + in.getPW());

		OAckCD out = new OAckCD();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
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
							if (actcheck.CheckChatID(in.getCID())) {
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
				// Password check failed
			} else {
				if (actcheck.getLastError().equalsIgnoreCase(
						Constants.NO_CONTENT_GIVEN)) {
					out.setET(Constants.NO_USERNAME_OR_PASSWORD);
				} else if (actcheck.getLastError().equalsIgnoreCase(
						Constants.ENCODING_ERROR)) {
					out.setET(Constants.ENCODING_ERROR);
				}
			}
			// User check failed
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.debug("End acknowledgeChatDownload with User = " + in.getUN()
				+ " Password = " + in.getPW());
		return out;
	}
}