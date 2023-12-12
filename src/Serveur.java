import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class Serveur {
  public static void main(String[] args) throws IOException{
    ServerSocket serverSock = new ServerSocket(4444);
    while (true){
      Socket clientSocket = serverSock.accept();
      Thread t = new Thread(new ClientHandler(clientSocket));
      System.out.println("Client connecté");
      t.start();

      InputStreamReader stream = new InputStreamReader(clientSocket.getInputStream());
      BufferedReader reader = new BufferedReader(stream);
      String message = reader.readLine();
      System.out.println(message);

      clientSocket.close();
      serverSock.close();
    }
  }
}
