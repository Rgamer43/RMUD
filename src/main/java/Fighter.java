
public class Fighter extends PlayerClass {
    public Fighter(){
        name = "Fighter";
        desc = "A tanky warrior";
        absc = new int[]{2, 0, 1, -1, 0, 1};
        startingGear.add(new Longsword());
        startingGear.add(new HealingPotion());
        startingGear.add(new HealingPotion());
    }
}
