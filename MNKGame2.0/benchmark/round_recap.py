import sys

def main():
    with open(sys.argv[1]) as file:
        lines = file.readlines()
        mylines = lines[-6:]
        print("\n**** ROUND SCORE *****")
        for l in mylines:
            if(l != "\n"):
                print(l, end="")
        print("*********************\n")
        exit()



if __name__ == "__main__":
    main()