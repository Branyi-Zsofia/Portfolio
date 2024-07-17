import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ball implements Runnable {
    private static final int wait = 50; //Tartalmaz egy konstanst, amely meghatározza, hogy két lépés között mennyit kell várnia. Ez legyen 50 ms.
    private final Room room; //Tárol egy referenciát a teremre.
    private int X;
    private int Y; //Tartalmazza a labda koordinátáit, amelyeknek kezdeti értékét a konstruktor állítja be.
    private int toX = 0;
    private int toY = 0; //Tartalmazza a mozgás irányát (pl. szintén két koordináta segítségével).
    private boolean isMoving = false;
    private final ReentrantLock lock;

    public Ball(int x, int y, Room room, ReentrantLock lock){
        this.X = x;
        this.Y = y;
        this.room = room; //A konstruktora beállítja a termet és a kezdeti koordinátákat.
        this.lock = lock;
    }
    public void run() { //A run() metódusa:
        if (room.getPlayerNum() > 1){ //Amíg a teremben több, mint egy játékos van:
            try{Thread.sleep(wait);} catch(InterruptedException e){e.printStackTrace();} //Vár meghatározott ideig. (Emlékeztető: 50 ms)
            if(this.isMoving && ((toX == -1) || (toX == 10) || (toY == -1) || (toY == 10))) {//Ha a labda mozgásban van, de elérte a falat, akkor megáll a mozgása.
                isMoving = false;
            } else if (this.isMoving && (room.getFieldInfo(toX,toY) instanceof Player)) { //Ha a labda mozgásban van, akkor megvizsgálja, hogy az irányában van-e játékos
                room.remove(toX,toY);
                room.move(X,Y,toX,toY);//Ha van játékos, akkor kidobja, és megáll a mozgása.
                X = toX;
                Y = toY;
            }else{
                room.move(X,Y,toX,toY);
                X = toX;
                Y = toY;//Ellenkező esetben a labda elmozdul a teremben.
            }

        }
    }
    public void throwBall(int x, int y) { //Van egy metódus, aminek segítségével el lehet dobni a labdát egy adott irányba. Ez a metódus annyit csinál, hogy beállítja a mozgás irányát jelző adattagokat.
        toX = x;
        toY = y;
    }
    public boolean isMoving(){ //Egy másik metódussal lekérdezhető, hogy mozgásban van-e a labda.
        return this.isMoving;
    }
    @Override
    public String toString() { //Érdemes toString() metódust is átírni, hogy egy o karaktert írjon ki, megkönnyítve a terem kirajzoló metódusát.
            return "o";
    }
}
