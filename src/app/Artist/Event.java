package app.Artist;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Event {
    String name;
    String description;
    String date;

    public Event(String name, String description, String date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }
}
