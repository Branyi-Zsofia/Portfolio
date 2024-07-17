import java.util.concurrent.atomic.AtomicReference;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game {
    private static final int roomX = 10;
    private static final int roomY = 10;
    private static final int playerNum = 10;
    private static final int wait = 50;
    private static final ReentrantLock lock = new ReentrantLock();
    //Tartalmazza a terem dimenzióit és a játékosok számát tartalmazó konstansokat, valamit egy további konstanst, amely meghatározza, hogy mennyit kell várni két kirajzolás között. Ez legyen 50 ms.


    public static void main(String[] args) throws InterruptedException {//Ezen kívül tartalmaz egy main() metódust, ami az alábbiakat teszi:
        Random rand = new Random();
        int ballx = rand.nextInt(roomX);
        int bally = rand.nextInt(roomY);
        Room room = new Room(roomX, roomY, playerNum);//Létrehozza a termet.
        Ball ball = new Ball(ballx, bally,room,lock);
        room.setField(ballx, bally, ball);
        //Létrehozza a labdát, elhelyezve azt a terem bármely pontján.
        for (int i = 0; i < 10; i++) {//Létrehozza és elindítja a játékosokat. (‘A’-tól kezdve legyenek elnevezve!)
            Thread t = new Thread(new Player( String.valueOf((char)(65+i)) , room, lock));
            t.start();
        }
        while(room.getPlayerNum()>1) {//Amíg több, mint egy játékos van játékban, megfelelő időközönként (emlékeztetőül: 50 ms) kirajzoltatja a terem állapotát.
            Thread.sleep(wait);
            room.show();
        }
        for (int i = 0; i < roomX; i++) {//Amint csak egy játékos marad, kiírja a győztes betűjelét, majd őt is leállítja.
            for (int j = 0; j < roomY; j++) {
                if (room.getFieldInfo(i, j) instanceof Player p){
                    System.out.println("győztes: " + room.getFieldInfo(i,j));
                    p.end();
                }
            }
        }
    }t
}
