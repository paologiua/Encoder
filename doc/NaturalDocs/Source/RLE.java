package frame1;

import java.io.*;
/*
 * Class: RLE
 * Classe che contiene le funzioni di codifica e decodifica dell'algoritmo RunLength (ottimizzato con l'uso di una bandiera).
 */
public class RLE {
    private static InputStream sorgente;
    private static OutputStream destinazione;
    private static byte[] buffer;
    private static long dimSorgente;
    private static byte flag;

    private final static byte BYTE_MIN = -128;
    private final static int UNSIGNED_BYTE_MAX = 255;

    /*
	 * Function: scriviTerna
     * Prende in input un primo byte *b* e un secondo byte *cont* e scrive la terna (*flag*, *b*, *cont*) nel file di destinazione.
     */
    private static void scriviTerna(byte b, byte cont) {
        try {
            byte[] x = new byte[3];
            x[0] = flag;
            x[1] = b;
            x[2] = cont;
            destinazione.write(x, 0, 3);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

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
     * Function: flagConveniente
     * Restituisce il byte dell'array *buffer* con meno ripetizioni. Se la dimensione del file e' maggiore o uguale a 10000, 
     * i byte verranno presi a campione.
     */
    private static byte flagConveniente(String source) throws IOException {
        sorgente = new FileInputStream(source);
        long[] x = new long[UNSIGNED_BYTE_MAX + 1];
        for (long i = 0; i < dimSorgente; i += (dimSorgente < 10000 ? 1 : (dimSorgente/10000))) {
            sorgente.read(buffer);
            x[buffer[0] - BYTE_MIN]++;
        }
        sorgente.close();
        long min = dimSorgente + 1, index = -1;
        for (int i = 0; i <= UNSIGNED_BYTE_MAX; i++) {
            if (min > x[i]) {
                min = x[i];
                index = i;
            }
        }
        byte[] b = { (byte) (index + BYTE_MIN) };
        try {
            destinazione.write(b, 0, 1);
        } catch (IOException e) {
            System.out.println(e);
        }
        return b[0];
    }

    /*
     * Function: code
     * Funzione di codifica del file dell'algoritmo RunLength (ottimizzato con l'uso di una bandiera).
     * 
     * See also:
     * 	<decode>
     */
    public static void code(String source, String destName) throws IOException {
        
        destinazione = new FileOutputStream(destName);
        Interfaccia.comprimiFormato(source, destinazione);
        
        dimSorgente = (new File(source)).length();
        buffer = new byte[1];
        flag = flagConveniente(source);
        
        sorgente = new FileInputStream(source);

        sorgente.read(buffer);


        try {
            long i = 0;
            while (i < dimSorgente) {
                if (buffer[0] == flag) {
                    long k = i;
                    byte aux = buffer[0];
                    while (i < dimSorgente && buffer[0] == aux && i - k <= UNSIGNED_BYTE_MAX) {
                        sorgente.read(buffer);
                        i++;
                    }
                    scriviTerna(flag, (byte) (i - k - 1 + (int) BYTE_MIN));
                } else {
                    long k = i;
                    byte aux = buffer[0];
                    while (i < dimSorgente && buffer[0] == aux && i - k <= UNSIGNED_BYTE_MAX + 3) {
                        sorgente.read(buffer);
                        i++;
                    }
                    if (i - k <= 3) {
                        for (long f = 0; f < i - k; f++)
                            scrivi(aux);
                    } else {
                        scriviTerna(aux, (byte) (i - k - 4 + (int) BYTE_MIN));
                    }
                }
                Interfaccia.percentualeBarra((int) ((i * (long) 100) / dimSorgente));
            }

        } finally {
            sorgente.close();
            destinazione.close();
        }
    }

    /*
     * Function: decode
     * Funzione di decodifica del file dell'algoritmo RunLength (ottimizzato con l'uso di una bandiera).
     * 
     * See also:
     * 	<code>
     */
    public static void decode(String source, String destName) throws IOException {
        sorgente = new FileInputStream(source);
        Interfaccia.decomprimiFormato(source, sorgente);
        destinazione = new FileOutputStream(destName + Interfaccia.formato);

        dimSorgente = (new File(source)).length();

        buffer = new byte[1];
        sorgente.read(buffer);

        flag = buffer[0];
        sorgente.read(buffer);
        try {
            for (long i = Interfaccia.lunghezzaTipo + 3; i < dimSorgente; i++) {
                if (buffer[0] != flag) {
                    scrivi(buffer[0]);
                }
                else {
                    sorgente.read(buffer);
                    byte b = buffer[0];
                    sorgente.read(buffer);
                    byte cont = buffer[0];
                    int k = (b != flag ? 4 : 1); 
                    for (int j = 0; j < cont - BYTE_MIN + k; j++)
                        scrivi(b);
                    i += 2;
                }
                sorgente.read(buffer);
                Interfaccia.percentualeBarra((int) ((i * (long) 100) / dimSorgente));
            }
        } finally {
            sorgente.close();
            destinazione.close();
        }
    }
}