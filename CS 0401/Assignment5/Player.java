import java.util.Random;
import java.util.Scanner;
 
public class Player
{
    private String name;
    private int hitPoints, initHP;
    private int strength;
    private Weapon weapon;
 
    public Player(String _name, int _hitPoints, int _strength, Weapon _weapon)
    {
        this.name = _name;
        this.hitPoints = _hitPoints;
        this.initHP = _hitPoints;
        this.strength = _strength;
        this.weapon = _weapon;
    }
 
    public String getName()
    {
        return this.name;
    }
 
    public int getHitPoints()
    {
        return this.hitPoints;
    }
 
    public void resetHitPoints() //new resetHitPoints method added
    {
        hitPoints = initHP;
    }
   
    public Weapon getWeapon()
    {
        return this.weapon;
    }
 
    public void increaseHitPoints(int _pointIncrease)
    {
        hitPoints += _pointIncrease;
        System.out.printf("Your HP has increased to %d + %d = %d!\n\n", hitPoints - 10, 10, hitPoints);
 
    }
 
    public void decreaseHitPoints(int _pointDecrease)
    {
        hitPoints -= _pointDecrease;
    }
 
    public int getStrength()
    {
        return this.strength;
    }
 
    public void increaseStrength(int _strengthIncrease)
    {
        strength += _strengthIncrease;
        System.out.printf("Your Strength has increased to %d + %d = %d!\n\n", strength - 5, 5,strength);
 
    }
 
    public void decreaseStrength(int _strengthDecrease)
    {
        strength -= _strengthDecrease;
    }
 
    public void setWeapon (Weapon _weapon)
    {
        this.weapon = _weapon;
    }
 
    public void attack(Enemy _enemy) // attack an enemy
    {
        int playerDamage = weapon.getDamage();
        int playerAttack = this.getStrength() + playerDamage;
        _enemy.decreaseHitPoints(playerAttack);
 
        System.out.printf("%s attacks with ATK = %d + %d = %d\n", this.getName(), this.getStrength(), playerDamage, playerAttack);
        System.out.printf("%s HP is now %d - %d = %d\n\n", _enemy.getName(), _enemy.getHitPoints() + playerAttack, playerAttack, _enemy.getHitPoints());
 
    }
 
    public void battleMinion(Enemy _enemy) //battle goblins or skeletons
    {
        while (_enemy.getHitPoints() > 0 && hitPoints > 0)
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
 
       System.out.printf("***%s vs The Evil Wizard***\n", name);
 
       while (hitPoints > 0 && _enemy.getHitPoints() > 0)
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
                       System.out.printf("The %s's spell is cast successfully! The Wizard's HP is now 0!\n\n", name);
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
               System.out.printf("--%s is defeated in battle!--\n\nGAME OVER\n", name);
               System.exit(0);
           }
    }
 
    public boolean isDefeated(){
        if(this.hitPoints<=0)
            return true;
        else
            return false;
    }
}