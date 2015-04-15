package sk.foundation.taskmanager.server.task;

import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import sk.foundation.taskmanager.client.util.InvalidRequestParameterException;
import sk.foundation.taskmanager.client.util.ParameterHelper;
import sk.foundation.taskmanager.server.entity.TaskDAO;
import sk.foundation.taskmanager.server.util.HibernateUtil;

public class AddTaskBL {
	private static final Logger log = Logger.getLogger(AddTaskBL.class);
	
	private static final String PARAM_DESC =       	"desc";
	private static final String PARAM_DUE_DATE =	"dueDate";
	private static final String PARAM_CREATE_DATE=	"createDate";
	
	public TaskDAO addNewTask(Map parameters) throws Exception {
		
		log.info("ADD NEW TASK from parameters: " + parameters);
		
		try {
			ParameterHelper.checkParamPresence(parameters, PARAM_DESC);
			ParameterHelper.checkParamPresence(parameters, PARAM_CREATE_DATE);
			ParameterHelper.checkParamPresence(parameters, PARAM_DUE_DATE);
		} catch (InvalidRequestParameterException e) {
			log.error("Add new task error - invalid request parameter", e);
			throw e;
		}
		
		String desc = 		ParameterHelper.getParameter(parameters, PARAM_DESC);
		String createDate = ParameterHelper.getParameter(parameters, PARAM_CREATE_DATE);
		String dueDate = 	ParameterHelper.getParameter(parameters, PARAM_DUE_DATE);		
		
		Session s = HibernateUtil.getSession();
		Transaction t = null;
		TaskDAO newTask = null;
		try {
			t = s.beginTransaction();

			newTask = new TaskDAO();
			addDefaults(newTask);
			
			newTask.setDesc(desc);
			newTask.setCreateDate(createDate);
			newTask.setDueDate(dueDate.isEmpty()?null:dueDate);
			
			s.persist(newTask);

			t.commit();
		} catch (RuntimeException e) {
			t.rollback();
			log.error("Add new task error - transaction rollback", e);
			throw e;
		} finally {
			s.close();
		}
		log.info("ADD NEW TASK - SUCESSFULL");
		return newTask;
	}
	
	private void addDefaults(TaskDAO task) {
		task.setDeleted(false);
		task.setDone(false);
		task.setResolutionDate(null);
		task.setDueDate(null);
		task.setProject(null);
	}
}
