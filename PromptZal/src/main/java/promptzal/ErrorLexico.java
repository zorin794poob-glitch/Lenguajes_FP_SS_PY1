package promptzal;

public class ErrorLexico {
    private final String lexema;
    private final String descripcion;
    private final int fila;
    private final int columna;

    public ErrorLexico(String lexema, String descripcion, int fila, int columna) {
        this.lexema = lexema;
        this.descripcion = descripcion;
        this.fila = fila;
        this.columna = columna;
    }

    public String getLexema() { return lexema; }
    public String getDescripcion() { return descripcion; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
}
