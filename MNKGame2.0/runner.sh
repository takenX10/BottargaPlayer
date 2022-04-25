
javac -d source BottargaPlayer/*/*.java mnkgame/*.java

cd source

java mnkgame.MNKPlayerTester 3 3 3 BottargaPlayer.PlayerNegamax.Player BottargaPlayer.PlayerMoveOrder.Player -v -t 10 -r 1

