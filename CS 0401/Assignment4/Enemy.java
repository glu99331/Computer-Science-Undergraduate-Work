import java.util.Random; //necessary to generate a random number of enemies
public class Enemy extends Character    //Enemy inherits all public attributes of its parent, Character
{
    static Random randomNum = new Random();
    public Enemy(Type enemyType)
    {
        super(enemyType);   //call to the super constructor
    }
    public int dropCoins()
    {
        Random coins = new Random();
        return coins.nextInt(21) + 30;  //randonmly generate number of coins between 30 and 50
    }
    public static int getNumGoblins()
   {
       return randomNum.nextInt(4) + 2; //randomly generate number of goblins between 2 and 5
   }
   public static int getNumSkeletons()
   {
       return randomNum.nextInt(5) + 3; // randomly generate number of skeletons between 3 and 7
   }
}