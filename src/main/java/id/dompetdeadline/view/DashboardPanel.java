package id.dompetdeadline.view;

import id.dompetdeadline.model.Anggaran;
import id.dompetdeadline.model.Tugas;
import id.dompetdeadline.service.Finance;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class DashboardPanel extends PagePanel {
    private static final DateTimeFormatter DEADLINE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    DashboardPanel(MainFrame frame) { super(frame); }

    private JPanel stat(String title, String value, String note, Color color) {
        JPanel p=Ui.card(new BorderLayout(0, 7));
        p.add(Ui.label(title, 13, true, Theme.muted), BorderLayout.NORTH);
        JLabel amount=Ui.label(value, 23, true, color);
        if(value.length()>14)amount.setFont(amount.getFont().deriveFont(18f));
        p.add(amount, BorderLayout.CENTER);
        p.add(Ui.label(note, 11, false, Theme.muted), BorderLayout.SOUTH);
        return p;
    }
    private JPanel balanceCard(YearMonth now, long balance) {
        JPanel card=new JPanel(new BorderLayout(0,7)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D a=(Graphics2D)g.create();
                a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                a.setPaint(new GradientPaint(0,0,Theme.NAVY,getWidth(),getHeight(),Theme.BLUE));
                a.fillRoundRect(0,0,getWidth(),getHeight(),24,24);
                a.clip(new java.awt.geom.RoundRectangle2D.Float(0,0,getWidth(),getHeight(),24,24));
                a.setColor(Theme.mix(Theme.BLUE,Theme.STEEL,38)); a.setStroke(new BasicStroke(1.2f));
                for(int i=0;i<5;i++)a.drawOval(getWidth()-116-i*15,-70-i*10,220+i*24,220+i*24);
                a.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false); card.setBorder(new EmptyBorder(21,23,20,23));
        card.add(Ui.label("SISA UANG  /  BULAN INI",11,true,Theme.SKY),BorderLayout.NORTH);
        JLabel value=Ui.label(Finance.rupiah(balance),32,true,Color.WHITE);
        if(value.getText().length()>15)value.setFont(value.getFont().deriveFont(22f));
        card.add(value,BorderLayout.CENTER);
        card.add(Ui.label(balance<0?"Pengeluaran melebihi pemasukan":"Pemasukan dikurangi pengeluaran",11,false,Theme.ICE),BorderLayout.SOUTH);
        return card;
    }
    @Override protected JPanel buildContent() {
        YearMonth now=YearMonth.now();
        JPanel p=Ui.panel(new BorderLayout(0, 17));
        JPanel top=Ui.panel(new BorderLayout(0, 12));
        JPanel period=Ui.panel(new BorderLayout());
        period.add(Ui.label("IKHTISAR BULAN INI", 11, true, Theme.muted));
        period.add(Ui.label(now.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("id-ID"))), 13,true,Theme.ink),BorderLayout.EAST);
        top.add(period, BorderLayout.NORTH);
        JPanel cards=Ui.panel(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints(); c.gridy=0;c.fill=GridBagConstraints.BOTH;c.weighty=1;c.insets=new Insets(0,0,0,12);
        c.gridx=0;c.weightx=1.55;cards.add(balanceCard(now,Finance.balance(frame.data(),now)),c);
        c.gridx=1;c.weightx=1;cards.add(stat("Pemasukan",Finance.rupiah(Finance.total(frame.data(),now,false)),"Total bulan ini",Theme.ink),c);
        c.gridx=2;cards.add(stat("Pengeluaran",Finance.rupiah(Finance.total(frame.data(),now,true)),"Total bulan ini",Theme.ink),c);
        c.gridx=3;c.insets=new Insets(0,0,0,0);cards.add(stat("Biaya tugas aktif",Finance.rupiah(Finance.activeCost(frame.data())),"Seluruh periode",Theme.accent),c);
        top.add(cards); p.add(top,BorderLayout.NORTH);
        JPanel grid=Ui.panel(new GridLayout(1, 2, 13, 0));
        JPanel chart=Ui.card(new BorderLayout(0, 8));
        JPanel h=Ui.vertical();
        h.add(Ui.label("Pengeluaran per kategori", 17, true, Theme.ink));
        Ui.gap(h, 4);
        h.add(Ui.label("Arus pengeluaran bulan ini", 12, false, Theme.muted));
        chart.add(h, BorderLayout.NORTH);
        chart.add(Ui.scroll(new BarChart(Finance.byCategory(frame.data(), now))));
        chart.add(Ui.button("Lihat analisis lengkap", false,()->frame.navigate("Analisis")), BorderLayout.SOUTH);
        grid.add(chart);
        JPanel right=Ui.panel(new BorderLayout(0, 12));
        JPanel due=Ui.card(new BorderLayout(0, 12));
        JPanel dh=Ui.vertical();
        dh.add(Ui.label("Tenggat terdekat", 17, true, Theme.ink));
        Ui.gap(dh, 4);
        dh.add(Ui.label("Tiga tugas yang belum selesai", 12, false, Theme.muted));
        due.add(dh, BorderLayout.NORTH);
        JPanel list=Ui.vertical();
        List<Tugas> upcoming=Finance.upcoming(frame.data(), 3);
        if(upcoming.isEmpty()) {
            list.add(Ui.label("Belum ada tugas aktif.", 13, false, Theme.muted));
        }
        for(Tugas t:upcoming) {
            JPanel row=Ui.panel(new BorderLayout(10, 0));
            row.setBorder(new EmptyBorder(4, 0, 4, 0));
            JPanel details=Ui.vertical();
            details.add(Ui.label(t.judul, 14, true, Theme.ink));
            Ui.gap(details, 1);
            details.add(Ui.label(t.mataKuliah, 11, false, Theme.muted));
            details.add(Ui.label(t.tenggat.format(DEADLINE), 11, false, Theme.muted));
            JPanel badge=Ui.panel(new GridBagLayout()); badge.setOpaque(true);badge.setBackground(Theme.accentSoft);
            badge.setPreferredSize(new Dimension(42,38));
            JPanel date=Ui.vertical(); JLabel day=Ui.label(String.format("%02d",t.tenggat.getDayOfMonth()),17,true,Theme.accent);day.setAlignmentX(.5f);date.add(day);
            JLabel month=Ui.label(t.tenggat.format(DateTimeFormatter.ofPattern("MMM",Locale.forLanguageTag("id-ID"))).toUpperCase(Locale.ROOT),9,true,Theme.ink);month.setAlignmentX(.5f);date.add(month);badge.add(date);
            row.add(badge,BorderLayout.WEST);row.add(details);
            row.add(Ui.label(t.tenggat.isBefore(LocalDateTime.now())?"Terlambat":t.prioritas, 11, true, t.prioritas.equals("Tinggi")||t.tenggat.isBefore(LocalDateTime.now())?Theme.red:t.prioritas.equals("Sedang")?Theme.amber:Theme.green), BorderLayout.EAST);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
            row.setAlignmentX(0);
            list.add(row);
            JSeparator s=new JSeparator();
            s.setForeground(Theme.line);
            s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            list.add(s);
        }
        JScrollPane dueScroll=Ui.scroll(list);dueScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        due.add(dueScroll);
        due.add(Ui.button("Kelola tugas", false,()->frame.navigate("Tugas kuliah")), BorderLayout.SOUTH);
        right.add(due);
        grid.add(right);
        p.add(grid);
        JPanel alerts=Ui.card(new BorderLayout(10, 0));
        alerts.setBorder(new EmptyBorder(12,22,12,22));
        List<Anggaran> warning=new ArrayList<>();
        for(Anggaran a:frame.data().anggaran)if(a.bulan.equals(now)&&!Finance.budgetStatus(Finance.spent(frame.data(), now, a.kategoriId), a.batas).equals("Aman"))warning.add(a);
        alerts.add(Ui.label(warning.isEmpty()?(frame.data().anggaran.stream().noneMatch(a->a.bulan.equals(now))?"Belum ada anggaran bulan ini":"Anggaran terkendali"):"Perhatian: "+warning.size()+" anggaran mendekati atau mencapai batas", 13, true, warning.isEmpty()?Theme.green:Theme.amber));
        alerts.add(Ui.button("Lihat anggaran", false,()->frame.navigate("Anggaran")), BorderLayout.EAST);
        p.add(alerts, BorderLayout.SOUTH);
        return p;
    }
}
