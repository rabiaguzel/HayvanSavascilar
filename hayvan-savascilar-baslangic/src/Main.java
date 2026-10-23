import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Oyunun başlangıç noktası. Bu dosyayı çalıştırın. (Değiştirmeyin.)
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame pencere = new JFrame("Hayvan Savaşçılar");
                OyunPaneli panel = new OyunPaneli();
                pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                pencere.setResizable(false);
                pencere.add(panel);
                pencere.pack();
                pencere.setLocationRelativeTo(null);
                pencere.setVisible(true);
            }
        });
    }
}
