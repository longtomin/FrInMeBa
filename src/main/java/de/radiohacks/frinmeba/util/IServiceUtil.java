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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.radiohacks.frinmeba.modelshort.IAckCD;
import de.radiohacks.frinmeba.modelshort.IAckMD;
import de.radiohacks.frinmeba.modelshort.IAdUC;
import de.radiohacks.frinmeba.modelshort.ICrCh;
import de.radiohacks.frinmeba.modelshort.IICIc;
import de.radiohacks.frinmeba.modelshort.IIMIC;
import de.radiohacks.frinmeba.modelshort.IIUIc;
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
import de.radiohacks.frinmeba.services.Constants;

public interface IServiceUtil {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/authenticate")
	public OAuth authenticateUser(
			@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password);

	@PUT
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/signup")
	public OSiUp singUpUser(ISiUp in);

	@PUT
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/createchat")
	public OCrCh createChat(ICrCh in);

	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@Path("/deletechat")
	public ODeCh deleteChat(@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password,
			@QueryParam(Constants.QP_CHATID) int chatID);

	@PUT
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/addusertochat")
	public OAdUC addUserToChat(IAdUC in);

	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@Path("/removeuserfromchat")
	public OReUC removeUserFromChat(
			@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password,
			@QueryParam(Constants.QP_USERID) int userID,
			@QueryParam(Constants.QP_CHATID) int chatID);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/listuser")
	public OLiUs listUsers(@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password,
			@QueryParam(Constants.QP_SEARCH) String search);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/listchat")
	public OLiCh listChats(@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password);

	@PUT
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/sendtextmessage")
	public OSTeM sendTextMessage(ISTeM in);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/gettextmessage")
	public OGTeM getTextMessage(@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password,
			@QueryParam(Constants.QP_TEXTMESSAGEID) int textMessageID);

	@PUT
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/insertmessageintochat")
	public OIMIC insertMessageIntoChat(IIMIC in);

	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@Path("/deletemessagefromchat")
	public ODMFC deleteMessageFromChat(
			@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password,
			@QueryParam(Constants.QP_MESSAGEID) int messageID);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/getmessagefromchat")
	public OFMFC getMessageFromChat(
			@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password,
			@QueryParam(Constants.QP_CHATID) int chatID,
			@QueryParam(Constants.QP_TIMESTAMP) int timestamp);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/checknew")
	public OCN checkNew(@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password);

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/setshowtimestamp")
	public OSShT setShowTimeStamp(ISShT in);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/getmessageinformation")
	public OGMI getMessageInformation(
			@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password,
			@QueryParam(Constants.QP_MESSAGEID) List<Integer> messageID);

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/acknowledgemessagedownload")
	public OAckMD acknowledgeMessageDownload(IAckMD in);

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/acknowledgechatdownload")
	public OAckCD acknowledgeChatDownload(IAckCD in);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/syncuser")
	public OSU syncUser(@QueryParam(Constants.QP_USERNAME) String user,
			@QueryParam(Constants.QP_PASSWORD) String password,
			@QueryParam(Constants.QP_USERID) List<Integer> userID);

	@PUT
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/insertusericon")
	public OIUIc insertUserIcon(IIUIc in);

	@PUT
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/insertchaticon")
	public OICIc insertChatIcon(IICIc in);
}