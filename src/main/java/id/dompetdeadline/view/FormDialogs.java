package id.dompetdeadline.view;

import id.dompetdeadline.model.*;
import id.dompetdeadline.service.Finance;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

final class FormDialogs {
    private final MainFrame frame;
    FormDialogs(MainFrame frame) { this.frame = frame; }

    private void form(String title, JPanel fields, Runnable save) {
        JDialog d=new JDialog(frame, title, true);
        JPanel root=new JPanel(new BorderLayout(0, 18));
        root.setBackground(Theme.surface);
        root.setBorder(new EmptyBorder(24, 24, 20, 24));
        root.add(Ui.heading(title, "Lengkapi data, lalu pilih Simpan."), BorderLayout.NORTH);
        root.add(fields);
        JPanel buttons=Ui.panel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(Ui.button("Batal", false, d::dispose));
        JButton ok=Ui.button("Simpan", true,()-> {
            try {
                save.run();
                d.dispose();
            }
            catch(IllegalArgumentException e) {
                JOptionPane.showMessageDialog(d, e.getMessage(), "Input belum valid", JOptionPane.WARNING_MESSAGE);
            }
            catch(java.time.DateTimeException e) {
                JOptionPane.showMessageDialog(d, "Format tanggal atau waktu tidak valid.", "Input belum valid", JOptionPane.WARNING_MESSAGE);
            }
        });
        buttons.add(ok);
        root.add(buttons, BorderLayout.SOUTH);
        d.setContentPane(root);
        d.getRootPane().setDefaultButton(ok);
        d.getRootPane().registerKeyboardAction(e->d.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        d.pack();
        d.setMinimumSize(new Dimension(490, d.getHeight()));
        d.setSize(Math.max(490, d.getWidth()), d.getHeight());
        d.setLocationRelativeTo(frame);
        d.setVisible(true);
    }
    void editTransaksi(Transaksi old) {
        if(!frame.ready())return;
        JPanel f=Ui.form();
        JTextField date=Ui.field(old==null?LocalDate.now().toString():old.tanggal.toString(), 19), amount=Ui.field(old==null?"":Long.toString(old.jumlah), 19);
        JComboBox<String> kind=Ui.combo(new String[] {
            "Pengeluaran", "Pemasukan"
        });
        if(old!=null)kind.setSelectedItem(frame.data().kategori(old.kategoriId).jenis);
        JComboBox<Kategori> cat=Ui.combo(new Kategori[0]);
        Runnable cats=()-> {
            cat.removeAllItems();
            for(Kategori k:frame.data().kategori)if(k.jenis.equals(kind.getSelectedItem()))cat.addItem(k);
        };
        cats.run();
        kind.addActionListener(e->cats.run());
        if(old!=null)for(int i=0; i<cat.getItemCount(); i++)if(cat.getItemAt(i).id==old.kategoriId)cat.setSelectedIndex(i);
        JComboBox<Object> task=Ui.combo(new Object[] {
            "— Tidak terkait —"
        });
        for(Tugas t:frame.data().tugas) {
            task.addItem(t);
            if(old!=null&&Objects.equals(old.tugasId, t.id))task.setSelectedItem(t);
        }
        task.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean selected, boolean focus) {
                super.getListCellRendererComponent(l, v, i, selected, focus);
                if(v instanceof Tugas) {
                    Tugas t=(Tugas)v;
                    setText(t.judul+" · "+t.mataKuliah+" (#"+t.id+")");
                }
                return this;
            }
        });
        JTextArea note=new JTextArea(old==null?"":old.catatan, 3, 22);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        Ui.field(f, 0, "Tanggal (YYYY-MM-DD)", date);
        Ui.field(f, 1, "Jenis transaksi", kind);
        Ui.field(f, 2, "Kategori", cat);
        Ui.field(f, 3, "Jumlah (Rp)", amount);
        Ui.field(f, 4, "Tugas terkait", task);
        Ui.field(f, 5, "Catatan", Ui.scroll(note));
        form(old==null?"Transaksi baru":"Ubah transaksi", f,()-> {
            LocalDate dt=LocalDate.parse(date.getText().trim());
            long n=Finance.amount(amount.getText());
            Kategori k=(Kategori)cat.getSelectedItem();
            if(k==null)throw new IllegalArgumentException("Pilih kategori atau tambahkan kategori terlebih dahulu.");
            Object obj=task.getSelectedItem();
            Long id=obj instanceof Tugas?((Tugas)obj).id:null;
            Transaksi x=new Transaksi(old==null?0:old.id, dt, n, k.id, id, note.getText().trim());
            frame.save(x);
        });
    }
    void editKategori() {
        if(!frame.ready())return;
        JPanel f=Ui.form();
        JTextField name=Ui.field("", 22);
        JComboBox<String> type=Ui.combo(new String[] {
            "Pengeluaran", "Pemasukan"
        });
        Ui.field(f, 0, "Nama kategori", name);
        Ui.field(f, 1, "Jenis", type);
        form("Kategori baru", f,()-> {
            String n=Finance.required(name.getText(), "Nama kategori"), k=(String)type.getSelectedItem();
            if(frame.data().kategori.stream().anyMatch(x->x.nama.equalsIgnoreCase(n)&&x.jenis.equals(k)))throw new IllegalArgumentException("Kategori tersebut sudah tersedia.");
            frame.save(new Kategori(0, n, k));
        });
    }
    void editTugas(Tugas old) {
        if(!frame.ready())return;
        JPanel f=Ui.form();
        JTextField title=Ui.field(old==null?"":old.judul, 22), subject=Ui.field(old==null?"":old.mataKuliah, 22), due=Ui.field((old==null?LocalDateTime.now().plusDays(1):old.tenggat).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 22);
        JComboBox<String> pri=Ui.combo(new String[] {
            "Tinggi", "Sedang", "Rendah"
        });
        pri.setSelectedItem(old==null?"Sedang":old.prioritas);
        JCheckBox done=new JCheckBox("Sudah selesai", old!=null&&old.selesai);
        Ui.field(f, 0, "Judul tugas", title);
        Ui.field(f, 1, "Mata kuliah", subject);
        Ui.field(f, 2, "Tenggat (YYYY-MM-DD HH:mm)", due);
        Ui.field(f, 3, "Prioritas", pri);
        Ui.field(f, 4, "Status", done);
        form(old==null?"Tugas baru":"Ubah tugas", f,()-> {
            String n=Finance.required(title.getText(), "Judul tugas"), s=Finance.required(subject.getText(), "Mata kuliah");
            LocalDateTime date=LocalDateTime.parse(due.getText().trim(), DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(java.time.format.ResolverStyle.STRICT));
            Tugas x=new Tugas(old==null?0:old.id, n, s, date,(String)pri.getSelectedItem(), done.isSelected());
            frame.save(x);
        });
    }
    void editBudget(Anggaran old) {
        if(!frame.ready())return;
        JPanel f=Ui.form();
        JTextField month=Ui.field((old==null?frame.budgetMonth():old.bulan).toString(), 18), limit=Ui.field(old==null?"":Long.toString(old.batas), 18);
        JComboBox<Kategori> cat=Ui.combo(frame.data().kategori.stream().filter(k->k.jenis.equals("Pengeluaran")).toArray(Kategori[]::new));
        if(old!=null) {
            for(int i=0; i<cat.getItemCount(); i++)if(cat.getItemAt(i).id==old.kategoriId)cat.setSelectedIndex(i);
            cat.setEnabled(false);
            month.setEditable(false);
        }
        Ui.field(f, 0, "Bulan (YYYY-MM)", month);
        Ui.field(f, 1, "Kategori", cat);
        Ui.field(f, 2, "Batas (Rp)", limit);
        form("Atur anggaran", f,()-> {
            YearMonth m=YearMonth.parse(month.getText().trim());
            Kategori k=(Kategori)cat.getSelectedItem();
            if(k==null)throw new IllegalArgumentException("Pilih kategori pengeluaran.");
            long n=Finance.amount(limit.getText());
            if(old==null&&frame.data().anggaran.stream().anyMatch(a->a.bulan.equals(m)&&a.kategoriId==k.id)&&!frame.confirm("Anggaran sudah ada. Ganti batasnya?"))throw new IllegalArgumentException("Perubahan dibatalkan.");
            frame.selectBudgetMonth(m);
            frame.save(new Anggaran(m, k.id, n));
        });
    }
}
