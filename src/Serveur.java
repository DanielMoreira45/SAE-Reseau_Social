import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.json.JSONObject;

class Serveur {
  public static void main(String[] args) throws IOException {
    Serveur serveur = new Serveur();
    serveur.start();
  }

  public void init() {

    System.out.println("Serveur initialisé");
  }

  public void close() {

    System.out.println("Serveur fermé");
  }

  public void start() throws IOException {
    ServerSocket serverSock = new ServerSocket(4444);
    System.out.println("Serveur démarré");
    while (true) {
      Socket clientSocket = serverSock.accept();
      Thread t = new Thread(new ClientHandler(this, clientSocket));
      System.out.println("Client connecté");
      t.start();
      
    }
    
  }

  public void enregistrerMessage(Message msg) throws IOException {
    FileWriter fileWriter = new FileWriter(new File("./src/messages.json"));
    String user = msg.getExpediteur();
    Date date = msg.getDate();
    String contenu = msg.getContenu();
    JSONObject message = new JSONObject();
    message.put("Utilisateur", user);
    message.put("Date", date);
    message.put("Contenu", contenu);
    
    fileWriter.write(message.toString());
    fileWriter.flush();
    fileWriter.close();
  }
}
