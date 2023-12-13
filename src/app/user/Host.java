package app.user;

import app.Host.Announcement;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import lombok.Setter;

import java.util.ArrayList;

public class Host extends User{
    @Setter
    public ArrayList<Podcast> podcasts;
    public ArrayList<Announcement> announcements;
    public Host (String username, int age, String city, ArrayList<Podcast> podcasts, ArrayList<Announcement> announcements) {
        super(username, age, city, 3);
        this.podcasts = podcasts;
        this.announcements = announcements;
    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }
    public void addPodcast(Podcast podcast) {
        podcasts.add(podcast);
    }
    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }
    public void addAnnouncement(Announcement announcement) {
        announcements.add(announcement);
    }

    public final ObjectMapper objectMapper = new ObjectMapper();
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
