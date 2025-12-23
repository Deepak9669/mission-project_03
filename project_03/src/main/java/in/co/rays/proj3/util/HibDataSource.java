package in.co.rays.proj3.util;

import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate DataSource is provides the object of session factory and session
 * 
 * 
 * @author Deepak Verma
 *
 */
public class HibDataSource {
	private static SessionFactory sessionFactory = null;

	public static SessionFactory getSessionFactory() {

		if (sessionFactory == null) {
            ResourceBundle rb = ResourceBundle.getBundle("in.co.rays.proj3.bundle.system");

            String jdbcUrl = System.getenv("DATABASE_URL");
            if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
                jdbcUrl = rb.getString("url");
            }
            System.out.println("Hibernate using DB URL = " + jdbcUrl);
            
            sessionFactory = new Configuration().configure()
            		.setProperty("hibernate.connection.url", jdbcUrl).buildSessionFactory();		}
		return sessionFactory;
	}

	public static Session getSession() {

		Session session = getSessionFactory().openSession();
		return session;

	}

	public static void closeSession(Session session) {

		if (session != null) {
			session.close();
		}
	}
}