/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package promptzal.Lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import promptzal.modelo.ErrorLexico;
import promptzal.modelo.TipoToken;
import promptzal.modelo.Token;

/**
 *
 * @author jonathan-zorin
 */
public class Lexer {

    private String entrada;
    private int posicion;
    private int fila;
    private int columna;
    private int numeroToken;
    private int totalLineas;
    private final List<Token> tokens = new ArrayList<>();
    private final List<ErrorLexico> errores = new ArrayList<>();

    private static final Map<String, TipoToken> PALABRAS = new HashMap<>();

    static {
        PALABRAS.put("AGENTE", TipoToken.PALABRA_RESERVADA);
        PALABRAS.put("contexto", TipoToken.PALABRA_RESERVADA);
        PALABRAS.put("variable", TipoToken.PALABRA_RESERVADA);
        PALABRAS.put("EJECUTAR", TipoToken.PALABRA_RESERVADA);
        PALABRAS.put("EXPORTAR", TipoToken.PALABRA_RESERVADA);

        PALABRAS.put("PREGUNTAR", TipoToken.COMANDO_IA);
        PALABRAS.put("GENERAR", TipoToken.COMANDO_IA);
        PALABRAS.put("RESUMIR", TipoToken.COMANDO_IA);
        PALABRAS.put("ANALIZAR", TipoToken.COMANDO_IA);
        PALABRAS.put("TRADUCIR", TipoToken.COMANDO_IA);
        PALABRAS.put("CLASIFICAR", TipoToken.COMANDO_IA);
        PALABRAS.put("EXTRAER", TipoToken.COMANDO_IA);

        PALABRAS.put("SOBRE", TipoToken.CONECTOR);
        PALABRAS.put("DESDE", TipoToken.CONECTOR);
        PALABRAS.put("EN", TipoToken.CONECTOR);
        PALABRAS.put("COMO", TipoToken.CONECTOR);

        PALABRAS.put("CARGAR", TipoToken.FUNCION);
    }

    public Lexer(String entrada) {
        this.entrada = entrada == null ? "" : entrada;
        reiniciar();
    }

    private void reiniciar() {
        posicion = 0;
        fila = 1;
        columna = 1;
        numeroToken = 1;
        totalLineas = contarLineas(entrada);
        tokens.clear();
        errores.clear();
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada == null ? "" : entrada;
        reiniciar();
    }

    public void analizar() {
        reiniciar();
        while (!fin()) {
            char c = actual();

            if (esEspacio(c)) {
                avanzar();
            } else if (c == '/') {
                procesarComentarioODivision();
            } else if (c == '"') {
                procesarCadena();
            } else if (c == '@') {
                procesarDirectiva();
            } else if (esLetra(c) || c == '_') {
                procesarIdentificadorOPalabra();
            } else if (esDigito(c)) {
                procesarNumero();
            } else if (c == '-' && siguiente() == '>') {
                int f = fila, col = columna;
                avanzar();
                avanzar();
                agregar("->", TipoToken.CONECTOR, f, col);
            } else if (c == '=' || c == '+') {
                int f = fila, col = columna;
                avanzar();
                agregar(String.valueOf(c), TipoToken.OPERADOR, f, col);
            } else if (esDelimitador(c)) {
                int f = fila, col = columna;
                avanzar();
                agregar(String.valueOf(c), TipoToken.DELIMITADOR, f, col);
            } else {
                registrarError(String.valueOf(c), "CARACTER_NO_RECONOCIDO",
                        "Carácter no reconocido por el lenguaje.", fila, columna);
                avanzar();
            }
        }

        tokens.add(new Token(numeroToken, "", TipoToken.EOF, fila, columna));
    }

    private void procesarComentarioODivision() {
        int f = fila, col = columna;
        if (siguiente() == '/') {
            avanzar();
            avanzar();
            while (!fin() && actual() != '\n') {
                avanzar();
            }
            return;
        }
        if (siguiente() == '*') {
            avanzar();
            avanzar();
            while (!fin()) {
                if (actual() == '*' && siguiente() == '/') {
                    avanzar();
                    avanzar();
                    return;
                }
                avanzar();
            }
            registrarError("/*", "COMENTARIO_SIN_CERRAR",
                    "Comentario de bloque sin cerrar.", f, col);
            return;
        }
        registrarError("/", "CARACTER_NO_RECONOCIDO",
                "El símbolo '/' no forma un token válido por sí solo.", f, col);
        avanzar();
    }

    private void procesarCadena() {
        int f = fila, col = columna;
        StringBuilder lexema = new StringBuilder();
        lexema.append(actual());
        avanzar();

        while (!fin() && actual() != '"') {
            if (actual() == '\n') {
                registrarError(lexema.toString(), "CADENA_SIN_CERRAR",
                        "La cadena no fue cerrada antes de terminar la línea.", f, col);
                return;
            }
            lexema.append(actual());
            avanzar();
        }

        if (fin()) {
            registrarError(lexema.toString(), "CADENA_SIN_CERRAR",
                    "La cadena no fue cerrada.", f, col);
            return;
        }

        lexema.append(actual());
        avanzar();
        agregar(lexema.toString(), TipoToken.CADENA, f, col);
    }

    private void procesarDirectiva() {
        int f = fila, col = columna;
        StringBuilder lexema = new StringBuilder();
        lexema.append(actual());
        avanzar();

        if (fin() || !(esLetra(actual()) || actual() == '_')) {
            registrarError(lexema.toString(), "DIRECTIVA_INVALIDA",
                    "Después de '@' se esperaba el nombre de una directiva.", f, col);
            return;
        }

        while (!fin() && (esLetra(actual()) || esDigito(actual()) || actual() == '_' || actual() == '-')) {
            lexema.append(actual());
            avanzar();
        }

        String valor = lexema.toString();
        if (valor.equals("@modelo") || valor.equals("@rol") || valor.equals("@formato")) {
            agregar(valor, TipoToken.DIRECTIVA, f, col);
        } else {
            registrarError(valor, "DIRECTIVA_INVALIDA",
                    "Directiva no reconocida.", f, col);
        }
    }

    private void procesarIdentificadorOPalabra() {
        int f = fila, col = columna;
        StringBuilder lexema = new StringBuilder();

        while (!fin() && (esLetra(actual()) || esDigito(actual()) || actual() == '_')) {
            lexema.append(actual());
            avanzar();
        }

        String valor = lexema.toString();
        TipoToken tipo = PALABRAS.get(valor);
        if (tipo == null) {
            tipo = TipoToken.IDENTIFICADOR;
        }
        agregar(valor, tipo, f, col);
    }

    private void procesarNumero() {
        int f = fila, col = columna;
        StringBuilder lexema = new StringBuilder();

        while (!fin() && esDigito(actual())) {
            lexema.append(actual());
            avanzar();
        }

        if (!fin() && actual() == '.') {
            lexema.append(actual());
            avanzar();

            if (fin() || !esDigito(actual())) {
                registrarError(lexema.toString(), "NUMERO_INVALIDO",
                        "Un número decimal debe tener al menos un dígito después del punto.", f, col);
                return;
            }

            while (!fin() && esDigito(actual())) {
                lexema.append(actual());
                avanzar();
            }
            agregar(lexema.toString(), TipoToken.DECIMAL, f, col);
        } else {
            agregar(lexema.toString(), TipoToken.ENTERO, f, col);
        }
    }

    private void agregar(String lexema, TipoToken tipo, int f, int col) {
        tokens.add(new Token(numeroToken++, lexema, tipo, f, col));
    }

    private void registrarError(String lexema, String tipo, String descripcion, int f, int col) {
        errores.add(new ErrorLexico(lexema, tipo, descripcion, f, col));
    }

    private void avanzar() {
        if (fin()) {
            return;
        }
        char c = entrada.charAt(posicion++);
        if (c == '\n') {
            fila++;
            columna = 1;
        } else {
            columna++;
        }
    }

    private char actual() {
        return entrada.charAt(posicion);
    }

    private char siguiente() {
        return posicion + 1 < entrada.length() ? entrada.charAt(posicion + 1) : '\0';
    }

    private boolean fin() {
        return posicion >= entrada.length();
    }

    private boolean esEspacio(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    private boolean esLetra(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean esDelimitador(char c) {
        return c == '{' || c == '}' || c == '(' || c == ')' || c == ',';
    }

    private int contarLineas(String s) {
        if (s.isEmpty()) {
            return 1;
        }
        int n = 1;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                n++;
            }
        }
        return n;
    }

    public List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }

    public List<ErrorLexico> getErrores() {
        return new ArrayList<>(errores);
    }

    public int getTotalLineas() {
        return totalLineas;
    }
}
