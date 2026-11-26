package id.dompetdeadline.repository;
import id.dompetdeadline.model.*;
import java.sql.*;
import java.time.*;
import java.util.*;
public final class SqliteRepository implements Repository {
    private final Database db;
    public SqliteRepository(Database db) {
        this.db=db;
    }
    @Override public Data load() throws SQLException {
        List<Kategori> k=new ArrayList<>();
        List<Tugas> t=new ArrayList<>();
        List<Transaksi> x=new ArrayList<>();
        List<Anggaran> a=new ArrayList<>();
        try(Connection c=db.open()) {
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement("SELECT * FROM kategori ORDER BY jenis,nama"); ResultSet r=p.executeQuery()) {
                while(r.next())k.add(new Kategori(r.getLong("id"), r.getString("nama"), r.getString("jenis")));
            }
            try(PreparedStatement p=c.prepareStatement("SELECT * FROM tugas ORDER BY tenggat"); ResultSet r=p.executeQuery()) {
                while(r.next())t.add(new Tugas(r.getLong("id"), r.getString("judul"), r.getString("mata_kuliah"), LocalDateTime.parse(r.getString("tenggat")), r.getString("prioritas"), r.getBoolean("selesai")));
            }
            try(PreparedStatement p=c.prepareStatement("SELECT * FROM transaksi ORDER BY tanggal DESC,id DESC"); ResultSet r=p.executeQuery()) {
                while(r.next()) {
                    long id=r.getLong("tugas_id");
                    Long tid=r.wasNull()?null:id;
                    x.add(new Transaksi(r.getLong("id"), LocalDate.parse(r.getString("tanggal")), r.getLong("jumlah"), r.getLong("kategori_id"), tid, r.getString("catatan")));
                }
            }
            try(PreparedStatement p=c.prepareStatement("SELECT * FROM anggaran ORDER BY bulan DESC,kategori_id"); ResultSet r=p.executeQuery()) {
                while(r.next())a.add(new Anggaran(YearMonth.parse(r.getString("bulan")), r.getLong("kategori_id"), r.getLong("batas")));
            }
            c.commit();
        }
        return new Data(k, t, x, a);
    }
    private int update(String sql, Object...values) throws SQLException {
        try(Connection c=db.open(); PreparedStatement p=c.prepareStatement(sql)) {
            for(int i=0; i<values.length; i++)p.setObject(i+1, values[i]);
            return p.executeUpdate();
        }
    }
    @Override public void save(Kategori k) throws SQLException {
        if(k.id==0)update("INSERT INTO kategori(nama,jenis) VALUES(?,?)", k.nama, k.jenis);
        else update("UPDATE kategori SET nama=?,jenis=? WHERE id=?", k.nama, k.jenis, k.id);
    }
    @Override public void save(Tugas t) throws SQLException {
        if(t.id==0)update("INSERT INTO tugas(judul,mata_kuliah,tenggat,prioritas,selesai) VALUES(?,?,?,?,?)", t.judul, t.mataKuliah, t.tenggat.toString(), t.prioritas, t.selesai?1:0);
        else update("UPDATE tugas SET judul=?,mata_kuliah=?,tenggat=?,prioritas=?,selesai=? WHERE id=?", t.judul, t.mataKuliah, t.tenggat.toString(), t.prioritas, t.selesai?1:0, t.id);
    }
    private void bindTransaksi(PreparedStatement p, Transaksi t) throws SQLException {
        p.setString(1, t.tanggal.toString());
        p.setLong(2, t.jumlah);
        p.setLong(3, t.kategoriId);
        if(t.tugasId==null)p.setNull(4, Types.INTEGER);
        else p.setLong(4, t.tugasId);
        p.setString(5, t.catatan);
        if(t.id!=0)p.setLong(6, t.id);
    }
    @Override public void save(Transaksi t) throws SQLException {
        String sql=t.id==0?"INSERT INTO transaksi(tanggal,jumlah,kategori_id,tugas_id,catatan) VALUES(?,?,?,?,?)":"UPDATE transaksi SET tanggal=?,jumlah=?,kategori_id=?,tugas_id=?,catatan=? WHERE id=?";
        try(Connection c=db.open(); PreparedStatement p=c.prepareStatement(sql)) {
            bindTransaksi(p, t);
            p.executeUpdate();
        }
    }
    @Override public void save(Anggaran a) throws SQLException {
        update("INSERT INTO anggaran(bulan,kategori_id,batas) VALUES(?,?,?) ON CONFLICT(bulan,kategori_id) DO UPDATE SET batas=excluded.batas", a.bulan.toString(), a.kategoriId, a.batas);
    }
    @Override public void deleteTugas(long id)throws SQLException {
        update("DELETE FROM tugas WHERE id=?", id);
    }
    @Override public void deleteTransaksi(long id)throws SQLException {
        update("DELETE FROM transaksi WHERE id=?", id);
    }
    @Override public void deleteAnggaran(YearMonth m, long id)throws SQLException {
        update("DELETE FROM anggaran WHERE bulan=? AND kategori_id=?", m.toString(), id);
    }
    @Override public void importTransaksi(List<Transaksi> list)throws SQLException {
        try(Connection c=db.open()) {
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement("INSERT INTO transaksi(tanggal,jumlah,kategori_id,tugas_id,catatan) VALUES(?,?,?,?,?)")) {
                for(Transaksi t:list) {
                    bindTransaksi(p, t);
                    p.addBatch();
                }
                p.executeBatch();
                c.commit();
            }
            catch(SQLException|RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }
    @Override public boolean claimReminder(Tugas t)throws SQLException {
        return update("INSERT OR IGNORE INTO pengingat(tugas_id,tenggat) SELECT id,tenggat FROM tugas WHERE id=? AND tenggat=? AND selesai=0", t.id, t.tenggat.toString())==1;
    }
}
