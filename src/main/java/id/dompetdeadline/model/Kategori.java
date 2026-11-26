package id.dompetdeadline.model;
public final class Kategori {
    public final long id;
    public final String nama, jenis;
    public Kategori(long id, String nama, String jenis) {
        this.id=id;
        this.nama=nama;
        this.jenis=jenis;
    }
    @Override public String toString() {
        return nama;
    }
}
