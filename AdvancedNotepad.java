import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class AdvancedNotepad extends JFrame implements ActionListener {
    JTextArea textArea;
    JLabel wordCountLabel;
    JFileChooser fileChooser;
    boolean isDarkMode = false;

    public AdvancedNotepad() {
        setTitle("Advanced Notepad");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Word count label
        wordCountLabel = new JLabel("Words: 0");
        add(wordCountLabel, BorderLayout.SOUTH);

        // File chooser
        fileChooser = new JFileChooser();

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // File Menu
        JMenu fileMenu = new JMenu("File");
        String[] fileItems = {"New", "Open", "Save", "Exit"};
        addMenuItems(fileMenu, fileItems);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        String[] editItems = {"Cut", "Copy", "Paste", "Find & Replace"};
        addMenuItems(editMenu, editItems);

        // View Menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem darkMode = new JMenuItem("Toggle Dark Mode");
        darkMode.addActionListener(this);
        viewMenu.add(darkMode);

        // Font Size
        JMenu fontMenu = new JMenu("Font Size");
        for (int size = 12; size <= 36; size += 4) {
            JMenuItem fontSize = new JMenuItem(String.valueOf(size));
            fontSize.addActionListener(e -> textArea.setFont(new Font("Arial", Font.PLAIN, Integer.parseInt(e.getActionCommand()))));
            fontMenu.add(fontSize);
        }

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(fontMenu);

        // Word count listener
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateWordCount(); }
            public void removeUpdate(DocumentEvent e) { updateWordCount(); }
            public void insertUpdate(DocumentEvent e) { updateWordCount(); }
        });
    }

    private void addMenuItems(JMenu menu, String[] items) {
        for (String item : items) {
            JMenuItem menuItem = new JMenuItem(item);
            menuItem.addActionListener(this);
            menu.add(menuItem);
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "New":
                textArea.setText("");
                break;

            case "Open":
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        textArea.read(br, null);
                    } catch (IOException ex) {
                        showError("Error opening file.");
                    }
                }
                break;

            case "Save":
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                        textArea.write(bw);
                    } catch (IOException ex) {
                        showError("Error saving file.");
                    }
                }
                break;

            case "Exit":
                System.exit(0);
                break;

            case "Cut":
                textArea.cut();
                break;

            case "Copy":
                textArea.copy();
                break;

            case "Paste":
                textArea.paste();
                break;

            case "Find & Replace":
                showFindReplaceDialog();
                break;

            case "Toggle Dark Mode":
                toggleDarkMode();
                break;
        }
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        Color bg = isDarkMode ? Color.DARK_GRAY : Color.WHITE;
        Color fg = isDarkMode ? Color.WHITE : Color.BLACK;
        textArea.setBackground(bg);
        textArea.setForeground(fg);
        textArea.setCaretColor(fg);
    }

    private void showFindReplaceDialog() {
        JDialog dialog = new JDialog(this, "Find & Replace", false);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField findField = new JTextField();
        JTextField replaceField = new JTextField();
        JButton replaceButton = new JButton("Replace All");

        dialog.add(new JLabel("Find:"));
        dialog.add(findField);
        dialog.add(new JLabel("Replace With:"));
        dialog.add(replaceField);
        dialog.add(new JLabel(""));
        dialog.add(replaceButton);

        replaceButton.addActionListener(ae -> {
            String findText = findField.getText();
            String replaceText = replaceField.getText();
            if (!findText.isEmpty()) {
                String content = textArea.getText().replaceAll(findText, replaceText);
                textArea.setText(content);
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void updateWordCount() {
        String text = textArea.getText().trim();
        int words = text.isEmpty() ? 0 : text.split("\\s+").length;
        wordCountLabel.setText("Words: " + words);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdvancedNotepad().setVisible(true));
    }
}
