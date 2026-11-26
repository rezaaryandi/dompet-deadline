package id.dompetdeadline.view;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.*;
final class Ui {
    static JPanel panel(LayoutManager l) {
        JPanel p=new JPanel(l);
        p.setOpaque(false);
        return p;
    }
    static JPanel vertical() {
        JPanel p=panel(new FlowLayout());
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }
    // layout assigned before use
    static JPanel card(LayoutManager l) {
        JPanel p=new JPanel(l) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D a=(Graphics2D)g.create();
                a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                a.setColor(Theme.surface);
                a.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
                a.setColor(Theme.line);
                a.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
                a.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 22, 20, 22));
        return p;
    }
    static JLabel label(String s, int size, boolean bold, Color c) {
        JLabel l=new JLabel(s);
        l.setFont(new Font("SansSerif", bold?Font.BOLD:Font.PLAIN, size));
        l.setForeground(c);
        return l;
    }
    static JButton button(String text, boolean primary, Runnable action) {
        JButton b=new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D a=(Graphics2D)g.create();
                a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill=primary?Theme.accent:Theme.surface;
                if(getModel().isRollover())fill=primary?Theme.accent.darker():Theme.accentSoft;
                a.setColor(fill);
                a.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 12, 12);
                if(!primary) {
                    a.setColor(Theme.line);
                    a.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 12, 12);
                }
                a.setColor(primary?(Theme.dark?Theme.NAVY:Color.WHITE):Theme.ink);
                a.setFont(getFont());
                FontMetrics f=a.getFontMetrics();
                a.drawString(getText(),(getWidth()-f.stringWidth(getText()))/2,(getHeight()-f.getHeight())/2+f.getAscent());
                if(isFocusOwner()) {
                    a.setColor(Theme.accent);
                    a.drawRoundRect(3, 3, getWidth()-7, getHeight()-7, 9, 9);
                }
                a.dispose();
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(primary?Color.WHITE:Theme.ink);
        b.setContentAreaFilled(false);
        b.setBorder(new EmptyBorder(10, 15, 10, 15));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e->action.run());
        return b;
    }
    static JTextField field(String value, int columns) {
        JTextField f=new JTextField(value, columns);
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(150, 39));
        f.setBackground(Theme.surface);f.setForeground(Theme.ink);f.setCaretColor(Theme.ink);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.line),new EmptyBorder(7,10,7,10)));
        return f;
    }
    static <T> JComboBox<T> combo(T[] a) {
        JComboBox<T> c=new JComboBox<>(a);
        c.setFont(new Font("SansSerif", Font.PLAIN, 13));
        c.setPreferredSize(new Dimension(150, 39));
        return c;
    }
    static JPanel form() {
        return panel(new GridBagLayout());
    }
    static void field(JPanel p, int row, String label, JComponent c) {
        GridBagConstraints g=new GridBagConstraints();
        g.gridx=0;
        g.gridy=row;
        g.insets=new Insets(6, 0, 6, 15);
        g.anchor=GridBagConstraints.WEST;
        JLabel l=label(label, 13, true, Theme.ink);
        l.setLabelFor(c instanceof JScrollPane ? ((JScrollPane)c).getViewport().getView() : c);
        p.add(l, g);
        g.gridx=1;
        g.weightx=1;
        g.fill=GridBagConstraints.HORIZONTAL;
        g.insets=new Insets(6, 0, 6, 0);
        p.add(c, g);
    }
    static JScrollPane scroll(Component c) {
        JScrollPane s=new JScrollPane(c);
        s.setBorder(BorderFactory.createEmptyBorder());
        s.getViewport().setBackground(Theme.surface);
        s.getVerticalScrollBar().setUnitIncrement(16);
        return s;
    }
    static DefaultTableModel model(String[] headers, Class<?>[] types) {
        return new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
            @Override public Class<?> getColumnClass(int c) {
                return types[c];
            }
        };
    }
    static JTable table(DefaultTableModel m, int...money) {
        JTable t=new JTable(m) {
            @Override public String getToolTipText(MouseEvent e) {
                int row=rowAtPoint(e.getPoint()),col=columnAtPoint(e.getPoint());
                if(row<0||col<0)return null;
                Object v=getValueAt(row,col);
                return v==null?null:String.valueOf(v);
            }
        };
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setRowHeight(49);
        t.setShowGrid(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(Theme.line);
        t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setBackground(Theme.surface);
        t.setForeground(Theme.ink);
        t.setSelectionBackground(Theme.accentSoft);
        t.setSelectionForeground(Theme.ink);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.getTableHeader().setPreferredSize(new Dimension(0, 43));
        t.getTableHeader().setBackground(Theme.bg);
        t.getTableHeader().setForeground(Theme.muted);
        for(int i:money)t.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
            @Override protected void setValue(Object v) {
                setText(v==null?"":id.dompetdeadline.service.Finance.rupiah(((Number)v).longValue()));
                setHorizontalAlignment(RIGHT);
            }
        });
        // Keep sorting by the real underlying value; style all cells at render time.
        JTableHeader header=t.getTableHeader();
        header.setDefaultRenderer((table,value,selected,focus,row,col)-> {
            String caption=String.valueOf(value);
            if(table.getRowSorter()!=null)for(RowSorter.SortKey key:table.getRowSorter().getSortKeys()) {
                if(key.getColumn()==table.convertColumnIndexToModel(col))caption+=key.getSortOrder()==SortOrder.ASCENDING?"  ↑":"  ↓";
            }
            JLabel l=label(caption,12,true,Theme.ink);l.setOpaque(true);l.setBackground(Theme.accentSoft);
            l.setBorder(new EmptyBorder(0,12,0,10));return l;
        });
        for(int i=0;i<t.getColumnCount();i++) {
            TableColumn column=t.getColumnModel().getColumn(i);
            TableCellRenderer renderer=column.getCellRenderer();
            if(renderer==null)renderer=t.getDefaultRenderer(m.getColumnClass(i));
            final TableCellRenderer base=renderer;
            column.setCellRenderer((table,value,selected,focus,row,col)-> {
                Component comp=base.getTableCellRendererComponent(table,value,selected,focus,row,col);
                if(comp instanceof JLabel) {
                    JLabel l=(JLabel)comp; l.setOpaque(true);
                    l.setBackground(selected?Theme.accentSoft:(row%2==0?Theme.surface:Theme.mix(Theme.surface,Theme.bg,50)));
                    l.setForeground(Theme.ink); l.setBorder(new EmptyBorder(0,12,0,12));
                    String v=String.valueOf(value);
                    if(java.util.Set.of("Tinggi","Sedang","Rendah","Aktif","Selesai","Terlambat","Pemasukan","Pengeluaran","Aman","Hampir habis","Batas tercapai").contains(v)) {
                        l.setFont(l.getFont().deriveFont(Font.BOLD)); l.setForeground(Theme.accent);
                    }
                }
                return comp;
            });
        }
        t.removeColumn(t.getColumnModel().getColumn(0));
        return t;
    }
    static void changes(JTextField f, Runnable r) {
        f.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                r.run();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                r.run();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                r.run();
            }
        });
    }
    static void gap(JPanel p, int h) {
        p.add(Box.createVerticalStrut(h));
    }
    static JPanel heading(String title, String sub) {
        JPanel p=vertical();
        JLabel a=label(title, 31, true, Theme.ink), b=label(sub, 13, false, Theme.muted);
        a.setAlignmentX(0);
        b.setAlignmentX(0);
        p.add(a);
        gap(p, 5);
        p.add(b);
        return p;
    }
}
