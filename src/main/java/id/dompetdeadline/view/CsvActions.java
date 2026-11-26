package id.dompetdeadline.view;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

final class CsvActions {
    private final MainFrame frame;
    CsvActions(MainFrame frame) { this.frame = frame; }

    void exportCsv() {
        if(!frame.ready())return;
        JFileChooser c=new JFileChooser();
        c.setDialogTitle("Simpan seluruh transaksi sebagai CSV");
        c.setFileFilter(new FileNameExtensionFilter("CSV (*.csv)", "csv"));
        c.setSelectedFile(new java.io.File("transaksi.csv"));
        if(c.showSaveDialog(frame)!=JFileChooser.APPROVE_OPTION)return;
        Path path=c.getSelectedFile().toPath();
        if(!path.toString().toLowerCase(Locale.ROOT).endsWith(".csv"))path=Path.of(path+".csv");
        if(Files.exists(path)&&!frame.confirm("Timpa file yang sudah ada?"))return;
        Path file=path;
        frame.exportFile(file);
    }
    void importCsv() {
        if(!frame.ready())return;
        JFileChooser c=new JFileChooser();
        c.setDialogTitle("Impor transaksi dari CSV");
        c.setFileFilter(new FileNameExtensionFilter("CSV (*.csv)", "csv"));
        if(c.showOpenDialog(frame)!=JFileChooser.APPROVE_OPTION)return;
        if(!frame.confirm("Impor akan menambahkan transaksi baru. Pastikan file belum pernah diimpor untuk mencegah duplikasi. Lanjutkan?"))return;
        Path path=c.getSelectedFile().toPath();
        frame.importFile(path);
    }
}
