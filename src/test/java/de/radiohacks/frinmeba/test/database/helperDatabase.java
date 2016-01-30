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
package de.radiohacks.frinmeba.test.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import org.apache.log4j.Logger;

import de.radiohacks.frinmeba.database.Check;
import de.radiohacks.frinmeba.database.MySqlConnection;
import de.radiohacks.frinmeba.services.Constants;

public class helperDatabase {

    private static final Logger LOGGER = Logger.getLogger(Check.class);
    final static String activate = "UPDATE Users SET ACTIVE = 1 where ID = ?";

    public void ActivateUser(int id) {
        try {
            Connection con = new MySqlConnection().getMySqlConnection();
            PreparedStatement pre = con.prepareStatement(activate);
            pre.setInt(1, id);
            pre.executeUpdate();
            con.close();
        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }

    public void CreateActiveUser(String username, String B64username,
            String password, String email, int IconID) {

        try {
            Connection con = new MySqlConnection().getMySqlConnection();

            Statement st = con.createStatement();
            long currentTime = System.currentTimeMillis() / 1000L;

            st.executeUpdate("insert into Users(Username, B64Username, Password, Email, SignupDate, Status, AuthenticationTime, Active, IconID) values ('"
                    + username
                    + "', '"
                    + B64username
                    + "', '"
                    + password
                    + "', '"
                    + email
                    + "', '"
                    + currentTime
                    + "', '0', '"
                    + currentTime + "', '1', " + IconID + ")");
            con.close();
        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }

    public int getUserID(String username) {

        int userid = 0;
        Connection con = new MySqlConnection().getMySqlConnection();

        Statement st;
        try {
            st = con.createStatement();
            ResultSet res = st
                    .executeQuery("select id from Users where Username = '"
                            + username + "'");
            while (res.next()) {
                userid = res.getInt(1);
            }
            res.close();
        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return userid;
    }

    public int getChatID(String chatname) {

        int chatid = 0;
        Connection con = new MySqlConnection().getMySqlConnection();

        Statement st;
        try {
            st = con.createStatement();
            ResultSet res = st
                    .executeQuery("select id from Chats where Chatname = '"
                            + chatname + "'");
            while (res.next()) {
                chatid = res.getInt(1);
            }
            res.close();
        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return chatid;
    }

    public int getUser2ChatID(int inUserID, int inChatID) {

        int chatid = 0;
        Connection con = new MySqlConnection().getMySqlConnection();

        Statement st;
        try {
            st = con.createStatement();
            ResultSet res = st
                    .executeQuery("select id from UserToChats where ChatID = '"
                            + inChatID + "'" + " AND UserID = '" + inUserID
                            + "'");
            while (res.next()) {
                chatid = res.getInt(1);
            }
            res.close();
        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return chatid;
    }

    public int CreateChat(String username, String chatname) {

        int userid = 0;
        int chatid = 0;
        try {
            Connection con = new MySqlConnection().getMySqlConnection();

            Statement st = con.createStatement();

            userid = getUserID(username);

            st.executeUpdate("insert into Chats(Chatname, OwningUserID) values ('"
                    + chatname + "', '" + userid + "')");

            ResultSet rs = st
                    .executeQuery("select id from Chats where Chatname ='"
                            + chatname + "'");
            while (rs.next()) {
                chatid = rs.getInt(1);
            }
            rs.close();

            con.close();
        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return chatid;
    }

    public int CreateContentMessage(String message, String msgType) {

        int key = 0;

        try {
            Connection con = new MySqlConnection().getMySqlConnection();

            Statement st = con.createStatement();
            if (msgType.equalsIgnoreCase(Constants.TYP_TEXT)) {
                st.executeUpdate("insert into Text (Text) values ('" + message
                        + "')", Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = st.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    key = rs.getInt(1);
                }
                rs.close();
            } else if (msgType.equalsIgnoreCase(Constants.TYP_IMAGE)) {
                st.executeUpdate("insert into Image (Image) values ('"
                        + message + "')", Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = st.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    key = rs.getInt(1);
                }
                rs.close();
            } else if (msgType.equalsIgnoreCase(Constants.TYP_VIDEO)) {
                st.executeUpdate("insert into Video (Video) values ('"
                        + message + "')", Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = st.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    key = rs.getInt(1);
                }
                rs.close();
            } else if (msgType.equalsIgnoreCase(Constants.TYP_FILE)) {
                st.executeUpdate("insert into File (File) values ('" + message
                        + "')", Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = st.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    key = rs.getInt(1);
                }
                rs.close();
            } else if (msgType.equalsIgnoreCase(Constants.TYP_LOCATION)) {
                st.executeUpdate("insert into Location (Location) values ('"
                        + message + "')", Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = st.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    key = rs.getInt(1);
                }
                rs.close();
            } else if (msgType.equalsIgnoreCase(Constants.TYP_CONTACT)) {
                st.executeUpdate("insert into Contact (Contact) values ('"
                        + message + "')", Statement.RETURN_GENERATED_KEYS);
                ResultSet rs = st.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    key = rs.getInt(1);
                }
                rs.close();
            }

            st.close();

            con.close();
        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return key;
    }

    public int AddUserToChat(int userid, int chatid) {

        int key = 0;
        try {
            Connection con = new MySqlConnection().getMySqlConnection();

            Statement st = con.createStatement();

            st.executeUpdate(
                    "insert into UserToChats(UserID, ChatID) values ('"
                            + userid + "', '" + chatid + "')",
                    Statement.RETURN_GENERATED_KEYS);

            ResultSet resultSet2 = st.getGeneratedKeys();
            if (resultSet2 != null && resultSet2.next()) {
                key = resultSet2.getInt(1);
            }

            con.close();
        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return key;
    }

    public int insertMessage(int uid, int User2ChatID, String MsgTyp,
            int MsgID, int OriginID, boolean origin) {

        String SQL = "Update Messages set OriginMsgID = ? where ID = ?";
        int key = 0;
        try {
            Connection con = new MySqlConnection().getMySqlConnection();

            Statement st = con.createStatement();
            if (MsgTyp.equalsIgnoreCase(Constants.TYP_TEXT)) {
                st.executeUpdate(
                        "insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, TextMsgID, OriginMsgID) values ('"
                                + uid
                                + "', '"
                                + Constants.TYP_TEXT
                                + "', 10, "
                                + User2ChatID
                                + ", "
                                + MsgID
                                + ", "
                                + OriginID
                                + ")", Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSet2 = st.getGeneratedKeys();
                if (resultSet2 != null && resultSet2.next()) {
                    key = resultSet2.getInt(1);
                    if (origin) {
                        PreparedStatement pstmt = null;

                        pstmt = con.prepareStatement(SQL);
                        pstmt.setInt(1, key);
                        pstmt.setInt(2, key);
                        pstmt.executeUpdate();
                    }
                }
            } else if (MsgTyp.equalsIgnoreCase(Constants.TYP_IMAGE)) {
                st.executeUpdate(
                        "insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, ImageMsgID, OriginMsgID) values ('"
                                + uid
                                + "', '"
                                + Constants.TYP_IMAGE
                                + "', 10, "
                                + User2ChatID
                                + ", "
                                + MsgID
                                + ", "
                                + OriginID + ")",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSet2 = st.getGeneratedKeys();
                if (resultSet2 != null && resultSet2.next()) {
                    key = resultSet2.getInt(1);
                    if (origin) {
                        PreparedStatement pstmt = null;

                        pstmt = con.prepareStatement(SQL);
                        pstmt.setInt(1, key);
                        pstmt.setInt(2, key);
                        pstmt.executeUpdate();
                    }
                }
            } else if (MsgTyp.equalsIgnoreCase(Constants.TYP_CONTACT)) {
                st.executeUpdate(
                        "insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, ContactMsgID, OriginMsgID) values ('"
                                + uid
                                + "', '"
                                + Constants.TYP_CONTACT
                                + "', 10, "
                                + User2ChatID
                                + ", "
                                + MsgID
                                + ", "
                                + OriginID + ")",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSet2 = st.getGeneratedKeys();
                if (resultSet2 != null && resultSet2.next()) {
                    key = resultSet2.getInt(1);
                    if (origin) {
                        PreparedStatement pstmt = null;

                        pstmt = con.prepareStatement(SQL);
                        pstmt.setInt(1, key);
                        pstmt.setInt(2, key);
                        pstmt.executeUpdate();
                    }
                }
            } else if (MsgTyp.equalsIgnoreCase(Constants.TYP_LOCATION)) {
                st.executeUpdate(
                        "insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, LocationMsgID, OriginMsgID) values ('"
                                + uid
                                + "', '"
                                + Constants.TYP_LOCATION
                                + "', 10, "
                                + User2ChatID
                                + ", "
                                + MsgID
                                + ", "
                                + OriginID + ")",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSet2 = st.getGeneratedKeys();
                if (resultSet2 != null && resultSet2.next()) {
                    key = resultSet2.getInt(1);
                    if (origin) {
                        PreparedStatement pstmt = null;

                        pstmt = con.prepareStatement(SQL);
                        pstmt.setInt(1, key);
                        pstmt.setInt(2, key);
                        pstmt.executeUpdate();
                    }
                }
            } else if (MsgTyp.equalsIgnoreCase(Constants.TYP_FILE)) {
                st.executeUpdate(
                        "insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, FileMsgID, OriginMsgID) values ('"
                                + uid
                                + "', '"
                                + Constants.TYP_FILE
                                + "', 10, "
                                + User2ChatID
                                + ", "
                                + MsgID
                                + ", "
                                + OriginID
                                + ")", Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSet2 = st.getGeneratedKeys();
                if (resultSet2 != null && resultSet2.next()) {
                    key = resultSet2.getInt(1);
                    if (origin) {
                        PreparedStatement pstmt = null;

                        pstmt = con.prepareStatement(SQL);
                        pstmt.setInt(1, key);
                        pstmt.setInt(2, key);
                        pstmt.executeUpdate();
                    }
                }
            } else if (MsgTyp.equalsIgnoreCase(Constants.TYP_VIDEO)) {
                st.executeUpdate(
                        "insert into Messages(OwningUserID, MessageTyp, SendTimestamp, UsertoChatID, VideoMsgID, OriginMsgID) values ('"
                                + uid
                                + "', '"
                                + Constants.TYP_VIDEO
                                + "', 10, "
                                + User2ChatID
                                + ", "
                                + MsgID
                                + ", "
                                + OriginID + ")",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSet2 = st.getGeneratedKeys();
                if (resultSet2 != null && resultSet2.next()) {
                    key = resultSet2.getInt(1);
                    if (origin) {
                        PreparedStatement pstmt = null;

                        pstmt = con.prepareStatement(SQL);
                        pstmt.setInt(1, key);
                        pstmt.setInt(2, key);
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e);
        }
        return key;
    }

    public int InsertAndSaveImage(File inFile) {

        long currentTime = System.currentTimeMillis() / 1000L;
        String filetime = Objects.toString(currentTime, null);

        String filePath = Constants.SERVER_UPLOAD_LOCATION_FOLDER + "images/"
                + filetime + inFile.getName();

        File dest = new File(filePath);
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(inFile);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(e);
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }

        int key = -1;
        ResultSet resultSet = null;
        Statement statement = null;

        try {
            Connection con = new MySqlConnection().getMySqlConnection();
            statement = con.createStatement();
            // TODO first check if Message already exists, idempotent?
            /* First we create a chat room */
            statement.executeUpdate("insert into Image (Image) values ('"
                    + filetime + inFile.getName() + "')",
                    Statement.RETURN_GENERATED_KEYS);
            resultSet = statement.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                key = resultSet.getInt(1);
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
        return key;
    }

    public int InsertFixedImage() {

        String filetime = "1010101010";
        String filename = "test.jpg";

        int key = -1;
        ResultSet resultSet = null;
        Statement statement = null;

        try {
            Connection con = new MySqlConnection().getMySqlConnection();
            statement = con.createStatement();
            statement.executeUpdate("insert into Image (Image) values ('"
                    + filetime + filename + "')",
                    Statement.RETURN_GENERATED_KEYS);
            resultSet = statement.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                key = resultSet.getInt(1);
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
                // Do nothing we are closing
            }
        }
        return key;
    }

    public int InsertFixedVideo() {

        String filetime = "1010101010";
        String filename = "test.mp4";

        int key = -1;
        ResultSet resultSet = null;
        Statement statement = null;

        try {
            Connection con = new MySqlConnection().getMySqlConnection();
            statement = con.createStatement();
            statement.executeUpdate("insert into Video (Video) values ('"
                    + filetime + filename + "')",
                    Statement.RETURN_GENERATED_KEYS);
            resultSet = statement.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                key = resultSet.getInt(1);
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
                // Do nothing we are closing
            }
        }
        return key;
    }

    public void deleteAndDropImage(int imgid) {

        String fname = null;
        ResultSet resultSet = null;
        Statement statement = null;

        try {
            Connection con = new MySqlConnection().getMySqlConnection();
            statement = con.createStatement();

            resultSet = statement
                    .executeQuery("select Image from Image where ID = " + imgid);
            if (resultSet != null) {
                if (resultSet.next()) {
                    fname = resultSet.getString("Image");

                    String filePath = Constants.SERVER_UPLOAD_LOCATION_FOLDER
                            + "images/" + fname;
                    File delf = new File(filePath);
                    if (delf.exists()) {
                        delf.delete();
                    }
                }
            }

            String SQL = "DELETE FROM Image WHERE ID = ? ";
            PreparedStatement pstmt = null;

            pstmt = con.prepareStatement(SQL);
            pstmt.setInt(1, imgid);
            pstmt.executeUpdate();

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
                // Do nothing we are closing
            }
        }
    }

    public void deleteFixedImage(int imgid) {

        try {
            Connection con = new MySqlConnection().getMySqlConnection();

            String SQL = "DELETE FROM Image WHERE ID = ? ";
            PreparedStatement pstmt = null;

            pstmt = con.prepareStatement(SQL);
            pstmt.setInt(1, imgid);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }

    public void deleteFixedVideo(int vidid) {

        try {
            Connection con = new MySqlConnection().getMySqlConnection();

            String SQL = "DELETE FROM Video WHERE ID = ? ";
            PreparedStatement pstmt = null;

            pstmt = con.prepareStatement(SQL);
            pstmt.setInt(1, vidid);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }

    public void setTimestamp(int MsgID, long stamp, String Type) {

        try {
            String SQL;
            Connection con = new MySqlConnection().getMySqlConnection();

            if (Type.equalsIgnoreCase("READ")) {
                SQL = "Update Messages SET ReadTimestamp = ? where ID = ?";
            } else {
                SQL = "Update Messages SET ShowTimestamp = ? where ID = ?";
            }
            PreparedStatement pstmt = null;

            pstmt = con.prepareStatement(SQL);
            pstmt.setLong(1, stamp);
            pstmt.setInt(2, MsgID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }
}