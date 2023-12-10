package app.PageSystem;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class HomePage {
    ArrayList<Song> songs;
    ArrayList<Playlist> followedPlaylists;

    public HomePage(ArrayList<Song> songs, ArrayList<Playlist> followedPlaylists) {
        this.songs = songs;
        this.followedPlaylists = followedPlaylists;
    }

    public final ObjectMapper objectMapper = new ObjectMapper();

    public ObjectNode showPage(HomePage homePage, String username, int timestamp) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("user", username);
        resultNode.put("command", "printCurrentPage");
        resultNode.put("timestamp", timestamp);
        ArrayList<Song> likedSongs = homePage.getSongs();
        ArrayList<Playlist> followedPlaylists = homePage.getFollowedPlaylists();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Liked songs:\n");
        if (!likedSongs.isEmpty()) {
            messageBuilder.append("\t[").append(likedSongs.get(0).getName());
            for (int i = 1; i < likedSongs.size(); i++) {
                messageBuilder.append(", ").append(likedSongs.get(i).getName());
            }
            messageBuilder.append("]\n");
        } else {
            messageBuilder.append("\t[]\n");
        }

        messageBuilder.append("\nFollowed playlists:\n");
        if (!followedPlaylists.isEmpty()) {
            messageBuilder.append("\t[").append(followedPlaylists.get(0).getName());
            for (int i = 1; i < followedPlaylists.size(); i++) {
                messageBuilder.append(", ").append(followedPlaylists.get(i).getName());
            }
            messageBuilder.append("]\n");
        } else {
            messageBuilder.append("\t[]");
        }

        // Adaugă mesajul la resultNode
        resultNode.put("message", messageBuilder.toString());

        return resultNode;
    }



    public ArrayList<Playlist> getFollowedPlaylists() {
        return followedPlaylists;
    }

    public void setFollowedPlaylists(ArrayList<Playlist> followedPlaylists) {
        this.followedPlaylists = followedPlaylists;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
