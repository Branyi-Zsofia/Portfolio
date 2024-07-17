from colorama import Fore, Back, Style # type: ignore

table = []
progress = True
def main():
    global table
    global progress
    withhand = [[]]
    load()
#    print(table)
    inbetween()
    doubleTrouble()
    while(len(withhand)!=0):
        while(progress):
            progress = False
            leftAlone()
            killOccupied()
            nextToBlocked()
        print("Current:")
        show()
        withhand = input("X Y Color: ").split()
        if(len(withhand)!=0):
            global table
            progress = True
            table[int(withhand[0])][int(withhand[1])][1] = withhand[2]
            table[int(withhand[0])][int(withhand[1])][2] = True
    print("Current:")
    show()
    greyCounter()

def load():
    f = open ( 'input.txt' , 'r')
    l = []
    l = [ line.split() for line in f]
    print("vertical:" + str(len(l))+ ", horizontal: " + str(len(l[0])))

    for i in range(len(l)):
        temp = []
        for j in range(len(l[0])):
            temp.append([int(l[i][j]), "Grey", False])
#            temp.append(int(l[i][j]))
        table.append(temp)
    for i in range(len(table)-1):
        if len(table[i]) != len(table[i+1]):
            print("Hibás pálya")
    show()
def show ():
    for i in range(len(table)):
        if(i<10):
            print(end = " ")
        print(Back.BLUE + str(i) + Style.RESET_ALL, end = " ")
    print()
    for i in range(len(table)):
        for j in range(len(table[i])):
            value = table[i][j][0]
            color = table[i][j][1]
            num = str(value)
            if(color == "White"):
                print ( Fore.LIGHTWHITE_EX , end = "")
            elif(color == "Grey"):
                print ( Fore.LIGHTBLACK_EX , end = "")
            elif(color == "Black"):
                print ( Fore.GREEN , end = "")
            if(table[i][j][2]):
                print ( Back.BLUE , end = "")
#            if(color == "Green"):
#                print ( Back.GREEN , end = "")
            if(color == "Red"):
                print ( Back.RED , end = "")
            if(value < 10):
                print(end = " ")
            print (num + Style.RESET_ALL , end = " ")
        print(Back.BLUE + str(i) + Style.RESET_ALL)
    for i in range (len(table)-1):
        print("--", end = "-")
    print("--")
    resetChanged()
def resetChanged():
    for i in range(len(table)):
        for j in range(len(table)):
            table[i][j][2] = False
def greyCounter():
    greynum = 0
    for i in range(len(table)):
        for j in range(len(table[i])):
            if(table[i][j][1] == "Grey"):
                greynum = greynum + 1
    print("Visszavan: " + str(greynum) + "darab mező, ami a tábla "+ str(100*(greynum/(len(table)*len(table[0]))))+ "szazaleka-a")

def leftAlone():
    toShow = False
    for i in range(len(table)):
        for j in range(len(table)):
            if(table[i][j][1] == "Grey"):
                isAlone = True
                for k in range(len(table)):
                    if k == i:
                        pass
                    elif (table[k][j][0] == table[i][j][0]) & (table[k][j][1] != "Black" ):
                        isAlone = False
                for l in range(len(table[i])):
                    if l == j:
                        pass
                    elif (table[i][l][0] == table[i][j][0]) & (table[i][l][1] != "Black"):
                        isAlone = False
                if(isAlone):
                    table[i][j][1] = "White"
                    table[i][j][2] = True
                    toShow = True
    if(toShow):
        print("Checking grey tiles if they have same number in column or row, so if not, turn white")
        show()
        global progress
        progress = True
    else:
        print("No luck: Checking grey tiles if they have same number in column or row, so if not, turn white")
def killOccupied():
    toShow = False
    for i in range(len(table)):
        for j in range(len(table)):
            if(table[i][j][1] == "Grey"):
                isAlone = True
                for k in range(len(table)):
                    if k == i:
                        pass
                    elif (table[k][j][0] == table[i][j][0]) & (table[k][j][1] == "White"):
                        isAlone = False
                for l in range(len(table[i])):
                    if l == j:
                        pass
                    elif (table[i][l][0] == table[i][j][0]) & (table[i][l][1] == "White"):
                        isAlone = False
                if(not isAlone):
                    table[i][j][1] = "Black"
                    table[i][j][2] = True
                    toShow = True
    if(toShow):
        print("Checking grey tiles if they have the same number in their row or column fixed to white")
        show()
        global progress
        progress = True
    else:
        print("No luck: Checking grey tiles if they have the same number in their row or column fixed to white")
def inbetween():
    toShow = False
    for i in range(len(table)-1):
        for j in range(len(table)-1):
            if (i==0)|(j==0):
                continue
            if((table[i][j][1] == "Grey") & (table[i-1][j][0] == table[i+1][j][0] | (table[i][j-1][0] == table[i][j+1][0]))):
                table[i][j][1] = "White"
                table[i][j][2] = True
                toShow = True
    if(toShow):
        print("Checking grey tiles if they have the same number in both their sides, so it has to be white")
        show()
        global progress
        progress = True
    else:
        print("No luck: Checking grey tiles if they have the same number in both their sides, so it has to be white")
        show()
def doubleTrouble():
    toShow = False
    for i in range(len(table)-1):
        for j in range(len(table)-1):
            if(table[i][j][0] == table[i+1][j][0]):
                for k in range(len(table)):
                    if (k == i) | (k == i+1) :
                        pass
                    elif (table[k][j][0] == table[i][j][0]) & (table[k][j][1] == "Grey"):
                        table[k][j][1] = "Black"
                        table[k][j][2] = True
                        toShow = True
            if(table[i][j][0] == table[i][j+1][0]):
                for l in range(len(table[i])):
                    if (l == j)|(l == j+1):
                        pass
                    elif (table[i][l][0] == table[i][j][0]) & (table[i][l][1] == "Grey"):
                        table[i][l][1] = "Black"
                        table[i][l][2] = True
                        toShow = True
    if(toShow):
        print("Checking tiles next to each other if they have the same number, so if the same number appears int their line, it has to be black")
        show()
        global progress
        progress = True
    else:
        print("No luck: Checking tiles next to each other if they have the same number, so if the same number appears int their line, it has to be black")
def nextToBlocked():
    toShow = False
    for i in range(len(table)):
        for j in range(len(table[i])):
            if(table[i][j][1]=="Black"):
                if i > 0:
                    if table[i-1][j][1]=="Grey" :
                        table[i-1][j][1] = "White"
                        table[i-1][j][2] = True
                        toShow = True
                if i < len(table)-1:
                    if table[i+1][j][1]=="Grey" :
                        table[i+1][j][1] = "White"
                        table[i+1][j][2] = True
                        toShow = True
                if j > 0:
                    if table[i][j-1][1]=="Grey" :
                        table[i][j-1][1] = "White"
                        table[i][j-1][2] = True
                        toShow = True
                if j < len(table[i])-1:
                    if table[i][j+1][1]=="Grey" :
                        table[i][j+1][1] = "White"
                        table[i][j+1][2] = True
                        toShow = True
    if(toShow):
        print("Checking grey tiles if they have black next, if so, turn white")
        show()
        global progress
        progress = True
    else:
        print("No luck: Checking grey tiles if they have black next, if so, turn white")
def isolatedWhite():
    toShow = False
    for i in range(len(table)):
        for j in range(len(table[i])):
            blockings = 0
            k=0
            l=0
            if(table[i][j][1]=="White"):
                if i > 0:
                    if table[i-1][j][1]=="Black" :
                        blockings = blockings +1
                    else:
                        k=i-1
                        l = j
                if i < len(table)-1:
                    if table[i+1][j][1]=="Black" :
                        blockings = blockings +1
                    else:
                        k=i+1
                        l = j
                if j > 0:
                    if table[i][j-1][1]=="Black" :
                        blockings = blockings +1
                    else:
                        k=i
                        l = j-1
                if j < len(table[i])-1:
                    if table[i][j+1][1]=="Black" :
                        blockings = blockings +1
                    else:
                        k=i
                        l = j+1
            if(blockings==3):
                table[k][l][1] = "White"
                table[k][l][2] = True
                toShow = True
    if(toShow):
        print("Checking grey tiles if they have 3 black next, if so, turn white")
        show()
        global progress
        progress = True
    else:
        print("No luck: Checking grey tiles if they have 3 black next, if so, turn white")
def isolatedGrey():
    toShow = False
    for i in range(len(table)):
        for j in range(len(table[i])):
            blockings = 0
            k=0
            l=0
            if(table[i][j][1]=="Grey"): # TODO
                if i > 0:
                    if table[i-1][j][1]=="White" :
                        blockings = blockings +1
                    else:
                        k=i-1
                        l = j
                if i > 0 &  j > 0:
                    if table[i-1][j-1][1]=="White" :
                        blockings = blockings +1
                    else:
                        k=i-1
                        l = j
                if j > 0:
                    if table[i][j-1][1]=="White" :
                        blockings = blockings +1
                    else:
                        k=i
                        l = j-1
                if j > 0:
                    if table[i+1][j-1][1]=="White" :
                        blockings = blockings +1
                    else:
                        k=i
                        l = j-1
                if i < len(table)-1:
                    if table[i+1][j][1]=="White" :
                        blockings = blockings +1
                    else:
                        k=i+1
                        l = j
                if i < len(table)-1:
                    if table[i+1][j+1][1]=="White" :
                        blockings = blockings +1
                    else:
                        k=i+1
                        l = j
                if j < len(table[i])-1:
                    if table[i][j+1][1]=="White" :
                        blockings = blockings +1
                    else:
                        k=i
                        l = j+1
                if j < len(table[i])-1:
                    if table[i-1][j+1][1]=="White" :
                        blockings = blockings +1
                    else:
                        k=i
                        l = j+1
            if(blockings==3):
                table[k][l][1] = "White"
                table[k][l][2] = True
                toShow = True
    if(toShow):
        print("Checking grey tiles if they have 3 black next, if so, turn white")
        show()
        global progress
        progress = True
    else:
        print("No luck: Checking grey tiles if they have 3 black next, if so, turn white")
























#filevége
if __name__ == "__main__":
    main()
