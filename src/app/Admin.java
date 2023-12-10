package app;

import app.Artist.Event;
import app.Artist.Merch;
import app.PageSystem.ArtistPage;
import app.PageSystem.HomePage;
import app.audio.Collections.Album;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.Artist;
import app.user.User;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.*;

import java.util.*;

public class Admin {
    private static List<User> users = new ArrayList<>();
    private static List<Song> songs = new ArrayList<>();
    private static List<Podcast> podcasts = new ArrayList<>();
    private static int timestamp = 0;

    public static ArrayList<String> getArtist(String part) {
        ArrayList<String> matchingArtists = new ArrayList<>();
        int count = 0;

        for (User user : users) {
            if (user instanceof Artist && user.getUsername().startsWith(part)) {
                matchingArtists.add(user.getUsername());
                count++;

                if (count == 5) {
                    break;
                }
            }
        }
        return matchingArtists;
    }

    public static void setUsers(List<UserInput> userInputList) {
        users = new ArrayList<>();
        for (UserInput userInput : userInputList) {
            users.add(new User(userInput.getUsername(), userInput.getAge(), userInput.getCity()));
        }
    }

    public static ObjectNode CurrentPage(String username, int timestamp) {
        User user1 = null;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user1 = user;
                break;
            }
        }
        if (user1 != null) {
            if (user1.getCurrentPage() == 1) {
                HomePage home = new HomePage(user1.getLikedSongs(), user1.getFollowedPlaylists());
                return home.showPage(home, username, timestamp);
            } else if (user1.getCurrentPage() == 3) {   //TODO
                String artistUsername = user1.getArtistPage();
                Artist artist = null;
                for (User user : users) {
                    if (user.getUsername().equals(artistUsername)) {
                        artist = (Artist) user;
                        break;
                    }
                }
                if (artist != null) {
                    ArtistPage artistPage = new ArtistPage(artist.getAlbums(), artist.getEvents(), artist.getMerches());
                    return artistPage.showPage(artistPage, username, timestamp);
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
        return null;
    }

    public static String AddEvent(String username, String name, String description, String date) {
        Artist artist = null;
        boolean found_event = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                artist = (Artist) user;
                break;
            }
        }
        ArrayList<Event> events = artist.getEvents();
        for (Event event : events) {
            if (event.getName().equals(name)) {
                found_event = true;
                break;
            }
        }
        if (!found_event) {
            Event event = new Event(name, description, date);
            artist.addEvents(event);
            return username + " has added new event successfully.";
        } else {
            return username + " has has another event with the same name.";
        }
    }

    public static String AddMerch(String username, String name, String description, int price) {
        Artist artist = null;
        boolean found_merch = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                artist = (Artist) user;
                break;
            }
        }
        ArrayList<Merch> merches = artist.getMerches();
        for (Merch merch : merches) {
            if (merch.getName().equals(name)) {
                found_merch = true;
                break;
            }
        }
        if (!found_merch) {
            if (price > 0) {
                Merch merch = new Merch(name, description, price);
                artist.addMerch(merch);
                return username + " has added new merchandise successfully.";
            } else {
                return "Price for merchandise can not be negative.";
            }
        } else {
            return username + " has merchandise with the same name.";
        }
    }

    public static String AddAlbum(String username, String name, List<SongInput> album_songs, int releaseYear, String description) {
        boolean found = false;
        Artist artist = null;
        boolean found_album = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                found = true;
                artist = (Artist) user;
                break;
            }
        }
        if (!found) {
            return "The username " + username + " doesn't exist.";
        } else {
            ArrayList<Album> albums = artist.getAlbums();
            for (Album album_search : albums) {
                if (album_search.getName().equals(name)) {
                    found_album = true;
                    break;
                }
            }
            if (!found_album) {
                Album album = new Album(name, username, album_songs, releaseYear, description);
                Set<String> songNamesSet = new HashSet<>();
                boolean duplicateFound = false;
                for (SongInput song : album_songs) {
                    if (songNamesSet.contains(song.getName())) {
                        duplicateFound = true;
                        break;
                    } else {
                        songNamesSet.add(song.getName());
                    }
                }
                if (duplicateFound) {
                    return username + " has the same song at least twice in this album.";
                } else {
                    artist.addAlbum(album);
                    for (SongInput song : album_songs) {
                        Song newsong = new Song(song.getName(), song.getDuration(), song.getAlbum(), song.getTags(), song.getLyrics(), song.getGenre(), song.getReleaseYear(), song.getArtist());
                        songs.add(newsong);
                    }
                    return username + " has added new album successfully.";
                }
            } else {
                return username + " has another album with the same name.";
            }
        }
    }

    public static ObjectNode ShowAlbum (String username, int timestamp) {
        Artist artist = null;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                artist = (Artist) user;
                break;
            }
        }
        return artist.showAlbums(username, timestamp);
    }

    public static String AddUser(String username, int age, String city, int type) {
        boolean found = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                found = true;
                break;
            }
        }

        if (!found) {
            if (type == 1) {
                User user = new User(username, age, city, type);
                users.add(user);
                return "The username " + username + " has been added successfully.";
            } else if (type == 2) {
                ArrayList<Album> albums = new ArrayList<>();
                ArrayList<Event> events = new ArrayList<>();
                ArrayList<Merch> merches = new ArrayList<>();
                Artist artist = new Artist(username, age, city, albums, events, merches);
                users.add(artist);
                return "The username " + username + " has been added successfully.";
            } else {
                return "TODO FOR HOST";
            }
        } else {
            return "The username " + username + " is already taken.";
        }
    }


    public static void setSongs(List<SongInput> songInputList) {
        songs = new ArrayList<>();
        for (SongInput songInput : songInputList) {
            songs.add(new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist()));
        }
    }

    public static void setPodcasts(List<PodcastInput> podcastInputList) {
        podcasts = new ArrayList<>();
        for (PodcastInput podcastInput : podcastInputList) {
            List<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episodeInput : podcastInput.getEpisodes()) {
                episodes.add(new Episode(episodeInput.getName(), episodeInput.getDuration(), episodeInput.getDescription()));
            }
            podcasts.add(new Podcast(podcastInput.getName(), podcastInput.getOwner(), episodes));
        }
    }

    public static List<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    public static List<Podcast> getPodcasts() {
        return new ArrayList<>(podcasts);
    }

    public static List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        for (User user : users) {
            playlists.addAll(user.getPlaylists());
        }
        return playlists;
    }

    public static User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static void updateTimestamp(int newTimestamp) {
        int elapsed = newTimestamp - timestamp;
        timestamp = newTimestamp;
        if (elapsed == 0) {
            return;
        }

        for (User user : users) {
            if (user.isOnline()) {
                user.simulateTime(elapsed);
            }
        }
    }

    public static List<String> getTop5Songs() {
        List<Song> sortedSongs = new ArrayList<>(songs);
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        List<String> topSongs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= 5) break;
            topSongs.add(song.getName());
            count++;
        }
        return topSongs;
    }

    public static List<String> getTop5Playlists() {
        List<Playlist> sortedPlaylists = new ArrayList<>(getPlaylists());
        sortedPlaylists.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topPlaylists = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedPlaylists) {
            if (count >= 5) break;
            topPlaylists.add(playlist.getName());
            count++;
        }
        return topPlaylists;
    }

    public static List<String> getOnlineUser() {
        List<String> onlineUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isOnline() && user.getTypeofuser() == 1) {
                onlineUsers.add(user.getUsername());
            }
        }
        return onlineUsers;
    }

    public static void reset() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        timestamp = 0;
    }

}
