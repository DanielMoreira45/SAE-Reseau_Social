import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Client extends Thread {
	private String pseudo;
	private Socket socket;
	private Scanner scannerClient;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	private HashSet<Message> lesMessages;

	public Client(String pseudo, Scanner scannerClient) {
		this.pseudo = pseudo;
		this.scannerClient = scannerClient;
		lesMessages = new HashSet<>();
		try {
			this.socket = new Socket("127.0.0.1", 4444);
			this.objectInputStream = null;
			this.objectOutputStream = null;
			System.out.println("Client connecté");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashSet<Message> getMessages(){
		return this.lesMessages;
	}

	public void sAbonner(String user) {
		String message = "follow";
		Message msg = new Message(message + "-" + user, this);
		this.envoiMessage(msg, socket);
	}

	public void seDesabonner(String user){
		String message = "unfollow";
		Message msg = new Message(message + "-" + user, this);
		this.envoiMessage(msg, socket);
	}

	public void listeAbonnements(String user) {
		String message = "liste_follow";
		Message msg = new Message(message + "-" + user, this);
		this.envoiMessage(msg, socket);
		Message receivedMessage = this.recevoirMessage();
		System.out.println(receivedMessage.getContenu());
	}

	public void ajouterMessage(){
		Message msg = new Message("add_message-"+this.pseudo, this);
		this.envoiMessage(msg, socket);
		Message receivedMessage = this.recevoirMessage();
		lesMessages.add(receivedMessage);
	}

	public void removeMessage(String contenu){
		String commande = "delete-" + this.pseudo + "_";
		Message message = new Message(commande, this);
		for (Message msg : lesMessages){
			if (contenu.equals(msg.getContenu())){
				message.setContenu(commande + msg.getContenu());
			}
		}
		this.envoiMessage(message, socket);
		Message receivedMessage = this.recevoirMessage();
		lesMessages.remove(receivedMessage);
	}

	public String listeMessages(String user){
		String res = "Liste des posts : " + "\n";
		int cpt = 0;
		for (Message msg : lesMessages){
			if (!msg.getContenu().contains("-")){
				res += "--> " + msg.getContenu() + " | Nombre de likes : " + msg.getNbLikes() + "\n";
				cpt += 1;
			}
		}
		res += "\n" + "Nombre de posts : " + cpt;
		return res;
	}

	public void listeClients(String user){
		String message = "liste_clients";
		Message msg = new Message(message + "-" + user, this);
		this.envoiMessage(msg, socket);
		Message receivedMessage = this.recevoirMessage();
		System.out.println(receivedMessage.getContenu());
	}

	public void messagesAbonnements() {
		String message = "posts_abonnements";
		Message msg = new Message(message + "-" + this.getPseudo(), this);
		envoiMessage(msg, socket);
		Message receivedMessage = this.recevoirMessage();
		System.out.println(receivedMessage.getContenu());
	}

	public void liker(String messageLike){
		String message = "like";
		Message msg = new Message(message + "-" + this.getPseudo() + "_" + messageLike, this);
		envoiMessage(msg, socket);
	}

	public int nbAbonnes() {
		return 0; // TODO
	}

	public String getPseudo() {
		return pseudo;
	}

	@Override
	public String toString() {
		return pseudo;
	}

	public void menu() {
		System.out.println("1. Envoyer un message");
		System.out.println("2. Utiliser une commande");
		System.out.println("0. Quitter");
		System.out.println("Choisissez une option : ");
	}

	public void optionMessage() {
		System.out.println("Ecrivez quelque chose : ");
		String message = this.scannerClient.nextLine();
		Message msg = new Message(message, this);
		this.envoiMessage(msg, this.socket);
		this.recevoirMessage();
	}

	public void optionCommandes() {
		System.out.println("Voici la liste des commandes disponibles :");
		System.out.println("--> /follow");
		System.out.println("--> /unfollow");
		System.out.println("--> /liste_follow");
		System.out.println("--> /like");
		System.out.println("--> /mes_posts");
		System.out.println("--> /posts_abonnements");
		System.out.println("--> /delete");
		System.out.println("--> /exit \n");
		System.out.println("Quelle commande souhaitez-vous utiliser ? ");
		String message = this.scannerClient.nextLine();
		switch (message) {
			case "/follow":
				System.out.println("Quel utilisateur souhaitez-vous suivre ? ");
				System.out.println("Voici la liste des utilisateurs existants : ");
				this.listeClients(this.pseudo);
				String user = this.scannerClient.nextLine();
				this.sAbonner(user);
				System.out.println("Vous suivez désormais " + user + " !");
				break;

			case "/unfollow":
				System.out.println("Quel utilisateur souhaitez-vous ne plus suivre ? ");
				System.out.println("Voici votre liste d'abonnements : ");
				this.listeAbonnements(this.pseudo);
				String user2 = this.scannerClient.nextLine();
				this.seDesabonner(user2);
				System.out.println("Vous ne suivez plus " + user2);
				break;

			case "/liste_follow":
				this.listeAbonnements(this.pseudo);
				break;

			case "/mes_posts":
				System.out.println(this.listeMessages(this.pseudo));
				break;

			case "/posts_abonnements":
				this.messagesAbonnements();
				break;

			case "/like":
				this.messagesAbonnements();
				System.out.println("Quel message souhaitez-vous liker ?");
				String msgLike = scannerClient.nextLine();
				this.liker(msgLike);
				break;

			case "/delete":
				System.out.println("Quel message souhaitez-vous supprimer ? ");
				System.out.println(this.listeMessages(this.pseudo));
				String msg = this.scannerClient.nextLine();
				this.removeMessage(msg);
				System.out.println("Message supprimé avec succès");
				break;

			case "/exit":
				System.out.println("A bientôt !");
				this.envoiMessage(new Message("exit-serv", this), socket);
				this.scannerClient.close();
				break;

			case "/exitall":
				System.out.println("A bientôt !");
				this.envoiMessage(new Message("exitall-serv", this), socket);
				this.scannerClient.close();
				break;

			default:
				System.out.println("Veuillez entrer une commande valide");
				break;
		}
	}

	@Override
	public void run() {
		int rep = -1;
		while (rep != 0) {
			menu();
			String userInput = this.scannerClient.nextLine();
			if (userInput.equals("")){
				System.out.println("Veuillez entrer une option valide \n");
			}
			else{
				rep = Integer.parseInt(userInput);

				switch (rep) {
					case 0: {
						System.out.println("A bientôt !");
						break;
					}

					case 1: {
						optionMessage();
						break;
					}

					case 2: {
						optionCommandes();
						break;
					}
					default: {
						System.out.println("Veuillez entrer une option valide \n");
						break;
					}
				}
				System.out.println("\n Appuyez sur Entrée pour continuer");
				userInput = this.scannerClient.nextLine();
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void envoiMessage(Message msg, Socket socket) {
		try {
			if (this.objectOutputStream == null){
				this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
			}
			this.objectOutputStream.writeObject(msg);
			this.objectOutputStream.flush();
			lesMessages.add(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Message recevoirMessage(){
		try {
			if (this.objectInputStream == null){
				this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
			}
			Message receivedMessage = (Message) this.objectInputStream.readObject();
			return receivedMessage;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			Scanner scannerClient = new Scanner(System.in);
			System.out.println("Création de votre compte : \n Entrez un pseudo : ");
			Client client = new Client(scannerClient.nextLine(), scannerClient);
			System.out.println("Bienvenue " + client.getPseudo());
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
