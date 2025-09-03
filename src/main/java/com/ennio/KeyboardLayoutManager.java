package com.ennio;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

import java.io.IOException;

public class KeyboardLayoutManager {

    // Windows 平台 JNA 接口
    public interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        int LoadKeyboardLayoutA(String pwszKLID, int Flags);
        int ActivateKeyboardLayout(int hkl, int Flags);
    }

    /**
     * 切换键盘布局（根据操作系统自动调用）
     * @param layoutCode 布局代码，例如：
     *                   Windows: "00000409" (美式英文), "00000804" (简体中文)
     *                   Linux:   "us" (美式英文), "fr" (法语)
     *                   Mac:     "ABC" (输入法名称, 可能需要自己调整)
     */
    public static void setKeyboardLayout(String layoutCode) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            setKeyboardLayoutWindows(layoutCode);
        } else if (os.contains("linux")) {
            setKeyboardLayoutLinux(layoutCode);
        } else if (os.contains("mac")) {
            setKeyboardLayoutMac(layoutCode);
        } else {
            throw new UnsupportedOperationException("不支持的操作系统: " + os);
        }
    }

    // Windows 下切换键盘布局
    private static void setKeyboardLayoutWindows(String layoutCode) {
        int hkl = User32.INSTANCE.LoadKeyboardLayoutA(layoutCode, 1);
        User32.INSTANCE.ActivateKeyboardLayout(hkl, 0);
        System.out.println("Windows 键盘布局已切换到: " + layoutCode);
    }

    // Linux 下切换键盘布局 (需要系统安装 setxkbmap)
    private static void setKeyboardLayoutLinux(String layoutCode) throws IOException {
        Runtime.getRuntime().exec("setxkbmap " + layoutCode);
        System.out.println("Linux 键盘布局已切换到: " + layoutCode);
    }

    // MacOS 下切换键盘布局 (模拟快捷键或 AppleScript)
    private static void setKeyboardLayoutMac(String layoutCode) throws IOException {
        // ⚠️ 注意：这里的 layoutCode 要改成你系统里的输入法名称
        String script = String.format(
                "tell application \"System Events\" to tell process \"SystemUIServer\" " +
                        "to click (menu item \"%s\" of menu 1 of menu bar item 1 of menu bar 1)", layoutCode
        );
        Runtime.getRuntime().exec(new String[]{"osascript", "-e", script});
        System.out.println("MacOS 键盘布局已切换到: " + layoutCode);
    }

    // 测试方法
    public static void main(String[] args) throws Exception {
        // 示例：Windows 切换到美式英文 (00000409)，Linux 切换到 us，MacOS 切换到 "ABC"
        setKeyboardLayout("00000409");
    }
}
