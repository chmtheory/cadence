package music;

public class TrackInfo {

    public final String trackId;
    private String name;
    private String artist;

    public TrackInfo(String id, String name) {
        this.trackId = id;
        this.name = name;
    }

    public TrackInfo(String id, String name, String artist) {
        this.trackId = id;
        this.name = name;
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

}
