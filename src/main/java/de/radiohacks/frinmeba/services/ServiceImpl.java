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
import javax.ws.rs.core.HttpHeaders;

import org.apache.log4j.Logger;

import de.radiohacks.frinmeba.database.Check;
import de.radiohacks.frinmeba.database.MyConnection;
import de.radiohacks.frinmeba.model.jaxb.IAckCD;
import de.radiohacks.frinmeba.model.jaxb.IAckMD;
import de.radiohacks.frinmeba.model.jaxb.IAdUC;
import de.radiohacks.frinmeba.model.jaxb.ICrCh;
import de.radiohacks.frinmeba.model.jaxb.IDMFC;
import de.radiohacks.frinmeba.model.jaxb.IDeCh;
import de.radiohacks.frinmeba.model.jaxb.IFMFC;
import de.radiohacks.frinmeba.model.jaxb.IGMI;
import de.radiohacks.frinmeba.model.jaxb.IGTeM;
import de.radiohacks.frinmeba.model.jaxb.IICIc;
import de.radiohacks.frinmeba.model.jaxb.IIMIC;
import de.radiohacks.frinmeba.model.jaxb.IIUIc;
import de.radiohacks.frinmeba.model.jaxb.ILiUs;
import de.radiohacks.frinmeba.model.jaxb.IReUC;
import de.radiohacks.frinmeba.model.jaxb.ISShT;
import de.radiohacks.frinmeba.model.jaxb.ISTeM;
import de.radiohacks.frinmeba.model.jaxb.ISU;
import de.radiohacks.frinmeba.model.jaxb.ISiUp;
import de.radiohacks.frinmeba.model.jaxb.OAckCD;
import de.radiohacks.frinmeba.model.jaxb.OAckMD;
import de.radiohacks.frinmeba.model.jaxb.OAdUC;
import de.radiohacks.frinmeba.model.jaxb.OCN;
import de.radiohacks.frinmeba.model.jaxb.OCrCh;
import de.radiohacks.frinmeba.model.jaxb.ODMFC;
import de.radiohacks.frinmeba.model.jaxb.ODeCh;
import de.radiohacks.frinmeba.model.jaxb.OFMFC;
import de.radiohacks.frinmeba.model.jaxb.OGMI;
import de.radiohacks.frinmeba.model.jaxb.OGTeM;
import de.radiohacks.frinmeba.model.jaxb.OICIc;
import de.radiohacks.frinmeba.model.jaxb.OIMIC;
import de.radiohacks.frinmeba.model.jaxb.OIUIc;
import de.radiohacks.frinmeba.model.jaxb.OLiCh;
import de.radiohacks.frinmeba.model.jaxb.OLiUs;
import de.radiohacks.frinmeba.model.jaxb.OReUC;
import de.radiohacks.frinmeba.model.jaxb.OSShT;
import de.radiohacks.frinmeba.model.jaxb.OSTeM;
import de.radiohacks.frinmeba.model.jaxb.OSU;
import de.radiohacks.frinmeba.model.jaxb.OSiUp;
import de.radiohacks.frinmeba.util.IServiceUtil;

@Path("/user")
public class ServiceImpl implements IServiceUtil {

    private static final Logger LOGGER = Logger.getLogger(ServiceImpl.class
            .getName());

    @Override
    public OIMIC insertMessageIntoChat(HttpHeaders headers, IIMIC in) {

        LOGGER.debug("Start insertMessageIntoChat with ChatID = " + in.getCID()
                + " MessageID = " + in.getMID() + " MessageType = "
                + in.getMT());
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);
        OIMIC out = new OIMIC();

        if (actcheck.checkValueMust(in.getMT())) {

            String tmp = actuser.base64Decode(in.getMT());
            in.setMT(tmp);

            /* Check if Chat exists */
            if (!actcheck.checkChatID(in.getCID())) {
                out.setET(Constants.NONE_EXISTING_CHAT);
            } else {
                /* Check if Message exists */
                if (!actcheck.checkContenMessageID(in.getMID(), in.getMT())) {
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

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End insertMessageIntoChat with ChatID = " + in.getCID()
                + " MessageID = " + in.getMID() + " MessageType = "
                + in.getMT());
        return out;
    }

    @Override
    public OSTeM sendTextMessage(HttpHeaders headers, ISTeM in) {

        LOGGER.debug("Start sendTextMessage with TextMessage = " + in.getTM());

        OSTeM out = new OSTeM();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        if (actcheck.checkValueMust(in.getTM())) {
            actuser.sendTextMessage(in, out);
        } else {
            if (actcheck.getLastError().equalsIgnoreCase(
                    Constants.NO_CONTENT_GIVEN)) {
                out.setET(Constants.NO_TEXTMESSAGE_GIVEN);
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

        LOGGER.debug("End sendTextMessage with TextMessage = " + in.getTM());
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
    public OCrCh createChat(HttpHeaders headers, ICrCh in) {

        LOGGER.debug("Start CreateChat with Chatname = " + in.getCN());

        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        OCrCh out = new OCrCh();
        Check actcheck = new Check(con);

        if (actcheck.checkValueMust(in.getCN())) {
            String tmp = actuser.base64Decode(in.getCN());
            in.setCN(tmp);
            actuser.createChat(in, out,
                    headers.getHeaderString(Constants.USERNAME));
        } else {
            if (actcheck.getLastError().equalsIgnoreCase(
                    Constants.NO_CONTENT_GIVEN)) {
                out.setET(Constants.MISSING_CHATNAME);
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

        LOGGER.debug("End CreateChat with Chatname = " + in.getCN());
        return out;
    }

    @Override
    public ODeCh deleteChat(HttpHeaders headers, int chatID) {
        LOGGER.debug("Start DeleteChat with ChatID = " + chatID);

        ODeCh out = new ODeCh();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        /* Check if Chat exists */
        if (!actcheck.checkChatID(chatID)) {
            out.setET(Constants.NONE_EXISTING_CHAT);
        } else {
            IDeCh in = new IDeCh();
            in.setCID(chatID);
            actuser.deleteChat(in, out);
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End DeleteChat with ChatID = " + chatID);
        return out;
    }

    @Override
    public OAdUC addUserToChat(HttpHeaders headers, IAdUC in) {
        LOGGER.debug("Start AddUserToChat with ChatID = " + in.getCID()
                + "UserID = " + in.getUID());

        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);
        OAdUC out = new OAdUC();

        /* Check if Chat exists */
        if (!actcheck.checkChatID(in.getCID())) {
            out.setET(Constants.NONE_EXISTING_CHAT);
        } else {
            /* Check if Message exists */
            if (!actcheck.checkUserID(in.getUID())) {
                out.setET(Constants.NONE_EXISTING_USER);
            } else {
                actuser.addUserToChat(in, out);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End AddUserToChat with ChatID = " + in.getCID()
                + "UserID = " + in.getUID());
        return out;
    }

    @Override
    public OReUC removeUserFromChat(HttpHeaders headers, int userID, int chatID) {

        LOGGER.debug("Start RemoveUserFromChat with ChatID = " + chatID
                + "UserID = " + userID);

        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);
        OReUC out = new OReUC();

        /* Check if Chat exists */
        if (!actcheck.checkChatID(chatID)) {
            out.setET(Constants.NONE_EXISTING_CHAT);
        } else {
            /* Check if Message exists */
            if (!actcheck.checkUserID(userID)) {
                out.setET(Constants.NONE_EXISTING_USER);
            } else {
                IReUC in = new IReUC();
                in.setUID(userID);
                in.setCID(chatID);
                actuser.removeUserFromChat(in, out);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End RemoveUserFromChat with ChatID = " + chatID
                + "UserID = " + userID);
        return out;
    }

    @Override
    public OLiUs listUsers(HttpHeaders headers, String search) {
        LOGGER.debug("Start ListUsers with Search = " + search);

        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);
        OLiUs out = new OLiUs();

        if (actcheck.checkValueCan(search)) {
            ILiUs in = new ILiUs();
            if (search != null && !search.isEmpty()) {
                in.setS(actuser.base64Decode(search));
            } else {
                in.setS("");
            }
            actuser.listUser(in, out);
            /* Search check failed */
        } else {
            if (actcheck.getLastError().equalsIgnoreCase(
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

        LOGGER.debug("End ListUsers with Search = " + search);
        return out;
    }

    @Override
    public OLiCh listChats(HttpHeaders headers) {
        LOGGER.debug("Start ListChats");

        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        OLiCh out = new OLiCh();

        actuser.listChat(out);

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End ListChats");
        return out;
    }

    @Override
    public OFMFC getMessageFromChat(HttpHeaders headers, int chatID,
            int timestamp) {
        LOGGER.debug("Start getMessageFromChat with ChatID = " + chatID);

        OFMFC out = new OFMFC();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        IFMFC in = new IFMFC();
        in.setCID(chatID);
        in.setRdT(timestamp);

        /* Check if Chat exists */
        if (!actcheck.checkChatID(in.getCID())) {
            out.setET(Constants.NONE_EXISTING_CHAT);
        } else {
            actuser.getMessagesFromChat(in, out);
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End getMessageFromChat with ChatID = " + chatID);
        return out;
    }

    @Override
    public OGTeM getTextMessage(HttpHeaders headers, int textMessageID) {
        LOGGER.debug("Start getTextMessage with TextMessageID = "
                + textMessageID);

        OGTeM out = new OGTeM();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        IGTeM in = new IGTeM();
        in.setTextID(textMessageID);
        /* Check if Chat exists */
        if (!actcheck.checkContenMessageID(textMessageID, Constants.TYP_TEXT)) {
            out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
        } else {
            actuser.getTextMessages(in, out);
        }
        /* Password check failed */

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End getTextMessage with TextMessageID = " + textMessageID);
        return out;
    }

    @Override
    public OCN checkNew(HttpHeaders headers) {
        LOGGER.debug("Start checkNewMessages");

        OCN out = new OCN();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));

        /* Check if Chat exists */
        actuser.checkNew(out);

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End checkNewMessages");
        return out;
    }

    @Override
    public ODMFC deleteMessageFromChat(HttpHeaders headers, int messageID) {

        LOGGER.debug("Start deleteMessageFromChat with MessageID = "
                + messageID);

        ODMFC out = new ODMFC();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        if (messageID > 0) {
            if (actcheck.checkMessageID(messageID)) {
                // actuser.fillUserinfo(actuser.base64Decode(headers
                // .getHeaderString(Constants.USERNAME)));
                if (actcheck.checkOwnMessage(actuser.getID(), messageID)) {
                    IDMFC in = new IDMFC();
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

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
                /* e.printStackTrace(); */
            }
        }

        LOGGER.debug("End checkNewMessages with MessageID = " + messageID);
        return out;
    }

    @Override
    public OSShT setShowTimeStamp(HttpHeaders headers, ISShT in) {
        LOGGER.debug("Start setShowTimeStamp with MessageID = " + in.getMID());

        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);
        OSShT out = new OSShT();

        boolean abort = false;
        // if (!in.getMID().isEmpty() && in.getMID().size() > 0) {
        if (!in.getMID().isEmpty()) {
            for (int i = 0; i < in.getMID().size(); i++) {
                /* Check if Message exists */
                if (!actcheck.checkMessageID(in.getMID().get(i))) {
                    out.setET(Constants.NONE_EXISTING_MESSAGE);
                    abort = true;
                } else {
                    if (!actcheck.checkMessageIDReadTimestamp(in.getMID()
                            .get(i))) {
                        out.setET(Constants.MESSAGE_NOT_READ);
                        abort = true;
                    } else {
                        /* Check if it is your own Message. Do not */
                        /* update */
                        /* ShowTimeStamps for other people */
                        if (!actcheck.checkOwnMessage(actuser.getID(), in
                                .getMID().get(i))) {
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

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End setShowTimeStamp with MessageID = " + in.getMID());
        return out;
    }

    @Override
    public OGMI getMessageInformation(HttpHeaders headers,
            List<Integer> messageID) {
        LOGGER.debug("Start getMessageInformation with Messagelist.size = "
                + messageID.size());

        OGMI out = new OGMI();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        IGMI in = new IGMI();
        boolean abort = false;
        // if (!messageID.isEmpty() && messageID.size() > 0) {
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

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End getMessageInformation with messagelist.size = "
                + messageID.size());
        return out;
    }

    @Override
    public OAckMD acknowledgeMessageDownload(HttpHeaders headers, IAckMD in) {
        LOGGER.debug("Start acknowledgeMessageDownload with MessageID = "
                + in.getMID() + " Acknowledge = " + in.getACK());

        OAckMD out = new OAckMD();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        if (actcheck.checkValueMust(in.getACK())) {
            if (in.getMID() > 0) {
                if (actcheck.checkMessageID(in.getMID())) {
                    String tmp = actuser.base64Decode(in.getACK());
                    in.setACK(tmp);
                    actuser.acknowledgeMessageDownload(in, out);
                } else {
                    out.setET(Constants.NONE_EXISTING_MESSAGE);
                }
            } else {
                out.setET(Constants.NONE_EXISTING_MESSAGE);
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

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End acknowledgeMessageDownload with MesageID = "
                + in.getMID() + " Acknowledge = " + in.getACK());
        return out;
    }

    @Override
    public OAckCD acknowledgeChatDownload(HttpHeaders headers, IAckCD in) {
        LOGGER.debug("Start acknowledgeChatDownload with ChatID = "
                + in.getCID() + " Acknowledge = " + in.getACK());

        OAckCD out = new OAckCD();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        if (actcheck.checkValueMust(in.getACK())) {
            if (in.getCID() > 0) {
                if (actcheck.checkChatID(in.getCID())) {
                    actuser.acknowledgeChatDownload(in, out);
                } else {
                    out.setET(Constants.NONE_EXISTING_CHAT);
                }
            } else {
                out.setET(Constants.NONE_EXISTING_CHAT);
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

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End acknowledgeChatDownload with ChatID = " + in.getCID()
                + " Acknowledge = " + in.getACK());
        return out;
    }

    @Override
    public OSU syncUser(HttpHeaders headers, List<Integer> userID) {
        LOGGER.debug("Start syncuser with Userlist.size =" + userID.size());

        OSU out = new OSU();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        ISU in = new ISU();
        boolean abort = false;
        // if (!userID.isEmpty() && userID.size() > 0) {
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

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End getMessageInformation with Userlist.size = "
                + userID.size());
        return out;
    }

    @Override
    public OIUIc insertUserIcon(HttpHeaders headers, IIUIc in) {
        LOGGER.debug("Start insertusericon with IconID = " + in.getIcID());

        OIUIc out = new OIUIc();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        if (actcheck.checkContenMessageID(in.getIcID(), Constants.TYP_IMAGE)) {
            actuser.insertUserIcon(in, out);
        } else {
            /* Icon not found */
            out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End insertchaticon with Icon = " + in.getIcID());
        return out;
    }

    @Override
    public OICIc insertChatIcon(HttpHeaders headers, IICIc in) {
        LOGGER.debug("Start insertchaticon with Icon = " + in.getIcID()
                + "ChatID = " + in.getCID());

        OICIc out = new OICIc();
        MyConnection mc = new MyConnection();
        Connection con = mc.getConnection();
        User actuser = new User(con,
                headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check(con);

        if (actcheck.checkChatID(in.getCID())) {
            if (actcheck
                    .checkContenMessageID(in.getIcID(), Constants.TYP_IMAGE)) {
                actuser.insertChatIcon(in, out);
            } else {
                /* Icon not found */
                out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
            }
        } else {
            /* Chat not found */
            out.setET(Constants.NONE_EXISTING_CHAT);
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.debug("End insertchaticon with Icon = " + in.getIcID()
                + "ChatID = " + in.getCID());
        return out;
    }

}
