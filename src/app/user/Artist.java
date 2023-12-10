package app.user;

import app.Artist.Event;
import app.Artist.Merch;
import app.audio.Collections.Album;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.SongInput;

import java.util.ArrayList;

public class Artist extends User {
    public ArrayList<Album> albums;
    public ArrayList<Event> events;
    public ArrayList<Merch> merches;
    public Artist(String username, int age, String city, ArrayList<Album> albums, ArrayList<Event> events, ArrayList<Merch> merches) {
        super(username, age, city, 2);
        this.albums = albums;
        this.events = events;
        this.merches = merches;
    }

    public ArrayList<Merch> getMerches() {
        return merches;
    }

    public void addMerch(Merch merch) {
        merches.add(merch);
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void addEvents(Event event) {
        events.add(event);
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }


    public final ObjectMapper objectMapper = new ObjectMapper();

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
