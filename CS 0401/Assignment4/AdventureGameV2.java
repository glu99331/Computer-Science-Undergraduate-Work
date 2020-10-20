/*
Gordon Lu
CS401
Professor Krebs
Spring 2018
Assignment 4
*/

import java.util.Scanner;
import java.util.Random;
 
public class AdventureGameV2
{
    public static void main(String[] args)
    {
        Player player = null;
        Potion potion = null;
 
        Enemy forestEnemy = null, graveyardEnemy = null, evilBossEnemy = null;
 
        int characterChoice = 0, numSkeletonEnemies = 0, numGoblinEnemies = 0, numCoinsDropped = 0,randomNumAnswer;
 
        Scanner keyboard = new Scanner(System.in);
        Random randomNums = new Random();
 
        System.out.println("\nAdventure Game - Start!\n");
        System.out.println("Here are the characters:");
        System.out.println("1. Rogue\n2. Paladin\n3. Jackie Chan\n");
 
        System.out.print("Which character do you choose?: ");
        characterChoice = keyboard.nextInt();
 
        switch(characterChoice) //switch statement dependent on choice of user
        {
        case 1:
            player = new Player(Character.Type.ROGUE);
            break;
        case 2:
            player = new Player(Character.Type.PALADIN);
            break;
        case 3:
            player = new Player(Character.Type.JACKIE_CHAN);
            break;
        }
 
        System.out.printf("\nYou chose: %s\n\n", player.getName());
 
        System.out.print("The Evil Wizard must be defeated! He is in The Castle. To get to");
        System.out.println("The Castle, you must travel through The Forest and then through The");
        System.out.println("Graveyard. Let’s go!");
 
        forestEnemy = new Enemy(Character.Type.GOBLIN); //create a new Enemy object
        numGoblinEnemies = Enemy.getNumGoblins(); //create a random number of goblins
 
        battleMinion("The Forest", forestEnemy, numGoblinEnemies, player, numCoinsDropped); //method to simulate battle against goblins
 
        System.out.printf("Your HP is: %d\n\n", player.getHitPoints());
        System.out.println("What would you like to do now?");
        choiceMenu();   //method call to choiceMenu to show the options the player can choose
        int choice = keyboard.nextInt();
        while(!(choice == 5)) //while the option chosen is not "CONTINUE", keep asking the player to do an action
        {
            if(choice < 1 || choice > 5)
            {
                System.out.println("Error. Please enter a valid input."); //if choice is not one of the options shown, prompt user to select again
                choiceMenu();  
                choice = keyboard.nextInt();
            }
            if(choice == 1)     //choice 1 --> display the player's current stats.
            {
                displayPlayerStats(player); //method call to displayStats, which also displays choiceMenu
                choice = keyboard.nextInt();
 
            }
            else if(choice == 2) //choice 2 --> display the player's stats
            {
                displayInventory(player);
                choice = keyboard.nextInt();
            }
            else if(choice == 3) //choice 3 --> purchase potions
            {
                if(player.getCoins() <= 0) 
                {
                    insufficientFunds(); //insufficient funds methods --> prompts the user to choose another option due to coins being less than or equal to zero
                    choice = keyboard.nextInt();
                }
                else if(player.getNumOpenSlots() == 0) //if inventory is full, execute this block
                {
                    fullInventory();
                    choice = keyboard.nextInt();
                }
                else    //otherwise prompt the user to purchase a potion
                {
                    System.out.println("Which potion would you like to purchase?");
                    potionMenu();
                    int potionChoice = keyboard.nextInt();
                    purchasePotion(potionChoice, player, potion);
 
                    System.out.println("What would you like to do now?");
                    choiceMenu();
                    choice = keyboard.nextInt();
                }
 
            }
            else if(choice == 4) //choice 5 --> drink a potion
            {
                if(player.getNumOpenSlots() == 5) // if all the slots are empty, execute this block
                {
                    noPotions();
                    choice = keyboard.nextInt();
                }
                else //otherwise, prompt the user to drink a potion
                {
                    System.out.println("Which potion would you like to drink?");
                    int drinkIndex = keyboard.nextInt() - 1;
                    Potion drinkPotion = null;
                    try 
                    {
                        drinkPotion = player.removeFromInventory(drinkIndex); //avoid exiting program if there is an out of bounds exception
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        //do nothing, simply exit try-catch block to execute the code after
                    }
                    
                    drinkPotion(drinkIndex, drinkPotion, potion, player); //method call to drink a potion, simulating the player drinking 1 of 4 available potions
 
                    System.out.println("What would you like to do now?");
                    choiceMenu();
                    choice = keyboard.nextInt();
                }
            }
        }
        //battle against skeletons
        graveyardEnemy = new Enemy(Character.Type.SKELETON);
        numSkeletonEnemies = Enemy.getNumSkeletons();
 
        battleMinion("The Graveyard", graveyardEnemy, numSkeletonEnemies, player, numCoinsDropped); //simulate battle against graveyard
 
        System.out.println("What would you like to do now?");
        choiceMenu();
        choice = keyboard.nextInt();
        while(!(choice == 5)) //same block as before regarding choices
        {
            if(choice < 1 || choice > 5)
            {
                System.out.println("Error. Please enter a valid output.");
                choiceMenu();
                choice = keyboard.nextInt();
            }
            if(choice == 1)
            {
                displayPlayerStats(player);
                choice = keyboard.nextInt();
 
            }
            else if(choice == 2)
            {
                displayInventory(player);
                choice = keyboard.nextInt();
            }
            else if(choice == 3)
            {
                if(player.getCoins() <= 0)
                {
                    insufficientFunds();
                    choice = keyboard.nextInt();
                }
                else if(player.getNumOpenSlots() == 0)
                {
                    fullInventory();
                    choice = keyboard.nextInt();
                }
                else
                {
                    System.out.println("Which potion would you like to purchase?");
                    potionMenu();
                    int potionChoice = keyboard.nextInt();
                    purchasePotion(potionChoice, player, potion);
 
                    System.out.println("What would you like to do now?");
                    choiceMenu();
                    choice = keyboard.nextInt();
                }
 
            }
            else if(choice == 4)
            {
                if(player.getNumOpenSlots() == 5)
                {
                    noPotions();
                    choice = keyboard.nextInt();
                }
                else
                {
                    System.out.println("Which potion would you like to drink?");
                    int drinkIndex = keyboard.nextInt() - 1;
                    Potion drinkPotion = player.removeFromInventory(drinkIndex);
 
                    drinkPotion(drinkIndex, drinkPotion, potion, player);
 
                    System.out.println("What would you like to do now?");
                    choiceMenu();
                    choice = keyboard.nextInt();
                }
            }
            else if(choice == 5)
            {
                Scanner enter = new Scanner(System.in);
                enter.nextLine();
                enter.close();
            }
        }
        //battle against the evil wizard
        System.out.println("You have now reached The Castle! Time to battle The Evil Wizard!\n");
 
        evilBossEnemy = new Enemy(Character.Type.WIZARD);
 
        randomNumAnswer = randomNums.nextInt(6) + 1;
 
        battleBoss(randomNumAnswer, player, evilBossEnemy); //method call to battle wizard
 
        keyboard.close(); //close the scanner
 
    }
    // end of main
    public static void battleMinion(String pathName, Enemy enemy, int numEnemies,Player player,int numCoinsDropped) //method to simulate battle against an enemy
    {
        System.out.printf("Once you enter %s, you encounter %d %ss! Time for battle!\n\n", pathName,
                numEnemies, enemy.getName());
        Scanner enter = new Scanner(System.in);
 
        for (int i = 1; i <= numEnemies; i++)
        {
            System.out.printf("***%s vs %s %d***\n", player.getName(), enemy.getName(), i);
 
            while(enemy.getHitPoints() > 0 && player.getHitPoints() > 0)
            {
                player.attack(enemy);
                if(enemy.getHitPoints() <= 0)
                {
                    break;
                }
                enemy.attack(player);
            } // end of while loop
 
            if (player.getHitPoints() > 0)
            {
                numCoinsDropped = enemy.dropCoins();
                System.out.printf("%s defeated %s %d!\n\n", player.getName(), enemy.getName(), i);
                System.out.println(player.getName() + " gains " + numCoinsDropped + " gold coins!");
                player.increaseCoins(numCoinsDropped);
                System.out.println("Press Enter to Continue: ");
                enter.nextLine();
 
                switch(pathName)
                {
                case "The Forest":
                    enemy = new Enemy(Character.Type.GOBLIN);
                    break;
 
                case "The Graveyard":
                    enemy = new Enemy(Character.Type.SKELETON);
                    break;
                }
 
            }
            else
            {
                System.out.printf("--%s is defeated in battle!--\n\nGAME OVER\n", player.getName());
                System.exit(0);
            }
        } // end of for loop
    }
 
    public static void battleBoss(int randomNumAnswer, Player player, Enemy enemy) //simulating a battle against the evil wizard
    {
        Scanner keyboard = new Scanner(System.in);
        System.out.printf("***%s vs The Evil Wizard***\n", player.getName());
        while(player.getHitPoints() > 0 && enemy.getHitPoints() > 0)
        {
            System.out.println("Choose your action:\n1. Attack\n2. Attempt Spell Cast\n");
            System.out.print("What would you like to do: ");
            int playerActionChoice = keyboard.nextInt();
 
            switch(playerActionChoice)
            {
            case 1:
                player.attack(enemy);
                break;
            case 2:
                System.out.print("Enter your guess: ");
                int randomNumGuess = keyboard.nextInt();
                if (randomNumGuess == randomNumAnswer)
                {
                    System.out.println("\nCorrect!\n");
                    System.out.printf("The %s's spell is cast successfully! The Wizard's HP is now 0!\n\n", player.getName());
                    enemy.decreaseHitPoints(enemy.getHitPoints());
                }
                else
                    System.out.println("\nIncorrect! The spell cast fails!\n");
                break;
            }
 
            if (enemy.getHitPoints() <= 0)
                break;
 
            enemy.attack(player);
        } // end of while loop
 
        if (player.getHitPoints() > 0)
        {
            System.out.printf("--%s wins the battle!--\n\n", player.getName());
            System.out.println("You win! Congratulations!");
        }
        else
        {
            System.out.printf("--%s is defeated in battle!--\n\nGAME OVER\n", player.getName());
        }
    }
 
    public static void drinkPotion(int drinkIndex,Potion drinkPotion, Potion potion, Player player) //method to drink a potion
    {
        if (drinkIndex < 0 || drinkIndex >= 5)
        {
            System.out.println("The slot you have selected does not exist.");
        }
        else if(drinkPotion == null)
        {
            System.out.println("The slot you have selected is empty.");
        }
        else
        {
            System.out.println("You drank a " + drinkPotion.getName() + "!" );
            if(drinkPotion.getName().contains("Minor Healing Potion"))
            {
                potion = new Potion(Potion.Type.MINOR_HEALING);
                int initialHealth = player.getHitPoints();
                drinkPotion.drink(player);
                int newHealth = player.getHitPoints();
                System.out.println("Your HP is now " + initialHealth + " + " + (newHealth - initialHealth) + " = " + newHealth );
            }
            else if(drinkPotion.getName().contains("Healing Potion"))
            {
                potion = new Potion(Potion.Type.HEALING);
                int initialHealth = player.getHitPoints();
                drinkPotion.drink(player);
                int newHealth = player.getHitPoints();
                System.out.println("Your HP is now " + initialHealth + " + " + (newHealth - initialHealth) + " = " + newHealth );
            }
            else if(drinkPotion.getName().contains("Minor Strength Potion"))
            {
                potion = new Potion(Potion.Type.MINOR_STRENGTH);
                int initialStrength = player.getStrength();
                drinkPotion.drink(player);
                int newStrength = player.getStrength();
                System.out.println("Your Strength is now " + initialStrength + " + " + (newStrength - initialStrength) + " = " + newStrength );
            }
            else if(drinkPotion.getName().contains("Strength Potion"))
            {
                potion = new Potion(Potion.Type.STRENGTH);
                int initialStrength = player.getStrength();
                drinkPotion.drink(player);
                int newStrength = player.getStrength();
                System.out.println("Your Strength is now " + initialStrength + " + " + (newStrength - initialStrength) + " = " + newStrength );
            }
        }
 
    }
    public static void purchasePotion(int potionDecision,Player player, Potion potion) //method for purchasing a potion
    {
        if(potionDecision < 1 || potionDecision > 4 )
        {
            System.out.println("Error. Please enter a valid input.");
            System.out.println("What would you like to do now?");
        }
        if(potionDecision == 1)
        {
            System.out.println("You purchased a Minor Healing Potion");
            System.out.print("Remaining Gold: " + player.getCoins() + " - " + "5 = ");
            player.decreaseCoins(5);                                                
            System.out.println(player.getCoins());
            potion = new Potion(Potion.Type.MINOR_HEALING);
            player.addToInventory(potion);
        }
        else if(potionDecision == 2)
        {
            System.out.println("You purchased a Healing Potion");
            System.out.print("Remaining Gold: " + player.getCoins() + " - " + "10 = ");
            player.decreaseCoins(10);
            System.out.println(player.getCoins());
            potion = new Potion(Potion.Type.HEALING);
            player.addToInventory(potion);
        }
        else if(potionDecision == 3)
        {
            System.out.println("You purchased a Minor Strength Potion");
            System.out.print("Remaining Gold: " + player.getCoins() + " - " + "20 = ");                                  
            player.decreaseCoins(20);
            System.out.println(player.getCoins());
            potion = new Potion(Potion.Type.MINOR_STRENGTH);
            player.addToInventory(potion);
        }
        else if(potionDecision == 4)
        {
            System.out.println("You purchased a Strength Potion");
            System.out.print("Remaining Gold: " + player.getCoins() + " - " + "40 = ");    
            player.decreaseCoins(40);
            System.out.println(player.getCoins());
            potion = new Potion(Potion.Type.STRENGTH);
            player.addToInventory(potion);
        }
 
    }
    public static void noPotions() //method to print if slots are all open
    {
        System.out.println("You do not have any potions!");
        System.out.println("What would you like to do now?");
        choiceMenu();
    }
    public static void fullInventory() //method to print if all slots are full
    {
        System.out.println("Sorry, your inventory is full, you must either drink a potion, or proceed with another action.");
        System.out.println("What would you like to do now?");
        choiceMenu();
    }
    public static void displayPlayerStats(Player player) //method to print players current stats
    {
        System.out.println("--Stats--");
        System.out.println(player.toString());
        System.out.println("What would you like to do now?");
        choiceMenu();
    }
    public static void displayInventory(Player player) //method to display players inventory
    {
        player.displayInventory();
        System.out.println("What would you like to do now?");
        choiceMenu();
    }
    public static void insufficientFunds() //method to print if the player has no coins
    {
        System.out.println("You do not have enough coins to purchase anything.");
        System.out.println("What would you like to do now?");
        choiceMenu();
    }
    public static void choiceMenu() //method to print the choices
    {
        System.out.println("1. View stats");
        System.out.println("2. View inventory");
        System.out.println("3. Purchase potion");
        System.out.println("4. Drink potion");
        System.out.println("5. Continue");
    }
    public static void potionMenu() //method to print the potions available for purchase
    {
        System.out.println("1. Minor Healing Potion     5 Gold");
        System.out.println("2. Healing Potion          10 Gold");
        System.out.println("3. Minor Strength Potion   20 Gold");
        System.out.println("4. Strength Potion         40 Gold");
    }
} // end of class