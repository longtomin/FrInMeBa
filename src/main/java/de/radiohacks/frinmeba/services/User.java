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
import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.glassfish.jersey.internal.util.Base64;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import de.radiohacks.frinmeba.database.Check;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbChats;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbContact;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbFile;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbImage;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbLocation;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbMessages;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbText;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUserToChats;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbUsers;
import de.radiohacks.frinmeba.model.hibernate.FrinmeDbVideo;
import de.radiohacks.frinmeba.model.jaxb.C;
import de.radiohacks.frinmeba.model.jaxb.CNM;
import de.radiohacks.frinmeba.model.jaxb.IAckCD;
import de.radiohacks.frinmeba.model.jaxb.IAckMD;
import de.radiohacks.frinmeba.model.jaxb.IAdUC;
import de.radiohacks.frinmeba.model.jaxb.ICrCh;
import de.radiohacks.frinmeba.model.jaxb.IDMFC;
import de.radiohacks.frinmeba.model.jaxb.IDeCh;
import de.radiohacks.frinmeba.model.jaxb.IFMFC;
import de.radiohacks.frinmeba.model.jaxb.IGImM;
import de.radiohacks.frinmeba.model.jaxb.IGMI;
import de.radiohacks.frinmeba.model.jaxb.IGTeM;
import de.radiohacks.frinmeba.model.jaxb.IGViM;
import de.radiohacks.frinmeba.model.jaxb.IICIc;
import de.radiohacks.frinmeba.model.jaxb.IIMIC;
import de.radiohacks.frinmeba.model.jaxb.IIUIc;
import de.radiohacks.frinmeba.model.jaxb.ILiUs;
import de.radiohacks.frinmeba.model.jaxb.IReUC;
import de.radiohacks.frinmeba.model.jaxb.ISIcM;
import de.radiohacks.frinmeba.model.jaxb.ISImM;
import de.radiohacks.frinmeba.model.jaxb.ISShT;
import de.radiohacks.frinmeba.model.jaxb.ISTeM;
import de.radiohacks.frinmeba.model.jaxb.ISU;
import de.radiohacks.frinmeba.model.jaxb.ISViM;
import de.radiohacks.frinmeba.model.jaxb.ISiUp;
import de.radiohacks.frinmeba.model.jaxb.M;
import de.radiohacks.frinmeba.model.jaxb.MI;
import de.radiohacks.frinmeba.model.jaxb.MIB;
import de.radiohacks.frinmeba.model.jaxb.OAckCD;
import de.radiohacks.frinmeba.model.jaxb.OAckMD;
import de.radiohacks.frinmeba.model.jaxb.OAdUC;
import de.radiohacks.frinmeba.model.jaxb.OCN;
import de.radiohacks.frinmeba.model.jaxb.OCrCh;
import de.radiohacks.frinmeba.model.jaxb.ODMFC;
import de.radiohacks.frinmeba.model.jaxb.ODeCh;
import de.radiohacks.frinmeba.model.jaxb.OFMFC;
import de.radiohacks.frinmeba.model.jaxb.OGImM;
import de.radiohacks.frinmeba.model.jaxb.OGMI;
import de.radiohacks.frinmeba.model.jaxb.OGTeM;
import de.radiohacks.frinmeba.model.jaxb.OGViM;
import de.radiohacks.frinmeba.model.jaxb.OICIc;
import de.radiohacks.frinmeba.model.jaxb.OIMIC;
import de.radiohacks.frinmeba.model.jaxb.OIUIc;
import de.radiohacks.frinmeba.model.jaxb.OLiCh;
import de.radiohacks.frinmeba.model.jaxb.OLiUs;
import de.radiohacks.frinmeba.model.jaxb.OReUC;
import de.radiohacks.frinmeba.model.jaxb.OSIcM;
import de.radiohacks.frinmeba.model.jaxb.OSImM;
import de.radiohacks.frinmeba.model.jaxb.OSShT;
import de.radiohacks.frinmeba.model.jaxb.OSTeM;
import de.radiohacks.frinmeba.model.jaxb.OSU;
import de.radiohacks.frinmeba.model.jaxb.OSViM;
import de.radiohacks.frinmeba.model.jaxb.OSiUp;
import de.radiohacks.frinmeba.model.jaxb.OU;
import de.radiohacks.frinmeba.model.jaxb.ShT;
import de.radiohacks.frinmeba.model.jaxb.U;

public class User {
    
    private FrinmeDbUsers ActiveUser;
    private String LastError = new String();
    private Session session = null;
    
    private static final Logger LOGGER = Logger.getLogger(User.class);
    
    public User(String uname) {
        LOGGER.debug("Start User with Connection and Username = " + uname);
        session = HibernateUtil.getSessionFactory().openSession();
        fillUserinfo(base64Decode(uname));
        LOGGER.debug("End User with Connection and Username = " + uname);
    }
    
    public User() {
        LOGGER.debug("Start User with Connection");
        session = HibernateUtil.getSessionFactory().openSession();
        LOGGER.debug("End User with Connection");
    }
    
    public int getID() {
        return ActiveUser.getId();
    }
    
    public String getLastError() {
        return LastError;
    }
    
    public String base64Encode(String token) {
        byte[] encodedBytes = Base64.encode(token.getBytes());
        return new String(encodedBytes);
    }
    
    public String base64Decode(String token) {
        byte[] decodedBytes = Base64.decode(token.getBytes());
        return new String(decodedBytes);
    }
    
    public boolean auth(String user, String pw) {
        LOGGER.debug("Start auth with user = " + user + " and pw = " + pw);
        
        boolean ret = false;
        String QueryUser = "from FrinmeDbUsers where B64Username = '" + user
                + "'";
        Check actcheck = new Check();
        
        if (actcheck.checkValueMust(user)) {
            if (actcheck.checkValueMust(pw)) {
                
                try {
                    // Session session =
                    // HibernateUtil.getSessionFactory().openSession();
                    session.beginTransaction();
                    Query q1 = session.createQuery(QueryUser);
                    List<?> results = q1.list();
                    if (!results.isEmpty()) {
                        FrinmeDbUsers u = (FrinmeDbUsers) results.get(0);
                        String encodedpw = base64Decode(pw);
                        if (u.isActive()) {
                            if (u.getPassword().equals(encodedpw)) {
                                ret = true;
                                u.setAuthenticationTime(
                                        System.currentTimeMillis() / 1000L);
                                session.saveOrUpdate(u);
                            } else {
                                LastError = Constants.WRONG_PASSWORD;
                            }
                        } else {
                            LastError = Constants.USER_NOT_ACTIVE;
                        }
                    } else {
                        LastError = Constants.NONE_EXISTING_USER;
                    }
                    session.getTransaction().commit();
                    // session.close();
                    // HibernateUtil.getSessionFactory().close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LastError = Constants.DB_ERROR;
                }
            } else {
                LastError = actcheck.getLastError();
            }
        } else {
            LastError = actcheck.getLastError();
        }
        LOGGER.debug("End authenticate with returnvalue " + ret);
        return ret;
    }
    
    public void fillUserinfo(String username) {
        LOGGER.debug("Start fillUserinfo with Username = " + username);
        String getid = "from FrinmeDbUsers where Username = " + "'" + username
                + "'";
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q1 = session.createQuery(getid);
            List<?> results = q1.list();
            if (!results.isEmpty()) {
                FrinmeDbUsers u = (FrinmeDbUsers) results.get(0);
                this.ActiveUser = u;
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        LOGGER.debug("End fillUserinfo with id = " + ActiveUser.getId());
    }
    
    public void signUp(ISiUp in, OSiUp out) {
        LOGGER.debug("Start signUp with In = " + in.toString());
        String checkuser = "from FrinmeDbUsers where B64Username = " + "'"
                + in.getUN() + "'";
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q1 = session.createQuery(checkuser);
            List<?> results = q1.list();
            if (results.isEmpty()) {
                FrinmeDbUsers saveU = new FrinmeDbUsers();
                saveU.setEmail(in.getE());
                saveU.setB64username(in.getUN());
                saveU.setPassword(in.getPW());
                saveU.setUsername(base64Decode(in.getUN()));
                saveU.setSignupDate(System.currentTimeMillis() / 1000L);
                session.save(saveU);
                out.setUID(saveU.getId());
                out.setUN(saveU.getUsername());
                out.setR(Constants.SIGNUP_SUCCESSFUL);
                out.setSU("SUCCESSFUL");
                session.getTransaction().commit();
            } else {
                out.setET(Constants.USER_ALREADY_EXISTS);
                FrinmeDbUsers existingUser = (FrinmeDbUsers) results.get(0);
                out.setUID(existingUser.getId());
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception ex) {
            ex.printStackTrace();
            out.setET(Constants.DB_ERROR);
        }
        LOGGER.debug("End signUp with Out = " + out.toString());
    }
    
    public void listUser(ILiUs in, OLiUs out) {
        LOGGER.debug("Start listUser with In = " + in.toString());
        
        String listchat = "from FrinmeDbUsers where Active = 1";
        if (in.getS() != null && !in.getS().isEmpty()) {
            listchat.concat(" and Username like '%" + in.getS() + "%'");
        }
        
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q1 = session.createQuery(listchat);
            List<?> results = q1.list();
            
            for (int i = 0; i < results.size(); i++) {
                FrinmeDbUsers u = (FrinmeDbUsers) results.get(i);
                if (ActiveUser.getId() != u.getId()) {
                    U xmlu = new U();
                    xmlu.setUN(u.getUsername());
                    xmlu.setE(u.getEmail());
                    xmlu.setUID(u.getId());
                    xmlu.setLA(u.getAuthenticationTime());
                    if (u.getFrinmeDbImage() != null) {
                        xmlu.setICID(u.getFrinmeDbImage().getId());
                    }
                    out.getU().add(xmlu);
                }
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception ex) {
            ex.printStackTrace();
            out.setET(Constants.DB_ERROR);
        }
        LOGGER.debug("End listUser with Out = " + out.toString());
    }
    
    public void listChat(OLiCh out) {
        LOGGER.debug("Start listChat");
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q1 = session
                    .createQuery("From FrinmeDbUserToChats where UserID = '"
                            + ActiveUser.getId() + "'");
            List<?> results = q1.list();
            
            if (!results.isEmpty()) {
                for (int i = 0; i < results.size(); i++) {
                    FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) results
                            .get(i);
                    C outchat = new C();
                    outchat.setCID(u2c.getFrinmeDbChats().getId());
                    outchat.setCN(u2c.getFrinmeDbChats().getChatname());
                    if (u2c.getFrinmeDbChats().getFrinmeDbImage() != null) {
                        outchat.setICID(u2c.getFrinmeDbChats()
                                .getFrinmeDbImage().getId());
                    }
                    OU outOwingUser = new OU();
                    outOwingUser.setOUID(
                            u2c.getFrinmeDbChats().getFrinmeDbUsers().getId());
                    outOwingUser.setOUN(u2c.getFrinmeDbChats()
                            .getFrinmeDbUsers().getUsername());
                    outchat.setOU(outOwingUser);
                    out.getC().add(outchat);
                }
            } else {
                out.setET(Constants.NO_ACTIVE_CHATS);
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End listChat");
    }
    
    public void createChat(ICrCh in, OCrCh out, String username) {
        LOGGER.debug("Start createChat with In = " + in.toString());
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            // First get the Owning User for the Chat
            FrinmeDbUsers saveU = new FrinmeDbUsers();
            saveU.setId(ActiveUser.getId());
            // Create new Chat with the Owning User
            FrinmeDbChats saveC = new FrinmeDbChats();
            saveC.setChatname(in.getCN());
            saveC.setFrinmeDbUsers(saveU);
            session.save(saveC);
            // Add the Owning User to the People into the Chat
            FrinmeDbUserToChats u2c = new FrinmeDbUserToChats();
            u2c.setFrinmeDbChats(saveC);
            u2c.setFrinmeDbUsers(saveU);
            session.save(u2c);
            // Get the Chatinformation for response
            out.setCID(saveC.getId());
            out.setCN(saveC.getChatname());
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception ex) {
            ex.printStackTrace();
            out.setET(Constants.DB_ERROR);
        }
        LOGGER.debug("End createChat with Out = " + out.toString());
    }
    
    // Done In & Out
    public void addUserToChat(IAdUC in, OAdUC out) {
        LOGGER.debug("Start addUserToChat with In = " + in.toString());
        
        if (ActiveUser.getId() == in.getUID()) {
            out.setET(Constants.CHAT_OWNER_NOT_ADDED);
        } else {
            String UserAlreadyInChat = "from FrinmeDbUserToChats where ChatID = '"
                    + in.getCID() + "' and UserID = '" + in.getUID() + "'";
            try {
                // Session session =
                // HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                
                /* Find the User Object which should be added to the Chat */
                FrinmeDbUsers UFindUser = session.load(FrinmeDbUsers.class,
                        in.getUID());
                
                /* Check first if Owning User is sening the Request */
                FrinmeDbChats C = session.load(FrinmeDbChats.class,
                        in.getCID());
                if (ActiveUser.getId().equals(C.getFrinmeDbUsers().getId())) {
                    /* The Owning User is adding the new User to the Chat */
                    /* Check first if user is already in the Chat */
                    Query qUserInChat = session.createQuery(UserAlreadyInChat);
                    List<?> resultsAlready = qUserInChat.list();
                    if (!resultsAlready.isEmpty()
                            && resultsAlready.size() > 0) {
                        FrinmeDbUserToChats U2C = (FrinmeDbUserToChats) resultsAlready
                                .get(0);
                        if (U2C.getFrinmeDbUsers().getId() == in.getUID()) {
                            out.setET(Constants.USER_ALREADY_IN_CHAT);
                        }
                    } else {
                        // Now we add the new user to the chat in the DB.
                        FrinmeDbUserToChats NewU2C = new FrinmeDbUserToChats();
                        NewU2C.setFrinmeDbUsers(UFindUser);
                        NewU2C.setFrinmeDbChats(C);
                        session.save(NewU2C);
                        out.setR(Constants.USER_ADDED);
                    }
                    
                } else {
                    // We are not owner of the chat, return ErrorText
                    out.setET(Constants.NOT_CHAT_OWNER);
                }
                session.getTransaction().commit();
                // session.close();
                // HibernateUtil.getSessionFactory().close();
            } catch (
            
            Exception ex) {
                ex.printStackTrace();
                out.setET(Constants.DB_ERROR);
            }
        }
        LOGGER.debug("End addUserToChat with Out = " + out.toString());
    }
    
    public void sendTextMessage(ISTeM in, OSTeM out) {
        LOGGER.debug("Start sendTextMessage with In = " + in.toString());
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            FrinmeDbText t = new FrinmeDbText();
            t.setText(in.getTM());
            session.save(t);
            out.setTID(t.getId());
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End sendTextMessage with Out = " + out.toString());
    }
    
    public void sendImageMessage(ISImM in, OSImM out) {
        LOGGER.debug("Start sendImageMessage with In = " + in.toString());
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            FrinmeDbImage i = new FrinmeDbImage();
            i.setImage(in.getImM());
            i.setMd5sum(in.getImMD5());
            session.save(i);
            out.setImID(i.getId());
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End sendImageMessage with Out = " + out.toString());
    }
    
    public void sendVideoMessage(ISViM in, OSViM out) {
        LOGGER.debug("Start sendVideoMessage with In = " + in.toString());
        try {
            
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            FrinmeDbVideo v = new FrinmeDbVideo();
            v.setVideo(in.getVM());
            v.setMd5sum(in.getVMD5());
            session.save(v);
            out.setVID(v.getId());
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End sendVideoMessage with Out = " + out.toString());
    }
    
    public void removeUserFromChat(IReUC in, OReUC out) {
        LOGGER.debug("Start removeUserFromChat with In = " + in.toString());
        if (ActiveUser.getId() == in.getUID()) {
            out.setET(Constants.CHAT_OWNER_NOT_REMOVED);
        } else {
            try {
                /* Check first if the Owning User is sending the Request */
                // Session session =
                // HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                FrinmeDbChats c = session.load(FrinmeDbChats.class,
                        in.getCID());
                if (ActiveUser.getId().equals(c.getFrinmeDbUsers().getId())) {
                    /* The Owing User is sending the request, proceed */
                    Query q2 = session.createQuery(
                            "FrinmeDbMessages WHERE UsertoChatID in (SELECT ID from UserToChats where UserID = '"
                                    + in.getUID() + "' and ChatiD = '"
                                    + in.getCID() + "'");
                    List<?> r2 = q2.list();
                    
                    if (!r2.isEmpty()) {
                        IDMFC idelete = new IDMFC();
                        ODMFC odelete = new ODMFC();
                        boolean deleteError = false;
                        for (int j = 0; j < r2.size(); j++) {
                            FrinmeDbMessages m = (FrinmeDbMessages) r2.get(j);
                            idelete.setMID(m.getId());
                            deleteMessageFromChat(idelete, odelete);
                            
                            if (odelete.getET() != null
                                    && !odelete.getET().isEmpty()) {
                                deleteError = true;
                            }
                        }
                        if (deleteError == false) {
                            out.setR("REMOVED");
                        }
                    }
                } else {
                    out.setET(Constants.NOT_CHAT_OWNER);
                }
                session.getTransaction().commit();
                // session.close();
                // HibernateUtil.getSessionFactory().close();
            } catch (Exception e) {
                out.setET(Constants.DB_ERROR);
                LOGGER.error(e);
            }
        }
        LOGGER.debug("End removeUserFromChat with Out = " + out.toString());
        
    }
    
    public void insertMessageIntoChat(IIMIC in, OIMIC out) {
        LOGGER.debug("Start insertMessageIntoChat with In = " + in.toString());
        int originMsgID = 0;
        
        try {
            boolean typefound = false;
            long currentTime = System.currentTimeMillis() / 1000L;
            
            // TODO Check if message is already inserted in the Chat,
            // idempotent?
            /* First we search all Users in the given Chat */
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            if (in.getMT().equalsIgnoreCase(Constants.TYP_TEXT)) {
                /* First find the Text to insert */
                typefound = true;
                FrinmeDbText t = session.load(FrinmeDbText.class, in.getMID());
                Query qt1 = session
                        .createQuery("from FrinmeDbUserToChats where ChatID = '"
                                + in.getCID() + "'");
                List<?> rt1 = qt1.list();
                List<FrinmeDbMessages> updMsg = new ArrayList<FrinmeDbMessages>();
                
                if (!rt1.isEmpty()) {
                    for (int i = 0; i < rt1.size(); i++) {
                        FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) rt1
                                .get(i);
                        
                        FrinmeDbMessages m = new FrinmeDbMessages();
                        m.setFrinmeDbUsers(ActiveUser);
                        m.setMessageTyp(Constants.TYP_TEXT);
                        m.setSendTimestamp(currentTime);
                        m.setFrinmeDbUserToChats(u2c);
                        m.setFrinmeDbText(t);
                        session.save(m);
                        if (u2c.getFrinmeDbUsers().getId()
                                .equals(ActiveUser.getId())) {
                            /* Found Origin Message, store ID */
                            m.setOriginMsgId(m.getId());
                            m.setReadTimestamp(currentTime);
                            m.setShowTimestamp(currentTime);
                            originMsgID = m.getId();
                            out.setMID(originMsgID);
                            out.setSdT(currentTime);
                        }
                        updMsg.add(m);
                        session.saveOrUpdate(m);
                    }
                    for (int j = 0; j < updMsg.size(); j++) {
                        FrinmeDbMessages mupd = updMsg.get(j);
                        mupd.setOriginMsgId(originMsgID);
                        session.saveOrUpdate(mupd);
                    }
                }
            }
            if (in.getMT().equalsIgnoreCase(Constants.TYP_IMAGE)) {
                /* First find the Text to insert */
                typefound = true;
                FrinmeDbImage im = session.load(FrinmeDbImage.class,
                        in.getMID());
                
                Query qi1 = session
                        .createQuery("from FrinmeDbUserToChats where ChatID = '"
                                + in.getCID() + "'");
                List<?> ri1 = qi1.list();
                List<FrinmeDbMessages> updMsg = new ArrayList<FrinmeDbMessages>();
                
                if (!ri1.isEmpty()) {
                    for (int i = 0; i < ri1.size(); i++) {
                        FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) ri1
                                .get(i);
                        
                        FrinmeDbMessages m = new FrinmeDbMessages();
                        m.setFrinmeDbUsers(ActiveUser);
                        m.setMessageTyp(Constants.TYP_IMAGE);
                        m.setSendTimestamp(currentTime);
                        m.setFrinmeDbUserToChats(u2c);
                        m.setFrinmeDbImage(im);
                        session.save(m);
                        if (u2c.getFrinmeDbUsers().getId()
                                .equals(ActiveUser.getId())) {
                            /* Found Origin Message, store ID */
                            m.setOriginMsgId(m.getId());
                            m.setReadTimestamp(currentTime);
                            m.setShowTimestamp(currentTime);
                            originMsgID = m.getId();
                            out.setMID(originMsgID);
                            out.setSdT(currentTime);
                        }
                        updMsg.add(m);
                        session.saveOrUpdate(m);
                    }
                    for (int j = 0; j < updMsg.size(); j++) {
                        FrinmeDbMessages mupd = updMsg.get(j);
                        mupd.setOriginMsgId(originMsgID);
                        session.saveOrUpdate(mupd);
                    }
                }
            }
            if (in.getMT().equalsIgnoreCase(Constants.TYP_VIDEO)) {
                /* First find the Text to insert */
                typefound = true;
                
                FrinmeDbVideo v = session.load(FrinmeDbVideo.class,
                        in.getMID());
                
                Query qv1 = session
                        .createQuery("from FrinmeDbUserToChats where ChatID = '"
                                + in.getCID() + "'");
                List<?> rv1 = qv1.list();
                List<FrinmeDbMessages> updMsg = new ArrayList<FrinmeDbMessages>();
                
                if (!rv1.isEmpty()) {
                    for (int i = 0; i < rv1.size(); i++) {
                        FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) rv1
                                .get(i);
                        
                        FrinmeDbMessages m = new FrinmeDbMessages();
                        m.setFrinmeDbUsers(ActiveUser);
                        m.setMessageTyp(Constants.TYP_VIDEO);
                        m.setSendTimestamp(currentTime);
                        m.setFrinmeDbUserToChats(u2c);
                        m.setFrinmeDbVideo(v);
                        session.save(m);
                        if (u2c.getFrinmeDbUsers().getId()
                                .equals(ActiveUser.getId())) {
                            /* Found Origin Message, store ID */
                            m.setOriginMsgId(m.getId());
                            m.setReadTimestamp(currentTime);
                            m.setShowTimestamp(currentTime);
                            originMsgID = m.getId();
                            out.setMID(originMsgID);
                            out.setSdT(currentTime);
                        }
                        updMsg.add(m);
                        session.saveOrUpdate(m);
                    }
                    for (int j = 0; j < updMsg.size(); j++) {
                        FrinmeDbMessages mupd = updMsg.get(j);
                        mupd.setOriginMsgId(originMsgID);
                        session.saveOrUpdate(mupd);
                    }
                }
            }
            if (in.getMT().equalsIgnoreCase(Constants.TYP_CONTACT)) {
                /* First find the Text to insert */
                typefound = true;
                FrinmeDbContact ct = session.load(FrinmeDbContact.class,
                        in.getMID());
                
                Query qc1 = session
                        .createQuery("from FrinmeDbUserToChats where ChatID = '"
                                + in.getCID() + "'");
                List<?> rc1 = qc1.list();
                List<FrinmeDbMessages> updMsg = new ArrayList<FrinmeDbMessages>();
                
                if (!rc1.isEmpty()) {
                    for (int i = 0; i < rc1.size(); i++) {
                        FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) rc1
                                .get(i);
                        
                        FrinmeDbMessages m = new FrinmeDbMessages();
                        m.setFrinmeDbUsers(ActiveUser);
                        m.setMessageTyp(Constants.TYP_CONTACT);
                        m.setSendTimestamp(currentTime);
                        m.setFrinmeDbUserToChats(u2c);
                        m.setFrinmeDbContact(ct);
                        session.save(m);
                        if (u2c.getFrinmeDbUsers().getId()
                                .equals(ActiveUser.getId())) {
                            /* Found Origin Message, store ID */
                            m.setOriginMsgId(m.getId());
                            m.setReadTimestamp(currentTime);
                            m.setShowTimestamp(currentTime);
                            originMsgID = m.getId();
                            out.setMID(originMsgID);
                            out.setSdT(currentTime);
                        }
                        updMsg.add(m);
                        session.saveOrUpdate(m);
                    }
                    for (int j = 0; j < updMsg.size(); j++) {
                        FrinmeDbMessages mupd = updMsg.get(j);
                        mupd.setOriginMsgId(originMsgID);
                        session.saveOrUpdate(mupd);
                    }
                    
                }
            }
            
            if (in.getMT().equalsIgnoreCase(Constants.TYP_FILE)) {
                /* First find the Text to insert */
                typefound = true;
                FrinmeDbFile f = session.load(FrinmeDbFile.class, in.getMID());
                
                Query qf1 = session
                        .createQuery("from FrinmeDbUserToChats where ChatID = '"
                                + in.getCID() + "'");
                List<?> rf1 = qf1.list();
                List<FrinmeDbMessages> updMsg = new ArrayList<FrinmeDbMessages>();
                
                if (!rf1.isEmpty()) {
                    for (int i = 0; i < rf1.size(); i++) {
                        FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) rf1
                                .get(i);
                        
                        FrinmeDbMessages m = new FrinmeDbMessages();
                        m.setFrinmeDbUsers(ActiveUser);
                        m.setMessageTyp(Constants.TYP_FILE);
                        m.setSendTimestamp(currentTime);
                        m.setFrinmeDbUserToChats(u2c);
                        m.setFrinmeDbFile(f);
                        session.save(m);
                        if (u2c.getFrinmeDbUsers().getId()
                                .equals(ActiveUser.getId())) {
                            /* Found Origin Message, store ID */
                            m.setOriginMsgId(m.getId());
                            m.setReadTimestamp(currentTime);
                            m.setShowTimestamp(currentTime);
                            originMsgID = m.getId();
                            out.setMID(originMsgID);
                            out.setSdT(currentTime);
                        }
                        updMsg.add(m);
                        session.saveOrUpdate(m);
                    }
                    for (int j = 0; j < updMsg.size(); j++) {
                        FrinmeDbMessages mupd = updMsg.get(j);
                        mupd.setOriginMsgId(originMsgID);
                        session.saveOrUpdate(mupd);
                    }
                    
                }
            }
            if (in.getMT().equalsIgnoreCase(Constants.TYP_LOCATION)) {
                /* First find the Text to insert */
                typefound = true;
                FrinmeDbLocation l = session.load(FrinmeDbLocation.class,
                        in.getMID());
                Query ql1 = session
                        .createQuery("from FrinmeDbUserToChats where ChatID = '"
                                + in.getCID() + "'");
                List<?> rl1 = ql1.list();
                List<FrinmeDbMessages> updMsg = new ArrayList<FrinmeDbMessages>();
                
                if (!rl1.isEmpty()) {
                    for (int i = 0; i < rl1.size(); i++) {
                        FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) rl1
                                .get(i);
                        
                        FrinmeDbMessages m = new FrinmeDbMessages();
                        m.setFrinmeDbUsers(ActiveUser);
                        m.setMessageTyp(Constants.TYP_LOCATION);
                        m.setSendTimestamp(currentTime);
                        m.setFrinmeDbUserToChats(u2c);
                        m.setFrinmeDbLocation(l);
                        session.save(m);
                        if (u2c.getFrinmeDbUsers().getId()
                                .equals(ActiveUser.getId())) {
                            /* Found Origin Message, store ID */
                            m.setOriginMsgId(m.getId());
                            m.setReadTimestamp(currentTime);
                            m.setShowTimestamp(currentTime);
                            originMsgID = m.getId();
                            out.setMID(originMsgID);
                            out.setSdT(currentTime);
                        }
                        updMsg.add(m);
                        session.saveOrUpdate(m);
                    }
                    for (int j = 0; j < updMsg.size(); j++) {
                        FrinmeDbMessages mupd = updMsg.get(j);
                        mupd.setOriginMsgId(originMsgID);
                        session.saveOrUpdate(mupd);
                    }
                }
            }
            
            if (!typefound) {
                out.setET(Constants.TYPE_NOT_FOUND);
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (
        
        Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End insertMessageIntoChat with Out = " + out.toString());
    }
    
    public void getMessagesFromChat(IFMFC in, OFMFC out) {
        LOGGER.debug("Start getMessagesFromChat with In = " + in.toString());
        
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q1 = session
                    .createQuery("From FrinmeDbUserToChats where UserID = '"
                            + +ActiveUser.getId() + "' and ChatiD = '"
                            + +in.getCID() + "'");
            List<?> r1 = q1.list();
            if (!r1.isEmpty() && r1.size() == 1) {
                FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) r1.get(0);
                Query q2 = session.createQuery(
                        "From FrinmeDbMessages WHERE UsertoChatID = '"
                                + u2c.getId() + "'");
                List<?> r2 = q2.list();
                
                if (!r2.isEmpty()) {
                    for (int i = 0; i < r2.size(); i++) {
                        FrinmeDbMessages m = (FrinmeDbMessages) r2.get(i);
                        
                        M msg = new M();
                        msg.setMID(m.getId());
                        msg.setMT(m.getMessageTyp());
                        msg.setSdT(m.getSendTimestamp());
                        msg.setShT(m.getShowTimestamp());
                        msg.setOMID(m.getOriginMsgId());
                        OU owingu = new OU();
                        owingu.setOUID(m.getFrinmeDbUsers().getId());
                        owingu.setOUN(m.getFrinmeDbUsers().getUsername());
                        msg.setOU(owingu);
                        
                        if (msg.getMT().equalsIgnoreCase(Constants.TYP_TEXT)) {
                            msg.setTMID(m.getFrinmeDbText().getId());
                        } else if (msg.getMT()
                                .equalsIgnoreCase(Constants.TYP_IMAGE)) {
                            msg.setIMID(m.getFrinmeDbImage().getId());
                        } else if (msg.getMT()
                                .equalsIgnoreCase(Constants.TYP_CONTACT)) {
                            msg.setCMID(m.getFrinmeDbContact().getId());
                        } else if (msg.getMT()
                                .equalsIgnoreCase(Constants.TYP_LOCATION)) {
                            msg.setLMID(m.getFrinmeDbLocation().getId());
                        } else if (msg.getMT()
                                .equalsIgnoreCase(Constants.TYP_FILE)) {
                            msg.setFMID(m.getFrinmeDbFile().getId());
                        } else if (msg.getMT()
                                .equalsIgnoreCase(Constants.TYP_VIDEO)) {
                            msg.setVMID(m.getFrinmeDbVideo().getId());
                        }
                        
                        /*
                         * Now set the Read Time Stamp
                         */
                        if (m.getReadTimestamp() == 0) {
                            long readTime = System.currentTimeMillis() / 1000L;
                            m.setReadTimestamp(readTime);
                            msg.setRdT(readTime);
                            session.saveOrUpdate(m);
                        } else {
                            msg.setRdT(m.getReadTimestamp());
                        }
                        out.getM().add(msg);
                    }
                }
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End getMessagesFromChat with out = " + out.toString());
    }
    
    public void getTextMessages(IGTeM in, OGTeM out) {
        LOGGER.debug("Start getTextMessages with In = " + in.toString());
        
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            FrinmeDbText t = session.get(FrinmeDbText.class, in.getTextID());
            if (t != null) {
                out.setTM(t.getText());
            } else {
                out.setET(Constants.NONE_EXISTING_MESSAGE);
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End getTextMessages with Out = " + out.toString());
    }
    
    public void getImageMessages(IGImM in, OGImM out) {
        LOGGER.debug("Start getImageMessages with In = " + in.toString());
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            FrinmeDbImage i = session.get(FrinmeDbImage.class, in.getIID());
            if (i != null) {
                out.setIM(i.getImage());
                out.setIMD5(i.getMd5sum());
            } else {
                out.setET(Constants.NONE_EXISTING_MESSAGE);
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End getImageMessages with Out = " + out.toString());
    }
    
    public void getVideoMessages(IGViM in, OGViM out) {
        LOGGER.debug("Start getImageMessages with In = " + in.toString());
        
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            FrinmeDbVideo v = session.get(FrinmeDbVideo.class, in.getVID());
            
            if (v != null) {
                out.setVM(v.getVideo());
                out.setVMD5(v.getMd5sum());
            } else {
                out.setET(Constants.NONE_EXISTING_MESSAGE);
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End getVideoMessages with Out = " + out.toString());
    }
    
    public void checkNew(OCN out) {
        LOGGER.debug("Start checkNew");
        
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            // First we check for unreaded chats
            Query qi = session
                    .createQuery("FROM FrinmeDbUserToChats WHERE UserID = "
                            + ActiveUser.getId() + " and ReadTimestamp = '0')");
            List<?> ri = qi.list();
            // First we check for unreaded chats
            long readTime = System.currentTimeMillis() / 1000L;
            if (!ri.isEmpty()) {
                for (int i = 0; i < ri.size(); i++) {
                    FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) ri.get(i);
                    
                    C oNC = new C();
                    OU oNCOU = new OU();
                    oNCOU.setOUID(u2c.getFrinmeDbUsers().getId());
                    oNCOU.setOUN(u2c.getFrinmeDbUsers().getUsername());
                    oNC.setOU(oNCOU);
                    oNC.setCID(u2c.getFrinmeDbChats().getId());
                    oNC.setCN(u2c.getFrinmeDbChats().getChatname());
                    oNC.setICID(
                            u2c.getFrinmeDbChats().getFrinmeDbImage().getId());
                    
                    out.getC().add(oNC);
                    u2c.setTempReadTimestamp(readTime);
                    session.saveOrUpdate(u2c);
                }
            }
            // Now we check for unread Messages
            Query qu2c = session
                    .createQuery("FROM FrinmeDbUserToChats where UserID = '"
                            + ActiveUser.getId() + "'");
            @SuppressWarnings("unchecked")
            List<FrinmeDbUserToChats> ru2c = qu2c.list();
            if (!ru2c.isEmpty() && ru2c.size() > 0) {
                for (int k = 0; k < ru2c.size(); k++) {
                    FrinmeDbUserToChats u2c = ru2c.get(k);
                    Query qm = session.createQuery(
                            "FROM FrinmeDbMessages WHERE ReadTimestamp = '0' AND UsertoChatID = '"
                                    + u2c.getId() + "'");
                    @SuppressWarnings("unchecked")
                    List<FrinmeDbMessages> rm = qm.list();
                    if (!rm.isEmpty() && rm.size() > 0) {
                        for (int j = 0; j < rm.size(); j++) {
                            FrinmeDbMessages urm = rm.get(j);
                            CNM oNM = new CNM();
                            oNM.setNOM(rm.size());
                            oNM.setCID(urm.getFrinmeDbUserToChats()
                                    .getFrinmeDbChats().getId());
                            oNM.setCN(urm.getFrinmeDbUserToChats()
                                    .getFrinmeDbChats().getChatname());
                            
                            out.getCNM().add(oNM);
                        }
                    }
                }
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End checkNew");
    }
    
    public void setShowTimeStamp(ISShT in, OSShT out) {
        LOGGER.debug("Start setShowTimeStamp with In = " + in.toString());
        
        long currentTime = System.currentTimeMillis() / 1000L;
        
        // TODO Change to query with given List in Hibernate
        String showUpdate = "UPDATE FrinmeDbMessages SET ShowTimestamp = '"
                + currentTime + "' where ID = '";
        
        for (int i = 0; i < in.getMID().size(); i++) {
            if (i == in.getMID().size() - 1) {
                showUpdate += in.getMID().get(i) + "'";
            } else {
                showUpdate += in.getMID().get(i) + "' OR ID = '";
            }
        }
        
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.createQuery(showUpdate).executeUpdate();
            session.getTransaction().commit();
            // session.close();
            
            for (int j = 0; j < in.getMID().size(); j++) {
                ShT s = new ShT();
                s.setMID(in.getMID().get(j));
                s.setT(currentTime);
                out.getShT().add(s);
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End setShowTimeStamp with Out = " + out.toString());
    }
    
    public void deleteMessageFromChat(IDMFC in, ODMFC out) {
        LOGGER.debug("Start deleteMessageFromChat with In = " + in.toString());
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            FrinmeDbMessages m = session.get(FrinmeDbMessages.class,
                    in.getMID());
            
            session.createQuery("delete from FrinmeDbMessages where ID = :id")
                    .setParameter("id", m.getId()).executeUpdate();
            
            if (m != null && m.getId().equals(in.getMID())) {
                /* We have found the Message */
                if (m.getMessageTyp().equalsIgnoreCase(Constants.TYP_TEXT)) {
                    /* Not used anymore = delete content */
                    deleteContent(Constants.TYP_TEXT,
                            m.getFrinmeDbText().getId());
                } else if (m.getMessageTyp()
                        .equalsIgnoreCase(Constants.TYP_IMAGE)) {
                    /* Not used anymore = delete content */
                    deleteContent(Constants.TYP_IMAGE,
                            m.getFrinmeDbImage().getId());
                } else if (m.getMessageTyp()
                        .equalsIgnoreCase(Constants.TYP_VIDEO)) {
                    /* Not used anymore = delete content */
                    deleteContent(Constants.TYP_VIDEO,
                            m.getFrinmeDbVideo().getId());
                } else if (m.getMessageTyp()
                        .equalsIgnoreCase(Constants.TYP_FILE)) {
                    /* Not used anymore = delete content */
                    deleteContent(Constants.TYP_FILE,
                            m.getFrinmeDbFile().getId());
                } else if (m.getMessageTyp()
                        .equalsIgnoreCase(Constants.TYP_LOCATION)) {
                    /* Not used anymore = delete content */
                    deleteContent(Constants.TYP_LOCATION,
                            m.getFrinmeDbLocation().getId());
                } else if (m.getMessageTyp()
                        .equalsIgnoreCase(Constants.TYP_CONTACT)) {
                    /* Not used anymore = delete content */
                    deleteContent(Constants.TYP_CONTACT,
                            m.getFrinmeDbContact().getId());
                }
                out.setMID(in.getMID());
                if (tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                    tx.commit();
                }
            }
        } catch (
        
        Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End deleteMessageFromChat with Out = " + out.toString());
    }
    
    private void deleteContent(String msgType, int id) {
        LOGGER.debug("Start deleteContent with MSGType = " + msgType + " ID = "
                + String.valueOf(id));
        
        long i = -1;
        
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            if (msgType.equalsIgnoreCase(Constants.TYP_TEXT)) {
                // Check if ID is not more used in the Mesages Table
                i = ((Long) session.createQuery(
                        "select count(*) from FrinmeDbMessages where TextMsgID = "
                                + id)
                        .iterate().next()).intValue();
                if (i == 0) {
                    FrinmeDbText txt = session.get(FrinmeDbText.class, id);
                    if (txt != null && txt.getId().equals(id)) {
                        session.createQuery(
                                "delete from FrinmeDbText where ID = :id")
                                .setParameter("id", id).executeUpdate();
                    }
                }
            } else if (msgType.equalsIgnoreCase(Constants.TYP_IMAGE)) {
                i = ((Long) session.createQuery(
                        "select count(*) from FrinmeDbMessages where ImageMsgID = "
                                + id)
                        .iterate().next()).intValue();
                if (i == 0) {
                    FrinmeDbImage img = session.get(FrinmeDbImage.class, id);
                    if (img != null && img.getId().equals(id)) {
                        File file = new File(
                                (new Constants()).getUploadFolderImage()
                                        + File.separatorChar + img.getImage());
                        if (file.exists()) {
                            file.delete();
                        }
                        session.createQuery(
                                "delete from FrinmeDbImage where ID = :id")
                                .setParameter("id", id).executeUpdate();
                    }
                }
            } else if (msgType.equalsIgnoreCase(Constants.TYP_VIDEO)) {
                i = ((Long) session.createQuery(
                        "select count(*) from FrinmeDbMessages where VideoMsgID = "
                                + id)
                        .iterate().next()).intValue();
                if (i == 0) {
                    FrinmeDbVideo v = session.get(FrinmeDbVideo.class, id);
                    if (v != null && v.getId().equals(id)) {
                        File file = new File(
                                (new Constants()).getUploadFolderVideo()
                                        + File.separatorChar + v.getVideo());
                        if (file.exists()) {
                            file.delete();
                        }
                        session.createQuery(
                                "delete from FrinmeDbVideo where ID = :id")
                                .setParameter("id", id).executeUpdate();
                    }
                }
            } else if (msgType.equalsIgnoreCase(Constants.TYP_LOCATION)) {
                i = ((Long) session.createQuery(
                        "select count(*) from FrinmeDbMessages where LocationMsgID = "
                                + id)
                        .iterate().next()).intValue();
                if (i == 0) {
                    
                    FrinmeDbLocation l = session.get(FrinmeDbLocation.class,
                            id);
                    if (l != null && l.getId().equals(id)) {
                        session.createQuery(
                                "delete from FrinmeDbLocation where ID = :id")
                                .setParameter("id", id).executeUpdate();
                    }
                }
            } else if (msgType.equalsIgnoreCase(Constants.TYP_FILE)) {
                i = ((Long) session.createQuery(
                        "select count(*) from FrinmeDbMessages where FileMsgID = "
                                + id)
                        .iterate().next()).intValue();
                if (i == 0) {
                    
                    FrinmeDbFile f = session.get(FrinmeDbFile.class, id);
                    if (f != null && f.getId().equals(id)) {
                        File file = new File(
                                (new Constants()).getUploadFolderFiles()
                                        + File.separatorChar + f.getFile());
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    session.createQuery(
                            "delete from FrinmeDbFile where ID = :id")
                            .setParameter("id", id).executeUpdate();
                }
            } else if (msgType.equalsIgnoreCase(Constants.TYP_CONTACT)) {
                i = ((Long) session.createQuery(
                        "select count(*) from FrinmeDbMessages where ContactMsgID = "
                                + id)
                        .iterate().next()).intValue();
                if (i == 0) {
                    FrinmeDbContact ct = session.get(FrinmeDbContact.class, id);
                    if (ct != null && ct.getId().equals(id)) {
                        session.createQuery(
                                "delete from FrinmeDbContact where ID = :id")
                                .setParameter("id", id).executeUpdate();
                    }
                }
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            LOGGER.error(e);
        }
        LOGGER.debug("End deleteContent with MSGType = " + msgType + " ID = "
                + String.valueOf(id));
    }
    
    public void acknowledgeMessageDownload(IAckMD in, OAckMD out) {
        
        LOGGER.debug(
                "Start acknowledgeMessageDownload with In = " + in.toString());
        out.setACK(Constants.ACKNOWLEDGE_FALSE);
        
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            
            FrinmeDbMessages m = session.get(FrinmeDbMessages.class,
                    in.getMID());
            
            if (m.getMessageTyp().equals(Constants.TYP_TEXT)) {
                String tmptxt = m.getFrinmeDbText().getText();
                int hashCode = tmptxt.hashCode();
                
                if (hashCode == Integer.valueOf(in.getACK())) {
                    out.setACK(Constants.ACKNOWLEDGE_TRUE);
                    out.setMID(in.getMID());
                    m.setReadTimestamp(m.getTempReadTimestamp());
                    session.saveOrUpdate(m);
                }
            }
            if (m.getMessageTyp().equals(Constants.TYP_IMAGE)) {
                if (in.getACK().equals(m.getFrinmeDbImage().getMd5sum())) {
                    out.setACK(Constants.ACKNOWLEDGE_TRUE);
                    out.setMID(in.getMID());
                    m.setReadTimestamp(m.getTempReadTimestamp());
                    session.saveOrUpdate(m);
                }
            }
            if (m.getMessageTyp().equals(Constants.TYP_VIDEO)) {
                if (in.getACK().equals(m.getFrinmeDbVideo().getMd5sum())) {
                    out.setACK(Constants.ACKNOWLEDGE_TRUE);
                    out.setMID(in.getMID());
                    m.setReadTimestamp(m.getTempReadTimestamp());
                    session.saveOrUpdate(m);
                }
                
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug(
                "End acknowledgeMessageDownload with In = " + in.toString());
    }
    
    public void acknowledgeChatDownload(IAckCD in, OAckCD out) {
        LOGGER.debug(
                "Start acknowledgeChatDownload with In = " + in.toString());
        
        out.setACK(Constants.ACKNOWLEDGE_FALSE);
        
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q1 = session.createQuery(
                    "FROM FrinmeDbUserToChats where ChatID = " + in.getCID()
                            + " and UserID = " + ActiveUser.getId());
            List<?> r1 = q1.list();
            
            if (!r1.isEmpty() && r1.size() == 1) {
                FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) r1.get(0);
                int hashCode = u2c.getFrinmeDbChats().getChatname().hashCode();
                String tmpack = base64Decode(in.getACK());
                
                if (hashCode == Integer.valueOf(tmpack)) {
                    out.setACK(Constants.ACKNOWLEDGE_TRUE);
                    u2c.setReadTimestamp(u2c.getTempReadTimestamp());
                    session.saveOrUpdate(u2c);
                }
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End acknowledgeChatDownload with In = " + in.toString());
    }
    
    public void getMessageInformation(IGMI in, OGMI out) {
        LOGGER.debug("Start getMessageInformation with In = " + in.toString());
        
        try {
            String messageIDQuery = "FROM FrinmeDbMessages msg WHERE msg.id IN (:ids)";
            
            boolean abort = false;
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q1 = session.createQuery(messageIDQuery);
            q1.setParameterList("ids", in.getMID());
            List<?> r1 = q1.list();
            
            if (!r1.isEmpty()) {
                for (int i = 0; i < r1.size(); i++) {
                    // Schleife über die ID's
                    FrinmeDbMessages m1 = (FrinmeDbMessages) r1.get(i);
                    if (ActiveUser.getId().equals(m1.getFrinmeDbUserToChats()
                            .getFrinmeDbUsers().getId())) {
                        MIB msgout = new MIB();
                        msgout.setSD(m1.getSendTimestamp());
                        msgout.setMID(m1.getId());
                        
                        Query q2 = session.createQuery(
                                "from FrinmeDbMessages where OriginMsgID = '"
                                        + m1.getOriginMsgId() + "'");
                        List<?> r2 = q2.list();
                        
                        if (!r2.isEmpty()) {
                            for (int j = 0; j < r2.size(); j++) {
                                // Schleife über die Messages zu einer ID
                                FrinmeDbMessages m2 = (FrinmeDbMessages) r2
                                        .get(j);
                                MI msginfo = new MI();
                                msginfo.setRD(m2.getReadTimestamp());
                                msginfo.setSH(m2.getShowTimestamp());
                                msginfo.setUID(m2.getFrinmeDbUsers().getId());
                                msginfo.setUN(
                                        m2.getFrinmeDbUsers().getUsername());
                                msgout.getMI().add(msginfo);
                            }
                        }
                        out.getMIB().add(msgout);
                    } else {
                        out.setET(Constants.NOT_MESSAGE_OWNER);
                        abort = true;
                        break;
                    }
                    
                    if (abort) {
                        break;
                    }
                }
            }
            if (abort) {
                out.getMIB().clear();
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End getMessageInformation with In = " + in.toString());
    }
    
    public void deleteChat(IDeCh in, ODeCh out) {
        LOGGER.debug("Start deleteChat with In = " + in.toString());
        
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q1 = session
                    .createQuery("FROM FrinmeDbUserToChats WHERE ChatID = "
                            + + +in.getCID());
            List<?> r1 = q1.list();
            if (!r1.isEmpty()) {
                for (int i = 0; i < r1.size(); i++) {
                    FrinmeDbUserToChats u2c = (FrinmeDbUserToChats) r1.get(i);
                    Query q2 = session.createQuery(
                            "FROM FrinmeDbMessages WHERE UserToChatID = '"
                                    + u2c.getId() + "'");
                    List<?> r2 = q2.list();
                    if (!r2.isEmpty()) {
                        IDMFC inmsg = new IDMFC();
                        ODMFC outmsg = new ODMFC();
                        for (int j = 0; j < r1.size(); j++) {
                            FrinmeDbMessages m = (FrinmeDbMessages) r1.get(i);
                            inmsg.setMID(m.getId());
                            deleteMessageFromChat(inmsg, outmsg);
                        }
                    }
                }
            }
            session.createQuery(
                    "DELETE FROM FrinmeDbUserToChats WHERE ChatID = '"
                            + in.getCID() + "'")
                    .executeUpdate();
            session.createQuery("DELETE FROM FrinmeDbChats WHERE ID = '"
                    + in.getCID() + "'").executeUpdate();
            out.setR(Constants.CHAT_DELETED);
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End deleteChat with In = " + in.toString());
    }
    
    public void syncUser(ISU in, OSU out) {
        LOGGER.debug("Start syncuser with In = " + in.toString());
        
        try {
            String userIDQuery = "FROM FrinmeDbUsers WHERE ID IN (:ids)";
            
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q1 = session.createQuery(userIDQuery);
            q1.setParameterList("ids", in.getUID());
            List<?> r1 = q1.list();
            
            if (!r1.isEmpty()) {
                for (int i = 0; i < r1.size(); i++) {
                    FrinmeDbUsers u = (FrinmeDbUsers) r1.get(i);
                    U uinfo = new U();
                    uinfo.setLA(u.getAuthenticationTime());
                    uinfo.setUID(u.getId());
                    uinfo.setUN(u.getUsername());
                    uinfo.setE(u.getEmail());
                    if (u.getFrinmeDbImage() != null) {
                        uinfo.setICID(u.getFrinmeDbImage().getId());
                    }
                    out.getU().add(uinfo);
                }
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End syncuser with In = " + in.toString());
    }
    
    public void sendIconMessage(ISIcM in, OSIcM out) {
        LOGGER.debug("Start sendIconMessage with In = " + in.toString());
        try {
            FrinmeDbImage i = new FrinmeDbImage();
            i.setImage(in.getIcM());
            i.setMd5sum(in.getIcMD5());
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(i);
            out.setIcID(i.getId());
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End sendIconMessage with Out = " + out.toString());
    }
    
    public void insertChatIcon(IICIc in, OICIc out) {
        LOGGER.debug("Start insertChatIcon with In = " + in.toString());
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            
            FrinmeDbChats c = session.get(FrinmeDbChats.class, in.getCID());
            if (c.getId().equals(in.getCID())) {
                if (ActiveUser.getId().equals(c.getFrinmeDbUsers().getId())) {
                    Query q2 = session
                            .createQuery("FROM FrinmeDbImage where ID = '"
                                    + in.getIcID() + "'");
                    List<?> r2 = q2.list();
                    
                    if (!r2.isEmpty() && r2.size() == 1) {
                        FrinmeDbImage i = session.get(FrinmeDbImage.class,
                                in.getIcID());
                        c.setFrinmeDbImage(i);
                        session.saveOrUpdate(c);
                        out.setR(Constants.ICON_ADDED);
                    }
                } else {
                    out.setET(Constants.NOT_CHAT_OWNER);
                }
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End insertChatIcon with Out = " + out.toString());
    }
    
    public void insertUserIcon(IIUIc in, OIUIc out) {
        LOGGER.debug("Start insertUserIcon with In = " + in.toString());
        try {
            // Session session =
            // HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q2 = session.createQuery(
                    "FROM FrinmeDbImage where ID = '" + in.getIcID() + "'");
            List<?> r2 = q2.list();
            
            if (!r2.isEmpty() && r2.size() == 1) {
                FrinmeDbImage i = (FrinmeDbImage) r2.get(0);
                ActiveUser.setFrinmeDbImage(i);
                session.saveOrUpdate(ActiveUser);
                out.setR(Constants.ICON_ADDED);
            }
            session.getTransaction().commit();
            // session.close();
            // HibernateUtil.getSessionFactory().close();
        } catch (Exception e) {
            out.setET(Constants.DB_ERROR);
            LOGGER.error(e);
        }
        LOGGER.debug("End insertUserIcon with Out = " + out.toString());
    }
}