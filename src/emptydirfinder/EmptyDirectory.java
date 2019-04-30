/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emptydirfinder;

import java.awt.Color;

/**
 *
 * @author Тиилл
 */
public class EmptyDirectory {
     public String SPath = null;
     public int emptySubDirectorys;
     public int igmoreSubFiles;
     public int emptySubFiles;
     private static int maxLongPath=0;
     public Color color;

    public EmptyDirectory(String Path,int emptySubDirectorys, int igmoreSubFiles, int emptySubFiles) {
        this.SPath = Path;
        this.emptySubDirectorys = emptySubDirectorys;
        this.igmoreSubFiles = igmoreSubFiles;
        this.emptySubFiles = emptySubFiles;
        if(Path.length()>maxLongPath) maxLongPath = Path.length();
    }

    @Override
    public String toString() {
        int spasesForInformation = (maxLongPath-SPath.length())+4;
        String toInsert = String.format("%1$"+spasesForInformation+"s", "(");
        StringBuilder answer = new StringBuilder();
        answer.append(SPath).append(toInsert)
                .append(emptySubDirectorys).append(" SubDirs;  ")
                .append(igmoreSubFiles).append(" ignoredFiles;  ")
                .append(emptySubFiles).append(" emptyFiles)");
        return answer.toString();
    }

    static void ResetLengthToString(){
        maxLongPath = 0;
    }
    
    
    
}
