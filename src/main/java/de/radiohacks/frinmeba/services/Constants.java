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

import org.apache.log4j.Logger;

import de.radiohacks.frinmeba.database.Check;

public class Constants {
    
    private static final Logger LOGGER = Logger.getLogger(Check.class);
    
    public static final String NONE_EXISTING_USER = "NONE_EXISTING_USER";
    public static final String NONE_EXISTING_CHAT = "NONE_EXISTING_CHAT";
    public static final String NO_TEXTMESSAGE_GIVEN = "NO_TEXTMESSAGE_GIVEN";
    public static final String NO_IMAGEMESSAGE_GIVEN = "NO_IMAGEMESSAGE_GIVEN";
    public static final String NONE_EXISTING_MESSAGE = "NONE_EXISTING_MESSAGE";
    public static final String INVALID_MESSAGE_TYPE = "INVALID_MESSAGE_TYPE";
    public static final String INVALID_EMAIL_ADRESS = "INVALID_EMAIL_ADRESS";
    public static final String MISSING_CHATNAME = "MISSING_CHATNAME";
    public static final String NONE_EXISTING_CONTENT_MESSAGE = "NONE_EXISTING_CONTENT_MESSAGE";
    public static final String TYPE_NOT_FOUND = "TYPE_NOT_FOUND";
    public static final String FILE_NOT_FOUND = "FILE_NOT_FOUND";
    public static final String NOT_MESSAGE_OWNER = "NOT_MESSAGE_OWNER";
    public static final String NOT_CHAT_OWNER = "NOT_CHAT_OWNER";
    public static final String DB_ERROR = "DATABASE_ERROR";
    public static final String USER_NOT_ACTIVE = "USER_NOT_ACTIVE";
    public static final String WRONG_PASSWORD = "WRONG_PASSWORD";
    public static final String NO_USERNAME_OR_PASSWORD = "NO_USERNAME_OR_PASSWORD";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String NO_ACTIVE_CHATS = "NO_ACTIVE_CHATS";
    public static final String CHAT_OWNER_NOT_ADDED = "CHAT_OWNER_NOT_ADDED";
    public static final String USER_ALREADY_IN_CHAT = "USER_ALREADY_IN_CHAT";
    public static final String ENCODING_ERROR = "ENCODING_ERROR";
    public static final String NO_CONTENT_GIVEN = "NO_CONTENT_GIVEN";
    public static final String MESSAGE_NOT_READ = "MESSAGE_NOT_READ";
    public static final String UPLOAD_FAILED = "UPLOAD_FAILED";
    public static final String NO_QUADRAT_IMAGE = "NO_QUADRAT_IMAGE";
    public static final String ICON_ADDED = "ICON_ADDED";

    public static final String USER_ADDED = "USER_ADDED";
    public static final String SIGNUP_SUCCESSFUL = "SIGNUP_SUCCESSFUL";
    public static final String CHAT_DELETED = "CHAT_DELETED";

    public static final String AUTHENTICATE_TRUE = "TRUE";
    public static final String AUTHENTICATE_FALSE = "FALSE";
    public static final String ACKNOWLEDGE_TRUE = "TRUE";
    public static final String ACKNOWLEDGE_FALSE = "FALSE";

    public static final String TYP_TEXT = "TEXT";
    public static final String TYP_IMAGE = "IMAGE";
    public static final String TYP_LOCATION = "LOCATION";
    public static final String TYP_CONTACT = "CONTACT";
    public static final String TYP_FILE = "FILE";
    public static final String TYP_VIDEO = "VIDEO";

    /* Where should the content go to on the Server */
    public static final String SERVER_UPLOAD_LOCATION_FOLDER = "/opt/frinme-data";
    public static final String SERVER_IMAGE_FOLDER = "images/";
    public static final String SERVER_VIDEO_FOLDER = "videos/";
    public static final String SERVER_FILE_FOLDER = "files/";
    
    /* Constants for the query Parameter names */
    public static final String QP_USERNAME = "username";
    public static final String QP_PASSWORD = "password";
    public static final String QP_EMAIL = "email";
    public static final String QP_CHATNAME = "chatname";
    public static final String QP_CHATID = "chatid";
    public static final String QP_USERID = "userid";
    public static final String QP_SEARCH = "search";
    public static final String QP_TEXTMESSAGE = "textmessage";
    public static final String QP_TEXTMESSAGEID = "textmessageid";
    public static final String QP_MESSAGEID = "messageid";
    public static final String QP_MESSAGETYPE = "messagetype";
    public static final String QP_TIMESTAMP = "timestamp";
    public static final String QP_IMAGEID = "imageid";
    public static final String QP_VIDEOID = "videoid";
    public static final String QP_ACKNOWLEDGE = "acknowledge";

    /*
     * Global Characterset for Base64 en- and decode
     */
    public static final String CHARACTERSET = "UTF-8";

    /*
     * Length of fields
     */
    public static final int CHATNAME_LENGTH = 50;
    public static final int USERNAME_LENGTH = 45;
    
    public Constants () {
        super();
    }
    
    private String mkDefaultFolder(String subdir) {
        String uploadFolder = null;
        uploadFolder = System.getProperty("user.home") + File.separatorChar + "frinme_data";
        if(!subdir.isEmpty()) {
            uploadFolder = uploadFolder + File.separatorChar + subdir;
        }
        File file = new File(uploadFolder);
        file.mkdirs();
        
        return uploadFolder;
    }
    
    public String getUploadFolder() {
        String uploadFolder = SERVER_UPLOAD_LOCATION_FOLDER;
        
        File file = new File(uploadFolder);
        if(!file.exists()){
            uploadFolder = this.mkDefaultFolder("");
        }
        
        if(!file.canWrite()){
            uploadFolder = this.mkDefaultFolder("");
            LOGGER.warn("SERVER_UPLOAD_LOCATION_FOLDER : " + uploadFolder);
        }
        
        LOGGER.debug("SERVER_UPLOAD_LOCATION_FOLDER : " + uploadFolder);
        return uploadFolder;
        
    }
    
    public String getUploadFolderImage() {
        String uploadFolder = getUploadFolder() + File.separatorChar + SERVER_IMAGE_FOLDER;
        
        File file = new File(uploadFolder);
        if(!file.exists()){
            uploadFolder = this.mkDefaultFolder(SERVER_IMAGE_FOLDER);
        }
        
        if(!file.canWrite()){
            uploadFolder = this.mkDefaultFolder(SERVER_IMAGE_FOLDER);
            LOGGER.warn("SERVER_UPLOAD_IMAGE_FOLDER : " + uploadFolder);
        }
        
        LOGGER.debug("SERVER_UPLOAD_IMAGE_FOLDER : " + uploadFolder);
        return uploadFolder;
        
    }
    
    public String getUploadFolderVideo() {
        String uploadFolder = getUploadFolder() + File.separatorChar + SERVER_VIDEO_FOLDER;
        
        File file = new File(uploadFolder);
        if(!file.exists()){
            uploadFolder = this.mkDefaultFolder(SERVER_VIDEO_FOLDER);
        }
        
        if(!file.canWrite()){
            uploadFolder = this.mkDefaultFolder(SERVER_VIDEO_FOLDER);
            LOGGER.warn("SERVER_UPLOAD_VIDEO_FOLDER : " + uploadFolder);
        }
        
        LOGGER.debug("SERVER_UPLOAD_VIDEO_FOLDER : " + uploadFolder);
        return uploadFolder;
        
    }
    
    public String getUploadFolderFiles() {
        String uploadFolder = getUploadFolder() + File.separatorChar + SERVER_FILE_FOLDER;
        
        File file = new File(uploadFolder);
        if(!file.exists()){
            uploadFolder = this.mkDefaultFolder(SERVER_FILE_FOLDER);
        }
        
        if(!file.canWrite()){
            uploadFolder = this.mkDefaultFolder(SERVER_FILE_FOLDER);
            LOGGER.warn("SERVER_UPLOAD_FILE_FOLDER : " + uploadFolder);
        }
        
        LOGGER.debug("SERVER_UPLOAD_FILE_FOLDER : " + uploadFolder);
        return uploadFolder;
        
    }
    
}
