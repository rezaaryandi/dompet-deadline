package id.dompetdeadline.model;
import java.util.*;
public final class Data {
    public final List<Kategori> kategori;
    public final List<Tugas> tugas;
    public final List<Transaksi> transaksi;
    public final List<Anggaran> anggaran;
    public Data(List<Kategori> k, List<Tugas> t, List<Transaksi> x, List<Anggaran> a) {
        kategori=List.copyOf(k);
        tugas=List.copyOf(t);
        transaksi=List.copyOf(x);
        anggaran=List.copyOf(a);
    }
    public Kategori kategori(long id) {
        return kategori.stream().filter(k->k.id==id).findFirst().orElseThrow();
    }
    public Tugas tugas(Long id) {
        return id==null?null:tugas.stream().filter(t->t.id==id).findFirst().orElse(null);
    }
    public String judul(Long id) {
        Tugas t=tugas(id);
        return t==null?"—":t.judul;
    }
}
