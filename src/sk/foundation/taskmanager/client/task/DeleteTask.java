package sk.foundation.taskmanager.client.task;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sk.foundation.taskmanager.server.entity.TaskDAO;
import sk.foundation.taskmanager.server.task.UpdateTaskBL;

import com.google.gson.Gson;

@WebServlet("/deleteTask")
public class DeleteTask extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			TaskDAO task = new UpdateTaskBL().deleteTask(request.getParameterMap());
		    if (task != null) {
			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().write(new Gson().toJson(task));
		    }
		} catch (Exception e1) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e1.getMessage());
		}
	}

}
