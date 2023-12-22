import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;

class ClientHandler implements Runnable {
    private Socket socketClient;
    private Serveur serveur;
    private boolean clientQuitte;

    public ClientHandler(Serveur serv, Socket socketClient) {
        this.serveur = serv;
        this.socketClient = socketClient;
        this.clientQuitte = false;
    }

    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socketClient.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
            while(!clientQuitte){
                Message receivedMessage = (Message) objectInputStream.readObject();
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
                            objectOutputStream.writeObject(message);
                            objectOutputStream.flush();
                            objectOutputStream.reset();
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
                    objectOutputStream.writeObject(out);
                    objectOutputStream.flush();
                    objectOutputStream.reset();
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
