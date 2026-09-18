package promptzal.generador_graphiz;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Genera el AFD de PromptZal usando Graphviz.
 */
public class GeneradorAFD {

    public static Path generar(Path carpeta) throws IOException, InterruptedException {
        Files.createDirectories(carpeta);

        Path dot = carpeta.resolve("afd_promptzal.dot");
        Path png = carpeta.resolve("afd_promptzal.png");
        Path svg = carpeta.resolve("afd_promptzal.svg");

        Files.writeString(dot, dot(), StandardCharsets.UTF_8);

        String ejecutable = encontrarDot();

        if (ejecutable == null) {
            throw new IOException(
                    "Graphviz no está instalado o 'dot' no está en PATH.\n\n"
                    + "En Ubuntu ejecuta:\n"
                    + "sudo apt update\n"
                    + "sudo apt install graphviz\n\n"
                    + "Después comprueba con:\n"
                    + "dot -V\n\n"
                    + "El archivo AFD.dot sí fue generado en:\n"
                    + dot.toAbsolutePath()
            );
        }

        generarArchivo(ejecutable, dot, png, "png");
        generarArchivo(ejecutable, dot, svg, "svg");

        if (!Files.exists(png)) {
            throw new IOException(
                    "Graphviz terminó pero no creó el archivo PNG.\n"
                    + "Revisa:\n" + dot.toAbsolutePath()
            );
        }

        return png;
    }

    private static void generarArchivo(
            String ejecutable, Path dot, Path salida, String formato)
            throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                ejecutable,
                "-T" + formato,
                dot.toAbsolutePath().toString(),
                "-o",
                salida.toAbsolutePath().toString()
        );

        pb.redirectErrorStream(true);

        Process proceso = pb.start();
        String salidaProceso
                = new String(proceso.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        int codigo = proceso.waitFor();

        if (codigo != 0) {
            throw new IOException(
                    "Graphviz encontró un error al generar " + formato + ".\n\n"
                    + salidaProceso
            );
        }
    }

    private static String encontrarDot() {
        String path = System.getenv("PATH");

        if (path != null) {
            java.util.StringTokenizer partes
                    = new java.util.StringTokenizer(path, java.io.File.pathSeparator);

            while (partes.hasMoreTokens()) {
                String carpeta = partes.nextToken();
                Path posible = Path.of(carpeta, "dot");
                if (Files.isRegularFile(posible) && Files.isExecutable(posible)) {
                    return posible.toAbsolutePath().toString();
                }

                // Por compatibilidad con Windows.
                posible = Path.of(carpeta, "dot.exe");
                if (Files.isRegularFile(posible) && Files.isExecutable(posible)) {
                    return posible.toAbsolutePath().toString();
                }
            }
        }

        // Rutas habituales en Linux.
        String[] comunes = {
            "/usr/bin/dot",
            "/usr/local/bin/dot",
            "/snap/bin/dot"
        };

        for (String ruta : comunes) {
            Path posible = Path.of(ruta);
            if (Files.isRegularFile(posible) && Files.isExecutable(posible)) {
                return posible.toString();
            }
        }

        return null;
    }

    public static String dot() {
        return """
        digraph AFD_PromptZal {
          rankdir=LR;
          graph [fontname="Arial", bgcolor="white", pad="0.4", nodesep="0.55", ranksep="0.8"];
          node [shape=circle, style="filled", fillcolor="white", fontname="Arial", fontsize=11];
          edge [fontname="Arial", fontsize=9, color="#425466"];

          inicio [shape=point, width=0.12, fillcolor="black"];
          inicio -> q0;

          q0 [label="q0\\nINICIO", fillcolor="#eaf2f8"];

          qID [label="qID\\nIdentificador"];
          accID [shape=doublecircle, label="ACEPTA\\nID / palabra", fillcolor="#e9f7ef"];

          qNUM [label="qNUM\\nEntero"];
          qDEC [label="qDEC\\nDecimal"];
          accNUM [shape=doublecircle, label="ACEPTA\\nENTERO", fillcolor="#e9f7ef"];
          accDEC [shape=doublecircle, label="ACEPTA\\nDECIMAL", fillcolor="#e9f7ef"];

          qCAD [label="qCAD\\nCadena"];
          accCAD [shape=doublecircle, label="ACEPTA\\nCADENA", fillcolor="#e9f7ef"];

          qDIR [label="qDIR\\nDirectiva"];
          accDIR [shape=doublecircle, label="ACEPTA\\nDIRECTIVA", fillcolor="#e9f7ef"];

          qCOM [label="qCOM\\nComentario"];
          accCOM [shape=doublecircle, label="ACEPTA\\nCOMENTARIO", fillcolor="#f2f4f5"];

          qARR [label="qARR\\nFlecha"];
          accARR [shape=doublecircle, label="ACEPTA\\n->", fillcolor="#e9f7ef"];

          accOP [shape=doublecircle, label="ACEPTA\\nOPERADOR", fillcolor="#e9f7ef"];
          accDEL [shape=doublecircle, label="ACEPTA\\nDELIMITADOR", fillcolor="#e9f7ef"];

          q0 -> qID [label="letra o _"];
          qID -> qID [label="letra, dígito, _"];
          qID -> accID [label="otro"];

          q0 -> qNUM [label="dígito"];
          qNUM -> qNUM [label="dígito"];
          qNUM -> qDEC [label="."];
          qDEC -> qDEC [label="dígito"];
          qDEC -> accDEC [label="otro"];
          qNUM -> accNUM [label="otro"];

          q0 -> qCAD [label="comilla inicial"];
          qCAD -> qCAD [label="contenido"];
          qCAD -> accCAD [label="comilla de cierre"];

          q0 -> qDIR [label="@"];
          qDIR -> qDIR [label="letra, dígito, _, -"];
          qDIR -> accDIR [label="otro"];

          q0 -> qCOM [label="/"];
          qCOM -> qCOM [label="/ o *"];
          qCOM -> accCOM [label="cierre"];

          q0 -> qARR [label="-"];
          qARR -> accARR [label=">"];

          q0 -> accOP [label="= o +"];
          q0 -> accDEL [label="{ } ( ) ,"];

          q0 -> q0 [label="espacio / tab / salto"];
        }
        """;
    }
}
