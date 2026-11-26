package id.dompetdeadline.view;

import id.dompetdeadline.model.*;
import id.dompetdeadline.service.ApplicationService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class MainFrame extends JFrame {
    static final String[] PAGES= {
        "Ringkasan", "Transaksi", "Tugas kuliah", "Analisis", "Anggaran"
    };
    private static final DateTimeFormatter DATE=DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("id-ID"));
    private final ApplicationService service;
    private final FormDialogs dialogs = new FormDialogs(this);
    private final CsvActions csv = new CsvActions(this);
    private final DashboardPanel dashboard = new DashboardPanel(this);
    private final TransaksiPanel transactions = new TransaksiPanel(this);
    private final TugasPanel tasks = new TugasPanel(this);
    private final AnalisisPanel analysis = new AnalisisPanel(this);
    private final AnggaranPanel budgets = new AnggaranPanel(this);
    private final Reminder reminder;
    private final javax.swing.Timer timer;
    private Data data=new Data(List.of(), List.of(), List.of(), List.of());
    private String page=PAGES[0], notice="Menyiapkan data…";
    private JPanel shell, body;
    private Sidebar nav;
    private JLabel status;
    private boolean busy;
    private boolean reminderRunning;
    @FunctionalInterface private interface Work {
        void run() throws Exception;
    }
    public MainFrame(ApplicationService service) {
        this(service, true);
    }
    public MainFrame(ApplicationService service, boolean notifications) {
        super("Dompet Deadline");
        this.service=java.util.Objects.requireNonNull(service);
        this.reminder=notifications?new Reminder():null;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 750));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setIconImage(icon());
        timer=new javax.swing.Timer(60000, e->checkReminders());
        timer.setInitialDelay(3000);
        if(notifications)timer.start();
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                timer.stop();
                if(reminder!=null)reminder.close();
            }
        });
        build();
        refresh();
    }
    Image icon() {
        java.awt.image.BufferedImage img=new java.awt.image.BufferedImage(64, 64, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=img.createGraphics();
        g.setColor(Theme.BLUE);
        g.fillRoundRect(0, 0, 64, 64, 18, 18);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        g.drawString("D", 16, 46);
        g.dispose();
        return img;
    }
    private void build() {
        shell=new JPanel(new BorderLayout());
        shell.setBackground(Theme.bg);
        nav=new Sidebar(this);
        body=new JPanel(new BorderLayout());
        body.setBackground(Theme.bg);
        shell.add(nav, BorderLayout.WEST);
        shell.add(body, BorderLayout.CENTER);
        setContentPane(shell);
        setJMenuBar(menu());
        render();
    }
    private JMenuBar menu() {
        JMenuBar b=new JMenuBar() {
            @Override protected void paintComponent(Graphics g) {g.setColor(Theme.surface);g.fillRect(0,0,getWidth(),getHeight());}
        };
        b.setUI(new javax.swing.plaf.basic.BasicMenuBarUI());
        b.setOpaque(true); b.setBackground(Theme.surface);
        b.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Theme.line));
        JMenu file=new JMenu("Berkas"), go=new JMenu("Navigasi"), view=new JMenu("Tampilan"), help=new JMenu("Bantuan");
        file.setMnemonic(KeyEvent.VK_B);
        int mask=Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        item(file, "Transaksi baru", KeyEvent.VK_N, mask,()->editTransaksi(null));
        item(file, "Tugas baru", KeyEvent.VK_T, mask,()->editTugas(null));
        file.addSeparator();
        item(file, "Impor CSV", KeyEvent.VK_I, mask, this::importCsv);
        item(file, "Ekspor CSV", KeyEvent.VK_E, mask, this::exportCsv);
        file.addSeparator();
        item(file, "Keluar", KeyEvent.VK_Q, mask, this::dispose);
        for(int i=0; i<PAGES.length; i++) {
            String p=PAGES[i];
            item(go, p, KeyEvent.VK_1+i, mask,()->navigate(p));
        }
        item(view, "Ganti tema", KeyEvent.VK_L, mask, this::toggleTheme);
        item(help, "Tentang", 0, 0,()->JOptionPane.showMessageDialog(this, "Dompet Deadline v1.0\nKeuangan dan tenggat kuliah dalam satu ruang.\nData tersimpan di folder pengguna.", "Tentang", JOptionPane.INFORMATION_MESSAGE));
        for(JMenu m:new JMenu[]{file,go,view,help}) {
            m.setOpaque(true);m.setBackground(Theme.surface);m.setForeground(Theme.ink);
            m.setFont(new Font("SansSerif",Font.PLAIN,12));m.setBorder(new EmptyBorder(5,11,5,11));
        }
        b.add(file);
        b.add(go);
        b.add(view);
        b.add(help);
        return b;
    }
    private void item(JMenu m, String text, int key, int mask, Runnable action) {
        JMenuItem i=new JMenuItem(text);
        if(key!=0)i.setAccelerator(KeyStroke.getKeyStroke(key, mask));
        i.addActionListener(e->action.run());
        m.add(i);
    }
    void navigate(String p) {
        page=p;
        render();
    }
    void toggleTheme() {
        Theme.apply(!Theme.dark, true);
        SwingUtilities.updateComponentTreeUI(this);
        build();
    }
    void render() {
        if(body==null)return;
        nav.refreshNav();
        body.removeAll();
        JPanel content=Ui.panel(new BorderLayout(0, 20));
        content.setBorder(new EmptyBorder(24, 28, 18, 28));
        JPanel header=Ui.panel(new BorderLayout(10, 0));
        String sub;
        switch(page) {
            case "Transaksi":sub="Catat arus uang dan hubungkan pengeluaran dengan tugas.";
            break;
            case "Tugas kuliah":sub="Kelola prioritas, tenggat, dan biaya setiap tugas.";
            break;
            case "Analisis":sub="Pahami ke mana uangmu pergi, kategori demi kategori.";
            break;
            case "Anggaran":sub="Atur batas pengeluaran dan pantau progres bulanan.";
            break;
            default:sub="Kelola uang dan waktu kuliah, dalam satu tempat.";
        }
        header.add(Ui.heading(page.equals("Ringkasan")?"Ringkasan keuangan":page, sub), BorderLayout.CENTER);
        JPanel actions=Ui.panel(new FlowLayout(FlowLayout.RIGHT, 7, 6));
        switch(page) {
            case "Transaksi":actions.add(Ui.button("+ Kategori", false,()->editKategori()));
            actions.add(Ui.button("+ Transaksi", true,()->editTransaksi(null)));
            break;
            case "Tugas kuliah":actions.add(Ui.button("+ Tugas baru", true,()->editTugas(null)));
            break;
            case "Anggaran":actions.add(Ui.button("+ Atur anggaran", true,()->editBudget(null)));
            break;
            case "Analisis":actions.add(Ui.button("Ekspor CSV", false, this::exportCsv));
            break;
            default:actions.add(Ui.button("+ Tugas", false,()->editTugas(null)));
            actions.add(Ui.button("+ Transaksi", true,()->editTransaksi(null)));
        }
        header.add(actions, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);
        switch(page) {
            case "Transaksi":content.add(transactions.refreshPage());
            break;
            case "Tugas kuliah":content.add(tasks.refreshPage());
            break;
            case "Analisis":content.add(analysis.refreshPage());
            break;
            case "Anggaran":content.add(budgets.refreshPage());
            break;
            default:content.add(dashboard.refreshPage());
        }
        body.add(content);
        JPanel footer=Ui.panel(new BorderLayout());
        footer.setBorder(new EmptyBorder(0, 29, 12, 28));
        status=Ui.label(busy?"Memproses data…":notice, 11, false, Theme.muted);
        footer.add(status);
        footer.add(Ui.label(LocalDate.now().format(DATE), 11, false, Theme.muted), BorderLayout.EAST);
        body.add(footer, BorderLayout.SOUTH);
        body.revalidate();
        body.repaint();
        nav.revalidate();
        nav.repaint();
    }
    boolean ready() {
        if(busy) {
            error("Data sedang diproses. Coba lagi setelah proses selesai.");
            return false;
        }
        return true;
    }
    boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION;
    }
    void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Periksa kembali", JOptionPane.WARNING_MESSAGE);
    }
    private void refresh() {
        mutate(()-> {
        }, "Data siap · tersimpan lokal");
    }
    private void mutate(Work action, String message) {
        if(busy) {
            error("Data sedang diproses. Coba lagi setelah proses selesai.");
            return;
        }
        busy=true;
        if(status!=null)status.setText("Memproses data…");
        new SwingWorker<Data, Void>() {
            protected Data doInBackground()throws Exception {
                action.run();
                return service.load();
            }
            protected void done() {
                busy=false;
                if(!isDisplayable()) return;
                try {
                    Data before=data;
                    data=get();
                    notice=message;
                    render();
                    if(!message.startsWith("Data siap")) {
                        List<String> warnings=service.budgetWarnings(before, data, YearMonth.now());
                        if(!warnings.isEmpty())JOptionPane.showMessageDialog(MainFrame.this, String.join("\n", warnings), "Peringatan anggaran", JOptionPane.WARNING_MESSAGE);
                    }
                }
                catch(Exception ex) {
                    Throwable c=ex.getCause()==null?ex:ex.getCause();
                    notice="Operasi tidak berhasil";
                    if(status!=null)status.setText(notice);
                    error(c.getMessage()==null?"Operasi gagal.":c.getMessage());
                }
            }
        }.execute();
    }
    private void checkReminders() {
        if(busy||reminderRunning||reminder==null||!isDisplayable())return;
        reminderRunning=true;
        Data snapshot=data;
        LocalDateTime now=LocalDateTime.now();
        new SwingWorker<List<Tugas>, Void>() {
            protected List<Tugas> doInBackground()throws Exception {
                return service.claimUrgent(snapshot, now);
            }
            protected void done() {
                reminderRunning=false;
                try {
                    if(isDisplayable())for(Tugas t:get())reminder.show(MainFrame.this, t);
                }
                catch(Exception e) {
                    if(status!=null)status.setText("Pengingat tidak dapat diperiksa: "+e.getMessage());
                }
            }
        }.execute();
    }
    // Package-private UI commands; repository access stays behind ApplicationService.
    Data data() { return data; }
    String currentPage() { return page; }
    YearMonth budgetMonth() { return budgets.selectedMonth(); }
    void selectBudgetMonth(YearMonth month) { budgets.selectMonth(month); }

    void editTransaksi(Transaksi old) { dialogs.editTransaksi(old); }
    void editKategori() { dialogs.editKategori(); }
    void editTugas(Tugas old) { dialogs.editTugas(old); }
    void editBudget(Anggaran old) { dialogs.editBudget(old); }
    void importCsv() { csv.importCsv(); }
    void exportCsv() { csv.exportCsv(); }

    void save(Transaksi x) { mutate(() -> service.save(x), "Transaksi tersimpan."); }
    void save(Kategori x) { mutate(() -> service.save(x), "Kategori ditambahkan."); }
    void save(Tugas x) { mutate(() -> service.save(x), "Tugas tersimpan."); }
    void save(Anggaran x) { mutate(() -> service.save(x), "Anggaran tersimpan."); }
    void deleteTransaksi(long id) { mutate(() -> service.deleteTransaksi(id), "Transaksi dihapus."); }
    void deleteTugas(long id) { mutate(() -> service.deleteTugas(id), "Tugas dihapus."); }
    void toggleTask(Tugas task) { mutate(() -> service.toggleTask(task), "Status tugas diperbarui."); }
    void deleteBudget(Anggaran budget) { mutate(() -> service.deleteBudget(budget), "Anggaran dihapus."); }
    void importFile(Path file) {
        Data snapshot = data;
        mutate(() -> service.importFile(file, snapshot), "Impor CSV selesai.");
    }
    void exportFile(Path file) {
        Data snapshot = data;
        mutate(() -> service.exportFile(file, snapshot), "CSV diekspor ke " + file.getFileName() + ".");
    }
}
