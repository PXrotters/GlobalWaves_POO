package app.audio.Collections;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Album extends AudioCollection {
    private final List<SongInput> songs;
    private final List<Song> songs1;
    private final int releaseYear;
    private final String description;

    public Album(String name, String owner, List<SongInput> songs, int releaseYear, String description, List<Song> convertedSongs) {
        super(name, owner);
        this.songs = songs;
        this.releaseYear = releaseYear;
        this.description = description;
        this.songs1 = convertedSongs;
    }

    public Album(String username) {
        super("", username);
        this.songs = new ArrayList<>();
        this.songs1 = new ArrayList<>();
        this.releaseYear = 0;
        this.description = "";
    }

    public Album() {
        super("", "");
        this.songs = new ArrayList<>();
        this.songs1 = new ArrayList<>();
        this.releaseYear = 0;
        this.description = "";
    }

    /**
     * Returneaza numarul total de aprecieri pentru toate cantecele din album.
     *
     * @return Numarul total de aprecieri.
     */
    public int getTotalLikes() {
        return songs1.stream().mapToInt(Song::getLikes).sum();
    }

    @Override
    public int getNumberOfTracks() {
        return songs1.size();
    }

    @Override
    public AudioFile getTrackByIndex(int index) {
        if (index >= 0 && index < songs1.size()) {
            return songs1.get(index);
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }

}
