package utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Utils {

    public boolean createFolder(File file, String name){
        File dir = new File(file, name);
        return dir.mkdir();
    }

    public boolean deleteFile(File file){
        if(file.isDirectory()){
            try {
                FileUtils.deleteDirectory(file);
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return file.delete();
        }
    }

    public File copyFiles(File source, File target){
        if(!source.isDirectory()){
            try {
                File dest = new File(target, source.getName());
                FileUtils.copyFile(source, dest);
                return dest;
            } catch (IOException e) {
                return null;
            }
        } else {
            try {
                File dest = new File(target, source.getName());
                FileUtils.forceMkdir(dest);
                FileUtils.copyDirectory(source, dest);
                return dest;
            } catch (IOException e) {
                return null;
            }
        }
    }

}
