package taskmanager6000.client.task;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import taskmanager6000.server.entity.TaskDAO;
import taskmanager6000.server.task.GetTaskListBL;

import com.google.gson.Gson;

@WebServlet("/getTasks")
public class GetTaskList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    List<TaskDAO> taskList;
		try {
			taskList = new GetTaskListBL().list(request.getParameterMap());
		    String json = new Gson().toJson(taskList);
		    
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(json);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
	}
}
