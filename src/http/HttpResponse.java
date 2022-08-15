package http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    int code;
    String status;
    final String HTTP_VERSION="HTTP/1.1";
    private TypedInputStream fileInput=null;

    private byte[] buffer = new byte[0x10000];

    private Map<String,String> headers = new HashMap<String,String>();

    public HttpResponse(int code){
        this.code=code;
    }

    public void setStatus(String status){
        this.status=status;
    }

    public void setFileInput(TypedInputStream input){
        this.fileInput=input;
    }

    public byte[] getSerializedHeaders(){
        headers.put("Content-length",String.valueOf(getFileLength()));
        if(fileInput!=null)
            headers.put("Content-type",fileInput.getType());
        if(fileInput.getType().toLowerCase().contains("image"))
            headers.put("Cache-Control","public, max-age=3600");
        StringBuilder sb = new StringBuilder(HTTP_VERSION+" "+String.valueOf(code)+" "+status+"\n");
        for(String s : headers.keySet())
            sb.append(s+": "+headers.get(s)+"\n");
        sb.append("\n");
        return sb.toString().getBytes();
    }

    private int getFileLength()  {
        if(this.fileInput==null)
            return 0;
        try{
            return fileInput.getInputStream().available();
        }catch(IOException e){
            e.printStackTrace();
            this.fileInput=null;
            return 0;
        }
    }

    public boolean hasFile(){
        return fileInput!=null;
    }

    public void addHeader(String key,String value){
        headers.put(key,value);
    }

    public void send(OutputStream out) throws IOException {
        try{
            out.write(getSerializedHeaders());
        }catch(IOException e){
            e.printStackTrace();
        }

        int length=0;
        if(fileInput==null){
            out.flush();
            return;
        }
        while((length=fileInput.getInputStream().read(buffer))!=-1)
            out.write(buffer,0,length);
        out.flush();
        fileInput.getInputStream().close();
    }

}
