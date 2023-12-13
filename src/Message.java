import java.io.Serializable;
import java.util.Date;

class Message implements Serializable {
  private String expediteur;
  private int id;
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

  public String getExpediteur(){return expediteur.toString();}
  public Date getDate(){return dateHeure;}
  public String getContenu(){return contenu;}

  @Override
  public String toString(){
    return "Envoyé le : " + dateHeure + "  de : " + expediteur + " -->  " + contenu;
  }
}
