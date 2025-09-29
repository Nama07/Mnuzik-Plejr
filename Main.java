import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mnuzik Plejr");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            Knihovna library= new Knihovna ();
            ArrayList<String> songs = library.getSongs();
            int[] currentIndex = {0};
            JLabel songLabel = new JLabel("No song playing");
            songLabel.setHorizontalAlignment(SwingConstants.CENTER);
            songLabel.setText(songs.get(currentIndex[0]));

            JPanel control_panel = new JPanel();
            JButton playButton = new JButton("Play");
            JButton pauseButton = new JButton("Pause");
            JButton stopButton = new JButton("Stop");
            JButton nextButton = new JButton("Next");
            JButton prevButton = new JButton("Prev");

            control_panel.add(prevButton);
            control_panel.add(playButton);
            control_panel.add(pauseButton);
            control_panel.add(stopButton);
            control_panel.add(nextButton);

            songLabel.setHorizontalAlignment(SwingConstants.CENTER);

            frame.setLayout(new BorderLayout());
            frame.add(songLabel, BorderLayout.CENTER);
            frame.add(control_panel, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}
