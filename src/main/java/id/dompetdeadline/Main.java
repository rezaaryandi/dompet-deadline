package id.dompetdeadline;
import id.dompetdeadline.repository.*;
import id.dompetdeadline.view.*;
import id.dompetdeadline.service.ApplicationService;
import javax.swing.*;
public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {
            Theme.apply(Theme.saved(), false);
            JWindow splash=new JWindow();
            JLabel text=new JLabel("  Dompet Deadline  •  Menyiapkan database…  ");
            text.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
            splash.add(text);
            splash.pack();
            splash.setLocationRelativeTo(null);
            splash.setVisible(true);
            new SwingWorker<Repository, Void>() {
                protected Repository doInBackground()throws Exception {
                    Database d=Database.userDatabase();
                    d.initialize();
                    return new SqliteRepository(d);
                }
                protected void done() {
                    splash.dispose();
                    try {
                        new MainFrame(new ApplicationService(get())).setVisible(true);
                    }
                    catch(Exception e) {
                        JOptionPane.showMessageDialog(null, "Database tidak dapat dibuka.\n"+e.getMessage()+"\nPastikan JAR dibangun dengan Maven.", "Gagal memulai", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
    }
}
