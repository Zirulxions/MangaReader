package servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;

import utility.DataBase;
import utility.InnerClass;
import utility.PropertiesReader;
import utility.Response;

@WebServlet("/FileManager")
@MultipartConfig
public class FileManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataBase conn = new DataBase();
	PropertiesReader prop = PropertiesReader.getInstance();
       
    public FileManager() {
        super();
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		HttpSession session = request.getSession();
		String mangaName = request.getParameter("mangaName");
		String mangaChapter = request.getParameter("newChapter");
		System.out.println("Manga name: " + mangaName + " and Chapter: " + mangaChapter);
		Response<InnerClass> resp = new Response<>();
		String user_username = (String) session.getAttribute("usr");
		if((mangaName != null) && (mangaChapter != null) && (user_username != null)) {
			Part file = request.getPart("file");
			InputStream fileContent = file.getInputStream();
			String direction = prop.getValue("baseDir");
			OutputStream output = null;
			//Integer something = new File(direction + session.getAttribute("usr")).listFiles().length;
			//Integer fileValue = (something + 1);
    	    boolean dir1 = false, dir2 = false;
			try {
				File newManga = new File(direction + user_username + "/" + mangaName);
				File newFolder = new File(direction + user_username + "/" + mangaName + "/" + mangaChapter + "/");
				if (!newManga.exists()) {
	        	    System.out.println("Directory: " + newFolder.getName());
	        	    try{
	        	        newManga.mkdir();
	        	        System.out.println("Manga Created.");
	        	        dir1 = true;
	        	        if(!newFolder.exists()) {
		        	        try {
		        	        	newFolder.mkdir();
		        	        	System.out.println("Chapter Created");
		        	        	dir2 = true;
		        	        } catch(SecurityException se) {
		        	        	System.out.println("Error Folder Chapter Exists: ");
		        	        }
	        	        }
	        	    } catch(SecurityException se){
	        	        System.out.println("Error Folder Exists but: " + se.getMessage());
	        	    }
	        	}
				output = new FileOutputStream(newFolder + "/" + this.getFileName(file));
				int read = 0;
				byte[] bytes = new byte[2048];
				while ((read = fileContent.read(bytes)) != -1) {
					output.write(bytes, 0, read);
				}
				if ((dir1 == true) && (dir2 == true)) {
					execInsertDB(conn.getConnection(), request, response);
				}		
				resp.setMessage("Operation Successful, Manga Uploaded");
	        	resp.setStatus(200);
	        	String res = objMapper.writeValueAsString(resp);
	        	response.getWriter().print(res);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fileContent != null) {
					fileContent.close();
				}
				if (output != null) {
					output.close();
				}
			}
		} else {
			System.out.println("You are not Logged or chapter/manga doesnt exists.");
			resp.setMessage("Something is not good.");
			resp.setStatus(500);
			String res = objMapper.writeValueAsString(resp);
        	response.getWriter().print(res);
		}
	}
	
	private void execInsertDB(Connection connection, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		HttpSession session = request.getSession();
		String user_username = (String) session.getAttribute("usr");
		String manga_name = request.getParameter("mangaName");;
		String manga_synop = request.getParameter("mangaSynopsis");
		boolean manga_status = true;
		PreparedStatement stat = null;
		stat = connection.prepareStatement(prop.getValue("query_consultUser"));
		stat.setString(1, user_username);
		ResultSet result = stat.executeQuery();
		Integer user_id = result.getInt("user_id");
		stat = null;
		stat = connection.prepareStatement(prop.getValue("query_insertManga"));
		stat.setInt(1, user_id);
		stat.setString(2, manga_name);
		stat.setString(3, manga_synop);
		stat.setBoolean(4, manga_status);
		stat.setTimestamp(5, getCurrentTimeStamp());
		stat.executeUpdate();
		System.out.println("Added to Database.");
	}

	private String getFileName(Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
	
	private static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}
}
