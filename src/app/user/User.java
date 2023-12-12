package app.user;

import app.Admin;
import app.PageSystem.HomePage;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.PlaylistOutput;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.player.Player;
import app.player.PlayerSource;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.utils.Enums;
import lombok.Getter;
import fileio.input.CommandInput;
import lombok.Setter;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.zip.Adler32;

public class User {
    @Getter @Setter
    private String username;
    @Getter @Setter
    private int age;
    @Getter @Setter
    private String city;
    @Getter
    private ArrayList<Playlist> playlists;
    @Getter
    private ArrayList<Song> likedSongs;
    @Getter
    private ArrayList<Playlist> followedPlaylists;
    private final Player player;
    private final SearchBar searchBar;
    private boolean searchedArtist = false;
    private ArrayList<String> searchedartists = new ArrayList<>();
    private boolean lastSearched;
    @Getter
    private boolean online;  //vedem daca userul este sau nu online
    @Getter @Setter
    private int typeofuser = 0;  //1-normal 2-artist 3-host;
    @Getter @Setter
    private int currentPage = 0; //1-Home 2-LikedContent 3-Artist 4-Host
    @Getter @Setter
    private String artistPage; //numele artistului selectat
    private String typeofsong = null;

    public User(String username, int age, String city) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.online = true;
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        lastSearched = false;
        typeofuser = 1;
        currentPage = 1;
        artistPage = null;
    }

    public User(String username, int age, String city, int typeofuser) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.online = true;
        this.typeofuser = typeofuser;
        currentPage = 1;
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        lastSearched = false;
    }

    public ArrayList<String> search(Filters filters, String type) {
        searchBar.clearSelection();
        player.stop();

        lastSearched = true;
        ArrayList<String> results = new ArrayList<>();
        if (type.equals("artist")) {
            String part = filters.getName();
            searchedartists = Admin.getArtist(part);
            searchedArtist = true;
            typeofsong = null;
            return searchedartists;
        } else {
            if (type.equals("album")) {
                typeofsong = "album";
            } else {
                typeofsong = null;
            }
            List<LibraryEntry> libraryEntries = searchBar.search(filters, type);

            for (LibraryEntry libraryEntry : libraryEntries) {
                results.add(libraryEntry.getName());
            }
            return results;
        }
    }

    public String select(int itemNumber) {
        String name = null;
        if (!lastSearched)
            return "Please conduct a search before making a selection.";

        LibraryEntry selected;
        lastSearched = false;

        if (!searchedArtist) {
            selected = searchBar.select(itemNumber);
        } else {
            this.currentPage = 3;
            if (searchedartists != null && itemNumber > 0 && itemNumber <= searchedartists.size()) {
                name = searchedartists.get(itemNumber - 1);
                this.artistPage = name;
                selected = null;
                searchedartists = new ArrayList<>();
            } else {
                return "Invalid item number.";
            }
        }

        if (selected == null && name == null) {
            return "The selected ID is too high.";
        } else if (selected != null && name == null) {
            return "Successfully selected %s.".formatted(selected.getName());
        } else if (name != null && selected == null) {
            return "Successfully selected " + name + "'s page.";
        }
        return "Unexpected situation occurred.";
    }

    public String load() {
        if (searchBar.getLastSelected() == null)
            return "Please select a source before attempting to load.";

        if (!searchBar.getLastSearchType().equals("song") && ((AudioCollection)searchBar.getLastSelected()).getNumberOfTracks() == 0) {
            return "You can't load an empty audio collection!";
        }

        if (typeofsong == null) {
            player.setSource(searchBar.getLastSelected(), searchBar.getLastSearchType());
            searchBar.clearSelection();

            player.pause();

            return "Playback loaded successfully.";
        } else {
            player.setRepeatMode(Enums.RepeatMode.NO_REPEAT);
            player.setShuffle(false);
            player.setPaused(true);
            player.setType(searchBar.getLastSearchType());
            PlayerSource source = new PlayerSource(Enums.PlayerSourceType.ALBUM, (AudioCollection) searchBar.getLastSelected());
            player.setSource(source);
            searchBar.clearSelection();

            player.pause();

            if (player != null) {
                if (player.getSource() != null) {
                    if (player.getSource().getAudioFile() != null)
                    System.out.println(searchBar.getLastSelected());
                }
            }

            return "Playback loaded successfully.";
        }
    }

    public String playPause() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before attempting to pause or resume playback.";

        player.pause();

        if (player.getPaused())
            return "Playback paused successfully.";
        else
            return "Playback resumed successfully.";
    }

    public String repeat() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before setting the repeat status.";

        Enums.RepeatMode repeatMode = player.repeat();
        String repeatStatus = "";

        switch(repeatMode) {
            case NO_REPEAT -> repeatStatus = "no repeat";
            case REPEAT_ONCE -> repeatStatus = "repeat once";
            case REPEAT_ALL -> repeatStatus = "repeat all";
            case REPEAT_INFINITE -> repeatStatus = "repeat infinite";
            case REPEAT_CURRENT_SONG -> repeatStatus = "repeat current song";
        }

        return "Repeat mode changed to %s.".formatted(repeatStatus);
    }

    public String shuffle(Integer seed) {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before using the shuffle function.";

        if (!player.getType().equals("playlist"))
            return "The loaded source is not a playlist.";

        player.shuffle(seed);

        if (player.getShuffle())
            return "Shuffle function activated successfully.";
        return "Shuffle function deactivated successfully.";
    }

    public String forward() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before attempting to forward.";

        if (!player.getType().equals("podcast"))
            return "The loaded source is not a podcast.";

        player.skipNext();

        return "Skipped forward successfully.";
    }

    public String backward() {
        if (player.getCurrentAudioFile() == null)
            return "Please select a source before rewinding.";

        if (!player.getType().equals("podcast"))
            return "The loaded source is not a podcast.";

        player.skipPrev();

        return "Rewound successfully.";
    }

    public String like() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before liking or unliking.";

        if (!player.getType().equals("song") && !player.getType().equals("playlist"))
            return "Loaded source is not a song.";

        Song song = (Song) player.getCurrentAudioFile();

        if (likedSongs.contains(song)) {
            likedSongs.remove(song);
            song.dislike();

            return "Unlike registered successfully.";
        }

        likedSongs.add(song);
        song.like();
        return "Like registered successfully.";
    }

    public String next() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before skipping to the next track.";

        player.next();

        if (player.getCurrentAudioFile() == null)
            return "Please load a source before skipping to the next track.";

        return "Skipped to next track successfully. The current track is %s.".formatted(player.getCurrentAudioFile().getName());
    }

    public String prev() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before returning to the previous track.";

        player.prev();

        return "Returned to previous track successfully. The current track is %s.".formatted(player.getCurrentAudioFile().getName());
    }

    public String createPlaylist(String name, int timestamp) {
        if (playlists.stream().anyMatch(playlist -> playlist.getName().equals(name)))
            return "A playlist with the same name already exists.";

        playlists.add(new Playlist(name, username, timestamp));

        return "Playlist created successfully.";
    }

    public String addRemoveInPlaylist(int Id) {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before adding to or removing from the playlist.";

        if (player.getType().equals("podcast"))
            return "The loaded source is not a song.";

        if (Id > playlists.size())
            return "The specified playlist does not exist.";

        Playlist playlist = playlists.get(Id - 1);

        if (playlist.containsSong((Song)player.getCurrentAudioFile())) {
            playlist.removeSong((Song)player.getCurrentAudioFile());
            return "Successfully removed from playlist.";
        }

        playlist.addSong((Song)player.getCurrentAudioFile());
        return "Successfully added to playlist.";
    }

    public String switchPlaylistVisibility(Integer playlistId) {
        if (playlistId > playlists.size())
            return "The specified playlist ID is too high.";

        Playlist playlist = playlists.get(playlistId - 1);
        playlist.switchVisibility();

        if (playlist.getVisibility() == Enums.Visibility.PUBLIC) {
            return "Visibility status updated successfully to public.";
        }

        return "Visibility status updated successfully to private.";
    }

    public ArrayList<PlaylistOutput> showPlaylists() {
        ArrayList<PlaylistOutput> playlistOutputs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistOutputs.add(new PlaylistOutput(playlist));
        }

        return playlistOutputs;
    }

    public String follow() {
        LibraryEntry selection = searchBar.getLastSelected();
        String type = searchBar.getLastSearchType();

        if (selection == null)
            return "Please select a source before following or unfollowing.";

        if (!type.equals("playlist"))
            return "The selected source is not a playlist.";

        Playlist playlist = (Playlist)selection;

        if (playlist.getOwner().equals(username))
            return "You cannot follow or unfollow your own playlist.";

        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.decreaseFollowers();

            return "Playlist unfollowed successfully.";
        }

        followedPlaylists.add(playlist);
        playlist.increaseFollowers();


        return "Playlist followed successfully.";
    }

    public PlayerStats getPlayerStats() {
        return player.getStats();
    }

    public ArrayList<String> showPreferredSongs() {
        ArrayList<String> results = new ArrayList<>();
        for (AudioFile audioFile : likedSongs) {
            results.add(audioFile.getName());
        }

        return results;
    }

    public String getPreferredGenre() {
        String[] genres = {"pop", "rock", "rap"};
        int[] counts = new int[genres.length];
        int mostLikedIndex = -1;
        int mostLikedCount = 0;

        for (Song song : likedSongs) {
            for (int i = 0; i < genres.length; i++) {
                if (song.getGenre().equals(genres[i])) {
                    counts[i]++;
                    if (counts[i] > mostLikedCount) {
                        mostLikedCount = counts[i];
                        mostLikedIndex = i;
                    }
                    break;
                }
            }
        }

        String preferredGenre = mostLikedIndex != -1 ? genres[mostLikedIndex] : "unknown";
        return "This user's preferred genre is %s.".formatted(preferredGenre);
    }

    public String getConnectionStatus(User user) {
        if (online) {
            online = false;
        } else {
            online = true;
        }
        return user.getUsername() + " has changed status successfully.";
    }

    public void simulateTime(int time) {
        player.simulatePlayer(time);
    }
}
