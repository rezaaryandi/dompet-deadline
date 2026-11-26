package id.dompetdeadline.service;
import id.dompetdeadline.model.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import java.text.NumberFormat;
public final class Finance {
    private Finance() {
    }
    public static boolean expense(Data d, Transaksi t) {
        return d.kategori(t.kategoriId).jenis.equals("Pengeluaran");
    }
    public static long total(Data d, YearMonth m, boolean keluar) {
        return d.transaksi.stream().filter(t->YearMonth.from(t.tanggal).equals(m)&&expense(d, t)==keluar).mapToLong(t->t.jumlah).reduce(0L, Math::addExact);
    }
    public static long balance(Data d, YearMonth m) {
        return Math.subtractExact(total(d, m, false), total(d, m, true));
    }
    public static long cost(Data d, long id) {
        return d.transaksi.stream().filter(t->Objects.equals(t.tugasId, id)&&expense(d, t)).mapToLong(t->t.jumlah).reduce(0L, Math::addExact);
    }
    public static long activeCost(Data d) {
        return d.tugas.stream().filter(t->!t.selesai).mapToLong(t->cost(d, t.id)).reduce(0L, Math::addExact);
    }
    public static List<Tugas> upcoming(Data d, int max) {
        return d.tugas.stream().filter(t->!t.selesai).sorted(Comparator.comparing(t->t.tenggat)).limit(max).collect(Collectors.toList());
    }
    public static boolean urgent(Tugas t, LocalDateTime now) {
        return !t.selesai&&!t.tenggat.isBefore(now)&&!t.tenggat.isAfter(now.plusHours(24));
    }
    public static long spent(Data d, YearMonth m, long category) {
        return d.transaksi.stream().filter(t->t.kategoriId==category&&YearMonth.from(t.tanggal).equals(m)&&expense(d, t)).mapToLong(t->t.jumlah).reduce(0L, Math::addExact);
    }
    public static Map<String, Long> byCategory(Data d, YearMonth m) {
        Map<String, Long> r=new LinkedHashMap<>();
        d.kategori.stream().filter(k->k.jenis.equals("Pengeluaran")).forEach(k-> {
            long n=spent(d, m, k.id);
            if(n>0)r.put(k.nama, n);
        });
        return r;
    }
    public static String rupiah(long v) {
        return "Rp"+NumberFormat.getIntegerInstance(Locale.forLanguageTag("id-ID")).format(v);
    }
    public static long amount(String text) {
        try {
            long v=Long.parseLong(text.trim());
            if(v<=0||v>1_000_000_000_000L)throw new NumberFormatException();
            return v;
        }
        catch(NumberFormatException e) {
            throw new IllegalArgumentException("Jumlah harus berupa bilangan bulat positif (maks. 1 triliun), tanpa titik atau koma.");
        }
    }
    public static String required(String s, String label) {
        s=s.trim();
        if(s.isEmpty())throw new IllegalArgumentException(label+" wajib diisi.");
        return s;
    }
    public static String budgetStatus(long spent, long limit) {
        if(spent>=limit)return "Batas tercapai";
        if(java.math.BigInteger.valueOf(spent).multiply(java.math.BigInteger.valueOf(100)).compareTo(java.math.BigInteger.valueOf(limit).multiply(java.math.BigInteger.valueOf(80)))>=0)return "Hampir habis";
        return "Aman";
    }
}
