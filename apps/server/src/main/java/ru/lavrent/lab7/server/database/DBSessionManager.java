package ru.lavrent.lab7.server.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.lavrent.lab7.server.dao.CoordinatesDao;
import ru.lavrent.lab7.server.dao.DisciplineDao;
import ru.lavrent.lab7.server.dao.LabWorkDao;
import ru.lavrent.lab7.server.dao.UserDao;

public class DBSessionManager {
  private static String dbUrl;
  private static String dbUser;
  private static String dbPassword;

  private static SessionFactory sessionFactory;

  public static void setCredentials(String dbUrl, String dbUser, String dbPassword) {
    DBSessionManager.dbUrl = dbUrl;
    DBSessionManager.dbUser = dbUser;
    DBSessionManager.dbPassword = dbPassword;
    sessionFactory = buildSessionFactory();
  }

  private static SessionFactory buildSessionFactory() {
    Configuration configuration = new Configuration();
    configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
    configuration.setProperty("hibernate.connection.url", dbUrl);
    configuration.setProperty("hibernate.connection.username", dbUser);
    configuration.setProperty("hibernate.connection.password", dbPassword);
    configuration.setProperty("hibernate.show_sql", "true");
    configuration.setProperty("hibernate.format_sql", "true");
    configuration.setProperty("hibernate.highlight_sql", "true");
    configuration.setProperty("hibernate.hbm2ddl.auto", "update");

    // TODO: load entity classes here
    configuration.addAnnotatedClass(DisciplineDao.class);
    configuration.addAnnotatedClass(CoordinatesDao.class);
    configuration.addAnnotatedClass(LabWorkDao.class);
    configuration.addAnnotatedClass(UserDao.class);

    return configuration.buildSessionFactory();

  }

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public static void shutdown() {
    getSessionFactory().close();
  }
}
