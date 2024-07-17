import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Empty {
    @Override //Ez az osztály csak azért jobb, mint a sima Object(), mert át tudjuk írni a toString() metódusát, hogy egy szóközt írjon ki.
    public String toString() {
            return " ";
    }
}
