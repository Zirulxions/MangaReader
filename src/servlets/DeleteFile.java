package servlets;

import java.io.IOException;
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

/**
 * Servlet implementation class DeleteFile
 */
@WebServlet("/DeleteFile")
public class DeleteFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
    public DeleteFile() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		PropertiesReader prop = PropertiesReader.getInstance();
		HttpSession session = request.getSession();
		String mangaName = request.getParameter("mangaDelete");
		String mangaChapter = request.getParameter("chapter");
		System.out.println("Manga name: " + mangaName + " and Chapter: " + mangaChapter);
		Response<InnerClass> resp = new Response<>();
		String user_username = (String) session.getAttribute("usr");
		if((mangaName != null) && (mangaChapter != null) && (user_username != null)) {
			
		} else {
			System.out.println("You are not Logged or chapter/manga doesnt exists.");
			resp.setMessage("Something is not good. You need to put Name and Chapter.");
			resp.setStatus(500);
			String res = objMapper.writeValueAsString(resp);
			response.getWriter().print(res);
		}
	}

}
