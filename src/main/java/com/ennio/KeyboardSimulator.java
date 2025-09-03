package com.ennio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class KeyboardSimulator extends JFrame {

    private final JTextArea textArea;
    private final JButton startButton;
    private final JButton clearButton;
    private final JLabel statusLabel;
    private final JProgressBar progressBar;
    private ImageIcon appIcon;
    private AsciiKeyTyper asciiKeyTyper = new AsciiKeyTyper();

    private boolean isRunning = false;

    public KeyboardSimulator() throws AWTException {
        // 设置窗口属性
        setTitle("键盘模拟输入工具");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        // 加载应用图标
        loadAppIcon();
        // 创建组件
        textArea = new JTextArea(10, 40);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);

        startButton = new JButton("开始模拟 (Ctrl+Enter)");
        clearButton = new JButton("清空内容");
        statusLabel = new JLabel("就绪 - 输入文本后点击开始按钮");
        progressBar = new JProgressBar(0, 100);
        progressBar.setVisible(false);

        // 设置快捷键
        setupKeyboardShortcuts();

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(clearButton);

        // 创建状态面板
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(progressBar, BorderLayout.SOUTH);

        // 添加组件到窗口
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);
        add(statusPanel, BorderLayout.SOUTH);

        // 添加事件监听器
        startButton.addActionListener(e -> startSimulation());
        clearButton.addActionListener(e -> textArea.setText(""));
    }
    private ImageIcon createDefaultIcon() {
        // 创建一个简单的默认图标
        Image image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics g2d = image.getGraphics();
        g2d.setColor(new Color(70, 130, 180));
        g2d.fillRect(0, 0, 64, 64);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("K", 20, 45);
        g2d.dispose();
        return new ImageIcon(image);
    }

    private void loadAppIcon() {
        try {
            // 从resources目录加载图标
            appIcon = new ImageIcon(KeyboardSimulator.class.getClassLoader().getResource("keyboard_icon.ico"));
            setIconImage(appIcon.getImage());
        } catch (Exception e) {
            System.out.println("图标加载失败，使用默认图标: " + e.getMessage());
            // 创建默认图标
            appIcon = createDefaultIcon();
            setIconImage(appIcon.getImage());
        }
    }

    private void setupKeyboardShortcuts() {
        // Ctrl+Enter 开始模拟
        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "startSimulation");
        textArea.getActionMap().put("startSimulation", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });

        // Esc 取消操作
        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelSimulation");
        textArea.getActionMap().put("cancelSimulation", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isRunning = false;
                statusLabel.setText("操作已取消");
                progressBar.setVisible(false);
                startButton.setEnabled(true);
            }
        });
    }

    private void startSimulation() {
        if (isRunning) return;
        
        String text = textArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要模拟的文本内容", "输入为空", JOptionPane.WARNING_MESSAGE);
            return;
        }

        isRunning = true;
        startButton.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setValue(0);

        // 使用新线程执行模拟操作
        new Thread(() -> {
            try {
                // 倒计时提示
                for (int i = 5; i > 0 && isRunning; i--) {
                    updateStatus("将在 " + i + " 秒后开始 - 请切换到目标窗口...", 0);
                    Thread.sleep(1000);
                }

                if (!isRunning) return;

                // 使用剪贴板粘贴方式输入文本
                pasteTextUsingClipboard(text);
                
                updateStatus("输入完成!", 100);
                Thread.sleep(1000);
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    isRunning = false;
                    startButton.setEnabled(true);
                    progressBar.setVisible(false);
                    statusLabel.setText("就绪 - 输入文本后点击开始按钮");
                });
            }
        }).start();
    }

    private void pasteTextUsingClipboard(String text) throws AWTException {
        Robot robot = new Robot();
        // 逐字符输入文本
        for (char c : text.toCharArray()) {
            typeChar(robot, c);
            robot.delay(10);
        }
    }

    // 模拟单个字符的键入
    private synchronized void typeChar(Robot robot, char c) {
        boolean upperCase = Character.isUpperCase(c);
        System.out.println("Is Upper case ? " + upperCase);

        KeyStroke ks = KeyStroke.getKeyStroke('k', 0);
        System.out.println(ks.getKeyCode());

        try {
            int keyCode = ks.getKeyCode();
            if (keyCode == KeyEvent.VK_UNDEFINED) {
                return;
            }
            System.out.println("keyCode : " + keyCode);

            asciiKeyTyper.typeKey(c);

        } catch (IllegalArgumentException e) {
            // 跳过无法识别的字符
            e.printStackTrace();
        }
    }

    // 判断是否为需要 shift 的特殊字符
    private boolean isSpecialShiftChar(char c) {
        String special = "~!@#$%^&*()_+{}|:\\\\\\\\\\\"<>?"; // 需要 shift 的符号
        return special.indexOf(c) >= 0;
    }

    private int getPasteShortcutKey() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            return KeyEvent.VK_META;
        } else {
            return KeyEvent.VK_CONTROL;
        }
    }

    private void updateStatus(String message, int progress) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            progressBar.setValue(progress);
        });
    }

    public static void main(String[] args) throws IOException {
        KeyboardLayoutManager.setKeyboardLayout("00000409");
        SwingUtilities.invokeLater(() -> {
            KeyboardSimulator simulator = null;
            try {
                simulator = new KeyboardSimulator();
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
            simulator.setVisible(true);
        });
    }
}