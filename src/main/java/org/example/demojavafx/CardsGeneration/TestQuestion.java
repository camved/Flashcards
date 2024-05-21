package org.example.demojavafx.CardsGeneration;

import com.google.common.base.StandardSystemProperty;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;


public class TestQuestion extends Application {

 public static boolean successState = false;

 private double orgSceneX, orgSceneY;
 private double orgTranslateX, orgTranslateY;
 private static final int SIZE = 100;
 private static final int SIZE_CHICK = 50;
 private static final int RIVER_BOUND_X_TP_LEFT = 284 ;
 private static final int RIVER_BOUND_Y_TP_LEFT = 257;
 private static final int RIVER_BOUND_Y_BT_RIGHT = 598;
 private static final int RIVER_BOUND_X_BT_RIGHT = 543;

 private static final Rectangle RIVER_BOUND_BOX = new Rectangle(RIVER_BOUND_X_TP_LEFT,RIVER_BOUND_Y_TP_LEFT,(RIVER_BOUND_X_BT_RIGHT-RIVER_BOUND_X_TP_LEFT),(RIVER_BOUND_Y_BT_RIGHT-RIVER_BOUND_Y_TP_LEFT));

 private static final int RIVER_SIDE_X_TP_LEFT = 543 ;
 private static final int RIVER_SIDE_Y_TP_LEFT = 259;
 private static final int RIVER_SIDE_Y_BT_RIGHT = 598;
 private static final int RIVER_SIDE_X_BT_RIGHT = 797;

 private static final Rectangle RIVER_SIDE_BOX = new Rectangle(RIVER_SIDE_X_TP_LEFT,RIVER_SIDE_Y_TP_LEFT,(RIVER_SIDE_X_BT_RIGHT-RIVER_SIDE_X_TP_LEFT),(RIVER_SIDE_Y_BT_RIGHT-RIVER_SIDE_Y_TP_LEFT));

 private static Set<ImageView> entetiesLeft  = new HashSet<>();
 private static Set<ImageView> entetiesRiver = new HashSet<>() ;
 private static Set<ImageView> entetiesRight = new HashSet<>() ;

 private static HashMap<ImageView,Point2D> allotedLocations = new HashMap<>(6);
 private static HashMap<ImageView,Point2D> originalLocations = new HashMap<>(6);
 private static final List<ImageView> allEntities = new ArrayList<>(6);

 private static boolean isTraverselected = false;

 public static void main(String[] args) {
  launch(args);
 }


 public SubScene enigmeScene(){
  Pane root = new Pane();

  //Creating a dialog
  Dialog<String> dialog = new Dialog<String>();
  //Setting the title
  dialog.setTitle("Enigme 1 :");
  ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
  //Setting the content of the dialog
  dialog.setContentText("La traversée du LAC:\n" +
          "Vous devez amener les trois loups et les trois poussins sur l autre rive. Attention à bien respecter les règles suivantes :\n" +
          "\n" +
          "Le radeau ne peut accueillir plus de deux animaux.\n" +
          "Vous ne pouvez déplacer le radeau s il est vide.\n" +
          "S il y a plus de loups que de poussins sur une des rives, les loups mangeront les pauvres volatiles sans défense et il vous faudra recommencer depuis le début.\n" +
          "Vous n avez pas de limite de déplacement, mais esseyé de le faire de le moins de déplacement possible.");
  //Adding buttons to the dialog pane
  dialog.getDialogPane().getButtonTypes().add(type);
  //Setting the label
/*  Text txt = new Text("?");les gars il faut vraiment rendre un produit au moins ce soir
  Font font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
  txt.setFont(font);*/
  //Creating a button
  Button button = new Button("?");
  //Showing the dialog on clicking the button
  button.relocate(750,550);
  button.setTranslateX(750);
  button.setTranslateY(0);
  button.setOnAction(e -> {
   dialog.showAndWait();
  });

  //ErrorPassageHandling

  Dialog<String> errorPassageMessage = new Dialog<>();
  errorPassageMessage.setTitle("Passage pas effectuer");
  errorPassageMessage.setContentText("On peux pas déplacer plus de 2 animaux!");

  ButtonType typeTraverseButton = new ButtonType("Traverse", ButtonBar.ButtonData.OK_DONE);
  errorPassageMessage.getDialogPane().getButtonTypes().add(typeTraverseButton);


  Button traverseButton = new Button("Traverser");
  traverseButton.setOnAction(e->{if(!isTraverselected){ if ( entetiesRiver.size() < 3  ){ traverse(entetiesRiver); }else{ errorPassageMessage.showAndWait();restart();   }}isTraverselected=true;});
  //Creating a vbox to hold the button and the label
  HBox paneQuestion = new HBox(30);

  paneQuestion.getChildren().addAll( button);

  Button back = new Button("Revenir");
  back.setOnAction(e->
          {
           if(isTraverselected) {
            for (ImageView entity : entetiesRiver) {
             Point2D originalPoint = originalLocations.get(entity);

             entity.setTranslateY(originalPoint.getY() - entity.getY());
             entity.setTranslateX(originalPoint.getX() - entity.getX());

            }

            if (checkForIllegalPositions()) {
                try {
                    restart();
                } catch (Exception ex) { throw new RuntimeException(ex); }
            }
            System.out.println(entetiesRiver.size());
            if (entetiesRiver.size() >= 3) {
             errorPassageMessage.showAndWait();
                try {
                    restart();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
           }
           isTraverselected =false;

          }


  );


  HBox buttons = new HBox(back,traverseButton);
  buttons.setTranslateX(340);
  buttons.setTranslateY(500);
  // Set the image as background
  Image image = new Image("file:/home/arthur/Documents/L3/demojavafx/src/main/java/org/example/demojavafx/CardsGeneration/Images/pixilart-drawing.png");
  ImageView imageView = new ImageView(image);
  root.getChildren().add(imageView);

  RIVER_SIDE_BOX.setFill(Color.TRANSPARENT);
  RIVER_BOUND_BOX.setFill(Color.TRANSPARENT);
  root.getChildren().addAll(RIVER_BOUND_BOX,RIVER_SIDE_BOX);

  ImageView poussin1 = new ImageView("file:/home/arthur/Documents/L3/demojavafx/src/main/java/org/example/demojavafx/CardsGeneration/Images/poussin.png");
  poussin1.setFitHeight(SIZE_CHICK);
  poussin1.setFitWidth(SIZE_CHICK);
  poussin1.setX(105);
  poussin1.setY(396);
  poussin1.setId("poussin");
  setDraggable(poussin1);
  root.getChildren().add(poussin1);

  allotedLocations.put(poussin1,new Point2D(580,396));
  allEntities.add(poussin1);



  ImageView poussin2 = new ImageView("file:/home/arthur/Documents/L3/demojavafx/src/main/java/org/example/demojavafx/CardsGeneration/Images/poussin.png");
  poussin2.setFitHeight(SIZE_CHICK);
  poussin2.setFitWidth(SIZE_CHICK);
  poussin2.setX(165);
  poussin2.setY(396);
  poussin2.setId("poussin");
  setDraggable(poussin2);
  root.getChildren().add(poussin2);
  allotedLocations.put(poussin2,new Point2D(650,396));
  allEntities.add(poussin2);


  ImageView poussin3 = new ImageView("file:/home/arthur/Documents/L3/demojavafx/src/main/java/org/example/demojavafx/CardsGeneration/Images/poussin.png");
  poussin3.setFitHeight(SIZE_CHICK);
  poussin3.setFitWidth(SIZE_CHICK);
  poussin3.setX(45);
  poussin3.setY(396);
  poussin3.setId("poussin");
  setDraggable(poussin3);
  root.getChildren().add(poussin3);
  allotedLocations.putIfAbsent(poussin3,new Point2D(725,396));
  allEntities.add(poussin3);

  //Setting Rectangle propreties
/*  RIVER_BOUND_BOX.setX(RIVER_BOUND_X_TP_LEFT);
  RIVER_BOUND_BOX.setY(RIVER_BOUND_Y_TP_LEFT);
  RIVER_BOUND_BOX.setHeight((RIVER_BOUND_Y_BT_RIGHT-RIVER_BOUND_Y_TP_LEFT));
  RIVER_BOUND_BOX.setWidth((RIVER_BOUND_X_BT_RIGHT-RIVER_BOUND_X_TP_LEFT));*/


  ImageView wolf1 = new ImageView("file:/home/arthur/Documents/L3/demojavafx/src/main/java/org/example/demojavafx/CardsGeneration/Images/wolf.png");
  wolf1.setFitHeight(SIZE);
  wolf1.setFitWidth(SIZE);
  wolf1.setX(45);
  wolf1.setY(306);

  wolf1.setId("wolf");
  setDraggable(wolf1);
  root.getChildren().add(wolf1);
  allotedLocations.put(wolf1,new Point2D(580,306));
  allEntities.add(wolf1);

  ImageView wolf2 = new ImageView("file:/home/arthur/Documents/L3/demojavafx/src/main/java/org/example/demojavafx/CardsGeneration/Images/wolf.png");
  wolf2.setFitHeight(SIZE);
  wolf2.setFitWidth(SIZE);
  wolf2.setX(165);
  wolf2.setY(306);
  wolf2.setId("wolf");
  setDraggable(wolf2);

  root.getChildren().add(wolf2);

  allotedLocations.put(wolf2,new Point2D(650,306));
  allEntities.add(wolf2);


  ImageView wolf3 = new ImageView("file:/home/arthur/Documents/L3/demojavafx/src/main/java/org/example/demojavafx/CardsGeneration/Images/wolf.png");
  wolf3.setFitHeight(SIZE);
  wolf3.setFitWidth(SIZE);
  wolf3.setX(105);
  wolf3.setY(306);
  wolf3.setId("wolf");
  setDraggable(wolf3);
  root.getChildren().add(wolf3);
  allotedLocations.put(wolf3,new Point2D(725,306));
  allEntities.add(wolf3);

  for(ImageView entity : allEntities){
   originalLocations.put(entity,new Point2D(entity.getX(),entity.getY()));
  }


  root.setLayoutX(0);
  root.setLayoutY(0);


  SubScene scene = new SubScene(new Group(root,paneQuestion,buttons), 800, 600);
  scene.setOnMouseClicked(e->{
   System.out.println("X : "+e.getSceneX()+"Y : "+e.getSceneY());});

  return scene;
 }

 @Override
 public void start(Stage primaryStage) {
  Pane pane = new Pane(enigmeScene());
  Scene scene = new Scene(pane, 800,600);
  primaryStage.setScene(scene);
  primaryStage.getIcons().add(new Image("file:/home/arthur/Documents/L3/demojavafx/src/main/java/org/example/demojavafx/CardsGeneration/Images/logo.png"));
  primaryStage.show();
 }

 private static void traverse (Set<ImageView> entitiesInRiver) {
  System.out.println(entitiesInRiver);

   for (ImageView entity: entitiesInRiver){
    entetiesRight.add(entity);
    entity.setTranslateX(allotedLocations.get(entity).getX()-entity.getX());
    entity.setTranslateY(allotedLocations.get(entity).getY()-entity.getY());
   }
  entetiesRiver = new HashSet<>();
  if (checkForIllegalPositions()){restart();}


 }

 private void setDraggable(javafx.scene.Node node) {
  node.setOnMousePressed(e -> {

   orgSceneX = e.getSceneX();
   orgSceneY = e.getSceneY();
   orgTranslateX = ((javafx.scene.Node) (e.getSource())).getTranslateX();
   orgTranslateY = ((javafx.scene.Node) (e.getSource())).getTranslateY();
   /*System.out.println("X : "+orgSceneX+" Y : "+orgSceneY);
    */
  });

  node.setOnMouseDragged(e -> {
   double offsetX = e.getSceneX() - orgSceneX;
   double offsetY = e.getSceneY() - orgSceneY;
   double newTranslateX = orgTranslateX + offsetX;
   double newTranslateY = orgTranslateY + offsetY;
   /*System.out.println("X : "+e.getSceneX() +" Y : "+e.getSceneY());*/

   int index = allEntities.indexOf(e.getSource());
   ((javafx.scene.Node) (e.getSource())).setTranslateX(newTranslateX);
   ((javafx.scene.Node) (e.getSource())).setTranslateY(newTranslateY);

   allEntities.set(index, (ImageView) e.getSource());
//   currentGameState();

  });
  node.setOnMouseReleased(e-> {updateGameState();System.out.println("State updated : "+entetiesRiver);});
 }

private static boolean isInRiver(ImageView entity){

 Bounds imageViewBounds = entity.getBoundsInParent();
 Bounds rectangleBounds = RIVER_BOUND_BOX.getBoundsInParent();

 // Check if the bounds intersect
 return  imageViewBounds.intersects(rectangleBounds);

}
private static boolean isInRiverSIDE(ImageView entity) {
 Bounds imageViewBounds = entity.getBoundsInParent();
 Bounds rectangleBounds = RIVER_SIDE_BOX.getBoundsInParent();
 return imageViewBounds.intersects(rectangleBounds);

}

private static void updateGameState() {
 entetiesRight=new HashSet<>();
 entetiesRiver=new HashSet<>();
 entetiesLeft=new HashSet<>();
  for ( ImageView entity :allEntities){
   if (isInRiver(entity)){
     entetiesRiver.add(entity);

   } else if (isInRiverSIDE(entity)) {
    entetiesRight.add(entity);
   }else {
    entetiesLeft.add(entity);
   }
  }
  if (winState()){ successState = true;
   System.out.println("Bien Joué !!!");
  }
}

//return true if there is an illegal position
private static boolean checkForIllegalPositions(){
  int wolfCount = 0;
  int poussinCount = 0;
 System.out.println("EntitiesRight  : " + entetiesRight);
  for (ImageView entity : entetiesRight){
   if (entity.getId().equals("wolf")){wolfCount++;}else {poussinCount++;}
  }
  if(wolfCount>poussinCount && (poussinCount!=0)){return true;}

  wolfCount=0;
  poussinCount=0;
 System.out.println("EntitiesLeft  : " + entetiesLeft);
  for (ImageView entity : entetiesLeft){

   if (entity.getId().equals("wolf")){wolfCount++;}else {poussinCount++;}
  }
  if(wolfCount>poussinCount && (poussinCount!=0)){return true;}

  return false;
}

private static void restart() {
 System.out.println("RESTART");
  for (ImageView entity : allEntities){
   Point2D originalPoint = originalLocations.get(entity);
   entity.setTranslateY(originalPoint.getY()-entity.getY());
   entity.setTranslateX(originalPoint.getX()-entity.getX());
  }
  updateGameState();
}

public static boolean winState(){
  return entetiesRight.size()==6;
}


}
