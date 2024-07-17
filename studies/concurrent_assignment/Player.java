import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Player implements Runnable { //Player: egy játékos viselkedését szimuláló szál.
    private static final int wait = 100; //Tartalmaz egy konstanst, amely meghatározza, hogy két mozgás között mennyit kell várnia. Ez legyen 100 ms.
    private final Room room; //Tárol egy referenciát a teremre.
    private boolean alive = true; //Tárolja, hogy aktív-e a játékos, vagy már kiesett.
    private final String nev;
    private int X;
    private int Y; //Tartalmazza a játékos koordinátáit, amelyeket az elején véletlenszerűen sorsol ki úgy, hogy az adott ponton még ne legyen játékos, és el is helyezi magát a teremben.
    private final ReentrantLock lock;

    public Player(String nev, Room room, ReentrantLock lock){
        Random rand = new Random();
        int x = rand.nextInt(room.getX());
        int y = rand.nextInt(room.getY());
        this.nev = nev;
        this.lock = lock;
        this.room = room;//A konstruktora átvesz egy nevet (karakterlánc) és a termet, és beállítja őket.
        while(!( room.getFieldInfo(x,y) instanceof Empty)){ //Tartalmazza a játékos koordinátáit, amelyeket az elején véletlenszerűen sorsol ki úgy, hogy az adott ponton még ne legyen játékos, és el is helyezi magát a teremben.
            x = rand.nextInt(room.getX());
            y = rand.nextInt(room.getY());
        }
        room.setField(x,y,this);
        X = x;
        Y = y;
    }
    public void run() { //A játékos viselkedésének lényege a run() metódusban van:
        int toX = X;
        int toY = Y;
        Random rand = new Random();
        int ballX = -1;
        int ballY = -1;
        int dontThrow = -1;
        int throwTo;
        while(alive){//Amíg a játékos aktív
            while (!(room.getFieldInfo(toX, toY) instanceof Empty)) {
                try{Thread.sleep(wait);} catch(InterruptedException e){e.printStackTrace();}//Vár meghatározott ideig. (Emlékeztető: 100 ms)
                switch(rand.nextInt(8)) {
                    case 0:
                        toX = X-1;
                        toY = Y-1;
                        break;
                    case 1:
                        toX = X-1;
                        toY = Y;
                        break;
                    case 2:
                        toX = X-1;
                        toY = Y+1;
                        break;
                    case 3:
                        toX = X;
                        toY = Y-1;
                        break;
                    case 4:
                        toX = X;
                        toY = Y+1;
                        break;
                    case 5:
                        toX = X+1;
                        toY = Y-1;
                        break;
                    case 6:
                        toX = X+1;
                        toY = Y;
                        break;
                    case 7:
                        toX = X+1;
                        toY = Y+1;
                        break;
                    default:
                        System.out.println("Player irány sorsolás meghiusult");
                        break;

                //Megpróbál elmozdulni a teremben valamelyik irányba a 8 közül egy lépést. (Semmiképp ne maradjon helyben!)
                //Ha nem sikerül elmozdulnia, mert az adott helyen fal van, akkor újrakezdi a várakozást.
                //Szintén újrakezdi a várakozást, ha azért nem sikerül elmozdulnia, mert az adott pozíció nem szabad.
            }}
            room.move(X,Y, toX,toY);
            X = toX;
            Y = toY;
            if(room.getFieldInfo(X-1,Y) instanceof Ball b && !b.isMoving()) { ballX = X-1; ballY = Y-1; dontThrow = 1;
            }else if(room.getFieldInfo(X,Y-1) instanceof Ball b && !b.isMoving()) { ballX = X-1; ballY = Y+1;dontThrow = 3;
            }else if(room.getFieldInfo(X,Y+1) instanceof Ball b && !b.isMoving()) { ballX = X+1; ballY = Y-1;dontThrow = 4;
            }else if(room.getFieldInfo(X+1,Y) instanceof Ball b && !b.isMoving()) { ballX = X+1; ballY = Y+1;dontThrow = 6;
            }//Miután elmozdult, megnézi, hogy valamelyik irányban tőle (most elég csak a 4 fő irányt vizsgálni) található-e labda, és az nem mozog-e.
            if(ballX != -1 && ballY != -1 && room.getFieldInfo(ballX,ballY) instanceof Ball b){//Ha talált labdát, ami nem mozog, akkor eldobja a lehetséges 7 irány közül az egyikbe. (A 8. irányba, vagyis saját maga felé nem dobja el.)
                throwTo = rand.nextInt(7);
                if(throwTo >= dontThrow){throwTo++;}
                switch(throwTo) {
                    case 0:
                        b.throwBall(ballX-1,ballY-1);
                        break;
                    case 1:
                        b.throwBall(ballX-1,ballY);
                        break;
                    case 2:
                        b.throwBall(ballX-1,ballY+1);
                        break;
                    case 3:
                        b.throwBall(ballX,ballY-1);
                        break;
                    case 4:
                        b.throwBall(ballX,ballY+1);
                        break;
                    case 5:
                        b.throwBall(ballX+1,ballY-1);
                        break;
                    case 6:
                        b.throwBall(ballX-1,ballY);
                        break;
                    case 7:
                        b.throwBall(ballX-1,ballY+1);
                        break;
                    default:
                        System.out.println("Player: labdadobás meghiusult");
                        break;

                    //Megpróbál elmozdulni a teremben valamelyik irányba a 8 közül egy lépést. (Semmiképp ne maradjon helyben!)
                    //Ha nem sikerül elmozdulnia, mert az adott helyen fal van, akkor újrakezdi a várakozást.
                    //Szintén újrakezdi a várakozást, ha azért nem sikerül elmozdulnia, mert az adott pozíció nem szabad.
                }}}
        if (room.getFieldInfo(X,Y)==this && !alive){
            room.remove(X,Y);//Ha a játékos már nem aktív (kidobták, vagy győzött), akkor távolítsa el magát a teremből.
        }
    }
    public void end(){//Van egy metódus, amelyet a labda hív meg, amikor kidobják a játékost, valamint ugyanezt használhatja a főprogram is, hogy a győztest leállítsa.
        this.alive = false;
    }
    @Override
    public String toString() {
        return this.nev;
    }
}
