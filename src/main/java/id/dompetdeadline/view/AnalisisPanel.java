package id.dompetdeadline.view;

import id.dompetdeadline.service.Finance;
import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;

final class AnalisisPanel extends PagePanel {
    private YearMonth chartMonth = YearMonth.now();
    AnalisisPanel(MainFrame frame) { super(frame); }

    @Override protected JPanel buildContent() {
        JPanel p=Ui.panel(new BorderLayout(0, 14));
        p.add(monthControl(chartMonth, m-> {
            chartMonth=m;
            frame.render();
        }), BorderLayout.NORTH);
        JPanel card=Ui.card(new BorderLayout(0, 10));
        JPanel h=Ui.panel(new BorderLayout());
        h.add(Ui.label("Pengeluaran menurut kategori", 18, true, Theme.ink));
        h.add(Ui.label(Finance.rupiah(Finance.total(frame.data(), chartMonth, true)), 20, true, Theme.accent), BorderLayout.EAST);
        card.add(h, BorderLayout.NORTH);
        card.add(Ui.scroll(new BarChart(Finance.byCategory(frame.data(), chartMonth))));
        card.add(Ui.label("Grafik berdasarkan transaksi pengeluaran pada bulan terpilih.", 12, false, Theme.muted), BorderLayout.SOUTH);
        p.add(card);
        return p;
    }
}
