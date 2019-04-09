package emptydirfinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmptyDirFinder {

    static StartJFrame stFrame;
    static File mainPath;
    static ArrayList<File> listAllFiles = new ArrayList<>();
    static int countAllFiles = 0;
    static ArrayList<File> listAllDirs = new ArrayList<>();
    static int countAllDirs = 0;
    static List<File> globalEmptyDirs = new LinkedList<>();
    private static final Object lock = new Object();
    static MJFrame mFraim;
    static Settings settings = new Settings();
    static Thread processThread = null;

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

    private static boolean findemptydirsWithRecAll(File z) {
        synchronized (lock) {
            if (z == null) {
                System.out.println("File null.");
                return false;
            }
            if (z.isFile()) {
                if (!settings.pat.isEmpty()) {
                    for (Pattern ignoreFile : settings.pat) {
                        Matcher mat = ignoreFile.matcher(z.getName());
                        if (mat.matches()) {
                            System.out.println("Ignore:" + z.getName());
                            return false;
                        }
                        if (settings.IGNORE_OMB == true && 0 == z.length()) {
                            System.out.println("Ignore:" + z.getName());
                            return false;
                        }
                    }
                }
                stFrame.incjTextField1();
                countAllFiles++;
                return true;
            }
            if (z.isDirectory()) {
                stFrame.incjTextField2();
                countAllDirs++;
                if (settings.IGNORE_SYS == true) {
                    for (String x : settings.IGNORE_SYSTEM_DIRECTRIES) {
                        if (z.getAbsolutePath().equals(x)) {
                            System.out.println("blocked: " + z);
                            return true;
                        }
                    }
                }
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

    public void restart(int version) throws IOException {
        processThread = new restartThread(version);
        processThread.start();
    }

    class restartThread extends Thread {

        private int versionToUse = 1;

        public restartThread(int versionToUse) {
            this.versionToUse = versionToUse;
        }

        @Override
        public void run() {
            mainPath = new File(mFraim.getjTextField1().getText());
            mFraim.dispose();

            globalEmptyDirs = new LinkedList<>();

            settings.pat.clear();
            for (String ignoreFile : settings.IGNORE_FILES) {
                settings.pat.add(Pattern.compile(ignoreFile));
            }

            stFrame = new StartJFrame();
            stFrame.setVisible(true);

            System.out.println(new Date(System.currentTimeMillis()));               //debug
            if (versionToUse == 1) {
                findemptydirsWithRecAll(mainPath);
            }
            if (versionToUse == 2) {
                new NIOBrowser().Find();
            }
            System.out.println(new Date(System.currentTimeMillis()));               //debug

            /* Close start window */
            //stFrame.dispose();
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
