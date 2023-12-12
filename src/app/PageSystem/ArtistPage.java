package app.PageSystem;

import app.Artist.Event;
import app.Artist.Merch;
import app.audio.Collections.Album;
import app.user.Artist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class ArtistPage {
    ArrayList<Album> albums;
    ArrayList<Event> events;
    ArrayList<Merch> merches;

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<Merch> getMerches() {
        return merches;
    }

    public void setMerches(ArrayList<Merch> merches) {
        this.merches = merches;
    }

    public ArtistPage(ArrayList<Album> albums, ArrayList<Event> events, ArrayList<Merch> merches) {
        this.albums = albums;
        this.events = events;
        this.merches = merches;
    }

    public final ObjectMapper objectMapper = new ObjectMapper();

    public ObjectNode showPage(ArtistPage artistPage, String username, int timestamp) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("user", username);
        resultNode.put("command", "printCurrentPage");
        resultNode.put("timestamp", timestamp);
        ArrayList<Album> albums = artistPage.getAlbums();
        ArrayList<Event> events = artistPage.getEvents();
        ArrayList<Merch> merches = artistPage.getMerches();

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append("Albums:\n\t");
        appendListToStringBuilder(messageBuilder, albums);

        messageBuilder.append("\nMerch:\n\t");
        appendListToStringBuilder(messageBuilder, merches);

        messageBuilder.append("\nEvents:\n\t");
        appendListToStringBuilder(messageBuilder, events);

        String message = messageBuilder.toString().replaceAll("\\n$", "");
        resultNode.put("message", message);

        return resultNode;
    }

    private void appendListToStringBuilder(StringBuilder builder, List<?> list) {
        builder.append("[");
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item instanceof Event) {
                Event event = (Event) item;
                builder.append(event.getName()).append(" - ").append(event.getDate()).append(":\n\t")
                        .append(event.getDescription());
            } else if (item instanceof Merch) {
                Merch merch = (Merch) item;
                builder.append(merch.getName()).append(" - ").append(merch.getPrice()).append(":\n\t")
                        .append(merch.getDescription());
            } else {
                builder.append(item);
            }
            if (i < list.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append("]\n");
    }

}
