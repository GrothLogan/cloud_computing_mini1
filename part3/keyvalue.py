path = input("enter output directory no / at the end: ")
file = open(path+"/part-r-00000")
lines = file.readlines()
temp = {}
for line in lines:
    key, value = line.split()
    temp[key] = float(value)
item = input("enter the key to get value: ")

print(temp[item])