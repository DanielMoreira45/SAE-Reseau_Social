import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

class Message implements Serializable {
	private String expediteur;
	private static int id;
	private int idActuel;
	private String contenu;
	private Date dateHeure;
	private int nbLikes;

	public Message(String contenu, Client expediteur) {
		this.expediteur = expediteur.getPseudo();
		this.contenu = contenu;
		id += 1;
		idActuel = id;
		dateHeure = new Date();
		nbLikes = 0;
	}

	public Message(String contenu, String expediteur) {
		this.expediteur = expediteur;
		this.contenu = contenu;
		id += 1;
		idActuel = id;
		dateHeure = new Date();
		nbLikes = 0;
	}

	public Message(int id, String contenu, int nbLikes, String expediteur){
		this.idActuel = id;
		this.contenu = contenu;
		this.nbLikes = nbLikes;
		this.expediteur = expediteur;
	}

	public String getExpediteur() {
		return expediteur.toString();
	}

	public Date getDate() {
		return dateHeure;
	}

	public String getContenu() {
		return contenu;
	}

	public int getNbLikes() {
		return nbLikes;
	}

	public void setNbLikes(int likes){
		this.nbLikes = likes;
	}

	public int getId() {
		return idActuel;
	}

	public HashMap<String,String> getJson(){
		HashMap<String, String> json = new HashMap<String, String>();
		json.put("id", Integer.toString(idActuel));
		json.put("contenu", contenu);
		json.put("dateHeure", dateHeure.toString());
		json.put("nbLikes", Integer.toString(nbLikes));
		return json;
	}

	public void setContenu(String contenu){
		this.contenu = contenu;
	}

	@Override
	public String toString() {
		return "Envoyé le : " + dateHeure + "  de : " + expediteur + " -->  " + contenu;
	}
}
