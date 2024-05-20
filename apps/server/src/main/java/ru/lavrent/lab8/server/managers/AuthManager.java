
package ru.lavrent.lab8.server.managers;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import ru.lavrent.lab8.common.models.User;
import ru.lavrent.lab8.common.utils.Credentials;
import ru.lavrent.lab8.common.utils.PublicUser;
import ru.lavrent.lab8.server.dao.UserDao;
import ru.lavrent.lab8.server.exceptions.BadRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthManager {
  private SessionFactory sessionFactory;

  public AuthManager(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public PublicUser register(User user) {
    var db = sessionFactory.openSession();
    PublicUser res = null;
    try {
      db.beginTransaction();
      UserDao dao = new UserDao(new User(user.getUsername(), md2(user.getPassword())));
      db.persist(dao);
      db.getTransaction().commit();
      res = dao.toPublicUser();
    } catch (ConstraintViolationException e) {
      db.getTransaction().rollback();
      throw new BadRequest(e.getMessage());
    }
    db.close();
    return res;
  }

  public PublicUser auth(Credentials credentials) {
    if (credentials == null) {
      return null;
    }
    Session db = sessionFactory.openSession();
    UserDao user = this.getUserByUsername(credentials.username);
    db.close();
    if (user == null) {
      return null;
    }
    String hashedPassword = this.md2(credentials.password);
    if (user.toUser().getPassword().equals(hashedPassword)) {
      return new PublicUser(user.getId(), user.getUsername());
    }
    return null;
  }

  public UserDao getUserByUsername(String username) {
    Session db = sessionFactory.openSession();
    var ans = getUserByUsername(username, db);
    db.close();
    return ans;
  }

  public static UserDao getUserByUsername(String username, Session db) {
    CriteriaBuilder criteriaBuilder = db.getCriteriaBuilder();
    CriteriaQuery<UserDao> criteriaQuery = criteriaBuilder.createQuery(UserDao.class);
    Root<UserDao> root = criteriaQuery.from(UserDao.class);

    Predicate predicate = criteriaBuilder.equal(root.get("username"), username);
    criteriaQuery.select(root).where(predicate);
    Query<UserDao> query = db.createQuery(criteriaQuery);
    UserDao res = query.uniqueResult();
    return res;
  }

  private String md2(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD2");
      md.update(input.getBytes());
      byte[] bytes = md.digest();
      StringBuilder sb = new StringBuilder();
      for (byte b : bytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }
}