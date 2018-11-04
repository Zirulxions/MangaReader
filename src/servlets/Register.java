package servlets;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import utility.Response;
import utility.InnerClass;
import utility.DataBase;
import utility.Encrypt;
import utility.PropertiesReader;

@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Encrypt encPassword;
	private DataBase conn = new DataBase();

    public Register() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		execRegister(conn.getConnection(), request, response);
	}
	
	public void execRegister(Connection connection, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ObjectMapper objMapper = new ObjectMapper();
    	Response<InnerClass> resp = new Response<>();
		PropertiesReader prop = PropertiesReader.getInstance();
		InnerClass innerClass = objMapper.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), InnerClass.class);
		String user_password = innerClass.getPassword();
		String user_username = innerClass.getUsername();
		String user_name = innerClass.getName();
		String user_email = innerClass.getEmail();
		//File newFolder = new File(prop.getValue("baseDir")+"/"+user_username);
		File newFolder = new File("C:\\Users\\Javier Delgado\\Documents\\Workspace Eclipse\\MangaReader1.0\\WebContent\\mangaStorage\\"+user_username);
		try {
        	PreparedStatement stat = null;
        	String signupQuery = prop.getValue("query_new");
        	encPassword = new Encrypt(user_password);
        	stat = connection.prepareStatement(signupQuery);
        	stat.setString(1, encPassword.returnEncrypt());
        	stat.setString(2, user_username);
        	stat.setString(3, user_name);
        	stat.setTimestamp(4, getCurrentTimeStamp());
        	stat.setString(5, user_email);
        	stat.executeUpdate();
        	System.out.println("Added to Database");
        	resp.setMessage("Operation Successful");
        	resp.setStatus(200);
        	resp.setData(innerClass);
        	String res = objMapper.writeValueAsString(resp);
        	System.out.println(objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp));
        	response.getWriter().print(res);
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
        } catch (SQLException e) {
        	System.out.println("Error: "+e.getMessage());
        }
	}
	
	private static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}
}

//Numero de la chama de los brazo gitanos: 0412-5800417 Soberanos: 200
