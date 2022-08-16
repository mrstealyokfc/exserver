package http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private boolean valid;
    private String method;
    private String locator;
    private final Map<String,String> headers = new HashMap<>();

    public HttpRequest(String[] rawRequestHeader){
        try{
            processHeaderData(rawRequestHeader);
            valid=true;
        }catch(Exception e){
            e.printStackTrace();
            valid=false;
        }
    }

    private void processHeaderData(String[] headerData)throws ArrayIndexOutOfBoundsException,NullPointerException{
        String[] headline = headerData[0].split(" ");
        this.method=headline[0].toLowerCase().trim();
        this.locator=headline[1].toLowerCase().trim();
        for(int i=1;i<headerData.length;i++){
            String[] splitLine = headerData[i].trim().toLowerCase().split(": ");
            headers.put(splitLine[0].trim(),splitLine[1].trim());
        }
    }

    public String getHeader(String key){
        return headers.get(key);
    }

    public boolean isValid(){
        return valid;
    }
    public String getLocator(){
        return locator;
    }
    public String getMethod(){
        return method;
    }
}