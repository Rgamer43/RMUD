import java.io.IOException;
import java.io.Serializable;

public class Player extends Entity implements Serializable {

    public String username;

    public PlayerClass pClass;

    public void onDeath(Entity killer) throws IOException {
        for (int x = 0; x < Server.locations[location].occupants.size(); x++) {
            Player p = Server.locations[location].occupants.get(x);
            Server.getUserSession(p).oos.writeUTF(name + " was slain by " + killer.name);
            Server.locations[1].enter(this, false);
            if (killer.getClass() == Player.class) {
                killer.gold += gold/2;
                Server.getUserSession((Player) killer).oos.writeUTF("Gained " + (gold/2) + " from killing " + name);
            }
            Server.getUserSession(this).oos.writeUTF("Lost " + (gold/2) + " gold");
            gold /= 2;
            hp = maxHp;
            Server.getUserSession(this).oos.writeUTF("setHPHUD " + hp + "/" + maxHp);
        }
    }

    public void calculateMaxHP() {
        if (hp == maxHp) hp = 10 + (absc[2].value * 5);
        maxHp = 10 + (absc[2].value * 5);
        if (hp > maxHp) hp = maxHp;
    }
}
