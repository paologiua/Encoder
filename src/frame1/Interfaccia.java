package frame1;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/*
 * Class: Interfaccia
 * E' la classe principale. Imposta la grafica del programma e contiene
 * altre funzioni per la gestione del formato del file e la gestione 
 * dei metodi di conversione.
 */
public class Interfaccia {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private ButtonGroup group;
	private JRadioButton rdbtnRunlengthEncoding;
	private JRadioButton rdbtnLzEncoding;
	private JRadioButton rdbtnDecoding;
	public static JProgressBar progressBar;
	public static String formato;
    public static int lunghezzaTipo;
    
    /*
     * Function: percentualeBarra
     * Imposta ad *i* il valore della barra di caricamento.
     */
    public static void percentualeBarra(int i) {
    	if(i != progressBar.getValue())
    		progressBar.setValue(i);
    }
    
    /*
     * Function: rapportoCompressione
     * Calcola il rapporto di compressione.
     */
    public static String rapportoCompressione(String source, String destName) throws IOException {
    	double rapporto = (double)(new File(source)).length() / (double)(new File(destName)).length();
    	return String.format("%.2f", rapporto);
    }
    
    /*
     * Function: calcolaLunghezzaTipo
     * Salva il valore della lunghezza del formato del file nella variabile *lunghezzaTipo* di tipo int.
     * Se il file non ha un formato, *lunghezzaTipo* viene impostata a -1.
     */
    public static void calcolaLunghezzaTipo(String tipoFile) {
    	lunghezzaTipo = 0;
        int i = tipoFile.length() - 1;
        while(i >= 0 && tipoFile.charAt(i--) != '.')
        	lunghezzaTipo++;
        if(lunghezzaTipo == tipoFile.length()) 
        	lunghezzaTipo = -1;
    }
    
    /*
     * Function: comprimiFormato
     * Codifica il formato del file e lo scrive nel file di destinazione.
	 * *Usa questo metodo solo dopo aver chiamato la funzione <calcolaLunghezzaTipo>.*
	 * 
	 * See Also:
	 * 	<decomprimiFormato>
	 * 	<calcolaLunghezzaTipo>
     */
	public static void comprimiFormato(String tipoFile, OutputStream destinazione) throws IOException {
		byte[] binTipo;
        if(lunghezzaTipo == -1) {
        	binTipo = new byte[1];
        	binTipo[0] = -1;
        }
        else {
        	binTipo = new byte[lunghezzaTipo + 2]; 
        	int i = tipoFile.length() - lunghezzaTipo - 1;
        	int j = 0;
        	while(i < tipoFile.length())
        		binTipo[j++] = (byte) tipoFile.charAt(i++);
        	binTipo[j] = (byte) -1;
        }
        destinazione.write(binTipo, 0, binTipo.length);
    }
	
	/*
     * Function: decomprimiFormato
     * Decodifica il formato del file e lo salva nella variabile di tipo String *formato*.
     * 
     * See Also:
	 * 	<comprimiFormato>
     */
    public static void decomprimiFormato(String source, InputStream sorgente){
    	try {
    		byte[] aux = new byte[1];
    		formato = "";
			sorgente.read(aux);
			
    		while(aux[0] != -1) {
    			formato += (char) aux[0];
    			sorgente.read(aux);
    		}
    	} catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /*
     * Function: avviso
     * Questa funzione manda a display un avviso.
     */
    public static void avviso(String titolo, String descrizione) {
    	JOptionPane.showConfirmDialog(
       			null,
        		descrizione,
        		titolo,
        		JOptionPane.CLOSED_OPTION);
    }
    
    /*
     * Function: opzioneCodifica 
	 * Chiama il metodo di codifica scelto dall'utente.
	 * Se il file non puo' essere codificato rimanda un avviso, tramite la funzione <avviso>.
	 * 
	 * See Also:
	 * 	<opzioneDecodifica>
	 * 	<avviso>
	 */
    public void opzioneCodifica(int scelta) {
        try {
        	progressBar.setValue(0);
            String file = textField.getText();
            
            if(!(new File(file).exists())) {
            	avviso("Errore", "File non trovato");
            	return;
            }
            
            calcolaLunghezzaTipo(file);
            String destinazione = file.substring(0, file.length() - lunghezzaTipo - 1);
            if(scelta == 1) {
            	destinazione += (file.substring(file.length() - lunghezzaTipo-1).equals(".lzw") ? "(2).lzw" : ".lzw");
            	LZW.code(file, destinazione);
            }
            else {
            	destinazione += (file.substring(file.length() - lunghezzaTipo-1).equals(".rlc") ? "(2).rlc" : ".rlc");
            	RLE.code(file, destinazione);
            }
            progressBar.setValue(100);
            
            textField_1.setText(destinazione);
            
            avviso("Successo", "Operazione completata\nRapporto di compressione: " + rapportoCompressione(file, destinazione));
            progressBar.setValue(100);
        } catch (IOException e) {
        	System.out.println(e);
        }
    }

    /* 
     * Function: opzioneDecodifica
	 * In base al formato del file, chiama la funzione di decodifica adatta.
	 * Se il file non puo' essere decodificato rimanda un avviso, tramite la funzione <avviso>.
	 * 
	 * See Also:
	 * 	<opzioneCodifica>
	 * 	<avviso>
	 */
    public void opzioneDecodifica() {
        try {
        	progressBar.setValue(0);
            String file = textField.getText();
            
            if(!(new File(file).exists())) {
            	avviso("Errore", "File non trovato");
            	return;
            }
            
            String destinazione = file.substring(0, file.length() - 4) + "(decoded)";

            if(file.substring(file.length()-4).equals(".lzw"))
            	LZW.decode(file, destinazione);
            else if(file.substring(file.length()-4).equals(".rlc"))
            	RLE.decode(file, destinazione);
            else {
            	textField_1.setText("");
            	avviso("Errore", "File non adatto alla decompressione");
            	return;
            }
            
            textField_1.setText(destinazione + formato);
            progressBar.setValue(100);
            
            avviso("Successo", "Operazione completata");
        } catch (IOException e) {
            System.out.println(e);
        }
    }


	/* 
	 * Function: main
	 * Lancia il programma.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interfaccia window = new Interfaccia();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * Function: Interfaccia
	 * Crea il programma.
	 */
	public Interfaccia() {
		initialize();
	}

	/* 
	 * Function: initialize
	 * Imposta la grafica del programma.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 512, 369);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);
		
		textField = new JTextField();
		textField.setBounds(34, 73, 366, 38);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBackground(Color.WHITE);
		textField_1.setColumns(10);
		textField_1.setBounds(34, 133, 428, 38);
		frame.getContentPane().add(textField_1);
		textField_1.setEditable(false);
		
		JLabel lblCompressore = new JLabel("Encoder");
		lblCompressore.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblCompressore.setHorizontalAlignment(SwingConstants.CENTER);
		lblCompressore.setBounds(34, 24, 428, 38);
		frame.getContentPane().add(lblCompressore);
		
		JButton btnNewButton = new JButton("coding");
		
		btnNewButton.setBounds(188, 213, 116, 38);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblFileCompresso = new JLabel("File compresso");
		lblFileCompresso.setBounds(34, 118, 205, 14);
		frame.getContentPane().add(lblFileCompresso);
		
		rdbtnRunlengthEncoding = new JRadioButton("RunLength encoding");
		rdbtnRunlengthEncoding.setSelected(true);
		rdbtnRunlengthEncoding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				btnNewButton.setText("coding");
				lblFileCompresso.setText("File compresso");
				progressBar.setValue(0);
			}
		});
		rdbtnRunlengthEncoding.setBounds(34, 280, 171, 23);
		frame.getContentPane().add(rdbtnRunlengthEncoding);
		
		rdbtnLzEncoding = new JRadioButton("LZW encoding");
		rdbtnLzEncoding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				btnNewButton.setText("coding");
				lblFileCompresso.setText("File compresso");
				progressBar.setValue(0);
			}
		});
		rdbtnLzEncoding.setBounds(204, 280, 147, 23);
		frame.getContentPane().add(rdbtnLzEncoding);
		
		rdbtnDecoding = new JRadioButton("decoding");
		rdbtnDecoding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				btnNewButton.setText("decoding");
				lblFileCompresso.setText("File decompresso");
				progressBar.setValue(0);
			}
		});
		rdbtnDecoding.setBounds(353, 280, 110, 23);
		frame.getContentPane().add(rdbtnDecoding);

		group = new ButtonGroup();
		group.add(rdbtnRunlengthEncoding);
		group.add(rdbtnLzEncoding);
		group.add(rdbtnDecoding);
		
		JLabel lblFileOriginale = new JLabel("File originale");
		lblFileOriginale.setBounds(34, 58, 205, 14);
		frame.getContentPane().add(lblFileOriginale);
		
		JButton btnNewButton_1 = new JButton("Apri");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText(Navigatore.naviga("Seleziona file"));
				textField_1.setText("");
			}
		});
		btnNewButton_1.setBounds(405, 73, 57, 38);
		frame.getContentPane().add(btnNewButton_1);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(44, 182, 408, 14);
		frame.getContentPane().add(progressBar);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>() {
				    @Override
				    public Void doInBackground() {
				    	textField.setEditable(false);
						textField.setBackground(Color.WHITE);
						btnNewButton_1.setEnabled(false);
						btnNewButton.setEnabled(false);
						rdbtnRunlengthEncoding.setEnabled(false);
						rdbtnLzEncoding.setEnabled(false);
						rdbtnDecoding.setEnabled(false);
						textField_1.setText("");
						
				    	if(rdbtnRunlengthEncoding.isSelected())
				    		opzioneCodifica(2);
						else if(rdbtnLzEncoding.isSelected())
							opzioneCodifica(1);
						else if(rdbtnDecoding.isSelected())
							opzioneDecodifica();
				    	
				    	rdbtnRunlengthEncoding.setEnabled(true);
						rdbtnLzEncoding.setEnabled(true);
						rdbtnDecoding.setEnabled(true);
						btnNewButton.setEnabled(true);
						textField.setEditable(true);
						btnNewButton_1.setEnabled(true);
						
						return null;
				    }
				}.execute();
			}
		});
		
	}
}
