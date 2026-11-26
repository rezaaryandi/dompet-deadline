package id.dompetdeadline.repository;
import id.dompetdeadline.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class SqliteRepositoryTest {
    @TempDir Path dir;
    @Test void persistenceAndForeignKeys()throws Exception {
        Database db=new Database(dir.resolve("test.db"));
        db.initialize();
        Repository r=new SqliteRepository(db);
        r.save(new Kategori(0, "Print 'A'", "Pengeluaran"));
        long k=r.load().kategori.stream().filter(x->x.nama.equals("Print 'A'")).findFirst().orElseThrow().id;
        LocalDateTime due=LocalDateTime.now().plusHours(2);
        r.save(new Tugas(0, "Laporan", "PBO", due, "Tinggi", false));
        Tugas task=r.load().tugas.get(0);
        r.save(new Transaksi(0, LocalDate.now(), 5000, k, task.id, "Catatan"));
        r.save(new Anggaran(YearMonth.now(), k, 10000));
        Database reopened=new Database(dir.resolve("test.db"));
        reopened.initialize();
        Repository rr=new SqliteRepository(reopened);
        Data data=rr.load();
        assertEquals(5000, data.transaksi.get(0).jumlah);
        assertEquals(task.id, data.transaksi.get(0).tugasId.longValue());
        assertEquals(10000, data.anggaran.get(0).batas);
        assertTrue(rr.claimReminder(task));
        assertFalse(rr.claimReminder(task));
        rr.deleteTugas(task.id);
        assertNull(rr.load().transaksi.get(0).tugasId);
    }
    @Test void importIsAtomic()throws Exception {
        Database db=new Database(dir.resolve("test.db"));
        db.initialize();
        Repository r=new SqliteRepository(db);
        long k=r.load().kategori.get(0).id;
        assertThrows(Exception.class,()->r.importTransaksi(List.of(new Transaksi(0, LocalDate.now(), 5000, k, null, "valid"), new Transaksi(0, LocalDate.now(), 5000, -99, null, "invalid"))));
        assertTrue(r.load().transaksi.isEmpty());
    }
}
