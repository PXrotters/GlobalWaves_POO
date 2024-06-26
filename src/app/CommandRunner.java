package app;

import app.audio.Collections.PlaylistOutput;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

import java.util.ArrayList;
import java.util.List;

public class CommandRunner {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectNode search(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        Filters filters = new Filters(commandInput.getFilters());
        String type = commandInput.getType();
        if (user != null) {
            ArrayList<String> results = user.search(filters, type);
            String message = "Search returned " + results.size() + " results";
            if (!user.isOnline()) {
                message = user.getUsername() + " is offline.";
                results = new ArrayList<>();
            }

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);
            objectNode.set("results", objectMapper.valueToTree(results));

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");
            return objectNode;
        }
    }

    public static ObjectNode select(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());

        if (user != null) {
            String message = user.select(commandInput.getItemNumber());

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode load(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.load();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode playPause(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.playPause();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode repeat(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.repeat();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode shuffle(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        Integer seed = commandInput.getSeed();
        if (user != null) {
            String message = user.shuffle(seed);

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode forward(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.forward();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode backward(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.backward();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode like(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message;
            if (!user.isOnline()) {
                message = user.getUsername() + " is offline.";
            } else {
                message = user.like();
            }

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode next(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.next();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode prev(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.prev();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");
            return objectNode;
        }
    }

    public static ObjectNode createPlaylist(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.createPlaylist(commandInput.getPlaylistName(), commandInput.getTimestamp());

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);
            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");
            return objectNode;
        }
    }

    public static ObjectNode addRemoveInPlaylist(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.addRemoveInPlaylist(commandInput.getPlaylistId());

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist");

            return objectNode;
        }
    }

    public static ObjectNode switchVisibility(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.switchPlaylistVisibility(commandInput.getPlaylistId());

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode showPlaylists(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            ArrayList<PlaylistOutput> playlists = user.showPlaylists();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.set("result", objectMapper.valueToTree(playlists));

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("result", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode follow(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.follow();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode status(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            PlayerStats stats = user.getPlayerStats();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.set("stats", objectMapper.valueToTree(stats));

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode showLikedSongs(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            ArrayList<String> songs = user.showPreferredSongs();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.set("result", objectMapper.valueToTree(songs));

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode getPreferredGenre(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String preferredGenre = user.getPreferredGenre();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.set("result", objectMapper.valueToTree(preferredGenre));

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("message", "The username doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode getTop5Songs(CommandInput commandInput) {
        List<String> songs = Admin.getTop5Songs();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.set("result", objectMapper.valueToTree(songs));

        return objectNode;
    }

    public static ObjectNode getTop5Albums(CommandInput commandInput) {
        List<String> albums = Admin.getTop5Albums();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.set("result", objectMapper.valueToTree(albums));

        return objectNode;
    }

    public static ObjectNode getTop5Playlists(CommandInput commandInput) {
        List<String> playlists = Admin.getTop5Playlists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.set("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    public static ObjectNode switchConnectionStatus(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.getConnectionStatus(user);
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");

            return objectNode;
        }
    }

    public static ObjectNode getOnlineUsers(CommandInput commandInput) {
        List<String> results = Admin.getOnlineUser();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.set("result", objectMapper.valueToTree(results));

        return objectNode;
    }

    public static ObjectNode getAllUsers (CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        List<String> results = Admin.getAllUsers();
        objectNode.set("result", objectMapper.valueToTree(results));
        return objectNode;
    }

    public static ObjectNode AddUser(CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        String typeOfUser = commandInput.getType();
        int type;
        if (typeOfUser.equals("user")){
            type = 1;
        } else if (typeOfUser.equals("artist")){
            type = 2;
        } else {
            type = 3;
        }
            String message = Admin.AddUser(commandInput.getUsername(), commandInput.getAge(), commandInput.getCity(), type);
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);
        return objectNode;
    }

    public static ObjectNode AddAlbum(CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        String message;
        int type = 0;
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            type = user.getTypeOfUser();
        }
        if (type == 1 || type == 3) {
            message = "The username " + commandInput.getUsername() + " is not an artist.";
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);
            return objectNode;
        } else if (type == 2) {
            message = Admin.AddAlbum(commandInput.getUsername(), commandInput.getName(), commandInput.getSongs(), commandInput.getReleaseYear(), commandInput.getDescription());
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);
            return objectNode;
        } else {
            objectNode.put("message", "False");
            return objectNode;
        }
    }

    public static ObjectNode AddPodcast(CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        String message;
        int type = 0;
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            type = user.getTypeOfUser();
        }
        if (type == 1 || type == 2) {
            message =commandInput.getUsername() + " is not a host.";
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);
            return objectNode;
        } else if (type == 3) {
            message = Admin.AddPodcast(commandInput.getUsername(), commandInput.getName(), commandInput.getEpisodes());
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);
            return objectNode;
        } else {
            objectNode.put("message", "False");
            return objectNode;
        }
    }

    public static ObjectNode ShowAlbum(CommandInput commandInput) {
        return Admin.ShowAlbum(commandInput.getUsername(), commandInput.getTimestamp());
    }

    public static ObjectNode ShowPodcasts(CommandInput commandInput) {
        return Admin.showPodcasts(commandInput.getUsername(), commandInput.getTimestamp());
    }

    public static ObjectNode PrintCurrentPage(CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            if (!user.isOnline()) {
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");
                return objectNode;
            }
            int type = user.getTypeOfUser();
            if (type == 1) {
                return Admin.CurrentPage(commandInput.getUsername(), commandInput.getTimestamp());
            } else {
                objectNode.put("message", "False");
                return objectNode;
            }
        } else {
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
            return objectNode;
        }
    }

    public static ObjectNode ChangePage(CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        String message = Admin.ChangePage(commandInput.getUsername(), commandInput.getNextPage());
        objectNode.put("message", message);
        return objectNode;
    }

    public static ObjectNode AddEvent (CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = Admin.getUser(commandInput.getUsername());
        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
        } else {
            int type = user.getTypeOfUser();
            if (type == 1 || type == 3) {
                objectNode.put("message", commandInput.getUsername() + " is not an artist.");
            } else {
                String message = Admin.AddEvent(commandInput.getUsername(), commandInput.getName(), commandInput.getDescription(), commandInput.getDate());
                objectNode.put("message", message);
            }
        }
        return objectNode;
    }

    public static ObjectNode AddMerch (CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = Admin.getUser(commandInput.getUsername());
        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
        } else {
            int type = user.getTypeOfUser();
            if (type == 1 || type == 3) {
                objectNode.put("message", commandInput.getUsername() + " is not an artist.");
            } else {
                String message = Admin.AddMerch(
                        commandInput.getUsername(),
                        commandInput.getName(),
                        commandInput.getDescription(),
                        commandInput.getPrice()
                );
                objectNode.put("message", message);
            }
        }
        return objectNode;
    }

    public static ObjectNode AddAnnouncement (CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = Admin.getUser(commandInput.getUsername());
        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
        } else {
            int type = user.getTypeOfUser();
            if (type == 1 || type == 2) {
                objectNode.put("message", commandInput.getUsername() + " is not a host.");
            } else {
                String message = Admin.AddAnnouncement(
                        commandInput.getUsername(),
                        commandInput.getName(),
                        commandInput.getDescription()
                );
                objectNode.put("message", message);
            }
        }
        return objectNode;
    }

    public static ObjectNode RemoveAnnouncement (CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = Admin.getUser(commandInput.getUsername());
        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
        } else {
            int type = user.getTypeOfUser();
            if (type == 1 || type == 2) {
                objectNode.put("message", commandInput.getUsername() + " is not a host.");
            } else {
                String message;
                message = Admin.RemoveAnnouncement(commandInput.getUsername(), commandInput.getName());
                objectNode.put("message", message);
            }
        }
        return objectNode;
    }

    public static ObjectNode RemoveEvent (CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = Admin.getUser(commandInput.getUsername());
        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
        } else {
            int type = user.getTypeOfUser();
            if (type == 1 || type == 3) {
                objectNode.put("message", commandInput.getUsername() + " is not an artist.");
            } else {
                String message;
                message = Admin.RemoveEvent(commandInput.getUsername(), commandInput.getName());
                objectNode.put("message", message);
            }
        }
        return objectNode;
    }

    public static ObjectNode RemoveAlbum (CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = Admin.getUser(commandInput.getUsername());
        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
        } else {
            int type = user.getTypeOfUser();
            if (type == 1 || type == 3) {
                objectNode.put("message", commandInput.getUsername() + " is not an artist.");
            } else {
                String message;
                message = Admin.RemoveAlbum(commandInput.getUsername(), commandInput.getName());
                objectNode.put("message", message);
            }
        }
        return objectNode;
    }

    public static ObjectNode RemovePodcast (CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = Admin.getUser(commandInput.getUsername());
        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
        } else {
            int type = user.getTypeOfUser();
            if (type == 1 || type == 2) {
                objectNode.put("message", commandInput.getUsername() + " is not a host.");
            } else {
                String message;
                message = Admin.RemovePodcast(commandInput.getUsername(), commandInput.getName());
                objectNode.put("message", message);
            }
        }
        return objectNode;
    }

    public static ObjectNode DeleteUser (CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = Admin.getUser(commandInput.getUsername());
        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
        } else {
            String message = Admin.DeleteUser(commandInput.getUsername());
            objectNode.put("message", message);
        }
        return objectNode;
    }
}
