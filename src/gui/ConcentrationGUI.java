package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Card;
import model.ConcentrationModel;
import model.Observer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The ConcentrationGUI application is the UI for Concentration.
 *
 * @author John Skorcik
 */
public class ConcentrationGUI extends Application
        implements Observer< ConcentrationModel, Object > {

    //The Model
    private ConcentrationModel model;
    //Tells you if no match, how many cards selected, etc.
    private Text result;
    //Move Tracker
    private Text moves;
    //List of Cards from the Model stores locally.
    private List<Card> cards;
    //stores Buttons in Hashmap with Card that correlates as the key.
    Map<Card,Button> cardMap = new HashMap<Card,Button>();
    //stores Buttons in HashMap with Card that correlates as the key. FOR CHEATS.
    Map<Card,Button> cheatMap = new HashMap<Card,Button>();

    /** abra image */
    private Image abra = new Image(getClass().getResourceAsStream(
            "resources/abra.png")
            ,145,145,false,false);
    /** bulbasaur image */
    private Image bulbasaur = new Image(getClass().getResourceAsStream(
            "resources/bulbasaur.png")
            ,145,145,false,false);
    /** charamander image */
    private Image charmander = new Image(getClass().getResourceAsStream(
            "resources/charmander.png")
            ,145,145,false,false);
    /** jigglypuff image */
    private Image jigglypuff = new Image(getClass().getResourceAsStream(
            "resources/jigglypuff.png")
            ,145,145,false,false);
    /** meowth image */
    private Image meowth = new Image(getClass().getResourceAsStream(
            "resources/meowth.png")
            ,145,145,false,false);
    /** pikachu image */
    private Image pikachu = new Image(getClass().getResourceAsStream(
            "resources/pikachu.png")
            ,145,145,false,false);
    /** pokeball image */
    private Image pokeball = new Image(getClass().getResourceAsStream(
            "resources/pokeball.png")
            ,145,145,false,false);
    /** squirtle image */
    private Image squirtle = new Image(getClass().getResourceAsStream(
            "resources/squirtle.png")
            ,145,145,false,false);
    /** venomoth image */
    private Image venomoth = new Image(getClass().getResourceAsStream(
            "resources/venomoth.png")
            ,145,145,false,false);

    /**
     * Constructs the layout for the game.
     * @param stage - Container (window) in which to render the UI.
     * @throws Exception Throws error if something goes wrong with start
     */
    @Override
    public void start( Stage stage ) throws Exception {
        /*
        CONTROLLER - RELATED COMPONENTS
         */
        //Create and Initialize Text and Buttons.
        this.result = new Text("Select the first card.");
        this.result.setLayoutX(10);
        this.result.setLayoutY(20);
        this.result.setFont(Font.font("Verdana",FontWeight.BOLD,15));
        Button reset = new Button("Reset");
        reset.setLayoutX(165);
        reset.setLayoutY(660);
        reset.setMinSize(70,30);
        reset.setFont(Font.font("Verdana",FontWeight.BOLD,15));
        Button undo = new Button("Undo");
        undo.setLayoutY(reset.getLayoutY());
        undo.setLayoutX(reset.getLayoutX()+70);
        undo.setMinSize(70,30);
        undo.setFont(reset.getFont());
        Button cheat = new Button("Cheat");
        cheat.setLayoutY(reset.getLayoutY());
        cheat.setLayoutX(undo.getLayoutX()+70);
        cheat.setMinSize(70,30);
        cheat.setFont(reset.getFont());
        Text moveCounter = new Text(this.model.getMoveCount()+" Moves");
        moveCounter.setLayoutX(550);
        moveCounter.setLayoutY(655);
        moveCounter.setScaleX(2);
        moveCounter.setScaleY(2);
        moveCounter.setFont(Font.font("Verdana", FontWeight.BOLD,10));

        //Setup Text Group and Button Grid
        Group text = new Group(result,moveCounter);
        GridPane buttons = new GridPane();
        buttons.add(reset,1,0);
        buttons.add(undo,2,0);
        buttons.add(cheat,3,0);
        //Positions the Grid in the best spot beneath the cards.
        buttons.setLayoutY(640);
        buttons.setLayoutX(165);

        //Setup Card Grid and Card Map.
        //CardMap - Stores Card Objects w/Unique Card Numbers as the key.
        GridPane cardGrid = new GridPane();
        cardGrid.setLayoutY(25);

        //Setting up for Card Generation, and looping.
        this.cards = this.model.getCards();
        int col = 1;
        int row = 0;
        int i = 0;
        while (row < 4){
            while (col <= 4){
                Card card = this.model.getCards().get(i);
                cardMap.put(card,new Button());
                setCardImage(card);
                cardGrid.add(cardMap.get(card),col,row);
                col++;
                i++;
            }
            col = 1;
            row++;
        }

        //When a card is selected, Move_Counter is changed.
        //Undo button being hit will revert the move.
        undo.setOnAction(actionEvent -> {
            this.model.undo();
            update(this.model,null);
        });
        //Reset will create a new, random board.
        reset.setOnAction(actionEvent ->{
            this.model.reset();
            try {
                start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //Cheat will display a board with all the cards flipped
        cheat.setOnAction(actionEvent -> {
            update(this.model,true);
        });
        //Functionality for each Pokemon Button.
        for (i = 0; i < this.cards.size(); i++){
            Card card = this.cards.get(i);
            Button button = cardMap.get(card);
            int finalI = i;
            button.setOnAction(event ->{
                this.model.selectCard(finalI);
                update(model,null);
            });
        }

        /*
        VIEW COMPONENT
         */

        this.moves = moveCounter;

        //Ultimate Group. Encompass all.
        Group root = new Group(cardGrid,buttons,text);

        //Start stage.
        Scene scene = new Scene(root);

        stage.setTitle( "Concentration" );
        stage.setScene( scene );
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Creates a separate Cheat Window.
     * @param stage - Stage to set the Cheat Window up on
     * @param cheatList - The Cheat List of Cards from the Model
     * @throws Exception - Error thrown if something happens bad!
     */
    private void cheatWindow(Stage stage, List<Card> cheatList) throws Exception{
        //Setup Cheat Grid and Cheat Map.
        GridPane cheatGrid = new GridPane();

        //Setting up for Card Generation, and looping.
        int col = 1;
        int row = 0;
        int i = 0;
        while (row < 4){
            while (col <= 4){
                Card card = cheatList.get(i);
                cheatMap.put(card,new Button());
                setCardImage(card);
                cheatGrid.add(cheatMap.get(card),col,row);
                col++;
                i++;
            }
            col = 1;
            row++;
        }
        //Start stage.
        Scene scene = new Scene(cheatGrid);

        stage.setTitle( "Cheat Window" );
        stage.setScene( scene );
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Sets the Image of the Button to either a PokeBall (FaceDown)
     * OR sets it to the Pokemon the card correlates to (FaceUp)
     * @param card - card being checked
     */
    private void setCardImage(Card card){
        Image image = null;
        if (card.isFaceUp()) {
            switch (card.getNumber()) {
                case 0:
                    image = abra;
                    break;
                case 1:
                    image = bulbasaur;
                    break;
                case 2:
                    image = charmander;
                    break;
                case 3:
                    image = jigglypuff;
                    break;
                case 4:
                    image = meowth;
                    break;
                case 5:
                    image = pikachu;
                    break;
                case 6:
                    image = squirtle;
                    break;
                case 7:
                    image = venomoth;
                    break;
            }
        }
        else
            image = pokeball;
        if (cardMap.containsKey(card))
            cardMap.get(card).setGraphic(new ImageView(image));
        if (cheatMap.containsKey(card))
            cheatMap.get(card).setGraphic(new ImageView(image));
    }


    /**
     * Update the UI. Method called by an object in the game model.
     * Contents of the buttons change based on card faces in model.
     * Changes in the test in the labels may also occur based on changed state.
     * The update makes calls to the public interface to determine new values to show.
     * @param concentrationModel - The model object that knows board state
     * @param o - Null = Non-cheating mode. Non Null = Cheating mode.
     */
    @Override
    public void update( ConcentrationModel concentrationModel, Object o ) {
        this.moves.setText(concentrationModel.getMoveCount()+" Moves");

        switch (concentrationModel.howManyCardsUp()) {
            case 2 -> this.result.setText("No Match. Undo or select a card.");
            case 1 -> this.result.setText("Select the second card.");
            case 0 -> this.result.setText("Select the first card.");
        }

        if (o != null) {
            try{
                cheatWindow(new Stage(),this.model.getCheat());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else
            this.cards = concentrationModel.getCards();

        int cardsFlipped = 0;
        for (Card card : this.cards){
            setCardImage(card);
            if (concentrationModel.howManyCardsUp() == 0 && card.isFaceUp())
                cardsFlipped++;
        }

        if (cardsFlipped == this.cards.size() && o == null)
            this.result.setText("You Win!");

    }

    /**
     * Create the model and add GUI object (this) as an observer of it.
     * @throws Exception throws error if error occurs in init().
     */
    @Override
    public void init() throws Exception{
        this.model = new ConcentrationModel();
        model.addObserver( this );
        System.out.println("init: Initialize and connect to model!");
    }


    /**
     * main entry point launches the JavaFX GUI.
     *
     * @param args not used
     */
    public static void main( String[] args ) {
        Application.launch( args );
    }
}
