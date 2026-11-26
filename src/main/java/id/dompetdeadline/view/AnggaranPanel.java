package id.dompetdeadline.view;

import id.dompetdeadline.model.Anggaran;
import id.dompetdeadline.service.Finance;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.YearMonth;

final class AnggaranPanel extends PagePanel {
    private YearMonth budgetMonth = YearMonth.now();
    YearMonth selectedMonth() { return budgetMonth; }
    void selectMonth(YearMonth month) { budgetMonth = month; }
    AnggaranPanel(MainFrame frame) { super(frame); }

    private Anggaran selectedBudget(JTable t) {
        Long id=selected(t);
        return id==null?null:frame.data().anggaran.stream().filter(x->x.kategoriId==id&&x.bulan.equals(budgetMonth)).findFirst().orElse(null);
    }
    @Override protected JPanel buildContent() {
        JPanel p=Ui.panel(new BorderLayout(0, 14));
        p.add(monthControl(budgetMonth, m-> {
            budgetMonth=m;
            frame.render();
        }), BorderLayout.NORTH);
        JPanel card=Ui.card(new BorderLayout(0, 12));
        card.add(Ui.label("Batas pengeluaran per kategori", 17, true, Theme.ink), BorderLayout.NORTH);
        DefaultTableModel m=Ui.model(new String[] {
            "ID", "Kategori", "Batas", "Terpakai", "Sisa", "Progres", "Status"
        }, new Class[] {
            Long.class, String.class, Long.class, Long.class, Long.class, Integer.class, String.class
        });
        JTable t=Ui.table(m, 2, 3, 4);
        for(Anggaran a:frame.data().anggaran)if(a.bulan.equals(budgetMonth)) {
            long used=Finance.spent(frame.data(), budgetMonth, a.kategoriId);
            int percent=java.math.BigInteger.valueOf(used).multiply(java.math.BigInteger.valueOf(100)).divide(java.math.BigInteger.valueOf(a.batas)).min(java.math.BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
            m.addRow(new Object[] {
                a.kategoriId, frame.data().kategori(a.kategoriId).nama, a.batas, used, a.batas-used, percent, Finance.budgetStatus(used, a.batas)
            });
        }
        t.getColumnModel().getColumn(4).setCellRenderer((table, v, selected, focus, row, col)-> {
            int n=(Integer)v;
            JProgressBar b=new JProgressBar(0, 100);
            b.setValue(Math.min(n, 100));
            b.setString(n+"%");
            b.setStringPainted(true);
            b.setForeground(n>=100?Theme.red:n>=80?Theme.amber:Theme.green);
            b.setBorder(new EmptyBorder(6, 5, 6, 5));
            return b;
        });
        card.add(Ui.scroll(t));
        JPanel bottom=Ui.panel(new BorderLayout());
        bottom.add(Ui.label("Peringatan mulai 80% · batas tercapai pada 100%.", 11, false, Theme.muted));
        JPanel a=Ui.panel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        a.add(Ui.button("Ubah", false,()-> {
            Anggaran x=selectedBudget(t);
            if(x!=null)frame.editBudget(x);
        }));
        a.add(Ui.button("Hapus", false,()-> {
            Anggaran x=selectedBudget(t);
            if(x!=null&&frame.confirm("Hapus batas anggaran ini?"))frame.deleteBudget(x);
        }));
        bottom.add(a, BorderLayout.EAST);
        card.add(bottom, BorderLayout.SOUTH);
        p.add(card);
        return p;
    }
}
