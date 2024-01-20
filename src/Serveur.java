import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    public HashMap<String, HashSet<Message>> messagesAbonnement(String utilisateur) {
        HashMap<String, HashSet<Message>> messages = new HashMap<>();
        HashSet<Object> abonnements = getAbo(utilisateur);

        for (Object abonnement : abonnements) {
            if (abonnement instanceof String) {
                String utilisateurAbonne = (String) abonnement;
                HashMap<String, HashSet<Object>> infosAbonne = this.donnees.get(utilisateurAbonne);

                if (infosAbonne.containsKey("message")) {
                    HashSet<Object> ensembleMessages = infosAbonne.get("message");
                    HashSet<Message> messagesAbo = new HashSet<>();

                    for (Object messageObj : ensembleMessages) {
                        if (messageObj instanceof HashMap) {
                            HashMap<String, Object> messMap = (HashMap<String, Object>) messageObj;
                            Message mes = new Message(Integer.valueOf((String) messMap.get("id")), (String) messMap.get("contenu"), Integer.valueOf((String) messMap.get("nbLikes")), utilisateurAbonne);
                            // lié avec le client concerné ?
                            messagesAbo.add(mes);
                        }
                    }
                    messages.put(utilisateurAbonne, messagesAbo);
                }
            }
        }
        return messages;
    }

    public void likeMessage(String commandeUser){        
        String[] message = commandeUser.split("_", 2);
        String pseudoUser = message[0];
        String messageLike = message[1];
        HashMap<String, HashSet<Message>> messagesDesAbonnements = this.messagesAbonnement(pseudoUser);
        
        System.out.println(messagesDesAbonnements);
        
        for (String abonnement : messagesDesAbonnements.keySet()){
            System.out.println(abonnement);
            HashSet<Message> infos = messagesDesAbonnements.get(abonnement);

            for (Message msg : infos){
                if (msg.getContenu().contains(messageLike)){
                    int likes = msg.getNbLikes();
                    msg.setNbLikes(likes + 1);
                }
            }
        }
    }

    public void supprimerMessage(String commandeUser){
        String[] message = commandeUser.split("_", 2);
        String pseudoUser = message[0];
        String messageDel = message[1];
        HashSet<Object> messagesObj = this.donnees.get(pseudoUser).get("message");
        HashSet<Message> lesMessages = new HashSet<>();

        for (Object messageObj : messagesObj) {
            if (messageObj instanceof HashMap) {
                HashMap<String, Object> messMap = (HashMap<String, Object>) messageObj;
                Message mes = new Message(Integer.valueOf((String) messMap.get("id")), (String) messMap.get("contenu"), Integer.valueOf((String) messMap.get("nbLikes")), pseudoUser);
                if (!mes.getContenu().contains("_")){
                    lesMessages.add(mes);
                }
            }
        }

        for (Message mess : lesMessages){
            if (mess.getContenu().contains(messageDel)){
                lesMessages.remove(mess);
                System.out.println(this.donnees.get(pseudoUser));
                this.donnees.get(pseudoUser).get("message").clear();
                this.donnees.get(pseudoUser).get("message").add("");
                System.out.println(this.donnees.get(pseudoUser));
            }
        }
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
        fileWriter.write(message.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
        Serveur serveur = new Serveur("./src/messages.json");
        serveur.start();
    }
}
   