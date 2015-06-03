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
package de.radiohacks.frinmeba.util;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
import de.radiohacks.frinmeba.services.Constants;

public interface ServiceUtil {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/authenticate")
	public OAuth AuthenticateUser(
			@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/signup")
	public OSiUp SingUpUser(@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPemail) String Email);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/createchat")
	public OCrCh CreateChat(@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPchatname) String Chatname);

	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@Path("/deletechat")
	public ODeCh DeleteChat(@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPchatid) int ChatID);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/addusertochat")
	public OAdUC AddUserToChat(@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPuserid) int UserID,
			@QueryParam(Constants.QPchatid) int ChatID);

	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@Path("/removeuserfromchat")
	public OReUC RemoveUserFromChat(
			@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPchatid) int ChatID,
			@QueryParam(Constants.QPuserid) int UserID);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/listuser")
	public OLiUs ListUsers(@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPsearch) String search);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/listchat")
	public OLiCh ListChats(@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/sendtextmessage")
	public OSTeM sendTextMessage(@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPtextmessage) String TextMessage);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/gettextmessage")
	public OGTeM getTextMessage(@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPtextmessageid) int TextMessageID);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/insertmessageintochat")
	public OIMIC insertMessageIntoChat(
			@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPchatid) int ChatID,
			@QueryParam(Constants.QPmessageid) int MessageID,
			@QueryParam(Constants.QPmessagetype) String MessageType);

	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@Path("/deletemessagefromchat")
	public ODMFC deleteMessageFromChat(
			@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPmessageid) int MessageID);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/getmessagefromchat")
	public OFMFC getMessageFromChat(
			@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPchatid) int ChatID,
			@QueryParam(Constants.QPtimestamp) int Timestamp);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/checknew")
	public OCN checkNew(@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/setshowtimestamp")
	public OSShT setShowTimeStamp(
			@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPmessageid) int MessageID);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/getmessageinformation")
	public OGMI getMessageInformation(
			@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPmessageid) int MessageID);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/acknowledgemessagedownload")
	public OAckMD acknowledgeMessageDownload(
			@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPmessageid) int MessageID,
			@QueryParam(Constants.QPacknowledge) String Acknowledge);
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/acknowledgechatdownload")
	public OAckCD acknowledgeChatDownload(
			@QueryParam(Constants.QPusername) String User,
			@QueryParam(Constants.QPpassword) String Password,
			@QueryParam(Constants.QPchatid) int ChatID,
			@QueryParam(Constants.QPacknowledge) String Acknowledge);
}