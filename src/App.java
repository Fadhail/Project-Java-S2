import screen.TitleScreenGui;
import javax.swing.*;

public class it App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Display the TitleScreenGui
                new TitleScreenGui().setVisible(true);
            }
        });
    }
}
