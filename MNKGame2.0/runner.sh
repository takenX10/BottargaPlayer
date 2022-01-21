
javac -d source mnkgame/*.java bottargaPlayer2/*.java bottargaPlayer1/*.java bottargaPlayer_old/*.java

cd source

java mnkgame.MNKPlayerTester 8 8 4 bottargaPlayer1.negamaxPlayer mnkgame.Solution -v -t 10 -r 1

