package util;

import javax.swing.*;

public final class Message {
    public static void showMessageInfo(String title, String msg) {
        JOptionPane.showMessageDialog(null,
                msg,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showMessageWarning(String title, String msg) {
        JOptionPane.showMessageDialog(null,
                msg,
                title,
                JOptionPane.WARNING_MESSAGE);
    }

    public static void showMessageError(String title, String msg) {
        JOptionPane.showMessageDialog(null,
                msg,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
}
