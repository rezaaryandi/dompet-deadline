package id.dompetdeadline.model;
import java.time.LocalDate;
public final class Transaksi {
    public final long id, jumlah, kategoriId;
    public final Long tugasId;
    public final LocalDate tanggal;
    public final String catatan;
    public Transaksi(long id, LocalDate tanggal, long jumlah, long kategoriId, Long tugasId, String catatan) {
        this.id=id;
        this.tanggal=tanggal;
        this.jumlah=jumlah;
        this.kategoriId=kategoriId;
        this.tugasId=tugasId;
        this.catatan=catatan;
    }
}
