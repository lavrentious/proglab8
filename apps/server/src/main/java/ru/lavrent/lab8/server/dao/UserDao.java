package ru.lavrent.lab8.server.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ru.lavrent.lab8.common.models.User;
import ru.lavrent.lab8.common.utils.PublicUser;

@Entity
@Table(name = "users")
public class UserDao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column
  private String password;

  public UserDao(User user) {
    this.username = user.getUsername();
    this.password = user.getPassword();
  }

  protected UserDao() {
  }

  public User toUser() {
    return new User(username, password);
  }

  public PublicUser toPublicUser() {
    return new PublicUser(id, username);
  }

  public Long getId() {
    return id;
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }
}
