package id.dompetdeadline.view;

import id.dompetdeadline.model.Tugas;
import id.dompetdeadline.service.Finance;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

final class TugasPanel extends PagePanel {
    private static final DateTimeFormatter DEADLINE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private String taskSearch = "", taskStatus = "Semua status";
    TugasPanel(MainFrame frame) { super(frame); }

    private Tugas selectedTask(JTable t) {
        Long id=selected(t);
        return id==null?null:frame.data().tugas(id);
    }
    @Override protected JPanel buildContent() {
        JPanel p=Ui.card(new BorderLayout(0, 13));
        JPanel filter=Ui.panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JTextField search=Ui.field(taskSearch, 19);
        JComboBox<String> statusBox=Ui.combo(new String[] {
            "Semua status", "Aktif", "Selesai", "Terlambat", "Prioritas tinggi"
        });
        statusBox.setSelectedItem(taskStatus);
        JLabel searchLabel=Ui.label("Cari", 12, true, Theme.muted);
        searchLabel.setLabelFor(search);
        filter.add(searchLabel);
        filter.add(search);
        statusBox.getAccessibleContext().setAccessibleName("Filter status tugas");
        statusBox.setToolTipText("Filter status tugas");
        filter.add(statusBox);
        p.add(filter, BorderLayout.NORTH);
        DefaultTableModel m=Ui.model(new String[] {
            "ID", "Judul tugas", "Mata kuliah", "Tenggat", "Prioritas", "Status", "Total biaya"
        }, new Class[] {
            Long.class, String.class, String.class, LocalDateTime.class, String.class, String.class, Long.class
        });
        JTable t=Ui.table(m, 6);
        t.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override protected void setValue(Object v) {
                setText(v==null?"":((LocalDateTime)v).format(DEADLINE));
            }
        });
        t.getColumnModel().getColumn(0).setPreferredWidth(220);
        t.getColumnModel().getColumn(1).setPreferredWidth(240);
        t.getColumnModel().getColumn(2).setPreferredWidth(150);
        t.getColumnModel().getColumn(3).setPreferredWidth(80);
        t.getColumnModel().getColumn(4).setPreferredWidth(75);
        t.getColumnModel().getColumn(5).setPreferredWidth(105);
        JLabel count=Ui.label("", 12, true, Theme.muted);
        Runnable fill=()-> {
            taskSearch=search.getText();
            taskStatus=(String)statusBox.getSelectedItem();
            m.setRowCount(0);
            String q=taskSearch.toLowerCase(Locale.ROOT);
            for(Tugas x:frame.data().tugas) {
                String st=x.selesai?"Selesai":x.tenggat.isBefore(LocalDateTime.now())?"Terlambat":"Aktif";
                boolean ok=taskStatus.equals("Semua status")||taskStatus.equals(st)||(taskStatus.equals("Aktif")&&!x.selesai)||(taskStatus.equals("Prioritas tinggi")&&x.prioritas.equals("Tinggi"));
                if(ok&&(x.judul+" "+x.mataKuliah+" "+x.prioritas).toLowerCase(Locale.ROOT).contains(q))m.addRow(new Object[] {
                    x.id, x.judul, x.mataKuliah, x.tenggat, x.prioritas, st, Finance.cost(frame.data(), x.id)
                });
            }
            count.setText(m.getRowCount()+" dari "+frame.data().tugas.size()+" tugas");
        };
        fill.run();
        Ui.changes(search, fill);
        statusBox.addActionListener(e->fill.run());
        p.add(Ui.scroll(t));
        JPanel bottom=Ui.panel(new BorderLayout());
        bottom.add(count);
        t.getAccessibleContext().setAccessibleDescription("Total biaya dihitung dari transaksi pengeluaran terkait.");
        JPanel a=Ui.panel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        a.add(Ui.button("Ubah status", true,()-> {
            Tugas x=selectedTask(t);
            if(x!=null)frame.toggleTask(x);
        }));
        a.add(Ui.button("Ubah", false,()-> {
            Tugas x=selectedTask(t);
            if(x!=null)frame.editTugas(x);
        }));
        a.add(Ui.button("Hapus", false,()-> {
            Tugas x=selectedTask(t);
            if(x!=null&&frame.confirm("Hapus tugas '"+x.judul+"'? Transaksi terkait tetap tersimpan tanpa tautan tugas."))frame.deleteTugas(x.id);
        }));
        bottom.add(a, BorderLayout.EAST);
        p.add(bottom, BorderLayout.SOUTH);
        t.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2) {
                    Tugas x=selectedTask(t);
                    if(x!=null)frame.editTugas(x);
                }
            }
        });
        return p;
    }
}
