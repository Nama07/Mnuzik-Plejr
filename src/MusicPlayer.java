import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {

    // Objekt pro synchronizaci při pozastavení/pokračování přehrávání
    private static final Object playSignal = new Object();

    // Reference na GUI, aby mohl přehrávač aktualizovat zobrazení (např. posuvník, název písně)
    private MusicPlayerGUI musicPlayerGUI;

    // Aktuálně přehrávaná skladba
    private Song currentSong;
    public Song getCurrentSong() {
        return currentSong;
    }

    // Seznam skladeb v playlistu
    private ArrayList<Song> playlist;

    // Index aktuální skladby v playlistu
    private int currentPlaylistIndex;

    // Objekt JLayer, který zajišťuje přehrávání MP3 souborů
    private AdvancedPlayer advancedPlayer;

    // Příznaky stavu přehrávání
    private boolean isPaused;
    private boolean songFinished;
    private boolean pressedNext, pressedPrev;

    // Informace o pozici a čase skladby (pro posuvník a obnovení po pauze)
    private int currentFrame;
    private int currentTimeInMilli;

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    public void setCurrentTimeInMilli(int timeInMilli) {
        currentTimeInMilli = timeInMilli;
    }

    // Konstruktor – propojí přehrávač s GUI
    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    // Načtení jedné skladby
    public void loadSong(Song song) {
        currentSong = song;
        playlist = null;

        // Pokud už něco hraje, zastaví se
        if (!songFinished)
            stopSong();

        if (currentSong != null) {
            currentFrame = 0;
            currentTimeInMilli = 0;
            musicPlayerGUI.setPlaybackSliderValue(0);

            // Spustí přehrávání
            playCurrentSong();
        }
    }

    // Načtení playlistu z textového souboru (.txt)
    public void loadPlaylist(File playlistFile) {
        playlist = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(playlistFile))) {
            String songPath;
            while ((songPath = br.readLine()) != null) {
                playlist.add(new Song(songPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Pokud playlist obsahuje skladby, začne přehrávání první
        if (!playlist.isEmpty()) {
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMilli = 0;
            currentSong = playlist.get(0);
            currentFrame = 0;

            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);

            playCurrentSong();
        }
    }

    // Pozastavení skladby
    public void pauseSong() {
        if (advancedPlayer != null) {
            isPaused = true;
            stopSong();
        }
    }

    // Zastavení přehrávání
    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    // Přehrání další skladby v playlistu
    public void nextSong() {
        if (playlist == null || currentPlaylistIndex + 1 > playlist.size() - 1) return;

        pressedNext = true;
        if (!songFinished) stopSong();

        currentPlaylistIndex++;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMilli = 0;

        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);
        playCurrentSong();
    }

    // Přehrání předchozí skladby v playlistu
    public void prevSong() {
        if (playlist == null || currentPlaylistIndex - 1 < 0) return;

        pressedPrev = true;
        if (!songFinished) stopSong();

        currentPlaylistIndex--;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMilli = 0;

        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);
        playCurrentSong();
    }

    // Spuštění aktuální skladby
    public void playCurrentSong() {
        if (currentSong == null) return;

        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(currentSong.getFilePath()));
            advancedPlayer = new AdvancedPlayer(bis);
            advancedPlayer.setPlayBackListener(this);

            // Spuštění přehrávacího vlákna a vlákna pro posuvník
            startMusicThread();
            startPlaybackSliderThread();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Vlákno pro přehrávání hudby
    private void startMusicThread() {
        new Thread(() -> {
            try {
                if (isPaused) {
                    synchronized (playSignal) {
                        isPaused = false;
                        playSignal.notify();
                    }
                    advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                } else {
                    advancedPlayer.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Vlákno pro aktualizaci posuvníku přehrávání
    private void startPlaybackSliderThread() {
        new Thread(() -> {
            if (isPaused) {
                try {
                    synchronized (playSignal) {
                        playSignal.wait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            while (!isPaused && !songFinished && !pressedNext && !pressedPrev) {
                try {
                    currentTimeInMilli++;
                    int calculatedFrame = (int) ((double) currentTimeInMilli * 2.08 * currentSong.getFrameRatePerMilliseconds());
                    musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);
                    Thread.sleep(1); // aktualizace každou 1 ms
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Přehrávání spuštěno");
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Přehrávání dokončeno");
        if (isPaused) {
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        } else {
            if (pressedNext || pressedPrev) return;

            songFinished = true;

            if (playlist == null) {
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            } else if (currentPlaylistIndex == playlist.size() - 1) {
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            } else {
                nextSong();
            }
        }
    }
}
