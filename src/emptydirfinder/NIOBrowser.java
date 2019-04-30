package emptydirfinder;

import static emptydirfinder.EmptyDirFinder.settings;
import static emptydirfinder.EmptyDirFinder.stFrame;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Тиилл
 */
public class NIOBrowser {

    private int countLevelIN = 0;
//    private Path TemperedEmptyFolder = null;
    private final List<Integer> countEmptyFiles = new LinkedList<>();
    private final List<Integer> countIgnoredFiles = new LinkedList<>();
    private final List<Integer> countEmptyDirectories = new LinkedList<>();
    {
        countEmptyDirectories.add(0);
    }

    public void Find() {
        try {
            Files.walkFileTree(EmptyDirFinder.mainPath.toPath(), new FileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    //проверка прерывания потока
                    if (EmptyDirFinder.processThread.isInterrupted()) {
                        return FileVisitResult.TERMINATE;
                    }

                    //Увидили новую папку счетчик ++
                    countLevelIN++;
                    countEmptyFiles.add(0);
                    countIgnoredFiles.add(0);
                    countEmptyDirectories.add(0);
                    countEmptyDirectories.set(countLevelIN-1, countEmptyDirectories.get(countLevelIN-1)+1);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    //проверка прерывания потока
                    if (EmptyDirFinder.processThread.isInterrupted()) {
                        return FileVisitResult.TERMINATE;
                    }

                    if (!settings.pat.isEmpty()) {
                        for (Pattern ignoreFile : settings.pat) {
                            Matcher mat = ignoreFile.matcher(file.getFileName().toString());
                            if (mat.matches()) {
//                                System.out.println("Ignore:" + file.getFileName().toString());
                                Integer o = countIgnoredFiles.get(countIgnoredFiles.size() - 1);
                                countIgnoredFiles.set(countIgnoredFiles.size() - 1, o+1);
                                return FileVisitResult.CONTINUE;
                            }
                            if (settings.IGNORE_OMB == true && 0 == file.toFile().length()) {
//                                System.out.println("Ignore:" + file.getFileName().toString());
                                Integer i = countEmptyFiles.get(countEmptyFiles.size() - 1);
                                countEmptyFiles.set(countEmptyFiles.size() - 1, i+1);
                                return FileVisitResult.CONTINUE;
                            }
                        }
                    }
                    //Увидели файл сбрасываем счетчик и проверяем были ли до этого пустые папки
                    countLevelIN = 0;
                    countEmptyFiles.clear();
                    countIgnoredFiles.clear();
                    countEmptyDirectories.clear();
                    countEmptyFiles.add(0);
                    countIgnoredFiles.add(0);
                    countEmptyDirectories.add(0);
//                    if (TemperedEmptyFolder != null) {
//                        stFrame.incjTextField3();
//                        EmptyDirectory emptydir = new EmptyDirectory(TemperedEmptyFolder.toFile().getAbsolutePath(), 0, 0, 0);
//                        EmptyDirFinderV2.globalEmptyDirs.add(emptydir);
//                        TemperedEmptyFolder = null;
//                    }

                    stFrame.incjTextField1();
                    EmptyDirFinder.countAllFiles++;

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    //проверка прерывания потока
                    if (EmptyDirFinder.processThread.isInterrupted()) {
                        return FileVisitResult.TERMINATE;
                    }

                    System.out.println("No access to path: " + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    //проверка прерывания потока
                    if (EmptyDirFinder.processThread.isInterrupted()) {
                        return FileVisitResult.TERMINATE;
                    }

                    if (settings.IGNORE_SYS == true) {
                        for (String x : settings.IGNORE_SYSTEM_DIRECTRIES) {
                            if (dir.toString().equals(x)) {
                                System.out.println("blocked: " + dir.toString());
                                EmptyDirFinder.countAllDirs++;
                                return FileVisitResult.CONTINUE;
                            }
                        }
                    }

                    stFrame.incjTextField2();
                    EmptyDirFinder.countAllDirs++;

//                    if (countLevelIN > 1) {
//                        countLevelIN--;
////                        TemperedEmptyFolder = dir;
//                        EmptyDirectory emptyDir = new EmptyDirectory(dir.toFile().getAbsolutePath(), 0, 0, 0);
//                        EmptyDirFinderV2.globalEmptyDirs.add(emptyDir);
//                    } else if (countLevelIN == 1) {
//                        countLevelIN--;
////                        TemperedEmptyFolder = null;
//                        stFrame.incjTextField3();
//                        EmptyDirectory emptyDir = new EmptyDirectory(dir.toFile().getAbsolutePath(), 0, 0, 0);
//                        EmptyDirFinderV2.globalEmptyDirs.add(emptyDir);
//                    }
                    if (countLevelIN > 0) {
                        countLevelIN--;
                        stFrame.incjTextField3();
                        EmptyDirectory emptyDir = new EmptyDirectory(dir.toFile().getAbsolutePath(), countEmptyDirectories.get(countLevelIN+1), countIgnoredFiles.get(countIgnoredFiles.size() - 1), countEmptyFiles.get(countEmptyFiles.size() - 1));
                        countEmptyFiles.remove(countEmptyFiles.size() - 1);
                        countIgnoredFiles.remove(countIgnoredFiles.size() - 1);
                        EmptyDirFinder.globalEmptyDirs.add(emptyDir);
                        countEmptyDirectories.remove(countLevelIN+1);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            //super.run(); //To change body of generated methods, choose Tools | Templates.
        } catch (IOException ex) {
            Logger.getLogger(NIOBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
