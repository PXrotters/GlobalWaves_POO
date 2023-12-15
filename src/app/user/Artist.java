package app.user;

import app.Artist.Event;
import app.Artist.Merch;
import app.audio.Collections.Album;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
public class Artist extends User {
    @Setter
    public ArrayList<Album> albums;
    public ArrayList<Event> events;
    public ArrayList<Merch> merches;
    public Artist(String username, int age, String city, ArrayList<Album> albums, ArrayList<Event> events, ArrayList<Merch> merches) {
        super(username, age, city, 2);
        this.albums = albums;
        this.events = events;
        this.merches = merches;
    }

    /**
     * Adauga un obiect de tip {@code Merch} la lista de obiecte de tip merch din aceasta clasa.
     *
     * @param merch Obiectul de tip {@code Merch} de adaugat.
     */
    public void addMerch(Merch merch) {
        merches.add(merch);
    }

    /**
     * Adauga un obiect de tip {@code Event} la lista de obiecte de tip eveniment din aceasta clasa.
     *
     * @param event Obiectul de tip {@code Event} de adaugat.
     */
    public void addEvents(Event event) {
        events.add(event);
    }

    /**
     * Adauga un obiect de tip {@code Album} la lista de obiecte de tip album din aceasta clasa.
     *
     * @param album Obiectul de tip {@code Album} de adaugat.
     */
    public void addAlbum(Album album) {
        albums.add(album);
    }

    public final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Obtine un obiect JSON care reprezinta informatiile despre albumele din aceasta clasa.
     *
     * @param username  Numele utilizatorului pentru care se afiseaza albumele.
     * @param timestamp Timestamp-ul momentului afisarii albumelelor.
     * @return Obiectul JSON care contine informatiile despre albume.
     */
    public ObjectNode showAlbums(String username, int timestamp) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "showAlbums");
        resultNode.put("user", username);
        resultNode.put("timestamp", timestamp);

        ArrayNode albumsArray = resultNode.putArray("result");
        for (Album album : albums) {
            ObjectNode albumNode = objectMapper.createObjectNode();
            albumNode.put("name", album.getName());

            ArrayNode songsArray = albumNode.putArray("songs");
            for (SongInput song : album.getSongs()) {
                songsArray.add(song.getName());
            }

            albumsArray.add(albumNode);
        }

        return resultNode;
    }

}
