// Internal visual QA only. Never included in the production JAR.
import id.dompetdeadline.model.*;
import id.dompetdeadline.repository.Repository;
import id.dompetdeadline.view.*;
import java.time.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
public class Preview {
 public static void main(String[] args)throws Exception{
    LocalDate d=LocalDate.now();LocalDateTime t=LocalDateTime.now().withSecond(0).withNano(0);
    Data sample=new Data(java.util.List.of(new Kategori(1,"Uang saku","Pemasukan"),new Kategori(2,"Makan & minum","Pengeluaran"),new Kategori(3,"Transportasi","Pengeluaran"),new Kategori(4,"Kuliah","Pengeluaran"),new Kategori(5,"Belanja","Pengeluaran")),java.util.List.of(new Tugas(1,"Laporan praktikum PBO","Pemrograman Berorientasi Objek",t.plusHours(16),"Tinggi",false),new Tugas(2,"Desain antarmuka","Interaksi Manusia Komputer",t.plusDays(3),"Sedang",false),new Tugas(3,"Normalisasi database","Basis Data",t.plusDays(5),"Rendah",false)),java.util.List.of(new Transaksi(1,d,1800000,1,null,"Uang saku bulanan"),new Transaksi(2,d,345000,2,null,"Makan minggu ini"),new Transaksi(3,d,160000,3,null,"Transportasi kampus"),new Transaksi(4,d,125000,4,1L,"Cetak laporan"),new Transaksi(5,d,85000,4,2L,"Bahan praktikum"),new Transaksi(6,d,120000,5,null,"Kebutuhan harian")),java.util.List.of(new Anggaran(YearMonth.now(),2,400000),new Anggaran(YearMonth.now(),4,350000)));
    Repository repo=new Repository(){public Data load(){return sample;}public void save(Kategori k){}public void save(Tugas t){}public void save(Transaksi x){}public void save(Anggaran a){}public void deleteTugas(long id){}public void deleteTransaksi(long id){}public void deleteAnggaran(YearMonth m,long id){}public void importTransaksi(java.util.List<Transaksi> l){}public boolean claimReminder(Tugas t){return false;}};
    MainFrame[] f=new MainFrame[1];SwingUtilities.invokeAndWait(()->{Theme.apply(false,false);f[0]=new MainFrame(new id.dompetdeadline.service.ApplicationService(repo),false);f[0].setLocation(30,30);f[0].setVisible(true);});Thread.sleep(1400);Robot r=new Robot();Rectangle bounds=f[0].getBounds();ImageIO.write(r.createScreenCapture(bounds),"png",new File(args[0]+"/dashboard.png"));SwingUtilities.invokeAndWait(()->f[0].getJMenuBar().getMenu(1).getItem(1).doClick());Thread.sleep(500);ImageIO.write(r.createScreenCapture(bounds),"png",new File(args[0]+"/transaksi.png"));for(int i=2;i<5;i++){final int k=i;SwingUtilities.invokeAndWait(()->f[0].getJMenuBar().getMenu(1).getItem(k).doClick());Thread.sleep(350);ImageIO.write(r.createScreenCapture(bounds),"png",new File(args[0]+"/"+new String[]{"dashboard","transaksi","tugas","analisis","anggaran"}[i]+".png"));}SwingUtilities.invokeAndWait(()->{f[0].getJMenuBar().getMenu(2).getItem(0).doClick();f[0].getJMenuBar().getMenu(1).getItem(0).doClick();});Thread.sleep(500);ImageIO.write(r.createScreenCapture(bounds),"png",new File(args[0]+"/dashboard-dark.png"));SwingUtilities.invokeAndWait(()->f[0].dispose());System.exit(0);
 }
}
