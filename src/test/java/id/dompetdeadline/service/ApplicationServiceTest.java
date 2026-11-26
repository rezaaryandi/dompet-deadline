package id.dompetdeadline.service;

import id.dompetdeadline.model.*;
import id.dompetdeadline.support.MemoryRepository;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationServiceTest {
    private final LocalDateTime now = LocalDateTime.of(2026, 11, 26, 8, 0);
    private final YearMonth month = YearMonth.from(now);
    private final Tugas task = new Tugas(1, "Laporan", "PBO", now.plusHours(12), "Tinggi", false);
    private final Data initial = new Data(List.of(new Kategori(1, "Kuliah", "Pengeluaran")), List.of(task), List.of(), List.of(new Anggaran(month, 1, 100000)));
    private final ApplicationService service = new ApplicationService(new MemoryRepository(initial));

    @Test void toggleTaskUsesRepository() throws Exception {
        service.toggleTask(task);
        assertTrue(service.load().tugas(1L).selesai);
        service.toggleTask(service.load().tugas(1L));
        assertFalse(service.load().tugas(1L).selesai);
    }
    @Test void reminderIsNotClaimedTwice() throws Exception {
        assertEquals(1, service.claimUrgent(initial, now).size());
        assertTrue(service.claimUrgent(initial, now).isEmpty());
    }
    @Test void budgetWarningOnlyOnStatusChange() throws Exception {
        service.save(new Transaksi(0, now.toLocalDate(), 80000, 1, 1L, "Cetak"));
        Data after = service.load();
        assertEquals(1, service.budgetWarnings(initial, after, month).size());
        assertTrue(service.budgetWarnings(after, after, month).isEmpty());
    }
    @Test void deletingTaskKeepsExpense() throws Exception {
        service.save(new Transaksi(0, now.toLocalDate(), 25000, 1, 1L, "Cetak"));
        service.deleteTugas(1);
        Data after = service.load();
        assertNull(after.tugas(1L));
        assertEquals(25000, Finance.total(after, month, true));
        assertNull(after.transaksi.get(0).tugasId);
    }
}
