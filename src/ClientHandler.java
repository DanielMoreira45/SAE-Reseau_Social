import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;

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
                if (!serveur.getDonnees().containsKey(user)) {
                    serveur.getDonnees().put(user, new HashSet<>());
                }
                serveur.getDonnees().get(user).add(receivedMessage);
                if (receivedMessage.getContenu().contains("/")) {
                    switch (receivedMessage.getContenu().split("/")[1]) {
                        case "list":
                            Message message = new Message("aled", "Serveur");
                            this.objectOutputStream.writeObject(message);
                            this.objectOutputStream.flush();
                            this.objectOutputStream.reset();
                            break;

                        case "exit":
                            this.clientQuitte = true;
                            socketClient.close();
                            break;

                        default:
                            break;
                    }
                }else{
                    Message out = new Message("Message reçu par le serveur", "Serveur");
                    this.objectOutputStream.writeObject(out);
                    this.objectOutputStream.flush();
                    this.objectOutputStream.reset();
                }
            }
        }catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
        }
        finally {
            try {
                socketClient.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
