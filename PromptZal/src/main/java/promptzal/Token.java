package promptzal;

public class Token {
    private final int numero;
    private final String lexema;
    private final TipoToken tipo;
    private final int fila;
    private final int columna;

    public Token(int numero, String lexema, TipoToken tipo, int fila, int columna) {
        this.numero = numero;
        this.lexema = lexema;
        this.tipo = tipo;
        this.fila = fila;
        this.columna = columna;
    }

    public int getNumero() { return numero; }
    public String getLexema() { return lexema; }
    public TipoToken getTipo() { return tipo; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }

    @Override
    public String toString() {
        return numero + " | " + lexema + " | " + tipo + " | fila: "
                + fila + " | columna: " + columna;
    }
}
