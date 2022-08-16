package http;

import java.io.InputStream;

public class TypedInputStream {

    private final String type;
    private final InputStream input;

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
