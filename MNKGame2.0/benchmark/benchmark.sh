#!/bin/bash
if [ "$#" -ne 3 ];
then
    echo "Usage: ./benchmark.sh mnkgame.player1 mnkgame.player2 logname.txt"
    exit 1
fi

time=10
round_per_type=2
tempfile=/tmp/tempfile.txt
var1=$1
var2=$2
var3=../benchmark/benchmark_logs/$3

# $1-> game configuration
start_round(){
    echo " -------------- Starting $1 ----------------"
    start_round=$SECONDS
    java mnkgame.MNKPlayerTester $1 $var1 $var2 -v -t $time -r $round_per_type > $tempfile 2> >(grep -v "^Picked up _JAVA_OPTIONS:" >&2)
    local duration=$(( SECONDS - start_round ))
    echo "# End of first round, round duration: $duration seconds"
    echo "# End of first round, round duration: $duration seconds" >> $var3
    cat $tempfile >> $var3
    python3 ../benchmark/round_recap.py $tempfile

    start_round=$SECONDS
    java mnkgame.MNKPlayerTester $1 $var2 $var1 -v -t $time -r $round_per_type > $tempfile 2> >(grep -v "^Picked up _JAVA_OPTIONS:" >&2)
    local duration=$(( SECONDS - start_round ))
    echo "# End of second round, round duration: $duration seconds"
    echo "# End of second round, round duration: $duration seconds" >> $var3
    cat $tempfile >> $var3
    python3 ../benchmark/round_recap.py $tempfile
    echo " ----------------------------------------------"
    local duration=$(( SECONDS - $BENCHMARK_START ))
    echo ""
    echo " Total Elapsed time: $duration seconds!"
    echo ""
}

quit_script(){
    cd ../benchmark
    python3 benchmark_parser.py $var3
    exit 1   
}

validator_benchmark(){
    start_round "3 3 3" 
    start_round "4 3 3" 
    start_round "4 4 3" 
    start_round "4 4 4"
}

partial_benchmark(){
    start_round "3 3 3" 
    start_round "4 3 3" 
    start_round "4 4 3" 
    start_round "4 4 4" 
    start_round "5 4 4" 
    start_round "5 5 4"  
    start_round "5 5 5"  
    start_round "6 4 4"  
    start_round "6 5 4"  
    start_round "6 6 4"
}

big_benchmark(){  
    start_round "8 8 4"  
    start_round "10 10 5"
    start_round "50 50 10"
    start_round "70 70 10"
    start_round "7 7 5"  
    start_round "7 7 6"  
    start_round "7 7 7"
}

full_benchmark(){
    start_round "3 3 3" 
    start_round "4 3 3" 
    start_round "4 4 3" 
    start_round "4 4 4" 
    start_round "5 4 4" 
    start_round "5 5 4"  
    start_round "5 5 5"  
    start_round "6 4 4"  
    start_round "6 5 4"  
    start_round "6 6 4"  
    start_round "6 6 5"  
    start_round "6 6 6"  
    start_round "7 4 4"  
    start_round "7 5 4"  
    start_round "7 6 4"  
    start_round "7 7 4"  
    start_round "7 5 5"  
    start_round "7 6 5"  
    start_round "7 7 5"  
    start_round "7 7 6"  
    start_round "7 7 7"  
    start_round "8 8 4"  
    start_round "10 10 5"
    start_round "50 50 10"
    start_round "70 70 10"
}

cd ..

# Compile the sources
javac -d source bottargaPlayer1/*.java bottargaPlayer_old/*.java mnkgame/*.java 2> >(grep -v "^Picked up _JAVA_OPTIONS:" >&2)

cd source
# Create the temp file
touch $tempfile

# Clear the log file in case it already exist
echo "" > $var3

echo ""
echo " Benchmark by takenX10!!!"
BENCHMARK_START=$SECONDS

# start_round "3 3 3"

full_benchmark

quit_script
