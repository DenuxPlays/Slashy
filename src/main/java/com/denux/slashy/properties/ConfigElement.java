package com.denux.slashy.properties;

import com.denux.slashy.services.Config;

import java.io.*;
import java.util.Properties;


public class ConfigElement {
    String entryname;

    ConfigElement(String entryname){
        this.entryname = entryname;
    }


    public void save(String value) throws IOException {
        Properties prop = new Properties();
        prop.load(new BufferedInputStream(new FileInputStream(Config.CONFIG_PATH)));
        prop.setProperty(entryname,value);
        prop.store(new FileOutputStream(Config.CONFIG_PATH),"");
    }

    public String load() throws IOException {
        Properties prop = new Properties();
        prop.load(new BufferedInputStream(new FileInputStream(Config.CONFIG_PATH)));
        return prop.getProperty(entryname);
    }
    boolean isRegisteredInConfig() {
        try{
            Properties prop = new Properties();
            prop.load(new BufferedInputStream(new FileInputStream(Config.CONFIG_PATH)));
            return prop.containsKey(entryname);
        }catch (Exception e){return false;}
    }
}
