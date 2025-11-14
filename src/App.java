import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Spuštění aplikace v rámci GUI vlákna Swingu (bezpečný způsob pro práci s grafickým rozhraním)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Vytvoření a zobrazení hlavního okna přehrávače
                new MusicPlayerGUI().setVisible(true);
            }
        });
    }
}
