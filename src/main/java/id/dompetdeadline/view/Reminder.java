package id.dompetdeadline.view;
import id.dompetdeadline.model.Tugas;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
final class Reminder {
    private TrayIcon tray;
    Reminder() {
        if(SystemTray.isSupported())try {
            BufferedImage img=new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g=img.createGraphics();
            g.setColor(Theme.BLUE);
            g.fillRoundRect(0, 0, 32, 32, 10, 10);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            g.drawString("D", 8, 23);
            g.dispose();
            tray=new TrayIcon(img, "Dompet Deadline");
            tray.setImageAutoSize(true);
            SystemTray.getSystemTray().add(tray);
        }
        catch(AWTException|RuntimeException e) {
            tray=null;
        }
    }
    void show(JFrame frame, Tugas t) {
        String msg=t.judul+" — tenggat "+t.tenggat.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        if(tray!=null) {
            try {
                tray.displayMessage("Tenggat dalam 24 jam", msg, TrayIcon.MessageType.WARNING);
                return;
            }
            catch(RuntimeException ignored) {
            }
        }
        JOptionPane.showMessageDialog(frame, msg, "Pengingat tenggat", JOptionPane.WARNING_MESSAGE);
    }
    void close() {
        if(tray!=null)SystemTray.getSystemTray().remove(tray);
    }
}
