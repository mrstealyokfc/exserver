package http;

import java.io.IOException;
import java.io.InputStream;

public class Preprocessor {

    private boolean valid=true;
    private Process proc;

    public Preprocessor(String scriptLocation){
        ProcessBuilder processBuilder = new ProcessBuilder("python", scriptLocation);
        processBuilder.redirectErrorStream(true);
        try{
            proc=processBuilder.start();
        }catch(IOException e){
            e.printStackTrace();
            valid=false;
        }
    }

    public InputStream getInputStream(){
        return proc.getInputStream();
    }

    public void close(){

    }


}
