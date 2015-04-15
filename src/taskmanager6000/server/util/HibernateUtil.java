package taskmanager6000.server.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Generic class for start Hibernate config and manipulate with sessions
 */
public final class HibernateUtil {
	
	private static ServiceRegistry serviceRegistry;
	private static final SessionFactory sessionFactory = configureSessionFactory();

	private static SessionFactory configureSessionFactory() throws HibernateException {
	    Configuration configuration = new Configuration();
	    configuration.configure();
	    serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
	    return configuration.buildSessionFactory(serviceRegistry);
	}
	
    public static Session getSession(){
    	return sessionFactory.openSession();
    }
}