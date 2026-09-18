/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package promptzal.reportes;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import promptzal.modelo.ErrorLexico;
import promptzal.modelo.TipoToken;
import promptzal.modelo.Token;

/**
 *
 * @author jonathan-zorin
 */
public class Estadisticas {

    private final Map<TipoToken, Integer> frecuencia = new EnumMap<>(TipoToken.class);
    private int totalTokens;
    private int totalLineas;
    private int totalErrores;

    public Estadisticas(List<Token> tokens, List<ErrorLexico> errores, int totalLineas) {
        calcular(tokens, errores, totalLineas);
    }

    public void calcular(List<Token> tokens, List<ErrorLexico> errores, int lineas) {
        frecuencia.clear();
        totalTokens = 0;
        for (Token t : tokens) {
            if (t.getTipo() == TipoToken.EOF) {
                continue;
            }
            totalTokens++;
            frecuencia.put(t.getTipo(), frecuencia.getOrDefault(t.getTipo(), 0) + 1);
        }
        totalLineas = lineas;
        totalErrores = errores.size();
    }

    public Map<TipoToken, Integer> getFrecuencia() {
        return frecuencia;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public int getTotalLineas() {
        return totalLineas;
    }

    public int getTotalErrores() {
        return totalErrores;
    }
}
