import os

file_in = input("Enter name of the input file: ")
# if os.path.isfile(os.path.join(os.getcwd(), file_in)): #check if file exists
#     f = open(file_in , "rb")
#     print (f.tell()) # current position
#     f.read(1) # read one byte and move forward
#     print (f.tell()) # current position
#     f.readline() # get bytes from the file until newline
#     #offset -> position of r/w pointer within the file
#     f.seek(-3,2) # move back 3 characters before the end
#     print (f.tell()) # current position
#     f.seek(0) # move back to beginning of file
#     print (f.tell()) # current position
#     f.close() # important if writing to file
# exit(0)

with open(file_in, "r+") as f:
    print(f.tell())
    read_data = f.read()
    print(read_data)

    f.seek(0)
    for line in f:
        print(line)

    ####WRITING TO A FILE#####
    f.writelines("Written to file on Friday, 9/4/20\n")

#####PART 2: JSON
import json

zipCodes = [60290, 60601, 60602, 60603, 60604, 60605, 60606]
#dump into a file
f = open("example2.json", "w") #overwrite existing file or create a new file
json.dump(zipCodes,f)
f.close()
#load back into Python
f = open("example2.json", "r")
zipCodes2 = json.load(f)
f.close()
#compare
print("CHECKING ZIPCODES...")
print(zipCodes == zipCodes2)
#exit(0)

##########PART 3###########
from requests import get

print("Downloading JSON file and printing entire file:")
response = get("http://db.cs.pitt.edu/courses/cs1656/data/hours.json", verify=False)
print(response.content)

print("Loading as Json and iterating one line at a time:")
hours = json.loads(response.content)
print(hours)

print("\nIterating over JSON:")
for line in hours:
    print(line)