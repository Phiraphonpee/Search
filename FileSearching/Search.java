import javax.sound.sampled.SourceDataLine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Search {
    ArrayList<String> result = new ArrayList<String>();
    ArrayList<String> filelist = new ArrayList<String>();
    String keyword = ""; // Define KeyWord to search
    void println(String in) {
        System.out.println(in);
    }
    void print(String in) {
        System.out.print(in);
    }
    void saveStrings(String filename,String[] array) {
        File fout = new File(filename);
        try (FileOutputStream fos = new FileOutputStream(fout); BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));) {
            for (String s : array) {
                bw.write(s);
                bw.newLine();
            }
        } catch (IOException ignored) {

        }
    }

    String []loadStrings(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            ArrayList<String> bufs = new ArrayList<String>();
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                bufs.add(line);
            }
            String[] out = new String[bufs.size()];
            for (int i = 0;i < out.length;i++) {
                out[i] = bufs.get(i);
            }
            return out;
        } catch (Exception e) {
            return null;
        }
        
    }
    void realSearch(String rootpath, String savetofile, String keyword) {
        this.keyword = keyword;
        println(rootpath); // Print Root Path
        printlist(rootpath, 0); // Root Path Directory and Level Root Level is 0
        println("Search keyword : " + keyword);
        println("Found: " + result.size() + " Result");
        for (String re : result) { // Print List of Result from Arraylist
            println(re);
        }
        String[] fl = new String[filelist.size()];
        int i = 0;
        for (String re : filelist) { // Print List of Result from Arraylist
            fl[i] = re;
            i++;
        }
        if (savetofile != null) {
            saveStrings(savetofile, fl);
        }
    }

    void fileSearch(String filename, String keyword) {
        String[] s = loadStrings(filename);
        int i = 0;
        for (String re : s) {
            if (search(Pattern.compile(keyword, Pattern.CASE_INSENSITIVE), re)) {
                result.add(re);
            }
            i++;
        }
        for (String re : result) {
            println(re);
        }
    }

    void printlist(String dir, int level) {
        File file = new File(dir);

        if (file.isDirectory()) { // Check if file is Folder
            String names[] = file.list();
            if (names != null) {
            for (int i = 0; i < names.length; i++) {
                for (int j = 0; j < level; j++) {
                    print("\t"); // Print tab level time
                }
                println(names[i]); // print file name or directory name

                filelist.add(dir + "" + names[i]);

                if (search(Pattern.compile(keyword, Pattern.CASE_INSENSITIVE), names[i])) {
                    result.add(dir + "/" + names[i]);
                }
                /*
                 * if (names[i].toLowerCase().indexOf(keyword.toLowerCase()) != -1) { // use
                 * indexOf Function to Search Some String is in Other String result.add(dir +
                 * "\\" + names[i]); // Add File name that match to the keyword to Dynamic
                 * String Array }
                 */
                printlist(dir + "/" + names[i], level + 1); // Print File Tree
            }
        }
        }
    }


    boolean search(Pattern pattern, String strtosearch) {
        return pattern.matcher(strtosearch).find();
    }

    Search() {
        
    }
    public static void main(String[] args) {
        if (args.length == 2) {
            System.out.println("PATH: " + args[0]);
            System.out.println("KEYWORD: " + args[1]);
            new Search().realSearch(args[0], "FileHistory", args[1]);
            
        } else if (args.length > 2) {
            char last = 0;
            String path = "";
            String key = "";
            String output = "FileHistory";
            String input = null;
            for (String arg : args) {
                if (arg.equals("-p")) {
                    last = 'p';
                } else if (arg.equals("-k")) {
                    last = 'k';
                }  else if (arg.equals("-o")) {
                    last = 'o';
                }  else if (arg.equals("-i")) {
                    last = 'i';
                } else {
                    if (last == 'p') {
                        path = arg;
                    } else if (last == 'k') {
                        key = arg;
                    } else if (last == 'o') {
                        output = arg;
                    } else if (last == 'i') {
                        input = arg;
                    }
                    last = 0;
                }
            }
            if (input != null) {
                System.out.println("Search From File");
                new Search().fileSearch(input, key);
            } else {
                System.out.println("Search From Directory");
                new Search().realSearch(path, output, key);
            }
            
        }else {
            System.out.println("Usage: Search 'PathToSearch' 'Regex or Keyword' ");
            System.out.println("Or Use Option\n\n-p RootPathToSearch \n-k KeyWordToSearch (Regex) \n-o saveDirectoryTreeToOutputFile \n-i LoadDirectoryMapFromFile");
        }
    }
}