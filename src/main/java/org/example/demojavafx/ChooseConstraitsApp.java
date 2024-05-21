package org.example.demojavafx;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.*;

import java.util.ArrayList;

public class ChooseConstraitsApp extends Application {

    HashSet<String> chapNameArrRemembered;
    HashSet<String> matiereNameArrRemembered;
    HashSet<String> anneeNameArrRemembered;
    HashSet<String> typeNameArrRemembered;

    ArrayList<String> matiereNames;
    ArrayList<String> Algebre_ArrString;
    ArrayList<String> Anglais_ArrString;
    ArrayList<String> IA_ArrString;
    ArrayList<String> Analyse_ArrString;
    ArrayList<String> Verification_ArrString;
    ArrayList<String> ProgAlgo_ArrString;
    ArrayList<String> BD_ArrString;
    ArrayList<String> Automate_ArrString;
    ArrayList<String> AnalNum_ArrString;
    ArrayList<String> Topo_ArrString;
    ArrayList<String> Anal_ArrString;


    public ChooseConstraitsApp() throws ClassNotFoundException {
        chapNameArrRemembered = new HashSet<>();
        matiereNameArrRemembered = new HashSet<>();
        anneeNameArrRemembered = new HashSet<>();
        typeNameArrRemembered = new HashSet<>();

        matiereNames = new ArrayList<>(Arrays.asList("Algebre","Anglais", "IA"
                , "Analyse", "Verification","Programmation et Algorithme",
                "Base De Donnee", "Automate", "Analyse Numerique", "Topologie"));

        Algebre_ArrString = new ArrayList<>(Arrays.asList("Forme quadratique", "Dualite","Morphisme", "Espace Euclidien", "Espace Hermitien",
                "Groupe", "Anneau", "Polynomes", "Arithmétique", "Espace Vectoriel", "Base", "Application Lineaire", "Diagonalisation", "Polynomes"));
        Anglais_ArrString = new ArrayList<>(Arrays.asList("Superlatif", "Temps"));
        IA_ArrString = new ArrayList<>(Arrays.asList("Parcours en Profondeur", "Parcours en Largeur", "BestFirst","Enigme", "Parcours Avancees"));
        Verification_ArrString = new ArrayList<>(Arrays.asList("Parcours","Enigme"));
        ProgAlgo_ArrString = new ArrayList<>(Arrays.asList("Graphe","Arbre", "Reseau", "Complexite"));
        BD_ArrString = new ArrayList<>(Arrays.asList("Normalisation"));
        Automate_ArrString = new ArrayList<>(Arrays.asList("Machine de Turing", "Language","Etoile de Kleene"));
        AnalNum_ArrString = new ArrayList<>(Arrays.asList("Spectre"));
        Topo_ArrString = new ArrayList<>(Arrays.asList("Ouvert et Ferme", "Contuinite", "Differentiabilte", "Extrema", "Espace metrique", "Compact"));
        Analyse_ArrString = new ArrayList<>(Arrays.asList("Contuinite", "Differentiabilte", "Derivabilite", "Develloppement Limite"));

    }

    public String getSqlRequest(){
        SQLRequest sqlRequest = new SQLRequest();

        ArrayList<String> matiere = new ArrayList<>(matiereNameArrRemembered);
        ArrayList<String> chapitre = new ArrayList<>(chapNameArrRemembered);
        ArrayList<String> year = new ArrayList<String>(anneeNameArrRemembered);
        ArrayList<String> type = new ArrayList<String>(typeNameArrRemembered);
        String strSqlRequest = sqlRequest.sqlRequest(matiere, chapitre, year, type, "yes");
        return strSqlRequest;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // TODO : C'est la fin du TODO :)

        StackPane rootPane = new StackPane();
        Scene scene = new Scene(rootPane);
        Pane pane2 = new Pane();


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public CheckBoxTreeItem<String> getMatiereTree(String matierName, ArrayList<String> matiere_ArrString){
        CheckBoxTreeItem<String> matiereTreeItem = new CheckBoxTreeItem<>(matierName);
        matiereTreeItem.setExpanded(true);
        for (String matiereChap : matiere_ArrString) {
            matiereTreeItem.getChildren().add(new CheckBoxTreeItem<>(matiereChap));
        }
        return matiereTreeItem;
    }

    public void addMatiere(CheckBoxTreeItem<String> rootMatiereEtChap, String matierName, ArrayList<String> matiere_ArrString){
        rootMatiereEtChap.getChildren().add(getMatiereTree(matierName,matiere_ArrString));
    }

    public void addListenerForMatiereAndChap(CheckBoxTreeItem<String> rootMatiereEtChap){

        for (TreeItem<String> rootMatiere: rootMatiereEtChap.getChildren()){
            addListenerForChap((CheckBoxTreeItem<String>) rootMatiere);
        }

        rootMatiereEtChap.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), (CheckBoxTreeItem.TreeModificationEvent<String> evt) -> {
            CheckBoxTreeItem<String> item = evt.getTreeItem();

            if (matiereNames.contains(item.getValue())) {
                if (item.isIndeterminate() || item.isSelected()) {
                    matiereNameArrRemembered.add(item.getValue());
                } else {
                    matiereNameArrRemembered.remove(item.getValue());
                }
            }
        });

        for (TreeItem<String> rootMatiere: rootMatiereEtChap.getChildren()){
            addListenerForChap((CheckBoxTreeItem<String>) rootMatiere);
        }

    }



    public void addListenerForChap(CheckBoxTreeItem<String> rootMatiere){
        rootMatiere.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), (CheckBoxTreeItem.TreeModificationEvent<String> evt) -> {
            CheckBoxTreeItem<String> item = evt.getTreeItem();
            if (!matiereNames.contains(item.getValue())) {
                if (item.isSelected()) {
                    chapNameArrRemembered.add(item.getValue());
                } else {
                    chapNameArrRemembered.remove(item.getValue());
                }
            }
        });
    }


    public void addAnnees(CheckBoxTreeItem<String> rootAnnee){
        CheckBoxTreeItem<String> treeL1 = new CheckBoxTreeItem<>("1");
        CheckBoxTreeItem<String> treeL2 = new CheckBoxTreeItem<>("2");
        CheckBoxTreeItem<String> treeL3 = new CheckBoxTreeItem<>("3");
        rootAnnee.getChildren().addAll(treeL1, treeL2, treeL3);
    }

    public void addTypes(CheckBoxTreeItem<String> rootTypes){
        CheckBoxTreeItem<String> treeEnigme = new CheckBoxTreeItem<>("Enigme");
        CheckBoxTreeItem<String> treeExercice = new CheckBoxTreeItem<>("Exercice");
        CheckBoxTreeItem<String> treeCours = new CheckBoxTreeItem<>("Cours");
        CheckBoxTreeItem<String> treeQuizz = new CheckBoxTreeItem<>("Quizz");
        rootTypes.getChildren().addAll(treeEnigme, treeExercice, treeCours,treeQuizz);
    }

    public void addListenerForAnnee(CheckBoxTreeItem<String> rootTypes){
        rootTypes.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), (CheckBoxTreeItem.TreeModificationEvent<String> evt) -> {
            CheckBoxTreeItem<String> item = evt.getTreeItem();
            if (item.isSelected()) {
                anneeNameArrRemembered.add(item.getValue());
            } else {
                anneeNameArrRemembered.remove(item.getValue());
            }
        });
    }

    public void addListenerForTypes(CheckBoxTreeItem<String> rootTypes){
        rootTypes.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), (CheckBoxTreeItem.TreeModificationEvent<String> evt) -> {
            CheckBoxTreeItem<String> item = evt.getTreeItem();
            if (item.isSelected()) {
                typeNameArrRemembered.add(item.getValue());
            } else {
                typeNameArrRemembered.remove(item.getValue());
            }
        });
    }

    public TreeView<String> getCompleteTree(){

        // Arbre Total
        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>("Choix:");
        root.setExpanded(true);

        // Arbre pour Matiere et Chapitre
        CheckBoxTreeItem<String> rootChapEtMatiere = new CheckBoxTreeItem<>("Sujet(s):");
        root.setExpanded(true);

        // Arbre pour l'année
        CheckBoxTreeItem<String> rootAnnee = new CheckBoxTreeItem<>("Année(s):");
        root.setExpanded(true);

        // Arbre pour le type
        CheckBoxTreeItem<String> rootType = new CheckBoxTreeItem<>("Type(s):");
        root.setExpanded(true);

        // Remplisssage de l'arbre des matières
        addMatiere(rootChapEtMatiere,"Algebre", Algebre_ArrString);
        addMatiere(rootChapEtMatiere,"Analyse", Analyse_ArrString);
        addMatiere(rootChapEtMatiere,"Analyse Numerique", AnalNum_ArrString);
        addMatiere(rootChapEtMatiere,"Anglais", Anglais_ArrString);
        addMatiere(rootChapEtMatiere,"Automate", Automate_ArrString);
        addMatiere(rootChapEtMatiere,"Base De Donnee", BD_ArrString);
        addMatiere(rootChapEtMatiere,"IA", IA_ArrString);
        addMatiere(rootChapEtMatiere,"Programmation et Algorithme", ProgAlgo_ArrString);
        addMatiere(rootChapEtMatiere,"Topologie", Topo_ArrString);
        addMatiere(rootChapEtMatiere,"Verification", Verification_ArrString);



        addAnnees(rootAnnee);
        addListenerForAnnee(rootAnnee);
        addTypes(rootType);
        addListenerForTypes(rootType);
        addListenerForMatiereAndChap(rootChapEtMatiere);

        root.getChildren().addAll(rootChapEtMatiere, rootAnnee, rootType);

        TreeView<String> treeView = new TreeView<>(root);
        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());

        return treeView;
    }

}
