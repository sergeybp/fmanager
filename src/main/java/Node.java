import java.io.File;

public class Node {

    boolean isDirectory = false;
    boolean isOpen = false;
    boolean isLoading = false;
    private File file;

    Node(File file) {
        this.file = file;
    }

    Node(File file, boolean isDirectory) {
        this.file = file;
        this.isDirectory = isDirectory;
    }

    @Override
    public String toString() {
        String name = file.getName();
        if (name.equals("")) {
            return file.getAbsolutePath();
        } else {
            return name;
        }
    }

    File getFile() {
        return file;
    }


}
