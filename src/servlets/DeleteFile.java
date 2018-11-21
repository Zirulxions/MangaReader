package servlets;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
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
import utility.InnerDeleteClass;
import utility.PropertiesReader;
import utility.Response;

@WebServlet("/DeleteFile")
@MultipartConfig
public class DeleteFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataBase conn = new DataBase();
 
    public DeleteFile() {
        super();
    }

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		execDelete(conn.getConnection(), request, response);
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
}
	
//	void deleteDirectoryStream(Path path) throws IOException {
//		Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
//	}
	
		/*ObjectMapper objMapper = new ObjectMapper();
		PropertiesReader prop = PropertiesReader.getInstance();
		HttpSession session = request.getSession();
		String mangaName = request.getParameter("mangaDelete");
		String mangaChapter = request.getParameter("chapter");
		String user_username = (String) session.getAttribute("usr");
		System.out.println("Manga name: " + mangaName + " and Chapter: " + mangaChapter + " For username: " + user_username);
		Response<InnerClass> resp = new Response<>();
		if((mangaName != null) && (mangaChapter != null) && (user_username != null)) {
			String direction = prop.getValue("baseDir");
			File file = new File(direction + user_username + "/" + mangaName + "/" + mangaChapter);
			if(!file.exists()) {
				System.out.println("Error, File does not Exists.");
				resp.setMessage("Unable to delete folder, it doesnt exists.");
	        	resp.setStatus(500);
	        	String res = objMapper.writeValueAsString(resp);
	        	response.getWriter().print(res);
			} else {
				file.delete();
				System.out.println("Folder Deleted.");
				resp.setMessage("Operation Successful, Chapter deleted");
	        	resp.setStatus(200);
	        	String res = objMapper.writeValueAsString(resp);
	        	response.getWriter().print(res);
			}
		} else {
			System.out.println("You are not Logged or chapter/manga doesnt exists.");
			resp.setMessage("Something is not good. You need to put Name and Chapter.");
			resp.setStatus(500);
			String res = objMapper.writeValueAsString(resp);
			response.getWriter().print(res);
		}*/