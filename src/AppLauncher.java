import javax.swing.SwingUtilities;

public class AppLauncher {
    public static void main(String[] args) {
        // Ensure that Swing-related code is executed on the Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Place your GUI initialization and other Swing code here
            }
        });
    }
}
