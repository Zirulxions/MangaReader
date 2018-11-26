package servlets;

import java.io.File;
import java.io.IOException;
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
		execInsertInManga(conn.getConnection(), request, response);
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		execDelete(conn.getConnection(), request, response);
	}
	
	private void execInsertInManga(Connection connection, HttpServletRequest request, HttpServletResponse response) throws IOException {
		ObjectMapper objMapper = new ObjectMapper();
		Response<InnerClass> resp = new Response<>();
		HttpSession session = request.getSession();
		String user_username = (String) session.getAttribute("usr");
		String mangaName = request.getParameter("mangaName");
		String mangaSynopsis = request.getParameter("synopsis");
		Integer mangaGender = Integer.parseInt(request.getParameter("mangaGender"));
		boolean mangaStatus = true;
		String direction = prop.getValue("baseDir");
		if((mangaName != null) && (mangaSynopsis != null) && (user_username != null) && (mangaGender != null)) {
			File newManga = new File(direction + user_username + "/" + mangaName);
			if(!newManga.exists()) {
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
					System.out.println("Creating Manga: " + newManga.getName());
					newManga.mkdir();
					System.out.println("Manga Created.");
					resp.setMessage("Operation Successful, Manga Created!");
					resp.setStatus(200);
					String res = objMapper.writeValueAsString(resp);
					response.getWriter().print(res);
				} catch (SQLException e) {
					System.out.println("Error: " + e.getMessage());
				}
			}
		} else {
			System.out.println("Not Valid Data.");
			resp.setMessage("You have to be logged and fill all the fields!");
			resp.setStatus(500);
			if(user_username == null) {
				resp.setRedirect("Login.html");
			}
			String res = objMapper.writeValueAsString(resp);
			response.getWriter().print(res);
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
		System.out.println("Manga Name: " + mangaName +  " Username: " + user_username);
		if((mangaName != null) && (user_username != null)) {
			String direction = prop.getValue("baseDir");
			File file = new File(direction + user_username + "/" + mangaName);
			if(!file.exists()) {
				System.out.println("This manga does not exists!");
				resp.setMessage("This Manga does not exist!");
	        	resp.setStatus(500);
	        	String res = objMapper.writeValueAsString(resp);
	        	response.getWriter().print(res);
			} else {
				String deleteManga = prop.getValue("query_deleteManga");
				try {
					PreparedStatement stat = null;
					stat = connection.prepareStatement(deleteManga);
					stat.setString(1, mangaName);
					stat.executeUpdate();
					System.out.println("Manga Deleted.");
					FileUtils.deleteDirectory(file);
				} catch (SQLException sql) {
					System.out.println("Error: " + sql.getMessage());
				}
				System.out.println("Folder Deleted.");
				resp.setMessage("Operation Successful, Manga Deleted!");
	        	resp.setStatus(200);
	        	String res = objMapper.writeValueAsString(resp);
	        	response.getWriter().print(res);
			}
		} else {
			System.out.println("Error: Not Logged or Manga doesnt exists.");
			resp.setMessage("Something is not good. You need to put Name and Chapter or just a Name!");
			resp.setStatus(500);
			if(user_username == null) {
				resp.setRedirect("Login.html");
			} else {
				resp.setRedirect("UploadFile.html");
			}
			String res = objMapper.writeValueAsString(resp);
			response.getWriter().print(res);
		}
	}

	private static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}
}