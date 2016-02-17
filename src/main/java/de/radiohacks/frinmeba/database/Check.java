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

package de.radiohacks.frinmeba.database;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.codec.binary.Base64;

import org.apache.log4j.Logger;

import de.radiohacks.frinmeba.services.Constants;

public class Check implements Serializable {

    private static final long serialVersionUID = 6984249988911637721L;
    private static final Logger LOGGER = Logger.getLogger(Check.class.getName());

    private Connection con = null;
    private String lastError = null;

    public Check(Connection conin) {
        this.con = conin;
        this.lastError = new String();
    }

    public boolean checkChatID(int chatid) {
        LOGGER.debug("Start CheckChatID with ChatID = " + chatid);
        boolean ret = false;
        if (chatid != 0) {
            ResultSet resultSet = null;
            Statement statement = null;

            try {
                statement = con.createStatement();
                resultSet = statement.executeQuery("select ID, Chatname from Chats where ID = '" + chatid + "'");

                if (resultSet != null && resultSet.next()) {
                    ret = true;
                }
            } catch (SQLException e) {
                LOGGER.error(e);
            } finally {
                try {
                    resultSet.close();
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }
        }
        LOGGER.debug("End CheckChatID with ChatID = " + chatid);
        return ret;
    }

    public boolean checkMessageID(int messageid) {
        LOGGER.debug("Start CheckMessageID with MessageID = " + messageid);

        boolean ret = false;
        if (messageid != 0) {
            ResultSet resultSet = null;
            Statement statement = null;

            try {
                statement = con.createStatement();
                resultSet = statement.executeQuery("select ID from Messages where ID = '" + messageid + "'");

                if (resultSet != null && resultSet.next()) {
                    ret = true;
                }
            } catch (SQLException e) {
                LOGGER.error(e);
            } finally {
                try {
                    resultSet.close();
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }
        }
        LOGGER.debug("End CheckMessageID with MessageID = " + messageid);
        return ret;
    }

    public boolean checkMessageIDReadTimestamp(int messageid) {
        LOGGER.debug("Start CheckMessageIDReadTimestamp with MessageID = " + messageid);

        boolean ret = false;
        if (messageid != 0) {
            ResultSet resultSet = null;
            Statement statement = null;

            try {
                statement = con.createStatement();
                resultSet = statement.executeQuery("select ReadTimestamp from Messages where ID = '" + messageid + "'");

                if (resultSet != null && resultSet.next()) {
                    if (resultSet.getInt("ReadTimestamp") > 0) {
                        ret = true;
                    }
                }
            } catch (SQLException e) {
                LOGGER.error(e);
            } finally {
                try {
                    resultSet.close();
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }
        }
        LOGGER.debug("End CheckMessageIDReadTimestamp with MessageID = " + messageid);
        return ret;
    }

    public boolean checkContenMessageID(int messageid, String messagetype) {
        LOGGER.debug("Start CheckContenMessageID with MessageID = " + messageid + " MessageType = " + messagetype);
        boolean ret = false;

        if (messageid != 0 && messagetype != null && !messagetype.isEmpty()) {
            ResultSet rsText = null;
            ResultSet rsImage = null;
            ResultSet rsLoc = null;
            ResultSet rsCon = null;
            ResultSet rsFile = null;
            ResultSet rsVideo = null;
            Statement statement = null;

            try {
                statement = con.createStatement();
                if (messagetype.equalsIgnoreCase(Constants.TYP_TEXT)) {
                    rsText = statement.executeQuery("select ID from Text where ID = '" + messageid + "'");
                    if (rsText != null && rsText.next()) {
                        ret = true;
                    }
                } else if (messagetype.equalsIgnoreCase(Constants.TYP_IMAGE)) {
                    rsImage = statement.executeQuery("select ID from Image where ID = '" + messageid + "'");
                    if (rsImage != null && rsImage.next()) {
                        ret = true;
                    }
                } else if (messagetype.equalsIgnoreCase(Constants.TYP_LOCATION)) {
                    rsLoc = statement.executeQuery("select ID from Location where ID = '" + messageid + "'");
                    if (rsLoc != null && rsLoc.next()) {
                        ret = true;
                    }
                } else if (messagetype.equalsIgnoreCase(Constants.TYP_CONTACT)) {
                    rsCon = statement.executeQuery("select ID from Contact where ID = '" + messageid + "'");
                    if (rsCon != null && rsCon.next()) {
                        ret = true;
                    }
                } else if (messagetype.equalsIgnoreCase(Constants.TYP_FILE)) {
                    rsFile = statement.executeQuery("select ID from File where ID = '" + messageid + "'");
                    if (rsFile != null && rsFile.next()) {
                        ret = true;
                    }
                } else if (messagetype.equalsIgnoreCase(Constants.TYP_VIDEO)) {
                    rsVideo = statement.executeQuery("select ID from Video where ID = '" + messageid + "'");
                    if (rsVideo != null && rsVideo.next()) {
                        ret = true;
                    }
                }
            } catch (SQLException e) {
                LOGGER.error(e);
            } finally {
                try {
                    if (rsText != null) {
                        rsText.close();
                    }
                    if (rsImage != null) {
                        rsImage.close();
                    }
                    if (rsLoc != null) {
                        rsLoc.close();
                    }
                    if (rsCon != null) {
                        rsCon.close();
                    }
                    if (rsFile != null) {
                        rsFile.close();
                    }
                    if (rsVideo != null) {
                        rsVideo.close();
                    }
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }
        }
        LOGGER.debug("End CheckContenMessageID with MessageID = " + messageid + " MessageType = " + messagetype);
        return ret;
    }

    public boolean checkOwnMessage(int userid, int messageid) {
        LOGGER.debug("End CheckOwnMessage with MessageID = " + messageid + " UserID = " + userid);
        boolean ret = false;
        if (messageid != 0 && userid != 0) {
            ResultSet rs = null;
            Statement statement = null;

            try {
                statement = con.createStatement();
                rs = statement.executeQuery("SELECT UserID FROM UserToChats a, Messages b WHERE b.ID = '" + messageid
                        + "' AND b.UsertoChatID = a.ID");
                if (rs != null) {
                    while (rs.next()) {
                        if (rs.getInt("UserID") == userid) {
                            ret = true;
                        }
                    }
                }
            } catch (SQLException e) {
                LOGGER.error(e);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }
        }
        LOGGER.debug("End CheckOwnMessage with MessageID = " + messageid + " UserID = " + userid);
        return ret;
    }

    public boolean checkUserID(int userid) {
        LOGGER.debug("Start CheckUserID with UserID = " + userid);
        boolean ret = false;

        if (userid != 0) {
            ResultSet resultSet = null;
            Statement statement = null;

            try {
                statement = con.createStatement();
                resultSet = statement.executeQuery("select ID from Users where ID = '" + userid + "'");

                if (resultSet != null && resultSet.next()) {
                    ret = true;
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
        }
        LOGGER.debug("End CheckUserID with UserID = " + userid);
        return ret;
    }

    public boolean checkMessageType(String messagetype) {
        LOGGER.debug("Start CheckMessageType with MessageType = " + messagetype);
        boolean ret = false;

        if (messagetype != null && !messagetype.isEmpty()) {

            if (messagetype.equalsIgnoreCase(Constants.TYP_TEXT)) {
                ret = true;
            } else if (messagetype.equalsIgnoreCase(Constants.TYP_IMAGE)) {
                ret = true;
            } else if (messagetype.equalsIgnoreCase(Constants.TYP_LOCATION)) {
                ret = true;
            } else if (messagetype.equalsIgnoreCase(Constants.TYP_CONTACT)) {
                ret = true;
            } else if (messagetype.equalsIgnoreCase(Constants.TYP_FILE)) {
                ret = true;
            } else if (messagetype.equalsIgnoreCase(Constants.TYP_VIDEO)) {
                ret = true;
            }
        }
        LOGGER.debug("End CheckMessageType with MessageType = " + messagetype);
        return ret;
    }

    public boolean checkEmail(String email) {
        LOGGER.debug("Start CheckEmail with EMail = " + email);
        boolean ret = true;

        if (email == null || email.isEmpty()) {
            ret = false;
        } else {
            try {
                InternetAddress emailAddr = new InternetAddress(email);
                emailAddr.validate();
            } catch (AddressException ex) {
                LOGGER.error(ex);
                ret = false;
            }
        }
        LOGGER.debug("End CheckEmail with EMail = " + email);
        return ret;
    }

    private static String base64Encode(String token) {
        byte[] encodedBytes = Base64.encodeBase64(token.getBytes());
        return new String(encodedBytes, Charset.forName(Constants.CHARACTERSET));
    }

    private static String base64Decode(String token) {
        byte[] decodedBytes = Base64.decodeBase64(token.getBytes());
        return new String(decodedBytes, Charset.forName(Constants.CHARACTERSET));
    }

    public boolean checkValueMust(String in) {
        LOGGER.debug("Start CheckValueMust with Value = " + in);
        boolean ret = false;

        if (in == null || in.isEmpty()) {
            lastError = Constants.NO_CONTENT_GIVEN;
        } else {
            if (in.equals(base64Encode(base64Decode(in)))) {
                ret = true;
                lastError = "";
            } else {
                lastError = Constants.ENCODING_ERROR;
            }
        }
        LOGGER.debug("End checkValueMust with ret = " + ret);
        return ret;
    }

    public boolean checkValueCan(String in) {
        LOGGER.debug("Start CheckValueCan with Value = " + in);
        boolean ret = false;

        if (in == null || in.isEmpty()) {
            lastError = "";
            ret = true;
        } else {
            if (in.equals(base64Encode(base64Decode(in)))) {
                ret = true;
                lastError = "";
            } else {
                lastError = Constants.ENCODING_ERROR;
            }
        }
        LOGGER.debug("End checkValueCan with ret = " + ret);
        return ret;
    }

    public String getLastError() {
        return lastError;
    }
}
