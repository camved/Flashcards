package org.example.demojavafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.demojavafx.CardsGeneration.TestQuestion;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainGUI extends Application {

    public Integer WIDTH = 1600;
    public Integer HEIGHT = 1000;

    public Integer WIDTH_CARD = 800;
    public Integer HEIGHT_CARD = 500;

    CardCollection deck;
    SQLRequest sqlRequest;
    boolean overwrittingAnswer; // Si l'utilisateur clique sur "Reussi" ou "Raté", cette réponse prime
    boolean weakerAnswer;
    ChooseConstraitsApp chooseConstraitsApp;


// =============================== CODE ===============================
// ====================================================================

    public MainGUI() throws ClassNotFoundException {
        chooseConstraitsApp = new ChooseConstraitsApp();
        deck = new CardCollection();

        deck.loadFromDatabase("SELECT * FROM flashcards");
        deck.makeFirstPool();
        deck.reinitializeSuccess();

        deck.selectFlashCard();

        System.out.println(deck);

        overwrittingAnswer = false;
        weakerAnswer = false;
        sqlRequest = new SQLRequest();
    }

    public boolean getFinalAnswer(){
        if (overwrittingAnswer) {return true;}
        else {return weakerAnswer;}
    }
// =============================== SET BORPANE ELEMENTS ===============================

    public VBox getBaseFlashCard() throws FileNotFoundException {

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        BackgroundSize backgroundSize = new BackgroundSize(
                WIDTH_CARD,
                HEIGHT_CARD,
                true,
                true,
                true,
                false);

        InputStream stream = new FileInputStream("/home/arthur/Documents/L3/demojavafx/src/main/java/org/example/demojavafx/CardsGeneration/Images/FlashCardDrawing.png");
        Image img = new Image(stream);

        BackgroundImage backgroundImage = new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize);
        layout.setBackground(new Background(backgroundImage));
        return layout;
    }

    public SubScene getCardSubScene() throws FileNotFoundException {
        if (deck.flashcardSelected.type.equals("Quizz")) {
            return subSceneForQuizz();
        } else if (deck.flashcardSelected.type.equals("Enigme")) {
            if (deck.flashcardSelected.front.startsWith("La traversée du LAC")){
                return subSceneForTraverseDuLac();
            }
        } else if (deck.flashcardSelected.takesInput.equals("yes")) {
            return subSceneForInput();
        }
        return subSceneForFrontBack();
    }

    public void setCardInCenter(BorderPane border) throws FileNotFoundException {
        Parent parentOfCardDisplay = getCardSubScene().getRoot();
        border.setCenter(parentOfCardDisplay);
    }

    public void setMenu(BorderPane border){
        SubScene menu = getMenuSubScene();
        border.setLeft(menu);
    }

    public SubScene getMenuSubScene(){

        TreeView<String> tree = chooseConstraitsApp.getCompleteTree();

        tree.setMinSize(300, 700);
        tree.setMaxSize(300, 700);
        tree.setPrefSize(300, 700);

        Button button = redistribuerButton();

        Button success = new Button("Réussi!");
        success.setOnAction(evt -> { overwrittingAnswer = true;});

        Button failure = new Button("Raté!");
        failure.setOnAction(evt -> { overwrittingAnswer = false;});

        VBox menuVBox = new VBox(tree);
        VBox buttonVBox = new VBox(button,success,failure);

        buttonVBox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(menuVBox,buttonVBox);

        SubScene subScene = new SubScene(vbox, 300, 800);

        return subScene;
    }

    public void setEndOfDeckCard(BorderPane border) throws FileNotFoundException {
        Label questionLabel = new Label("Fin du Deck.");
        questionLabel.setWrapText(true);
        questionLabel.setFont(new Font("Arial", 24));
        questionLabel.setMaxSize(WIDTH_CARD, HEIGHT_CARD);

        VBox layout = getBaseFlashCard();
        layout.getChildren().addAll(questionLabel);

        SubScene endOfDeckSubScene = new SubScene(layout, WIDTH_CARD, HEIGHT_CARD);
        Parent parentOfCardDisplay = endOfDeckSubScene.getRoot();
        border.setCenter(parentOfCardDisplay);
    }

// =============================== BUTTONS ===============================

    public void setButtonNextCard(BorderPane border){
        Button nextCardButton = new Button("Carte Suivante");

        nextCardButton.setOnAction(evt -> {

            deck.simulProbAdapt(deck.flashcardSelected, getFinalAnswer());
            deck.probaNormalized();

            if ((deck.flashcardsList.size() != deck.counterDefinetlyNotConsidered)){

                overwrittingAnswer = true;
                weakerAnswer = true;
                deck.selectFlashCard();

                deck.flashcardSelected.denormalizedCard();
                deck.probaDeNormalized();
                deck.addToPool(deck.exFlashCardSelected);


                System.out.println(deck);

                try {
                    setCardInCenter(border);
                } catch (FileNotFoundException e) {throw new RuntimeException(e);}
            }
            else{try {setEndOfDeckCard(border);} catch (FileNotFoundException e) { throw new RuntimeException(e);}}

        });


        HBox hbox = new HBox(nextCardButton);
        SubScene subScene = new SubScene(hbox, WIDTH, 100);
        hbox.setAlignment(Pos.CENTER);
        border.setBottom(subScene);
    }

    public Button redistribuerButton(){
        Button button = new Button("Redistribuer");
        button.setOnAction(evt -> {
            try {
                deck.updateDB();

                String sqlRequest = chooseConstraitsApp.getSqlRequest();
                deck = new CardCollection(sqlRequest);
                deck.selectFlashCard();
                System.out.println(sqlRequest);
            } catch (ClassNotFoundException e) { throw new RuntimeException(e); }
        });
        return button;
    }

    public HBox getButtonTrueOrFalse(){
        Label truthLabel = new Label();
        Button trueButton = new Button("Vrai");
        Button falseButton = new Button("Faux");

        HBox trueOrFalseHbox = new HBox(trueButton, falseButton);
        trueOrFalseHbox.setAlignment(Pos.CENTER);


        trueButton.setOnAction(e -> {
            weakerAnswer = deck.flashcardSelected.back.equals("Vrai");
            truthLabel.setText(STR."C'est \{weakerAnswer}");
            trueOrFalseHbox.getChildren().removeAll();
            trueOrFalseHbox.getChildren().add(truthLabel);
        });

        falseButton.setOnAction(e -> {
            weakerAnswer = deck.flashcardSelected.back.equals("False");
            truthLabel.setText(STR."C'est \{weakerAnswer}");
            trueOrFalseHbox.getChildren().removeAll();
            trueOrFalseHbox.getChildren().add(truthLabel);
        });
        return trueOrFalseHbox;
    }

    public HBox getButtonInput(){

        TextField inputField = new TextField();
        inputField.setEditable(true);
        Button validerButton = new Button("Valider");
        HBox inputHBox = new HBox(inputField,validerButton);

        validerButton.setOnAction(e -> {
            if (inputField.getText().equals(deck.flashcardSelected.back)){
                weakerAnswer = true;
                inputHBox.getChildren().removeAll();
                inputHBox.getChildren().add(new Label("C'est correct"));
            }
            else {
                weakerAnswer = false;
                inputHBox.getChildren().removeAll();
                inputHBox.getChildren().add(new Label("C'est faux."));
            }
        });
        return inputHBox;
    }

// =============================== SUBSCENES FOR DIFFERRENT TYPES OF CARDS ===============================

    public SubScene subSceneForFrontBack() throws FileNotFoundException {

        // overwrittingAnswer = true;

        Label questionLabel = new Label(deck.flashcardSelected.front);
        questionLabel.setWrapText(true);
        questionLabel.setFont(new Font("Arial", 24));
        questionLabel.setMaxSize(WIDTH_CARD, HEIGHT_CARD);

        Label answerLabel = new Label(STR."Réponse: \n \{deck.flashcardSelected.back}");
        answerLabel.setWrapText(true);
        answerLabel.setFont(new Font("Arial", 24));
        answerLabel.setMaxSize(WIDTH_CARD, HEIGHT_CARD);
        answerLabel.setVisible(false);


        VBox layout = getBaseFlashCard();
        layout.setOnMouseClicked(e -> answerLabel.setVisible(true));

        layout.getChildren().addAll(questionLabel, answerLabel);
        return new SubScene(layout, WIDTH_CARD, HEIGHT_CARD);

    }

    public SubScene subSceneForQuizz() throws FileNotFoundException {
        Label questionLabel = new Label(deck.flashcardSelected.front);
        questionLabel.setWrapText(true);
        questionLabel.setFont(new Font("Arial", 24));
        questionLabel.setMaxSize(WIDTH_CARD, HEIGHT_CARD);

        VBox layout = getBaseFlashCard();
        layout.getChildren().addAll(questionLabel, getButtonTrueOrFalse());

        return new SubScene(layout, WIDTH_CARD, HEIGHT_CARD);

    }

    public SubScene subSceneForInput() throws FileNotFoundException {
        Label questionLabel = new Label(deck.flashcardSelected.front);
        questionLabel.setWrapText(true);
        questionLabel.setFont(new Font("Arial", 24));
        questionLabel.setMaxSize(WIDTH_CARD, HEIGHT_CARD);

        VBox layout = getBaseFlashCard();
        HBox inputHBox = getButtonInput();
        layout.getChildren().addAll(questionLabel, inputHBox);
        return new SubScene(layout, WIDTH_CARD, HEIGHT_CARD);

    }

    public SubScene subSceneForTraverseDuLac() throws FileNotFoundException {
        VBox layout = getBaseFlashCard();
        overwrittingAnswer = true;
        TestQuestion enigme = new TestQuestion();

        SubScene enigmeScene = enigme.enigmeScene();

        layout.getChildren().addAll(enigmeScene);
        return new SubScene(layout, WIDTH_CARD, HEIGHT_CARD);
    }

    // =============================== MAIN AND START  ===============================

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        primaryStage.setTitle("FlashCard Application : ");
        BorderPane border = new BorderPane();

        setCardInCenter(border);
        setButtonNextCard(border);
        setMenu(border);

        Scene scene = new Scene(border, WIDTH,HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}