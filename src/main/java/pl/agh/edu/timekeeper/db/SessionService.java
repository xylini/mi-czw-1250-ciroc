package pl.agh.edu.timekeeper.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SessionService {

    private static Session session;

    public static Session openSession(SessionFactory sessionFactory) {
        session = sessionFactory.openSession();
        return session;
    }

    public static void closeCurrentSession() {
        session.close();
    }

    public static Session getCurrentSession() {
        return session;
    }
}
