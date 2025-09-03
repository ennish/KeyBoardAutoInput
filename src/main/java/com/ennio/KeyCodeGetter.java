package com.ennio;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.im.InputContext;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class KeyCodeGetter extends JFrame {

    public KeyCodeGetter() {
        setTitle("键盘键码获取器 - Java 1.8 兼容");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText("请点击此处然后按任意键...");

        // 添加键盘监听器
        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                char keyChar = e.getKeyChar();
                String keyText = KeyEvent.getKeyText(keyCode);
                InputContext context = InputContext.getInstance();
                Locale locale = context.getLocale();

                String message = String.format(
                        "键码: %d\n键字符: %c\n键名称: %s , 当前输入法语言环境: %s \n",
                        keyCode, keyChar, keyText, locale
                );

                textArea.setText(message);

                // 阻止事件继续传播
                e.consume();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // 不需要实现
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // 不需要实现
            }
        });

        add(textArea);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        // 使用SwingUtilities确保线程安全
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new KeyCodeGetter();
            }
        });
    }
}