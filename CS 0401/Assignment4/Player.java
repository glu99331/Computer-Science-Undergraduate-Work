import java.util.Scanner;
import java.util.Random;
 
public class Player extends Character
{
    private int coins;
    private Potion[] inventory;
   
    public Player(Character.Type playerType)
    {
        super(playerType);  //reference to superclass constructor
        coins = 0;
        inventory = new Potion[5]; //array of five Potion objects
 
    }
    public int getCoins()
    {
        return this.coins;
    }
    public void increaseCoins(int coins)
    {
        this.coins += coins;
    }
    public void decreaseCoins(int coins)
    {
        this.coins -= coins;
    }
    public void addToInventory(Potion potion)
    {
       
            for(int i = 0; i < inventory.length; i++ )
            {
               
                if(inventory[i] == null && getNumOpenSlots() > 0)
                {
                    inventory[i] = potion; //a new potion is added if the slot is empty and null
                    break;
                   
                }
            }
       
    }
    public Potion removeFromInventory(int index)
    {
            if(inventory[index] == null)
            {
                return null;    //if the element is empty, leave it as is
            }
            Potion hold = inventory[index]; //refers to a Potion object that is not null
            inventory[index] = null;   //make this index null
            return hold;    
    }
    public void displayInventory()
    {
        System.out.println("--Inventory--"); //for formatting purposes
        for(int i = 0; i < inventory.length; i++)
        {
            System.out.print("[" + (i+1) + "] ");
            if(inventory[i] != null)
            {
                System.out.print(inventory[i].getName()); //print the name of the object
            }
            System.out.println();
        }
        System.out.println(); // for formatting purposes
    }
    public int getNumOpenSlots()
    {
        int openSlots = 0;
       
        for(int i = 0; i < inventory.length; i++)
        {
            if(inventory[i] == null)
            {
                openSlots++;    //increment the number of open slots for each time the slot is empty
            }
        }
        return openSlots;
    }
    public void battleMinion(Enemy _enemy) //battle goblins or skeletons
    {
        while (_enemy.getHitPoints() > 0 && super.getHitPoints() > 0)
       {
           this.attack(_enemy);
 
           if (_enemy.isDefeated())
           {
               break;
           }
 
           _enemy.attack(this);  
       }
       
       
    }
 
    public void battleWizard(Enemy _enemy) // battle against the Evil Wizard
    {
       Random randomNums = new Random();
       Scanner keyboard = new Scanner(System.in);
 
       int randomNumAnswer = randomNums.nextInt(6) + 1; // random guess between 1 and 6 to defeat wizard with a spell
 
       System.out.printf("***%s vs The Evil Wizard***\n", super.getName());
 
       while (super.getHitPoints() > 0 && _enemy.getHitPoints() > 0)    //super reference to method getHitPoints
       {
           System.out.println("Choose your action:\n1. Attack\n2. Attempt Spell Cast\n");
            System.out.print("What would you like to do: ");
            int playerActionChoice = keyboard.nextInt();
            System.out.println();
 
           switch(playerActionChoice)
           {
               case 1:       // Attack
                   this.attack(_enemy);
                   break;
               case 2:       // Attempt spell
                   System.out.print("Enter your guess: ");
                   int randomNumGuess = keyboard.nextInt();
                   if (randomNumGuess == randomNumAnswer)
                   {
                       System.out.println("\nCorrect!\n");
                       System.out.printf("The %s's spell is cast successfully! The Wizard's HP is now 0!\n\n", super.getName());
                       _enemy.decreaseHitPoints(_enemy.getHitPoints());
                   }
                   else
                   {
                       System.out.println("\nIncorrect! The spell cast fails!\n");
                   }
                   break;
               default:   // fail-safe
                   System.out.printf("\nInvalid input. %d is not an available option.\n\n", playerActionChoice);
                   System.exit(0);
           }
 
           if (_enemy.getHitPoints() <= 0)
           {
               break;
           }
           _enemy.attack(this);
 
           keyboard.close();
       } // end while
 
       if (this.isDefeated())
           {
               System.out.printf("--%s is defeated in battle!--\n\nGAME OVER\n", super.getName());
               System.exit(0);
           }
    }
 
    public boolean isDefeated()
    {
        return super.getHitPoints() <= 0;
       
    }
 
    public String toString()
    {
        return super.toString() + "Coins: " + coins + "\n" ;    //return the super toString method along with Coins.
    }
 
}