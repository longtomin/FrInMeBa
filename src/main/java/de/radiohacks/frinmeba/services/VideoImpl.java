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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import de.radiohacks.frinmeba.database.Check;
import de.radiohacks.frinmeba.database.MySqlConnection;
import de.radiohacks.frinmeba.modelshort.IAuth;
import de.radiohacks.frinmeba.modelshort.IGViM;
import de.radiohacks.frinmeba.modelshort.IGViMMD;
import de.radiohacks.frinmeba.modelshort.ISViM;
import de.radiohacks.frinmeba.modelshort.OAuth;
import de.radiohacks.frinmeba.modelshort.OGViM;
import de.radiohacks.frinmeba.modelshort.OGViMMD;
import de.radiohacks.frinmeba.modelshort.OSViM;
import de.radiohacks.frinmeba.util.VideoUtil;

@Path("/video")
public class VideoImpl implements VideoUtil {
	/**
	 * Upload a File
	 */

	@Override
	public OSViM uploadVideo(String User, String Password,
			InputStream fileInputStream,
			FormDataContentDisposition contentDispositionHeader) {

		OSViM out = new OSViM();
		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);

				ISViM in = new ISViM();
				in.setPW(actuser.base64Decode(Password));
				in.setUN(actuser.base64Decode(User));

				/* First check if the User is valid */
				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					if (contentDispositionHeader != null) {
						if (fileInputStream != null) {
							if (contentDispositionHeader.getFileName() != null
									&& !contentDispositionHeader.getFileName()
											.isEmpty()) {

								// Now we save the Image
								long currentTime = System.currentTimeMillis() / 1000L;
								String filetime = Objects.toString(currentTime,
										null);

								String filePath = Constants.SERVER_UPLOAD_LOCATION_FOLDER
										+ Constants.SERVER_VIDEO_FOLDER
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
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								String md5Hex = md5.toString();

								out.setVF(filetime
										+ contentDispositionHeader
												.getFileName());
								in.setVM(filetime
										+ contentDispositionHeader
												.getFileName());
								in.setVMD5(md5Hex);

								actuser.sendVideoMessage(in, out);

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return out;
	}

	/*
	 * Download a File with the given Name in the Path
	 */

	@Override
	public Response downloadVideo(String User, String Password, int videoid) {

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OGViM out = new OGViM();

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);

				IGViM in = new IGViM();
				in.setUN(actuser.base64Decode(User));
				in.setPW(actuser.base64Decode(Password));
				in.setVID(videoid);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					if (!actcheck.CheckContenMessageID(videoid,
							Constants.TYP_VIDEO)) {
						out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
					} else {
						actuser.getVideoMessages(in, out);

						final File file = new File(
								Constants.SERVER_UPLOAD_LOCATION_FOLDER
										+ Constants.SERVER_VIDEO_FOLDER
										+ out.getVM());
						if (!file.exists()) {
							// LOGGER.error(ErrorCode.IMAGE_NOT_FOUND, imageId);
							// throw new NotFoundException();
						}
						try {
							/*
							 * return Response
							 * .ok(FileUtils.readFileToByteArray(file)) .header(
							 * "Content-Type, image/jpeg; Content-Disposition" ,
							 * "attachment; filename=" + out.getImageMessage())
							 * .build();
							 */
							if (con != null) {
								con.close();
								con = null;
							}
							return Response
									.ok(FileUtils.readFileToByteArray(file))
									.header("Content-Disposition", "attachment")
									.header("filename", out.getVM()).build();
						} catch (java.io.IOException ex) {
							// LOGGER.error(ErrorCode.IMAGE_READ_ERROR,
							// file.getAbsolutePath(),
							// ex);
							// throw new NotFoundException(ex);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public OGViMMD getvideometadata(String User, String Password, int videoid) {

		MySqlConnection mc = new MySqlConnection();
		Connection con = mc.getMySqlConnection();
		User actuser = new User(con);
		Check actcheck = new Check(con);
		OGViMMD out = new OGViMMD();

		if (actcheck.checkValueMust(User)) {
			if (actcheck.checkValueMust(Password)) {

				IAuth inauth = new IAuth();
				OAuth outauth = new OAuth();
				inauth.setPW(actuser.base64Decode(Password));
				inauth.setUN(User);

				IGViMMD in = new IGViMMD();
				in.setUN(actuser.base64Decode(User));
				in.setPW(actuser.base64Decode(Password));
				in.setVID(videoid);

				actuser.authenticate(inauth, outauth);

				actuser.authenticate(inauth, outauth);

				if (outauth.getA().equalsIgnoreCase(
						Constants.AUTHENTICATE_FALSE)) {
					out.setET(outauth.getET());
				} else {
					IGViM tmpin = new IGViM();
					tmpin.setUN(User);
					tmpin.setPW(Password);
					tmpin.setVID(videoid);
					OGViM tmpout = new OGViM();

					if (!actcheck.CheckContenMessageID(videoid,
							Constants.TYP_VIDEO)) {
						out.setET(Constants.NONE_EXISTING_CONTENT_MESSAGE);
					} else {
						actuser.getVideoMessages(tmpin, tmpout);

						final File file = new File(
								Constants.SERVER_UPLOAD_LOCATION_FOLDER
										+ Constants.SERVER_VIDEO_FOLDER
										+ tmpout.getVM());
						if (file.exists()) {
							out.setVM(tmpout.getVM());
							out.setVMD5(tmpout.getVMD5());
							out.setVS(file.length());
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
				// TODO Auto-generated catch block
				e.printStackTrace();
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

			e.printStackTrace();
		}
	}
}