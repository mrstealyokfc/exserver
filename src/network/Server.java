package network;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
public class Server extends Thread{
    private volatile List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private NewConnectionListenerThread newConnectionListenerThread;
    private ServerSocket ss;
    public Server(int port) throws IOException {
        ss=new ServerSocket(port,10);
        newConnectionListenerThread =  new NewConnectionListenerThread(ss,connections);
    }
    public void run(){
        newConnectionListenerThread.start();
        while(!ss.isClosed()){
            for(int i=0; i<connections.size();i++) {
                Connection s=null;
                try {
                    s = connections.get(i);
                    if(s.getSocket().isConnected())
                        if (s.getSocket().getInputStream().available()>0&&!s.isBeingProcessed()) {
                            System.out.println("submitting thread");
                            s.setProcessingState(true);
                            threadPool.submit(new RequestHandler(s));
                        }
                    if(!s.getSocket().isConnected()) {
                        System.out.println("Removing connection");
                        connections.remove(s);
                    }
                } catch (IOException e) {
                    System.out.println("Removing Connection");
                    connections.remove(s);
                } catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void close(){
        try{
            ss.close();
        }catch(IOException e){
            System.err.println("FAILED TO CLOSE SERVER");
            e.printStackTrace();
        }
        for(Connection s : connections){
            try {
                s.close();
            } catch (IOException e) {
                System.err.println("FAILED TO CLOSE CONNECTION TO: "+s.getSocket().getInetAddress().getHostAddress());
                e.printStackTrace();
            }
        }
    }
}
class NewConnectionListenerThread extends Thread{
    private ServerSocket server;
    private volatile List<Connection> destination;
    public NewConnectionListenerThread(ServerSocket server, List<Connection> destination){
        this.server=server;
        this.destination=destination;
    }
    @Override
    public void run(){
        while(!server.isClosed()){
            try{
                Socket newConnection = server.accept();
                destination.add(new Connection(newConnection));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}