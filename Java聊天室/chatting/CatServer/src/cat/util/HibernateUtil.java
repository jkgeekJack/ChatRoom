package cat.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    /**
     * @return 获取会话工厂
     */
    public static SessionFactory getSessionFactory()
    {
        Configuration cfg = new Configuration().configure();
        sessionFactory = cfg.buildSessionFactory();
        return sessionFactory;
    }

    /**
     * @return 获取会话对象
     */
    public static Session getSession()
    {
        return getSessionFactory().openSession();
    }
}