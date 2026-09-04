package promptzal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReporteHTML {

    public static void generarTokens(List<Token> tokens, String ruta) throws IOException {
        File archivo = prepararArchivo(ruta);

        try (BufferedWriter out = new BufferedWriter(new FileWriter(archivo))) {
            out.write("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>");
            out.write("<title>Reporte de Tokens - PromptZal</title>");
            out.write("<style>body{font-family:Arial;margin:30px}table{border-collapse:collapse;width:100%}");
            out.write("th,td{border:1px solid #999;padding:8px;text-align:left}th{background:#ddd}");
            out.write("</style></head><body>");
            out.write("<h1>Reporte de Tokens</h1>");
            out.write("<table><tr><th>No.</th><th>Lexema</th><th>Tipo</th><th>Fila</th><th>Columna</th></tr>");

            for (Token token : tokens) {
                out.write("<tr>");
                out.write("<td>" + token.getNumero() + "</td>");
                out.write("<td>" + escapar(token.getLexema()) + "</td>");
                out.write("<td>" + token.getTipo() + "</td>");
                out.write("<td>" + token.getFila() + "</td>");
                out.write("<td>" + token.getColumna() + "</td>");
                out.write("</tr>");
            }

            out.write("</table></body></html>");
        }
    }

    public static void generarErrores(List<ErrorLexico> errores, String ruta) throws IOException {
        File archivo = prepararArchivo(ruta);

        try (BufferedWriter out = new BufferedWriter(new FileWriter(archivo))) {
            out.write("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>");
            out.write("<title>Reporte de Errores - PromptZal</title>");
            out.write("<style>body{font-family:Arial;margin:30px}table{border-collapse:collapse;width:100%}");
            out.write("th,td{border:1px solid #999;padding:8px;text-align:left}th{background:#ddd}");
            out.write("</style></head><body>");
            out.write("<h1>Reporte de Errores Léxicos</h1>");

            if (errores.isEmpty()) {
                out.write("<p>No se encontraron errores léxicos.</p>");
            } else {
                out.write("<table><tr><th>Lexema</th><th>Descripción</th><th>Fila</th><th>Columna</th></tr>");

                for (ErrorLexico error : errores) {
                    out.write("<tr>");
                    out.write("<td>" + escapar(error.getLexema()) + "</td>");
                    out.write("<td>" + escapar(error.getDescripcion()) + "</td>");
                    out.write("<td>" + error.getFila() + "</td>");
                    out.write("<td>" + error.getColumna() + "</td>");
                    out.write("</tr>");
                }

                out.write("</table>");
            }

            out.write("</body></html>");
        }
    }

    private static File prepararArchivo(String ruta) throws IOException {
        File archivo = new File(ruta);
        File padre = archivo.getParentFile();

        if (padre != null && !padre.exists() && !padre.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta: " + padre);
        }

        return archivo;
    }

    private static String escapar(String texto) {
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);

            if (c == '&') resultado.append("&amp;");
            else if (c == '<') resultado.append("&lt;");
            else if (c == '>') resultado.append("&gt;");
            else if (c == '"') resultado.append("&quot;");
            else resultado.append(c);
        }

        return resultado.toString();
    }
}
