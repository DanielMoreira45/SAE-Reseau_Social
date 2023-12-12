import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    public void run(){
        
    }
}
