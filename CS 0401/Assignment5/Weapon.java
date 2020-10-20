import java.util.Random; // necessary to calculate a value from the range of values from the min and max damage.

public class Weapon
{
   /*** Public Constants ***/

   public static final int SHORT_SWORD_MIN = 1;
   public static final int SHORT_SWORD_MAX = 4;
   public static final int LONG_SWORD_MIN = 3;
   public static final int LONG_SWORD_MAX = 7;
   public static final int AXE_MIN = 2;
   public static final int AXE_MAX = 6;
   public static final int MACE_MIN = 2;
   public static final int MACE_MAX = 6;

   /*** Attributes ***/

   private String name;
   private int minDamage;
   private int maxDamage;

   /*** Singletons ***/
  
    private static Weapon shortSword = null;
    private static Weapon longSword = null;
    private static Weapon axe = null;
    public static Weapon mace = null;
   /*** Methods ***/

   // constructor
   public Weapon(String _name, int _minDamage, int _maxDamage)
   {
       this.name = _name;
       this.minDamage = _minDamage;
       this.maxDamage = _maxDamage;
   }

   public String getName()
   {
       return name;
   }

   public int getMinDamage()
   {
       return minDamage;
   }

   public int getMaxDamage()
   {
       return maxDamage;
   }

   // determines weapon damage as random number in range of [minDamage-maxDamage]
   public int getDamage()
   {
       Random randomNum = new Random();
       return randomNum.nextInt(maxDamage - minDamage + 1) + minDamage;
   }


   
}