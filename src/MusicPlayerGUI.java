import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame {
    // Barvy rozhraní
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer; // hlavní přehrávač
    private JFileChooser jFileChooser; // výběr souborů

    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;
    private JSlider playbackSlider;

    public MusicPlayerGUI() {
        super("Music Player");

        // Základní nastavení okna
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();

        // Výchozí složka pro soubory MP3
        jFileChooser.setCurrentDirectory(new File("src/assets"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        addGuiComponents();
    }

    // Přidání všech částí GUI
    private void addGuiComponents() {
        addToolbar();

        // Obrázek (gramofonová deska)
        JLabel songImage = new JLabel(loadImage("src/assets/record.png"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);

        // Název písně
        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        // Interpret
        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        // Posuvník přehrávání
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth() / 2 - 300 / 2, 365, 300, 40);
        playbackSlider.setBackground(null);

        // Ovládání posuvníku myší
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // při uchopení posuvníku se skladba pozastaví
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // po puštění posuvníku se skladba přehraje od nové pozice
                JSlider source = (JSlider) e.getSource();
                int frame = source.getValue();
                musicPlayer.setCurrentFrame(frame);
                musicPlayer.setCurrentTimeInMilli(
                        (int) (frame / (2.08 * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds()))
                );
                musicPlayer.playCurrentSong();
                enablePauseButtonDisablePlayButton();
            }
        });
        add(playbackSlider);

        // Přidání přehrávacích tlačítek (předchozí, play, pause, další)
        addPlaybackBtns();
    }

    // Horní lišta s nabídkou
    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);
        toolBar.setFloatable(false);

        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        // Menu pro jednotlivé skladby
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        // Položka pro načtení jedné skladby
        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(e -> {
            int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
            File selectedFile = jFileChooser.getSelectedFile();

            if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                Song song = new Song(selectedFile.getPath());
                musicPlayer.loadSong(song);
                updateSongTitleAndArtist(song);
                updatePlaybackSlider(song);
                enablePauseButtonDisablePlayButton();
            }
        });
        songMenu.add(loadSong);

        // Menu pro playlist
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        // Vytvoření nového playlistu
        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(e -> new MusicPlaylistDialog(MusicPlayerGUI.this).setVisible(true));
        playlistMenu.add(createPlaylist);

        // Načtení existujícího playlistu
        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt"));
            jFileChooser.setCurrentDirectory(new File("src/assets"));
            int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
            File selectedFile = jFileChooser.getSelectedFile();

            if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                musicPlayer.stopSong();
                musicPlayer.loadPlaylist(selectedFile);
            }
        });
        playlistMenu.add(loadPlaylist);

        add(toolBar);
    }

    // Přidání ovládacích tlačítek pro přehrávání
    private void addPlaybackBtns() {
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbackBtns.setBackground(null);

        // Předchozí skladba
        JButton prevButton = new JButton(loadImage("src/assets/previous.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.addActionListener(e -> musicPlayer.prevSong());
        playbackBtns.add(prevButton);

        // Play
        JButton playButton = new JButton(loadImage("src/assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(e -> {
            enablePauseButtonDisablePlayButton();
            musicPlayer.playCurrentSong();
        });
        playbackBtns.add(playButton);

        // Pause
        JButton pauseButton = new JButton(loadImage("src/assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(e -> {
            enablePlayButtonDisablePauseButton();
            musicPlayer.pauseSong();
        });
        playbackBtns.add(pauseButton);

        // Další skladba
        JButton nextButton = new JButton(loadImage("src/assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.addActionListener(e -> musicPlayer.nextSong());
        playbackBtns.add(nextButton);

        add(playbackBtns);
    }

    // Aktualizace posuvníku z přehrávače
    public void setPlaybackSliderValue(int frame) {
        playbackSlider.setValue(frame);
    }

    // Aktualizace názvu a interpreta
    public void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    // Nastavení rozsahu posuvníku a časových popisků
    public void updatePlaybackSlider(Song song) {
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        JLabel startLabel = new JLabel("00:00");
        startLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        startLabel.setForeground(TEXT_COLOR);

        JLabel endLabel = new JLabel(song.getSongLength());
        endLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        endLabel.setForeground(TEXT_COLOR);

        labelTable.put(0, startLabel);
        labelTable.put(song.getMp3File().getFrameCount(), endLabel);
        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    // Přepne na pauzu (skryje play tlačítko)
    public void enablePauseButtonDisablePlayButton() {
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        playButton.setVisible(false);
        playButton.setEnabled(false);
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    // Přepne na play (skryje pause tlačítko)
    public void enablePlayButtonDisablePauseButton() {
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        playButton.setVisible(true);
        playButton.setEnabled(true);
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    // Načtení obrázku z cesty
    private ImageIcon loadImage(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
