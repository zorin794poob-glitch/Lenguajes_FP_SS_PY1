/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package promptzal.generador_graphiz;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author jonathan-zorin
 */
public class GeneradorAFD {

    public static Path generar(Path carpeta) throws IOException, InterruptedException {
        Files.createDirectories(carpeta);
        Path dot = carpeta.resolve("afd_promptzal.dot");
        Path png = carpeta.resolve("afd_promptzal.png");
        String contenido = dot();
        Files.writeString(dot, contenido, StandardCharsets.UTF_8);

        ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dot.toAbsolutePath().toString(),
                "-o", png.toAbsolutePath().toString());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.getInputStream().readAllBytes();
        int codigo = p.waitFor();
        if (codigo != 0 || !Files.exists(png)) {
            throw new IOException("Graphviz no pudo generar la imagen. Verifique que 'dot' esté instalado y en PATH.");
        }
        return png;
    }

    public static String dot() {
        return """
        digraph AFD_PromptZal {
          rankdir=LR;
          graph [fontname="Arial", bgcolor="white"];
          node [shape=circle, fontname="Arial"];
          edge [fontname="Arial"];
          inicio [shape=point];
          q0 [label="q0\\nINICIO"];
          qID [label="qID"];
          qNUM [label="qNUM"];
          qDEC [label="qDEC"];
          qCAD [label="qCAD"];
          qDIR [label="qDIR"];
          qCOM1 [label="qCOM1"];
          qCOM2 [label="qCOM2"];
          qARR [label="qARR"];
          accID [shape=doublecircle,label="ID / palabra\\nclasificada"];
          accNUM [shape=doublecircle,label="ENTERO"];
          accDEC [shape=doublecircle,label="DECIMAL"];
          accCAD [shape=doublecircle,label="CADENA"];
          accDIR [shape=doublecircle,label="DIRECTIVA"];
          accCOM [shape=doublecircle,label="COMENTARIO\\n(no token)"];
          accARR [shape=doublecircle,label="->"];
          inicio -> q0;
          q0 -> qID [label="letra o _"];
          qID -> qID [label="letra, digito, _"];
          qID -> accID [label="otro"];
          q0 -> qNUM [label="digito"];
          qNUM -> qNUM [label="digito"];
          qNUM -> qDEC [label="."];
          qDEC -> accDEC [label="digito"];
          qDEC -> qDEC [label="digito"];
          qNUM -> accNUM [label="otro"];
          q0 -> qCAD [label="\""];
          qCAD -> qCAD [label="caracter distinto de \" y \\n"];
          qCAD -> accCAD [label="\""];
          q0 -> qDIR [label="@"];
          qDIR -> qDIR [label="letra, digito, _, -"];
          qDIR -> accDIR [label="otro"];
          q0 -> qCOM1 [label="/"];
          qCOM1 -> qCOM2 [label="/ o *"];
          qCOM2 -> qCOM2 [label="hasta cierre"];
          qCOM2 -> accCOM [label="fin de comentario"];
          q0 -> qARR [label="-"];
          qARR -> accARR [label=">"];
          q0 -> accID [label="palabra fija; clasificar por tabla"];
          q0 -> q0 [label="espacio/tab/newline"];
          q0 -> q0 [label="=,+,{,},(,),,"];
        }
        """;
    }
}
