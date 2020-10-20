public class Potion 
{
    public enum Type    //constants for potions
    {
        MINOR_HEALING, 
        HEALING, 
        MINOR_STRENGTH,
        STRENGTH;
    }
    private String name; //attributes
    private Type type;

    public Potion(Type potionType)  //dependent on type chosen, initialize name.
    {
        type = potionType;
        switch(type)
        {
            case MINOR_HEALING:
                name = "Minor Healing Potion";
                break;
            case HEALING:
                name = "Healing Potion";
                break;
            case MINOR_STRENGTH:
                name = "Minor Strength Potion";
                break;
            case STRENGTH: 
                name = "Strength Potion";
                break;
        }
    }

    public String getName()
    {
        return this.name;
    }


    public void drink(Player player) 
    {
        //provide cases dependent on potion
        if(type == Type.MINOR_HEALING) //increase hp if healing potion
        {
             player.increaseHitPoints(5);
        }
        if(type == Type.HEALING)
        {
            player.increaseHitPoints(10);
        }
        if(type == Type.MINOR_STRENGTH) //increase strength if strength potion
        {
            player.increaseStrength(2);
        }
        if(type == Type.STRENGTH)
        {
            player.increaseStrength(5);
        }
    }
}