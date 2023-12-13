package app;

import app.Artist.Event;
import app.Artist.Merch;
import app.Host.Announcement;
import app.PageSystem.ArtistPage;
import app.PageSystem.HomePage;
import app.PageSystem.HostPage;
import app.PageSystem.LikedContentPage;
import app.audio.Collections.Album;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.player.Player;
import app.player.PlayerStats;
import app.user.Artist;
import app.user.Host;
import app.user.User;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.*;
import lombok.Setter;

import java.util.*;

public class Admin {
    private static List<User> users = new ArrayList<>();
    private static List<Song> songs = new ArrayList<>();
    private static List<Podcast> podcasts = new ArrayList<>();
    @Setter
    private static List<Album> albumslibrary = new ArrayList<>();
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

    public static ArrayList<String> getHost(String part) {
        ArrayList<String> matchingHosts = new ArrayList<>();
        int count = 0;

        for (User user : users) {
            if (user instanceof Host && user.getUsername().startsWith(part)) {
                matchingHosts.add(user.getUsername());
                count++;

                if (count == 5) {
                    break;
                }
            }
        }
        return matchingHosts;
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
            } else if (user1.getCurrentPage() == 3) {
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
            } else if (user1.getCurrentPage() == 4) {
                String hostUsername = user1.getHostPage();
                Host host = null;
                for (User user : users) {
                    if (user.getUsername().equals(hostUsername)) {
                        host = (Host) user;
                        break;
                    }
                }
                if (host != null) {
                    HostPage hostPage = new HostPage(host.getPodcasts(), host.getAnnouncements());
                    return hostPage.showPage(hostPage, username, timestamp);
                } else {
                    return null;
                }
            } else if (user1.getCurrentPage() == 2) {
                LikedContentPage likedContentPage = new LikedContentPage(user1.getLikedSongs(), user1.getFollowedPlaylists());
                return likedContentPage.showPage(likedContentPage, username, timestamp);
            }
        } else {
            return null;
        }
        return null;
    }

    public static String ChangePage(String username, String nextPage) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (nextPage.equals("Home")) {
                    user.setCurrentPage(1);
                    return username + " accessed " + nextPage + " successfully.";
                } else if (nextPage.equals("LikedContent")) {
                    user.setCurrentPage(2);
                    return username + " accessed " + nextPage + " successfully.";
                } else {
                    return username + " is trying to access a non-existent page.";
                }
            }
        }
        return "Error";
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

    public static String AddAnnouncement(String username, String name, String description) {
        Host host = null;
        boolean found_announcement = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                host = (Host) user;
                break;
            }
        }
        ArrayList<Announcement> announcements = host.getAnnouncements();
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(name)) {
                found_announcement = true;
                break;
            }
        }
        if (!found_announcement) {
            Announcement announcement = new Announcement(name, description);
            host.addAnnouncement(announcement);
            return username + " has successfully added new announcement.";
        } else {
            return username + " has already added an announcement with this name.";
        }
    }

    public static String RemoveAnnouncement(String username, String name) {
        Host host = null;
        boolean found_announcement = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                host = (Host) user;
                break;
            }
        }
        ArrayList<Announcement> announcements = host.getAnnouncements();
        Iterator<Announcement> iterator = announcements.iterator();
        while (iterator.hasNext()) {
            Announcement announcement = iterator.next();
            if (announcement.getName().equals(name)) {
                iterator.remove();
                found_announcement = true;
                break;
            }
        }
        if (!found_announcement){
            return username + " has no announcement with the given name.";
        } else {
            return username + " has successfully deleted the announcement.";
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
                List<Song> sngs = new ArrayList<>();
                Album album = new Album(name, username, album_songs, releaseYear, description, sngs);
                for (SongInput songInput : album_songs) {
                    Song song = new Song(songInput.getName(),songInput.getDuration(),songInput.getAlbum(),songInput.getTags(),songInput.getLyrics(),songInput.getGenre(),songInput.getReleaseYear(), songInput.getArtist());
                    sngs.add(song);
                }

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
                    albumslibrary.add(album);
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

    public static String AddPodcast(String username, String name, List<EpisodeInput> podcastEpisodes) {
        boolean found = false;
        Host host = null;
        boolean found_podcast = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                found = true;
                host = (Host) user;
                break;
            }
        }

        if (!found) {
            return "The username " + username + " doesn't exist.";
        } else {
            ArrayList<Podcast> HostPodcast = host.getPodcasts();
            for (Podcast podcast_search : HostPodcast) {
                if (podcast_search.getName().equals(name)) {
                    found_podcast = true;
                    break;
                }
            }
            if (!found_podcast) {
                List<Episode> episodes = new ArrayList<>();
                for (EpisodeInput episode : podcastEpisodes) {
                    Episode newepisode = new Episode(episode.getName(), episode.getDuration(), episode.getDescription());
                    episodes.add(newepisode);
                }
                Podcast newpodcast = new Podcast(name, username, episodes);

                Set<String> episodeNamesSet = new HashSet<>();
                boolean duplicateFound = false;
                for (EpisodeInput episode : podcastEpisodes) {
                    if (episodeNamesSet.contains(episode.getName())) {
                        duplicateFound = true;
                        break;
                    } else {
                        episodeNamesSet.add(episode.getName());
                    }
                }
                if (duplicateFound) {
                    return username + " has the same episode in this podcast.";
                } else {
                    host.addPodcast(newpodcast);
                    podcasts.add(newpodcast);
                    return username + " has added new podcast successfully.";
                }
            } else {
                return username + " has another podcast with the same name.";
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

    public static ObjectNode showPodcasts (String username, int timestamp) {
        Host host = null;
        for (User user: users) {
            if (user.getUsername().equals(username)) {
                host = (Host) user;
                break;
            }
        }
        return host.showPodcasts(username, timestamp);
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
                ArrayList<Podcast> hostPodcasts = new ArrayList<>();
                ArrayList<Announcement> announcements = new ArrayList<>();
                Host host = new Host(username, age, city, hostPodcasts, announcements);
                users.add(host);
                return "The username " + username + " has been added successfully.";
            }
        } else {
            return "The username " + username + " is already taken.";
        }
    }

    public static String DeleteUser(String username) {
        int indexToRemove = -1;
        int type = 0;
        User ourUser = null;

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getUsername().equals(username)) {
                ourUser = user;
                type = user.getTypeofuser();
                indexToRemove = i;
                break;
            }
        }

        if (type == 1) {   //TODO eliminare playlisturi create de el + followed playlist la ceilalti useri
            if (indexToRemove != -1) {
                ArrayList<Playlist> playlists = ourUser.getPlaylists(); //playlisturile userului pe care vrem sa le stergem
                List<Song> playlistsSongs = new ArrayList<>();  //toate melodiile din playlisturile userului
                for (Playlist playlist : playlists) {
                    for (Song song : playlist.getSongs()) {
                        playlistsSongs.add(song);
                    }
                }

                boolean interactions = false;
                for (User user : users) {  //cautam sa vedem daca playerul tuturor userilor contine vreo melodie din playlisturile sale
                    PlayerStats player = user.getPlayerStats();
                    if (player.getRemainedTime() != 0) {
                        for (Song song : playlistsSongs) {
                            if (song.getName().equals(player.getName())) {
                                interactions = true;
                            }
                        }
                    }
                }
                if (interactions == true) {
                    return username + " can't be deleted.";
                } else {
                    for (User user : users) {  //eliminam toate playlisturile userului nostru din urmaririle celorlalti
                        Iterator<Playlist> playlistIterator = user.getFollowedPlaylists().iterator();
                        while (playlistIterator.hasNext()) {
                            Playlist playlist1 = playlistIterator.next();
                            for (Playlist playlist : playlists) {
                                Playlist newPlaylist = new Playlist(playlist.getName(), playlist.getOwner());
                                if (playlist1.getName().equals(newPlaylist.getName())) {
                                    playlistIterator.remove();
                                    break;
                                }
                            }
                        }
                    }
                    users.remove(indexToRemove);
                    return username + " was successfully deleted.";
                }
            } else {
                return "Greseala Delete User Normal";
            }
        } else if (type == 2) {   //eliminare artist + legaturi
            boolean interactions = false;

            if (indexToRemove == -1) {
                return "Greseala Delete User Artist";
            } else {
                for (User user : users) {  //cautam sa vedem daca playerul tuturor userilor contine vreo melodie
                    PlayerStats player = user.getPlayerStats();
                    if (player.getRemainedTime() != 0) {  //daca gasim o melodie care ruleaza in playerul unui user
                        for (Song song : songs) {
                            if (song.getName().equals(player.getName())) {
                                Song newsong = new Song(song.getName(), song.getDuration(), song.getAlbum(), song.getTags(), song.getLyrics(), song.getGenre(), song.getReleaseYear(), song.getArtist());
                                if (newsong.getArtist().equals(username)) {
                                    interactions = true;
                                }
                            }
                        }
                    }
                }
                if (interactions == true) {
                    return username + " can't be deleted.";
                } else {
                    Iterator<Album> iterator = albumslibrary.iterator();
                    while (iterator.hasNext()) {  //stergem toate albumele din librarie
                        Album album = iterator.next();
                        if (album.getOwner().equals(username)) {
                            iterator.remove();
                        }
                    }

                    Artist artist = null;  //gasim artistul in users
                    for (User user : users) {
                        if (user.getUsername().equals(username)) {
                            artist = (Artist) user;
                            break;
                        }
                    }
                    List<Album> currentalbums = artist.getAlbums(); //toate albumele artistului
                    List<Song> allartistsongs = new ArrayList<>(); //toate melodiile din albumele artistului
                    for (Album currentalbum : currentalbums) {
                        List<Song> allsongs = currentalbum.getSongs1();
                        for (Song song : allsongs) {
                            allartistsongs.add(song);
                        }
                    }
                    Iterator<Song> songIterator = songs.iterator();  //stergem toate melodiile din biblioteca si din likedsongs
                    while (songIterator.hasNext()) {
                        Song song = songIterator.next();
                        ArrayList<Song> newlikedsongs = new ArrayList<>();
                        for (User user : users) {
                            for (Song currentlikedsong : user.getLikedSongs()) {
                                boolean found = false;
                                for (Song artistsong : allartistsongs) {
                                    if (artistsong.getName().equals(currentlikedsong.getName())) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    newlikedsongs.add(currentlikedsong);
                                }
                            }
                            user.setLikedSongs(newlikedsongs);
                            newlikedsongs.clear();
                        }
                        if (song.getArtist().equals(artist.getUsername())) {
                            songIterator.remove();
                        }
                    }

                    users.remove(indexToRemove);
                    return username + " was successfully deleted.";
                }
            }
        } else {
            return "TODO for hosts";
        }
    }

    public static String RemoveAlbum(String username, String name) {
        Artist artist = null;
        boolean found_album = false;
        boolean interactions = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                artist = (Artist) user;
                break;
            }
        }
        ArrayList<Album> albums = artist.getAlbums();  //toate albumele artistului
        Album searchedalbum = new Album();  //albumul pe care vrem sa il stergem
        List<Song> allartistsongs = new ArrayList<>();  //toate melodiile din albumul artistului

        for (Album album : albums) {
            if (album.getName().equals(name)) {
                searchedalbum = album;
                allartistsongs = searchedalbum.getSongs1();
                found_album = true;
                break;
            }
        }

        if (found_album) {
            for (User user : users) {
                PlayerStats player = user.getPlayerStats();
                if (player.getRemainedTime() != 0) {  //daca gasim o melide care ruleza in playerul unui user o verificam
                    for (Song song : songs) {
                        if (song.getName().equals(player.getName())) {
                            Song newsong = new Song(song.getName(), song.getDuration(), song.getAlbum(), song.getTags(), song.getLyrics(), song.getGenre(), song.getReleaseYear(), song.getArtist());
                            if (newsong.getAlbum().equals(name)) {
                                interactions = true;
                            }
                        }
                    }
                }
            }

            if (interactions == true) {
                return username + " can't delete this album.";
            } else {
                Iterator<Album> iterator = albumslibrary.iterator();
                while (iterator.hasNext()) {  //stergem albumul din librarie
                    Album album = iterator.next();
                    if (album.getName().equals(name)) {
                        iterator.remove();
                        break;
                    }
                }

                Iterator<Song> songIterator = songs.iterator();  //stergem toate melodiile din biblioteca si din likedsongs
                while (songIterator.hasNext()) {
                    Song song = songIterator.next();
                    ArrayList<Song> newlikedsongs = new ArrayList<>();
                    for (User user : users) {
                        for (Song currentlikedsong : user.getLikedSongs()) {
                            boolean found = false;
                            for (Song artistsong : allartistsongs) {
                                if (artistsong.getName().equals(currentlikedsong.getName())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                newlikedsongs.add(currentlikedsong);
                            }
                        }
                        user.setLikedSongs(newlikedsongs);
                        newlikedsongs.clear();
                    }
                    if (song.getArtist().equals(artist.getUsername())) {
                        songIterator.remove();
                    }
                }

                iterator = albums.iterator();
                while (iterator.hasNext()) {  //stergem albumul din lista de albume a artistului
                    Album album = iterator.next();
                    if (album.getName().equals(name)) {
                        iterator.remove();
                    }
                }
                artist.setAlbums(albums);

                return username + " deleted the album successfully.";
            }
        } else {
            return username + " doesn't have an album with the given name.";
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

    public static List<String> getAllUsers() {
        Set<String> allUsersSet = new LinkedHashSet<>();

        for (User user : users) {
            if (user.getTypeofuser() == 1) {
                allUsersSet.add(user.getUsername());
            }
        }
        for (User user : users) {
            if (user.getTypeofuser() == 2) {
                allUsersSet.add(user.getUsername());
            }
        }
        for (User user : users) {
            if (user.getTypeofuser() == 3) {
                allUsersSet.add(user.getUsername());
            }
        }

        return new ArrayList<>(allUsersSet);
    }

    public static List<Album> getAlbumslibrary() {
        return albumslibrary;
    }

    public static void reset() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        timestamp = 0;
    }

}