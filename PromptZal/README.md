# PromptZal - Práctica 1

Analizador léxico manual desarrollado en Java para la Práctica 1 de Lenguajes Formales y de Programación.

## Características

- Lee archivos `.pz`.
- Recorre la entrada carácter por carácter.
- Reconoce directivas, palabras reservadas, comandos de IA, conectores, identificadores, cadenas, números, operadores y delimitadores.
- Ignora comentarios `//` y `/* ... */`.
- Reporta errores sin detener el análisis.
- Muestra tokens en consola.
- Genera `reporte_tokens.html`.
- Genera `reporte_errores.html`.
- No utiliza JFlex, JLex, Lex ni expresiones regulares.

## Abrir en NetBeans

1. Abrir NetBeans.
2. File > Open Project.
3. Seleccionar la carpeta `PromptZal`.
4. Ejecutar `Main.java`.
5. Escribir la ruta de un archivo `.pz`.

## Importante

La práctica incluye una comprobación de conocimiento. Revisa y comprende especialmente `Lexer.java`, porque allí se realiza el recorrido carácter por carácter y el reconocimiento de tokens.


## Graphviz y AFD

El botón **Generar AFD** crea:
- `reportes/afd_promptzal.dot`: definición del autómata.
- `reportes/afd_promptzal.png`: imagen del AFD.
- `reportes/afd_promptzal.svg`: versión vectorial.

El programa busca `dot` en el `PATH` y en rutas comunes de Ubuntu. Si Graphviz no está instalado, el programa muestra instrucciones claras en lugar de cerrarse.

En Ubuntu puedes ejecutar:

```bash
sudo apt update
sudo apt install graphviz
dot -V
```

También se incluye `INSTALAR_GRAPHVIZ_UBUNTU.sh`.

## Interfaz

La interfaz conserva las funciones originales:
- Abrir `.pz`
- Guardar / Guardar como
- Cargar ejemplo
- Limpiar
- Copiar
- Analizar
- Ver tokens y errores
- Reportes HTML
- Generación del AFD

La mejora visual se realizó solamente con Swing/AWT, sin agregar librerías externas.
