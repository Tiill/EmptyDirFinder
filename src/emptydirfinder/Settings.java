package emptydirfinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Тиилл
 */
public class Settings {
    List<String> IGNORE_FILES = new LinkedList<>();
    boolean IGNORE_OMB = true;

    public Settings() {
        try {
            load();
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    void load() throws FileNotFoundException, IOException, URISyntaxException{
        Properties fileProp = new Properties();
        fileProp.load(new FileInputStream(new File(this.getClass().getResource("/Programm.ini").toURI())));
        IGNORE_OMB = Boolean.valueOf(fileProp.getProperty("IGNORE_OMB"));
        String[] parts = fileProp.getProperty("IGNORE_FILES").split(";");
        IGNORE_FILES = new LinkedList<>();
        for (String part : parts) {
            IGNORE_FILES.add(part);
        }
    }
    
    void save() throws URISyntaxException, FileNotFoundException, IOException{
        FileOutputStream fout = new FileOutputStream(new File(this.getClass().getResource("/Programm.ini").toURI()));
        StringBuilder str = new StringBuilder();
        str.append("IGNORE_OMB =").append(IGNORE_OMB).append("\n");
        str.append("IGNORE_FILES =");
        for(String z :IGNORE_FILES){
            str.append(z).append(";");
        }
        fout.write(str.toString().getBytes());
        fout.close();
    }
    
    void setDefaultSettings(){
        IGNORE_FILES = new LinkedList<>();
        IGNORE_FILES.add("Thumbs.db");
        IGNORE_FILES.add("desktop.ini");
        IGNORE_FILES.add(".*\\.tmp");
        IGNORE_OMB = true;
    }
}
