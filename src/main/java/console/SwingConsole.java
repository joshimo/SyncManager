package console;

import javax.swing.*;

public class SwingConsole extends JScrollPane implements Console{

    private JTextPane console = new JTextPane();
    private static SwingConsole ourInstance = new SwingConsole();

    public static SwingConsole getInstance() {
        return ourInstance;
    }

    private SwingConsole() {
        this.setViewportView(console);
        console.setEditable(false);
    }

    public void setConsoleText(String text) {

        if (console.getText().isEmpty() || console.getText() == null)
            console.setText(text);
        else
            console.setText(console.getText() + "\n" + text);

        console.setCaretPosition(console.getText().length());
    }
}
