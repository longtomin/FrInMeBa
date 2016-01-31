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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import de.radiohacks.frinmeba.database.Check;
import de.radiohacks.frinmeba.database.MySqlConnection;
import de.radiohacks.frinmeba.modelshort.IAuth;
import de.radiohacks.frinmeba.modelshort.IGImM;
import de.radiohacks.frinmeba.modelshort.IGImMMD;
import de.radiohacks.frinmeba.modelshort.ISIcM;
import de.radiohacks.frinmeba.modelshort.ISImM;
import de.radiohacks.frinmeba.modelshort.OAuth;
import de.radiohacks.frinmeba.modelshort.OGImM;
import de.radiohacks.frinmeba.modelshort.OGImMMD;
import de.radiohacks.frinmeba.modelshort.OSIcM;
import de.radiohacks.frinmeba.modelshort.OSImM;
import de.radiohacks.frinmeba.util.IImageUtil;

@Path("/image")
public class ImageImpl implements IImageUtil {
    private static final Logger LOGGER = Logger.getLogger(ImageImpl.class.getName());
    
    /**
     * Upload a File
     */

    @Override
    public OSImM uploadImage(String user, String password, String acknowledge,
            InputStream fileInputStream,
            FormDataContentDisposition contentDispositionHeader) {

        OSImM out = new OSImM();
        MySqlConnection mc = new MySqlConnection();
        Connection con = mc.getMySqlConnection();
        User actuser = new User(con);
        Check actcheck = new Check(con);

        if (actcheck.checkValueMust(user)) {
            if (actcheck.checkValueMust(password)) {
                if (actcheck.checkValueMust(acknowledge)) {

                    IAuth inauth = new IAuth();
                    OAuth outauth = new OAuth();
                    inauth.setPW(actuser.base64Decode(password));
                    inauth.setUN(user);

                    ISImM in = new ISImM();
                    in.setPW(actuser.base64Decode(password));
                    in.setUN(actuser.base64Decode(user));

                    /* First check if the User is valid */
                    actuser.authenticate(inauth, outauth);

                    if (outauth.getA().equalsIgnoreCase(
                            Constants.AUTHENTICATE_FALSE)) {
                        out.setET(outauth.getET());
                    } else {
                        if (contentDispositionHeader != null) {
                            if (fileInputStream != null) {
                                if (contentDispositionHeader.getFileName() != null
                                        && !contentDispositionHeader
                                                .getFileName().isEmpty()) {

                                    // Now we save the Image
                                    long currentTime = System
                                            .currentTimeMillis() / 1000L;
                                    String filetime = Objects.toString(
                                            currentTime, null);

                                    String filePath = (new Constants()).getUploadFolderImage() + File.separatorChar
                                            + filetime
                                            + contentDispositionHeader
                                                    .getFileName();

                                    // save the file to the server
                                    saveFile(fileInputStream, filePath);
                                    // Insert the New Image Message into the
                                    // Database an set
                                    // the out
                                    // Information.

                                    HashCode md5 = null;
                                    try {
                                        md5 = Files.hash(new File(filePath),
                                                Hashing.md5());
                                    } catch (IOException e) {
                                        LOGGER.error(e);
                                    }
                                    String md5Hex = md5.toString();

                                    out.setImF(filetime
                                            + contentDispositionHeader
                                                    .getFileName());
                                    in.setImM(filetime
                                            + contentDispositionHeader
                                                    .getFileName());
                                    in.setImMD5(md5Hex);

                                    if (actuser.base64Decode(acknowledge)
                                            .equalsIgnoreCase(md5Hex)) {
                                        actuser.sendImageMessage(in, out);
                                    } else {
                                        out.setET(Constants.UPLOAD_FAILED);
                                    }
                                } else {
                                    out.setET(Constants.NO_IMAGEMESSAGE_GIVEN);
                                }
                            } else {
                                out.setET(Constants.NO_IMAGEMESSAGE_GIVEN);
                            }
                        } else {
                            out.setET(Constants.NO_IMAGEMESSAGE_GIVEN);
                        }
                    }
                } else {
                    if (actcheck.getLastError().equalsIgnoreCase(
                            Constants.NO_CONTENT_GIVEN)) {
                        out.setET(Constants.UPLOAD_FAILED);
                    } else if (actcheck.getLastError().equalsIgnoreCase(
                            Constants.ENCODING_ERROR)) {
                        out.setET(Constants.ENCODING_ERROR);
                    }
                }
            } else {
                if (actcheck.getLastError().equalsIgnoreCase(
                        Constants.NO_CONTENT_GIVEN)) {
                    out.setET(Constants.NO_USERNAME_OR_PASSWORD);
                } else if (actcheck.getLastError().equalsIgnoreCase(
                        Constants.ENCODING_ERROR)) {
                    out.setET(Constants.ENCODING_ERROR);
                }
            }
        } else {
            if (actcheck.getLastError().equalsIgnoreCase(
                    Constants.NO_CONTENT_GIVEN)) {
                out.setET(Constants.NO_USERNAME_OR_PASSWORD);
            } else if (actcheck.getLastError().equalsIgnoreCase(
                    Constants.ENCODING_ERROR)) {
                out.setET(Constants.ENCODING_ERROR);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                out.setET(Constants.DB_ERROR);
                LOGGER.error(e);
            }
        }
        return out;
    }

    /*
     * Download a File with the given Name in the Path
     */

    @Override
    public Response downloadImage(String user, String password, int imageid) {

        MySqlConnection mc = new MySqlConnection();
        Connection con = mc.getMySqlConnection();
        User actuser = new User(con);
        Check actcheck = new Check(con);
        OGImM out = new OGImM();

        if (actcheck.checkValueMust(user)) {
            if (actcheck.checkValueMust(password)) {

                IAuth inauth = new IAuth();
                OAuth outauth = new OAuth();
                inauth.setPW(actuser.base64Decode(password));
                inauth.setUN(user);

                IGImM in = new IGImM();
                in.setUN(actuser.base64Decode(user));
                in.setPW(actuser.base64Decode(password));
                in.setIID(imageid);

                actuser.authenticate(inauth, outauth);

                if (outauth.getA().equalsIgnoreCase(
                        Constants.AUTHENTICATE_FALSE)) {
                    out.setET(outauth.getET());
                } else {
                    if (!actcheck.checkContenMessageID(imageid,
                            Constants.TYP_IMAGE)) {
                        out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
                    } else {
                        actuser.getImageMessages(in, out);

                        final File file = new File(
                                (new Constants()).getUploadFolderImage() + File.separatorChar + out.getIM());
                        if (!file.exists()) {
                            out.setET(Constants.FILE_NOT_FOUND);
                        } else {
                            try {
                                if (con != null) {
                                    con.close();
                                    con = null;
                                }
                                return Response
                                        .ok(FileUtils.readFileToByteArray(file))
                                        .header("Content-Disposition",
                                                "attachment")
                                        .header("filename", out.getIM())
                                        .build();
                            } catch (java.io.IOException ex) {
                                out.setET(Constants.FILE_NOT_FOUND);
                                LOGGER.error(ex);
                            } catch (SQLException e) {
                                out.setET(Constants.DB_ERROR);
                                LOGGER.error(e);
                            }
                        }
                    }
                }
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                out.setET(Constants.DB_ERROR);
                LOGGER.error(e);
            }
        }

        return null;
    }

    @Override
    public OGImMMD getImageMetadata(String user, String password, int imageid) {

        MySqlConnection mc = new MySqlConnection();
        Connection con = mc.getMySqlConnection();
        User actuser = new User(con);
        Check actcheck = new Check(con);
        OGImMMD out = new OGImMMD();

        if (actcheck.checkValueMust(user)) {
            if (actcheck.checkValueMust(password)) {

                IAuth inauth = new IAuth();
                OAuth outauth = new OAuth();
                inauth.setPW(actuser.base64Decode(password));
                inauth.setUN(user);

                IGImMMD in = new IGImMMD();
                in.setUN(actuser.base64Decode(user));
                in.setPW(actuser.base64Decode(password));
                in.setIID(imageid);

                actuser.authenticate(inauth, outauth);

                actuser.authenticate(inauth, outauth);

                if (outauth.getA().equalsIgnoreCase(
                        Constants.AUTHENTICATE_FALSE)) {
                    out.setET(outauth.getET());
                } else {
                    IGImM tmpin = new IGImM();
                    tmpin.setUN(user);
                    tmpin.setPW(password);
                    tmpin.setIID(imageid);
                    OGImM tmpout = new OGImM();

                    if (!actcheck.checkContenMessageID(imageid,
                            Constants.TYP_IMAGE)) {
                        out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
                    } else {
                        actuser.getImageMessages(tmpin, tmpout);

                        final File file = new File(
                                (new Constants()).getUploadFolderImage() + File.separatorChar + tmpout.getIM());
                        if (file.exists()) {
                            out.setIM(tmpout.getIM());
                            out.setIMD5(tmpout.getIMD5());
                            out.setIS(file.length());
                        } else {
                            out.setET(Constants.FILE_NOT_FOUND);
                        }
                    }
                }
            } else {
                if (actcheck.getLastError().equalsIgnoreCase(
                        Constants.NO_CONTENT_GIVEN)) {
                    out.setET(Constants.NO_USERNAME_OR_PASSWORD);
                } else if (actcheck.getLastError().equalsIgnoreCase(
                        Constants.ENCODING_ERROR)) {
                    out.setET(Constants.ENCODING_ERROR);
                }
            }
        } else {
            if (actcheck.getLastError().equalsIgnoreCase(
                    Constants.NO_CONTENT_GIVEN)) {
                out.setET(Constants.NO_USERNAME_OR_PASSWORD);
            } else if (actcheck.getLastError().equalsIgnoreCase(
                    Constants.ENCODING_ERROR)) {
                out.setET(Constants.ENCODING_ERROR);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                out.setET(Constants.DB_ERROR);
                LOGGER.error(e);
            }
        }
        return out;
    }

    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream, String serverLocation) {

        try {
            OutputStream outpuStream = new FileOutputStream(new File(
                    serverLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            outpuStream = new FileOutputStream(new File(serverLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outpuStream.write(bytes, 0, read);
            }
            outpuStream.flush();
            outpuStream.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public OSIcM uploadIcon(String user, String password, String acknowledge,
            InputStream fileInputStream,
            FormDataContentDisposition contentDispositionHeader) {

        OSIcM out = new OSIcM();
        MySqlConnection mc = new MySqlConnection();
        Connection con = mc.getMySqlConnection();
        User actuser = new User(con);
        Check actcheck = new Check(con);

        if (actcheck.checkValueMust(user)) {
            if (actcheck.checkValueMust(password)) {
                if (actcheck.checkValueMust(acknowledge)) {

                    IAuth inauth = new IAuth();
                    OAuth outauth = new OAuth();
                    inauth.setPW(actuser.base64Decode(password));
                    inauth.setUN(user);

                    ISIcM in = new ISIcM();
                    in.setPW(actuser.base64Decode(password));
                    in.setUN(actuser.base64Decode(user));

                    /* First check if the User is valid */
                    actuser.authenticate(inauth, outauth);

                    if (outauth.getA().equalsIgnoreCase(
                            Constants.AUTHENTICATE_FALSE)) {
                        out.setET(outauth.getET());
                    } else {
                        if (contentDispositionHeader != null) {
                            if (fileInputStream != null) {
                                if (contentDispositionHeader.getFileName() != null
                                        && !contentDispositionHeader
                                                .getFileName().isEmpty()) {

                                    // Now we save the Image
                                    long currentTime = System
                                            .currentTimeMillis() / 1000L;
                                    String filetime = Objects.toString(
                                            currentTime, null);

                                    String filePath = (new Constants()).getUploadFolderImage() + File.separatorChar
                                            + filetime
                                            + contentDispositionHeader
                                                    .getFileName();

                                    // save the file to the server
                                    saveFile(fileInputStream, filePath);
                                    // Insert the New Image Message into the
                                    // Database an set
                                    // the out
                                    // Information.

                                    File f = new File(filePath);

                                    BufferedImage img;
                                    int width = 1;
                                    int heigth = 0;
                                    try {
                                        img = ImageIO.read(f);
                                        width = img.getWidth();
                                        heigth = img.getHeight();
                                    } catch (IOException e1) {
                                        LOGGER.error(e1);
                                    }

                                    if (heigth != width) {
                                        out.setET(Constants.NO_QUADRAT_IMAGE);
                                    } else {
                                        HashCode md5 = null;
                                        try {
                                            md5 = Files.hash(
                                                    new File(filePath),
                                                    Hashing.md5());
                                        } catch (IOException e) {
                                            LOGGER.error(e);
                                        }
                                        String md5Hex = md5.toString();

                                        out.setIcF(filetime
                                                + contentDispositionHeader
                                                        .getFileName());
                                        in.setIcM(filetime
                                                + contentDispositionHeader
                                                        .getFileName());
                                        in.setIcMD5(md5Hex);

                                        if (actuser.base64Decode(acknowledge)
                                                .equalsIgnoreCase(md5Hex)) {
                                            actuser.sendIconMessage(in, out);
                                        } else {
                                            out.setET(Constants.UPLOAD_FAILED);
                                        }
                                    }
                                } else {
                                    out.setET(Constants.NO_IMAGEMESSAGE_GIVEN);
                                }
                            } else {
                                out.setET(Constants.NO_IMAGEMESSAGE_GIVEN);
                            }
                        } else {
                            out.setET(Constants.NO_IMAGEMESSAGE_GIVEN);
                        }
                    }
                } else {
                    if (actcheck.getLastError().equalsIgnoreCase(
                            Constants.NO_CONTENT_GIVEN)) {
                        out.setET(Constants.UPLOAD_FAILED);
                    } else if (actcheck.getLastError().equalsIgnoreCase(
                            Constants.ENCODING_ERROR)) {
                        out.setET(Constants.ENCODING_ERROR);
                    }
                }
            } else {
                if (actcheck.getLastError().equalsIgnoreCase(
                        Constants.NO_CONTENT_GIVEN)) {
                    out.setET(Constants.NO_USERNAME_OR_PASSWORD);
                } else if (actcheck.getLastError().equalsIgnoreCase(
                        Constants.ENCODING_ERROR)) {
                    out.setET(Constants.ENCODING_ERROR);
                }
            }
        } else {
            if (actcheck.getLastError().equalsIgnoreCase(
                    Constants.NO_CONTENT_GIVEN)) {
                out.setET(Constants.NO_USERNAME_OR_PASSWORD);
            } else if (actcheck.getLastError().equalsIgnoreCase(
                    Constants.ENCODING_ERROR)) {
                out.setET(Constants.ENCODING_ERROR);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                out.setET(Constants.DB_ERROR);
                LOGGER.error(e);
            }
        }
        return out;
    }
}