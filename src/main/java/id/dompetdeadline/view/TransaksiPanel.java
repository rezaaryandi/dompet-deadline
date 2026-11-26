package id.dompetdeadline.view;

import id.dompetdeadline.model.Kategori;
import id.dompetdeadline.model.Transaksi;
import id.dompetdeadline.service.Finance;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Locale;

final class TransaksiPanel extends PagePanel {
    private String transactionSearch = "", transactionType = "Semua jenis";
    TransaksiPanel(MainFrame frame) { super(frame); }

    private Transaksi selectedTransaction(JTable t) {
        Long id=selected(t);
        return id==null?null:frame.data().transaksi.stream().filter(x->x.id==id).findFirst().orElse(null);
    }
    @Override protected JPanel buildContent() {
        JPanel p=Ui.card(new BorderLayout(0, 13));
        JPanel tools=Ui.panel(new BorderLayout());
        JPanel filters=Ui.panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JTextField search=Ui.field(transactionSearch, 16);
        search.setToolTipText("Cari transaksi");
        JComboBox<String> type=Ui.combo(new String[] {
            "Semua jenis", "Pemasukan", "Pengeluaran"
        });
        type.setSelectedItem(transactionType);
        JLabel searchLabel=Ui.label("Cari", 12, true, Theme.muted);
        searchLabel.setLabelFor(search);
        filters.add(searchLabel);
        filters.add(search);
        type.getAccessibleContext().setAccessibleName("Filter jenis transaksi");
        type.setToolTipText("Filter jenis transaksi");
        filters.add(type);
        tools.add(filters);
        JPanel btn=Ui.panel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        btn.add(Ui.button("Impor CSV", false, frame::importCsv));
        btn.add(Ui.button("Ekspor CSV", false, frame::exportCsv));
        tools.add(btn, BorderLayout.EAST);
        p.add(tools, BorderLayout.NORTH);
        DefaultTableModel m=Ui.model(new String[] {
            "ID", "Tanggal", "Kategori", "Jenis", "Jumlah", "Tugas terkait", "Catatan"
        }, new Class[] {
            Long.class, LocalDate.class, String.class, String.class, Long.class, String.class, String.class
        });
        JTable t=Ui.table(m, 4);
        JLabel count=Ui.label("", 12, true, Theme.muted);
        Runnable fill=()-> {
            transactionSearch=search.getText();
            transactionType=(String)type.getSelectedItem();
            m.setRowCount(0);
            String q=transactionSearch.toLowerCase(Locale.ROOT);
            for(Transaksi x:frame.data().transaksi) {
                Kategori k=frame.data().kategori(x.kategoriId);
                String task=frame.data().judul(x.tugasId);
                if((transactionType.equals("Semua jenis")||transactionType.equals(k.jenis))&&(k.nama+" "+task+" "+x.catatan+" "+x.tanggal+" "+x.jumlah).toLowerCase(Locale.ROOT).contains(q))m.addRow(new Object[] {
                    x.id, x.tanggal, k.nama, k.jenis, x.jumlah, task, x.catatan
                });
            }
            count.setText(m.getRowCount()+" dari "+frame.data().transaksi.size()+" transaksi");
        };
        fill.run();
        Ui.changes(search, fill);
        type.addActionListener(e->fill.run());
        p.add(Ui.scroll(t));
        JPanel bottom=Ui.panel(new BorderLayout());
        bottom.add(count);
        t.setToolTipText("Klik judul kolom untuk mengurutkan");
        JPanel a=Ui.panel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        a.add(Ui.button("Ubah", false,()-> {
            Transaksi x=selectedTransaction(t);
            if(x!=null)frame.editTransaksi(x);
        }));
        a.add(Ui.button("Hapus", false,()-> {
            Transaksi x=selectedTransaction(t);
            if(x!=null&&frame.confirm("Hapus transaksi "+Finance.rupiah(x.jumlah)+"?"))frame.deleteTransaksi(x.id);
        }));
        bottom.add(a, BorderLayout.EAST);
        p.add(bottom, BorderLayout.SOUTH);
        t.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2) {
                    Transaksi x=selectedTransaction(t);
                    if(x!=null)frame.editTransaksi(x);
                }
            }
        });
        return p;
    }
}
