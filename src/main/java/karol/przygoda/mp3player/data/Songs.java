package karol.przygoda.mp3player.data;

public class Songs {
    private String songPath;
    private String albumCoverPath;
    private String title;
    private String author;
    private int id;

    Songs(String title, String author, String songPath) {
        this.title = title;
        this.author = author;
        this.songPath = songPath;
    }

    Songs(int id, String title, String author, String songPath, String albumCoverPath) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.songPath = songPath;
        this.albumCoverPath = albumCoverPath;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSongPath() {
        return this.songPath;
    }

    public int getId() {
        return this.id;
    }

    public String getAlbumCoverPath() {
        return this.albumCoverPath;
    }
}
