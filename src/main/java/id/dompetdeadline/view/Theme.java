package id.dompetdeadline.view;
import java.awt.*;
import javax.swing.*;
import java.util.prefs.Preferences;
public final class Theme {
    private static final Preferences PREF=Preferences.userNodeForPackage(Theme.class);
    public static final Color NAVY=c(0x001D39), BLUE=c(0x0A4174), STEEL=c(0x49769F), TEAL=c(0x4E8EA2), MIST=c(0x6EA2B3), SKY=c(0x7BBDE8), ICE=c(0xBDD8E9);
    public static boolean dark;
    public static Color mix(Color a, Color b, int pct) {
        return new Color((a.getRed()*(100-pct)+b.getRed()*pct)/100,(a.getGreen()*(100-pct)+b.getGreen()*pct)/100,(a.getBlue()*(100-pct)+b.getBlue()*pct)/100);
    }
    public static Color bg, surface, sidebar, ink, muted, line, accent, accentSoft, green, red, amber;
    public static boolean saved() {
        return PREF.getBoolean("dark", false);
    }
    public static void apply(boolean value, boolean persist) {
        dark=value;
        if(persist)PREF.putBoolean("dark", value);
        try {
            UIManager.setLookAndFeel((LookAndFeel)Class.forName(value?"com.formdev.flatlaf.FlatDarkLaf":"com.formdev.flatlaf.FlatLightLaf").getDeclaredConstructor().newInstance());
        }
        catch(Exception e) {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            }
            catch(Exception ignored) {
            }
        }
        // Seven reference colors; neutral surfaces are tints of the same blue family.
        bg=value?NAVY:mix(ICE, Color.WHITE, 83);
        surface=value?mix(NAVY, BLUE, 26):mix(ICE, Color.WHITE, 96);
        sidebar=NAVY;
        ink=value?ICE:NAVY;
        muted=value?SKY:STEEL;
        line=value?mix(BLUE, STEEL, 32):mix(ICE, Color.WHITE, 30);
        accent=value?SKY:BLUE;
        accentSoft=value?BLUE:mix(ICE, Color.WHITE, 36);
        green=value?SKY:BLUE;
        red=value?ICE:NAVY;
        amber=value?MIST:BLUE;
        UIManager.put("defaultFont", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.focusColor", SKY);
        UIManager.put("Component.borderColor", line);
        UIManager.put("Component.focusedBorderColor", accent);
        UIManager.put("TextArea.background", surface);
        UIManager.put("TextArea.foreground", ink);
        UIManager.put("MenuBar.background", surface);
        UIManager.put("Menu.foreground", ink);
        UIManager.put("MenuItem.foreground", ink);
        UIManager.put("PopupMenu.background", surface);
        UIManager.put("OptionPane.background", surface);
        UIManager.put("OptionPane.messageForeground", ink);
        UIManager.put("CheckBox.foreground", ink);
        UIManager.put("TextField.caretForeground", ink);
        UIManager.put("TextField.selectionBackground", accentSoft);
        UIManager.put("ProgressBar.foreground", accent);
        UIManager.put("ProgressBar.background", accentSoft);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("Panel.background", surface);
        UIManager.put("Label.foreground", ink);
        UIManager.put("TextField.background", surface);
        UIManager.put("TextField.foreground", ink);
        UIManager.put("ComboBox.background", surface);
        UIManager.put("ComboBox.foreground", ink);
        UIManager.put("Table.background", surface);
        UIManager.put("Table.foreground", ink);
        UIManager.put("Table.selectionBackground", accentSoft);
        UIManager.put("Table.selectionForeground", ink);
        UIManager.put("Table.gridColor", line);
        UIManager.put("TableHeader.background", bg);
        UIManager.put("TableHeader.foreground", muted);
    }
    private static Color c(int n) {
        return new Color(n);
    }
}
