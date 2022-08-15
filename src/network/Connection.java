package network;

import java.io.IOException;
import java.net.Socket;

public class Connection {

    Socket socket;
    volatile boolean processingState;
    private TimeOutThread timout;
    public Connection(Socket socket){
        this.socket=socket;
        timout=new TimeOutThread(socket);
        timout.start();
    }

    public boolean isBeingProcessed(){
        return processingState;
    }

    public void setProcessingState(boolean state){
        this.processingState=state;
    }

    public Socket getSocket(){
        return socket;
    }

    public void close()throws IOException{
        socket.close();
    }

    public void updateTimeOut(){
        timout.interrupt();
    }

    public void pauseTimeOut(){
        timout.setActive(false);
        timout.interrupt();
    }

    public void startTimeOut(){
        timout.interrupt();
        timout.setActive(true);
    }


}

class TimeOutThread extends Thread{

    private Socket socket;
    private volatile boolean active;
    public TimeOutThread(Socket socket){
        this.socket=socket;
    }

    public void setActive(boolean active){
        this.active=active;
    }

    public void run(){
        while(true) {
            try {
                Thread.sleep(10000);
                if(active){
                    socket.close();
                    break;
                }
            } catch (InterruptedException e) {
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

}
