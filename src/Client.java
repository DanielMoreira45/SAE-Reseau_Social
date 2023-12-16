import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

class Client extends Thread {
  private String pseudo;
  private Socket socket;
  private boolean connecte = false;

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

  public void setConnecte(boolean connecte){
    this.connecte = connecte;
  }

  @Override
  public String toString() {
    return pseudo;
  }

  public void menu(){
    System.out.println("1. Envoyer un message");
    System.out.println("2. Utiliser une commande");
    System.out.println("0. Quitter");
    System.out.println("Choisissez une option : ");
  }

  public void optionMessage(Scanner scanner){
    System.out.println("Ecrivez quelque chose : ");
    String message = scanner.nextLine();
    Message msg = new Message(message, this);
    this.envoiMessage(msg, this.socket);
    try{
      ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
      Message receivedMessage = (Message) objectInputStream.readObject();
      System.out.println("Received message from server: " + receivedMessage);
    }catch(IOException | ClassNotFoundException e){
      e.printStackTrace();
    }
  }

  public void optionCommandes(Scanner scanner){
    System.out.println("Voici la liste des commandes disponibles :");
    System.out.println("--> \\list");
    System.out.println("--> \\follow");
    System.out.println("--> \\exit \n");
    System.out.println("Quelle commande souhaitez-vous utiliser ? ");
    String message = scanner.nextLine();
    Message msg = new Message(message, this);
    this.envoiMessage(msg, socket);

    if (message.equals("/list")) {
      try{
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Message receivedMessage = (Message) objectInputStream.readObject();
        System.out.println("Received message from server: " + receivedMessage);
      }catch(IOException | ClassNotFoundException e){
        e.printStackTrace();
      }
    }
  }

  @Override
  public void run() {
    int rep = -1;
    Scanner scannerMessage = new Scanner(System.in);
    while (rep != 0){
      menu();
      String userInput = scannerMessage.nextLine();
      rep = Integer.parseInt(userInput);

      switch(rep){
        case 1: {
          optionMessage(scannerMessage);
          break;
        }
        case 2: { //Ne marche pas pour le moment
          optionCommandes(scannerMessage);
          break;
        }
      }
      System.out.println("\n Appuyez sur Entrée pour continuer");
      userInput = scannerMessage.nextLine();
    }
  }

  public void envoiMessage(Message msg, Socket socket) {
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
      client.setConnecte(true);
      client.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
