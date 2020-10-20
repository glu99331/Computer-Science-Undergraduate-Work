public abstract class Character
{
    public enum Type    //enum types for character
    {
        ROGUE,
        PALADIN,
        JACKIE_CHAN,
        GOBLIN,
        SKELETON,
        WIZARD;
    }
    //private attributes
    private String name;
    private int hitPoints;
    private int strength;
    private Weapon weapon;

    public Character(Type characterType)
    {  
        switch(characterType) //cases dependent on the type chosen 
        {
            case ROGUE: 
                name = "Rogue";
                hitPoints = 55;
                strength = 8;
                weapon = new Weapon("Short Sword", Weapon.SHORT_SWORD_MIN, Weapon.SHORT_SWORD_MAX);
                break;
            case PALADIN:
                name = "Paladin";
                hitPoints = 35;
                strength = 14;
                weapon = new Weapon("Long Sword", Weapon.LONG_SWORD_MIN, Weapon.LONG_SWORD_MAX);
                break;
            case JACKIE_CHAN:
                name = "Jackie Chan";
                hitPoints = 45;
                strength = 10;
                weapon = new Weapon("Jump Kick", Weapon.JUMP_KICK_MIN, Weapon.JUMP_KICK_MAX);
                break;
            case GOBLIN:
            {
                name = "Goblin";
                hitPoints = 25;
                strength = 4;
                weapon = new Weapon("Axe", Weapon.AXE_MIN, Weapon.AXE_MAX);
                break;
            }
            case SKELETON:
            {
                name = "Skeleton";
                hitPoints = 25;
                strength = 3;
                weapon = new Weapon("Short Sword", Weapon.SHORT_SWORD_MIN, Weapon.SHORT_SWORD_MAX);
                break;
            }
            case WIZARD:
            {
                name = "Wizard";
                hitPoints = 40;
                strength = 8;
                weapon = new Weapon("Fire Blast", Weapon.FIRE_BLAST_MIN, Weapon.FIRE_BLAST_MAX); 
                break;
            }
       
        }
        
    }
    public String getName()
    {
        return this.name;
    }
    public int getHitPoints()
    {
        return this.hitPoints;
    }
    public int getStrength()
    {
        return this.strength;
    }
    public void setStrength(int strength)
    {
        this.strength = strength;
    }
    public void setWeapon(Weapon weapon)
    {
        this.weapon = weapon;
    }
    public void increaseStrength(int strengthIncrease)  //increasing strength by value passed in
    {
        strength += strengthIncrease;
    }
    public void increaseHitPoints(int pointIncrease)    //increase hp by pointIncrease
    {
        hitPoints += pointIncrease;
    }
    public void decreaseHitPoints(int pointDecrease)    //decrease hp by pointDecrease
    {
        hitPoints -= pointDecrease;
    }
    public void attack(Character opponent)  //character attacking an opposing force
    {
        int playerDamage = weapon.getDamage();
        int playerAttack = this.getStrength() + playerDamage;
        opponent.decreaseHitPoints(playerAttack);

        System.out.printf("%s attacks with ATK = %d + %d = %d\n", this.getName(), this.getStrength(), playerDamage, playerAttack);
        System.out.printf("%s HP is now %d - %d = %d\n\n", opponent.getName(), opponent.getHitPoints() + playerAttack, playerAttack, opponent.getHitPoints());
    }
    public boolean isDefeated() 
    {
        return this.hitPoints<=0; //returns true if hp less than or equal to 0, false otherwise
    }
    public String toString()    //toString method
    {
        return "\nName: " +  this.name + "\nHit Points: " + this.hitPoints + "\nStrength: " + this.strength + "\nWeapon: " + this.weapon.getName() + " (" + this.weapon.getMinDamage() + " - " + this.weapon.getMaxDamage() + ")\n" ; 
    }
}