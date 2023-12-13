package app.Host;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter

public class Announcement {
    String name;
    String description;

    public Announcement(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
