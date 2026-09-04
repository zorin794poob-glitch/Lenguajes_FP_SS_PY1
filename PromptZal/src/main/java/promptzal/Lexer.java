package promptzal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String entrada;
    private final List<Token> tokens = new ArrayList<>();
    private final List<ErrorLexico> errores = new ArrayList<>();
    private final Map<String, TipoToken> palabras = new HashMap<>();

    private int posicion = 0;
    private int fila = 1;
    private int columna = 1;
    private int numeroToken = 1;

    public Lexer(String entrada) {
        this.entrada = entrada;
        cargarPalabras();
    }

    private void cargarPalabras() {
        palabras.put("AGENTE", TipoToken.PALABRA_RESERVADA);
        palabras.put("contexto", TipoToken.PALABRA_RESERVADA);
        palabras.put("variable", TipoToken.PALABRA_RESERVADA);
        palabras.put("EJECUTAR", TipoToken.PALABRA_RESERVADA);
        palabras.put("EXPORTAR", TipoToken.PALABRA_RESERVADA);

        palabras.put("PREGUNTAR", TipoToken.COMANDO_IA);
        palabras.put("GENERAR", TipoToken.COMANDO_IA);
        palabras.put("RESUMIR", TipoToken.COMANDO_IA);
        palabras.put("ANALIZAR", TipoToken.COMANDO_IA);
        palabras.put("TRADUCIR", TipoToken.COMANDO_IA);
        palabras.put("CLASIFICAR", TipoToken.COMANDO_IA);
        palabras.put("EXTRAER", TipoToken.COMANDO_IA);

        palabras.put("SOBRE", TipoToken.CONECTOR);
        palabras.put("DESDE", TipoToken.CONECTOR);
        palabras.put("EN", TipoToken.CONECTOR);
        palabras.put("COMO", TipoToken.CONECTOR);

        palabras.put("CARGAR", TipoToken.FUNCION);
    }

    public void analizar() {
        while (!fin()) {
            char actual = actual();

            if (esEspacio(actual)) {
                avanzar();
            } else if (actual == '/' && siguienteEs('/')) {
                comentarioLinea();
            } else if (actual == '/' && siguienteEs('*')) {
                comentarioBloque();
            } else if (actual == '"') {
                reconocerCadena();
            } else if (actual == '@') {
                reconocerDirectiva();
            } else if (esLetra(actual) || actual == '_') {
                reconocerIdentificadorOPalabra();
            } else if (esDigito(actual)) {
                reconocerNumero();
            } else if (actual == '-' && siguienteEs('>')) {
                agregarToken("->", TipoToken.FLECHA, fila, columna);
                avanzar();
                avanzar();
            } else if (actual == '=') {
                agregarToken("=", TipoToken.OPERADOR_ASIGNACION, fila, columna);
                avanzar();
            } else if (actual == '+') {
                agregarToken("+", TipoToken.OPERADOR_CONCATENACION, fila, columna);
                avanzar();
            } else if (esDelimitador(actual)) {
                int f = fila;
                int c = columna;
                agregarToken(String.valueOf(actual), TipoToken.DELIMITADOR, f, c);
                avanzar();
            } else {
                registrarError(String.valueOf(actual),
                        "Carácter no reconocido", fila, columna);
                avanzar();
            }
        }
    }

    private void reconocerDirectiva() {
        int f = fila;
        int c = columna;
        avanzar();

        if (fin() || !(esLetra(actual()) || actual() == '_')) {
            registrarError("@", "Directiva inválida", f, c);
            return;
        }

        StringBuilder lexema = new StringBuilder("@");
        while (!fin() && (esLetra(actual()) || esDigito(actual()) || actual() == '_')) {
            lexema.append(actual());
            avanzar();
        }

        String valor = lexema.toString();
        if (valor.equals("@modelo") || valor.equals("@rol") || valor.equals("@formato")) {
            agregarToken(valor, TipoToken.DIRECTIVA, f, c);
        } else {
            registrarError(valor, "Directiva no reconocida", f, c);
        }
    }

    private void reconocerIdentificadorOPalabra() {
        int f = fila;
        int c = columna;
        StringBuilder lexema = new StringBuilder();

        while (!fin() && (esLetra(actual()) || esDigito(actual()) || actual() == '_')) {
            lexema.append(actual());
            avanzar();
        }

        String valor = lexema.toString();
        TipoToken tipo = palabras.get(valor);

        if (tipo == null) {
            tipo = TipoToken.IDENTIFICADOR;
        }

        agregarToken(valor, tipo, f, c);
    }

    private void reconocerCadena() {
        int f = fila;
        int c = columna;
        StringBuilder lexema = new StringBuilder();

        lexema.append(actual());
        avanzar();

        boolean cerrada = false;

        while (!fin()) {
            char actual = actual();

            if (actual == '"') {
                lexema.append(actual);
                avanzar();
                cerrada = true;
                break;
            }

            // Una cadena no puede continuar físicamente en otra línea.
            if (actual == '\n') {
                break;
            }

            lexema.append(actual);
            avanzar();
        }

        if (cerrada) {
            agregarToken(lexema.toString(), TipoToken.CADENA, f, c);
        } else {
            registrarError(lexema.toString(),
                    "Cadena sin cerrar", f, c);
        }
    }

    private void reconocerNumero() {
        int f = fila;
        int c = columna;
        StringBuilder lexema = new StringBuilder();
        boolean tienePunto = false;

        while (!fin() && esDigito(actual())) {
            lexema.append(actual());
            avanzar();
        }

        if (!fin() && actual() == '.') {
            tienePunto = true;
            lexema.append(actual());
            avanzar();

            if (fin() || !esDigito(actual())) {
                registrarError(lexema.toString(),
                        "Número decimal inválido", f, c);
                return;
            }

            while (!fin() && esDigito(actual())) {
                lexema.append(actual());
                avanzar();
            }

            // Detectar una segunda parte decimal, por ejemplo 12.3.4
            if (!fin() && actual() == '.') {
                while (!fin() && (esDigito(actual()) || actual() == '.')) {
                    lexema.append(actual());
                    avanzar();
                }
                registrarError(lexema.toString(),
                        "Número con formato inválido", f, c);
                return;
            }
        }

        agregarToken(lexema.toString(),
                tienePunto ? TipoToken.DECIMAL : TipoToken.ENTERO, f, c);
    }

    private void comentarioLinea() {
        avanzar();
        avanzar();

        while (!fin() && actual() != '\n') {
            avanzar();
        }
    }

    private void comentarioBloque() {
        int f = fila;
        int c = columna;

        avanzar();
        avanzar();

        while (!fin()) {
            if (actual() == '*' && siguienteEs('/')) {
                avanzar();
                avanzar();
                return;
            }
            avanzar();
        }

        registrarError("/*", "Comentario de bloque sin cerrar", f, c);
    }

    private void agregarToken(String lexema, TipoToken tipo, int f, int c) {
        tokens.add(new Token(numeroToken++, lexema, tipo, f, c));
    }

    private void registrarError(String lexema, String descripcion, int f, int c) {
        errores.add(new ErrorLexico(lexema, descripcion, f, c));
    }

    private boolean fin() {
        return posicion >= entrada.length();
    }

    private char actual() {
        return entrada.charAt(posicion);
    }

    private boolean siguienteEs(char esperado) {
        return posicion + 1 < entrada.length()
                && entrada.charAt(posicion + 1) == esperado;
    }

    private void avanzar() {
        if (fin()) return;

        if (entrada.charAt(posicion) == '\n') {
            fila++;
            columna = 1;
        } else {
            columna++;
        }
        posicion++;
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

    public List<Token> getTokens() {
        return tokens;
    }

    public List<ErrorLexico> getErrores() {
        return errores;
    }
}
