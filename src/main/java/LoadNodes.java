import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.File;

public class LoadNodes implements Runnable {

    private DefaultMutableTreeNode root;
    private File fileRoot;
    private JTree tree;
    private TreePath path;
    private boolean needExpand;

    LoadNodes(File fileRoot,
              DefaultMutableTreeNode root, JTree tree, TreePath path, boolean needExpand) {
        this.fileRoot = fileRoot;
        this.root = root;
        this.tree = tree;
        this.path = path;
        this.needExpand = needExpand;
    }


    public void run() {
        if (((Node) (root.getUserObject())).isLoading) {
            return;
        }
        ((Node) (root.getUserObject())).isLoading = true;
        root.removeAllChildren();
        createChildren(fileRoot, root);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (needExpand) {
            tree.expandPath(path);
        }
        ((Node) (root.getUserObject())).isLoading = false;
        ((Node) (root.getUserObject())).isOpen = true;

    }

    private void createChildren(File fileRoot,
                                DefaultMutableTreeNode node) {
        File[] files = fileRoot.listFiles();
        if (files == null) {
            needExpand = false;
            return;
        }
        if (files.length == 0) {
            needExpand = false;
            return;
        }

        for (File file : files) {
            DefaultMutableTreeNode childNode =
                    new DefaultMutableTreeNode(new Node(file));

            node.add(childNode);
            if (file.isDirectory()) {
                ((Node) childNode.getUserObject()).isDirectory = true;
            }

        }
    }

}
