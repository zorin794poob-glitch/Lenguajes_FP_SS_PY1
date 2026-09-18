package promptzal;

import javax.swing.SwingUtilities;
import promptzal.iu.Interfaz_Grafica;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interfaz_Grafica().setVisible(true));
    }
}
