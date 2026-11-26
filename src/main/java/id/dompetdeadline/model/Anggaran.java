package id.dompetdeadline.model;
import java.time.YearMonth;
public final class Anggaran {
    public final YearMonth bulan;
    public final long kategoriId, batas;
    public Anggaran(YearMonth bulan, long kategoriId, long batas) {
        this.bulan=bulan;
        this.kategoriId=kategoriId;
        this.batas=batas;
    }
}
