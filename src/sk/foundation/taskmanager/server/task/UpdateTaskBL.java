package sk.foundation.taskmanager.server.task;

import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;

import sk.foundation.taskmanager.client.util.InvalidRequestParameterException;
import sk.foundation.taskmanager.client.util.ParameterHelper;
import sk.foundation.taskmanager.server.entity.TaskDAO;
import sk.foundation.taskmanager.server.util.HibernateUtil;

public class UpdateTaskBL {
	private static final Logger log = Logger.getLogger(UpdateTaskBL.class);

	private static final String PARAM_ID =				"id";
	private static final String PARAM_DESC =       		"desc";
//	private static final String PARAM_DUE_DATE =		"due_date";
	private static final String PARAM_RESOLUTION_DATE =	"resolution_date";
	private static final String PARAM_DONE =			"done";
	private static final String PARAM_VERSION =			"version";
	
	public TaskDAO updateTask(Map parameters) throws Exception {
		
		log.info("UPDATE TASK from parameters: " + parameters);
		
		try {
			ParameterHelper.checkParamPresence(parameters, PARAM_ID);
			ParameterHelper.checkParamPresence(parameters, PARAM_DESC);
			ParameterHelper.checkParamPresence(parameters, PARAM_VERSION);
//			ParameterHelper.checkParamPresence(parameters, PARAM_DUE_DATE);
		} catch (InvalidRequestParameterException e) {
			log.error("Update task error - invalid request parameter", e);
			throw e;
		}

		Long id 	= Long.valueOf( ParameterHelper.getParameter(parameters, PARAM_ID));
		Long version= Long.valueOf( ParameterHelper.getParameter(parameters, PARAM_VERSION));
		String desc = 				ParameterHelper.getParameter(parameters, PARAM_DESC);
//		String dueDate = 		ParameterHelper.getParameter(parameters, PARAM_DUE_DATE);
		
		TaskDAO updatedTask = null;
		TaskDAO task = null;
		
		Session s = HibernateUtil.getSession();
		Transaction t = null;
		try {
			t = s.beginTransaction();

			task = (TaskDAO)s.get(TaskDAO.class, id);
			s.buildLockRequest(LockOptions.UPGRADE).lock(task);

			//task has been changed meanwhile
			if (version != task.getVersion()) {
				throw new StaleObjectStateException("UPDATE TASK: newer version is available", task);
			}
			
			task.setDesc(desc);
//			task.setDueDate(dueDate);
			
			doneTask(task, parameters);
			
			s.update(task);

			t.commit();
		} catch (StaleObjectStateException e) {
			t.rollback();
			log.error(e.getMessage(), e);
			updatedTask = task;
		} catch (RuntimeException e) {
			t.rollback();
			log.error("UPDATE TASK error - transaction rollback", e);
			throw e;
		} finally {
			s.close();
		}		
		log.info("UPDATE TASK - SUCESSFULL");
		return updatedTask;
	}
	
	private void doneTask(TaskDAO task, Map parameters) throws InvalidRequestParameterException {
		try {
			ParameterHelper.checkParamPresence(parameters, PARAM_RESOLUTION_DATE);
			ParameterHelper.checkParamPresence(parameters, PARAM_DONE);
		} catch (InvalidRequestParameterException e) {
			log.error("UPDATE TASK [DONE/UNDONE] error - invalid request parameter", e);
			throw e;
		}

		String resolutionDate = ParameterHelper.getParameter(parameters, PARAM_RESOLUTION_DATE);
		String done = 			ParameterHelper.getParameter(parameters, PARAM_DONE);

		if (Boolean.parseBoolean(done) && !resolutionDate.isEmpty()) {
			task.setResolutionDate(resolutionDate);
			task.setDone(true);
			log.info("UPDATE TASK [DONE]");
		}
		if (!Boolean.parseBoolean(done) && resolutionDate.isEmpty()) {
			task.setResolutionDate(null);
			task.setDone(false);
			log.info("UPDATE TASK [UNDONE]");
		}

	}

	public TaskDAO deleteTask(Map parameters) throws Exception {
		
		log.info("DELETE TASK from parameters: " + parameters);
		
		try {
			ParameterHelper.checkParamPresence(parameters, PARAM_ID);
			ParameterHelper.checkParamPresence(parameters, PARAM_VERSION);
		} catch (InvalidRequestParameterException e) {
			log.error("Delete task error - invalid request parameter", e);
			throw e;
		}

		Long id 	 = Long.valueOf(ParameterHelper.getParameter(parameters, PARAM_ID));
		Long version = Long.valueOf(ParameterHelper.getParameter(parameters, PARAM_VERSION));

		TaskDAO updatedTask = null;
		TaskDAO task = null;
		
		Session s = HibernateUtil.getSession();
		Transaction t = null;
		try {
			t = s.beginTransaction();

			task = (TaskDAO)s.get(TaskDAO.class, id);
			s.buildLockRequest(LockOptions.UPGRADE).lock(task);
			
			//task has been changed meanwhile
			if (version != task.getVersion()) {
				throw new StaleObjectStateException("DELETE TASK: newer version is available", task);
			}
			
			task.setDeleted(true);
			
			s.update(task);

			t.commit();
		} catch (StaleObjectStateException e) {
			t.rollback();
			log.error(e.getMessage(), e);
			updatedTask = task;
		} catch (RuntimeException e) {
			t.rollback();
			log.error("Delete task error - transaction rollback", e);
			throw e;
		} finally {
			s.close();
		}		
		log.info("DELETE TASK - SUCESSFULL");
		return updatedTask;
	}
}
