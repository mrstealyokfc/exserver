import config.Config;
import network.Server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileLockInterruptionException;

public class Main {

    public static void main(String[] args) throws IOException {
        loadHttp404Page();
        Config.reloadFileTable();
        new Server(3000).start();
    }

    private static void loadHttp404Page(){
        try {
            FileInputStream input=new FileInputStream("404.html");
            Config.http404page=input.readAllBytes();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

