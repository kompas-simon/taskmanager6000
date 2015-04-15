package taskmanager6000.server.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;

public class InitHSQLDatabase extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(InitHSQLDatabase.class);

	public void init(ServletConfig config) throws ServletException {
		super.init();
		
		try {
			log.info("Loading JDBC HSQLDB Driver");

			Class.forName("org.hsqldb.jdbcDriver");
		} catch (Exception e) {
			log.error("Failed to load JDBC HSQLDB driver", e);
			return;
		}
		Connection c = null;
		try {
			log.info("Starting Database");

			HsqlProperties p = new HsqlProperties();
			p.setProperty("server.database.0", "file:${catalina.base}/DB/taskManagerDB");
			p.setProperty("server.dbname.0", "taskmanager");
			p.setProperty("server.port", "9001");

			Server server = new Server();
			server.setProperties(p);
			server.setLogWriter(null); // can use custom writer
			server.setErrWriter(null); // can use custom writer
			server.start();

			log.info("Create table TASK");
			c = DriverManager.getConnection("jdbc:hsqldb:file:${catalina.base}/DB/taskManagerDB", "SA",	"");
			
			Statement st = c.createStatement();
			String createTaskTableQuery = "CREATE TABLE task" + "( "
					+ "id INT IDENTITY PRIMARY KEY NOT NULL, "
					+ "create_date DATE NOT NULL, "
					+ "due_date DATE, "
					+ "resolution_date DATE, "
					+ "desc VARCHAR(200) NOT NULL, "
					+ "done  BOOLEAN NOT NULL, "
					+ "deleted BOOLEAN NOT NULL, "
					+ "project_id INT , "
					+ "version INT NOT NULL)";
			st.execute(createTaskTableQuery);
			
			st = c.createStatement();
			String createProjectTableQuery = "CREATE TABLE project" + "( "
					+ "id INT IDENTITY PRIMARY KEY NOT NULL, "
					+ "create_date DATE NOT NULL, "
					+ "due_date DATE, "
					+ "resolution_date DATE, "
					+ "name VARCHAR(200) NOT NULL, "
					+ "done  BOOLEAN NOT NULL, "
					+ "deleted BOOLEAN NOT NULL, "
					+ "version INT NOT NULL)";
			st.execute(createProjectTableQuery);

		} catch (AclFormatException afex) {
			log.error("Creating database error", afex);
			throw new ServletException(afex);
		} catch (IOException ioex) {
			log.error("Creating database error", ioex);
			throw new ServletException(ioex);
		} catch (SQLException sqle) {
			log.info("Schema and table already exists");
		}

		try {
			if (c != null) {
				log.info("Closing Database");
				Statement st = c.createStatement();
				st.execute("SHUTDOWN");
				c.close();
			}
		} catch (SQLException e) {
			log.error("Could not close init connection and database", e);
		}

		log.info("Init Hibernate");
		HibernateUtil.getSession().close();
	}

	public void destroy() {
		try {
			log.info("Closing Database");

			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:${catalina.base}/DB/taskManagerDB", "SA", "");
			Statement st = c.createStatement();
			st.execute("SHUTDOWN");
			c.close();
		} catch (SQLException e) {
			log.error("Closing database error", e);
		}

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				log.info("Deregistering jdbc driver: " + driver);
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
				log.error("Error deregistering driver: " + driver, e);
			}
		}
	}

}
