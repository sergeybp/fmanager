import dialogs.ErrorDialog;
import dialogs.MessageDialog;
import dialogs.SimpleDialog;
import menus.SimpleMenu;
import utils.CopyBuffer;
import utils.Utils;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;

public class Main implements Runnable {

    private Utils utils;
    private CopyBuffer copyBuffer;
    private HashSet<DefaultMutableTreeNode> loadingNodes = new HashSet<>();
    private Icon closedFolderIcon, openFolderIcon, fileIcon, loadingIcon;
    private JTree tree;

    public void run() {
        closedFolderIcon = new ImageIcon(Main.class.getResource("folder-closed.png"));
        fileIcon = new ImageIcon(Main.class.getResource("file.png"));
        openFolderIcon = new ImageIcon(Main.class.getResource("folder-open.png"));
        loadingIcon = new ImageIcon(Main.class.getResource("sand-clock.png"));

        utils = new Utils();
        copyBuffer = new CopyBuffer();

        JFrame frame = new JFrame("FManager");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final File fileRoot = FileSystemView.getFileSystemView().getHomeDirectory();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new Node(fileRoot, true));
        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);

        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    tree.setSelectionPath(tree.getPathForLocation(e.getX(), e.getY()));
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        showMenu(e, ((Node) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getFile());
                    }
                    return;
                }
                int row = tree.getRowForLocation(e.getX(), e.getY());
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (row != -1) {
                    if (e.getClickCount() == 2) {
                        if (path == null) {
                            return;
                        }
                        Node now = ((Node) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject());
                        if (now.isLoading) {
                            return;
                        }
                        if (now.isOpen) {
                            tree.collapsePath(path);
                            ((DefaultMutableTreeNode) path.getLastPathComponent()).removeAllChildren();
                            now.isOpen = false;
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    ((DefaultTreeModel) (tree.getModel())).nodeChanged(((DefaultMutableTreeNode) path.getLastPathComponent()));
                                } catch (Exception ignored) {

                                }
                            });
                        } else {

                            LoadNodes ccn =
                                    new LoadNodes(now.getFile(), (DefaultMutableTreeNode) path.getLastPathComponent(), tree, path, true);
                            new Thread(ccn).start();
                        }
                    }
                }

            }
        };
        tree.addMouseListener(ml);
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(final JTree tree,
                                                          Object value, boolean selected, boolean expanded,
                                                          boolean isLeaf, int row, boolean focused) {
                Component c = super.getTreeCellRendererComponent(tree, value,
                        selected, expanded, isLeaf, row, focused);
                TreePath path = tree.getPathForRow(row);
                if (path != null) {
                    final DefaultMutableTreeNode currentTreeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Node currentNode = (Node) currentTreeNode.getUserObject();
                    if (currentNode.isDirectory) {
                        if (currentNode.isLoading) {
                            loadingNodes.add(currentTreeNode);
                            setIcon(loadingIcon);
                        } else {
                            if (loadingNodes.contains(currentTreeNode)) {
                                loadingNodes.remove(currentTreeNode);
                                for (int i = 0; i < currentTreeNode.getChildCount(); i++) {
                                    final int finalI = i;
                                    SwingUtilities.invokeLater(() -> {
                                        try {
                                            ((DefaultTreeModel) (tree.getModel())).nodeChanged(currentTreeNode.getChildAt(finalI));
                                        } catch (Exception ignored) {

                                        }
                                    });
                                }
                            }
                            if (currentNode.isOpen) {
                                setIcon(openFolderIcon);
                            } else {
                                setIcon(closedFolderIcon);
                            }
                        }
                    } else {
                        setIcon(fileIcon);
                    }
                }
                return c;
            }
        });
        tree.setShowsRootHandles(true);
        tree.setRowHeight(32);
        JScrollPane scrollPane = new JScrollPane(tree);
        frame.add(scrollPane);
        frame.setLocationByPlatform(true);
        frame.setSize(640, 480);
        frame.setVisible(true);
        showWelcomeMessage();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main());
    }

    private void showMenu(MouseEvent e, final File file) {
        SimpleMenu menu;
        if (file.isDirectory()) {
            menu = new SimpleMenu("Delete", "Copy", "Paste", "New folder");
            menu.items.get(3).addActionListener(e1 -> createFolderLogic(file));
            menu.items.get(2).addActionListener(e12 -> {
                copyBuffer.to = file;
                goCopyLogic();
            });
        } else {
            menu = new SimpleMenu("Delete", "Copy");
        }
        menu.items.get(0).addActionListener(e13 -> deleteFolderLogic(file));
        menu.items.get(1).addActionListener(e14 -> copyBuffer.from = file);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void createFolderLogic(File file) {
        final SimpleDialog dialog;
        dialog = new SimpleDialog(file, "New folder", "Enter name:", "Create");
        dialog.button.addActionListener(e -> {
            String name = dialog.textField.getText();
            if (utils.createFolder(dialog.file, name)) {
                Node n = new Node(new File(dialog.file, name));
                n.isDirectory = true;
                DefaultMutableTreeNode x = new DefaultMutableTreeNode(n);
                ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).add(x);
                ((DefaultTreeModel) tree.getModel()).reload(((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()));
                dialog.setVisible(false);
            } else {
                dialog.dispose();
                ErrorDialog errorDialog = new ErrorDialog("Error", "Error while creating.", "OK");
                errorDialog.button.addActionListener(e1 -> errorDialog.dispose());
                SwingUtilities.invokeLater(() -> errorDialog.setVisible(true));
            }
        });
        SwingUtilities.invokeLater(() -> dialog.setVisible(true));
    }

    private void deleteFolderLogic(File file) {
        if (utils.deleteFile(file)) {
            ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()));
        } else {
            ErrorDialog errorDialog = new ErrorDialog("Error", "Error while deleting.", "OK");
            errorDialog.button.addActionListener(e -> errorDialog.dispose());
            SwingUtilities.invokeLater(() -> errorDialog.setVisible(true));
        }
    }

    private void goCopyLogic() {
        if (copyBuffer.from == null) {
            ErrorDialog dialog = new ErrorDialog("Error", "No file or directory selected", "OK");
            dialog.button.addActionListener(e -> dialog.dispose());
            SwingUtilities.invokeLater(() -> dialog.setVisible(true));
            return;
        }
        SwingUtilities.invokeLater(() -> {
            MessageDialog messageDialog = new MessageDialog("File copy", "Please wait...");
            messageDialog.setVisible(true);
            File res = utils.copyFiles(copyBuffer.from, copyBuffer.to);
            if (res != null) {
                DefaultMutableTreeNode now = (((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()));
                if(((Node)now.getUserObject()).isOpen) {
                    boolean isDir = res.isDirectory();
                    Node n = new Node(res, isDir);
                    now.add(new DefaultMutableTreeNode(n));
                    ((DefaultTreeModel) tree.getModel()).reload(((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()));
                }
                messageDialog.dispose();
            } else {
                messageDialog.dispose();
                ErrorDialog dialog = new ErrorDialog("Error", "Error while copying.", "OK");
                dialog.button.addActionListener(e -> dialog.dispose());
                SwingUtilities.invokeLater(() -> dialog.setVisible(true));
            }
        });
    }

    private void showWelcomeMessage(){
        ErrorDialog dialog = new ErrorDialog("Welcome", "<html>Double-Left click to expand/collapse node.<br>Right click to show extra menu.</html>", "OK", 300,100);
        dialog.setLocation(170,190);
        dialog.button.addActionListener(e -> dialog.dispose());
        SwingUtilities.invokeLater(() -> dialog.setVisible(true));
    }

}