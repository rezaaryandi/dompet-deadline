package id.dompetdeadline.view;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import id.dompetdeadline.service.Finance;
public final class BarChart extends JPanel implements Scrollable {
    private final Map<String, Long> values;
    public BarChart(Map<String, Long> v) {
        values=new LinkedHashMap<>(v);
        setOpaque(false);
        setPreferredSize(new Dimension(360, Math.max(172, values.size()*36+30)));
    }
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
        return 16;
    }
    public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
        return Math.max(16, r.height-16);
    }
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }
    public boolean getScrollableTracksViewportHeight() {
        return getParent()!=null&&getParent().getHeight()>=getPreferredSize().height;
    }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D a=(Graphics2D)g.create();
        a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        a.setFont(new Font("SansSerif", Font.PLAIN, 12));
        int w=getWidth(), h=getHeight();
        if(values.isEmpty()) {
            a.setColor(Theme.muted);
            String s="Belum ada pengeluaran pada bulan ini.";
            a.drawString(s, Math.max(8,(w-a.getFontMetrics().stringWidth(s))/2), h/2);
            a.dispose();
            return;
        }
        int left=Math.min(140, w/3), right=Math.max(91, a.getFontMetrics().stringWidth(Finance.rupiah(Collections.max(values.values())))+16), available=Math.max(1, w-left-right-15), top=12, step=Math.max(34,(h-42)/values.size());
        long max=Collections.max(values.values());
        for(int i=0; i<=4; i++) {
            int x=left+available*i/4;
            a.setColor(Theme.line);
            a.drawLine(x, top, x, Math.min(h-30, top+step*values.size()));
        }
        int i=0;
        for(Map.Entry<String, Long> entry:values.entrySet()) {
            int y=top+i++*step;
            String name=entry.getKey();
            while(a.getFontMetrics().stringWidth(name)>left-12&&name.length()>2)name=name.substring(0, name.length()-2)+"…";
            a.setColor(Theme.ink);
            a.drawString(name, 4, y+22);
            int bw=(int)Math.max(3, java.math.BigInteger.valueOf(entry.getValue()).multiply(java.math.BigInteger.valueOf(available)).divide(java.math.BigInteger.valueOf(max)).longValue());
            a.setColor(Theme.accentSoft);
            a.fillRoundRect(left, y+8, available, 23, 8, 8);
            Color[] colors=Theme.dark?new Color[]{Theme.SKY,Theme.MIST,Theme.TEAL,Theme.STEEL}:new Color[]{Theme.BLUE,Theme.STEEL,Theme.TEAL,Theme.MIST};
            a.setPaint(new GradientPaint(left,y,colors[(i-1)%colors.length],left+bw,y,Theme.mix(colors[(i-1)%colors.length],Theme.SKY,25)));
            a.fillRoundRect(left, y+8, bw, 23, 8, 8);
            a.setColor(Theme.ink);
            a.drawString(Finance.rupiah(entry.getValue()), left+available+9, y+24);
        }
        a.setColor(Theme.muted);
        a.drawString("Rp0", left, h-9);
        String m=Finance.rupiah(max);
        a.drawString(m, left+available-a.getFontMetrics().stringWidth(m), h-9);
        a.dispose();
    }
}
