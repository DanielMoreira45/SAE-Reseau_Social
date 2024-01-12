import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;

class ClientHandler implements Runnable {
    private Socket socketClient;
    private Serveur serveur;
    private boolean clientQuitte;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ClientHandler(Serveur serv, Socket socketClient) {
        this.serveur = serv;
        this.socketClient = socketClient;
        this.clientQuitte = false;
        try {
            this.objectInputStream = new ObjectInputStream(this.socketClient.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(this.socketClient.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while(!clientQuitte){
                Message receivedMessage = (Message) this.objectInputStream.readObject();
                System.out.println(receivedMessage);
                String user = receivedMessage.getExpediteur();
                if (!serveur.getPersonne().contains(user)) {
                    serveur.addPersonne(user);
                }
                serveur.getMessages(user).add(receivedMessage.getJson());

                if(receivedMessage.getContenu().contains("-")){
                    String[] message = receivedMessage.getContenu().split("-", 2);
                    String commande = message[0];
                    String pseudo = message[1];
                    switch (commande) {
                        case "follow":
                            this.serveur.addAbo(user, pseudo);
                            break;

                        case "unfollow":
                            this.serveur.removeAbo(user, pseudo);
                            break;

                        case "listefollow":
                            Message out = new Message(this.serveur.getAbo(user).toString(), "Serveur");
                            this.objectOutputStream.writeObject(out);
                            this.objectOutputStream.flush();
                            this.objectOutputStream.reset();
                            break;
                        
                        case "liste_clients":
                            Message output = new Message(this.serveur.getPersonne().toString(), "Serveur");
                            this.objectOutputStream.writeObject(output);
                            this.objectOutputStream.flush();
                            this.objectOutputStream.reset();
                            break;
                        
                        case "messages_abonnements":
                            Message outputt = new Message(this.serveur.messagesAbonnement(user).toString(), "Serveur");
                            this.objectOutputStream.writeObject(outputt);
                            this.objectOutputStream.flush();
                            this.objectOutputStream.reset();
                            break;

                        case "exit":
                            this.clientQuitte = true;
                            this.socketClient.close();
                            break;

                        case "exitall":
                            this.clientQuitte = true;
                            this.serveur.close();
                            break;

                        default:
                            break;
                    }
                }
                else{
                    Message out = new Message("Message reçu par le serveur", "Serveur");
                    this.objectOutputStream.writeObject(out);
                    this.objectOutputStream.flush();
                    this.objectOutputStream.reset();
                }
            }
        }catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
        }
    }
}
