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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import de.radiohacks.frinmeba.model.jaxb.IAckCD;
import de.radiohacks.frinmeba.model.jaxb.IAckMD;
import de.radiohacks.frinmeba.model.jaxb.IAdUC;
import de.radiohacks.frinmeba.model.jaxb.ICrCh;
import de.radiohacks.frinmeba.model.jaxb.IICIc;
import de.radiohacks.frinmeba.model.jaxb.IIMIC;
import de.radiohacks.frinmeba.model.jaxb.IIUIc;
import de.radiohacks.frinmeba.model.jaxb.ISShT;
import de.radiohacks.frinmeba.model.jaxb.ISTeM;
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
import de.radiohacks.frinmeba.services.Constants;

public interface IServiceUtil {

    @PUT
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/signup")
    public OSiUp signUpUser(ISiUp in);

    @PUT
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/createchat")
    public OCrCh createChat(@Context HttpHeaders headers, ICrCh in);

    @DELETE
    @Produces(MediaType.APPLICATION_XML)
    @Path("/deletechat")
    public ODeCh deleteChat(@Context HttpHeaders headers,
            @QueryParam(Constants.QP_CHATID) int chatID);

    @PUT
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/addusertochat")
    public OAdUC addUserToChat(@Context HttpHeaders headers, IAdUC in);

    @DELETE
    @Produces(MediaType.APPLICATION_XML)
    @Path("/removeuserfromchat")
    public OReUC removeUserFromChat(@Context HttpHeaders headers,
            @QueryParam(Constants.QP_USERID) int userID,
            @QueryParam(Constants.QP_CHATID) int chatID);

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/listuser")
    public OLiUs listUsers(@Context HttpHeaders headers,
            @QueryParam(Constants.QP_SEARCH) String search);

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/listchat")
    public OLiCh listChats(@Context HttpHeaders headers);

    @PUT
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/sendtextmessage")
    public OSTeM sendTextMessage(@Context HttpHeaders headers, ISTeM in);

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/gettextmessage")
    public OGTeM getTextMessage(@Context HttpHeaders headers,
            @QueryParam(Constants.QP_TEXTMESSAGEID) int textMessageID);

    @PUT
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/insertmessageintochat")
    public OIMIC insertMessageIntoChat(@Context HttpHeaders headers, IIMIC in);

    @DELETE
    @Produces(MediaType.APPLICATION_XML)
    @Path("/deletemessagefromchat")
    public ODMFC deleteMessageFromChat(@Context HttpHeaders headers,
            @QueryParam(Constants.QP_MESSAGEID) int messageID);

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/getmessagefromchat")
    public OFMFC getMessageFromChat(@Context HttpHeaders headers,
            @QueryParam(Constants.QP_CHATID) int chatID,
            @QueryParam(Constants.QP_TIMESTAMP) int timestamp);

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/checknew")
    public OCN checkNew(@Context HttpHeaders headers);

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/setshowtimestamp")
    public OSShT setShowTimeStamp(@Context HttpHeaders headers, ISShT in);

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/getmessageinformation")
    public OGMI getMessageInformation(@Context HttpHeaders headers,
            @QueryParam(Constants.QP_MESSAGEID) List<Integer> messageID);

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/acknowledgemessagedownload")
    public OAckMD acknowledgeMessageDownload(@Context HttpHeaders headers,
            IAckMD in);

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/acknowledgechatdownload")
    public OAckCD acknowledgeChatDownload(@Context HttpHeaders headers,
            IAckCD in);

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/syncuser")
    public OSU syncUser(@Context HttpHeaders headers,
            @QueryParam(Constants.QP_USERID) List<Integer> userID);

    @PUT
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/insertusericon")
    public OIUIc insertUserIcon(@Context HttpHeaders headers, IIUIc in);

    @PUT
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/insertchaticon")
    public OICIc insertChatIcon(@Context HttpHeaders headers, IICIc in);
}