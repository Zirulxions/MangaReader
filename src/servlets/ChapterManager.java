package servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utility.DataBase;
import utility.InnerClass;
import utility.InnerDeleteClass;
import utility.PropertiesReader;
import utility.Response;

@WebServlet("/ChapterManager")
@MultipartConfig
public class ChapterManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataBase conn = new DataBase();
	PropertiesReader prop = PropertiesReader.getInstance();

    public ChapterManager() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 	
    	HttpSession session = request.getSession();
    	String directionMangas = prop.getValue("baseDir") + (String) session.getAttribute("usr") + "/";
    	String baseDel = prop.getValue("baseDel");
    	String baseDirUniq = prop.getValue("baseDirUniq");
    	List<String> listFolders = fileList(directionMangas);
    	Integer randomManga = (int) ((Math.random() * ((listFolders.toArray().length - 1) + 1)) + 0);
    	String Manga = (String) listFolders.toArray()[randomManga];
    	String directionChapters = Manga + "/";
    	List<String> listChapters = fileList(directionChapters);
    	Integer randomChapter = (int) ((Math.random() * ((listChapters.toArray().length - 1) + 1)) + 0);
    	String selected = (String) listChapters.toArray()[randomChapter];
    	String fullyDirection = selected + "/";
    	String opKey = "{";
    	List<String> imageInside = fileList(fullyDirection);
    	response.setContentType("application/json");
    	response.getWriter().print(opKey + '\"' + "arr" + '\"' + ':' + '\"' + imageInside.toString().replace(baseDel, baseDirUniq).replace("\\","\\\\") + '\"' + '}');
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		execInsertChapter(conn.getConnection(), request, response);
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		execDeleteChapter(conn.getConnection(), request, response);
	}

	private void execInsertChapter(Connection connection, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ObjectMapper objMapper = new ObjectMapper();
		Response<InnerClass> resp = new Response<>();
		HttpSession session = request.getSession();
		String user_username = (String) session.getAttribute("usr");
		String cMangaName = request.getParameter("cMangaName");
		Integer chapterNumber = Integer.parseInt(request.getParameter("chapterNumber"));
		String chapterTitle = request.getParameter("chapterTitle");
		Integer chapterPages = Integer.parseInt(request.getParameter("chapterPages"));
		System.out.println("Chapter Number: " + chapterNumber + " Title: " + chapterTitle + " Pages: " + chapterPages);
		String baseDir = prop.getValue("baseDir");
		if((user_username != null) && (chapterNumber != null) && (chapterTitle != null) && (chapterPages != null) && (cMangaName != null)) {
			Collection<Part> files = request.getParts();
			InputStream filecontent = null;
			OutputStream os = null;
			String direction = baseDir + user_username + "/" + cMangaName + "/" + chapterNumber + "/";
			File newChap = new File(baseDir + user_username + "/" + cMangaName + "/" + chapterNumber);
			try {
				if(!newChap.exists()) {
					newChap.mkdir();
					System.out.println("Folder Created... Adding to Database...");
				}
				try {
					PreparedStatement stat = null;
					stat = connection.prepareStatement(prop.getValue("query_consultManga"));
					stat.setString(1, cMangaName);
					ResultSet result = stat.executeQuery();
					if(result.next()) {
						Integer manga_id = result.getInt("manga_id");
						stat = null;
						result = null;
						stat = connection.prepareStatement(prop.getValue("query_insertChapter"));
						stat.setInt(1, manga_id);
						stat.setInt(2, chapterNumber);
						stat.setString(3, chapterTitle);
						stat.setTimestamp(4, getCurrentTimeStamp());
						stat.setString(5, direction);
						stat.setInt(6, chapterPages);
						stat.executeUpdate();
						System.out.println("Chapter Added to Database!");
					}
				} catch (SQLException err) {
					System.out.println("Error: " + err.getMessage());
				}
				for (Part file : files) {
					filecontent = file.getInputStream();
					os = new FileOutputStream(direction + this.getFileName(file));
					int read = 0;
					byte[] bytes = new byte[2048];
					while ((read = filecontent.read(bytes)) != -1) {
						os.write(bytes, 0, read);
					}
					if (filecontent != null) {
						filecontent.close();
					}
					if (os != null) {
						os.close();
					}
				}
				resp.setMessage("Operation Successful, Chapter Created!");
				resp.setStatus(200);
				String res = objMapper.writeValueAsString(resp);
				response.getWriter().print(res);
			} catch (Exception e) {
				e.printStackTrace();
				resp.setMessage("Something is not Good, Unable to Write Files or Chapters!");
				resp.setStatus(500);
				String res = objMapper.writeValueAsString(resp);
				response.getWriter().print(res);
			}
		} else {
			resp.setMessage("Missing Data! Something is Wrong!");
			resp.setStatus(500);
			if(user_username == null) {
				resp.setRedirect("Login.html");
			}
			String res = objMapper.writeValueAsString(resp);
			response.getWriter().print(res);
		}
	}
	
	private void execDeleteChapter(Connection connection, HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		HttpSession session = request.getSession();
		InnerDeleteClass deleteClass = objMapper.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), InnerDeleteClass.class);
		Response<InnerDeleteClass> resp = new Response<>();
		String user_username = (String) session.getAttribute("usr");
		deleteClass.setUsername(user_username);
		Integer chapter = deleteClass.getChapter();
		String mangaName = deleteClass.getMangaDelete();
		PropertiesReader prop = PropertiesReader.getInstance();
		if((mangaName != null) && (user_username != null) && (chapter != null)) {
			String direction = prop.getValue("baseDir");
			File file = new File(direction + user_username + "/" + mangaName + "/" + chapter);
			if(!file.exists()) {
				System.out.println("Unable to Delete this chapter, does not exists!");
				resp.setMessage("This Chapter does not exist!");
	        	resp.setStatus(500);
	        	String res = objMapper.writeValueAsString(resp);
	        	response.getWriter().print(res);
			} else {
				String consultManga = prop.getValue("query_consultManga");
				try {
					System.out.println("Deleting From Database...");
					PreparedStatement stat = null;
					stat = connection.prepareStatement(consultManga);
					stat.setString(1, mangaName);
					ResultSet result = stat.executeQuery();
					if(result.next()) {
						System.out.println("Still Deleting...");
						Integer manga_id = result.getInt("manga_id");
						result = null;
						stat = null;
						System.out.println("Still Deleting... Id Manga: " + manga_id);
						stat = connection.prepareStatement(prop.getValue("query_deleteChapter"));
						stat.setInt(1, manga_id);
						stat.setInt(2, chapter);
						System.out.println("Still Deleting... Executing...");
						stat.executeUpdate();
						System.out.println("Chapter deleted from database! Deleting from disk..");
						FileUtils.deleteDirectory(file);
						System.out.println("Chapter's Folder Deleted!");
						resp.setMessage("Successfull, Chapter Deleted!");
			        	resp.setStatus(200);
			        	String res = objMapper.writeValueAsString(resp);
			        	response.getWriter().print(res);
					}
				} catch (SQLException ex) {
					System.out.println("Error: " + ex.getMessage());
					resp.setMessage("Uh Oh... Something is Wrong...!");
		        	resp.setStatus(500);
		        	String res = objMapper.writeValueAsString(resp);
		        	response.getWriter().print(res);
				}
			}
		} else {
			System.out.println("Error: Not Logged or chapter/manga doesnt exists.");
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
	
	public List<String> fileList(String directory) {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        } catch (IOException ex) {
        	System.out.println("Error: " + ex.getMessage());
        }
        return fileNames;
    }
}










/*
for(Object img : imageInside.toArray()) {
	System.out.println(img);
}
*/