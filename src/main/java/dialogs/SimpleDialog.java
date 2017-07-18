package dialogs;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SimpleDialog extends JDialog {

    public JButton button;
    public File file;
    public JTextField textField;

    public SimpleDialog(File file, String name, String text , String buttonText) {
        super();
        this.file = file;
        setName(name);
        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
        pan.add(new JLabel(text));
        textField = new JTextField();
        pan.add(textField);
        button = new JButton(buttonText);
        pan.add(button);
        add(pan);
        setSize(200, 100);
        setLocation(MouseInfo.getPointerInfo().getLocation());
    }

}
