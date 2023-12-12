import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Client {
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

      PrintWriter writer = new PrintWriter(socket.getOutputStream());
      Message msg = new Message(scannerMessage.next(), client);
      writer.println(msg);
      writer.flush();

      FileWriter fileWriter = new FileWriter(new File("./src/messages.json"));
      String user = msg.getExpediteur();
      Date date = msg.getDate();
      String contenu = msg.getContenu();
      String jsonContent = "{ \n \"Utilisateur\" : " + "\"" + user + "\"," + "\n \"Date\" : " + "\"" + date + "\"," + "\n \"Contenu\" : " + "\"" + contenu + "\" \n}";
      fileWriter.write(jsonContent);
      fileWriter.flush();

      scannerClient.close();
      scannerMessage.close();
      socket.close();
      fileWriter.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
