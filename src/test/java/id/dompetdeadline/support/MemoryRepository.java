package id.dompetdeadline.support;

import id.dompetdeadline.model.*;
import id.dompetdeadline.repository.Repository;
import java.time.YearMonth;
import java.util.*;

/** Test double only; not included in the executable application. */
public final class MemoryRepository implements Repository {
    private final List<Kategori> categories = new ArrayList<>();
    private final List<Tugas> tasks = new ArrayList<>();
    private final List<Transaksi> transactions = new ArrayList<>();
    private final List<Anggaran> budgets = new ArrayList<>();
    private final Set<String> reminders = new HashSet<>();
    private long nextId = 100;

    public MemoryRepository(Data initial) {
        categories.addAll(initial.kategori);
        tasks.addAll(initial.tugas);
        transactions.addAll(initial.transaksi);
        budgets.addAll(initial.anggaran);
    }
    @Override public synchronized Data load() { return new Data(categories, tasks, transactions, budgets); }
    @Override public synchronized void save(Kategori k) {
        categories.removeIf(x -> x.id == k.id);
        categories.add(new Kategori(k.id == 0 ? nextId++ : k.id, k.nama, k.jenis));
    }
    @Override public synchronized void save(Tugas t) {
        tasks.removeIf(x -> x.id == t.id);
        tasks.add(new Tugas(t.id == 0 ? nextId++ : t.id, t.judul, t.mataKuliah, t.tenggat, t.prioritas, t.selesai));
    }
    @Override public synchronized void save(Transaksi t) {
        transactions.removeIf(x -> x.id == t.id);
        transactions.add(new Transaksi(t.id == 0 ? nextId++ : t.id, t.tanggal, t.jumlah, t.kategoriId, t.tugasId, t.catatan));
    }
    @Override public synchronized void save(Anggaran a) { deleteAnggaran(a.bulan, a.kategoriId); budgets.add(a); }
    @Override public synchronized void deleteTugas(long id) {
        tasks.removeIf(x -> x.id == id);
        transactions.replaceAll(x -> Objects.equals(x.tugasId, id) ? new Transaksi(x.id, x.tanggal, x.jumlah, x.kategoriId, null, x.catatan) : x);
    }
    @Override public synchronized void deleteTransaksi(long id) { transactions.removeIf(x -> x.id == id); }
    @Override public synchronized void deleteAnggaran(YearMonth month, long id) { budgets.removeIf(x -> x.kategoriId == id && x.bulan.equals(month)); }
    @Override public synchronized void importTransaksi(List<Transaksi> list) { list.forEach(this::save); }
    @Override public synchronized boolean claimReminder(Tugas t) { return reminders.add(t.id + ":" + t.tenggat); }
}
