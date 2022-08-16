import config.Config;
import network.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        loadHttp404Page();
        Config.reloadFileTable();
        Server server = new Server(3000);
        server.start();
        Scanner scanner = new Scanner(System.in);
        String line;
        while((line=scanner.nextLine())!=null)
            if(line.equals("stop"))
                server.close();

    }

    private static void loadHttp404Page(){
        try {
            FileInputStream input=new FileInputStream("404.html");
            Config.http404page=input.readAllBytes();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

