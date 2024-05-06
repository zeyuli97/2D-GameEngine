package Observers;

import Observers.Events.Event;
import jade.GameObject;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {
  private static List<Observer> observers = new ArrayList<>();

  public static void addObserver(Observer observer) {
    observers.add(observer);
  }

  public static void notify(GameObject go, Event event) {
    for (Observer observer : observers) {
      observer.onNotify(go, event);
    }
  }
}
