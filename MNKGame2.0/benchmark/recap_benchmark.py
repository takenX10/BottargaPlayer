import re
import sys

def main():
    try:
        filename = sys.argv[1]
    except:
        print("ERROR: filename missing!")
        print("Usage: python3 benchmark_parser.py logfile.txt")
        exit()
    with open(filename) as file:
        logfile = ''.join(file.readlines())
        scores = logfile.split("**** FINAL SCORE ****")[1:]
        i = 0
        for score in scores:
            i+= 1
            if(i%2 == 0):
                conf = re.findall("Game type.*\n",score)[0].split(": ")[1][:-1]
                print(f'\n\n----- Configurazione: {conf} ----')
                print( " *** Round 1 ***")
            else:
                print( "\n *** Round 2 ***")
            print('\n'.join(score.split('\n\n')[:2]))

if __name__ == "__main__":
    main()


