import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;

class ClientHandler implements Runnable {
    private Socket socketClient;
    private Serveur serveur;

    public ClientHandler(Serveur serv, Socket socketClient) {
        this.serveur = serv;
        this.socketClient = socketClient;
    }

    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socketClient.getInputStream())) {
            Message receivedMessage = (Message) objectInputStream.readObject();
            System.out.println(receivedMessage);
            ObjectOutputStream output = new ObjectOutputStream(socketClient.getOutputStream());
            Message out = new Message("Message reçu par le serveur", "Serveur");
            output.writeObject(out);
            output.flush();

            String user = receivedMessage.getExpediteur();
            if (!serveur.getDonnees().containsKey(user)) {
                serveur.getDonnees().put(user, new HashSet<>());
            }
            serveur.getDonnees().get(user).add(receivedMessage);
            if (receivedMessage.getContenu().contains("/")) {
                switch (receivedMessage.getContenu().split("/")[1]) {
                    case "list":
                        String liste = "";
                        for (String key : serveur.getDonnees().keySet()) {
                            liste += key + "\n";
                        }
                        System.out.println("liste " +liste);
                        Message message = new Message("aled", "Serveur");
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
                        objectOutputStream.writeObject(message);
                        objectOutputStream.flush();
                        break;

                    case "exit":
                        socketClient.close();
                        serveur.close();
                        break;
                    default:
                        break;
                }

            }
            
            objectInputStream.close();
        } catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
        }
    }
}
