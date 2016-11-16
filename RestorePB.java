import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
public class RestorePB {
    public static void main(String[] argv) {
        JFileChooser chooser = new JFileChooser(".");
        JFrame f = new JFrame("Load EasyPrint");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("EasyPrint Object", "pb");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(f);
        f.dispose();
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            EasyPrint pb = new EasyPrint();
            pb.load(chooser.getSelectedFile());
            pb.preview();
        }
    }
}
