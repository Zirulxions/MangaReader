package servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import utility.PropertiesReader;

@WebServlet("/FileManager")
public class FileManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public FileManager() {
        super();
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PropertiesReader prop = PropertiesReader.getInstance();
		HttpSession session = request.getSession();
		String mangaName = request.getParameter("mangaName");
		Part file = request.getPart("file");
		InputStream fileContent = file.getInputStream();
		String direction = prop.getValue("baseDir");
		OutputStream output = null;
		Integer something = new File(direction + session.getAttribute("usr")).listFiles().length;
		Integer fileValue = (something + 1);
		try {
			String user_username = (String) session.getAttribute("usr");
			File newFolder = new File(direction + user_username + mangaName);
			if (!newFolder.exists()) {
        	    System.out.println("Directory: " + newFolder.getName());
        	    boolean result = false;
        	    try{
        	        newFolder.mkdir();
        	        result = true;
        	    } 
        	    catch(SecurityException se){
        	        System.out.println("Error: " + se.getMessage());
        	    }        
        	    if(result) {    
        	        System.out.println("Folder Created");  
        	    }
        	}
			output = new FileOutputStream(direction + "/" + user_username + "/" + fileValue.toString() + ".jpg");
			int read = 0;
			byte[] bytes = new byte[2048];
			while ((read = fileContent.read(bytes)) != -1) {
				output.write(bytes, 0, read);
			}

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
	}
}
