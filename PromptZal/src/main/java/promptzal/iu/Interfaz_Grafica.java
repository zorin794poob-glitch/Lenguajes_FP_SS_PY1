package promptzal.iu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import promptzal.Lexer.Lexer;
import promptzal.generador_graphiz.GeneradorAFD;
import promptzal.modelo.ErrorLexico;
import promptzal.modelo.Token;
import promptzal.reportes.Estadisticas;
import promptzal.reportes.ReportesHtml;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * Interfaz gráfica principal de PromptZal. Se conserva la funcionalidad
 * original y se mejora solamente la presentación.
 */
public class Interfaz_Grafica extends JFrame {

    private static final Color AZUL = new Color(25, 55, 85);
    private static final Color AZUL_CLARO = new Color(42, 115, 155);
    private static final Color VERDE = new Color(42, 115, 85);
    private static final Color FONDO = new Color(244, 247, 250);
    private static final Color BORDE = new Color(215, 222, 229);

    private final JTextArea editor = new JTextArea();
    private final JTable tablaTokens = new JTable();
    private final JTable tablaErrores = new JTable();
    private final JLabel estado = new JLabel("Listo para analizar.");
    private final JLabel ct = new JLabel("0");
    private final JLabel ce = new JLabel("0");
    private final JLabel cl = new JLabel("0");

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
        actualizarContadores(1);
    }

    private void build() {
        setJMenuBar(menu());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        root.add(header(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(12, 12));
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(14, 16, 10, 16));

        centro.add(editorPanel(), BorderLayout.CENTER);

        JSplitPane resultados = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                editorPanel(),
                results()
        );
        resultados.setDividerLocation(500);
        resultados.setResizeWeight(0.60);
        resultados.setBorder(null);
        resultados.setOpaque(false);

        // El editor y resultados se muestran como una sola zona dividida.
        centro.removeAll();
        centro.add(resultados, BorderLayout.CENTER);

        root.add(centro, BorderLayout.CENTER);
        root.add(status(), BorderLayout.SOUTH);

        setContentPane(root);
        tables();
    }

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout(20, 0));
        p.setBackground(AZUL);
        p.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel identidad = new JPanel();
        identidad.setOpaque(false);
        identidad.setLayout(new BoxLayout(identidad, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("PromptZal");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));

        JLabel subtitulo = new JLabel(
                "Analizador léxico  •  Proyecto 1  • Jonathan Zorin");
        subtitulo.setForeground(new Color(218, 229, 239));
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        identidad.add(titulo);
        identidad.add(Box.createVerticalStrut(4));
        identidad.add(subtitulo);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 2));
        acciones.setOpaque(false);

        JButton analizar = button("▶  Analizar", VERDE);
        JButton afd = button("◇  Generar AFD", AZUL_CLARO);

        analizar.setToolTipText("Analizar el archivo carácter por carácter");
        afd.setToolTipText("Generar el AFD con Graphviz");

        analizar.addActionListener(e -> analizar());
        afd.addActionListener(e -> afd());

        acciones.add(analizar);
        acciones.add(afd);

        p.add(identidad, BorderLayout.WEST);
        p.add(acciones, BorderLayout.EAST);

        return p;
    }

    private JPanel editorPanel() {
        JPanel p = card("EDITOR .PZ");

        JPanel herramientas = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 5));
        herramientas.setOpaque(false);

        JButton abrir = sec("Abrir");
        JButton guardar = sec("Guardar");
        JButton guardarComo = sec("Guardar como");
        JButton limpiar = sec("Limpiar");
        JButton ejemplo = sec("Cargar ejemplo");
        JButton copiar = sec("Copiar");

        abrir.addActionListener(e -> abrir());
        guardar.addActionListener(e -> guardar());
        guardarComo.addActionListener(e -> guardarComo());
        limpiar.addActionListener(e -> {
            editor.setText("");
            estado.setText("Editor limpiado.");
        });
        ejemplo.addActionListener(e -> {
            editor.setText(ejemplo());
            estado.setText("Ejemplo cargado.");
        });
        copiar.addActionListener(e -> copiarTexto());

        herramientas.add(abrir);
        herramientas.add(guardar);
        herramientas.add(guardarComo);
        herramientas.add(Box.createHorizontalStrut(8));
        herramientas.add(ejemplo);
        herramientas.add(limpiar);
        herramientas.add(copiar);

        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        editor.setTabSize(4);
        editor.setLineWrap(false);
        editor.setBackground(new Color(252, 253, 255));
        editor.setForeground(new Color(35, 43, 52));
        editor.setCaretColor(AZUL_CLARO);
        editor.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(editor);
        scroll.setBorder(new LineBorder(BORDE));

        p.add(herramientas, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private JPanel results() {
        JPanel p = card("RESULTADOS DEL ANÁLISIS");

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabs.addTab("  Tokens  ", new JScrollPane(tablaTokens));
        tabs.addTab("  Errores  ", new JScrollPane(tablaErrores));

        JPanel metricas = new JPanel(new GridLayout(1, 3, 10, 0));
        metricas.setOpaque(false);
        metricas.setBorder(new EmptyBorder(10, 0, 0, 0));

        metricas.add(metric("TOKENS RECONOCIDOS", ct, AZUL_CLARO));
        metricas.add(metric("ERRORES LÉXICOS", ce, new Color(165, 75, 60)));
        metricas.add(metric("LÍNEAS", cl, VERDE));

        p.add(tabs, BorderLayout.CENTER);
        p.add(metricas, BorderLayout.SOUTH);

        return p;
    }

    private JPanel metric(String titulo, JLabel valor, Color color) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDE),
                new EmptyBorder(8, 13, 8, 13)));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 10));
        t.setForeground(new Color(100, 112, 124));

        valor.setFont(new Font("SansSerif", Font.BOLD, 22));
        valor.setForeground(color);

        p.add(t, BorderLayout.WEST);
        p.add(valor, BorderLayout.EAST);

        return p;
    }

    private JPanel status() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(232, 237, 242));
        p.setBorder(new EmptyBorder(7, 16, 8, 16));

        estado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        estado.setForeground(new Color(75, 88, 101));

        p.add(estado, BorderLayout.WEST);
        return p;
    }

    private JPanel card(String titulo) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDE),
                new EmptyBorder(10, 12, 12, 12)));

        JLabel l = new JLabel(titulo);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(AZUL);

        p.add(l, BorderLayout.NORTH);
        return p;
    }

    private JButton button(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(10, 15, 10, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton sec(String texto) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDE),
                new EmptyBorder(7, 11, 7, 11)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void tables() {
        tablaTokens.setModel(new javax.swing.table.DefaultTableModel(
                new Object[]{"#", "Lexema", "Tipo", "Fila", "Columna"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });

        tablaErrores.setModel(new javax.swing.table.DefaultTableModel(
                new Object[]{"Lexema", "Tipo", "Descripción", "Fila", "Columna"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });

        prepararTabla(tablaTokens);
        prepararTabla(tablaErrores);
    }

    private void prepararTabla(JTable tabla) {
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(28);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setGridColor(new Color(228, 233, 238));
        tabla.setSelectionBackground(new Color(218, 231, 242));
        tabla.setSelectionForeground(new Color(30, 40, 50));

        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(AZUL);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 30));
    }

    private JMenuBar menu() {
        JMenuBar m = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        archivo.add(item("Abrir .pz", e -> abrir()));
        archivo.add(item("Guardar", e -> guardar()));
        archivo.add(item("Guardar como...", e -> guardarComo()));
        archivo.addSeparator();
        archivo.add(item("Salir", e -> dispose()));

        JMenu reportes = new JMenu("Reportes HTML");
        reportes.add(item("Tokens", e -> open(Path.of("reportes/reporte_tokens.html"))));
        reportes.add(item("Errores", e -> open(Path.of("reportes/reporte_errores.html"))));
        reportes.add(item("Estadísticas", e -> open(Path.of("reportes/reporte_estadisticas.html"))));

        m.add(archivo);
        m.add(reportes);

        return m;
    }

    private JMenuItem item(String texto, java.awt.event.ActionListener accion) {
        JMenuItem i = new JMenuItem(texto);
        i.addActionListener(accion);
        return i;
    }

    private void analizar() {
        Lexer l = new Lexer(editor.getText());
        l.analizar();

        tokens = l.getTokens();
        errores = l.getErrores();

        cargarTablas();
        actualizarContadores(l.getTotalLineas());

        try {
            Path carpeta = Path.of("reportes");
            Files.createDirectories(carpeta);

            ReportesHtml.generarTokens(
                    tokens, carpeta.resolve("reporte_tokens.html"));

            ReportesHtml.generarErrores(
                    errores, carpeta.resolve("reporte_errores.html"));

            ReportesHtml.generarEstadisticas(
                    new Estadisticas(tokens, errores, l.getTotalLineas()),
                    carpeta.resolve("reporte_estadisticas.html"));

            estado.setText("Análisis terminado  •  "
                    + contarTokensReales() + " tokens  •  "
                    + errores.size() + " errores.");

        } catch (Exception e) {
            error(e);
        }
    }

    private int contarTokensReales() {
        int total = 0;
        for (Token t : tokens) {
            if (t.getTipo() != promptzal.modelo.TipoToken.EOF) {
                total++;
            }
        }
        return total;
    }

    private void cargarTablas() {
        var a = (javax.swing.table.DefaultTableModel) tablaTokens.getModel();
        a.setRowCount(0);

        for (Token t : tokens) {
            if (t.getTipo() == promptzal.modelo.TipoToken.EOF) {
                continue;
            }
            a.addRow(new Object[]{
                t.getNumero(), t.getLexema(), t.getTipo(),
                t.getFila(), t.getColumna()
            });
        }

        var b = (javax.swing.table.DefaultTableModel) tablaErrores.getModel();
        b.setRowCount(0);

        for (ErrorLexico e : errores) {
            b.addRow(new Object[]{
                e.getLexema(), e.getTipo(), e.getDescripcion(),
                e.getFila(), e.getColumna()
            });
        }
    }

    private void actualizarContadores(int lineas) {
        ct.setText(String.valueOf(contarTokensReales()));
        ce.setText(String.valueOf(errores.size()));
        cl.setText(String.valueOf(lineas));
    }

    private void abrir() {
        JFileChooser f = new JFileChooser();
        f.setDialogTitle("Abrir programa PromptZal");
        f.setFileFilter(new FileNameExtensionFilter(
                "Archivos PromptZal (*.pz)", "pz"));

        if (f.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                actual = f.getSelectedFile().toPath();
                editor.setText(Files.readString(actual, StandardCharsets.UTF_8));
                estado.setText("Archivo abierto  •  " + actual.getFileName());
            } catch (Exception e) {
                error(e);
            }
        }
    }

    private void guardar() {
        if (actual == null) {
            guardarComo();
            return;
        }

        try {
            Files.writeString(actual, editor.getText(), StandardCharsets.UTF_8);
            estado.setText("Archivo guardado  •  " + actual.getFileName());
        } catch (Exception e) {
            error(e);
        }
    }

    private void guardarComo() {
        JFileChooser f = new JFileChooser();
        f.setDialogTitle("Guardar programa PromptZal");
        f.setFileFilter(new FileNameExtensionFilter(
                "Archivos PromptZal (*.pz)", "pz"));

        if (f.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            Path p = f.getSelectedFile().toPath();

            if (!p.toString().toLowerCase().endsWith(".pz")) {
                p = Path.of(p + ".pz");
            }

            actual = p;
            guardar();
        }
    }

    private void copiarTexto() {
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(editor.getText()), null);
        estado.setText("Contenido copiado al portapapeles.");
    }

    private void afd() {
        try {
            Path png = GeneradorAFD.generar(Path.of("reportes"));

            estado.setText("AFD generado correctamente.");

            open(png);

            JOptionPane.showMessageDialog(
                    this,
                    "El AFD fue generado correctamente.\n\n"
                    + "PNG:\n" + png.toAbsolutePath()
                    + "\n\nDOT:\n"
                    + png.getParent().resolve("afd_promptzal.dot").toAbsolutePath(),
                    "AFD • Graphviz",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            String mensaje = e.getMessage();

            JOptionPane.showMessageDialog(
                    this,
                    mensaje == null
                            ? "No fue posible generar el AFD."
                            : mensaje,
                    "Graphviz",
                    JOptionPane.WARNING_MESSAGE
            );

            estado.setText("No se pudo generar el PNG del AFD.");
        }
    }

    private void open(Path p) {
        try {
            if (!Files.exists(p)) {
                JOptionPane.showMessageDialog(
                        this,
                        "El archivo todavía no existe:\n" + p.toAbsolutePath(),
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!Desktop.isDesktopSupported()) {                JOptionPane.showMessageDialog(
                        this,
                        "El sistema no permite abrir archivos automáticamente.\n"
                        + "Puedes abrir manualmente:\n" + p.toAbsolutePath(),
                        "Aviso",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            Desktop.getDesktop().open(p.toFile());

        } catch (Exception e) {
            error(e);
        }
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
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
