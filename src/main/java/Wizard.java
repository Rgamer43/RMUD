public class Wizard extends PlayerClass{
    public Wizard(){
        name = "Wizard";
        desc = "A powerful spellcaster";
        absc = new int[]{-1, 1, 0, 2, 1, 0};
        startingGear.add(new MagicMissile());
        startingGear.add(new HealingPotion());
        startingGear.add(new HealingPotion());
    }
}
