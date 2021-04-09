package frame1;

import java.io.*;
/*
 * Class: LZW
 * Classe che contiene le funzioni di codifica e decodifica dell'algoritmo Lempel-Ziv-Welch.
 */
public class LZW {
	private final static byte BYTE_MIN = -128;
	private final static int CHAR_MAX = 255;
    private final static int MAX_DIM_DIZIONARIO = 130000;
	    
    private static InputStream sorgente;
    private static OutputStream destinazione;
    private static byte[] buffer;

    private static int segnaPunto;
    private static byte segnaByte;

    private static long len;

    private static int contaBit;

    private static int out;
    private static double contaLettura;

    /* 
     * Function: scrivi
     * Funzione che prende in input un byte e lo scrive nel file di destinazione.
     */
    private static void scrivi(byte b) {
        try {
            byte[] x = new byte[1];
            x[0] = b;
            destinazione.write(x, 0, 1);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /* 
     * Function: adattaEScrivi
     * Questa funzione prende in input un primo long *a* e un secondo long *length*
     * e scrive una sequenza di bit, di lunghezza *length* e conservata in *a*, nel file
     * di destinazione a gruppi di 8 bit alla volta. Se l'ultimo gruppo ha meno di 8 bit viene
     * salvato e caricato nel file alla succesiva chiamata di questa funzione.
     * 
     * See also:
     * 	<scrivi>
     */
    private static void adattaEScrivi(long a, long length) {
        for(int i = 0; i < length; i++) {
            segnaByte = (byte) ((segnaByte << 1) | (iBitInverse(a, i, length)));
            segnaPunto = (segnaPunto + 1) % 8;
            if(segnaPunto == 0) {
                scrivi(segnaByte);
                segnaByte = 0;
            }
        }
    }
    
    /*
     * Function: iBitInverse
     * Questa funzione prende in input un long *x*, un int *i* e un long *length* e restituisce l' *i* -esimo bit (contando da sinistra verso destra)
     * della sequenza di bit di lunghezza *length* e conservata in *x*.
     */
    private static byte iBitInverse(long x, int i, long length) {
        return (byte)((x >> (length - 1 - i)) & 1);
    }

    /*
     * Function: preparazione
     * Prepara il programma all'esecuzione della codifica o della decodifica del file.
     */
    private static void preparazione() throws IOException {
        segnaPunto = 0;
        segnaByte = 0;

        contaBit = 0;

        buffer = new byte[1];

        contaLettura = 0;
        out = sorgente.available();
    }

    /*
     * Function: code
     * Funzione di codifica del file che applica l'algoritmo LZW.
     * 
     * See also:
     * 	<decode>
     */
    public static void code(String source, String destName) throws IOException{
        sorgente = new FileInputStream(source);
        destinazione = new FileOutputStream(destName);
        Interfaccia.comprimiFormato(source, destinazione);
        preparazione();
        
        long dimensione = (new File(source)).length();
        
        while(out > 0) {
            len = 9;

            BST dizionario = new BST();

            String p =  "";
            while(dizionario.numNodi < MAX_DIM_DIZIONARIO && out > 0) {
                sorgente.read(buffer);
                out = sorgente.available();
                String c = "" + (char) (buffer[0] - BYTE_MIN);
                if(dizionario.ricerca(p + c) != null) {
                    p += c;                                                                                                   
                }
                else {
                    adattaEScrivi(dizionario.ricerca(p).index, len); 
                    dizionario.inserisci(p + c);
                    p = c;
                }
                if(dizionario.numNodi == Math.pow(2, len))
                    len++;
                Interfaccia.percentualeBarra((int)((contaLettura++)*(100.0/dimensione))); 
            }
            adattaEScrivi(dizionario.ricerca(p).index, len);
        }
        
        while(segnaPunto != 0) {
            adattaEScrivi(0, 1);
        }

        sorgente.close();
        destinazione.close();
    }
    
    /* 
     * Function: adattaELeggi
     * Questa funzione prende in input un long, che determina il numero di bit da leggere (partendo dal primo bit non letto) dell'array di byte *buffer*,
     * e restituisce il valore decimale di tale sequenza di bit.
     * La posizione del primo bit non ancora letto viene salvata nelle variabili contaBit e cont.
     */
    private static int adattaELeggi(long length) throws IOException {
        int traduzione = 0;
        for(int i = 0; out > 0 && i < length; i++) {
            traduzione = (traduzione << 1) | iBitInverse(buffer[0], contaBit, 8);
            contaBit = (contaBit + 1) % 8;
            if(contaBit == 0) {
                out = sorgente.available(); 
                sorgente.read(buffer);
                contaLettura++;
            }
        }
        return traduzione;
    }

    /*
     * Function: output
     * Questa funzione prende in input una stringa *s*, coverte i suoi caratteri in byte
     * e li carica uno alla volta nel file di destinazione.
     * 
     * See also:
     * 	<scrivi>
     */
    private static void output(String s) {
        for(int i = 0;  i < s.length(); i++) {
            scrivi((byte) ((int)s.charAt(i) + BYTE_MIN));
        }
    }

    /* 
     * Function: decode
     * Funzione di decodifica del file che applica l'algoritmo LZW.
     * 
     * See also:
     * 	<code>
     */
    public static void decode(String source, String destName) throws IOException {
        sorgente = new FileInputStream(source);
        Interfaccia.decomprimiFormato(source, sorgente);
        destinazione = new FileOutputStream(destName + Interfaccia.formato);
        preparazione();

        sorgente.read(buffer);
        long dimensione = (new File(source)).length();
        
        while(out > 1) { 
            len = 9;
            Vettore dizionario = new Vettore();
            for(int i = 0; i <= CHAR_MAX; i++)
                dizionario.inserisci("" + (char) i);

            int vecchio = adattaELeggi(len);
            output(dizionario.array[vecchio]);
            String s = "", c = dizionario.array[vecchio];
            while(dizionario.n < MAX_DIM_DIZIONARIO && out > 1) {
                int nuovo = adattaELeggi(len);
                s = (nuovo >= dizionario.n ? dizionario.array[vecchio] + c : dizionario.array[nuovo]);
                
                output(s);
                c = "" + s.charAt(0);
                dizionario.inserisci(dizionario.array[vecchio] + c);
                vecchio = nuovo;
            
                if(dizionario.n+1 == Math.pow(2, len))
                    len++;
                Interfaccia.percentualeBarra((int)(contaLettura*(100.0/dimensione)));
            }
        }
        sorgente.close();
        destinazione.close();
    }
}