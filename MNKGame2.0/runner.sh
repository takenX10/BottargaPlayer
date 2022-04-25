
javac -d source mnkgame/*.java bottargaPlayer2/*.java bottargaPlayer3/*.java bottargaPlayer1/*.java bottargaPlayer_old/*.java

cd source

java mnkgame.MNKPlayerTester 3 3 3 BottargaPlayer.PlayerNegamax.Player BottargaPlayer.PlayerMoveOrder.Player -v -t 10 -r 1

