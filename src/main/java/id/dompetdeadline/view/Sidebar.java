package id.dompetdeadline.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

final class Sidebar extends JPanel {
    private final MainFrame frame;
    Sidebar(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;
        setBackground(Theme.sidebar);
        setPreferredSize(new Dimension(224, 0));
    }

    void refreshNav() {
        removeAll();
        JPanel top=Ui.vertical();
        top.setBorder(new EmptyBorder(25, 17, 15, 17));
        JPanel brand=Ui.panel(new BorderLayout(10, 0));
        JLabel logo=new JLabel(new ImageIcon(frame.icon().getScaledInstance(43, 43, Image.SCALE_SMOOTH)));
        brand.add(logo, BorderLayout.WEST);
        JPanel names=Ui.vertical();
        names.add(Ui.label("dompet.", 24, true, Theme.ICE));
        names.add(Ui.label("D E A D L I N E", 10, true, Theme.SKY));
        brand.add(names);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        brand.setAlignmentX(0);
        top.add(brand);
        Ui.gap(top, 32);
        JLabel section=Ui.label("MENU UTAMA", 11, true, Theme.MIST);
        section.setAlignmentX(0);
        top.add(section);
        Ui.gap(top, 11);
        for(int i=0; i<MainFrame.PAGES.length; i++) {
            String name=MainFrame.PAGES[i];
            final int index=i;
            JButton b=new JButton(name) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D a=(Graphics2D)g.create();
                    a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    a.setColor(name.equals(frame.currentPage())||getModel().isRollover()?Theme.BLUE:Theme.sidebar);
                    a.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    if(name.equals(frame.currentPage())) {
                        a.setColor(Theme.SKY);
                        a.fillRoundRect(0, 8, 3, getHeight()-16, 3, 3);
                    }
                    a.setFont(getFont());
                    a.setColor(getForeground());
                    FontMetrics fm=a.getFontMetrics();
                    drawNavIcon(a,index,15,(getHeight()-20)/2);
                    a.drawString(getText(), 48,(getHeight()-fm.getHeight())/2+fm.getAscent());
                    if(isFocusOwner()) {
                        a.setColor(Theme.accent);
                        a.drawRoundRect(2, 2, getWidth()-5, getHeight()-5, 10, 10);
                    }
                    a.dispose();
                }
            };
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setFont(new Font("SansSerif", name.equals(frame.currentPage())?Font.BOLD:Font.PLAIN, 14));
            b.setForeground(name.equals(frame.currentPage())?Color.WHITE:Theme.ICE);
            b.setBackground(name.equals(frame.currentPage())?Theme.BLUE:Theme.sidebar);
            b.setOpaque(true);
            b.setContentAreaFilled(false);
            b.setBorder(new EmptyBorder(13, 13, 13, 10));
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 49));
            b.setAlignmentX(0);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.addActionListener(e->frame.navigate(name));
            top.add(b);
            Ui.gap(top, 5);
        }
        add(top, BorderLayout.NORTH);
        JPanel bottom=Ui.vertical();
        bottom.setBorder(new EmptyBorder(16, 18, 23, 18));
        JPanel local=Ui.panel(new BorderLayout());
        local.setOpaque(true); local.setBackground(Theme.BLUE);
        local.setBorder(new EmptyBorder(14, 13, 14, 13));
        JPanel localText=Ui.vertical();
        localText.add(Ui.label("●  Penyimpanan lokal", 12, true, Theme.ICE));
        Ui.gap(localText, 5);
        localText.add(Ui.label("Data tetap di perangkat", 11, false, Theme.SKY));
        local.add(localText);
        local.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        local.setAlignmentX(0);
        bottom.add(local);
        Ui.gap(bottom, 12);
        JButton theme=Ui.button(Theme.dark?"Mode terang":"Mode gelap", false, frame::toggleTheme);
        theme.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        theme.setAlignmentX(0);
        bottom.add(theme);
        Ui.gap(bottom, 15);
        bottom.add(Ui.label("Dompet Deadline  /  v1.0", 10, false, Theme.MIST));
        add(bottom, BorderLayout.SOUTH);
    }
    private static void drawNavIcon(Graphics2D g, int index, int x, int y) {
        Graphics2D a=(Graphics2D)g.create(); a.translate(x,y); a.setStroke(new BasicStroke(1.6f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        switch(index) {
            case 0: for(int i=0;i<2;i++)for(int j=0;j<2;j++)a.drawRoundRect(i*10,j*10,7,7,2,2); break;
            case 1: a.drawLine(1,6,18,6);a.drawLine(14,2,18,6);a.drawLine(18,6,14,10);a.drawLine(18,15,1,15);a.drawLine(5,11,1,15);a.drawLine(1,15,5,19);break;
            case 2: a.drawRoundRect(3,2,15,17,3,3);a.drawLine(6,10,9,13);a.drawLine(9,13,15,7);break;
            case 3: a.drawLine(1,19,19,19);a.drawRoundRect(3,10,3,6,1,1);a.drawRoundRect(9,6,3,10,1,1);a.drawRoundRect(15,1,3,15,1,1);break;
            default: a.drawOval(1,1,18,18);a.drawArc(5,5,10,10,0,270);a.drawLine(10,10,10,3);break;
        } a.dispose();
    }
}
