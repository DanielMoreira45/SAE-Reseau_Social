import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

class Client extends Thread {
	private String pseudo;
	private Socket socket;
	private Scanner scannerClient;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	private HashSet<Message> lesMessages;
	private List<String> abonnements;

	public Client(String pseudo, Scanner scannerClient) {
		this.pseudo = pseudo;
		this.scannerClient = scannerClient;
		lesMessages = new HashSet<>();
		abonnements = new ArrayList<>();
		try {
			this.socket = new Socket("127.0.0.1", 4444);
			this.objectInputStream = null;
			this.objectOutputStream = null;
			System.out.println("Client connecté");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sAbonner(String user) {
		String message = "follow";
		Message msg = new Message(message + "-" + user, this);
		this.envoiMessage(msg, socket);
		abonnements.add(user);
	}

	public void seDesabonner(String user){
		String message = "unfollow";
		Message msg = new Message(message + "-" + user, this);
		this.envoiMessage(msg, socket);
		abonnements.remove(user);
	}

	public List<String> mesAbonnements(){
		return abonnements;
	}

	public void listeAbonnements() {
		Message msg = new Message("listefollow-"+this.pseudo, this);
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
		Message message = null;
		for (Message msg : lesMessages){
			if (contenu.equals(msg.getContenu())){
				message = msg;
			}
		}
		this.envoiMessage(message, socket);
		Message receivedMessage = this.recevoirMessage();
		lesMessages.remove(receivedMessage);
	}

	public String listeMessages(String user){
		String res = "Liste des posts : " + "\n";
		for (Message msg : lesMessages){
			res += "--> " + msg.getContenu() + " | Nombre de likes : " + msg.getNbLikes() + "\n";
		}
		return res;
	}

	// dans l'idée il faudrait que je récupère un client dans ma liste d'abonnements puis que je stocke quelque part tous les posts de cet 
	// utilisateur (il faudrait faire ça pour chacun des abonnements)
	public HashSet<String> messagesAbonnements(){
		HashSet<String> messages = new HashSet<>();
		for (String follow : abonnements){
			messages.add(listeMessages(follow));
		}
		return messages;
	}

	public int nbAbonnes() {
		return 0; // TODO
	}

	public int nbAbonnements() {
		return abonnements.size();
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
		System.out.println("--> /listefollow");
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
				String user = this.scannerClient.nextLine();
				this.sAbonner(user);
				break;

			case "/unfollow":
				System.out.println("Quel utilisateur souhaitez-vous ne plus suivre ? ");
				String user2 = this.scannerClient.nextLine();
				this.seDesabonner(user2);
				break;
			
			case "/listefollow":
				this.listeAbonnements();
				break;
			
			case "/mes_posts":
				System.out.println(this.listeMessages(this.pseudo));
				System.out.println("Nombre de posts : " + lesMessages.size());
				break;
			
			case "/posts_abonnement":
				// System.out.println(this.);
				break;

			case "/like":
				// TODO
				break;

			case "/delete":
				System.out.println("Quel message souhaitez-vous supprimer ? ");
				System.out.println(this.lesMessages);
				String msg = this.scannerClient.nextLine();
				this.removeMessage(msg);
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
				break;
		}
	}

	@Override
	public void run() {
		int rep = -1;
		while (rep != 0) {
			menu();
			String userInput = this.scannerClient.nextLine();
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
					System.out.println("Veuillez entrer une option valide");
					break;
				}
			}
			System.out.println("\n Appuyez sur Entrée pour continuer");
			userInput = this.scannerClient.nextLine();
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
