package servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;

import utility.InnerClass;
import utility.PropertiesReader;
import utility.Response;

@WebServlet("/FileManager")
@MultipartConfig
public class FileManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public FileManager() {
        super();
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		PropertiesReader prop = PropertiesReader.getInstance();
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
			try {
				File newFolder = new File(direction + user_username + "/" + mangaName + "/" + mangaChapter + "/");
				if (!newFolder.exists()) {
	        	    System.out.println("Directory: " + newFolder.getName());
	        	    boolean result = false;
	        	    try{
	        	        newFolder.mkdir();
	        	        result = true;
	        	    } 
	        	    catch(SecurityException se){
	        	        System.out.println("Error Folder Exists but: " + se.getMessage());
	        	    }        
	        	    if(result) {    
	        	        System.out.println("Folder Created");  
	        	    }
	        	}
				output = new FileOutputStream(newFolder + "/" + this.getFileName(file));
				int read = 0;
				byte[] bytes = new byte[2048];
				while ((read = fileContent.read(bytes)) != -1) {
					output.write(bytes, 0, read);
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
	
	private String getFileName(Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
}
