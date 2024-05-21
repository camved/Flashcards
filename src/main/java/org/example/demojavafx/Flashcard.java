package org.example.demojavafx;
import javafx.scene.Node;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class Flashcard {

    final int idFlashCard;    // nombre de reponses justes pour cette carte
    int nbWin;    // nombre de reponses fausses pour cette carte
    int nbLoose;
    final int idChap;    // enigme, cours ou exercice
    public final String type;    // 'yes' or 'no', pour savoir si on peut retrouver la carte directement dans notre deck de cartes
    final String isFirstOfSuite;    // Donne la carte de niveau deux de ce niveau
    final int nextCard;    // 'yes' or 'no', pour savoir si on attends une réponse de l'utilisateur
    public final String takesInput;    // Stocker le problème de la carte
    public final String front;    // Stocker la réponse de la carte
    public final String back;    // Proba de la carte
    double proba;    // Stocker l'interface graphique    // final Node graphicalRepresentation;
    double successRate = 0;
    double normalizedValue;

    public Flashcard(int idFlashCard, int nbWin, int nbLoose, int idChap, String type, String isFirstOfSuite, int nextCard, String takesInput, String front, String back, double proba) {
        this.idFlashCard = idFlashCard ;
        this.nbWin = nbWin;
        this.nbLoose = nbLoose;
        this.idChap = idChap;
        this.type = type;
        this.isFirstOfSuite = isFirstOfSuite;
        this.nextCard = nextCard;
        this.takesInput = takesInput;
        this.front = front;
        this.back = back;
        this.proba = proba;

        // this.graphicalRepresentation = graphicalRepresentation;

    }

    public int getNextCard() {
        return this.nextCard;
    }

    public boolean isFirstOfSuite(){
        return this.isFirstOfSuite.equals("yes");
    }

    public int getId() {
        return this.idFlashCard;
    }

    public void denormalizedCard(){
        this.proba -= this.normalizedValue ;
    }

    public void adaptSuccessRate(){
         double successAdapted = 0;
        if ((this.nbWin != 0) | (this.nbLoose != 0)) {
            successAdapted = ((float) (this.nbWin) / (this.nbWin + this.nbLoose));
        }
        this.successRate = successAdapted;
    }

    public boolean noMoreConsidered(){
        this.adaptSuccessRate();
        return (this.successRate >= 0.7 && this.nbWin > 3 );
    }

    public static Comparator<Flashcard> SuccessRateComparator = new Comparator<Flashcard>() {
        public int compare(Flashcard f1, Flashcard f2) {
            return Double.compare(f2.successRate, f1.successRate);
        }
    };

    public static Comparator<Flashcard> ProbaComparator = new Comparator<Flashcard>() {
        public int compare(Flashcard f1, Flashcard f2) {
            return Double.compare(f2.proba, f1.proba);
        }
    };

    // a compléter pour vérifier
    public boolean isAnswerTrue (Type answer){
        return true;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" (ID : " + this.idFlashCard +", " );
        stringBuilder.append("nbWin : " + this.nbWin + ", " );
        stringBuilder.append("nbLoose : " + this.nbLoose + ", " );
//        stringBuilder.append("Id Chapitre : " + this.idChap + "\n" );
//        stringBuilder.append("Type : " + this.type + "\n" );
//        stringBuilder.append("isFirstOfSuite : " + this.isFirstOfSuite + "\n" );
//        stringBuilder.append("Carte du niveau suivant : " + this.nextCard + "\n" );
//        stringBuilder.append("takesInput : " + this.takesInput + "\n" );
//        stringBuilder.append("Front : " + this.front + ", " );
//        stringBuilder.append("Back : " + this.back +", ");
        stringBuilder.append( " Proba : " + this.proba + ")");
//        stringBuilder.append(" Taux de réussite : " + this.successRate +")\n" );
        return stringBuilder.toString();

    }
}