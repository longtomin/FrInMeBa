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

public class Constants {
	public final static String NONE_EXISTING_USER = "NONE_EXISTING_USER";
	public final static String NONE_EXISTING_CHAT = "NONE_EXISTING_CHAT";
	public final static String NO_TEXTMESSAGE_GIVEN = "NO_TEXTMESSAGE_GIVEN";
	public static final String NO_IMAGEMESSAGE_GIVEN = "NO_IMAGEMESSAGE_GIVEN";
	public final static String NONE_EXISTING_MESSAGE = "NONE_EXISTING_MESSAGE";
	public final static String INVALID_MESSAGE_TYPE = "INVALID_MESSAGE_TYPE";
	public final static String INVALID_EMAIL_ADRESS = "INVALID_EMAIL_ADRESS";
	public final static String MISSING_CHATNAME = "MISSING_CHATNAME";
	public final static String NONE_EXISTING_CONTENT_MESSAGE = "NONE_EXISTING_CONTENT_MESSAGE";
	public final static String TYPE_NOT_FOUND = "TYPE_NOT_FOUND";
	public final static String FILE_NOT_FOUND = "FILE_NOT_FOUND";
	public final static String NOT_MESSAGE_OWNER = "NOT_MESSAGE_OWNER";
	public final static String NOT_CHAT_OWNER = "NOT_CHAT_OWNER";
	public final static String DB_ERROR = "DATABASE_ERROR";
	public final static String USER_NOT_ACTIVE = "USER_NOT_ACTIVE";
	public final static String WRONG_PASSWORD = "WRONG_PASSWORD";
	public final static String NO_USERNAME_OR_PASSWORD = "NO_USERNAME_OR_PASSWORD";
	public final static String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
	public final static String NO_ACTIVE_CHATS = "NO_ACTIVE_CHATS";
	public final static String CHAT_OWNER_NOT_ADDED = "CHAT_OWNER_NOT_ADDED";
	public final static String USER_ALREADY_IN_CHAT = "USER_ALREADY_IN_CHAT";
	public final static String ENCODING_ERROR = "ENCODING_ERROR";
	public final static String NO_CONTENT_GIVEN = "NO_CONTENT_GIVEN";
	public final static String MESSAGE_NOT_READ = "MESSAGE_NOT_READ";

	public final static String USER_ADDED = "USER_ADDED";
	public final static String SIGNUP_SUCCESSFUL = "SIGNUP_SUCCESSFUL";
	public final static String CHAT_DELETED = "CHAT_DELETED";

	public final static String AUTHENTICATE_TRUE = "TRUE";
	public final static String AUTHENTICATE_FALSE = "FALSE";
	public final static String ACKNOWLEDGE_TRUE = "TRUE";
	public final static String ACKNOWLEDGE_FALSE = "FALSE";

	public final static String TYP_TEXT = "TEXT";
	public final static String TYP_IMAGE = "IMAGE";
	public final static String TYP_LOCATION = "LOCATION";
	public final static String TYP_CONTACT = "CONTACT";
	public final static String TYP_FILE = "FILE";
	public final static String TYP_VIDEO = "VIDEO";

	/* Where should the content go to on the Server */
	public static final String SERVER_UPLOAD_LOCATION_FOLDER = "/opt/frinme-data/";
	public static final String SERVER_IMAGE_FOLDER = "images/";
	public static final String SERVER_VIDEO_FOLDER = "videos/";
	public static final String SERVER_FILE_FOLDER = "files/";

	/* Constants for the query Parameter names */
	public static final String QPusername = "username";
	public static final String QPpassword = "password";
	public static final String QPemail = "email";
	public static final String QPchatname = "chatname";
	public static final String QPchatid = "chatid";
	public static final String QPuserid = "userid";
	public static final String QPsearch = "search";
	public static final String QPtextmessage = "textmessage";
	public static final String QPtextmessageid = "textmessageid";
	public static final String QPmessageid = "messageid";
	public static final String QPmessagetype = "messagetype";
	public static final String QPtimestamp = "timestamp";
	public static final String QPimageid = "imageid";
	public static final String QPvideoid = "videoid";
	public static final String QPacknowledge = "acknowledge";

	/*
	 * Global Characterset for Base64 en- and decode
	 */
	public static final String CharacterSet = "UTF-8";

	/*
	 * Length of fields
	 */
	public static final int Chatname_length = 50;
	public static final int Username_length = 45;
}
