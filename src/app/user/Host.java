package app.user;

import app.Host.Announcement;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Host extends User{
    @Getter @Setter
    public ArrayList<Podcast> podcasts;
    @Getter
    public ArrayList<Announcement> announcements;
    public Host (String username, int age, String city, ArrayList<Podcast> podcasts, ArrayList<Announcement> announcements) {
        super(username, age, city, 3);
        this.podcasts = podcasts;
        this.announcements = announcements;
    }

    /**
     * Adauga un obiect de tip {@code Podcast} la lista de obiecte de tip podcast din aceasta clasa.
     *
     * @param podcast Obiectul de tip {@code Podcast} de adaugat.
     */
    public void addPodcast(Podcast podcast) {
        podcasts.add(podcast);
    }

    /**
     * Adauga un obiect de tip {@code Announcement} la lista de obiecte de tip anunt din aceasta clasa.
     *
     * @param announcement Obiectul de tip {@code Announcement} de adaugat.
     */
    public void addAnnouncement(Announcement announcement) {
        announcements.add(announcement);
    }

    public final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Obtine un obiect JSON care reprezinta informatiile despre podcasturile din aceasta clasa.
     *
     * @param username  Numele utilizatorului pentru care se afiseaza podcasturile.
     * @param timestamp Timestamp-ul momentului afisarii podcasturilor.
     * @return Obiectul JSON care contine informatiile despre podcasturi.
     */
    public ObjectNode showPodcasts(String username, int timestamp) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "showPodcasts");
        resultNode.put("user", username);
        resultNode.put("timestamp", timestamp);

        ArrayNode podcastsArray = resultNode.putArray("result");
        for (Podcast podcast : podcasts) {
            ObjectNode podcastNode = objectMapper.createObjectNode();
            podcastNode.put("name", podcast.getName());

            ArrayNode episodesArray = podcastNode.putArray("episodes");
            for (Episode episode : podcast.getEpisodes()) {
                episodesArray.add(episode.getName());
            }
            podcastsArray.add(podcastNode);
        }
        return resultNode;
    }

}
