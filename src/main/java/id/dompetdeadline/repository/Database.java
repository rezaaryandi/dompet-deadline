package id.dompetdeadline.repository;
import java.nio.file.*;
import java.sql.*;
public final class Database {
    private final Path file;
    public Database(Path file) {
        this.file=file.toAbsolutePath();
    }
    public static Database userDatabase() {
        return new Database(Path.of(System.getProperty("user.home"), ".dompet-deadline", "dompet.db"));
    }
    public Connection open() throws SQLException {
        Connection c=DriverManager.getConnection("jdbc:sqlite:"+file);
        try(Statement s=c.createStatement()) {
            s.execute("PRAGMA foreign_keys=ON");
            s.execute("PRAGMA busy_timeout=5000");
        }
        catch(SQLException e) {
            c.close();
            throw e;
        }
        return c;
    }
    public void initialize() throws Exception {
        Class.forName("org.sqlite.JDBC");
        Files.createDirectories(file.getParent());
        try(Connection c=open(); Statement s=c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS kategori(id INTEGER PRIMARY KEY, nama TEXT NOT NULL COLLATE NOCASE, jenis TEXT NOT NULL CHECK(jenis IN ('Pemasukan','Pengeluaran')), UNIQUE(nama,jenis))");
            s.execute("CREATE TABLE IF NOT EXISTS tugas(id INTEGER PRIMARY KEY, judul TEXT NOT NULL, mata_kuliah TEXT NOT NULL, tenggat TEXT NOT NULL, prioritas TEXT NOT NULL CHECK(prioritas IN ('Tinggi','Sedang','Rendah')), selesai INTEGER NOT NULL DEFAULT 0 CHECK(selesai IN (0,1)))");
            s.execute("CREATE TABLE IF NOT EXISTS transaksi(id INTEGER PRIMARY KEY, tanggal TEXT NOT NULL, jumlah INTEGER NOT NULL CHECK(jumlah>0), kategori_id INTEGER NOT NULL REFERENCES kategori(id), tugas_id INTEGER REFERENCES tugas(id) ON DELETE SET NULL, catatan TEXT NOT NULL DEFAULT '')");
            s.execute("CREATE TABLE IF NOT EXISTS anggaran(bulan TEXT NOT NULL,kategori_id INTEGER NOT NULL REFERENCES kategori(id),batas INTEGER NOT NULL CHECK(batas>0),PRIMARY KEY(bulan,kategori_id))");
            s.execute("CREATE TABLE IF NOT EXISTS pengingat(tugas_id INTEGER NOT NULL REFERENCES tugas(id) ON DELETE CASCADE,tenggat TEXT NOT NULL,PRIMARY KEY(tugas_id,tenggat))");
            s.execute("CREATE INDEX IF NOT EXISTS idx_transaksi_tanggal ON transaksi(tanggal)");
            s.execute("CREATE INDEX IF NOT EXISTS idx_transaksi_tugas ON transaksi(tugas_id)");
            s.execute("CREATE INDEX IF NOT EXISTS idx_tugas_tenggat ON tugas(tenggat,selesai)");
            s.execute("INSERT OR IGNORE INTO kategori(nama,jenis) VALUES ('Uang saku','Pemasukan'),('Beasiswa','Pemasukan'),('Freelance','Pemasukan'),('Lainnya','Pemasukan'),('Makan & minum','Pengeluaran'),('Transportasi','Pengeluaran'),('Kuliah','Pengeluaran'),('Belanja','Pengeluaran'),('Lainnya','Pengeluaran')");
        }
    }
}
