import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Room { //Room: a játékosok és a labda helyzetét ábrázolja.

    private final int X;
    private final int Y;//Tartalmazza a szélességét és a hosszúságát, amit a konstruktor paramétereként kell átadni.
    private Object[][] room; //Tárol egy objektumokból álló mátrixot, amelyben azt tartjuk nyilván, hogy az adott pozíción játékos van, labda, vagy semmi sincs.
    private int playerNum;//Tárolja a pályán levő játékosok számát.
    public Room(int x, int y, int num){//A konstruktora inicializálja a mátrixot, és feltölti üres helyet ábrázoló objektumokkal.
        X = x;
        Y = y;
        playerNum = num;
        room = new Object[X][Y];
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                room[i][j] = new Empty();
            }
        }
    }
    public int getX() {return X;}
    public int getY() {return Y;}//Van egy lekérdező metódusa a szélességének és a hosszúságának a lekérdezésére.

    public void show() {//Van egy kirajzoló metódusa, amely minden egyes pozícióra vagy szóközt ír ki (üres hely), vagy egy o karaktert, ha ott labda van, vagy pedig egy játékos betűjelét.
        System.out.println("\033[H\033[2J");
        System.out.println("\u001B[0;0H");//A látványosság érdekében érdemes az összes kiírás előtt a képernyőt törölni egy "\033[H\033[2J" karakterlánc kiírásával, majd minden kiírás előtt a kurzort a bal felső sarokba mozgatni a "\u001B[0;0H" kiírásával.
        System.out.println("+----------+");
        for (int i = 0; i < X; i++) {
            System.out.print("|");
            for (int j = 0; j < Y; j++) {
                System.out.print(room[i][j]);
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("+----------+");
    }
    public Object getFieldInfo(int x, int y) { //Le lehet kérdezni egy adott pozíción található objektumot.
        if(x<0 || x>9 || y<0 || y>9){return -1;}else
        {return room[x][y];}
    }
    public int getPlayerNum(){return playerNum;}//Le lehet kérdezni az aktív játékosok számát.
    public void setField(int x, int y, Object object) {//El lehet helyezni egy objektumot egy adott pozícióra. (Nem ennek az osztálynak a feladata annak ellenőrzése, hogy az adott pozíció üres-e.) Ha az objektum játékos, akkor növekszik az aktív játékosok száma.
        room[x][y] = object;
    }
    public void move(int x, int y, int tox, int toy) {//Át lehet mozgatni egy objektumot egyik pozícióról egy másikra. (Nem ennek az osztálynak a feladata annak ellenőrzése, hogy az új pozíció üres-e.)
        room[tox][toy] = room[x][y];
        room[x][y] = new Empty();
    }
    public void remove(int x, int y){//El lehet távolítani egy objektumot egy adott pozícióról. Ha az objektum játékos, akkor csökken az aktív játékosok száma.
        if(room[x][y] instanceof Player p){
            this.playerNum -= 1;
            p.end();
        }
        room[x][y] = new Empty() ;
    }
}
