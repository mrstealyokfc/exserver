package http;

import config.Config;

import java.io.ByteArrayInputStream;
import java.util.Date;

public class HttpResponseFactory {


    public static HttpResponse create200Response(){
        HttpResponse response = new HttpResponse(200);
        response.setStatus("I AM HAPPY");
        response.addHeader("date",new Date().toString());
        response.addHeader("Server","Ex-veilnaught server 2");
        return response;
    }
    public static HttpResponse create400Response(){
        HttpResponse response = new HttpResponse(400);
        response.setStatus("I DONT UNDERSTAND");
        response.addHeader("date",new Date().toString());
        response.addHeader("Server","Ex-veilnaught server 2");
        return response;
    }
    public static HttpResponse create404Response(){
        HttpResponse response = new HttpResponse(404);
        response.setStatus("I AM LOST");
        response.addHeader("date",new Date().toString());
        response.addHeader("Server","Ex-veilnaught server 2");
        if(Config.http404page!=null)
            response.setFileInput(new TypedInputStream(new ByteArrayInputStream(Config.http404page),"text/html"));
        return response;
    }
    public static HttpResponse create500Response(){
        HttpResponse response = new HttpResponse(500);
        response.setStatus("HELP ME");
        response.addHeader("date",new Date().toString());
        response.addHeader("Server","Ex-veilnaught server 2");
        return response;
    }
    public static HttpResponse create501Response(){
        HttpResponse response = new HttpResponse(501);
        response.setStatus("I CANT DO THAT");
        response.addHeader("date",new Date().toString());
        response.addHeader("Server","Ex-veilnaught server 2");
        return response;
    }

}
