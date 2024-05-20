package ru.lavrent.lab8.common.utils;

public class Block<T> {
  private volatile boolean ready;
  private T value;

  public Block() {
    this.ready = false;
  }

  public Block(T value) {
    this.ready = false;
    this.value = value;
  }

  public synchronized void put(T value) {
    while (ready)
      try {
        wait();
      } catch (InterruptedException e) {
      }
    this.value = value;
    this.ready = true;
    notifyAll();
  }

  public synchronized T get() {
    while (!ready)
      try {
        wait();
      } catch (InterruptedException e) {
      }
    this.ready = false;
    notifyAll();
    return this.value;
  }

}
