package emptydirfinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Тиилл
 */
public class EmptyDirFinder {

    static File mainPath;
    static Queue<File> listdirs = new LinkedList<>();
    static ArrayList<File> listfiles = new ArrayList<>();
    static ArrayList<File> listalldirs = new ArrayList<>();
    static ArrayList<File> deletedirs = new ArrayList<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        StartJFrame stframe= new StartJFrame();
        stframe.setVisible(true);
        
        
        try {
            mainPath = new File(".").getCanonicalFile();
        } catch (IOException ex) {
        }

        File[] i = mainPath.listFiles();
        for (File x : i) {
            if (x.isDirectory()) {
                listdirs.add(x);
                listalldirs.add(x);
                stframe.incjTextField2();
            }
        }

        while (!listdirs.isEmpty()) {
            File x = listdirs.poll();
            File[] p = x.listFiles();
            if (p == null){listalldirs.remove(listalldirs.size()-1);continue;}  /*Если список null значит нет доступа к папке*/
            for (File pos : p) {
                if (pos.isDirectory()) {
                    listdirs.add(pos);
                    listalldirs.add(pos);
                    stframe.incjTextField2();
                }
                if (pos.isFile()) {
                    listfiles.add(pos);
                    stframe.incjTextField1();
                }
            }
        }

        File[] flistdirs = new File[listalldirs.size()];
        File[] flistfiles = new File[listfiles.size()];
        flistfiles = listfiles.toArray(flistfiles);
        for (File x : listalldirs.toArray(flistdirs)) {
            boolean tr = false;
            String path = x.getPath();
            for (int y = 0; y < flistfiles.length; y++) {
                if (flistfiles[y].getPath().contains(path)) {
                    tr = true;
                    break;
                }
            }
            if (!tr) {
                deletedirs.add(x);
            }
        }
        
        deletedirs.sort(new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                int z = o1.compareTo(o2);
                if(z < 0) z=1;
                if(z > 0) z=-1;
                return z;
            }
        });

        stframe.setVisible(false);
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MJFrame().setVisible(true);
            }
        });
    }

}
