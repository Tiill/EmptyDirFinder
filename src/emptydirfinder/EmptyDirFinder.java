package emptydirfinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class EmptyDirFinder {

    static StartJFrame stFrame;
    static File mainPath;
    static int countAllFiles = 0;
    static int countAllDirs = 0;
    static List<EmptyDirectory> globalEmptyDirs = new LinkedList<>();
    static MJFrame mFraim;
    static Settings settings = new Settings();
    static Thread processThread = null;
    static LocalTime TimeOfProccess = LocalTime.MIDNIGHT;

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

    public void restart() throws IOException {
        processThread = new restartThread();
        processThread.start();
    }

    class restartThread extends Thread {
        

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            mainPath = new File(mFraim.getjTextField1().getText());
            mFraim.dispose();

            globalEmptyDirs = new LinkedList<>();
            countAllDirs=0;
            countAllFiles=0;
            EmptyDirectory.ResetLengthToString();

            settings.pat.clear();
            for (String ignoreFile : settings.IGNORE_FILES) {
                settings.pat.add(Pattern.compile(ignoreFile));
            }

            stFrame = new StartJFrame();
            stFrame.setVisible(true);

            Date startTime = new Date(System.currentTimeMillis());
            LocalTime Time = LocalTime.now();
            new NIOBrowser().Find();
            LocalTime lTime = LocalTime.ofSecondOfDay(LocalTime.now().toSecondOfDay()-Time.toSecondOfDay());
            TimeOfProccess = lTime;

            /* Close start window */
            stFrame.dispose();
//            stFrame.setVisible(false);
            globalEmptyDirs.sort(new Comparator<EmptyDirectory>() {

                @Override
                public int compare(EmptyDirectory o1, EmptyDirectory o2) {
                    return o1.SPath.compareTo(o2.SPath);
                }
            });
            
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
