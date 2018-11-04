package servlets;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import utility.InnerClass;
import utility.PropertiesReader;
import utility.Response;
import utility.DataBase;
import utility.Encrypt;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Encrypt encPassword;
	private DataBase conn = new DataBase();

    public Login() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			validateLogin(conn.getConnection(), request, response);
		} catch (NoSuchAlgorithmException | SQLException e1) {
			e1.printStackTrace();
		}
	}	
	
	private void validateLogin(Connection connection, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, NoSuchAlgorithmException, SQLException {
		ObjectMapper objMapper = new ObjectMapper();
		PropertiesReader prop = PropertiesReader.getInstance();
		HttpSession session;
		PreparedStatement stat = null;
		String loginQuery = prop.getValue("query_logIn");
		InnerClass innerClass = objMapper.readValue(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), InnerClass.class);
		Response<InnerClass> resp = new Response<>();
        try { 
        	String user_username = innerClass.getUsername();
            String user_password = innerClass.getPassword();
            encPassword = new Encrypt(user_password);
            stat = connection.prepareStatement(loginQuery);
            stat.setString(1, user_username);
            stat.setString(2, encPassword.returnEncrypt());
            ResultSet result = stat.executeQuery();
            if(result.next()) {
            	user_username = innerClass.getUsername();
            	int type_id = result.getInt("type_id");
            	if(checkUserType(type_id)) {
            		System.out.println("You are an Admin");
            		session = request.getSession();
            		session.setAttribute("usr", user_username);
            		session.setAttribute("tusr", "admin");
            		resp.setMessage("Login Successful");
                    resp.setStatus(200);
                    resp.setRedirect("UploadFile.html");
                    resp.setData(innerClass);
            	} else {
            		System.out.println("You are an User");
            		session = request.getSession();
            		session.setAttribute("usr", user_username);
            		session.setAttribute("tusr", "user");
            		resp.setMessage("Login Successful");
                    resp.setStatus(404);
                    resp.setRedirect("UploadFile.html");
                    resp.setData(innerClass);
            	}
            } else {
            	resp.setMessage("Invalid Username or Password");
                resp.setStatus(500);
                resp.setData(innerClass);
            }
            String res = objMapper.writeValueAsString(resp);
            System.out.println(objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp));
            response.getWriter().print(res);
        } catch (IOException e) {
           System.out.println("Error: "+e.getMessage());
        }
	}
	
	public boolean checkUserType(int type_id) {
        boolean isAdmin = false;
        isAdmin = ((type_id == 1) ? true : false);
        return isAdmin;
    }
}
