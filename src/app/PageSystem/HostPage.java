package app.PageSystem;

import app.Host.Announcement;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class HostPage {
    ArrayList<Podcast> podcasts;
    ArrayList<Announcement> announcements;

    public HostPage(ArrayList<Podcast> podcasts, ArrayList<Announcement> announcements) {
        this.podcasts = podcasts;
        this.announcements = announcements;
    }

    public final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Construieste un obiect JSON pentru a afisa informatiile despre o pagina de host.
     *
     * @param hostPage   Pagina hostului din care se extrag informatiile.
     * @param username   Numele utilizatorului pentru care se afiseaza pagina.
     * @param timestamp  Timestamp-ul momentului afisarii paginii.
     * @return Un obiect JSON care contine informatii despre pagina hostului.
     */
    public ObjectNode showPage(HostPage hostPage, String username, int timestamp) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("user", username);
        resultNode.put("command", "printCurrentPage");
        resultNode.put("timestamp", timestamp);
        ArrayList<Podcast> podcasts1 = hostPage.getPodcasts();
        ArrayList<Announcement> announcements1 = hostPage.getAnnouncements();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Podcasts:\n\t");
        appendListToStringBuilder(messageBuilder, podcasts1);

        messageBuilder.append("\nAnnouncements:\n\t");
        appendListToStringBuilder(messageBuilder, announcements1);

        String message = messageBuilder.toString().replaceAll("\\n$", "");
        resultNode.put("message", message);

        return resultNode;
    }

    /**
     * Adauga informatii despre obiectele dintr-o lista la un sir de caractere.
     *
     * @param builder  StringBuilder pentru construirea sirului de caractere.
     * @param list     Lista de obiecte din care se extrag informatiile.
     */
    private void appendListToStringBuilder(StringBuilder builder, List<?> list) {
        builder.append("[");
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item instanceof Podcast) {
                Podcast podcast = (Podcast) item;
                builder.append(podcast.getName()).append(":\n\t");

                List<Episode> episodes = podcast.getEpisodes();
                builder.append("[");
                for (int j = 0; j < episodes.size(); j++) {
                    Episode episode = episodes.get(j);
                    builder.append(episode.getName()).append(" - ").append(episode.getDescription());
                    if (j < episodes.size() - 1) {
                        builder.append(", ");
                    }
                }
                builder.append("]");

                if (i < list.size() - 1) {
                    builder.append("\n");
                } else {
                    builder.append("\n");
                }
            } else if (item instanceof Announcement) {
                Announcement announcement = (Announcement) item;
                builder.append(announcement.getName()).append(":\n\t")
                        .append(announcement.getDescription());

                if (i < list.size() - 1) {
                    builder.append("\n,");
                } else {
                    builder.append("\n");
                }
            } else {
                builder.append(item);
            }
            if (i < list.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append("]\n");
    }
}
