package taskmanager6000.server.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import taskmanager6000.client.util.InvalidRequestParameterException;
import taskmanager6000.client.util.ParameterHelper;
import taskmanager6000.server.entity.TaskDAO;
import taskmanager6000.server.util.HibernateUtil;

public class GetTaskListBL {
	private static final Logger log = Logger.getLogger(GetTaskListBL.class);

	private static final String PARAM_TASKTYPE =   		"task-type";
	private static final String PARAM_DESC =       		"desc";
	private static final String PARAM_DATEOPTION_SUFIX= "-select-options";
	
	private static final String CREATE_DATE =   	"createDate";
	private static final String DUE_DATE =      	"dueDate";
	private static final String RESOLUTION_DATE =	"resolutionDate";
	
	private static final String DATETYPE_FROM = "-from";
	private static final String DATETYPE_TO =   "-to";
	
	public enum TaskType {
		TODO, DONE, TRASH, ALL
	}
	
	public List list(Map parameters) throws Exception {
		List<TaskDAO> taskList = null;
		
		log.info("GET TASK LIST from parameters: " + parameters);
		
		Session s = HibernateUtil.getSession();
		Transaction t = null;
		try {
			t = s.beginTransaction();

			Criteria c = s.createCriteria(TaskDAO.class);
			c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			addTaskTypeCriteria(c, parameters);
			addDateCriteria(c, parameters, CREATE_DATE);
			addDateCriteria(c, parameters, DUE_DATE);
			addDateCriteria(c, parameters, RESOLUTION_DATE);
			addDescCriteria(c, parameters);
			
			taskList = c.list();

			t.commit(); // Flush happens automatically
		} catch (RuntimeException e) {
			log.error("Get task list error - transaction rollback", e);
			t.rollback();
			throw e;
		} catch (InvalidRequestParameterException e) {
			log.error("Get task list error - invalid request parameter", e);
			throw e;
		} finally {
			s.close();
		}
		log.info("GET TASK LIST - SUCESSFULL");
		return taskList;
	}

	public Map getListCount(Map parameters) throws Exception {
		Map<String, Object> taskTypeCounts = new HashMap<String, Object>();
		
		log.info("GET TASK LIST COUNTS from parameters: " + parameters);
		
		Session s = HibernateUtil.getSession();
		Transaction t = null;
		try {
			t = s.beginTransaction();
			
			Criteria c = null;
			for (TaskType type : TaskType.values()) {
				c = s.createCriteria(TaskDAO.class);
				c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				addTaskTypeCriteria(c, type);
				taskTypeCounts.put(type.name(), c.setProjection(Projections.rowCount()).uniqueResult());
			}

			t.commit(); // Flush happens automatically
		} catch (RuntimeException e) {
			log.error("Get task list error - transaction rollback", e);
			t.rollback();
			throw e;
		} catch (InvalidRequestParameterException e) {
			log.error("Get task list error - invalid request parameter", e);
			throw e;
		} finally {
			s.close();
		}
		log.info("GET TASK LIST COUNTS - SUCESSFULL");
		return taskTypeCounts;
	}
	
	private void addTaskTypeCriteria(Criteria c, TaskType taskType) throws InvalidRequestParameterException {
		switch (taskType) {
			case TODO:
				c.add(Restrictions.eq("done", false));
				c.add(Restrictions.eq("deleted", false));
				break;
			case DONE:
				c.add(Restrictions.eq("done", true));
				c.add(Restrictions.eq("deleted", false));
				break;
			case ALL:
				// ALL = not deleted
				c.add(Restrictions.eq("deleted", false));
				break;
			case TRASH:
				c.add(Restrictions.eq("deleted", true));
				break;
			default:
				throw new InvalidRequestParameterException(taskType.name());
		}		
	}
	
	private void addTaskTypeCriteria(Criteria c, Map parameters) throws InvalidRequestParameterException {
		ParameterHelper.checkParamPresence(parameters, PARAM_TASKTYPE);
		
		TaskType taskType = TaskType.valueOf(ParameterHelper.getParameter(parameters, PARAM_TASKTYPE));
		log.info("Filter " + PARAM_TASKTYPE + " is set to: " + taskType);
		addTaskTypeCriteria(c, taskType);
	}

	private void addDateCriteria(Criteria c, Map parameters, String dateType) throws InvalidRequestParameterException {
		ParameterHelper.checkParamPresence(parameters, dateType + PARAM_DATEOPTION_SUFIX);
		ParameterHelper.checkParamPresence(parameters, dateType + DATETYPE_FROM);
		ParameterHelper.checkParamPresence(parameters, dateType + DATETYPE_TO);

		String dateFrom = ParameterHelper.getParameter(parameters, dateType + DATETYPE_FROM);
		String dateTo = ParameterHelper.getParameter(parameters, dateType + DATETYPE_TO);
		if ( dateFrom.isEmpty() && dateTo.isEmpty()) {
			log.info("Filter " + dateType + " was not set");
			return;
		}
		
		String dateOption = ((String[]) parameters.get(dateType + PARAM_DATEOPTION_SUFIX))[0];
		log.info("Filter " + dateType + " is set to: " + dateOption);
		
		switch (dateOption) {
		case "> <":
			c.add(Restrictions.le(dateType, dateTo));
		case ">":
			c.add(Restrictions.ge(dateType, dateFrom)); 
			break;
		case "<":
			c.add(Restrictions.le(dateType, dateFrom)); 
			break;
		case "=":
			c.add(Restrictions.eq(dateType, dateFrom)); 
			break;
		default:
			throw new InvalidRequestParameterException(dateType + PARAM_DATEOPTION_SUFIX);
		}
	}
	
	private void addDescCriteria(Criteria c, Map parameters) throws InvalidRequestParameterException {
		ParameterHelper.checkParamPresence(parameters, PARAM_DESC);
		String searchString = ((String[]) parameters.get(PARAM_DESC))[0];
		if (searchString != null && !searchString.isEmpty()) {
			log.info("Filter " + PARAM_DESC + " is set to: " + searchString);
			c.add(Restrictions.ilike("desc", searchString, MatchMode.ANYWHERE));
		} else {
			log.info("Filter " + PARAM_DESC + " was not set");
		}
	}
}
