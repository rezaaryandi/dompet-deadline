package id.dompetdeadline.service;
import id.dompetdeadline.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class CsvTest {
    @TempDir Path dir;
    @Test void quotedRoundTrip()throws Exception {
        String note="Cetak, \"laporan\"\nPBO";
        Kategori k=new Kategori(1, "Kuliah", "Pengeluaran");
        Tugas task=new Tugas(7, "Laporan", "PBO", LocalDateTime.now(), "Tinggi", false);
        Data d=new Data(List.of(k), List.of(task), List.of(new Transaksi(1, LocalDate.of(2026, 11, 26), 25000, 1, 7L, note)), List.of());
        Path file=dir.resolve("export.csv");
        Csv.exportFile(file, d);
        Transaksi t=Csv.importFile(file, d).get(0);
        assertEquals(note, t.catatan);
        assertEquals(25000, t.jumlah);
        assertEquals(7L, t.tugasId.longValue());
    }
    @Test void malformedQuotes() {
        assertThrows(IOException.class,()->Csv.parse(new StringReader("\"unclosed")));
    }
    @Test void bomAndEmptyCells()throws Exception {
        assertEquals(List.of(List.of("tanggal", "kategori"), List.of("x", "")), Csv.parse(new StringReader("\uFEFF\"tanggal\",\"kategori\"\r\nx,\r\n")));
    }
}
