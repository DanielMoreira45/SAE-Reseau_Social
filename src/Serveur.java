import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;

class Serveur {
  private FileWriter fileWriter;
  private ServerSocket serverSock;
  private HashMap<String, Set<Message>> donnees;

  public Serveur(String filePath){
    try{
      this.serverSock = new ServerSocket(4444);
      this.donnees = new HashMap<>();
      this.fileWriter = new FileWriter(filePath);
      System.out.println("Serveur initialisé");
    }catch (IOException e){
      e.printStackTrace();
      System.out.println("Erreur lors de l'initialisation du serveur");
    }
  }

  public HashMap<String, Set<Message>> getDonnees() {
    return donnees;
  }

  public static void main(String[] args) throws IOException {
    Serveur serveur = new Serveur("./src/messages.json");
    serveur.start();
  }

  public void close() throws IOException {
    this.serverSock.close();
    System.out.println("Serveur fermé");
  }

  public void start() throws IOException {
    System.out.println("Serveur démarré");
    while (true) {
      Socket clientSocket = this.serverSock.accept();
      Thread t = new Thread(new ClientHandler(this, clientSocket));
      System.out.println("Client connecté");
      t.start();
    }
  }

  /**
   * Enregistre toutes les informations dans le fichier JSON
   * @throws IOException
   */
  public void enregistrement() throws IOException {
    // TODO
    JSONObject message = new JSONObject();
    // message.put("Utilisateur", user);
    // message.put("Date", date);
    // message.put("Contenu", contenu);

    fileWriter.write(message.toString());
    fileWriter.flush();
    fileWriter.close();
  }
}
