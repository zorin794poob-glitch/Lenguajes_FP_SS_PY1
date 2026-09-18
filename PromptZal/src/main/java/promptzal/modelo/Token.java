/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package promptzal.modelo;

/**
 *
 * @author jonathan-zorin
 */
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

    public int getNumero() {
        return numero;
    }

    public String getLexema() {
        return lexema;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
