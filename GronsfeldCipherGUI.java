import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GronsfeldCipherGUI extends JFrame {

    // Русский алфавит (33 буквы, включая Ё)
    private static final String ALPHABET = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final int ALPHABET_SIZE = 33;

    private JTextArea inputArea;
    private JTextArea outputArea;
    private JTextField keyField;

    public GronsfeldCipherGUI() {
    setTitle("Шифр Гронсфельда");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    Font largeFont = new Font("Segoe UI", Font.PLAIN, 16);

    // === Исходный текст ===
    inputArea = new JTextArea(8, 30);
    inputArea.setFont(largeFont);
    JScrollPane inputScroll = new JScrollPane(inputArea);
    inputScroll.setBorder(BorderFactory.createTitledBorder("Исходный текст"));

    // === Поле ключа (исправлено!) ===
    keyField = new JTextField(); // теперь будет растягиваться
    keyField.setFont(largeFont);
    JLabel keyLabel = new JLabel("Ключ (только цифры):");
    keyLabel.setFont(largeFont);

    JPanel keyInputPanel = new JPanel(new BorderLayout());
    keyInputPanel.add(keyLabel, BorderLayout.WEST);
    keyInputPanel.add(keyField, BorderLayout.CENTER);

    JPanel keyPanel = new JPanel(new BorderLayout());
    keyPanel.add(keyInputPanel, BorderLayout.CENTER);
    keyPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    keyPanel.setPreferredSize(new Dimension(0, 50));

    // === Кнопки ===
    JButton encryptButton = new JButton("Зашифровать");
    JButton decryptButton = new JButton("Расшифровать");
    encryptButton.setFont(largeFont);
    decryptButton.setFont(largeFont);

    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(encryptButton);
    buttonPanel.add(decryptButton);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    buttonPanel.setPreferredSize(new Dimension(0, 50));

    encryptButton.addActionListener(new EncryptAction());
    decryptButton.addActionListener(new DecryptAction());

    // === Результат ===
    outputArea = new JTextArea(8, 30);
    outputArea.setEditable(false);
    outputArea.setFont(largeFont);
    JScrollPane outputScroll = new JScrollPane(outputArea);
    outputScroll.setBorder(BorderFactory.createTitledBorder("Результат"));

    // === Сборка интерфейса ===
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(inputScroll, BorderLayout.CENTER);
    leftPanel.add(keyPanel, BorderLayout.SOUTH);

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(outputScroll, BorderLayout.CENTER);
    rightPanel.add(buttonPanel, BorderLayout.SOUTH);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
    splitPane.setResizeWeight(0.5);
    splitPane.setOneTouchExpandable(true);

    ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    add(splitPane, BorderLayout.CENTER);

    setSize(850, 600);
    setMinimumSize(new Dimension(650, 500));
    setLocationRelativeTo(null);
}
    private String processText(String text, String key, boolean encrypt) {
        if (!key.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Ключ должен содержать только цифры!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return "";
        }

        StringBuilder result = new StringBuilder();
        int keyIndex = 0;

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char upperC = Character.toUpperCase(c);
                int charIndex = ALPHABET.indexOf(upperC);

                // Если буква не из русского алфавита — оставляем как есть
                if (charIndex == -1) {
                    result.append(c);
                    continue;
                }

                int shift = Character.getNumericValue(key.charAt(keyIndex % key.length()));
                int newIndex;
                if (encrypt) {
                    newIndex = (charIndex + shift) % ALPHABET_SIZE;
                } else {
                    newIndex = (charIndex - shift + ALPHABET_SIZE) % ALPHABET_SIZE;
                }

                char newChar = ALPHABET.charAt(newIndex);
                result.append(Character.isLowerCase(c) ? Character.toLowerCase(newChar) : newChar);
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private class EncryptAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = inputArea.getText();
            String key = keyField.getText().trim();
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(GronsfeldCipherGUI.this, "Введите ключ!", "Предупреждение", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String result = processText(text, key, true);
            outputArea.setText(result);
        }
    }

    private class DecryptAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = inputArea.getText();
            String key = keyField.getText().trim();
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(GronsfeldCipherGUI.this, "Введите ключ!", "Предупреждение", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String result = processText(text, key, false);
            outputArea.setText(result);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Не удалось установить системный стиль.");
            }
            new GronsfeldCipherGUI().setVisible(true);
        });
    }
}