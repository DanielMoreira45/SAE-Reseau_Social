import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

class Serveur {
    private String filePath;
    private ServerSocket serverSock;
    private HashMap<String, HashMap<String, HashSet<Object>>> donnees;

    public Serveur(String filePath) {
        try {
            this.serverSock = new ServerSocket(4444);
            this.donnees = new HashMap<>();
            this.filePath = filePath;
            System.out.println("Serveur initialisé");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'initialisation du serveur");
        }
    }

    public void extractionJson(String filePath) {
        try {
            JSONTokener tokener = new JSONTokener(new FileReader(filePath));
            JSONObject donneesJson = new JSONObject(tokener);
            JSONObject donneesObjet = donneesJson.getJSONObject("donnees");
            for (String utilisateur : donneesObjet.keySet()) {
                JSONObject utilisateurData = donneesObjet.getJSONObject(utilisateur);
                HashMap<String, HashSet<Object>> utilisateurMap = new HashMap<>();

                // Traitement des messages
                if (utilisateurData.has("message")) {
                    JSONArray messagesArray = utilisateurData.getJSONArray("message");
                    HashSet<Object> messages = new HashSet<>();
                    for (Object messageObj : messagesArray) {
                        JSONObject messageData = (JSONObject) messageObj;
                        messages.add(messageData.toMap());
                    }
                    utilisateurMap.put("message", messages);
                }
                // Traitement des abonnements
                if (utilisateurData.has("abo")) {
                    JSONArray abonnementsArray = utilisateurData.getJSONArray("abo");
                    HashSet<Object> abonnements = new HashSet<>();
                    for (Object abonnement : abonnementsArray) {
                        abonnements.add(abonnement);
                    }
                    utilisateurMap.put("abo", abonnements);
                }

                donnees.put(utilisateur, utilisateurMap);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        this.extractionJson(filePath);
        FileWriter fileWriter = new FileWriter(filePath, false);
        fileWriter.write(message.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
        Serveur serveur = new Serveur("./src/messages.json");
        serveur.start();
    }
}
