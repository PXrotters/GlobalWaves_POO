# Proiect GlobalWaves  - Etapa 2

Lemnaru Mihai-Daniel - 322CD

I have used the official solution for Stage I.

## Skel Structure

* src/
  * fileio/ - contains classes used to read data from the json files
  * main/
      * Main - the Main class runs the checker on your implementation. Add the entry point to your implementation in it. Run Main to test your implementation from the IDE or from command line. From this class, we find the command we want to send to the corresponding method in CommandRunner.
      * Test - run the main method from Test class with the name of the input file from the command line and the result will be written
        to the out.txt file. Thus, you can compare this result with ref.
  * app/
      * SingletonLazy - implementing the Singleton design pattern with lazy instantiation. The class provides a single instance of the Admin class and allows access to this instance.
      * CommandRunner - is a class where we have all possible commands. It retrieves the information necessary for displaying each command.
      * Admin - in this class, we have all the information stored in the library. Based on this information, we will make the necessary modifications according to each command found in CommandRunner. This way, we will make changes to both the entities in the library and to the users and their players.
  * Artist/
      * Event - A class that creates an object of type event.
      * Merch - A class that creates an object of type merch.
  * audio/
      * Collections/
          * Album - a class that creates an object of type album and contains three methods: one that finds the total number of likes, one that returns the number of songs in the album, and one that returns a specific song at a given index. This class inherits from the AudioCollection class, which, in turn, inherits from the LibraryEntry class representing one of the existing entities: Playlist, Podcast, Album.
  * Host/
      * Announcement - a class that creates an object of type announcement.
  * PageSystem/
      * ArtistPage - a class representing an artist's page and holding all the necessary information about him.
      * HostPage - a class representing a host's page and holding all the necessary information about him.
      * HomePage - a class representing a user's home page and containing information about the songs and playlists appreciated by the user.
      * LikedContentPage - a class representing a page similar to the HomePage but containing information about all the liked songs and followed playlists of a user.
  * player/ - contains various classes that manage the user's player.
  * searchBar/ - contains different classes that manage input filters and appropriately filter based on possible commands: search/select.
  * user/
      * User - a class that holds all information about a user. It also contains multiple methods that handle commands that can be executed by a user in stage I.
      * Artist - a class that creates an object of type artist and contains methods modeling specific artist data. Also, this class extends the User class since an artist is a User with additional features.
      * Host - a class that creates an object of type host and contains methods modeling specific host data. Also, this class extends the User class since a host is a User with additional features.
* input/ - contains the tests and library in JSON format
* ref/ - contains all reference output for the tests in JSON format

