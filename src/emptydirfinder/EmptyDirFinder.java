package emptydirfinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 * @author Тиилл
 */
public class EmptyDirFinder {

    static StartJFrame stFrame;
    static File mainPath;
    static List<File> currentListDirs = new LinkedList<>();
    static ArrayList<File> listAllFiles = new ArrayList<>();
    static ArrayList<File> listAllDirs = new ArrayList<>();
    static List<File> globalEmptyDirs = new LinkedList<>();
    private static final Object lock = new Object();
    static MJFrame mFraim;
    static Settings settings = new Settings();

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println(new Date(System.currentTimeMillis()));               //debug
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


        try {
            mainPath = new File(".").getCanonicalFile();
        } catch (IOException ex) {
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                mFraim = new MJFrame();
                mFraim.setVisible(true);
            }
        });
    }

    private static void oldWalkFileTree() {
        File[] i = mainPath.listFiles();
        for (File x : i) {
            if (x.isDirectory()) {
                currentListDirs.add(x);
                listAllDirs.add(x);
                stFrame.incjTextField2();
            }
        }

        while (!currentListDirs.isEmpty()) {
            List<File> inverter = new LinkedList<>();
            File x = currentListDirs.get(0);
            currentListDirs.remove(0);
            File[] p = x.listFiles();
            if (p == null) {
                listAllDirs.remove(listAllDirs.indexOf(x));
                continue;
            }  /*Если список null значит нет доступа к папке*/

            for (File pos : p) {
                if (pos.isDirectory()) {
                    inverter.add(0, pos);
                    stFrame.incjTextField2();
                }
                if (pos.isFile()) {
                    listAllFiles.add(pos);
                    stFrame.incjTextField1();
                }
            }
            if (inverter.size() > 0) {
                currentListDirs.addAll(0, inverter);
                listAllDirs.addAll(0, inverter);
            }
        }
    }

    private static void debug() throws FileNotFoundException, IOException {
        FileOutputStream fout = new FileOutputStream("Dirs.txt");
        fout.write(listAllDirs.toString().getBytes());
        FileOutputStream fout2 = new FileOutputStream("Files.txt");
        fout2.write(listAllFiles.toString().getBytes());
        FileOutputStream fout3 = new FileOutputStream("DeletingDirs.txt");
        fout3.write(globalEmptyDirs.toString().getBytes());
        fout.close();
        fout2.close();
        fout3.close();
    }

    private static void findemptydirsWithString() throws FileNotFoundException, IOException {
        FileOutputStream fout = new FileOutputStream("Solutions.txt");
        String files = listAllFiles.toString();
        for (File x : listAllDirs) {
            String y = x.toString() + "\t" + files.indexOf(x.getAbsolutePath());
            fout.write(y.getBytes());
            if (!files.contains(x.getAbsolutePath())) {
                globalEmptyDirs.add(x);
            }
        }
    }

    private static void findemptydirsWithFile() throws FileNotFoundException, IOException {
        File[] flistdirs = new File[listAllDirs.size()];
        File[] flistfiles = new File[listAllFiles.size()];
        flistfiles = listAllFiles.toArray(flistfiles);
        for (File x : listAllDirs.toArray(flistdirs)) {
            stFrame.incjTextField3();
            boolean tr = false;
            String path = x.getPath();
            for (int y = 0; y < flistfiles.length; y++) {
                if (flistfiles[y].getAbsolutePath().contains(path)) {
                    tr = true;
                    break;
                }
            }
            if (!tr) {
                globalEmptyDirs.add(x);
            }
        }

    }

    private static void findemptydirsWithDelete() throws FileNotFoundException, IOException {
        globalEmptyDirs = new LinkedList<>(listAllDirs);

        for (File x : listAllFiles) {
            stFrame.incjTextField3();
            System.out.println(globalEmptyDirs.size());
            String path = x.getAbsolutePath();
            Iterator<File> iter = globalEmptyDirs.iterator();
            for (int z = 0; z < globalEmptyDirs.size(); z++) {
                System.out.println("+");
                File y = iter.next();
                if (y == null) {
                    continue;
                }
                if (path.contains(y.getPath())) {
                    globalEmptyDirs.remove(z);
                }
            }
        }
    }

    private static boolean findemptydirsWithRecOnlyTOP(File z) {
        synchronized (lock) {
            ArrayList<File> localeEmptyDirs = new ArrayList<>();
            if (z == null) {
                System.out.println("File null.");
                return false;
            }
            if (z.isFile()) {
                stFrame.incjTextField1();
                return true;
            }
            if (z.isDirectory()) {
                stFrame.incjTextField2();
                boolean notEmpty = false;
                File[] childrenFiles = z.listFiles();
                if (childrenFiles == null) {
                    System.out.println("blocked: " + z);
                    return true;
                }
                for (File x : childrenFiles) {
                    boolean answer = findemptydirsWithRecOnlyTOP(x);
                    if (answer == true) {
                        notEmpty = true;
                    } else {
                        localeEmptyDirs.add(x);
                    }
                }
                if (notEmpty == false) {
                    stFrame.incjTextField3();
                    return false;
                } else {
                    globalEmptyDirs.addAll(localeEmptyDirs);
                    return true;
                }

            }
            System.out.println("not one predicate");
            return false;
        }
    }

    private static boolean findemptydirsWithRecAll(File z) {
        synchronized (lock) {
            if (z == null) {
                System.out.println("File null.");
                return false;
            }
            if (z.isFile()) {
                for (String ignoreFile : settings.IGNORE_FILES) {
                    Pattern pat = Pattern.compile(ignoreFile);
                    Matcher mat = pat.matcher(z.getName());
                    if (mat.matches()) {
                        System.out.println("Ignore:" + z.getName());
                        return false;
                    }
                    if (settings.IGNORE_OMB == true && 0 == z.length()) {
                        System.out.println("Ignore:" + z.getName());
                        return false;
                    }
                }
                stFrame.incjTextField1();
                return true;
            }
            if (z.isDirectory()) {
                stFrame.incjTextField2();
                boolean notEmpty = false;
                File[] childrenFiles = z.listFiles();
                if (childrenFiles == null) {
                    System.out.println("blocked: " + z);
                    return true;
                }
                for (File x : childrenFiles) {
                    boolean answer = findemptydirsWithRecAll(x);
                    if (answer == true) {
                        notEmpty = true;
                    } else {
                    }
                }
                if (notEmpty == false) {
                    stFrame.incjTextField3();
                    globalEmptyDirs.add(z);
                    return false;
                } else {
                    return true;
                }

            }
            System.out.println("not one predicate");
            return false;
        }
    }

    public void restart() throws IOException {
        new restartThread().start();
    }

    class restartThread extends Thread {

        @Override
        public void run() {
            mainPath = new File(mFraim.getjTextField1().getText());
            mFraim.dispose();
            mFraim = null;

            currentListDirs = new LinkedList<>();
            listAllFiles = new ArrayList<>();
            listAllDirs = new ArrayList<>();
            globalEmptyDirs = new LinkedList<>();

            stFrame = new StartJFrame();
            stFrame.setVisible(true);

            findemptydirsWithRecAll(mainPath);
            System.out.println(new Date(System.currentTimeMillis()));               //debug
            try {
                debug();
            } catch (IOException e) {
                System.out.println(e);
            }

            /* Close start window */
            stFrame.setVisible(false);
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    /* Create and display the form */
                    mFraim = new MJFrame();
                    mFraim.setVisible(true);
                }
            });
        }

    }
}
