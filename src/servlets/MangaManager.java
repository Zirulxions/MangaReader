package servlets;

import java.io.File;
//import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import utility.DataBase;
import utility.InnerClass;
import utility.InnerDeleteClass;
import utility.PropertiesReader;
import utility.Response;

@WebServlet("/MangaManager")
@MultipartConfig
public class MangaManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataBase conn = new DataBase();
	PropertiesReader prop = PropertiesReader.getInstance();
       
    public MangaManager() {
        super();
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		HttpSession session = request.getSession();
		String user_username = (String) session.getAttribute("usr");
		String mangaName = request.getParameter("mangaName");
		String mangaSynopsis = request.getParameter("synopsis");
		Integer gender = Integer.parseInt(request.getParameter("mangaGender"));
		String direction = prop.getValue("baseDir");
		Response<InnerClass> resp = new Response<>();
		if((mangaName != null) && (mangaSynopsis != null) && (user_username != null) && (gender != null)) {
			File newManga = new File(direction + user_username + "/" + mangaName);
			try {
				if(!newManga.exists()) {
					 System.out.println("Creating Manga: " + newManga.getName());
					 newManga.mkdir();
					 System.out.println("Manga Created.");
					 execInsertInManga(conn.getConnection(), request, response);
					 resp.setMessage("Operation Successful, Manga Created!");
					 resp.setStatus(200);
					 String res = objMapper.writeValueAsString(resp);
					 response.getWriter().print(res);
				}
			} catch (SecurityException ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		} else {
			System.out.println("Not Valid Data.");
			resp.setMessage("You have to be logged and fill all the fields!");
			resp.setStatus(500);
			String res = objMapper.writeValueAsString(resp);
			response.getWriter().print(res);
		}
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		execDelete(conn.getConnection(), request, response);
	}
	
	
		
		/*ObjectMapper objMapper = new ObjectMapper();
		HttpSession session = request.getSession();
		String mangaName = request.getParameter("mangaName");
		String mangaChapter = request.getParameter("newChapter");
		String mangaGender = request.getParameter("mangaGender");
		String mangaSynopsis = request.getParameter("mangaSynopsis");
		System.out.println("Manga name: " + mangaName + " and Chapter: " + mangaChapter);
		Response<InnerClass> resp = new Response<>();
		String user_username = (String) session.getAttribute("usr");
		if((mangaName != null) && (mangaChapter != null) && (user_username != null) && (mangaGender != null) && (mangaSynopsis != null)) {
			Part file = request.getPart("file");
			InputStream fileContent = file.getInputStream();
			String direction = prop.getValue("baseDir");
			OutputStream output = null;
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
			System.out.println("Invalid User or sended null data.");
			resp.setMessage("All fields must be filled, or You are not Logged.");
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
		Integer manga_gender = Integer.parseInt(request.getParameter("mangaGender"));
		boolean manga_status = true;
		PreparedStatement stat = null;
		stat = connection.prepareStatement(prop.getValue("query_consultUser"));
		stat.setString(1, user_username);
		ResultSet result = stat.executeQuery();
		Integer user_id = 0;
		if(result.next()) {
			user_id = result.getInt("user_id");
			System.out.println("my id: " + user_id);
			stat = null;
			stat = connection.prepareStatement(prop.getValue("query_insertManga"));
			stat.setInt(1, user_id);
			stat.setString(2, manga_name);
			stat.setString(3, manga_synop);
			stat.setBoolean(4, manga_status);
			stat.setTimestamp(5, getCurrentTimeStamp());
			stat.executeUpdate();
			result = null;
			stat = null;
			System.out.println("Manga added to Database. Adding Genre...");
			stat = connection.prepareStatement(prop.getValue("query_consultManga"));
			stat.setString(1, manga_name);
			result = stat.executeQuery();
			Integer manga_id = 0;
			if(result.next()) {
				manga_id = result.getInt("manga_id");
				System.out.println("Manga Id: " + manga_id);
				stat = null;
				result = null;
				stat = connection.prepareStatement(prop.getValue("query_insertGenre"));
				stat.setInt(1, manga_gender);
				stat.setInt(2, manga_id);
				stat.executeUpdate();
				System.out.println("Added Genre. Creating Chapter...");
				stat = null;
				result = null;
				
			}
		} else {
			System.out.println("Already Exists.");
		}*/

	private void execInsertInManga(Connection connection, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String user_username = (String) session.getAttribute("usr");
		String mangaName = request.getParameter("mangaName");
		String mangaSynopsis = request.getParameter("synopsis");
		Integer mangaGender = Integer.parseInt(request.getParameter("mangaGender"));
		boolean mangaStatus = true;
		try {
			PreparedStatement stat = null;
			stat = connection.prepareStatement(prop.getValue("query_consultUser"));
			stat.setString(1, user_username);
			ResultSet result = stat.executeQuery();
			Integer user_id = 0;
			if(result.next()) {
				user_id = result.getInt("user_id");
				System.out.println("my id: " + user_id);
				stat = null;
				stat = connection.prepareStatement(prop.getValue("query_insertManga"));
				stat.setInt(1, user_id);
				stat.setString(2, mangaName);
				stat.setString(3, mangaSynopsis);
				stat.setBoolean(4, mangaStatus);
				stat.setTimestamp(5, getCurrentTimeStamp());
				stat.executeUpdate();
				result = null;
				stat = null;
				System.out.println("Manga added to Database. Adding Genre...");
				stat = connection.prepareStatement(prop.getValue("query_consultManga"));
				stat.setString(1, mangaName);
				result = stat.executeQuery();
				Integer manga_id = 0;
				if(result.next()) {
					manga_id = result.getInt("manga_id");
					System.out.println("Manga Id: " + manga_id);
					stat = null;
					result = null;
					stat = connection.prepareStatement(prop.getValue("query_insertGenre"));
					stat.setInt(1, mangaGender);
					stat.setInt(2, manga_id);
					stat.executeUpdate();
					System.out.println("Added Genre. Finished!");
					stat = null;
					result = null;
				}
			}
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public void execDelete(Connection connection, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ObjectMapper objMapper = new ObjectMapper();
		HttpSession session = request.getSession();
    	Response<InnerDeleteClass> resp = new Response<>();
		PropertiesReader prop = PropertiesReader.getInstance();
		String user_username = (String) session.getAttribute("usr");
		InnerDeleteClass deleteClass = objMapper.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), InnerDeleteClass.class);
		deleteClass.setUsername(user_username);
		String mangaName = deleteClass.getMangaDelete();
		String chapter = deleteClass.getChapter();
		if((mangaName != null) && (user_username != null)) {
			String direction = prop.getValue("baseDir");
			if(chapter != null) {
				File file = new File(direction + user_username + "/" + mangaName + "/" + chapter);
				if(!file.exists()) {
					System.out.println("Error, File does not Exists.");
					resp.setMessage("This Chapter does not exist!");
		        	resp.setStatus(500);
		        	String res = objMapper.writeValueAsString(resp);
		        	response.getWriter().print(res);
				} else {
					FileUtils.deleteDirectory(file);
					System.out.println("Folder Deleted.");
					resp.setMessage("Operation Successful, Chapter deleted");
		        	resp.setStatus(200);
		        	String res = objMapper.writeValueAsString(resp);
		        	response.getWriter().print(res);
				}
			} else {
				File file = new File(direction + user_username + "/" + mangaName);
				if(!file.exists()) {
					System.out.println("Error, File does not Exists.");
					resp.setMessage("Unable to Delete. this manga does not Exist!");
		        	resp.setStatus(500);
		        	String res = objMapper.writeValueAsString(resp);
		        	response.getWriter().print(res);
				} else {
					FileUtils.deleteDirectory(file);
					System.out.println("Folder Deleted.");
					resp.setMessage("Operation Successful, Chapter deleted");
		        	resp.setStatus(200);
		        	String res = objMapper.writeValueAsString(resp);
		        	response.getWriter().print(res);
				}
			}
		} else {
			System.out.println("Error: Not Logged or chapter/manga doesnt exists.");
			resp.setMessage("Something is not good. You need to put Name and Chapter or just a Name redirecting to Login Page.");
			resp.setStatus(500);
			resp.setRedirect("Login.html");
			String res = objMapper.writeValueAsString(resp);
			response.getWriter().print(res);
		}
	}

	/*private String getFileName(Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}*/
	
	private static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}
}