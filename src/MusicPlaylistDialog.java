import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class MusicPlaylistDialog extends JDialog {
    private MusicPlayerGUI musicPlayerGUI;
    private ArrayList<String> songPaths; // seznam cest k písním

    public MusicPlaylistDialog(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
        songPaths = new ArrayList<>();

        // Nastavení okna
        setTitle("Vytvořit playlist");
        setSize(400, 400);
        setResizable(false);
        getContentPane().setBackground(MusicPlayerGUI.FRAME_COLOR);
        setLayout(null);
        setModal(true);
        setLocationRelativeTo(musicPlayerGUI);

        addDialogComponents();
    }

    // Vytvoření grafických prvků dialogu
    private void addDialogComponents() {
        // Panel, kde se zobrazí seznam přidaných skladeb
        JPanel songContainer = new JPanel();
        songContainer.setLayout(new BoxLayout(songContainer, BoxLayout.Y_AXIS));
        songContainer.setBounds((int) (getWidth() * 0.025), 10, (int) (getWidth() * 0.90), (int) (getHeight() * 0.75));
        add(songContainer);

        // Tlačítko "Add" – přidá skladbu do seznamu
        JButton addSongButton = new JButton("Přidat");
        addSongButton.setBounds(60, (int) (getHeight() * 0.80), 100, 25);
        addSongButton.setFont(new Font("Dialog", Font.BOLD, 14));
        addSongButton.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
            jFileChooser.setCurrentDirectory(new File("src/assets"));
            int result = jFileChooser.showOpenDialog(MusicPlaylistDialog.this);

            File selectedFile = jFileChooser.getSelectedFile();
            if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                JLabel filePathLabel = new JLabel(selectedFile.getPath());
                filePathLabel.setFont(new Font("Dialog", Font.BOLD, 12));
                filePathLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                songPaths.add(filePathLabel.getText());
                songContainer.add(filePathLabel);
                songContainer.revalidate();
            }
        });
        add(addSongButton);

        // Tlačítko "Uložit" – uloží seznam skladeb do textového souboru
        JButton savePlaylistButton = new JButton("Uložit");
        savePlaylistButton.setBounds(215, (int) (getHeight() * 0.80), 100, 25);
        savePlaylistButton.setFont(new Font("Dialog", Font.BOLD, 14));
        savePlaylistButton.addActionListener(e -> {
            try {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setCurrentDirectory(new File("src/assets"));
                int result = jFileChooser.showSaveDialog(MusicPlaylistDialog.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jFileChooser.getSelectedFile();

                    // Automatické doplnění .txt přípony
                    if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                        selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                    }

                    selectedFile.createNewFile();

                    // Zápis všech cest do souboru
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(selectedFile))) {
                        for (String songPath : songPaths) {
                            bw.write(songPath + "\n");
                        }
                    }

                    JOptionPane.showMessageDialog(MusicPlaylistDialog.this, "Playlist byl úspěšně vytvořen!");
                    dispose();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        add(savePlaylistButton);
    }
}
