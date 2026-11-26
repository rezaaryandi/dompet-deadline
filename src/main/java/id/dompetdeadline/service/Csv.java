package id.dompetdeadline.service;
import id.dompetdeadline.model.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;
import java.time.*;
import java.util.*;
public final class Csv {
    private static final List<String> HEADER=List.of("tanggal", "kategori", "jenis", "jumlah", "tugas terkait", "catatan");
    private Csv() {
    }
    public static void exportFile(Path path, Data d)throws IOException {
        try(BufferedWriter w=Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            write(w, HEADER);
            for(Transaksi t:d.transaksi) {
                Kategori k=d.kategori(t.kategoriId);
                Tugas task=d.tugas(t.tugasId);
                write(w, List.of(t.tanggal.toString(), k.nama, k.jenis, Long.toString(t.jumlah), task==null?"":task.judul, t.catatan));
            }
        }
    }
    private static void write(Writer w, List<String> cells)throws IOException {
        for(int i=0; i<cells.size(); i++) {
            if(i>0)w.write(',');
            w.write('"');
            w.write(cells.get(i).replace("\"", "\"\""));
            w.write('"');
        }
        w.write("\r\n");
    }
    public static List<List<String>> parse(Reader reader)throws IOException {
        PushbackReader r=new PushbackReader(reader, 1);
        List<List<String>> rows=new ArrayList<>();
        List<String> row=new ArrayList<>();
        StringBuilder cell=new StringBuilder();
        boolean quote=false, closed=false;
        int c;
        int first=r.read();
        if(first!=-1&&first!=0xFEFF)r.unread(first);
        while((c=r.read())!=-1) {
            if(quote) {
                if(c=='"') {
                    int next=r.read();
                    if(next=='"')cell.append('"');
                    else {
                        quote=false;
                        closed=true;
                        if(next!=-1)r.unread(next);
                    }
                }
                else cell.append((char)c);
            }
            else if(c=='"'&&cell.length()==0&&!closed) {
                quote=true;
            }
            else if(c==','||c=='\n'||c=='\r') {
                row.add(cell.toString());
                cell.setLength(0);
                closed=false;
                if(c!=',') {
                    if(c=='\r') {
                        int next=r.read();
                        if(next!=-1&&next!='\n')r.unread(next);
                    }
                    rows.add(row);
                    row=new ArrayList<>();
                }
            }
            else {
                if(closed||c=='"')throw new IOException("Format kutip CSV tidak valid.");
                cell.append((char)c);
            }
        }
        if(quote)throw new IOException("Kutip CSV belum ditutup.");
        if(closed||cell.length()>0||!row.isEmpty()) {
            row.add(cell.toString());
            rows.add(row);
        }
        return rows;
    }
    public static List<Transaksi> importFile(Path path, Data d)throws IOException {
        List<List<String>> rows;
        try(Reader r=Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            rows=parse(r);
        }
        if(rows.isEmpty())throw new IOException("CSV kosong.");
        List<String> h=new ArrayList<>(rows.get(0));
        if(!h.isEmpty())h.set(0, h.get(0).replace("\uFEFF", ""));
        if(!h.equals(HEADER))throw new IOException("Header CSV harus: "+String.join(", ", HEADER));
        List<Transaksi> out=new ArrayList<>();
        for(int i=1; i<rows.size(); i++) {
            List<String> a=rows.get(i);
            if(a.size()==1&&a.get(0).isBlank())continue;
            try {
                if(a.size()!=6)throw new IllegalArgumentException("Harus ada 6 kolom.");
                Kategori k=d.kategori.stream().filter(v->v.nama.equalsIgnoreCase(a.get(1).trim())&&v.jenis.equalsIgnoreCase(a.get(2).trim())).findFirst().orElseThrow(()->new IllegalArgumentException("Kategori belum tersedia: "+a.get(1)));
                Long id=null;
                if(!a.get(4).isBlank()) {
                    List<Tugas> found=new ArrayList<>();
                    for(Tugas t:d.tugas)if(t.judul.equalsIgnoreCase(a.get(4).trim()))found.add(t);
                    if(found.size()!=1)throw new IllegalArgumentException("Judul tugas tidak ditemukan atau tidak unik: "+a.get(4));
                    id=found.get(0).id;
                }
                out.add(new Transaksi(0, LocalDate.parse(a.get(0)), Finance.amount(a.get(3)), k.id, id, a.get(5)));
            }
            catch(RuntimeException e) {
                throw new IOException("Baris "+(i+1)+": "+e.getMessage(), e);
            }
        }
        return out;
    }
}
