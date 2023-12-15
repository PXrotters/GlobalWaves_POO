package app.Artist;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Merch {
    String name;
    String description;
    int price;

    public Merch(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public String toString() {
        return "[" + name + " - " + price + ": " + description + "]";
    }
}
