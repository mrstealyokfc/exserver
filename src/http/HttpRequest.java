package http;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private boolean valid;
    private String method;
    private String locator;
    private Map<String,String> headers = new HashMap<>();
    private InputStream additionalData;

    public HttpRequest(String[] rawRequestHeader){
        try{
            processHeaderData(rawRequestHeader);
            valid=true;
        }catch(Exception e){
            e.printStackTrace();
            valid=false;
        }
    }

    private void processHeaderData(String[] headerData)throws Exception{
        String[] headline = headerData[0].split(" ");
        this.method=headline[0].toLowerCase().trim();
        this.locator=headline[1].toLowerCase().trim();
        for(int i=1;i<headerData.length;i++){
            String[] splitLine = headerData[i].trim().toLowerCase().split(": ");
            headers.put(splitLine[0].trim(),splitLine[1].trim());
        }
    }

    public int hasAdditionalData(){
        if(headers.get("content-length")==null)
            return 0;
        try{
            return Integer.parseInt(headers.get("content-length").trim());
        }catch(NumberFormatException e){
            e.printStackTrace();
            return 0;
        }
    }

    public boolean needsAdditionalData(){
        return hasAdditionalData() > 0;
    }
    public void setAdditionalData(InputStream additionalData){
        this.additionalData=additionalData;
    }
    public void setMethod(String method){
        this.method=method;
    }
    public void setLocator(String locator){
        this.locator=locator;
    }
    public boolean isValid(){
        return valid;
    }
    public void addHeader(String key, String value){
        this.headers.put(key,value);
    }
    public String getHeader(String key){
        return headers.get(key);
    }
    public String[] getHeaderKeySet(){
        return headers.keySet().toArray(new String[0]);
    }
    public String getLocator(){
        return locator;
    }
    public String getMethod(){
        return method;
    }
    public InputStream getAdditionalData(){
        return additionalData;
    }
}