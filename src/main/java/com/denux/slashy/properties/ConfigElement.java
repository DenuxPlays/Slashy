package com.denux.slashy.properties;

import com.denux.slashy.services.Constants;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;


public class ConfigElement {
    String entryname;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigElement.class);

    ConfigElement(String entryname){
        this.entryname = entryname;
    }

    public static void init() {
        try { new File(Constants.CONFIG_PATH).createNewFile();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void save(String value) throws IOException {
        Properties prop = new Properties();
        try { prop.load(new BufferedInputStream(new FileInputStream(Constants.CONFIG_PATH)));
        } catch (FileNotFoundException e) {
            logger.info("Properties file on path \"{}\" is missing. Initializing one now.", Constants.CONFIG_PATH);
            init(); save(value); }
        prop.setProperty(entryname,value);
        prop.store(new FileOutputStream(Constants.CONFIG_PATH),"");
    }

    public String load() throws IOException {
        Properties prop = new Properties();
        prop.load(new BufferedInputStream(new FileInputStream(Constants.CONFIG_PATH)));
        return prop.getProperty(entryname);
    }
    boolean isRegisteredInConfig() {
        try{
            Properties prop = new Properties();
            prop.load(new BufferedInputStream(new FileInputStream(Constants.CONFIG_PATH)));
            return prop.containsKey(entryname);
        }catch (Exception e){return false;}
    }
}
