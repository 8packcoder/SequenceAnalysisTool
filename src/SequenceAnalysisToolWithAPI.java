import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SequenceAnalysisToolWithAPI {

    public static void main(String[] args) {
        // Creating GUI
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sequence Analysis Tool");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(7, 1));

            JButton dnaButton = new JButton("Analyze DNA");
            JButton rnaButton = new JButton("Analyze RNA");
            JButton proteinButton = new JButton("Analyze Protein");
            JButton motifButton = new JButton("Find Motif");
            JButton transcriptionButton = new JButton("Transcribe DNA to RNA");
            JButton translationButton = new JButton("Translate RNA to Protein");
            JButton exitButton = new JButton("Exit");

            dnaButton.addActionListener(e -> handleDNAAnalysis());
            rnaButton.addActionListener(e -> handleRNAAnalysis());
            proteinButton.addActionListener(e -> handleProteinAnalysis());
            motifButton.addActionListener(e -> handleMotifFinding());
            transcriptionButton.addActionListener(e -> handleTranscription());
            translationButton.addActionListener(e -> handleTranslation());
            exitButton.addActionListener(e -> System.exit(0));

            panel.add(dnaButton);
            panel.add(rnaButton);
            panel.add(proteinButton);
            panel.add(motifButton);
            panel.add(transcriptionButton);
            panel.add(translationButton);
            panel.add(exitButton);

            frame.add(panel);
            frame.setVisible(true);
        });
    }

    // Handles DNA analysis
    private static void handleDNAAnalysis() {
        String filePath = JOptionPane.showInputDialog("Enter the DNA FASTA file path:");
        if (filePath == null) return;

        java.util.List<Sequence> dnaSequences = readFastaFile(filePath);
        if (dnaSequences.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No valid sequences found in the file.");
            return;
        }

        StringBuilder results = new StringBuilder("DNA Analysis Results:\n");
        for (Sequence sequence : dnaSequences) {
            double gcContent = calculateGCContent(sequence.getSequence());
            String reverseComplement = calculateReverseComplement(sequence.getSequence());
            results.append("Name: ").append(sequence.getName()).append("\n")
                    .append("Sequence: ").append(sequence.getSequence()).append("\n")
                    .append("GC Content: ").append(gcContent).append("%\n")
                    .append("Reverse Complement: ").append(reverseComplement).append("\n\n");
        }
        showResultsWithCopyButton("DNA Analysis Results", results.toString());
    }

    // Handles RNA analysis
    private static void handleRNAAnalysis() {
        String filePath = JOptionPane.showInputDialog("Enter the RNA FASTA file path:");
        if (filePath == null) return;

        java.util.List<Sequence> rnaSequences = readFastaFile(filePath);
        if (rnaSequences.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No valid sequences found in the file.");
            return;
        }

        StringBuilder results = new StringBuilder("RNA Analysis Results:\n");
        for (Sequence sequence : rnaSequences) {
            String translatedProtein = translateRNA(sequence.getSequence());
            results.append("Name: ").append(sequence.getName()).append("\n")
                    .append("Sequence: ").append(sequence.getSequence()).append("\n")
                    .append("Translated Protein: ").append(translatedProtein).append("\n\n");
        }
        showResultsWithCopyButton("RNA Analysis Results", results.toString());
    }

    // Handles Protein analysis
    private static void handleProteinAnalysis() {
        String filePath = JOptionPane.showInputDialog("Enter the Protein FASTA file path:");
        if (filePath == null) return;

        java.util.List<Sequence> proteinSequences = readFastaFile(filePath);
        if (proteinSequences.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No valid sequences found in the file.");
            return;
        }

        StringBuilder results = new StringBuilder("Protein Analysis Results:\n");
        for (Sequence sequence : proteinSequences) {
            results.append("Name: ").append(sequence.getName()).append("\n")
                    .append("Sequence: ").append(sequence.getSequence()).append("\n")
                    .append("Length: ").append(sequence.getSequence().length()).append("\n\n");
        }
        showResultsWithCopyButton("Protein Analysis Results", results.toString());
    }

    // Handles motif finding
    private static void handleMotifFinding() {
        String sequence = JOptionPane.showInputDialog("Enter the sequence:");
        String motif = JOptionPane.showInputDialog("Enter the motif to search for:");
        if (sequence == null || motif == null) return;

        java.util.List<Integer> positions = findMotif(sequence, motif);
        StringBuilder results = new StringBuilder("Motif Found at Positions:\n");
        for (int position : positions) {
            results.append(position).append(" ");
        }

        if (positions.isEmpty()) {
            results.append("No matches found.");
        }

        showResultsWithCopyButton("Motif Search Results", results.toString());
    }

    // Handles Transcription (DNA to RNA)
    private static void handleTranscription() {
        String dnaSequence = JOptionPane.showInputDialog("Enter the DNA sequence:");
        if (dnaSequence == null) return;

        String rnaSequence = transcribeDNAtoRNA(dnaSequence);
        StringBuilder results = new StringBuilder("Transcription Result:\n")
                .append("DNA: ").append(dnaSequence).append("\n")
                .append("RNA: ").append(rnaSequence);

        showResultsWithCopyButton("Transcription Results", results.toString());
    }

    // Handles Translation (RNA to Protein)
    private static void handleTranslation() {
        String rnaSequence = JOptionPane.showInputDialog("Enter the RNA sequence:");
        if (rnaSequence == null) return;

        String proteinSequence = translateRNA(rnaSequence);
        StringBuilder results = new StringBuilder("Translation Result:\n")
                .append("RNA: ").append(rnaSequence).append("\n")
                .append("Protein: ").append(proteinSequence);

        showResultsWithCopyButton("Translation Results", results.toString());
    }

    // Transcribes DNA to RNA
    private static String transcribeDNAtoRNA(String dna) {
        return dna.replace('T', 'U');
    }

    // Translates RNA to Protein
    private static String translateRNA(String rna) {
        StringBuilder protein = new StringBuilder();
        Map<String, String> codonTable = createCodonTable();

        for (int i = 0; i < rna.length() - 2; i += 3) {
            String codon = rna.substring(i, i + 3);
            String aminoAcid = codonTable.get(codon);
            if (aminoAcid != null) {
                protein.append(aminoAcid);
            } else {
                break;
            }
        }

        return protein.toString();
    }

    // Creates a codon table for translation (simplified version)
    private static Map<String, String> createCodonTable() {
        Map<String, String> codonTable = Map.ofEntries(
                Map.entry("UUU", "F"), Map.entry("UUC", "F"), Map.entry("UUA", "L"), Map.entry("UUG", "L"),
                Map.entry("CUU", "L"), Map.entry("CUC", "L"), Map.entry("CUA", "L"), Map.entry("CUG", "L"),
                Map.entry("AUU", "I"), Map.entry("AUC", "I"), Map.entry("AUA", "I"), Map.entry("AUG", "M"),
                Map.entry("GUU", "V"), Map.entry("GUC", "V"), Map.entry("GUA", "V"), Map.entry("GUG", "V"),
                Map.entry("UCU", "S"), Map.entry("UCC", "S"), Map.entry("UCA", "S"), Map.entry("UCG", "S"),
                Map.entry("CCU", "P"), Map.entry("CCC", "P"), Map.entry("CCA", "P"), Map.entry("CCG", "P"),
                Map.entry("ACU", "T"), Map.entry("ACC", "T"), Map.entry("ACA", "T"), Map.entry("ACG", "T"),
                Map.entry("GCU", "A"), Map.entry("GCC", "A"), Map.entry("GCA", "A"), Map.entry("GCG", "A"),
                Map.entry("UAU", "Y"), Map.entry("UAC", "Y"), Map.entry("UAA", "*"), Map.entry("UAG", "*"),
                Map.entry("CAU", "H"), Map.entry("CAC", "H"), Map.entry("CAA", "Q"), Map.entry("CAG", "Q"),
                Map.entry("AAU", "N"), Map.entry("AAC", "N"), Map.entry("AAA", "K"), Map.entry("AAG", "K"),
                Map.entry("GAU", "D"), Map.entry("GAC", "D"), Map.entry("GAA", "E"), Map.entry("GAG", "E"),
                Map.entry("UGU", "C"), Map.entry("UGC", "C"), Map.entry("UGA", "*"), Map.entry("UGG", "W"),
                Map.entry("CGU", "R"), Map.entry("CGC", "R"), Map.entry("CGA", "R"), Map.entry("CGG", "R"),
                Map.entry("AGU", "S"), Map.entry("AGC", "S"), Map.entry("AGA", "R"), Map.entry("AGG", "R"),
                Map.entry("GGU", "G"), Map.entry("GGC", "G"), Map.entry("GGA", "G"), Map.entry("GGG", "G")
        );

        return codonTable;
    }

    // Reads sequences from a FASTA file
    private static java.util.List<Sequence> readFastaFile(String filePath) {
        java.util.List<Sequence> sequences = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder sequence = new StringBuilder();
            String name = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(">")) {
                    if (sequence.length() > 0) {
                        sequences.add(new Sequence(name, sequence.toString()));
                        sequence.setLength(0);
                    }
                    name = line.substring(1).trim(); // Get sequence name (skip '>')
                } else {
                    sequence.append(line.trim());
                }
            }
            if (sequence.length() > 0) {
                sequences.add(new Sequence(name, sequence.toString()));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage());
        }
        return sequences;
    }

    // Calculates GC content
    private static double calculateGCContent(String sequence) {
        long gcCount = sequence.chars()
                .filter(ch -> ch == 'G' || ch == 'C')
                .count();
        return (double) gcCount / sequence.length() * 100;
    }

    // Calculates reverse complement
    private static String calculateReverseComplement(String sequence) {
        Map<Character, Character> complement = new HashMap<>();
        complement.put('A', 'T');
        complement.put('T', 'A');
        complement.put('G', 'C');
        complement.put('C', 'G');

        StringBuilder reverseComplement = new StringBuilder();
        for (int i = sequence.length() - 1; i >= 0; i--) {
            reverseComplement.append(complement.getOrDefault(sequence.charAt(i), 'N'));
        }
        return reverseComplement.toString();
    }

    // Finds motif positions in a sequence
    private static java.util.List<Integer> findMotif(String sequence, String motif) {
        java.util.List<Integer> positions = new ArrayList<>();
        for (int i = 0; i <= sequence.length() - motif.length(); i++) {
            if (sequence.substring(i, i + motif.length()).equals(motif)) {
                positions.add(i + 1); // 1-based indexing
            }
        }
        return positions;
    }

    // Displays results with a Copy to Clipboard button
    private static void showResultsWithCopyButton(String title, String results) {
        // Create a JFrame for the results
        JFrame frame = new JFrame(title);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a JTextArea to display the results
        JTextArea textArea = new JTextArea(results);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Create a Copy to Clipboard button
        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(results);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });

        // Add components to the frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(copyButton, BorderLayout.SOUTH);

        // Display the frame
        frame.setVisible(true);
    }

    // Sequence class to store name and sequence
    static class Sequence {
        private final String name;
        private final String sequence;

        public Sequence(String name, String sequence) {
            this.name = name;
            this.sequence = sequence;
        }

        public String getName() {
            return name;
        }

        public String getSequence() {
            return sequence;
        }
    }
}
