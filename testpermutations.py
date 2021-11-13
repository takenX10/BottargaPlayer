import math

spazi = 49
tot  =0
for i in range(1,spazi,1):
        
    nx = math.ceil(i/2)
    # no == nx || no == nx -1 
    no = math.floor(i/2)
    empty = spazi - nx - no
    print(math.factorial(spazi)// (math.factorial(nx) * math.factorial(no) * math.factorial(empty)))