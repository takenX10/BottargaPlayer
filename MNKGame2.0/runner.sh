
javac -d source mnkgame/*.java bottargaPlayer2/*.java bottargaPlayer1/*.java bottargaPlayer_old/*.java

cd source

java mnkgame.MNKPlayerTester 3 3 3 mnkgame.Solution bottargaPlayer1.negamaxPlayer -v -t 10 -r 1

