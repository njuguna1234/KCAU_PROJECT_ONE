package pharmacy;

import pharmacy.ui.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try {
            // Prepare the database before opening the app.
            DatabaseInitializer.initialize();

            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                    );
                } catch (Exception ignored) {
                    // Keep the default Swing appearance.
                }

                new LoginFrame().setVisible(true);
            });

        } catch (SQLException ex) {
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(
                    null,
                    "The database could not be initialized.\n\n" +
                    "Check that MySQL is running, the database " +
                    "exists, and the application credentials are correct.\n\n" +
                    "Details: " + ex.getMessage(),
                    "Database startup error",
                    JOptionPane.ERROR_MESSAGE
                )
            );
        }
    }
}