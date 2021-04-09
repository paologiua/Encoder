package frame1;
/*
 * Class: BST
 * Classe per la creazione di alberi di ricerca binaria (Binary Search Tree),
 * in parte modificato per essere usato come dizionario nell'algoritmo di codifica LZW.
 */
public class BST {
    private Nodo radice;
    public long numNodi;
    
    /*
     * Function: BST
     * Crea l'albero e inserisce nell'albero i primi 256 caratteri convertiti nel tipo stringa.
     */
    public BST() {
        radice = null;
        numNodi = 0;
        for(int i = 0; i < 256; i++)
            inserisci("" + (char) i);
    }
    
    /*
     * Function: inserisci
     * Inserisce nell'albero la stringa *val*.
     */
    public BST inserisci(String val) {
        Nodo nuovo = new Nodo(val, numNodi);
        numNodi++;
        Nodo x = radice;
        Nodo y = null;
        nuovo.sin = nuovo.des = null;

        while(x != null) {
            y = x;
            if(val.compareTo(x.val) < 0) x = x.sin;
            else x = x.des;
        }

        nuovo.padre = y;
        if(y == null) radice = nuovo;
        else if(y.val.compareTo(val) > 0) y.sin = nuovo;
        else y.des = nuovo;
        return this;
    }

    /*
     * Function: ricerca
     * Ricerca nell'albero la stringa *x*.
     */
    public Nodo ricerca(String x) {
        Nodo iter = radice;
        while(iter != null && !(x.equals(iter.val))) {
            if(iter.val.compareTo(x) > 0) iter = iter.sin;
            else iter = iter.des;
        }

        return iter;
    }
}