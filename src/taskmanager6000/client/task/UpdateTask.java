package taskmanager6000.client.task;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import taskmanager6000.server.entity.TaskDAO;
import taskmanager6000.server.task.UpdateTaskBL;

@WebServlet("/updateTask")
public class UpdateTask extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			TaskDAO task = new UpdateTaskBL().updateTask(request.getParameterMap());
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
