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
        # print(logfile)
        match = re.findall("(Win.*:.*\n|Draws:.*\n)",logfile)
        first_player_name = match[0][4:-4]
        first_wins = 0
        second_player_name = match[1][4:-4]
        second_wins = 0
        draws = 0
        # print(match)
        for m in match:
            if first_player_name in m:
                first_wins += int(m[-2])
            elif second_player_name in m:
                second_wins += int(m[-2])
            else:
                draws += int(m[-2])
        logfile = logfile.split('\n')
        r = re.compile(f"(^{first_player_name}.*|^{second_player_name}.*)")
        match = list(filter(r.match, logfile)) # Read Note below
        first_score = 0
        second_score = 0
        for m in match:
            if first_player_name in m:
                first_score += int(m.replace(first_player_name, ""))
            elif second_player_name in m:
                second_score += int(m.replace(second_player_name, ""))
        
        print(f"Win {first_player_name}: {first_wins}")
        print(f"Win {second_player_name}: {second_wins}")
        print(f"Draws: {draws}")
        print(f"Score {first_player_name}: {first_score}")
        print(f"Score {second_player_name}: {second_score}")

        if(first_score > second_score):
            print(f"WINNER: {first_player_name}")
        elif(first_score < second_score):
            print(f"WINNER: {second_player_name}")
        else:
            print("DRAW!")

if __name__ == "__main__":
    main()
