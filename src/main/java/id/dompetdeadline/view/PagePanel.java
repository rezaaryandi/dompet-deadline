package id.dompetdeadline.view;

import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.util.function.Consumer;

abstract class PagePanel extends JPanel {
    protected final MainFrame frame;

    PagePanel(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;
        setOpaque(false);
    }

    /** Rebuild the visible page from the current immutable data snapshot. */
    final JPanel refreshPage() {
        removeAll();
        add(buildContent(), BorderLayout.CENTER);
        revalidate();
        repaint();
        return this;
    }

    protected abstract JPanel buildContent();

    protected final Long selected(JTable t) {
        int i=t.getSelectedRow();
        if(i<0) {
            frame.error("Pilih satu baris terlebih dahulu.");
            return null;
        }
        return(Long)t.getModel().getValueAt(t.convertRowIndexToModel(i), 0);
    }
    protected final JPanel monthControl(YearMonth month, Consumer<YearMonth> change) {
        JPanel p=Ui.panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JTextField f=Ui.field(month.toString(), 8);
        p.add(Ui.button("‹", false,()->change.accept(month.minusMonths(1))));
        JLabel label=Ui.label("Bulan (YYYY-MM)", 12, true, Theme.muted);
        label.setLabelFor(f);
        p.add(label);
        p.add(f);
        Runnable apply=()-> {
            try {
                change.accept(YearMonth.parse(f.getText().trim()));
            }
            catch(Exception e) {
                frame.error("Gunakan format bulan YYYY-MM, misalnya 2026-11.");
            }
        };
        p.add(Ui.button("Terapkan", false, apply));
        p.add(Ui.button("›", false,()->change.accept(month.plusMonths(1))));
        f.addActionListener(e->apply.run());
        return p;
    }
}
