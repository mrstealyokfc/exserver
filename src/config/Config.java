package config;

import http.TypedInputStream;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Config {

    public static byte[] http404page=null;
    public static Map<String,TypedFileName> fileTable = new ConcurrentHashMap<>();
    private static final HashMap<String,String> FILE_TYPES = loadFileTypes();
    private static volatile long lastReload=0;
    private static final int RELOAD_DELAY=10;//in seconds

    public static void reloadFileTable(){
        if(Instant.now().getEpochSecond()-lastReload>RELOAD_DELAY){
            lastReload=Instant.now().getEpochSecond();
            loadFileTable();
            lastReload=Instant.now().getEpochSecond();
        }
    }



    private static void loadFileTable(){
        fileTable.clear();
        generateFileTable();
        try{
            loadFileTableFromFile();
        }catch(IOException e){
            System.err.println("FAILED TO LOAD THE FILE TABLE");
            e.printStackTrace();
        }
    }

    private static void generateFileTable(){
        File webfiles = new File("webfiles");
        if(!webfiles.isDirectory())
            return;
        ArrayList<String> filenames = new ArrayList<>();
        addAllFiles(webfiles,filenames);
        for(String s : filenames)
            fileTable.put(s.substring(s.indexOf("/")),new TypedFileName(s,getFileType(s)));
    }
    private static String getFileType(String s){
        return FILE_TYPES.get(trimToFileExt(s));
    }
    private static String trimToFileExt(String s){
        int index=0;
        while(s.indexOf(".",index+1)!=-1)
            index=s.indexOf(".",index+1);
        return s.substring(index+1);
    }

    private static void addAllFiles(File file,ArrayList<String> dest){
        if(!file.isDirectory())
            dest.add(file.getPath());
        else
            for(File f : Objects.requireNonNull(file.listFiles()))
                addAllFiles(f,dest);
    }

    private static void loadFileTableFromFile() throws IOException {
        BufferedReader reader  = new BufferedReader(new FileReader("filetable"));
        String line;
        while((line=reader.readLine())!=null)
            processLine(line);
        reader.close();
    }
    private static void processLine(String line)throws ArrayIndexOutOfBoundsException{
        String[] split = line.split(" ");
        fileTable.put(split[0],new TypedFileName("webfiles/"+split[1],split[2]));
    }

    public static HashMap<String,String> loadFileTypes(){
        HashMap<String,String> fileTypes = new HashMap<>();

        fileTypes.put("html","text/html");
        fileTypes.put("htm","text/html");
        fileTypes.put("css" ,"text/css");
        fileTypes.put("js"  ,"text/javascript");
        fileTypes.put("png" ,"image/png");
        fileTypes.put("jpg" ,"image/jpeg");
        fileTypes.put("jpeg","image/jpeg");
        fileTypes.put("webp","image/webp");
        fileTypes.put("bz","application/x-bzip");
        fileTypes.put("bz2","application/x-bzip2");
        fileTypes.put("gz","application/gzip");
        fileTypes.put("json","application/json");
        fileTypes.put("tar","application/x-tar");
        fileTypes.put("zip","application/zip");
        fileTypes.put("rar","application/vnd.rar");
        fileTypes.put("sh","application/x-sh");
        fileTypes.put("rtf","application/rtf");
        fileTypes.put("ttf","font/ttf");
        fileTypes.put("gif","image/gif");
        fileTypes.put("mp3","audio/mp3");
        fileTypes.put("opus","audio/ogg");
        fileTypes.put("ogg","audio/ogg");

        return fileTypes;
    }

    public static TypedInputStream getFile(String fname){
        TypedFileName type = fileTable.get(fname);
        if(type==null)
            return null;
        try{
            return new TypedInputStream(new FileInputStream(type.getName()),type.getFILE_TYPE());
        }catch(FileNotFoundException e){
            return null;
        }

    }

}

class TypedFileName{

    private final String FILE_NAME;
    private final String FILE_TYPE;

    public TypedFileName(String fname, String type){
        this.FILE_TYPE =type;
        this.FILE_NAME =fname;
    }

    public String getName(){
        return FILE_NAME;
    }

    public String getFILE_TYPE(){
        return FILE_TYPE;
    }

}
