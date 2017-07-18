package dialogs;

import javax.swing.*;
import java.awt.*;

public class ErrorDialog extends JDialog {

    public JButton button;

    public ErrorDialog(String name, String text, String buttonText, int w, int h) {
        super();
        init(name, text, buttonText, w, h);

    }

    public ErrorDialog(String name, String text, String buttonText) {
        super();
        init(name, text, buttonText, 200, 100);
    }

    private void init(String name, String text, String buttonText, int w, int h) {
        setName(name);
        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
        pan.add(new JLabel(text));
        button = new JButton(buttonText);
        pan.add(button);
        add(pan);
        setSize(w, h);
        setLocation(MouseInfo.getPointerInfo().getLocation());
    }
}
