package org.example.demojavafx;

import java.lang.reflect.Type;
import java.util.*;
import java.sql.*;


public class CardCollection {

    public List<Flashcard> flashcardsList;
    public List<Flashcard> notConsidered;
    public List<Flashcard> considered;
    public List<Flashcard> apparitionOrder;
    public Flashcard flashcardSelected;
    public Flashcard exFlashCardSelected;
    public double probaTmp;
    public int counterSwitch;
    public int counterDefinetlyNotConsidered = 0 ;

    // stockage provisoire de la proba de la carte selectionnée

    public CardCollection() {
        this.flashcardsList = new ArrayList<>();
        this.notConsidered = new ArrayList<>();
        this.considered = new ArrayList<>();
        this.apparitionOrder = new ArrayList<>();
        this.probaTmp = 0;
    }

    public CardCollection(String sqlRequest) throws ClassNotFoundException {
        this.flashcardsList = new ArrayList<>();
        this.notConsidered = new ArrayList<>();
        this.considered = new ArrayList<>();
        this.apparitionOrder = new ArrayList<>();
        this.probaTmp = 0;

        this.reinitializeSuccess();

        this.loadFromDatabase(sqlRequest);
        this.makeFirstPool();

    }

    public void updateCardProba(boolean answer){
        // Apres avoir la réponse
        this.simulProbAdapt(this.flashcardSelected, answer);
        this.probaNormalized();
    }

    public void relaxNormalisationAndAddToPool(){
        // Apres la selection d'une carte autre que le 1er tirage
        this.flashcardSelected.denormalizedCard();
        this.probaDeNormalized();
        this.makeFirstPool();

    }

    public boolean hasCardLeft(){
        return (this.flashcardsList.size() != this.counterDefinetlyNotConsidered);
    }

    // à utiliser pour charger les cartes depuis la database
    public void loadFromDatabase(String sqlRequest) throws ClassNotFoundException {
        Connection conn =null;
        String url ="";
        ArrayList<Flashcard> flashCards = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            url = "jdbc:sqlite:/home/arthur/Documents/L3/demojavafx/Database/deck.db";
            conn = DriverManager.getConnection(url);

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        String user = "";
        String password = "";

        try {
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery(sqlRequest);

            while (myRs.next()) {
                Flashcard flashCard = new Flashcard(
                        myRs.getInt("idFlashCard"),
                        myRs.getInt("nbWin"),
                        myRs.getInt("nbLoose"),
                        myRs.getInt("idChap"),
                        myRs.getString("type"),
                        myRs.getString("isFirstOfSuite"),
                        myRs.getInt("nextCard"),
                        myRs.getString("takesInput"),
                        myRs.getString("front"),
                        myRs.getString("back"),
                        0);

                flashCards.add(flashCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.flashcardsList = flashCards;

    }

    public void updateDB() throws ClassNotFoundException {
        for (Flashcard card : flashcardsList){
            writeInDatabase(card);
        }
    }
    public void writeInDatabase(Flashcard flashcard) throws ClassNotFoundException {

        Connection conn = null;
        String url = "";
        try {
            Class.forName("org.sqlite.JDBC");
            url = "jdbc:sqlite:/home/arthur/Documents/L3/demojavafx/Database/deck.db";
            conn = DriverManager.getConnection(url);

        }catch (SQLException e){
            System.out.println(e.getMessage());

        }
        String user = "";
        String password = "";

        try {

            String sqlWin = "UPDATE flashcards SET nbWin =" + flashcard.nbWin + " WHERE idFlashCard ="  + flashcard.idFlashCard ;
            String sqlLoose = "UPDATE flashcards SET nbLoose ="+ flashcard.nbLoose + " WHERE idFlashCard =" + flashcard.idFlashCard;

            // Mettre à jour le nombre de victoires
            PreparedStatement statementWin = conn.prepareStatement(sqlWin);
            statementWin.executeUpdate();

            // Mettre à jour le nombre de défaites
            PreparedStatement statementLoose = conn.prepareStatement(sqlLoose);
            statementLoose.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    //on veut que les probas soient normalisées
    public void probaNormalized(){

        for (Flashcard card : this.considered){
                card.normalizedValue =Math.floor(1000 * ((this.probaTmp * this.apparitionOrder.indexOf(card))  / (((double) (this.considered.size() * (this.considered.size() + 1)) / 2))))/1000;
                card.proba += card.normalizedValue;

        }

    }

    //on veut que les probas ne soient plus normalisées pour rejouer avec les probas
    public void probaDeNormalized(){
        for (Flashcard card : this.considered ){
            card.denormalizedCard();
        }

    }

    // Seulement pour la première utilisation
    public void makeFirstPool(){
        ArrayList<Integer> nextCards = new ArrayList<>();
        for (Flashcard flashCard : this.flashcardsList){
            nextCards.add(flashCard.getNextCard());
        }

        // Si les cartes sont déjà jouées, l'indice de poids correpondra a leur succes rate. Sinon il sera attribué aléatoirement
        Collections.sort(this.flashcardsList,Flashcard.SuccessRateComparator);
        for (Flashcard flashcard : this.flashcardsList ){
            if (!(nextCards.contains(flashcard.getId()))){
                this.considered.add(flashcard);
                this.apparitionOrder.add(flashcard);
            } else {
                this.notConsidered.add(flashcard);
            }

        }

        // Equiprobabilité d'apparition pour le premier tour si la carte est de niveau 1
        for (Flashcard flashcard : this.considered){
            flashcard.proba = Math.floor(1000 * (double) 1 /this.considered.size())/1000;
        }

    }

    public Flashcard selectFlashCard(){
            Collections.sort(this.considered,Flashcard.ProbaComparator);

            for(Flashcard card : this.considered){

                if (card.noMoreConsidered()){
                    this.safeDeleteFromPool(card);
                }
            }

            if (this.counterDefinetlyNotConsidered == this.flashcardsList.size()){
                System.out.println("Apprentissage terminé !" );
                return null ;
            }
            Flashcard flashCardtaken = this.considered.get(0);
            this.notConsidered.add(flashCardtaken);
            this.flashcardSelected = flashCardtaken;
            this.probaTmp = flashCardtaken.proba;
            this.considered.remove(flashCardtaken);

            // On actualise les indices de la liste d apparition des flashcards en supprimant celle que l'on vient de selectionner:
            // les plus récentes sont au début de notre liste (indice proche de zero)

            this.apparitionOrder.remove(flashCardtaken);
            this.apparitionOrder.add(0, flashCardtaken);
            this.counterSwitch += 1;
            return flashCardtaken;


    }

    public void addToPool(Flashcard flashcard){
        if(flashcard != null) {
            this.considered.add(flashcard);
            this.notConsidered.remove(flashcard);
            this.counterSwitch -= 1;
        }


    }

    public void addNextLevelCard (Flashcard flashcard){

        if (flashcardSelected.nextCard != 0){
            double probaCollected = 0;
            for (Flashcard card : this.considered){

                probaCollected =+ ((double) 1 /(this.considered.size() + 2));
                card.proba = card.proba - ((double) 1 /(this.considered.size() + 2));
            }
            System.out.println(flashcardSelected.proba + " - " + ((double) 1 /(this.considered.size() + 2)));
            flashcardSelected.proba = flashcardSelected.proba -  ((double) 1 /(this.considered.size() + 2));

            probaCollected =+ ((double) 1 /(this.considered.size() + 2));
            flashcard = this.getFlashcardFromId(flashcard.nextCard);
            flashcard.proba = probaCollected;
            this.considered.add(flashcard);

            //ajouter la carte de niveau suivant au pool si niveau précédent juste

        }
    }

    public void safeDeleteFromPool(Flashcard flashcard){
        if(flashcard.noMoreConsidered()){
            this.considered.remove(flashcard);
            this.notConsidered.add(flashcard);
            this.counterDefinetlyNotConsidered ++;
            if (this.apparitionOrder.contains(flashcard)) {
                this.apparitionOrder.remove(flashcard);
            }
            double div =0;
            double probaNotUsed = 0;
            if (flashcardSelected != null){
                div = this.considered.size() + 1;
                probaNotUsed = flashcard.proba/ div;
                flashcardSelected.proba = flashcardSelected.proba + probaNotUsed;
            } else {
                div = this.considered.size();
                probaNotUsed = flashcard.proba/ div;
            }

            for (Flashcard card : this.considered){
                card.proba = card.proba + probaNotUsed ;
            }
        }
    }

    public void probAdapt(Flashcard flashcard, Type answer) {
        int cardinal = 0;
        if(this.counterSwitch != 0){
            cardinal =  this.considered.size() + 1;
        } else {
            cardinal = this.considered.size();
        }

        if (flashcard.isAnswerTrue(answer)) {
            this.addNextLevelCard(flashcard);
            flashcard.proba =Math.floor (1000* (Math.cbrt(0.8) * flashcard.proba))/1000;
            flashcard.nbWin ++;
            for (Flashcard card : this.considered) {
                card.proba += Math.floor (1000* ((((flashcard.proba) / Math.cbrt(0.8)) - flashcard.proba) / cardinal))/1000;
            }
        } else {
            flashcard.proba = Math.floor (1000* (flashcard.proba * 1.25))/1000;
            flashcard.nbLoose ++;
            for (Flashcard card : this.considered) {
                card.proba += Math.floor (1000* ((((flashcard.proba) / 1.25) - flashcard.proba) / cardinal));
            }
        }
        this.probaTmp = flashcard.proba;
        this.exFlashCardSelected = flashcard;
    }

    public void simulProbAdapt(Flashcard flashcard, boolean answer){
        int cardinal = 0;
        if(this.counterSwitch != 0){
            cardinal =  this.considered.size() + 1;
        } else {
            cardinal = this.considered.size();
        }

        if(answer){
            this.addNextLevelCard(flashcard);
            flashcard.proba = Math.floor (1000* (Math.cbrt(0.8) * flashcard.proba))/1000;
            flashcard.nbWin ++;
            for (Flashcard card : this.considered) {
                card.proba += Math.floor (1000* ((((flashcard.proba) / Math.cbrt(0.8)) - flashcard.proba) / cardinal))/1000;
                }
            }else{
            flashcard.proba = Math.floor (1000*  (flashcard.proba * 1.25))/1000;
            flashcard.nbLoose ++;
            for (Flashcard card : this.considered) {
                card.proba += Math.floor (1000* ((((flashcard.proba) / 1.25) - flashcard.proba) / cardinal))/1000;
            }
            }
            this.probaTmp = flashcard.proba;
            this.exFlashCardSelected = flashcard;
    }

    public Flashcard getFlashcardFromId(int id){
        Optional<Flashcard> flashCard = this.flashcardsList.stream()
                .filter(card -> card.getId() == id)
                .findFirst();

        Flashcard foundcard = flashCard.get();
        return foundcard;
    }

    public void reinitializeSuccess(){
        for(Flashcard card : this.flashcardsList){
            card.nbLoose = 0;
            card.nbWin = 0;
        }
    }
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("DECK \n");

        stringBuilder.append("Listes des FlashCards utilisées  : \n" + this.flashcardsList.toString() + "\n");

        stringBuilder.append("FlashCards considérées : \n" + this.considered.toString() + "\n");

        stringBuilder.append("Flashcards non considérées : \n" + this.notConsidered.toString()+ "\n");

        stringBuilder.append("Liste des dernières apparitions : \n" + this.apparitionOrder.toString()+ "\n");

        return stringBuilder.toString();

    }


}
