package Generador;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.security.SecureRandom;
import javax.swing.*;

public class CreadorDeClaves extends JFrame implements ActionListener {

    private static final String CARACTERES_ESPECIALES = "!¡@#$%^&()_/*-+=<>?¿";
    private JTextField servicioField;
    private JTextArea claveGeneradaArea;
    private JButton generarButton;
    private JButton copiarButton;
    private JSpinner longitudSpinner;

    public CreadorDeClaves() {
        super("Generador de Claves");

        // Configuración de la interfaz
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 500);
        setLayout(new GridLayout(5, 2));

        // Etiqueta del servicio
        JLabel servicioLabel = new JLabel("Nombre del servicio:");
        servicioLabel.setFont(new Font("VictorMono", Font.BOLD, 24));
        add(servicioLabel);

        // Campo de texto para el servicio
        servicioField = new JTextField();
        servicioField.setFont(new Font("VictorMono", Font.PLAIN, 24));
        add(servicioField);

        // Etiqueta de la longitud de la clave
        JLabel longitudLabel = new JLabel("Longitud de la clave:");
        longitudLabel.setFont(new Font("VictorMono", Font.BOLD, 24));
        add(longitudLabel);

        // Selector de la longitud de la clave
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(10, 10, 25, 1);
        longitudSpinner = new JSpinner(spinnerModel);
        JComponent spinnerEditor = new JSpinner.NumberEditor(longitudSpinner, "#");
        longitudSpinner.setEditor(spinnerEditor);
        longitudSpinner.setFont(new Font("VictorMono", Font.PLAIN, 24));
        add(longitudSpinner);

        // Etiqueta de la clave generada
        JLabel claveGeneradaLabel = new JLabel("Clave generada:");
        claveGeneradaLabel.setFont(new Font("VictorMono", Font.BOLD, 24));
        add(claveGeneradaLabel);

        // Área de texto para mostrar la clave generada
        claveGeneradaArea = new JTextArea();
        claveGeneradaArea.setFont(new Font("VictorMono", Font.PLAIN, 24));
        claveGeneradaArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(claveGeneradaArea);
        add(scrollPane);

        // Botón para generar la clave
        generarButton = new JButton("Generar Clave");
        generarButton.setFont(new Font("VictorMono", Font.BOLD, 24));
        generarButton.addActionListener(this);
        generarButton.setOpaque(true);
        generarButton.setBorderPainted(true);
        generarButton.setFocusPainted(true);
        generarButton.setContentAreaFilled(true);
        generarButton.setBackground(new Color(192, 216, 255));
        generarButton.setForeground(Color.BLACK);
        add(generarButton);

        // Botón para copiar la clave al portapapeles
        copiarButton = new JButton("Copiar al Portapapeles");
        copiarButton.setFont(new Font("VictorMono", Font.BOLD, 24));
        copiarButton.addActionListener(this);
        copiarButton.setOpaque(true);
        copiarButton.setBorderPainted(true);
        copiarButton.setFocusPainted(true);
        copiarButton.setContentAreaFilled(true);
        copiarButton.setBackground(new Color(255, 205, 215));
        copiarButton.setForeground(Color.BLACK);
        add(copiarButton);

        // Mostrar la interfaz
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generarButton) {
            String servicio = servicioField.getText();
            int longitud = (int) longitudSpinner.getValue();
            if (servicio.isEmpty()) {
                mostrarError("Error al intentar generar clave: seleccione un nombre de servicio");
            } else {
                String claveGenerada = generarClave(servicio, longitud);
                claveGeneradaArea.setText(claveGenerada);
            }
        } else if (e.getSource() == copiarButton) {
            String clave = claveGeneradaArea.getText();
            if (clave.isEmpty()) {
                mostrarError("Error al intentar copiar al portapapeles: no se generó una clave de servicio");
            } else {
                copiarAlPortapapeles(clave);
                mostrarMensajeCopiado();
            }
        }
    }

    private static String generarClave(String servicio, int longitud) {
        if (longitud < 10) {
            longitud = 10;
        } else if (longitud > 25) {
            longitud = 25;
        }

        StringBuilder clave = new StringBuilder();
        SecureRandom random = new SecureRandom();

        int letras = longitud / 3;
        int numeros = longitud / 3;
        int especiales = longitud - letras - numeros;

        // Generar letras
        for (int i = 0; i < letras; i++) {
            char letra = generarLetraAleatoria(random);
            clave.append(letra);
        }

        // Generar números
        for (int i = 0; i < numeros; i++) {
            int numero = generarNumeroAleatorio(random);
            clave.append(numero);
        }

        // Generar caracteres especiales
        for (int i = 0; i < especiales; i++) {
            char caracterEspecial = generarCaracterEspecialAleatorio(random);
            clave.append(caracterEspecial);
        }

        // Mezclar la clave para evitar secuencialidad
        mezclarClave(clave);

        // Agregar caracteres del servicio en la clave
        if (servicio != null && !servicio.isEmpty()) {
            clave = agregarCaracteresServicio(clave, servicio);
        }

        return clave.toString();
    }

    private static char generarLetraAleatoria(SecureRandom random) {
        String letras = "abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
        int indice = random.nextInt(letras.length());
        return letras.charAt(indice);
    }

    private static int generarNumeroAleatorio(SecureRandom random) {
        return random.nextInt(10);
    }

    private static char generarCaracterEspecialAleatorio(SecureRandom random) {
        int indice = random.nextInt(CARACTERES_ESPECIALES.length());
        return CARACTERES_ESPECIALES.charAt(indice);
    }

    private static void mezclarClave(StringBuilder clave) {
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < clave.length(); i++) {
            int indiceAleatorio = random.nextInt(clave.length());
            char temp = clave.charAt(i);
            clave.setCharAt(i, clave.charAt(indiceAleatorio));
            clave.setCharAt(indiceAleatorio, temp);
        }
    }

    private static StringBuilder agregarCaracteresServicio(StringBuilder clave, String servicio) {
        String parteServicio = obtenerParteServicio(servicio);
        int longitudServicio = Math.min(parteServicio.length(), clave.length() / 3);
        int indiceClave = 0;

        for (int i = 0; i < longitudServicio; i++) {
            if (indiceClave >= clave.length()) {
                break;
            }

            clave.setCharAt(indiceClave, parteServicio.charAt(i));
            indiceClave += 3;
        }

        return clave;
    }

    private static String obtenerParteServicio(String servicio) {
        String[] palabras = servicio.split("\\s+");
        StringBuilder parteServicio = new StringBuilder();
        for (String palabra : palabras) {
            parteServicio.append(Character.toUpperCase(palabra.charAt(0)));
        }
        return parteServicio.toString();
    }

    private static void copiarAlPortapapeles(String texto) {
        StringSelection seleccion = new StringSelection(texto);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(seleccion, null);
    }

    private static void mostrarMensajeCopiado() {
        JOptionPane.showMessageDialog(null, "Clave copiada al portapapeles", "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new CreadorDeClaves();
    }
}