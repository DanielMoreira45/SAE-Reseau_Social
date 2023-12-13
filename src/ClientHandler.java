import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;
    private Serveur serveur;

    public ClientHandler(Serveur serv,Socket socket){
        this.serveur = serv;
        this.socket = socket;
    }

    public void run(){

        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            // Read the Message object from the ObjectInputStream
            Message receivedMessage = (Message) objectInputStream.readObject();
            System.out.println(receivedMessage);
            serveur.enregistrerMessage(receivedMessage);
            objectInputStream.close();
        } catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
        }

        // try (InputStreamReader stream = new InputStreamReader(socket.getInputStream())) {
        //     BufferedReader reader = new BufferedReader(stream);
        //       String message = reader.readLine();
              
        //       System.out.println(message);
        //       socket.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
