package frame1;

import javax.swing.JFileChooser;
/*
 * Class: Navigatore
 * Questa classe permette di navigare i file del computer.
 */
public class Navigatore {
	/*
	 * Function: naviga
	 * Manda a display un esplora file e restituisce, sottoforma di stringa, il percorso
	 * del file selezionato.
	 */
    public static String naviga(String titolo) {
        JFileChooser esploraFile = new JFileChooser();
        esploraFile.setCurrentDirectory(new java.io.File(""));
        esploraFile.setDialogTitle(titolo);

        if (esploraFile.showOpenDialog(esploraFile) == JFileChooser.APPROVE_OPTION) {
            System.out.println("File selezionato : " +  esploraFile.getSelectedFile());
            return "" + esploraFile.getSelectedFile();
        }
        else {
            System.out.println("Nessuna selezione");
            return " ";
        }
    }
}