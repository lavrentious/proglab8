package ru.lavrent.lab8.server.managers;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.lavrent.lab8.common.models.DryLabWork;
import ru.lavrent.lab8.common.models.LabWork;
import ru.lavrent.lab8.common.utils.PublicUser;
import ru.lavrent.lab8.server.dao.CoordinatesDao;
import ru.lavrent.lab8.server.dao.DisciplineDao;
import ru.lavrent.lab8.server.dao.LabWorkDao;
import ru.lavrent.lab8.server.dao.UserDao;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class CollectionManager {
  private TreeSet<LabWork> collection;
  private String type;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private SessionFactory sessionFactory;

  public CollectionManager(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
    collection = new TreeSet<>();
    type = "TreeSet";
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.from(createdAt);
  }

  public long getCollectionSize() {
    return collection.size();
  }

  public String getType() {
    return type;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  private void setUpdatedAt() {
    updatedAt = LocalDateTime.now();
  }

  synchronized public LabWork add(DryLabWork labWork, PublicUser user) {
    var db = sessionFactory.openSession();
    db.beginTransaction();
    UserDao author = AuthManager.getUserByUsername(user.getUsername(), db);
    LabWork lw = new LabWork(labWork, 1, ZonedDateTime.now(), author.getId());
    LabWorkDao dao = new LabWorkDao(lw);
    dao.setAuthor(author);
    db.persist(dao);
    db.getTransaction().commit();
    lw.setId(dao.getId());
    collection.add(lw);
    setUpdatedAt();
    db.close();
    return dao.toLabWork();
  }

  public void loadToMemory() {
    RuntimeManager.logger.config("loading db to memory");
    this.collection.clear();
    var db = sessionFactory.openSession();
    db.beginTransaction();
    CriteriaBuilder cb = db.getCriteriaBuilder();
    CriteriaQuery<LabWorkDao> cq = cb.createQuery(LabWorkDao.class);
    Root<LabWorkDao> rootEntry = cq.from(LabWorkDao.class);
    CriteriaQuery<LabWorkDao> all = cq.select(rootEntry);

    TypedQuery<LabWorkDao> allQuery = db.createQuery(all);
    db.getTransaction().commit();

    var res = allQuery.getResultList();
    var list = res.stream().map((LabWorkDao dao) -> dao.toLabWork()).toList();
    db.close();
    RuntimeManager.logger.info("loaded %d labworks from db to memory".formatted(list.size()));
    this.collection.addAll(list);
  }

  public LabWork getById(long id) {
    for (LabWork labWork : collection) {
      if (labWork.getId() == id) {
        return labWork;
      }
    }
    return null;
  }

  public ArrayList<LabWork> getList() {
    return new ArrayList<>(collection);
  }

  synchronized public LabWork updateById(long id, DryLabWork newLabWork) {
    Session db = sessionFactory.openSession();
    db.beginTransaction();

    LabWorkDao existingLabWork = db.get(LabWorkDao.class, id);
    if (existingLabWork != null) {
      // Update the existingLabWork fields
      existingLabWork.setName(newLabWork.getName());
      existingLabWork.setCoordinates(new CoordinatesDao(newLabWork.getCoordinates()));
      existingLabWork.setMinimalPoint(newLabWork.getMinimalPoint());
      existingLabWork.setDifficulty(newLabWork.getDifficulty());
      existingLabWork.setDiscipline(new DisciplineDao(newLabWork.getDiscipline()));

      db.merge(existingLabWork);
      db.getTransaction().commit();

      // Update the in-memory collection if necessary
      for (LabWork labWork : collection) {
        if (labWork.getId() == id) {
          collection.remove(labWork);
          LabWork updatedLabWork = new LabWork(newLabWork, id, labWork.getCreationDate(), labWork.getAuthorId());
          collection.add(updatedLabWork);
          setUpdatedAt();
          db.close();
          return updatedLabWork;
        }
      }
    }

    db.close();
    return null;
  }

  public void clear(PublicUser user) {
    var db = sessionFactory.openSession();
    UserDao userDao = AuthManager.getUserByUsername(user.getUsername(), db);
    CriteriaBuilder criteriaBuilder = db.getCriteriaBuilder();
    CriteriaDelete<LabWorkDao> deleteQuery = criteriaBuilder.createCriteriaDelete(LabWorkDao.class);
    Root<LabWorkDao> root = deleteQuery.from(LabWorkDao.class);

    deleteQuery.where(criteriaBuilder.equal(root.get("author").get("id"), userDao.getId()));

    db.getTransaction().begin();
    int deletedCount = db.createMutationQuery(deleteQuery).executeUpdate();
    db.getTransaction().commit();
    if (deletedCount > 0) {
      collection.removeIf(lw -> lw.getAuthorId() == userDao.getId());
      setUpdatedAt();
    }
    db.close();
  }

  public boolean removeById(long id) {
    System.out.println("removing id " + id);
    Session db = sessionFactory.openSession();
    db.beginTransaction();

    CriteriaBuilder cb = db.getCriteriaBuilder();
    CriteriaDelete<LabWorkDao> delete = cb.createCriteriaDelete(LabWorkDao.class);
    Root<LabWorkDao> root = delete.from(LabWorkDao.class);
    delete.where(cb.equal(root.get("id"), id));

    int rowsDeleted = db.createMutationQuery(delete).executeUpdate();
    db.getTransaction().commit();

    if (rowsDeleted > 0) {
      Iterator<LabWork> iterator = collection.iterator();
      while (iterator.hasNext()) {
        LabWork labWork = iterator.next();
        if (labWork.getId() == id) {
          iterator.remove();
          setUpdatedAt();
          db.close();
          return true;
        }
      }
    }

    db.close();
    return false;
  }
}
