import random



root = "5000"


f = open("million_instruction", "w")

nodes = set()
for i in range(10 ** 5):
    k = random.randint(0,10** 5)
  
    while k in nodes:
        k = random.randint(0,10** 5)
        
    nodes.add(k)
    
    f.write("ADDNODE " + str(k) + "\n")

for i in range(10 ** 4):
    n1 = random.sample(nodes,  1 )
    n2 = random.sample(nodes , 1)

    if(n1[0] not in nodes or n2[0] not in nodes):
        print("pog")
    if n1 == n2:
        continue
    f.write("SEND " + str(n1[0]) + " " + str(n2[0])+ "\n")

f.close()