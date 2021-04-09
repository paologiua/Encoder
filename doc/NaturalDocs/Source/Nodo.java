package frame1;

/*
 * Class: Nodo
 * Componente fondamentale dell'albero binario di ricerca.
 */
public class Nodo {
    public String val;
    public Nodo sin;
    public Nodo des;
    public Nodo padre;
    public long index;

    /*
     * Function: Nodo
     * Crea un nodo.
     */
    public Nodo(String s, long i) {
        val = s;
        index = i;
        sin = null;
        des = null;
        padre = null;
    }
}