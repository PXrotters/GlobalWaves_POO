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
import app.player.PlayerStats;
import app.user.Artist;
import app.user.Host;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Admin {
    private static List<User> users = new ArrayList<>();
    private static List<Song> songs = new ArrayList<>();
    private static List<Podcast> podcasts = new ArrayList<>();
    @Getter @Setter
    private static List<Album> albumsLibrary = new ArrayList<>();
    private static int timestamp = 0;

    /**
     * Returneaza o lista de cel mult 5 artisti ale caror nume de utilizator incepe cu o anumita parte specificata.
     * Se cauta in randul utilizatorilor pentru obiecte de tip Artist care indeplinesc conditia.
     *
     * @param part Partea specificata a numelui de utilizator cu care sa inceapa cautarea.
     * @return O lista de cel mult 5 nume de utilizator ale artistilor care indeplinesc conditia.
     */
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

    /**
     * Returneaza o lista de cel mult 5 gazde ale caror nume de utilizator incepe cu o anumita parte specificata.
     * Se cauta in randul utilizatorilor pentru obiecte de tip Host care indeplinesc conditia.
     *
     * @param part Partea specificata a numelui de utilizator cu care sa inceapa cautarea.
     * @return O lista de cel mult 5 nume de utilizator ale gazdelor care indeplinesc conditia.
     */
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

    /**
     * Returneaza un obiect JSON care reprezinta pagina curenta a unui utilizator in functie de starea curenta a acestuia.
     *
     * @param username  Numele utilizatorului pentru care se afiseaza pagina curenta.
     * @param timestamp Timestamp-ul momentului afisarii paginii.
     * @return Un obiect JSON care contine informatii despre pagina curenta a utilizatorului.
     *         În cazul in care nu se gaseste utilizatorul sau tipul paginii curente nu este recunoscut, se returneaza null.
     */
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

    /**
     * Schimba pagina curenta a unui utilizator in functie de pagina specificata.
     *
     * @param username  Numele utilizatorului pentru care se schimba pagina.
     * @param nextPage  Pagina catre se doreste a fi accesata ("Home" sau "LikedContent").
     * @return Un mesaj care indica daca schimbarea paginii a fost realizata cu succes sau daca s-a incercat accesarea
     *         unei pagini inexistente.
     */
    public static String ChangePage(String username, String nextPage) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (nextPage.equals("Home")) {
                    user.setCurrentPage(1);
                    user.setSelectArtistPage(null);
                    user.setSelectHostPage(null);
                    return username + " accessed " + nextPage + " successfully.";
                } else if (nextPage.equals("LikedContent")) {
                    user.setCurrentPage(2);
                    user.setSelectArtistPage(null);
                    user.setSelectHostPage(null);
                    return username + " accessed " + nextPage + " successfully.";
                } else {
                    return username + " is trying to access a non-existent page.";
                }
            }
        }
        return "Error";
    }

    /**
     * Adauga un nou eveniment pentru un artist dat, cu anumite informatii precum nume, descriere si data.
     *
     * @param username     Numele utilizatorului artist pentru care se adauga evenimentul.
     * @param name         Numele evenimentului de adaugat.
     * @param description  Descrierea evenimentului de adaugat.
     * @param date         Data evenimentului de adaugat, in formatul "dd-MM-yyyy".
     * @return Un mesaj care indica rezultatul adaugarii evenimentului:
     *         - Daca adaugarea s-a realizat cu succes, se returneaza un mesaj de succes.
     *         - Daca exista deja un eveniment cu acelasi nume pentru artistul respectiv, se returneaza un mesaj de eroare.
     *         - Daca data furnizata nu este valida, se returneaza un mesaj de eroare specific datei.
     *         - Daca formatul datei nu este corect, se returneaza un mesaj de eroare legat de formatul datei.
     */
    public static String AddEvent(String username, String name, String description, String date) {
        Artist artist = null;
        boolean found_event = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                artist = (Artist) user;
                break;
            }
        }
        ArrayList<Event> events = new ArrayList<>();
        if (artist != null) {
            events = artist.getEvents();
        }
        for (Event event : events) {
            if (event.getName().equals(name)) {
                found_event = true;
                break;
            }
        }
        if (!found_event) {
            boolean dateError = false;
            boolean formatError = false;

            try {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate date1 = LocalDate.parse(date, dateTimeFormatter);

                int day = date1.getDayOfMonth();
                int month = date1.getMonthValue();
                int year = date1.getYear();

                if (month == 2 && day > 28) {
                    dateError = true;
                }
                if (year < 1900 || year > 2023) {
                    dateError = true;
                }
            } catch (DateTimeParseException e) {
                formatError = true;
            }

            if (formatError || dateError) {
                return "Event for " + username + " does not have a valid date.";
            } else {
                Event event = new Event(name, description, date);
                if (artist != null) {
                    artist.addEvents(event);
                }
                return username + " has added new event successfully.";
            }
        } else {
            return username + " has has another event with the same name.";
        }
    }

    /**
     * Adauga un nou produs de merchandising pentru un artist dat, cu anumite informatii precum nume, descriere si pret.
     *
     * @param username     Numele utilizatorului artist pentru care se adauga produsul de merchandising.
     * @param name         Numele produsului de merchandising de adaugat.
     * @param description  Descrierea produsului de merchandising de adaugat.
     * @param price        Pretul produsului de merchandising de adaugat.
     * @return Un mesaj care indica rezultatul adaugarii produsului de merchandising:
     *         - Daca adaugarea s-a realizat cu succes, se returneaza un mesaj de succes.
     *         - Daca exista deja un produs de merchandising cu acelasi nume pentru artistul respectiv, se returneaza un mesaj de eroare.
     *         - Daca pretul furnizat este negativ, se returneaza un mesaj de eroare legat de pretul negativ.
     */
    public static String AddMerch(String username, String name, String description, int price) {
        Artist artist = null;
        boolean found_merch = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                artist = (Artist) user;
                break;
            }
        }
        ArrayList<Merch> merches = new ArrayList<>();
        if (artist != null) {
            merches = artist.getMerches();
        }
        for (Merch merch : merches) {
            if (merch.getName().equals(name)) {
                found_merch = true;
                break;
            }
        }
        if (!found_merch) {
            if (price > 0) {
                Merch merch = new Merch(name, description, price);
                if (artist != null) {
                    artist.addMerch(merch);
                }
                return username + " has added new merchandise successfully.";
            } else {
                return "Price for merchandise can not be negative.";
            }
        } else {
            return username + " has merchandise with the same name.";
        }
    }

    /**
     * Adauga o noua anuntare pentru un host data, cu anumite informatii precum nume si descriere.
     *
     * @param username     Numele hostului pentru care se adauga anuntarea.
     * @param name         Numele anuntarii de adaugat.
     * @param description  Descrierea anuntarii de adaugat.
     * @return Un mesaj care indica rezultatul adaugarii anuntarii:
     *         - Daca adaugarea s-a realizat cu succes, se returneaza un mesaj de succes.
     *         - Daca exista deja o anuntare cu acelasi nume pentru hostul respectiv, se returneaza un mesaj de eroare.
     */
    public static String AddAnnouncement(String username, String name, String description) {
        Host host = null;
        boolean found_announcement = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                host = (Host) user;
                break;
            }
        }
        ArrayList<Announcement> announcements = new ArrayList<>();
        if (host != null) {
            announcements = host.getAnnouncements();
        }
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(name)) {
                found_announcement = true;
                break;
            }
        }
        if (!found_announcement) {
            Announcement announcement = new Announcement(name, description);
            if (host != null) {
                host.addAnnouncement(announcement);
            }
            return username + " has successfully added new announcement.";
        } else {
            return username + " has already added an announcement with this name.";
        }
    }

    /**
     * Sterge o anuntare existenta pentru un host dat, pe baza numelui acestuia.
     *
     * @param username Numele hostului pentru care se sterge anuntarea.
     * @param name     Numele anuntarii de sters.
     * @return Un mesaj care indica rezultatul stergerii anuntarii:
     *         - Daca stergerea s-a realizat cu succes, se returneaza un mesaj de succes.
     *         - Daca nu exista nicio anuntare cu acelasi nume pentru hostul respectiv, se returneaza un mesaj de eroare.
     */
    public static String RemoveAnnouncement(String username, String name) {
        Host host = null;
        boolean found_announcement = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                host = (Host) user;
                break;
            }
        }
        ArrayList<Announcement> announcements = new ArrayList<>();
        if (host != null) {
            announcements = host.getAnnouncements();
        }
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

    /**
     * Sterge un eveniment existent pentru un artist dat, pe baza numelui acestuia.
     *
     * @param username Numele utilizatorului artist pentru care se sterge evenimentul.
     * @param name     Numele evenimentului de sters.
     * @return Un mesaj care indica rezultatul stergerii evenimentului:
     *         - Daca stergerea s-a realizat cu succes, se returneaza un mesaj de succes.
     *         - Daca nu exista niciun eveniment cu acelasi nume pentru artistul respectiv, se returneaza un mesaj de eroare.
     */
    public static String RemoveEvent(String username, String name) {
        Artist artist = null;
        boolean found_event = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                artist = (Artist) user;
                break;
            }
        }
        ArrayList<Event> events = new ArrayList<>();
        if (artist != null) {
            events = artist.getEvents();
        }
        Iterator<Event> iterator = events.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            if (event.getName().equals(name)) {
                iterator.remove();
                found_event = true;
                break;
            }
        }
        if (!found_event){
            return username + " doesn't have an event with the given name.";
        } else {
            return username + " deleted the event successfully.";
        }
    }

    /**
     * Adauga un nou album pentru un artist dat, cu anumite informatii precum nume, piese, an de lansare si descriere.
     *
     * @param username      Numele utilizatorului artist pentru care se adauga albumul.
     * @param name          Numele albumului de adaugat.
     * @param album_songs   Lista de piese care vor face parte din album.
     * @param releaseYear   Anul de lansare al albumului.
     * @param description   Descrierea albumului de adaugat.
     * @return Un mesaj care indica rezultatul adaugarii albumului:
     *         - Daca adaugarea s-a realizat cu succes, se returneaza un mesaj de succes.
     *         - Daca exista deja un album cu acelasi nume pentru artistul respectiv, se returneaza un mesaj de eroare.
     *         - Daca exista cel putin o piesa duplicata in cadrul albumului, se returneaza un mesaj de eroare.
     *         - Daca numele de utilizator specificat nu exista, se returneaza un mesaj de eroare.
     */
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
                List<Song> currentSongs = new ArrayList<>();
                Album album = new Album(name, username, album_songs, releaseYear, description, currentSongs);
                for (SongInput songInput : album_songs) {
                    Song song = new Song(
                            songInput.getName(),
                            songInput.getDuration(),
                            songInput.getAlbum(),
                            songInput.getTags(),
                            songInput.getLyrics(),
                            songInput.getGenre(),
                            songInput.getReleaseYear(),
                            songInput.getArtist()
                    );
                    currentSongs.add(song);
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
                    albumsLibrary.add(album);
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

    /**
     * Adauga un nou podcast pentru un hostul dat, cu anumite informatii precum nume si episoade.
     *
     * @param username          Numele hostului pentru care se adauga podcastul.
     * @param name              Numele podcastului de adaugat.
     * @param podcastEpisodes   Lista de episoade care vor face parte din podcast.
     * @return Un mesaj care indica rezultatul adaugarii podcastului:
     *         - Daca adaugarea s-a realizat cu succes, se returneaza un mesaj de succes.
     *         - Daca exista deja un podcast cu acelasi nume pentru hostul respectiv, se returneaza un mesaj de eroare.
     *         - Daca exista cel putin un episod duplicat in cadrul podcastului, se returneaza un mesaj de eroare.
     *         - Daca numele de utilizator specificat nu exista, se returneaza un mesaj de eroare.
     */
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

    /**
     * Genereaza un obiect JSON ce contine informatii despre albumele unui artist si trimite un mesaj de eroare in cazul unei situatii neasteptate.
     *
     * @param username   Numele de utilizator al artistului ale carui albume vor fi afisate.
     * @param timestamp  Marca de timp pentru solicitarea afisarii albumelelor.
     * @return Un obiect JSON care contine informatii despre albumele artistului:
     *         - Daca artistul este gasit, se returneaza un obiect JSON cu informatii despre albume si un mesaj de succes.
     *         - Daca artistul nu este gasit, se returneaza un obiect JSON cu un mesaj de eroare.
     */
    public static ObjectNode ShowAlbum (String username, int timestamp) {
        Artist artist = null;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                artist = (Artist) user;
                break;
            }
        }
        if (artist != null) {
            return artist.showAlbums(username, timestamp);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "Error");
            return objectNode;
        }
    }

    /**
     * Genereaza un obiect JSON ce contine informatii despre podcasturile unei gazde si trimite un mesaj de eroare in cazul unei situatii neasteptate.
     *
     * @param username   Numele de utilizator al gazdei ale carui podcasturi vor fi afisate.
     * @param timestamp  Marca de timp pentru solicitarea afisarii podcasturilor.
     * @return Un obiect JSON care contine informatii despre podcasturile gazdei:
     *         - Daca hostul este gasita, se returneaza un obiect JSON cu informatii despre podcasturi si un mesaj de succes.
     *         - Daca hostul nu este gasita, se returneaza un obiect JSON cu un mesaj de eroare.
     */
    public static ObjectNode showPodcasts (String username, int timestamp) {
        Host host = null;
        for (User user: users) {
            if (user.getUsername().equals(username)) {
                host = (Host) user;
                break;
            }
        }
        if (host != null) {
            return host.showPodcasts(username, timestamp);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "Error");
            return objectNode;
        }
    }

    /**
     * Adauga un nou utilizator in sistem in functie de tipul specificat.
     *
     * @param username Numele de utilizator al noului utilizator.
     * @param age      Varsta noului utilizator.
     * @param city     Orasul de provenienta al noului utilizator.
     * @param type     Tipul de utilizator:
     *                 - 1 pentru utilizator obisnuit,
     *                 - 2 pentru artist,
     *                 - 3 pentru host.
     * @return Un mesaj care indica rezultatul operatiei:
     *         - Daca utilizatorul a fost adaugat cu succes, se returneaza un mesaj de succes.
     *         - Daca numele de utilizator este deja luat, se returneaza un mesaj de eroare.
     */
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

    /**
     * Sterge un utilizator din sistem in functie de numele de utilizator si tipul de utilizator.
     *
     * @param username Numele de utilizator al utilizatorului de sters.
     * @return Un mesaj care indica rezultatul operatiei:
     *         - Daca utilizatorul a fost sters cu succes, se returneaza un mesaj de succes.
     *         - Daca utilizatorul nu a putut fi sters din cauza interactiunilor existente, se returneaza un mesaj de eroare.
     */
    public static String DeleteUser(String username) {
        int indexToRemove = -1;
        int type = 0;
        User ourUser = null;
        boolean interactions = false;

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getUsername().equals(username)) {
                ourUser = user;
                type = user.getTypeOfUser();
                indexToRemove = i;
                break;
            }
        }

        if (type == 1) {
                ArrayList<Playlist> playlists = ourUser.getPlaylists();
                ArrayList<Playlist> followedPlaylists = ourUser.getFollowedPlaylists();
                List<Song> playlistsSongs = new ArrayList<>();
                for (Playlist playlist : playlists) {
                    playlistsSongs.addAll(playlist.getSongs());
                }

                for (User user : users) {
                    PlayerStats player = user.getPlayerStats();
                    if (player.getRemainedTime() != 0) {
                        for (Song song : playlistsSongs) {
                            if (song.getName().equals(player.getName())) {
                                interactions = true;
                                break;
                            }
                        }
                    }
                }
                if (interactions) {
                    return username + " can't be deleted.";
                } else {
                    for (User user : users) {
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

                    for (User user : users) {
                        for (Playlist playlist : followedPlaylists) {
                            if (playlist.getOwner().equals(user.getUsername())) {
                                int followers = playlist.getFollowers();
                                followers--;
                                playlist.setFollowers(followers);
                            }
                        }
                    }

                    users.remove(indexToRemove);
                    return username + " was successfully deleted.";
                }
        } else if (type == 2) {

                for (User user : users) {
                    PlayerStats player = user.getPlayerStats();
                    if (player.getRemainedTime() != 0) {
                        for (Song song : songs) {
                            if (song.getName().equals(player.getName())) {
                                if (song.getArtist().equals(username)) {
                                    interactions = true;
                                }
                            }
                        }
                    }
                    if (user.getSelectArtistPage() != null) {
                        if (user.getSelectArtistPage().equals(username)) {
                            interactions = true;
                        }
                    }
                }
                if (interactions) {
                    return username + " can't be deleted.";
                } else {
                    albumsLibrary.removeIf(album -> album.getOwner().equals(username));
                    Artist artist = null;
                    for (User user : users) {
                        if (user.getUsername().equals(username)) {
                            artist = (Artist) user;
                            break;
                        }
                    }
                    List<Album> CurrentAlbum = new ArrayList<>();
                    if (artist != null) {
                        CurrentAlbum = artist.getAlbums();
                    }
                    List<Song> allArtistSongs = new ArrayList<>();
                    for (Album currentalbum : CurrentAlbum) {
                        List<Song> allSongs = currentalbum.getSongs1();
                        allArtistSongs.addAll(allSongs);
                    }

                    Iterator<Song> songIterator = songs.iterator();
                    while (songIterator.hasNext()) {
                        Song song = songIterator.next();
                        ArrayList<Song> newLikedSongs = new ArrayList<>();
                        for (User user : users) {
                            for (Song currentlikedsong : user.getLikedSongs()) {
                                boolean found = false;
                                for (Song artistsong : allArtistSongs) {
                                    if (artistsong.getName().equals(currentlikedsong.getName())) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    newLikedSongs.add(currentlikedsong);
                                }
                            }
                            user.setLikedSongs(newLikedSongs);
                            newLikedSongs.clear();
                        }
                        if (artist != null) {
                            if (song.getArtist().equals(artist.getUsername())) {
                                songIterator.remove();
                            }
                        }
                    }

                    users.remove(indexToRemove);
                    return username + " was successfully deleted.";
                }
        } else {
            Host ourHost = null;
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    ourHost = (Host) user;
                }
            }

            List<Episode> allHostEpisodes = new ArrayList<>();
            if (ourHost != null) {
                ArrayList<Podcast> allHostPodcasts = ourHost.getPodcasts();
                allHostEpisodes = allHostPodcasts.stream()
                        .flatMap(podcast -> podcast.getEpisodes().stream())
                        .toList();
            }

                for (User user : users) {
                    PlayerStats player = user.getPlayerStats();
                    if (player.getRemainedTime() != 0) {
                        for (Episode episode : allHostEpisodes) {
                            if (episode.getName().equals(player.getName())) {
                                interactions = true;
                                break;
                            }
                        }
                    }
                    if (user.getSelectHostPage() != null) {
                        if (user.getSelectHostPage().equals(username)) {
                            interactions = true;
                        }
                    }
                }

                if (interactions) {
                    return username + " can't be deleted.";
                } else {
                    podcasts.removeIf(podcast -> podcast.getOwner().equals(username));
                    users.remove(indexToRemove);
                    return username + " was successfully deleted.";
                }
        }
    }

    /**
     * Sterge un album de artist din sistem in functie de numele artistului si numele albumului.
     *
     * @param username Numele de utilizator al artistului.
     * @param name     Numele albumului de sters.
     * @return Un mesaj care indica rezultatul operatiei:
     *         - Daca albumul a fost sters cu succes, se returneaza un mesaj de succes.
     *         - Daca albumul nu a putut fi sters din cauza interactiunilor existente, se returneaza un mesaj de eroare.
     */
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
        ArrayList<Album> albums = new ArrayList<>();
        if (artist != null) {
            albums = artist.getAlbums();
        }
        Album searchedalbum;
        List<Song> allArtistSongs = new ArrayList<>();

        for (Album album : albums) {
            if (album.getName().equals(name)) {
                searchedalbum = album;
                allArtistSongs = searchedalbum.getSongs1();
                found_album = true;
                break;
            }
        }

        if (found_album) {
            for (User user : users) {
                PlayerStats player = user.getPlayerStats();
                if (player.getRemainedTime() != 0) {
                    for (Song song : songs) {
                        if (song.getName().equals(player.getName())) {
                            if (song.getAlbum().equals(name)) {
                                interactions = true;
                            }
                        }
                    }
                }
            }

            if (interactions) {
                return username + " can't delete this album.";
            } else {
                Iterator<Album> iterator = albumsLibrary.iterator();
                while (iterator.hasNext()) {
                    Album album = iterator.next();
                    if (album.getName().equals(name)) {
                        iterator.remove();
                        break;
                    }
                }

                for (User user : users) {
                    ArrayList<Playlist> userPlaylists = user.getPlaylists();
                    ArrayList<Song> userSongsFromPlaylists = new ArrayList<>();
                    for (Playlist playlist : userPlaylists) {
                        userSongsFromPlaylists.addAll(playlist.getSongs());
                        Iterator<Song> iteratorSong = userSongsFromPlaylists.iterator();
                        while (iteratorSong.hasNext()) {
                            Song song = iteratorSong.next();
                            for (Song artistSong : allArtistSongs) {
                                if (artistSong.getName().equals(song.getName())) {
                                    iteratorSong.remove();
                                    break;
                                }
                            }
                        }
                        playlist.setSongs(userSongsFromPlaylists);
                    }
                    user.setPlaylists(userPlaylists);
                }

                Iterator<Song> songIterator = songs.iterator();
                while (songIterator.hasNext()) {
                    Song song = songIterator.next();
                    ArrayList<Song> newLikedSongs = new ArrayList<>();
                    for (User user : users) {
                        for (Song currentlikedsong : user.getLikedSongs()) {
                            boolean found = false;
                            for (Song artistsong : allArtistSongs) {
                                if (artistsong.getName().equals(currentlikedsong.getName())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                newLikedSongs.add(currentlikedsong);
                            }
                        }
                        user.setLikedSongs(newLikedSongs);
                        newLikedSongs.clear();
                    }
                    if (song.getArtist().equals(artist.getUsername())) {
                        songIterator.remove();
                    }
                }

                iterator = albums.iterator();
                while (iterator.hasNext()) {
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

    /**
     * Sterge un podcast de la un host din sistem in functie de numele host-ului si numele podcast-ului.
     *
     * @param username Numele de utilizator al host-ului.
     * @param name     Numele podcast-ului de sters.
     * @return Un mesaj care indica rezultatul operatiei:
     *         - Daca podcast-ul a fost sters cu succes, se returneaza un mesaj de succes.
     *         - Daca podcast-ul nu a putut fi sters din cauza interactiunilor existente, se returneaza un mesaj de eroare.
     *         - Daca nu exista un podcast cu numele specificat, se returneaza un mesaj de eroare.
     */
    public static String RemovePodcast(String username, String name) {
        Host host = null;
        boolean found_podcast = false;
        boolean interactions = false;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                host = (Host) user;
                break;
            }
        }
        ArrayList<Podcast> podcasts1 = new ArrayList<>();
        if (host != null) {
            podcasts1 = host.getPodcasts();
        }
        ArrayList<Episode> episodes = new ArrayList<>();

        for (Podcast podcast : podcasts1) {
            if (podcast.getName().equals(name)) {
                episodes.addAll(podcast.getEpisodes());
                found_podcast = true;
                break;
            }
        }

        if (found_podcast) {
            for (User user : users) {
                PlayerStats player = user.getPlayerStats();
                if (player.getRemainedTime() != 0) {
                    for (Episode episode : episodes) {
                        if (episode.getName().equals(player.getName())) {
                            interactions = true;
                            break;
                        }
                    }
                }
            }

            if (interactions) {
                return username + " can't delete this podcast.";
            } else {
                Iterator<Podcast> podcastIterator = podcasts.iterator();
                while (podcastIterator.hasNext()) {
                    Podcast podcast = podcastIterator.next();
                    if (podcast.getName().equals(name)) {
                        podcastIterator.remove();
                    }
                }

                podcastIterator = podcasts1.iterator();
                while (podcastIterator.hasNext()) {
                    Podcast podcast = podcastIterator.next();
                    if (podcast.getName().equals(name)) {
                        podcastIterator.remove();
                    }
                }
                host.setPodcasts(podcasts1);

                return username + " deleted the podcast successfully.";

            }
        } else {
            return username + " doesn't have a podcast with the given name.";
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

    /**
     * Returneaza o lista cu numele celor mai apreciate 5 albume din biblioteca generala, sortate dupa numarul total de aprecieri
     * si, in caz de egalitate, dupa nume.
     *
     * @return Lista continand numele celor mai apreciate 5 albume.
     */
    public static List<String> getTop5Albums() {
        return albumsLibrary.stream()
                .sorted(Comparator.comparing(Album::getTotalLikes).reversed()
                        .thenComparing(Album::getName))
                .limit(5)
                .map(Album::getName)
                .collect(Collectors.toList());
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

    /**
     * Returneaza o lista cu numele utilizatorilor online de tipul "Player" (tip 1).
     *
     * @return Lista continand numele utilizatorilor online de tip "Player".
     */
    public static List<String> getOnlineUser() {
        List<String> onlineUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isOnline() && user.getTypeOfUser() == 1) {
                onlineUsers.add(user.getUsername());
            }
        }
        return onlineUsers;
    }

    /**
     * Returneaza o lista cu numele tuturor utilizatorilor de toate tipurile (Player, Artist, Host).
     *
     * @return Lista continand numele tuturor utilizatorilor de toate tipurile.
     */
    public static List<String> getAllUsers() {
        Set<String> allUsersSet = new LinkedHashSet<>();

        for (User user : users) {
            if (user.getTypeOfUser() == 1) {
                allUsersSet.add(user.getUsername());
            }
        }
        for (User user : users) {
            if (user.getTypeOfUser() == 2) {
                allUsersSet.add(user.getUsername());
            }
        }
        for (User user : users) {
            if (user.getTypeOfUser() == 3) {
                allUsersSet.add(user.getUsername());
            }
        }

        return new ArrayList<>(allUsersSet);
    }

    public static void reset() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        timestamp = 0;
    }

}