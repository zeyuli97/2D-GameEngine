package Observers.Events;

public class Event {
  private EventType type;

  public Event(EventType eventType) {
    this.type = eventType;
  }

  public Event() {
    this.type = EventType.UserEvent;
  }

  public EventType getType() {
    return type;
  }
}
