package id.dompetdeadline.service;

import id.dompetdeadline.model.*;
import id.dompetdeadline.repository.Repository;
import java.nio.file.Path;
import java.time.*;
import java.util.*;

/** Application use cases. No Swing and no SQL; delegates persistence to Repository. */
public final class ApplicationService {
    private final Repository repository;

    public ApplicationService(Repository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Data load() throws Exception { return repository.load(); }
    public void save(Transaksi x) throws Exception { repository.save(x); }
    public void save(Kategori x) throws Exception { repository.save(x); }
    public void save(Tugas x) throws Exception { repository.save(x); }
    public void save(Anggaran x) throws Exception { repository.save(x); }
    public void deleteTransaksi(long id) throws Exception { repository.deleteTransaksi(id); }
    public void deleteTugas(long id) throws Exception { repository.deleteTugas(id); }
    public void deleteBudget(Anggaran a) throws Exception { repository.deleteAnggaran(a.bulan, a.kategoriId); }

    public void toggleTask(Tugas t) throws Exception {
        repository.save(new Tugas(t.id, t.judul, t.mataKuliah, t.tenggat, t.prioritas, !t.selesai));
    }

    public void importFile(Path file, Data snapshot) throws Exception {
        repository.importTransaksi(Csv.importFile(file, snapshot));
    }

    public void exportFile(Path file, Data snapshot) throws Exception {
        Csv.exportFile(file, snapshot);
    }

    public List<Tugas> claimUrgent(Data snapshot, LocalDateTime now) throws Exception {
        List<Tugas> result = new ArrayList<>();
        for (Tugas task : snapshot.tugas) {
            if (Finance.urgent(task, now) && repository.claimReminder(task)) result.add(task);
        }
        return result;
    }

    public List<String> budgetWarnings(Data before, Data after, YearMonth now) {
        List<String> changes=new ArrayList<>();
        for(Anggaran a:after.anggaran) {
            if(!a.bulan.equals(now))continue;
            long used=Finance.spent(after, now, a.kategoriId);
            String state=Finance.budgetStatus(used, a.batas);
            Anggaran old=before.anggaran.stream().filter(x->x.bulan.equals(now)&&x.kategoriId==a.kategoriId).findFirst().orElse(null);
            String previous=old==null?"Aman":Finance.budgetStatus(Finance.spent(before, now, a.kategoriId), old.batas);
            if(!state.equals("Aman")&&!state.equals(previous))changes.add(after.kategori(a.kategoriId).nama+": "+state+" ("+Finance.rupiah(used)+" / "+Finance.rupiah(a.batas)+")");
        }
        return changes;
    }
}
