#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>
#include <sys/time.h>
#include <unistd.h>
#include <signal.h>
#include <sys/msg.h>
#include <wait.h>
#include <sys/wait.h>
#include <errno.h>
#include <fcntl.h>
#include <mqueue.h>
#include <sys/sem.h>
#include <sys/shm.h>
#include <sys/stat.h>
#include <sys/types.h>
#define MAX_STRING_LENGTH 200


struct message{
     long pipeID;
     char mtext [1024]; 
}; 
void handler(int signalNumber){
    // lefut ha olyan szignált kapott, amire feliratkoztattuk a folyamatot
    // ha egy signalra nincs beállítva handler, akkor a program meghal
}




// DECLARE
int parent(pid_t pid)   {return pid > 0;}
int child(pid_t pid)    {return pid == 0;}
int forkerror(pid_t pid){return pid < 0;}

int randomNum02(){
    int szam = rand()%3;
    return szam;
}

int send(int mq, char* msg)
{ // tetszőlegesen módosítható, ez most éppen paraméterként vár egy mq-t és egy stringet, amit elküld mq-n
    int pipeID = 5; // prioritás, alacsonyabb prioritásút hamarabb olvasunk
    struct message ms;
    ms.pipeID = pipeID;
    strcpy(ms.mtext, msg);
    int status;
    int size = strlen(ms.mtext)+1; // +1 a '\0' miatt biztos ami biztos 

    status = msgsnd(mq, &ms, size, 0);
    if ( status < 0 ) 
        perror("msgsnd");
    return 0;
}
struct message receive(int mq)
{
    struct message ms;
    int status;
    int pipeID = 0;
    int id = 0; // ha nulla akkor a legkisebb pipeID-ből olvas
    status = msgrcv(mq, &ms, MAX_STRING_LENGTH+1, pipeID, id);
    
    if (status < 0)
        perror("msgsnd");
    return ms;
} 

int main(int argc, char* argv[]){
    srand(time(NULL));
/*OSZTOTT MEMORIA
    key_t key2 = ftok(argv[0],37); //jovanazugy, innen osztott
    int shm = shmget(key2, 100, S_IRUSR | S_IWUSR | IPC_CREAT); // 100 hosszú shared memory
*/
/*SZEMAFOR
    int sem = semget(key2, 1, S_IRUSR | S_IWUSR | IPC_CREAT); // eh
    semctl(sem, 0, SETVAL, 0) ; // 
*/
    int pipefd[2];
    if (pipe(pipefd) == -1){ // bárhányszor forkolunk mindegyik process látni fogja
        perror("Error at opening pipe!");
        exit(-1);
    }
    int pipefd2[2];
    if (pipe(pipefd2) == -1){ // bárhányszor forkolunk mindegyik process látni fogja
        perror("Error at opening pipe!");
        exit(-1);
    }

    int mq, status;
    key_t key;
    key = ftok(argv[0], 1); // fájl elérési útjád adjuk át (argv[0])
    mq = msgget(key, 0600 | IPC_CREAT); // ezt is látja az összes process, ha ezután van a fork
    if(mq < 0){
        perror("Error at creating message queue");
        exit(1);
    }

    pid_t pid = fork();
    if(forkerror(pid)){
        perror("Miscarriage");
        exit(1);
    }
    
    if(parent(pid)){
        pid_t pid2 = fork(); // belső fork törölhető, ha csak egy gyerek kell
        if(forkerror(pid2)){
        perror("Miscarriage");
        exit(1);
        }
    
        if(parent(pid2)){
            // UEFA

            close(pipefd[0]); // bezárjuk az olvasó végét a pipe-nak, ha tudjuk, hogy nem fogunk olvasni ebben az ágban
            close(pipefd2[0]); // már nem akarunk írni, lezárjuk az író véget
          
            signal(SIGUSR1, handler); // ha SIGUSR1 jelzést kap, fusson le a handler
            pause();
            pause(); // várjunk egy signalt, ha van rá handler, akkor futhat tovább, ha nincs  aprogram meghal
            printf("UEFA: Mindket fel kesz a konzultaciora\n");

            char kerdes[MAX_STRING_LENGTH] = "Milyen szinu mezben léptek palyara a terveitek szerint es hany jegyet kertek a szurkoloitoknak?\n";
            write(pipefd[1],kerdes,MAX_STRING_LENGTH); // elküldjük a szöveget, pipefd[1]-en
            close(pipefd[1]); // már nem akarunk írni, lezárjuk az író véget
            write(pipefd2[1],kerdes,MAX_STRING_LENGTH); // elküldjük a szöveget, pipefd[1]-en
            close(pipefd2[1]); // már nem akarunk írni, lezárjuk az író véget
                   
            struct message igenyek = receive(mq);
            printf(igenyek.mtext);
            igenyek = receive(mq);
            printf(igenyek.mtext);
            igenyek = receive(mq);
            printf(igenyek.mtext);
            igenyek = receive(mq);
            printf(igenyek.mtext);

/*szemafor
            struct sembuf muvelet = {.sem_num = 0, .sem_op = -1, .sem_flg = 0}; //
            semop(sem, &muvelet, 1); //
*/
/*osztott
            char* shared = shmat(shm,NULL,0); //shared az ajtó az osztotthoz mostantól
            int napi_uj_fertozottek_szama; //  idefogjuk rakni amit osztottbol olvasunk
            memcpy(&napi_uj_fertozottek_szama, shared, sizeof(int)); //plagizáljuk
            shmdt(shared); //mégse ajtó
            printf("napi uj fertozottek szama: %d\n", napi_uj_fertozottek_szama); //
   
            shmctl(shm, IPC_RMID, NULL); // osztott memori to kuka
*/
//szemaforvége            semctl(sem, 0, IPC_RMID); //szemafor to kuka

            wait(NULL); // vár minden gyerek folyamatra, hogy végezzenek (pid2-es childra)



        }
        if(child(pid2)){
            // Real Madrid
        close(pipefd[0]); // már nem akarunk írni, lezárjuk az író véget
        close(pipefd[1]); // bezárjuk az olvasó végét a pipe-nak, ha tudjuk, hogy nem fogunk olvasni ebben az ágban
        sleep(1); // sleep azért, hogy biztosan elérjen a parent a pause()-ig
        kill(getppid(),SIGUSR1); // lekérjük a szülű azonosítóját(processID), ami alapján signalt tudunk küldeni

        char kerdes[MAX_STRING_LENGTH]; // ide fogunk beolvasni pipe-ból
        read(pipefd2[0], kerdes, MAX_STRING_LENGTH); // kiolvasunk egy szöveget (lehet akár struct is, de akkor kell a méret)
        printf("Real Madridtol kerdezik: %s", kerdes);
        close(pipefd2[0]); // bezárjuk az olvasó végét a pipe-nak, ha tudjuk, hogy nem fogunk olvasni ebben az ágban
        switch (randomNum02())
        {
        case 0:
            send(mq, "Real Madrid szinei: sarga-fekete\n");
            break;
        case 1:
            send(mq, "Real Madrid szinei: piros-feher\n");
            break;
        case 2:
            send(mq, "Real Madrid szinei: zold-feher\n");
            break;
        
        default:
            break;
        }
        switch (randomNum02())
        {
        case 0:
            send(mq, "Real Madrid szurkoloi: 5 ezer\n");
            break;
        case 1:
            send(mq, "Real Madrid szurkoloi: 7 ezer\n");
            break;
        case 2:
            send(mq, "Real Madrid szurkoloi: 12 ezer\n");
            break;
        
        default:
            break;
        }

            
/*osztott mem
            char* shared = shmat(shm,NULL,0); // shared változó mostantól osztott
            int napi_uj_fertozottek_szama = 900; // megadott osztott bigyo tárolása
            // strcpy(shared, napi_uj_fertozottek_szama) suffni version, hátha
            memcpy(shared, &napi_uj_fertozottek_szama, sizeof(int)); //sizeof mit küldünk
            shmdt(shared); // shared már nem osztott
*/
/*szemarfor
            struct sembuf muvelet = {.sem_num = 0, .sem_op = 1, .sem_flg = 0}; //
            semop(sem, &muvelet, 1); //
*/
            
            exit(0); // TERMINÁL A CHILD
        }
    }
    if(child(pid)){
        // Ellenfel1 Dortmund
        close(pipefd2[0]); // már nem akarunk írni, lezárjuk az író véget
        close(pipefd2[1]); // bezárjuk az olvasó végét a pipe-nak, ha tudjuk, hogy nem fogunk olvasni ebben az ágban
        sleep(2); // sleep azért, hogy biztosan elérjen a parent a pause()-ig
        kill(getppid(),SIGUSR1); // lekérjük a szülű azonosítóját(processID), ami alapján signalt tudunk küldeni

        char kerdes[MAX_STRING_LENGTH]; // ide fogunk beolvasni pipe-ból
        read(pipefd[0], kerdes, MAX_STRING_LENGTH); // kiolvasunk egy szöveget (lehet akár struct is, de akkor kell a méret)
        printf("Dortmundtol kerdezik: %s", kerdes);
        close(pipefd[0]); // bezárjuk az olvasó végét a pipe-nak, ha tudjuk, hogy nem fogunk olvasni ebben az ágban
        

        switch (randomNum02())
        {
        case 0:
            send(mq, "Dortmund szinei: fekete-sarga\n");
            break;
        case 1:
            send(mq, "Dortmund szinei: feher-piros\n");
            break;
        case 2:
            send(mq, "Dortmund szinei: feher-zold\n");
            break;
        
        default:
            break;
        }
        switch (randomNum02())
        {
        case 0:
            send(mq, "Dortmund szurkoloi: 8 ezer\n");
            break;
        case 1:
            send(mq, "Dortmund szurkoloi: 10 ezer\n");
            break;
        case 2:
            send(mq, "Dortmund szurkoloi: 15 ezer\n");
            break;
        
        default:
            break;
        }

        exit(0); // TERMINÁL A CHILD
    }
    
    

    status = msgctl(mq, IPC_RMID, NULL); // megpróbáljuk kitörölni a message queue-t
    if(status < 0){
        perror("Failed to delete message queue");
    }
}
