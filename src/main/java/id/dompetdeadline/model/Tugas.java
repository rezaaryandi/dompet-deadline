package id.dompetdeadline.model;
import java.time.LocalDateTime;
public final class Tugas {
    public final long id;
    public final String judul, mataKuliah, prioritas;
    public final LocalDateTime tenggat;
    public final boolean selesai;
    public Tugas(long id, String judul, String mataKuliah, LocalDateTime tenggat, String prioritas, boolean selesai) {
        this.id=id;
        this.judul=judul;
        this.mataKuliah=mataKuliah;
        this.tenggat=tenggat;
        this.prioritas=prioritas;
        this.selesai=selesai;
    }
    @Override public String toString() {
        return judul;
    }
}
