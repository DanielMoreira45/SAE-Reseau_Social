import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

class Serveur {
    private FileWriter fileWriter;
    private ServerSocket serverSock;
    private HashMap<String, HashMap<String, HashSet<Object>>> donnees;

    public Serveur(String filePath) {
        try {
            this.serverSock = new ServerSocket(4444);
            this.donnees = new HashMap<>();
            this.fileWriter = new FileWriter(filePath);
            System.out.println("Serveur initialisé");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'initialisation du serveur");
        }
    }

    public Set<String> getPersonne() {
        return this.donnees.keySet();
    }

    public HashSet<Object> getMessages(String user) {
        return this.donnees.get(user).get("message");
    }

    public HashSet<Object> getAbo(String user) {
        return this.donnees.get(user).get("abo");
    }

    public void addPersonne(String user) {
        HashSet<Object> setMessage = new HashSet<>();
        HashSet<Object> setabo = new HashSet<>();
        HashMap<String, HashSet<Object>> mapInfo = new HashMap<>();
        mapInfo.put("abo", setabo);
        mapInfo.put("message", setMessage);
        this.donnees.put(user, mapInfo);
    }

    public boolean addAbo(String user, String nomAbo) {
        if (this.donnees.containsKey(nomAbo)) {
            this.getAbo(user).add(nomAbo);
            return true;
        }
        return false;
    }

    public boolean removeAbo(String user, String nomAbo) {
        if (this.donnees.containsKey(nomAbo)) {
            this.getAbo(user).remove(nomAbo);
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        Serveur serveur = new Serveur("./src/messages.json");
        serveur.start();
    }

    public void close() throws IOException {
        this.serverSock.close();
        this.enregistrement();
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
     * 
     * @throws IOException
     */
    public void enregistrement() throws IOException {
        JSONObject message = new JSONObject();
        message.put("donnees", this.donnees);
        fileWriter.write(message.toString());
        fileWriter.flush();
        fileWriter.close();
    }
}
