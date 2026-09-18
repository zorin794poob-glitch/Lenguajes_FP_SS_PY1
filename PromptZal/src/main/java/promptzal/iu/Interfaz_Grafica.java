/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package promptzal.iu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import promptzal.Lexer.Lexer;
import promptzal.generador_graphiz.GeneradorAFD;
import promptzal.modelo.ErrorLexico;
import promptzal.modelo.Token;
import promptzal.reportes.Estadisticas;
import promptzal.reportes.ReportesHtml;

/**
 *
 * @author jonathan-zorin
 */
public class Interfaz_Grafica extends JFrame {

    private final JTextArea editor = new JTextArea();
    private final JTable tablaTokens = new JTable(), tablaErrores = new JTable();
    private final JLabel estado = new JLabel("Listo."), ct = new JLabel("0"), ce = new JLabel("0"), cl = new JLabel("0");
    private Path actual;
    private List<Token> tokens = List.of();
    private List<ErrorLexico> errores = List.of();

    public Interfaz_Grafica() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        setTitle("PromptZal • Analizador Léxico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1250, 820);
        setMinimumSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);
        build();
        editor.setText(ejemplo());
        update(1);
    }

    private void build() {
        setJMenuBar(menu());
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        root.add(header(), BorderLayout.NORTH);
        JPanel c = new JPanel(new BorderLayout(12, 12));
        c.setOpaque(false);
        c.setBorder(new EmptyBorder(14, 16, 10, 16));
        c.add(editorPanel(), BorderLayout.CENTER);
        c.add(results(), BorderLayout.SOUTH);
        root.add(c, BorderLayout.CENTER);
        root.add(status(), BorderLayout.SOUTH);
        setContentPane(root);
        tables();
    }

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setBackground(new Color(25, 55, 85));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));
        JPanel x = new JPanel();
        x.setOpaque(false);
        x.setLayout(new BoxLayout(x, BoxLayout.Y_AXIS));
        JLabel a = new JLabel("PromptZal");
        a.setForeground(Color.WHITE);
        a.setFont(new Font("SansSerif", Font.BOLD, 28));
        JLabel b = new JLabel("Analizador léxico • Proyecto 1 • Jonathan Zorin ");
        b.setForeground(new Color(220, 230, 240));
        x.add(a);
        x.add(Box.createVerticalStrut(3));
        x.add(b);
        p.add(x, BorderLayout.WEST);
        JPanel z = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        z.setOpaque(false);
        JButton an = button("▶  Analizar", new Color(42, 115, 85));
        an.addActionListener(e -> analizar());
        JButton af = button("◈  AFD", new Color(72, 88, 120));
        af.addActionListener(e -> afd());
        z.add(an);
        z.add(af);
        p.add(z, BorderLayout.EAST);
        return p;
    }

    private JPanel editorPanel() {
        JPanel p = card("EDITOR .PZ");
        JPanel t = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        t.setOpaque(false);
        JButton l = sec("Limpiar");
        l.addActionListener(e -> editor.setText(""));
        JButton ej = sec("Cargar ejemplo");
        ej.addActionListener(e -> editor.setText(ejemplo()));
        JButton cp = sec("Copiar");
        cp.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(editor.getText()), null));
        t.add(l);
        t.add(ej);
        t.add(cp);
        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        editor.setTabSize(4);
        editor.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(t, BorderLayout.NORTH);
        p.add(new JScrollPane(editor), BorderLayout.CENTER);
        return p;
    }

    private JPanel results() {
        JPanel p = card("RESULTADOS");
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Tokens", new JScrollPane(tablaTokens));
        tabs.addTab("Errores", new JScrollPane(tablaErrores));
        JPanel m = new JPanel(new GridLayout(1, 3, 10, 0));
        m.setOpaque(false);
        m.setBorder(new EmptyBorder(10, 0, 0, 0));
        m.add(metric("TOKENS", ct));
        m.add(metric("ERRORES", ce));
        m.add(metric("LÍNEAS", cl));
        p.add(tabs, BorderLayout.CENTER);
        p.add(m, BorderLayout.SOUTH);
        p.setPreferredSize(new Dimension(1000, 330));
        return p;
    }

    private JPanel metric(String s, JLabel v) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(215, 220, 226)));
        JLabel l = new JLabel(s);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        v.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        return p;
    }

    private JPanel status() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(232, 236, 240));
        p.setBorder(new EmptyBorder(6, 16, 7, 16));
        p.add(estado, BorderLayout.WEST);
        return p;
    }

    private JPanel card(String s) {
        JPanel p = new JPanel(new BorderLayout(0, 7));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(215, 220, 226)), new EmptyBorder(10, 12, 12, 12)));
        JLabel l = new JLabel(s);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        p.add(l, BorderLayout.NORTH);
        return p;
    }

    private JButton button(String s, Color c) {
        JButton b = new JButton(s);
        b.setForeground(Color.WHITE);
        b.setBackground(c);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
    }

    private JButton sec(String s) {
        JButton b = new JButton(s);
        b.setFocusPainted(false);
        return b;
    }

    private void tables() {
        tablaTokens.setModel(new javax.swing.table.DefaultTableModel(new Object[]{"#", "Lexema", "Tipo", "Fila", "Columna"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        tablaErrores.setModel(new javax.swing.table.DefaultTableModel(new Object[]{"Lexema", "Tipo", "Descripción", "Fila", "Columna"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        tablaTokens.setAutoCreateRowSorter(true);
        tablaErrores.setAutoCreateRowSorter(true);
        tablaTokens.setRowHeight(27);
        tablaErrores.setRowHeight(27);
    }

    private JMenuBar menu() {
        JMenuBar m = new JMenuBar();
        JMenu a = new JMenu("Archivo");
        a.add(item("Abrir .pz", e -> abrir()));
        a.add(item("Guardar", e -> guardar()));
        a.add(item("Guardar como...", e -> guardarComo()));
        a.addSeparator();
        a.add(item("Salir", e -> dispose()));
        JMenu r = new JMenu("Reportes HTML");
        r.add(item("Tokens", e -> open(Path.of("reportes/reporte_tokens.html"))));
        r.add(item("Errores", e -> open(Path.of("reportes/reporte_errores.html"))));
        r.add(item("Estadísticas", e -> open(Path.of("reportes/reporte_estadisticas.html"))));
        m.add(a);
        m.add(r);
        return m;
    }

    private JMenuItem item(String s, java.awt.event.ActionListener a) {
        JMenuItem i = new JMenuItem(s);
        i.addActionListener(a);
        return i;
    }

    private void analizar() {
        Lexer l = new Lexer(editor.getText());
        l.analizar();
        tokens = l.getTokens();
        errores = l.getErrores();
        load();
        update(l.getTotalLineas());
        try {
            Path d = Path.of("reportes");
            ReportesHtml.generarTokens(tokens, d.resolve("reporte_tokens.html"));
            ReportesHtml.generarErrores(errores, d.resolve("reporte_errores.html"));
            ReportesHtml.generarEstadisticas(new Estadisticas(tokens, errores, l.getTotalLineas()), d.resolve("reporte_estadisticas.html"));
            estado.setText("Análisis terminado • " + (tokens.size() - 1) + " tokens • " + errores.size() + " errores.");
        } catch (Exception e) {
            error(e);
        }
    }

    private void load() {
        var a = (javax.swing.table.DefaultTableModel) tablaTokens.getModel();
        a.setRowCount(0);
        for (Token t : tokens) {
            a.addRow(new Object[]{t.getNumero(), t.getLexema(), t.getTipo(), t.getFila(), t.getColumna()});
        }
        var b = (javax.swing.table.DefaultTableModel) tablaErrores.getModel();
        b.setRowCount(0);
        for (ErrorLexico e : errores) {
            b.addRow(new Object[]{e.getLexema(), e.getTipo(), e.getDescripcion(), e.getFila(), e.getColumna()});
        }
    }

    private void update(int lines) {
        ct.setText(String.valueOf(Math.max(0, tokens.size() - 1)));
        ce.setText(String.valueOf(errores.size()));
        cl.setText(String.valueOf(lines));
    }

    private void abrir() {
        JFileChooser f = new JFileChooser();
        f.setFileFilter(new FileNameExtensionFilter("PromptZal (*.pz)", "pz"));
        if (f.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)try {
            actual = f.getSelectedFile().toPath();
            editor.setText(Files.readString(actual, StandardCharsets.UTF_8));
            estado.setText("Abierto: " + actual.getFileName());
        } catch (Exception e) {
            error(e);
        }
    }

    private void guardar() {
        if (actual == null) {
            guardarComo();
            return;
        }
        try {
            Files.writeString(actual, editor.getText(), StandardCharsets.UTF_8);
            estado.setText("Guardado: " + actual.getFileName());
        } catch (Exception e) {
            error(e);
        }
    }

    private void guardarComo() {
        JFileChooser f = new JFileChooser();
        f.setFileFilter(new FileNameExtensionFilter("PromptZal (*.pz)", "pz"));
        if (f.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            Path p = f.getSelectedFile().toPath();
            if (!p.toString().endsWith(".pz")) {
                p = Path.of(p + ".pz");
            }
            actual = p;
            guardar();
        }
    }

    private void afd() {
        try {
            Path p = GeneradorAFD.generar(Path.of("reportes"));
            estado.setText("AFD generado.");
            open(p);
        } catch (Exception e) {
            error(e);
        }
    }

    private void open(Path p) {
        try {
            if (Files.exists(p)) {
                Desktop.getDesktop().open(p.toFile());
            } else {
                JOptionPane.showMessageDialog(this, "Primero genere el archivo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            error(e);
        }
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String ejemplo() {
        return """
@modelo "claude-sonnet-4-6"
@rol "analista de datos"
@formato "markdown"

// Agente que prepara el analisis
AGENTE analista {
    contexto = "Eres un analista de datos experto"
    variable ventas = CARGAR("ventas.csv")
    PREGUNTAR "Cuales son las 3 tendencias principales?" SOBRE ventas -> tendencias
    RESUMIR tendencias EN 100 palabras -> resumen
}
EJECUTAR analista
EXPORTAR resumen
""";
    }
}
