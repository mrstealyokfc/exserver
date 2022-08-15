package http;

import java.io.InputStream;

public class TypedInputStream {

    private String type;
    private InputStream input;

    public TypedInputStream(InputStream input,String type){
        this.type=type;
        this.input=input;
    }

    public String getType(){
        return type;
    }

    public InputStream getInputStream(){
        return input;
    }

}
