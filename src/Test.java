import com.tofvesson.joe.Language;

import java.io.File;

public class Test {
    public static void main(String args[]){
        try{
            Language test = Language.parse(new File(Test.class.getResource("/Test.txt").getFile()));
            System.out.println("Language: "+test.getLanguage());
            System.out.println("Identifier: "+test.getLanguageIdentifier());
        }catch(Exception e){ e.printStackTrace();}
    }
}
