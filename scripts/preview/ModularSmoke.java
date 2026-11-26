package id.dompetdeadline.view;

import id.dompetdeadline.model.*;
import id.dompetdeadline.service.*;
import id.dompetdeadline.support.MemoryRepository;
import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.time.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/** Automated navigation and use-case smoke test, using a test repository, not SQLite. */
public final class ModularSmoke {
    private static int checks;
    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
        checks++;
        System.out.println("PASS " + message);
    }
    private static <T> T edt(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        SwingUtilities.invokeAndWait(task);
        return task.get();
    }
    private static <T extends Component> T find(Container root, Class<T> type) {
        for (Component child : root.getComponents()) {
            if (type.isInstance(child)) return type.cast(child);
            if (child instanceof Container) {
                T found = find((Container) child, type);
                if (found != null) return found;
            }
        }
        return null;
    }
    private static void waitReady(MainFrame frame) throws Exception {
        for (int i = 0; i < 100; i++) {
            if (edt(() -> find(frame, JLabel.class) != null && !frame.data().kategori.isEmpty())) {
                // SwingWorker completion has run on the EDT.
                return;
            }
            Thread.sleep(25);
        }
        throw new AssertionError("Initial snapshot did not load");
    }
    public static void main(String[] args) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        YearMonth month = YearMonth.from(now);
        Tugas task = new Tugas(1, "Laporan PBO", "PBO", now.plusHours(12), "Tinggi", false);
        Data initial = new Data(List.of(new Kategori(1,"Kuliah","Pengeluaran"),new Kategori(2,"Uang saku","Pemasukan")), List.of(task), List.of(new Transaksi(1,now.toLocalDate(),100000,2,null,"Uang bulanan"),new Transaksi(2,now.toLocalDate(),25000,1,1L,"Cetak")), List.of());
        MemoryRepository repository = new MemoryRepository(initial);
        ApplicationService service = new ApplicationService(repository);
        check(service.claimUrgent(initial,now).size()==1,"service claims urgent task");
        check(service.claimUrgent(initial,now).isEmpty(),"service suppresses repeated reminder");
        service.toggleTask(task);
        check(service.load().tugas(1L).selesai,"service toggles task status");
        service.toggleTask(service.load().tugas(1L));
        Data before = service.load();
        service.save(new Anggaran(month,1,30000));
        Data after = service.load();
        check(service.budgetWarnings(before,after,month).size()==1,"service detects new budget warning");
        check(service.budgetWarnings(after,after,month).isEmpty(),"service does not repeat budget warning");
        service.deleteBudget(after.anggaran.get(0));
        MainFrame frame = edt(() -> { Theme.apply(false,false); MainFrame f=new MainFrame(service,false); f.setVisible(true); return f; });
        try {
            waitReady(frame);
            for (int i=0;i<MainFrame.PAGES.length;i++) {
                final int index=i;
                edt(() -> {frame.getJMenuBar().getMenu(1).getItem(index).doClick();return null;});
                check(edt(() -> frame.currentPage().equals(MainFrame.PAGES[index])),"navigation: "+MainFrame.PAGES[index]);
            }
            edt(() -> {frame.navigate("Transaksi"); return null;});
            check(edt(() -> find(frame.getContentPane(),JTable.class).getRowCount()==2),"transaction table loaded");
            edt(() -> {find(frame.getContentPane(),JTextField.class).setText("Cetak");return null;});
            check(edt(() -> find(frame.getContentPane(),JTable.class).getRowCount()==1),"transaction search filters rows");
            edt(() -> {frame.navigate("Ringkasan");frame.navigate("Transaksi");return null;});
            check(edt(() -> find(frame.getContentPane(),JTextField.class).getText().equals("Cetak") && find(frame.getContentPane(),JTable.class).getRowCount()==1),"filter survives page navigation");
            edt(() -> {frame.toggleTheme();return null;});
            check(edt(() -> Theme.dark && find(frame.getContentPane(),JTable.class).getRowCount()==1),"dark theme retains page state");
            edt(() -> {frame.navigate("Tugas kuliah");return null;});
            check(edt(() -> find(frame.getContentPane(),JTable.class).getModel().getValueAt(0,6).equals(25000L)),"linked task cost in table");
            edt(() -> {frame.toggleTask(frame.data().tugas(1L));return null;});
            boolean changed=false;
            for(int i=0;i<100;i++) {if(edt(() -> frame.data().tugas(1L).selesai)){changed=true;break;}Thread.sleep(25);}
            check(changed,"background worker updates task snapshot");
            check(edt(() -> find(frame.getContentPane(),JTable.class).getModel().getValueAt(0,5).equals("Selesai")),"task table refreshes after mutation");
        } finally { edt(() -> {frame.dispose();return null;}); }
        System.out.println("Total: " + checks + " modular checks passed.");
        System.exit(0);
    }
}
