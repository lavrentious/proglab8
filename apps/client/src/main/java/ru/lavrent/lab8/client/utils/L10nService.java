package ru.lavrent.lab8.client.utils;

import javafx.beans.property.SimpleObjectProperty;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class L10nService {
  private static L10nService instance;
  private SimpleObjectProperty<Locale> locale = new SimpleObjectProperty<>(null);
  private ResourceBundle bundle;
  private Map<String, Locale> availableLocales;
  private String localeName;

  private L10nService() {
    this.availableLocales = new HashMap<>();
    System.out.println("initializing availableLocales");
    availableLocales.put("English", new Locale("en", "US"));
    availableLocales.put("Русский", new Locale("ru", "RU"));
    availableLocales.put("Español", new Locale("es", "ES"));

    this.locale.set(new Locale("en", "US"));
    this.bundle = ResourceBundle.getBundle("locales/gui", this.locale.get());
    this.localeName = "English";
  }

  public Map<String, Locale> getAvailableLocales() {
    return availableLocales;
  }

  public static L10nService getInstance() {
    if (instance == null) {
      instance = new L10nService();
    }
    return instance;
  }

  public void setLocale(String localeName) {
    System.out.println("setting locale " + localeName);
    if (!availableLocales.containsKey(localeName))
      return;
    this.localeName = localeName;
    this.bundle = ResourceBundle.getBundle("locales/gui", availableLocales.get(localeName));
    this.locale.set(availableLocales.get(localeName));
  }

  public Locale getLocale() {
    return locale.get();
  }

  public ResourceBundle getBundle() {
    return bundle;
  }

  public SimpleObjectProperty<Locale> getObservableLocale() {
    return locale;
  }

  public String getLocaleName() {
    return localeName;
  }

  public String getString(String key) {
    try {
      return this.bundle.getString(key);
    } catch (MissingResourceException e) {
      System.out.println("missing resource: " + key);
      return "#" + key + "#";
    }
  }
}
