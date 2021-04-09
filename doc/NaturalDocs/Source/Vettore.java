package frame1;

/*
 * Class: Vettore
 * Un vettore e' una struttura dati contenente un array di lunghezza variabile.
 * Quando l'array si riempie, la sua capienza viene raddoppiata.
 */
public class Vettore {
    public String[] array;
    public int n;

    /*
     * Function: Vettore
     * Crea il vettore.
     */
    public Vettore() {
        array = new String[1];
        n = 0;
    }

    /*
     * Function: aumentaGrandezza
     * Raddoppia la lunghezza dell'array.
     */
    public String[] aumentaGrandezza() {
        String[] aux = new String[array.length*2];
        for(int i = 0; i < array.length; i++) {
            aux[i] = array[i];
        }
        return aux;
    }

    /*
     * Function: inserisci
     * Aggiunge la stringa *s* al vettore
     */
    public void inserisci(String s) {
        if(n == array.length)
            array = aumentaGrandezza();
        array[n] = s;
        n++;
    }
}