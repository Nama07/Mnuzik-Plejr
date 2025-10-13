import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame {
    // nastavení barev
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    public MusicPlayerGUI() {
        super("Music Player");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        getContentPane().setBackground(FRAME_COLOR);

        addGuiComponents();
    }

    private void addGuiComponents() {
        addToolbar();

        // Obrázek
        JLabel songImage = new JLabel(loadImage("src/assets/record.png"));
        songImage.setBounds(50, 50, 300, 300); 
        add(songImage);

        JLabel songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 370, 400, 30); 
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        JLabel songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 400, 400, 30);
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(50, 370, 300, 30); 
        playbackSlider.setBackground(FRAME_COLOR);
        playbackSlider.setForeground(TEXT_COLOR);
        playbackSlider.setPaintTicks(true);
        playbackSlider.setPaintLabels(false);
        playbackSlider.setMajorTickSpacing(10);
        add(playbackSlider);

        addPlaybackBtns();
    }

    // Toolbar 
    private void addToolbar() {

        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);
        toolBar.setFloatable(false);

        // Drop menu 
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        // Music menu
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        // Načtení songu
        JMenuItem loadSong = new JMenuItem("Load");
        songMenu.add(loadSong);

        // Playlist menu
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        playlistMenu.add(loadPlaylist);

        add(toolBar);
    }

private void addPlaybackBtns() {
    JPanel playbackBtns = new JPanel();
    playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
    playbackBtns.setBackground(null);

    // Předchozí tlačítko
    JButton prevButton = new JButton(loadImage("src/assets/previous.png"));
    prevButton.setBorderPainted(false);
    prevButton.setBackground(null);
    playbackBtns.add(prevButton);

    // Play tlačítko
    JButton playButton = new JButton(loadImage("src/assets/play.png"));
    playButton.setBorderPainted(false);
    playButton.setBackground(null);
    playbackBtns.add(playButton);

    // Pause tlačítko
    JButton pauseButton = new JButton(loadImage("src/assets/pause.png"));
    pauseButton.setBorderPainted(false);
    pauseButton.setBackground(null);
    playbackBtns.add(pauseButton);

    // Next tlačítko 
    JButton nextButton = new JButton(loadImage("src/assets/next.png"));
    nextButton.setBorderPainted(false);
    nextButton.setBackground(null);
    playbackBtns.add(nextButton);

    add(playbackBtns);
}



    private ImageIcon loadImage(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MusicPlayerGUI().setVisible(true));
    }
}
