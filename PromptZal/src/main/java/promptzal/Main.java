package promptzal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String direccion_Errores_Html = "/home/jonathan/Documentos/Lenguajes_SS/Lenguajes_FP_SS/PromptZal/reportes/reporte_errores.html";
    private static final String direccion_tokens_Html = "/home/jonathan/Documentos/Lenguajes_SS/Lenguajes_FP_SS/PromptZal/reportes/reporte_tokens.html";

    public static void main(String[] args) {
        menu_Inicio(args);
    }

    private static void menu_Inicio(String[] args) {
        Scanner ingreso_Valores = new Scanner(System.in);
        boolean cierre_Programa = true;
        do {
            System.out.println("Bienvenido al Analizador Lexico, que Desea Hacer?");
            System.out.print("""
                               1 <~~~~~~~~~> Analizar Lexico.pz
                               2 <~~~~~~~~~>  Ver HTML de Errores
                               3 <~~~~~~~~~> Ver HTML de Tokens
                               4 <~~~~~~~~~>  Salir de Analizador
                               """);
            int valor = ingreso_Valores.nextInt();
            switch (valor) {
                case 1:
                    cierre_Programa = lector_Documento(args);
                    break;
                case 2:
                    cierre_Programa = abrirEnFirefox(direccion_Errores_Html);
                    break;
                case 3:
                    cierre_Programa = abrirEnFirefox(direccion_tokens_Html);
                    break;
                case 4:
                    cierre_Programa = false;
                    break;
                default:
                    System.out.println("Inidique una opcion valida porfavor");
                    cierre_Programa = true;
            }
        } while (cierre_Programa);
        
        
        System.out.println("""
                              HASTA LA PROXIMA!
                               """);

    }

    private static boolean lector_Documento(String[] args) {
        Scanner ruta_Pz = new Scanner(System.in);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("        PRACTICA #1 ANALIZADOR LEXICO PROMPTZAL");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        String ruta;
        if (args.length > 0) {
            ruta = args[0];
        } else {
            System.out.print("Ingrese la Ruta del Archivo para Analizar el Lexico .pz: ");
            ruta = ruta_Pz.nextLine();
        }
        try {
            String contenido = Files.readString(Path.of(ruta));
            Lexer lexer = new Lexer(contenido);
            lexer.analizar();
            mostrarTokens(lexer.getTokens());
            mostrarErrores(lexer.getErrores());
            ReporteHTML.generarTokens(lexer.getTokens(), direccion_tokens_Html);
            ReporteHTML.generarErrores(lexer.getErrores(), direccion_Errores_Html);
            System.out.println();
            System.out.println("Análisis terminado.");
            System.out.println("Tokens reconocidos: " + lexer.getTokens().size());
            System.out.println("Errores encontrados: " + lexer.getErrores().size());
            System.out.println("Reporte Generado de tokens: reportes/reporte_tokens.html");
            System.out.println("Reporte Generado de errores: reportes/reporte_errores.html");

            System.out.println("\n/****************************************************************/");
            System.out.println("      DESEAS HACER OTRA PRUEBA? ~~~~~> 1 = SI ~~~~~~~~~> 2 = NO     ");
            System.out.println("/****************************************************************/");
            int repetidor = ruta_Pz.nextInt();
            if (repetidor == 1) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
            System.out.println("Indique Nuevamente la Ruta");
            return true;
        } catch (Exception e) {
            System.out.println("Ocurrió un error: " + e.getMessage());
            return true;
        }
    }

    private static void mostrarTokens(List<Token> tokens) {
        System.out.println();
        System.out.println("TOKENS A REVISAR");
        System.out.println("*************************************************");
        System.out.printf("%-5s %-35s %-25s %-6s %-8s%n",
                "#.", "Lexema", "Tipo", "Fila", "Columna");
        System.out.println("*************************************************");
        for (Token token : tokens) {
            System.out.printf("%-5d %-35s %-25s %-6d %-8d%n",
                    token.getNumero(),
                    token.getLexema(),
                    token.getTipo(),
                    token.getFila(),
                    token.getColumna());
        }
    }

    private static void mostrarErrores(List<ErrorLexico> errores) {
        System.out.println();
        System.out.println("ERRORES LÉXICOS");
        System.out.println("--------------------------------------------------------------------------");

        if (errores.isEmpty()) {
            System.out.println("No se encontraron errores.");
            return;
        }

        for (ErrorLexico error : errores) {
            System.out.println("Lexema: " + error.getLexema()
                    + " | Tipo: " + error.getDescripcion()
                    + " | Fila: " + error.getFila()
                    + " | Columna: " + error.getColumna());
        }
    }

    public static boolean abrirEnFirefox(String rutaHtml) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "firefox",
                    rutaHtml
            );
            pb.start();
            return true;
        } catch (IOException e) {
            System.out.println("No se pudo abrir Firefox: " + e.getMessage());
             return true;
        }
    }
}
