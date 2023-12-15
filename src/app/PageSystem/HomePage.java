package app.PageSystem;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public class HomePage {
    ArrayList<Song> songs;
    ArrayList<Playlist> followedPlaylists;

    public HomePage(ArrayList<Song> songs, ArrayList<Playlist> followedPlaylists) {
        this.songs = songs;
        this.followedPlaylists = followedPlaylists;
    }

    public final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Construieste un obiect JSON pentru a afisa informatiile despre pagina principala a unui utilizator.
     *
     * @param homePage   Pagina principala din care se extrag informatiile.
     * @param username   Numele utilizatorului pentru care se afiseaza pagina.
     * @param timestamp  Timestamp-ul momentului afisarii paginii.
     * @return Un obiect JSON care contine informatii despre pagina principala a utilizatorului.
     */
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
            messageBuilder.append("]");
        } else {
            messageBuilder.append("\t[]");
        }

        resultNode.put("message", messageBuilder.toString());

        return resultNode;
    }
}
