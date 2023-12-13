import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Client implements Serializable{
  private String pseudo;
  private List<Client> abonnements;
  private List<Client> abonnes;

  public Client(String pseudo) {
    this.pseudo = pseudo;
    abonnements = new ArrayList<>();
    abonnes = new ArrayList<>();
  }

  public void sAbonner(Client user) {
    user.abonnes.add(this);
    abonnements.add(user);
  }

  public int nbAbonnes(){return abonnes.size();}
  public int nbAbonnements(){return abonnements.size();}
  public String getPseudo(){return pseudo;}

  @Override
  public String toString(){
    return pseudo;
  }


  public static void main(String[] args){
    try{
      Scanner scannerClient = new Scanner(System.in);
      System.out.println("Création de votre compte : \n Entrez un pseudo : ");
      String pseudo = scannerClient.nextLine();
      Client client = new Client(pseudo);

      Socket socket = new Socket("127.0.0.1", 4444);
      Scanner scannerMessage = new Scanner(System.in);
      System.out.println("Ecrivez quelque chose : ");

      // PrintWriter writer = new PrintWriter(socket.getOutputStream());
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
      Message msg = new Message(scannerMessage.next(), client);
      objectOutputStream.writeObject(msg);
      objectOutputStream.flush();

      

      scannerClient.close();
      scannerMessage.close();
      socket.close();
      
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
