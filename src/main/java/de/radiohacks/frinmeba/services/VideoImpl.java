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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import de.radiohacks.frinmeba.database.Check;
import de.radiohacks.frinmeba.model.jaxb.IGViM;
import de.radiohacks.frinmeba.model.jaxb.IGViMMD;
import de.radiohacks.frinmeba.model.jaxb.ISViM;
import de.radiohacks.frinmeba.model.jaxb.OGViM;
import de.radiohacks.frinmeba.model.jaxb.OGViMMD;
import de.radiohacks.frinmeba.model.jaxb.OSViM;
import de.radiohacks.frinmeba.util.IVideoUtil;

@Path("/video")
public class VideoImpl implements IVideoUtil {
    /**
     * Upload a File
     */
    
    private static final Logger LOGGER = Logger
            .getLogger(VideoImpl.class.getName());
    
    @Override
    public OSViM uploadVideo(HttpHeaders headers, String acknowldge,
            InputStream fileInputStream,
            FormDataContentDisposition contentDispositionHeader) {
        
        OSViM out = new OSViM();
        User actuser = new User(headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check();
        
        if (actcheck.checkValueMust(acknowldge)) {
            
            ISViM in = new ISViM();
            if (contentDispositionHeader != null) {
                if (fileInputStream != null) {
                    if (contentDispositionHeader.getFileName() != null
                            && !contentDispositionHeader.getFileName()
                                    .isEmpty()) {
                        
                        // Now we save the Image
                        long currentTime = System.currentTimeMillis() / 1000L;
                        String filetime = Objects.toString(currentTime, null);
                        
                        String filePath = (new Constants())
                                .getUploadFolderVideo() + File.separatorChar
                                + filetime
                                + contentDispositionHeader.getFileName();
                        
                        // save the file to the server
                        saveFile(fileInputStream, filePath);
                        // Insert the New Image Message into the
                        // Database an set
                        // the out
                        // Information.
                        
                        HashCode md5 = null;
                        try {
                            md5 = Files.hash(new File(filePath), Hashing.md5());
                        } catch (IOException e) {
                            LOGGER.error(e);
                        }
                        String md5Hex = md5.toString();
                        
                        out.setVF(filetime
                                + contentDispositionHeader.getFileName());
                        in.setVM(filetime
                                + contentDispositionHeader.getFileName());
                        in.setVMD5(md5Hex);
                        if (actuser.base64Decode(acknowldge)
                                .equalsIgnoreCase(md5Hex)) {
                            actuser.sendVideoMessage(in, out);
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
        } else {
            if (actcheck.getLastError()
                    .equalsIgnoreCase(Constants.NO_CONTENT_GIVEN)) {
                out.setET(Constants.UPLOAD_FAILED);
            } else if (actcheck.getLastError()
                    .equalsIgnoreCase(Constants.ENCODING_ERROR)) {
                out.setET(Constants.ENCODING_ERROR);
            }
        }
        return out;
    }
    
    /*
     * Download a File with the given Name in the Path
     */
    
    @Override
    public Response downloadVideo(HttpHeaders headers, int videoid) {
        
        User actuser = new User(headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check();
        OGViM out = new OGViM();
        
        IGViM in = new IGViM();
        in.setVID(videoid);
        
        if (!actcheck.checkContenMessageID(videoid, Constants.TYP_VIDEO)) {
            out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
        } else {
            actuser.getVideoMessages(in, out);
            
            final File file = new File((new Constants()).getUploadFolderVideo()
                    + File.separatorChar + out.getVM());
            if (!file.exists()) {
                LOGGER.error("file not found : " + file.getAbsolutePath());
            }
            try {
                return Response.ok(FileUtils.readFileToByteArray(file))
                        .header("Content-Disposition", "attachment")
                        .header("filename", out.getVM()).build();
            } catch (java.io.IOException ex) {
                LOGGER.error(ex);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }
    
    @Override
    public OGViMMD getVideoMetadata(HttpHeaders headers, int videoid) {
        
        User actuser = new User(headers.getHeaderString(Constants.USERNAME));
        Check actcheck = new Check();
        OGViMMD out = new OGViMMD();
        
        IGViMMD in = new IGViMMD();
        in.setVID(videoid);
        
        IGViM tmpin = new IGViM();
        tmpin.setVID(videoid);
        OGViM tmpout = new OGViM();
        
        if (!actcheck.checkContenMessageID(videoid, Constants.TYP_VIDEO)) {
            out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
        } else {
            actuser.getVideoMessages(tmpin, tmpout);
            
            final File file = new File(
                    (new Constants()).getUploadFolderVideo() + tmpout.getVM());
            if (file.exists()) {
                out.setVM(tmpout.getVM());
                out.setVMD5(tmpout.getVMD5());
                out.setVS(file.length());
            } else {
                out.setET(Constants.FILE_NOT_FOUND);
            }
        }
        return out;
    }
    
    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream,
            String serverLocation) {
        
        try {
            OutputStream outpuStream = new FileOutputStream(
                    new File(serverLocation));
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
}
