package app.PageSystem;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class LikedContentPage {
    ArrayList<Song> likedSongs;
    ArrayList<Playlist> followedPlaylists;

    public LikedContentPage(ArrayList<Song> likedSongs, ArrayList<Playlist> followedPlaylists) {
        this.likedSongs = likedSongs;
        this.followedPlaylists = followedPlaylists;
    }

    public ArrayList<Song> getLikedSongs() {
        return likedSongs;
    }

    public void setLikedSongs(ArrayList<Song> likedSongs) {
        this.likedSongs = likedSongs;
    }

    public ArrayList<Playlist> getFollowedPlaylists() {
        return followedPlaylists;
    }

    public void setFollowedPlaylists(ArrayList<Playlist> followedPlaylists) {
        this.followedPlaylists = followedPlaylists;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public final ObjectMapper objectMapper = new ObjectMapper();

    public ObjectNode showPage(LikedContentPage likedContentPage, String username, int timestamp) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("user", username);
        resultNode.put("command", "printCurrentPage");
        resultNode.put("timestamp", timestamp);
        ArrayList<Song> likedSongs = likedContentPage.getLikedSongs();
        ArrayList<Playlist> followedPlaylists = likedContentPage.getFollowedPlaylists();

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append("Liked songs:\n");
        if (!likedSongs.isEmpty()) {
            messageBuilder.append("\t[").append(formatSong(likedSongs.get(0)));
            for (int i = 1; i < likedSongs.size(); i++) {
                messageBuilder.append(", ").append(formatSong(likedSongs.get(i)));
            }
            messageBuilder.append("]\n\n");
        } else {
            messageBuilder.append("\t[]\n\n");
        }

        messageBuilder.append("Followed playlists:\n");
        if (!followedPlaylists.isEmpty()) {
            messageBuilder.append("\t[").append(formatPlaylist(followedPlaylists.get(0)));
            for (int i = 1; i < followedPlaylists.size(); i++) {
                messageBuilder.append(", ").append(formatPlaylist(followedPlaylists.get(i)));
            }
            messageBuilder.append("]");
        } else {
            messageBuilder.append("\t[]");
        }

        resultNode.put("message", messageBuilder.toString());

        return resultNode;
    }

    private String formatSong(Song song) {
        return song.getName() + " - " + song.getArtist();
    }

    private String formatPlaylist(Playlist playlist) {
        return playlist.getName() + " - " + playlist.getOwner();
    }

}
