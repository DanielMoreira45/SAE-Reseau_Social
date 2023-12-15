import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;

class Client extends Thread implements Serializable {
  private String pseudo;
  private Socket socket;

  public Client(String pseudo) {
    this.pseudo = pseudo;
    try {
      this.socket = new Socket("127.0.0.1", 4444);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sAbonner(Client user) {
    return; // TODO
  }

  public int nbAbonnes() {
    return 0; // TODO
  }

  public int nbAbonnements() {
    return 0; // TODO
  }

  public String getPseudo() {
    return pseudo;
  }

  @Override
  public String toString() {
    return pseudo;
  }

  @Override
  public void run() {
    try {
      Scanner scannerMessage = new Scanner(System.in);
      System.out.println("Ecrivez quelque chose : ");
      String userInput = scannerMessage.next();
      Message msg = new Message(userInput, this);
      this.envoieMessage(msg, this.socket);

      if (userInput.equals("/list")) {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Message receivedMessage = (Message) objectInputStream.readObject();
        System.out.println("Received message from server: " + receivedMessage);
      }

      scannerMessage.close();
      socket.close();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void envoieMessage(Message msg, Socket socket) {
    try {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
      objectOutputStream.writeObject(msg);
      objectOutputStream.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    try {
      Scanner scannerClient = new Scanner(System.in);
      System.out.println("Création de votre compte : \n Entrez un pseudo : ");
      Client client = new Client(scannerClient.nextLine());
      System.out.println("Bienvenue " + client.getPseudo());
      client.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
