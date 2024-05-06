package Observers;

import Observers.Events.Event;
import jade.GameObject;

public interface Observer {

  void onNotify(GameObject go, Event e);

}
