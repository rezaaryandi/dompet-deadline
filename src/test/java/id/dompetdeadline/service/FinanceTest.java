package id.dompetdeadline.service;
import id.dompetdeadline.model.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class FinanceTest {
    private final LocalDate date=LocalDate.of(2026, 11, 26);
    private final LocalDateTime now=date.atTime(9, 0);
    private final YearMonth month=YearMonth.from(date);
    private final Tugas task=new Tugas(1, "Laporan", "PBO", now.plusHours(12), "Tinggi", false);
    private final Data data=new Data(List.of(new Kategori(1, "Uang saku", "Pemasukan"), new Kategori(2, "Kuliah", "Pengeluaran")), List.of(task), List.of(new Transaksi(1, date, 1000000, 1, null, ""), new Transaksi(2, date, 250000, 2, 1L, ""), new Transaksi(3, date.minusMonths(1), 50000, 2, 1L, ""), new Transaksi(4, date, 10000, 1, 1L, "")), List.of());
    @Test void monthlyBalance() {
        assertEquals(760000, Finance.balance(data, month));
    }
    @Test void costsOnlyExpensesAcrossMonths() {
        assertEquals(300000, Finance.cost(data, 1));
        assertEquals(300000, Finance.activeCost(data));
    }
    @Test void reminderWithin24Hours() {
        assertTrue(Finance.urgent(task, now));
        assertFalse(Finance.urgent(new Tugas(2, "x", "PBO", now.plusHours(25), "Rendah", false), now));
        assertFalse(Finance.urgent(new Tugas(2, "x", "PBO", now.plusHours(1), "Rendah", true), now));
        assertTrue(Finance.urgent(new Tugas(2, "x", "PBO", now.plusHours(24), "Rendah", false), now));
        assertFalse(Finance.urgent(task, now.plusDays(1)));
    }
    @Test void validatesMoney() {
        assertEquals(12500, Finance.amount("12500"));
        assertThrows(IllegalArgumentException.class,()->Finance.amount("12.500"));
        assertThrows(IllegalArgumentException.class,()->Finance.amount("0"));
        assertThrows(IllegalArgumentException.class,()->Finance.amount("-100"));
    }
    @Test void budgetThreshold() {
        assertEquals("Aman", Finance.budgetStatus(79, 100));
        assertEquals("Hampir habis", Finance.budgetStatus(80, 100));
        assertEquals("Batas tercapai", Finance.budgetStatus(100, 100));
    }
    @Test void categoryTotals() {
        assertEquals(Map.of("Kuliah", 250000L), Finance.byCategory(data, month));
    }
    @Test void upcomingSortedAndLimited() {
        Tugas early=new Tugas(2, "x", "PBO", now.plusHours(1), "Tinggi", false);
        Tugas done=new Tugas(3, "done", "PBO", now, "Tinggi", true);
        Data x=new Data(data.kategori, List.of(task, done, early), List.of(), List.of());
        assertEquals(List.of(early), Finance.upcoming(x, 1));
    }
}
