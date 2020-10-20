#This Python Script will utilizes mathplotlib to generate graphs for the simulation of the LRU, OPT, and Second Chance algorithms
"""
Developed by: Gordon Lu
For: Dr. Mosse's Operating Systems Class
"""

import matplotlib.pyplot as plt 

#Read from requested file:
fName = input("Enter the desired trace file: ")
fHand = open(fName, "r")

#Number of Frames as a list
numFrames = []
if("beladys" in fName):
    for i in range(101):
        if(i % 2 == 0 and i >= 2):
            numFrames.append(i)
        i += 2
else:
    numFrames = [8, 16, 32, 64]
print(numFrames)

results = [[]]

opt_results = []
lru_results = []
second_chance_results = []

gcc_results = []
gzip_results = []
swim_results = []

traceName = []

count = 0
for line in fHand:
    traceName.append(line[0:line.index(":")])
    line = line[line.index(":") + 1:]
    results.append(list(map(int, line.split())))

#Opt, LRU, and Second Chance Results:
if("beladys" in fName):
    gcc_results = results[1]
    gzip_results = results[2]
    swim_results = results[3]
    #Now Start Graphing:
    plt.plot(numFrames, gcc_results, label = "gcc")
    plt.plot(numFrames, gzip_results, label = "gzip")
    plt.plot(numFrames, swim_results, label = "swim")
else:
    opt_results = results[1]
    lru_results = results[2]
    second_chance_results = results[3]
    #Now Start Graphing:
    plt.plot(numFrames, opt_results, label = "OPT")
    plt.plot(numFrames, lru_results, label = "LRU")
    plt.plot(numFrames, second_chance_results, label = "Second Chance")

"""print('Opt results: ', opt_results)
print('LRU results: ', lru_results)
print('Second Chance results: ', second_chance_results)"""


plt.xlabel("Number of Page Frames")
plt.ylabel("Number of Page Faults")

titleName = ""
if("gcc" in fName):
    titleName = "Page Fault Statistics for gcc.trace"
elif("gzip" in fName):
    titleName = "Page Fault Statistics for gzip.trace"
elif("swim" in fName):
    titleName = "Page Fault Statistics for swim.trace"
elif("beladys" in fName):
    titleName = "Second Chance Performance Along Trace Files"
#Display Title
plt.title(titleName)
#Show the Legend
plt.legend()
#Display the plot
plt.show()

