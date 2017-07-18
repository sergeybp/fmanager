package dialogs;

import javax.swing.*;
import java.awt.*;

public class MessageDialog extends JDialog {

    public MessageDialog(String name, String text) {
        super();
        setName(name);
        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
        pan.add(new JLabel(text));
        add(pan);
        setSize(200, 100);
        setLocation(MouseInfo.getPointerInfo().getLocation());
    }
}
