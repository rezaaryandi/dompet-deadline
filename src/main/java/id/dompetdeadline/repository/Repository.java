package id.dompetdeadline.repository;
import id.dompetdeadline.model.*;
import java.time.YearMonth;
import java.util.List;
public interface Repository {
    Data load() throws Exception;
    void save(Kategori k) throws Exception;
    void save(Tugas t) throws Exception;
    void save(Transaksi t) throws Exception;
    void save(Anggaran a) throws Exception;
    void deleteTugas(long id) throws Exception;
    void deleteTransaksi(long id) throws Exception;
    void deleteAnggaran(YearMonth bulan, long id) throws Exception;
    void importTransaksi(List<Transaksi> list) throws Exception;
    boolean claimReminder(Tugas t) throws Exception;
}
