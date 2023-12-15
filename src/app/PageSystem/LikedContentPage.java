package app.PageSystem;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public class LikedContentPage {
    ArrayList<Song> likedSongs;
    ArrayList<Playlist> followedPlaylists;

    public LikedContentPage(ArrayList<Song> likedSongs, ArrayList<Playlist> followedPlaylists) {
        this.likedSongs = likedSongs;
        this.followedPlaylists = followedPlaylists;
    }

    public final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Construieste un obiect JSON pentru a afisa informatiile despre pagina continutului apreciat a unui utilizator.
     *
     * @param likedContentPage  Pagina continutului apreciat din care se extrag informatiile.
     * @param username         Numele utilizatorului pentru care se afiseaza pagina.
     * @param timestamp        Timestamp-ul momentului afisarii paginii.
     * @return Un obiect JSON care contine informatii despre pagina continutului apreciat.
     */
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

    /**
     * Formateaza o instanta de {@code Song} intr-un sir de caractere cu format specific.
     *
     * @param song  Instanta de {@code Song} de formatat.
     * @return Un sir de caractere care reprezinta formatul specific al cantecului.
     */
    private String formatSong(Song song) {
        return song.getName() + " - " + song.getArtist();
    }

    /**
     * Formateaza o instanta de {@code Playlist} intr-un sir de caractere cu format specific.
     *
     * @param playlist  Instanta de {@code Playlist} de formatat.
     * @return Un sir de caractere care reprezinta formatul specific al listei de redare.
     */
    private String formatPlaylist(Playlist playlist) {
        return playlist.getName() + " - " + playlist.getOwner();
    }

}
