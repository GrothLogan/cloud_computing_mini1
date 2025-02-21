path = input("enter output directory no / at the end: ")
file = open(path+"/part-r-00000")
lines = file.readlines()
temp = {}
for line in lines:
    key, value = line.split()
    temp[key] = float(value)
temp = dict(sorted(temp.items(), key=lambda item: item[1], reverse=True))
i = 0
for key,value in temp.items():
    if i >= 10:
        break
    print(key," ",value)
    i = i +1
    
    