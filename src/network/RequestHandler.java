package network;

import config.Config;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponseFactory;
import http.TypedInputStream;

import javax.annotation.processing.FilerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RequestHandler implements Runnable{
    private volatile Connection connection;
    public RequestHandler(Connection connection){
        this.connection=connection;
        connection.setProcessingState(true);
    }

    @Override
    public void run() {
        connection.setProcessingState(true);
        connection.pauseTimeOut();
        System.out.println("handling request from: "+connection.getSocket().getInetAddress().getHostAddress());

        while(true) {
            if(mainLoop())
                break;
        }

        System.out.println("Finished Handling request");
        connection.startTimeOut();
        connection.setProcessingState(false);
    }

    private boolean mainLoop(){
        try{
            processNextRequest();
            Thread.sleep(10);
            if(connection.getSocket().getInputStream().available()>0)
                return false;
        }catch(IOException e){
            closeSocket();
            e.printStackTrace();
        } catch (InterruptedException e) {
            closeSocket();
            e.printStackTrace();
        }
        return true;
    }

    private void processNextRequest()throws IOException{
        Config.reloadFileTable();
        HttpRequest request = new HttpRequest(getRequest());
        if(request.needsAdditionalData())
            request.setAdditionalData(connection.getSocket().getInputStream());
        sendResponse(request);
    }

    private void sendResponse(HttpRequest request) throws IOException {
        HttpResponse response=null;
        if(!request.isValid()){
            response = HttpResponseFactory.create400Response();
            response.send(connection.getSocket().getOutputStream());
            return;
        }
        if(request.getMethod().equalsIgnoreCase("GET"))
            response = getResponse(request);
        if(response==null)
            response = HttpResponseFactory.create501Response();
        response.send(connection.getSocket().getOutputStream());
    }

    private String[] getRequest(){
        try{
            return readRequest();
        }catch(IOException e){
            e.printStackTrace();
            closeSocket();
            throw new RuntimeException(e);
        }
    }

    String[] readRequest() throws IOException {
        Scanner scanner = new Scanner(connection.getSocket().getInputStream());
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while((line= scanner.nextLine())!=null){
            line = line.trim().replace("\r","").toLowerCase();
            if(line.hashCode()==0)
                break;
            lines.add(line);
        }
        return lines.toArray(new String[0]);
    }

    private void closeSocket(){
        try{
            connection.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpResponse putResponse(){
        return HttpResponseFactory.create401Response();
    }

    private HttpResponse getResponse(HttpRequest request){
        try{
            return findFile(request.getLocator());
        }catch(IOException e){
            if(e instanceof FileNotFoundException)
                return HttpResponseFactory.create404Response();
            return HttpResponseFactory.create500Response();
        }

    }

    private HttpResponse findFile(String fname) throws IOException{
        TypedInputStream file = Config.getFile(fname);
        if(file==null)
            throw new FileNotFoundException("cannot find: "+fname);
        HttpResponse response = HttpResponseFactory.create200Response();
        response.setFileInput(file);
        return response;
    }

}