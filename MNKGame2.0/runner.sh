
javac -d source mnkgame/*.java bottargaPlayer2/*.java bottargaPlayer1/*.java bottargaPlayer_old/*.java

cd source

java mnkgame.MNKPlayerTester 4 4 4 mnkgame.Solution bottargaPlayer2.transpositionPlayer -v -t 10000 -r 1

