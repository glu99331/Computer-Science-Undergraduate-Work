import java.util.Random;
import java.util.Scanner;

public class EnemyE
{
   static Random randomNum = new Random();

   /*** Attributes ***/

   private String name;
   private int hitPoints;
   private int strength;
   private Weapon weapon;

   

   /*** Methods ***/

   // constructor
   public Enemy(String _name, int _hitPoints, int _strength, Weapon _weapon)
   {
       this.name = _name;
       this.hitPoints = _hitPoints;
       this.strength = _strength;
       this.weapon = _weapon;
   }

   public String getName()
   {
       return name;
   }

   public int getHitPoints()
   {
       return hitPoints;
   }

   public void resetHitPoints()
   {
       hitPoints = 25;
   }

   public void increaseHitPoints(int _pointsIncrease)
   {
       hitPoints += _pointsIncrease;
   }

   public void decreaseHitPoints(int _pointsDecrease)
   {
       hitPoints -= _pointsDecrease;
   }

   public int getStrength()
   {
       return strength;
   }

   // enemy attacks player
   public void attack(Player _player)
   {
       int enemyDamage = weapon.getDamage();
       int enemyAttack = this.strength + enemyDamage;
       _player.decreaseHitPoints(enemyAttack);
       System.out.printf("%s attacks with ATK = %d + %d = %d\n", this.getName(), this.strength, enemyDamage, enemyAttack);
       System.out.printf("%s HP is now %d - %d = %d\n\n", _player.getName(), _player.getHitPoints() + enemyAttack, enemyAttack, _player.getHitPoints());
    

   }

   public boolean isDefeated()
   {
       if (hitPoints <= 0)
       {
           return true;
       }
       return false;
   }

   // which method is called depends on which path is taken
   public static int getNumGoblins()
   {
       return randomNum.nextInt(4) + 2; //randomly generate number of goblins between 2 and 5
   }
   public static int getNumSkeletons()
   {
       return randomNum.nextInt(5) + 3; // randomly generate number of skeletons between 3 and 7
   }
   // Utility Methods
  
   
}

