/*
Gordon Lu
CS401
Assignment 3
Professor Krebs
Spring 2018
*/
import java.util.Scanner;
import java.util.Random;

public class AdventureGameWithObjects 
{
    public static void main(String[] args)
    {
        final int SHORT_SWORD_MIN = 1;
        final int SHORT_SWORD_MAX = 4;
        final int LONG_SWORD_MIN = 3;
        final int LONG_SWORD_MAX = 7;
        final int JUMP_KICK_MIN = 2;
        final int JUMP_KICK_MAX = 6;
        final int AXE_MIN = 2;
        final int AXE_MAX = 6;
        final int FIRE_BLAST_MIN = 4;
        final int FIRE_BLAST_MAX = 10;
        final int STAFF_OF_POWER_MIN = 5;
        final int STAFF_OF_POWER_MAX = 9;

        final int ROGUE_INIT_HP = 55;
        final int ROGUE_INIT_STRENGTH = 8;
        final int PALADIN_INIT_HP = 35;
        final int PALADIN_INIT_STRENGTH = 14;
        final int CHAN_INIT_HP = 45;
        final int CHAN_INIT_STRENGTH = 10;

        final int MINION_INIT_HP = 25;
        final int GOBLIN_INIT_STRENGTH = 4;
        final int SKELETON_INIT_STRENGTH = 3;
        final int WIZARD_INIT_HP = 40;
        final int WIZARD_INIT_STRENGTH = 8;

        

        //Declaring Player Class
        Player player = null;

        //Declaring Enemy Class
        Enemy enemy = null;


        int characterChoice = 0, pathChoice = 0, itemChoice = 0, numEnemies = 0;
        String pathName = "";

        

        int playerActionChoice, randomNumAnswer, randomNumGuess;

        Scanner keyboard = new Scanner(System.in);
        Random randomNums = new Random();

        System.out.println("\nAdventure Game - Start!\n");
        System.out.println("Here are the characters:");
        System.out.println("1. Rogue\n2. Paladin\n3. Jackie Chan\n");

        System.out.print("Which character do you choose?: ");
        characterChoice = keyboard.nextInt();

        switch(characterChoice)
        {
            case 1:
                player = new Player("Rogue",ROGUE_INIT_HP,ROGUE_INIT_STRENGTH,new Weapon("SHORT SWORD",SHORT_SWORD_MIN,SHORT_SWORD_MAX));
                break;
            case 2:
                player = new Player("Paladin",PALADIN_INIT_HP,PALADIN_INIT_STRENGTH,new Weapon("LONG SWORD",LONG_SWORD_MIN,LONG_SWORD_MAX));
                break;
            case 3:
                player = new Player("Jackie Chan",CHAN_INIT_HP,CHAN_INIT_STRENGTH,new Weapon("JUMP KICK",JUMP_KICK_MIN,JUMP_KICK_MAX));
                break;
        }

        System.out.printf("\nYou chose: %s\n\n", player.getName());

        System.out.print("The Evil Wizard must be defeated! He is in The Castle. To get to ");
        System.out.println("The Castle, you must travel through either:");
        System.out.println("1. The Forest\n2. The Graveyard\n");

        System.out.print("Which path will you take?: ");
        pathChoice = keyboard.nextInt();

        switch(pathChoice) 
        {
            case 1:

                pathName = "The Forest";
                enemy = new Enemy("Goblin",MINION_INIT_HP,GOBLIN_INIT_STRENGTH,new Weapon("AXE",AXE_MIN,AXE_MAX));
                numEnemies = Enemy.getNumGoblins(); //randomly generated amount of goblins
                break;
            case 2:
                pathName = "The Graveyard";
                enemy = new Enemy("Skeleton",MINION_INIT_HP,SKELETON_INIT_STRENGTH,new Weapon("SHORT SWORD",SHORT_SWORD_MIN,SHORT_SWORD_MAX));
                numEnemies = Enemy.getNumSkeletons();
                break;
            
        }

        System.out.printf("\nYou chose: %s\n\n", pathName);
        System.out.printf("Once you enter %s, you encounter %d %ss! Time for battle!\n\n", pathName,
                numEnemies, enemy.getName());


        Scanner enter = new Scanner(System.in);        
        for (int i = 1; i <= numEnemies; i++) //battle against either skeletons or minions
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
            
            if (player.getHitPoints() > 0 )
            {                
                    System.out.printf("%s defeated %s %d!\n\n", player.getName(), enemy.getName(), i);
                    System.out.println("Press Enter to Continue: ");
                    enter.nextLine();

                    enemy.resetHitPoints();   
                      
            }

            else
            {
                    System.out.printf("--%s is defeated in battle!--\n\nGAME OVER\n", player.getName());
                    System.exit(0);
            }

        } // end of for loop


        System.out.printf("Your HP is: %d\n\n", player.getHitPoints());

        System.out.println("Please choose a reward.\n1. Healing Potion\n2. Ring of Strength\n3. Staff of Power\n");
        System.out.print("Which item do you choose?: ");
        itemChoice = keyboard.nextInt();

        switch(itemChoice)
        {
            case 1:
                System.out.println("\nYou chose: Healing Potion\n");
                player.increaseHitPoints(10);
                break;
            case 2:
                System.out.println("\nYou chose: Ring of Strength\n");
                player.increaseStrength(5);
                break;
            case 3: 
                System.out.println("\nYou chose: Staff of Power\n");
                Weapon weapon = new Weapon("Staff of Power", STAFF_OF_POWER_MIN, STAFF_OF_POWER_MAX);
                System.out.println("Your weapon damage is now 5 - 9!\n");


                switch(characterChoice) //choosing the staff of power changes the character's intial weapon
                {
                    case 1:
                        player = new Player("Rogue",ROGUE_INIT_HP,ROGUE_INIT_STRENGTH, weapon);
                        break;
                    
                    case 2:
                        player = new Player("Paladin",PALADIN_INIT_HP,PALADIN_INIT_STRENGTH,weapon);
                        break;
                    
                    case 3:
                        player = new Player("Jackie Chan",CHAN_INIT_HP,CHAN_INIT_STRENGTH,weapon);
                        break;
                }

                break;
        }

        System.out.println("You have now reached The Castle! Time to battle The Evil Wizard!\n");

        enemy = new Enemy("Wizard",WIZARD_INIT_HP,WIZARD_INIT_STRENGTH,new Weapon("FIRE BLAST",FIRE_BLAST_MIN,FIRE_BLAST_MAX));

        randomNumAnswer = randomNums.nextInt(6) + 1;

        System.out.printf("***%s vs The Evil Wizard***\n", player.getName());
        while(player.getHitPoints() > 0 && enemy.getHitPoints() > 0)    //battle against the Evil Wizard
        {
            System.out.println("Choose your action:\n1. Attack\n2. Attempt Spell Cast\n");
            System.out.print("What would you like to do: ");
            playerActionChoice = keyboard.nextInt();

            switch(playerActionChoice)
            {
                case 1:
                     player.attack(enemy);
                     break;
                case 2:
                    System.out.print("Enter your guess: ");
                    randomNumGuess = keyboard.nextInt();
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
            System.exit(0);
        }
        else
        {
            System.out.printf("--%s is defeated in battle!--\n\nGAME OVER\n", player.getName());
            System.exit(0);
        }

        keyboard.close();   //closing scanner
        enter.close();

    } // end of main
} // end of class
