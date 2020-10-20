import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.script.Bindings; //import for boolean binding
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
 
 
public class AdventureGameBattleSimulator extends Application
{
    //private attributes
    private Stage stage;
    private BorderPane scenePane;
    private VBox centerBox;
    private HBox topBox;
    private HBox bottomBox;
    private VBox numRoundsBox;
    private RadioButton numRounds1, numRounds2, numRounds3, numRounds4;
    private Label numEnemiesLabel;
    private Label roundsLabel;
    private Text optionsText;
    private VBox numEnemiesBox;
    private VBox playerBox;
    private Text playerText;
    private VBox enemyBox;
    private Text enemyText;
    private ToggleGroup roundsGroup;
    private ToggleGroup numEnemiesGroup;
    private RadioButton numEnemies1, numEnemies2, numEnemies3;
    private ListView<String> enemyWeaponListView;
    private ImageView enemyWeaponImageView;
    private ListView<String> playerWeaponListView;
    private ImageView playerWeaponImageView;
    private ListView<String> playerListView;
    private ImageView playerImageView;
    private ListView<String> enemyListView;
    private ImageView enemyImageView;
    private Button enemyButton;
    private Button playerButton;
    private Text titleText;
    private Button runSimulationButton;
    private Button exitButton;

    //Public constants for images
    private static final String PALADIN_IMG = "file:/Users/gordon/Desktop/MyProjects/Assignments/Assignment5/Paladin.png"; 
    private static final String ROGUE_IMG = "file:/Users/gordon/Desktop/MyProjects/Assignments/Assignment5//Rogue.png";
    private static final String JACKIE_CHAN_IMG = "file:/Users/gordon/Desktop/MyProjects/Assignments/Assignment5/JackieChan.png";

    private static final String GOBLIN_IMG = "file:/Users/gordon/Desktop/MyProjects/Assignments/Assignment5/Goblin.png";
    private static final String SKELETON_IMG = "file:/Users/gordon/Desktop/MyProjects/Assignments/Assignment5/Skeleton.png";

    private static final String AXE_IMG = "file:/Users/gordon/Desktop/MyProjects/Assignments/Assignment5/Axe.png";
    private static final String MACE_IMG = "file:/Users/gordon/Desktop/MyProjects/Assignments/Assignment5//Mace.png";
    private static final String SHORT_SWORD_IMG = "file:/Users/gordon/Desktop/MyProjects/Assignments/Assignment5/ShortSword.png";
    private static final String LONG_SWORD_IMG = "file:/Users/gordon/Desktop/MyProjects/Assignments/Assignment5/LongSword.png";


    //Public constants for damage and hp
    private final int ROGUE_INIT_HP = 55;
    private final int ROGUE_INIT_STRENGTH = 8;
    private final int PALADIN_INIT_HP = 35;
    private final int PALADIN_INIT_STRENGTH = 14;
    private final int CHAN_INIT_HP = 45;
    private final int CHAN_INIT_STRENGTH = 10;

    private final int MINION_INIT_HP = 25;
    private final int GOBLIN_INIT_STRENGTH = 4;
    private final int SKELETON_INIT_STRENGTH = 3;
    private final int WIZARD_INIT_HP = 40;
    private final int WIZARD_INIT_STRENGTH = 8;



    @Override
    public void start(Stage primaryStage)
    {
       
        stage = primaryStage;
        //create new text on top
        titleText = new Text("ADVENTURE GAME SIMULATOR");
        titleText.setFont(new Font(30));
 
        topBox = new HBox(titleText);
        topBox.setAlignment(Pos.CENTER);
        //add two buttons on the bottom, one that runs the simulation and one that exits the program
        runSimulationButton = new Button("RUN SIMULATION");
        runSimulationButton.setDisable(true); //set simulation button off until certain conditions are fulfilled
 
        exitButton = new Button("EXIT");
        exitButton.setOnAction(e -> close()); //lambda expression to call on exit method
 
        bottomBox = new HBox(50, runSimulationButton, exitButton);
        bottomBox.setAlignment(Pos.CENTER);
        
        //create center box with two vboxes, each to hold radiobuttons
        optionsText = new Text("OPTIONS");
        optionsText.setFont(new Font(20));
 
        numEnemiesLabel = new Label("\t Number of enemies");
 
        numEnemiesLabel.setFont(new Font(15));
 
       
        numEnemies1 = new RadioButton("4 Enemies");
        numEnemies2 = new RadioButton("5 Enemies");
        numEnemies3 = new RadioButton("6 Enemies");
 
        numEnemiesGroup = new ToggleGroup();
        numEnemies1.setToggleGroup(numEnemiesGroup);
        numEnemies1.setSelected(true); //default selection is 4 enemies
 
        numEnemies2.setToggleGroup(numEnemiesGroup);
        numEnemies3.setToggleGroup(numEnemiesGroup);
       
        numEnemiesBox = new VBox(20,numEnemiesLabel,numEnemies1,numEnemies2,numEnemies3);
        numEnemiesBox.setAlignment(Pos.BASELINE_CENTER);
       
       
        roundsLabel = new Label("\tNumber of rounds");
        roundsLabel.setFont(new Font(15));
 
        numRounds1 = new RadioButton("1 Round    ");    //simply to align buttons
        numRounds2 = new RadioButton("5 Rounds  ");     //simply to align buttons
        numRounds3 = new RadioButton("10 Rounds");
        numRounds4 = new RadioButton("20 Rounds");
 
        roundsGroup = new ToggleGroup();
 
        numRounds1.setToggleGroup(roundsGroup);
        numRounds1.setSelected(true);
 
        numRounds2.setToggleGroup(roundsGroup);
        numRounds3.setToggleGroup(roundsGroup);
        numRounds4.setToggleGroup(roundsGroup);
   
        numRoundsBox = new VBox(20, roundsLabel,numRounds1, numRounds2, numRounds3, numRounds4);
        numRoundsBox.setAlignment(Pos.BASELINE_CENTER);
 
        centerBox = new VBox(40, optionsText, numEnemiesBox, numRoundsBox);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets (30,0,30,30));
 
        //left box holds two list views, one for player, one for weapons
        playerText = new Text("PLAYER");
        playerText.setFont(new Font(20));
 
        playerListView = new ListView<String>();
        playerListView.getItems().addAll("Paladin", "Rogue", "Jackie Chan");
 
        playerImageView = new ImageView();
 
 
        playerWeaponListView = new ListView<String>();
        playerWeaponListView.getItems().addAll("Mace", "Short Sword", "Long Sword", "Axe");
 
        playerWeaponImageView = new ImageView();
        playerButton = new Button("SELECT PLAYER");
        playerButton.setAlignment(Pos.BOTTOM_LEFT);
        playerButton.setOnAction(e -> selectPlayer());
 
        playerBox = new VBox(40, playerText, playerListView, playerImageView, playerWeaponListView, playerWeaponImageView,playerButton);
        playerBox.setPadding(new Insets(30, 0, 30, 30));
       
        playerBox.setAlignment(Pos.TOP_CENTER);
        
        //right box mirrors left box except enemies instead of players
        enemyText = new Text("ENEMY");
        enemyText.setFont(new Font(20));
 
        enemyListView = new ListView<String>();
        enemyListView.getItems().addAll("Goblin", "Skeleton");
 
        enemyImageView = new ImageView();
 
        enemyButton = new Button("SELECT ENEMY");
        enemyButton.setOnAction(e -> selectEnemy());        
 
        enemyWeaponListView = new ListView<String>();
        enemyWeaponListView.getItems().addAll("Mace", "Short Sword", "Long Sword", "Axe");
 
        enemyWeaponImageView = new ImageView();
 
        enemyBox = new VBox(40, enemyText, enemyListView, enemyImageView,enemyWeaponListView,enemyWeaponImageView, enemyButton);
        enemyBox.setPadding(new Insets(30,30,0,30));
 
        enemyBox.setAlignment(Pos.TOP_CENTER);
 
        //conditions that must hold true to turn button on
        BooleanExpression bBind = playerImageView.imageProperty().isNull()
                .or(enemyImageView.imageProperty().isNull())
                .or(playerWeaponImageView.imageProperty().isNull())
                .or(enemyWeaponImageView.imageProperty().isNull());
        
       
        BooleanExpression boolBind = playerListView.getSelectionModel().selectedItemProperty().isNull()
                .or(playerWeaponListView.getSelectionModel().selectedItemProperty().isNull())
                .or(enemyListView.getSelectionModel().selectedItemProperty().isNull())
                .or(enemyWeaponListView.getSelectionModel().selectedItemProperty().isNull());
       
        runSimulationButton.disableProperty().bind(bBind);
        runSimulationButton.setOnAction(e -> runSimulation());
 
        scenePane = new BorderPane();
        scenePane.setTop(topBox);
        scenePane.setCenter(centerBox);
        scenePane.setBottom(bottomBox);
        scenePane.setLeft(playerBox);
        scenePane.setRight(enemyBox);
 
        Scene scene = new Scene(scenePane, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
       
    }
 //method that is called when player button is pressed
public void selectPlayer()
{
    String playerType = playerListView.getSelectionModel().getSelectedItem();
    String weaponType = playerWeaponListView.getSelectionModel().getSelectedItem();
    Image weaponImage = null, playerImage = null;
    Player player = null;
   
    if(playerType == null && weaponType != null)
    {
        MessageBox.show("You must select a character!", "ERROR!");
        return;
    }
    if(weaponType == null && playerType != null )
    {
        MessageBox.show("You must select a weapon!", "ERROR!");
        return;
    }
    if(weaponType == null && playerType == null)
    {
        MessageBox.show("You select a character and a weapon!", "ERROR!");
        return;
    }
 
    if(playerType == "Paladin")
    {
        playerImage = new Image(PALADIN_IMG);
 
    }
    else if(playerType == "Rogue")
    {
        playerImage = new Image(ROGUE_IMG);
 
    }
    else if(playerType == "Jackie Chan")
    {
        playerImage = new Image(JACKIE_CHAN_IMG);
 
    }
    if(weaponType == "Mace")
    {
        weaponImage = new Image(MACE_IMG);
 
    }
    else if(weaponType == "Short Sword")
    {
        weaponImage = new Image(SHORT_SWORD_IMG);
 
    }
    else if(weaponType == "Long Sword")
    {
        weaponImage = new Image(LONG_SWORD_IMG);
 
    }
    else if(weaponType == "Axe")
    {
        weaponImage = new Image(AXE_IMG);
 
    }
 
    playerImageView.setImage(playerImage);
    playerWeaponImageView.setImage(weaponImage);
 
 
 
}
//method that is called when run simulation button is pressed
public void runSimulation() 
{
    RadioButton selectedRounds = (RadioButton) roundsGroup.getSelectedToggle();
    String numRounds = selectedRounds.getText().trim(); 
    System.out.println("#Rounds: " + selectedRounds.getText()); //to display values in command prompt to confirm program's functionality
    int rounds = 0;
 
    RadioButton selectedEnemies = (RadioButton) numEnemiesGroup.getSelectedToggle(); //get selected radio button
    String numEnemies = selectedEnemies.getText(); //get value
    System.out.println("#Enemies: " + numEnemies); //to display values in command prompt to confirm program's functionality
 
    int enemies = 0;
 
    int battlesWon = 0;
    int battlesLost = 0;
    int totalEnemiesDefeated = 0;
 
    String playerType = playerListView.getSelectionModel().getSelectedItem();
    String weaponType = playerWeaponListView.getSelectionModel().getSelectedItem();
 
    String enemyType = enemyListView.getSelectionModel().getSelectedItem();
    String enemyWeaponType = enemyWeaponListView.getSelectionModel().getSelectedItem();
   
    Player player = null;
    Enemy enemy = null;
    Weapon pWeapon = getWeapon(weaponType);
    Weapon eWeapon = getWeapon(enemyWeaponType);
   
    switch(playerType) //intialize player based on choice
    {
    case "Rogue":
        player = new Player(playerType, ROGUE_INIT_HP, ROGUE_INIT_STRENGTH, pWeapon);
        break;
    case "Paladin":
        player = new Player(playerType, PALADIN_INIT_HP, PALADIN_INIT_STRENGTH, pWeapon);
        break;
    case "Jackie Chan":
        player = new Player(playerType, CHAN_INIT_HP, CHAN_INIT_STRENGTH, pWeapon);
        break;
    }
   
    switch(enemyType) //initialize enemy based on choice
    {
    case "Goblin":
        enemy = new Enemy(enemyType, MINION_INIT_HP, GOBLIN_INIT_STRENGTH, eWeapon);
        break;
    case "Skeleton":
        enemy = new Enemy(enemyType, MINION_INIT_HP, SKELETON_INIT_STRENGTH, eWeapon);
        break;
    }
   
    if(numRounds.equals("1 Round")) //set number of rounds based on radio button choice
    {
        rounds = 1;
    }
    else if(numRounds.equals("5 Rounds"))
    {
        rounds = 5;
    }
    else if(numRounds.equals("10 Rounds"))
    {
        rounds = 10;
    }
    else if(numRounds.equals("20 Rounds"))
    {
        rounds = 20;
    }
 
    if(numEnemies.equals("4 Enemies")) //set number of enemies based on radio button choice
    {
        enemies = 4;
    }
    else if(numEnemies.equals("5 Enemies"))
    {
        enemies = 5;
    }
    else if(numEnemies.equals("6 Enemies"))
    {
        enemies = 6;
    }
 
    for(int i = 0; i < rounds; i++) //for loop to simulate battles
    {
        for(int ii = 1; ii <= enemies; ii++) //nested for loop in order to get proper counts
        {
            while(enemy.getHitPoints() > 0 && player.getHitPoints() > 0)
            {
                player.attack(enemy);
               
                if(enemy.getHitPoints() <= 0)
                {
                    break;
                }
               
                enemy.attack(player);
            }
            if (player.getHitPoints() > 0 )
            {     
                    totalEnemiesDefeated++; //increment total number of enemies defeated each time the player's hitpoints exceeds 0 after each battle
                    System.out.println("Another one down for the good guys! " + totalEnemiesDefeated + " enemies have been taken down!" ); //to show the count of the enemies defeated to demonstrate functionality of code in the command prompt
                    enemy.resetHitPoints();  //reset hp after every round
                     
            }
        }
        if(player.isDefeated())
        {
            battlesLost++; //if the player is defeated, increment number of battles lost
            System.out.println("No worries, We'll beat them next time! Total Deaths: " + battlesLost ); //to show the functionality of the program by showing the number of the enemies defeated in the command prompt
        }
        else
        {
            battlesWon++; //otherwise, increment the number of battles won, implying the player is not defeated
            System.out.println("We might've won the battle, but the war is far from over... Total Victories: " + battlesWon ); //to show the functionality of the program by showing the number of battles won in the command prompt
        }
        player.resetHitPoints(); //reset player hp after every round, an extra method has been added to player.java
    }
    MessageBox.show("Number of Enemies per round: " + enemies + "\nNumber of Rounds: " + rounds + "\nNumber of battles won: " + battlesWon + "\nNumber of battles lost: " + battlesLost + "\nTotal number of enemies defeated: " + totalEnemiesDefeated, "SIMULATION RESULTS");
    //finally, display the results in the gui
 
}
 
 
public void selectEnemy() //mirrors select player with the exception of enemy instead of player
{
    String enemyType = enemyListView.getSelectionModel().getSelectedItem();
    String weaponType = enemyWeaponListView.getSelectionModel().getSelectedItem();
    Image enemyImage = null;
    Image weaponImage = null;
 
    if(enemyType == null && weaponType != null)
    {
        MessageBox.show("You must select an enemy!", "ERROR!");
        return;
    }
    if(weaponType == null && enemyType != null )
    {
        MessageBox.show("You must select a weapon!", "ERROR!");
        return;
    }
    if(weaponType == null && enemyType == null)
    {
        MessageBox.show("You select an enemy and a weapon!", "ERROR!");
        return;
    }
 
    if(enemyType == "Goblin")
    {
        enemyImage = new Image(GOBLIN_IMG);
    }
    else if(enemyType == "Skeleton")
    {
        enemyImage = new Image(SKELETON_IMG);
    }
    if(weaponType == "Mace")
    {
        weaponImage = new Image(MACE_IMG);
       
    }
    else if(weaponType == "Short Sword")
    {
        weaponImage = new Image(SHORT_SWORD_IMG);
 
    }
    else if(weaponType == "Long Sword")
    {
        weaponImage = new Image(LONG_SWORD_IMG);
 
    }
    else if(weaponType == "Axe")
    {
        weaponImage = new Image(AXE_IMG);
 
    }
 
    enemyImageView.setImage(enemyImage);
    enemyWeaponImageView.setImage(weaponImage);
}
 
public Weapon getWeapon(String weaponType) //constructor for weapon to ease the access of implementation of weapon class
{
    Weapon w = null;
   
    switch(weaponType)
    {
    case "Short Sword":
        w = new Weapon(weaponType, Weapon.SHORT_SWORD_MIN, Weapon.SHORT_SWORD_MAX);
        break;
    case "Long Sword":
        w = new Weapon(weaponType, Weapon.LONG_SWORD_MIN, Weapon.LONG_SWORD_MAX);
        break;
    case "Axe":
        w = new Weapon(weaponType, Weapon.AXE_MIN, Weapon.AXE_MAX);
        break;
    case "Mace":
        w = new Weapon(weaponType, Weapon.MACE_MIN, Weapon.MACE_MAX);
        break;
    }
   
    return w;
   
}
 
public void close() //exit method called when exit button is pressed
{
    stage.close();
}
 
 
public static void main(String[] args) //launch the gui!
{
    Application.launch(args);
}
 
}