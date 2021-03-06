package com.tofvesson.joe;

import com.sun.istack.internal.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings({"WeakerAccess", "unused", "ConstantConditions", "SameParameterValue"})
public class Language {

    private final String language, languageID, entry;
    private Map<String, String> data = new HashMap<>();
    private final File f;
    private final ZipFile zip;


    private Language(ZipFile zip, String entry, String language, String languageID){
        this.zip = zip;
        this.entry = entry;
        this.language = language;
        this.languageID = languageID;
        this.f = null;
    }

    private Language(File f, String language, String languageID){
        this.f = f;
        this.language = language;
        this.languageID = languageID;
        entry = null;
        zip = null;
    }

    private Language(){
        f = null;
        language="";
        languageID="";
        entry = "";
        zip = null;
    }


    public String getLanguage(){ return language; }
    public String getLanguageIdentifier(){ return languageID; }
    public String get(String key) {
        if(f==null && zip==null && (entry==null || (entry!=null && entry.equals("")))) return "";
        if(data.containsKey(key))
            return data.get(key);
        try {
            InputStream i = f!=null?new FileInputStream(f):zip.getInputStream(zip.getEntry(entry));
            readLine(i);
            String s, s1="", s2="";
            while(i.available()>0){
                if((s2=s1=readLine(i)).equals(key)){
                    char[] c = s1.toCharArray();
                    char n, m=0, k=0;
                    for(int o = 0; o<c.length; ++o){
                        n=m;
                        m=k;
                        k=c[o];
                        if(n!='\\' && m=='/' && k=='/'){
                            s1 = s1.substring(0, o-1);
                            break;
                        }else if(n=='\\' && m=='/' && k=='/') s1 = s1.substring(0, o-2) + s1.substring(o-1, s1.length());
                    }
                    if(s1.equals(key)) break;
                }
            }
            if(s1.equals(key)){
                data.put(s1, s=s2.length()==key.length()+1?"":getValue(s1));
                return s;
            }
        } catch (IOException ignored) { ignored.printStackTrace(); }
        return null;
    }
    public int getInt(String key){
        String s = get(key);
        if(s==null) return 0;
        try{
            return Integer.parseInt(s);
        }catch(Exception e){ return 0; }
    }
    public double getDouble(String key){
        String s = get(key);
        if(s==null) return 0;
        try{
            return Double.parseDouble(s);
        }catch(Exception e){ return 0; }
    }
    public boolean getBoolean(String key, boolean defaultValue){
        String s;

        return (s = get(key))==null||(!s.equalsIgnoreCase("true")&&!s.equals("1")&&!s.equalsIgnoreCase("false")&&
                !s.equals("0"))?defaultValue:(s.equalsIgnoreCase("true")||s.equals("1"))&&(!s.equalsIgnoreCase("false")||!s.equals("0"));
    }

    /**
     * Parses data aggressively from zip.
     * @param zip Zip file to load language data from.
     * @param entry Entry in zip file.
     * @return Language or null.
     */
    public static @Nullable Language safeParse(ZipFile zip, String entry){ return safeParse(zip, entry, true); }

    /**
     * A safe version of {@link #parse(ZipFile, String, boolean)} that will simply return <b>null</b> if given file isn't a valid language file.
     * @param zip Zip file to load language data from.
     * @param entry Entry in zip file.
     * @param aggressiveLoading Whether or not to aggressively load and handle data.
     * @return Language or null.
     */
    public static @Nullable Language safeParse(ZipFile zip, String entry, boolean aggressiveLoading){
        try{ return parse(zip, entry, aggressiveLoading); }catch(Exception ignored){}
        return null;
    }

    /**
     * Parses data aggressively.
     * @param f File to read from.
     * @return Language or null.
     */
    public static @Nullable Language safeParse(File f){
        return safeParse(f, true);
    }

    /**
     * A safe version of {@link #parse(File, boolean)} that will simply return <b>null</b> if given file isn't a valid language file.
     * @param f File to read from.
     * @param aggressiveParsing Whether or not to aggressively load and handle data.
     * @return Language or null.
     */
    public static @Nullable Language safeParse(File f, boolean aggressiveParsing){
        try{ return parse(f, aggressiveParsing); }catch(Exception ignored){}
        return null;
    }

    /**
     * Parses the given zip entry into a usable language very aggressively.
     * @param zip Zip file to load language data from.
     * @param entry Entry in zip file.
     * @return Language.
     * @throws NotALanguageFileException if Zip data isn't a real zip file or entry.
     * @throws IOException if data can't be read.
     * @throws MalformedLanguageException if language file isn't specified correctly.
     */
    public static Language parse(ZipFile zip, String entry) throws NotALanguageFileException, IOException, MalformedLanguageException { return parse(zip, entry, true); }

    /**
     * Parses the given zip entry into a usable language.
     * @param zip Zip file to load language data from.
     * @param entry Entry in zip file.
     * @param aggressiveParsing Whether or not to aggressively load and handle data.
     * @return Language.
     * @throws NotALanguageFileException Thrown if file isn't a valid language file.
     * @throws IOException if data can't be read.
     * @throws MalformedLanguageException if language file isn't specified correctly.
     */
    public static Language parse(ZipFile zip, String entry, boolean aggressiveParsing) throws NotALanguageFileException, IOException, MalformedLanguageException {
        return parse(zip, entry, null, aggressiveParsing);
    }

    /**
     * Parses the given file into a usable language very aggressively.
     * @param f File to parse.
     * @return Language.
     * @throws NotALanguageFileException Thrown if file isn't a valid language file.
     * @throws IOException if data can't be read.
     * @throws MalformedLanguageException if language file isn't specified correctly.
     */
    public static Language parse(File f) throws NotALanguageFileException, IOException, MalformedLanguageException {
        return parse(f, true);
    }

    /**
     * Parses the given file into a usable language.
     * @param f File to parse.
     * @param aggressiveParsing Whether or not to aggressively load and handle data.
     * @return Language.
     * @throws NotALanguageFileException Thrown if file isn't a valid language file.
     * @throws IOException if data can't be read.
     * @throws MalformedLanguageException if language file isn't specified correctly.
     */
    public static Language parse(File f, boolean aggressiveParsing) throws NotALanguageFileException, IOException, MalformedLanguageException {
        // Is file existent?
        if(!f.isFile()) throw new FileNotFoundException("File "+f.getAbsolutePath()+" isn't a file!");
        return parse(null, "", f, aggressiveParsing);
    }

    /**
     * Parses the given file or zip entry data into a usable language.
     * @param zip Zip file to load language data from.
     * @param entry Entry in zip file.
     * @param f File to parse.
     * @param aggressiveParsing Whether or not to aggressively load and handle data.
     * @return Language.
     * @throws NotALanguageFileException Thrown if file isn't a valid language file.
     * @throws IOException if data can't be read.
     * @throws MalformedLanguageException if language file isn't specified correctly.
     */
    private static Language parse(ZipFile zip, String entry, File f, boolean aggressiveParsing) throws NotALanguageFileException, MalformedLanguageException, IOException {
        String s;
        ZipEntry z = zip==null?null:zip.getEntry(entry);
        if(z==null && zip!=null) throw new NotALanguageFileException("File "+zip.getName()+":/"+entry+" isn't a file!");

        InputStream i = zip==null?new FileInputStream(f):zip.getInputStream(zip.getEntry(entry));

        // Does file meet preliminary requirements?
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


        Language l = zip==null ? new Language(f, s.substring(start, end), f.getName().substring(0, f.getName().lastIndexOf('.')))
                : new Language(zip, entry, s.substring(start, end), entry.substring(entry.lastIndexOf("/")+1).substring(0, entry.lastIndexOf('.')-entry.substring(0, entry.lastIndexOf("/")).length()-1));

        // Check language integrity
        ArrayList<String> keys = new ArrayList<>();
        i = zip==null?new FileInputStream(f):zip.getInputStream(zip.getEntry(entry));
        String subVerify;
        boolean firstLine = true;
        char read;
        int lineCount = 0;
        while(i.available()>0){
            subVerify = truncateLeadingSpaces(readLine(i));
            if(firstLine){
                ++lineCount;
                firstLine = false;
                continue;
            }
            char[] c = subVerify.toCharArray();
            char n, m=0, k=0;
            for(int o = 0; o<c.length; ++o){
                n=m;
                m=k;
                k=c[o];
                if(n!='\\' && m=='/' && k=='/'){
                    subVerify = subVerify.substring(0, o-1);
                    break;
                }else if(n=='\\' && m=='/' && k=='/') subVerify = subVerify.substring(0, o-2) + subVerify.substring(o-1, subVerify.length());
            }
            if(subVerify.length()==0 || subVerify.toCharArray().length==0 || subVerify.toCharArray()[0]=='\n') continue;
            ++lineCount;
            if(!isValidKVPair(subVerify)) throw new MalformedLanguageException("Error found at line "+lineCount
                    +" of "+f.getAbsolutePath()+". Invalid key-value pair detected! Note that ':' in the keys or values must be escaped with '\'");
            String s1 = getKey(subVerify);
            if(keys.contains(s1)) throw new MalformedLanguageException("Error found at line "+lineCount+" : "
                    +subVerify+"\nDuplicate key detected!");
            keys.add(s1);
            if(aggressiveParsing) l.data.put(s1, getValue(subVerify));
        }
        return l;
    }

    private static String ignoreSpaces(String s){
        s=s.replace(" ", "");
        s=s.replace("\t", "");
        return s;
    }

    private static String truncateLeadingSpaces(String s){
        char[] str = s.toCharArray();
        for(int i = 0; i<str.length; ++i) if(str[i]!=' ' && str[i]!='\t') return s.substring(i);
        return "";
    }

    private static String readLine(InputStream i){
        String s = "";
        try{
            char j;
            while(i.available()>0 && (j=(char)i.read())!='\n' && j!=13) s+=j;
        }catch(IOException ignored){}
        return s;
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

    private static String getValue(String data){
        char[] str = truncateLeadingSpaces(data).toCharArray();
        char prev = 0;
        String p1="", p2="";
        for(int i = str.length-1; i>0; --i) {
            if (i != str.length-1){
                if(str[i]!='\\' && prev==':') {
                    if(data.indexOf(":")+1==data.length()) return "";
                    p1 = data.substring(i + 2, data.length());
                    break;
                }
            }
            prev = str[i];
        }
        str = p1.toCharArray();
        for(int i = 0; i<p1.length(); ++i){
            if(i != 0)
                if(!(str[i]==':' && prev=='\\')) p2+=prev;
            prev = str[i];
        }
        p2+=prev;
        return p2;
    }
}
