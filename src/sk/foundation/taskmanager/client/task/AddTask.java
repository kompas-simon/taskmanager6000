package sk.foundation.taskmanager.client.task;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sk.foundation.taskmanager.server.entity.TaskDAO;
import sk.foundation.taskmanager.server.task.AddTaskBL;

import com.google.gson.Gson;

@WebServlet("/addTask")
public class AddTask extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			TaskDAO task = new AddTaskBL().addNewTask(request.getParameterMap());
		    String json = new Gson().toJson(task);
		    
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(json);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
	}

}
