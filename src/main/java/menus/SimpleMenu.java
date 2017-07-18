package menus;

import javax.swing.*;
import java.util.ArrayList;

public class SimpleMenu extends JPopupMenu {

    public ArrayList<JMenuItem> items;

    public SimpleMenu(String ... elements) {
        super();
        items = new ArrayList<>();
        for(String tmp : elements){
            JMenuItem item = new JMenuItem(tmp);
            items.add(item);
            this.add(item);
        }
    }
}
