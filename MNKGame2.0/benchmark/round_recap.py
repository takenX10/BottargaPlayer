###############
#             #
#   ( )   _   #
#  (_` )_('>  #
#  (__,~_)8   #
#    _YY_     #
###############
#
# Use:          this is just for the benchmark.sh file, don't use it
#
# Disclaimer:   this file is not meant to be bulletproof, I made it quickly just to get the benchmarker done
#               so use it at your own risk

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