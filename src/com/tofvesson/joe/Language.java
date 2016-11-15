package com.tofvesson.joe;

import com.sun.istack.internal.Nullable;

import java.io.*;
import java.util.ArrayList;

public class Language {

    private final String language, languageID;


    private Language(String language, String languageID){ this.language = language; this.languageID = languageID; }

    public String getLanguage(){ return language; }
    public String getLanguageIdentifier(){ return languageID; }

    /**
     * A safe version of {@link #parse(File)} that will simply return <b>null</b> if given file isn't a valid language file.
     * @param f File to read from.
     * @return Language or null.
     */
    public static @Nullable Language safeParse(File f){
        try{ return parse(f); }catch(Exception ignored){}
        return null;
    }

    /**
     * Parses the given file into a usable language.
     * @param f File to parse.
     * @return Language.
     * @throws NotALanguageFileException Thrown if file isn't a valid language file.
     */
    public static Language parse(File f) throws NotALanguageFileException, MalformedLanguageException, IOException {
        String s="";


        // Is file existent?
        if(!f.isFile()) throw new FileNotFoundException("File "+f.getAbsolutePath()+" isn't a file!");


        // Does file meet preliminary requirements?
        InputStream i = new FileInputStream(f);
        int c;
        s = ignoreSpaces(readLine(i));
        i.close();
        for(int j=0; j<s.length(); ++j)
            if(!s.substring(j, j+1).equals("\t") && !s.substring(j, j+1).equals(" ")){
                s = s.substring(j);
                break;
            }
        if(!s.startsWith("Language:")) throw new NotALanguageFileException("First line does not identify the language: "+s);
        int start = -1, end = -1;
        for(int j=9; j<s.length(); ++j) if(!s.substring(j, j+1).equals("\t") && !s.substring(j, j+1).equals(" ")){ start = j; break; }
        for(int j=s.length(); j>9; --j) if(!s.substring(j-1, j).equals("\t") && !s.substring(j-1, j).equals(" ")){ end = j; break; }
        if(start==-1 || end==-1 || start>=end) throw new NotALanguageFileException("Malformed language name definition: \""+s+"\"");


        // Check language integrity
        ArrayList<String> keys = new ArrayList<>();
        i = new FileInputStream(f);
        String subVerify;
        boolean firstLine = true;
        char read;
        int lineCount = 1;
        while(i.available()>0){
            read = (char) i.read();
            if(firstLine && read=='\n'){
                firstLine=false;
                continue;
            }
            subVerify = read+readLine(i);
            ++lineCount;
            if(!isValidKVPair(subVerify)) throw new MalformedLanguageException("Error found at line "+lineCount
                    +" : \""+subVerify+"\"\nInvalid key-value pair detected! Note that ':' in the keys or values must be escaped with '\\'");
            String s1 = getKey(truncateLeadingSpaces(subVerify));
            if(keys.contains(s1)) throw new MalformedLanguageException("Error found at line "+lineCount+" : "
                    +subVerify+"\nDuplicate key detected!");
            keys.add(getKey(truncateLeadingSpaces(subVerify)));
        }
        return new Language(s.substring(start, end), f.getName().substring(0, f.getName().lastIndexOf('.')));
    }

    private static String ignoreSpaces(String s){
        s=s.replace(" ", "");
        s=s.replace("\t", "");
        return s;
    }

    private static String truncateLeadingSpaces(String s){
        char[] str = s.toCharArray();
        for(int i = 0; i<str.length; ++i) if(str[i]!=' ' && str[i]!='\t') return s.substring(i);
        return s;
    }

    private static String readLine(InputStream i){
        String s = "";
        char j;
        try{ while(i.available()>0 && (j=(char)i.read())!='\n') s+=j; }catch(IOException ignored){}
        return s;
    }

    private static int amountOf(char toFind, String in){
        char[] c = in.toCharArray();
        int ctr = 0;
        for(char c1 : c) if(c1==toFind) ++ctr;
        return ctr;
    }

    private static Type getTypeForString(String name){
        for(Type t : Type.values())
            if(name.equalsIgnoreCase(t.name())) return t;
        return null;
    }

    private static boolean isValidKVPair(String data){
        boolean colonFound = false;
        char[] str = truncateLeadingSpaces(data).toCharArray();
        char prev = 0;
        for(int i = 0; i<str.length; ++i) {
            if (i != 0){
                if(str[i]==':' && prev!='\\')
                    if(colonFound) return false;
                    else colonFound = true;
            }else if(str[i]==':') return false;
            prev = str[i];
        }
        return colonFound;
    }

    private static String getKey(String data){
        char[] str = truncateLeadingSpaces(data).toCharArray();
        char prev = 0;
        for(int i = 0; i<str.length; ++i) {
            if (i != 0){
                if(str[i]==':' && prev!='\\')
                    return data.substring(0, i);
            }
            prev = str[i];
        }
        return data;
    }
}
