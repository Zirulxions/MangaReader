package servlets;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import utility.DataBase;
import utility.PropertiesReader;

@WebServlet("/FilesManager")
public class FilesManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataBase conn = new DataBase();
	PropertiesReader prop = PropertiesReader.getInstance();

    public FilesManager() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		execInsertAndSave(conn.getConnection(), request, response);
	}

	private void execInsertAndSave(Connection connection, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpSession session = request.getSession();
		String user_username = (String) session.getAttribute("usr");
		String chapterNumber = request.getParameter("chapterNumber");
		String chapterTitle = request.getParameter("chapterTitle");
		String chapterPages = request.getParameter("chapterPages");
		System.out.println("Chapter: Number " + chapterNumber + " Title: " + chapterTitle + " Pages: " + chapterPages);
		String baseDir = prop.getValue("baseDir");
		if((user_username != null) && (chapterNumber != null) && (chapterTitle != null) && (chapterPages != null)) {
			Collection<Part> files = request.getParts();
			InputStream filecontent = null;
			OutputStream os = null;
			String direction = baseDir + chapterNumber + "/";
			try {
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
			} catch (Exception e) {
				e.printStackTrace();
			}
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
