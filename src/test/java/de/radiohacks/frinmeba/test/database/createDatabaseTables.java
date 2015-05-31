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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import de.radiohacks.frinmeba.database.MySqlConnection;

public class createDatabaseTables {

	private final static String CreateChats = "CREATE TABLE IF NOT EXISTS `Chats` ( "
			+ "`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, "
			+ "`Chatname` varchar(50) NOT NULL, "
			+ "`OwningUserID` int(10) unsigned NOT NULL, "
			+ "PRIMARY KEY (`ID`), "
			+ "KEY `OwningUserID` (`OwningUserID`) "
			+ ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";

	private final static String CreateContact = "CREATE TABLE IF NOT EXISTS `Contact` ( "
			+ "`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, "
			+ "`Contact` varchar(1024) NOT NULL, "
			+ "PRIMARY KEY (`ID`) "
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";

	private final static String CreateFiles = "CREATE TABLE IF NOT EXISTS `File` ( "
			+ "`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, "
			+ "`File` varchar(1024) NOT NULL, "
			+ "`MD5Sum` varchar(32) NOT NULL DEFAULT '0', "
			+ "PRIMARY KEY (`ID`) "
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";

	private final static String CreateImage = "CREATE TABLE IF NOT EXISTS `Image` ( "
			+ "`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, "
			+ "`Image` varchar(1024) NOT NULL, "
			+ "`MD5Sum` varchar(32) NOT NULL DEFAULT '0', "
			+ "PRIMARY KEY (`ID`) "
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";

	private final static String CreateLocation = "CREATE TABLE IF NOT EXISTS `Location` ( "
			+ "`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, "
			+ "`Location` varchar(1024) NOT NULL, "
			+ "PRIMARY KEY (`ID`) "
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";

	private final static String CreateMessages = "CREATE TABLE IF NOT EXISTS `Messages` ( "
			+ "`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, "
			+ "`OwningUserID` int(10) unsigned NOT NULL, "
			+ "`MessageTyp` varchar(10) NOT NULL, "
			+ "`SendTimestamp` bigint(20) NOT NULL, "
			+ "`ReadTimestamp` bigint(20) NOT NULL DEFAULT '0', "
			+ "`TempReadTimestamp` bigint(20) NOT NULL DEFAULT '0', "
			+ "`ShowTimestamp` bigint(20) NOT NULL DEFAULT '0', "
			+ "`UsertoChatID` int(10) unsigned NOT NULL, "
			+ "`TextMsgID` int(10) unsigned DEFAULT NULL, "
			+ "`ImageMsgID` int(10) unsigned DEFAULT NULL, "
			+ "`LocationMsgID` int(10) unsigned DEFAULT NULL, "
			+ "`ContactMsgID` int(10) unsigned DEFAULT NULL, "
			+ "`FileMsgID` int(10) unsigned DEFAULT NULL, "
			+ "`VideoMsgID` int(10) unsigned DEFAULT NULL, "
			+ "`OriginMsgID` int(10) unsigned DEFAULT NULL, "
			+ "PRIMARY KEY (`ID`), "
			+ "KEY `ImageMsgIDIdx` (`ImageMsgID`), "
			+ "KEY `LocationMsgIDIdx` (`LocationMsgID`), "
			+ "KEY `ContectMsgIDIdx` (`ContactMsgID`), "
			+ "KEY `FileMsgID` (`FileMsgID`), "
			+ "KEY `OwningUserID` (`OwningUserID`), "
			+ "KEY `UserToChatIDIdx` (`UsertoChatID`), "
			+ "KEY `TextMsgIDIdx` (`TextMsgID`), "
			+ "KEY `VideoMsgID` (`VideoMsgID`) "
			+ ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ";

	private final static String CreateText = "CREATE TABLE IF NOT EXISTS `Text` ("
			+ "`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, "
			+ "`Text` varchar(10000) NOT NULL, "
			+ "PRIMARY KEY (`ID`) "
			+ ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";

	private final static String CreateUsers = "CREATE TABLE IF NOT EXISTS `Users` ( "
			+ "`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, "
			+ "`Username` varchar(45) NOT NULL, "
			+ "`B64Username` varchar(55) NOT NULL, "
			+ "`Password` char(64) NOT NULL, "
			+ "`Email` varchar(100) NOT NULL, "
			+ "`SignupDate` bigint(20) NOT NULL DEFAULT '0', "
			+ "`Status` tinyint(3) DEFAULT NULL, "
			+ "`AuthenticationTime` bigint(20) NOT NULL DEFAULT '0', "
			+ "`Active` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'User activ set by admin', "
			+ "PRIMARY KEY (`ID`) "
			+ ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ";

	private final static String CreateUserToChats = "CREATE TABLE IF NOT EXISTS `UserToChats` ( "
			+ "`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, "
			+ "`UserID` int(10) unsigned NOT NULL, "
			+ "`ChatID` int(10) unsigned NOT NULL, "
			+ "`ReadTimestamp` bigint(20) NOT NULL DEFAULT '0', "
			+ "`TempReadTimestamp` bigint(20) NOT NULL DEFAULT '0', "
			+ "PRIMARY KEY (`ID`), "
			+ "KEY `UserIDIdx` (`UserID`), "
			+ "KEY `ChatIDIdx` (`ChatID`), "
			+ "KEY `ChatID` (`ChatID`) "
			+ ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";

	private final static String CreateVideo = "CREATE TABLE IF NOT EXISTS `Video` ( "
			+ "`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, "
			+ "`Video` varchar(1024) NOT NULL, "
			+ "`MD5Sum` varchar(32) NOT NULL DEFAULT '0', "
			+ "PRIMARY KEY (`ID`) "
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";

	private final static String AlterChats = "ALTER TABLE `Chats` "
			+ "ADD CONSTRAINT `Chats_ibfk_1` FOREIGN KEY (`OwningUserID`) REFERENCES `Users` (`ID`)";

	private final static String AlterMessages1 = "ALTER TABLE `Messages` ADD  FOREIGN KEY (`OwningUserID`) REFERENCES `aim`.`Users`(`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
	private final static String AlterMessages2 = "ALTER TABLE `Messages` ADD  FOREIGN KEY (`UsertoChatID`) REFERENCES `aim`.`UserToChats`(`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
	private final static String AlterMessages3 = "ALTER TABLE `Messages` ADD  FOREIGN KEY (`TextMsgID`) REFERENCES `aim`.`Text`(`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
	private final static String AlterMessages4 = "ALTER TABLE `Messages` ADD  FOREIGN KEY (`ImageMsgID`) REFERENCES `aim`.`Image`(`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
	private final static String AlterMessages5 = "ALTER TABLE `Messages` ADD  FOREIGN KEY (`LocationMsgID`) REFERENCES `aim`.`Location`(`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
	private final static String AlterMessages6 = "ALTER TABLE `Messages` ADD  FOREIGN KEY (`ContactMsgID`) REFERENCES `aim`.`Contact`(`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
	private final static String AlterMessages7 = "ALTER TABLE `Messages` ADD  FOREIGN KEY (`FileMsgID`) REFERENCES `aim`.`File`(`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
	private final static String AlterMessages8 = "ALTER TABLE `Messages` ADD  FOREIGN KEY (`VideoMsgID`) REFERENCES `aim`.`Video`(`ID`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
		
	private final static String AlterUserToChats = "ALTER TABLE `UserToChats` "
			+ "ADD CONSTRAINT `UserToChats_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `Users` (`ID`), "
			+ "ADD CONSTRAINT `UserToChats_ibfk_2` FOREIGN KEY (`ChatID`) REFERENCES `Chats` (`ID`)";

	public createDatabaseTables() {
	};
	
	public void createTable() {
		Statement st;
		try {
			Connection con = new MySqlConnection().getMySqlConnection();
			st = con.createStatement();
			st.execute(CreateChats);
			st.execute(CreateContact);
			st.execute(CreateFiles);
			st.execute(CreateImage);
			st.execute(CreateLocation);
			st.execute(CreateMessages);
			st.execute(CreateText);
			st.execute(CreateUsers);
			st.execute(CreateUserToChats);
			st.execute(CreateVideo);

			st.execute(AlterChats);
			st.execute(AlterMessages1);
			st.execute(AlterMessages2);
			st.execute(AlterMessages3);
			st.execute(AlterMessages4);
			st.execute(AlterMessages5);
			st.execute(AlterMessages6);
			st.execute(AlterMessages7);
			st.execute(AlterMessages8);
			st.execute(AlterUserToChats);
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
