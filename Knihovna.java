import java.util.ArrayList;

public class Knihovna {
    private ArrayList<String> songs;

    public Knihovna() {
        songs = new ArrayList<>();
        songs.add("Songs/hafo.mp3");

    }

    public ArrayList<String> getSongs() {
        return songs;
    }
}
