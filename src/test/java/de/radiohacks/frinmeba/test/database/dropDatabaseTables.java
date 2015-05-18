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

public class dropDatabaseTables {
	
	private final static String DropMessages = "DROP TABLE IF EXISTS `Messages`";
	private final static String DropText = "DROP TABLE IF EXISTS `Text`";
	private final static String DropUserToChats = "DROP TABLE IF EXISTS `UserToChats`";
	private final static String DropChats = "DROP TABLE IF EXISTS `Chats`";
	private final static String DropContact = "DROP TABLE IF EXISTS `Contact`";
	private final static String DropFile = "DROP TABLE IF EXISTS `File`";
	private final static String DropImage = "DROP TABLE IF EXISTS `Image`";
	private final static String DropLocation = "DROP TABLE IF EXISTS `Location`";
	private final static String DropUsers = "DROP TABLE IF EXISTS `Users`";
	private final static String DropVideo = "DROP TABLE IF EXISTS `Video`";
	
	public dropDatabaseTables() {
	};
	
	public void dropTable() {
		Statement st;
		try {
			Connection con = new MySqlConnection().getMySqlConnection();
			st = con.createStatement();
			st.execute(DropMessages);
			st.execute(DropText);
			st.execute(DropUserToChats);
			st.execute(DropChats);
			st.execute(DropContact);
			st.execute(DropFile);
			st.execute(DropImage);
			st.execute(DropLocation);
			st.execute(DropUsers);
			st.execute(DropVideo);
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}