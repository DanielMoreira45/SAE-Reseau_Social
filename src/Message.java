import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

class Message implements Serializable {
	private String expediteur;
	private static int id;
	private String contenu;
	private Date dateHeure;
	private int nbLikes;

	public Message(String contenu, Client expediteur) {
		this.expediteur = expediteur.getPseudo();
		this.contenu = contenu;
		id += 1;
		dateHeure = new Date();
		nbLikes = 0;
	}

	public Message(String contenu, String expediteur) {
		this.expediteur = expediteur;
		this.contenu = contenu;
		id += 1;
		dateHeure = new Date();
		nbLikes = 0;
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

	public int getId() {
		return id;
	}

	public HashMap<String,String> getJson(){
		HashMap<String, String> json = new HashMap<String, String>();
		json.put("id", Integer.toString(id));
		json.put("contenu", contenu);
		json.put("dateHeure", dateHeure.toString());
		json.put("nbLikes", Integer.toString(nbLikes));
		return json;
	}

	@Override
	public String toString() {
		return "Envoyé le : " + dateHeure + "  de : " + expediteur + " -->  " + contenu;
	}
}
