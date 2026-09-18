/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package promptzal.modelo;

/**
 *
 * @author jonathan-zorin
 */
public class ErrorLexico {

    private final String lexema;
    private final String tipo;
    private final String descripcion;
    private final int fila;
    private final int columna;

    public ErrorLexico(String lexema, String tipo, String descripcion, int fila, int columna) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fila = fila;
        this.columna = columna;
    }

    public String getLexema() {
        return lexema;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
