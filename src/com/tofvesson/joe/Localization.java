package com.tofvesson.joe;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Localization {

    private Language defaultLang;
    private List<Language> all = new ArrayList<>();

    public Localization(ZipFile zip, String folder, String regexNamingPattern){ this(zip, folder, regexNamingPattern, true); }
    public Localization(ZipFile zip, String folder){ this(zip, folder, true); }
    public Localization(ZipFile zip, String folder, boolean aggressiveLoading){ this(zip, folder, "(.*)", aggressiveLoading); }
    public Localization(ZipFile zip, String folder, String regexNamingPattern, boolean aggressiveLoading){
        Pattern p = Pattern.compile(regexNamingPattern);
        ArrayList<String> files = new ArrayList<>();
        Enumeration<? extends ZipEntry> e = zip.entries();
        ZipEntry tmp;
        while(e.hasMoreElements())
            if((tmp=e.nextElement()).getName().startsWith(folder) && p.matcher(tmp.getName()).find())
                files.add(tmp.getName());
        if(files.size()==0){
            fixFail();
            return;
        }

        try {
            for (String s : files) {
                if(defaultLang==null){
                    defaultLang = Language.parse(zip, s, aggressiveLoading);
                    all.add(defaultLang);
                }else all.add(Language.parse(zip, s, aggressiveLoading));
            }
        }catch(Exception ignored){}

    }
    public Localization(File folder, String regexNamingPattern){ this(folder, regexNamingPattern, true); }
    public Localization(File folder){ this(folder, true); }
    public Localization(File folder, boolean aggressiveLoading){ this(folder, "(.*)", aggressiveLoading); }
    public Localization(File folder, String regexNamingPattern, boolean aggressiveLoading){
        if(folder==null || !folder.isDirectory()) throw new RuntimeException("Folder argument passed isn't a directory!");
        String[] s;
        if((s=folder.list())==null || s.length==0){
            fixFail();
            return;
        }
        try {
            File[] fs = folder.listFiles();
            if(fs==null){
                fixFail();
                return;
            }
            for (File f : fs) {
                if (Pattern.matches(regexNamingPattern, f.getName())){
                    if(defaultLang==null){
                        defaultLang = Language.parse(f, aggressiveLoading);
                        all.add(defaultLang);
                    }else all.add(Language.parse(f, aggressiveLoading));
                }
            }
        }catch(Exception ignored){}
        if(defaultLang==null) fixFail();
    }

    private void fixFail(){
        try{
            Constructor<Language> c = Language.class.getDeclaredConstructor();
            c.setAccessible(true);
            defaultLang=c.newInstance();
            all.add(defaultLang);
        }catch(Exception ignored){}
    }

    public String get(String language, String key){
        for(Language l : all)
            if(l.getLanguage().equals(language))
                return l.get(key);
        return defaultLang.get(key);
    }

    public String get(String key){ return defaultLang.get(key); }

    public int getInt(String language, String key){
        for(Language l : all)
            if(l.getLanguage().equals(language))
                return l.getInt(key);
        return defaultLang.getInt(key);
    }

    public int getInt(String key){ return defaultLang.getInt(key); }

    public double getDouble(String language, String key){
        for(Language l : all)
            if(l.getLanguage().equals(language))
                return l.getDouble(key);
        return defaultLang.getDouble(key);
    }

    public double getDouble(String key){ return defaultLang.getDouble(key); }

    public boolean getBoolean(String language, String key, boolean defaultValue){
        for(Language l : all)
            if(l.getLanguage().equals(language))
                return l.getBoolean(key, defaultValue);
        return defaultLang.getBoolean(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue){ return defaultLang.getBoolean(key, defaultValue); }

    public Language[] getLanguages(){ return all.toArray(new Language[all.size()]); }

    public String[] getLanguageNames(){
        String[] s = new String[all.size()];
        for(int i = 0; i<s.length; ++i) s[i]=all.get(i).getLanguage();
        return s;
    }

    public Language getDefaultLanguage(){ return defaultLang; }

    public Language setDefaultLanguage(Language newLang){ Language l = defaultLang; defaultLang = newLang; return l; }

}
