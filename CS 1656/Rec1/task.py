import os
from requests import get
import json
import csv
import ssl

#Lu, Gordon: CS 1656 Fall 2020 Recitation Lab 1 -> Passes Gradescope!
class Task(object):
    def __init__(self):
        self.response = get('http://db.cs.pitt.edu/courses/cs1656/data/hours.json', verify=False) 
        self.hours = json.loads(self.response.content) 
        # print(self.hours)
    def part4(self):
        #write output to hours.csv
        filename  = "hours.csv"
        field_names = ["name", "day", "time"]

        with open(filename, "w", newline ='') as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames = field_names)
            writer.writeheader()
            for data in self.hours:
                writer.writerow(data)


    def part5(self):
        #write output to 'part5.txt'
        f = open('part5.txt', 'w') 
        with open("hours.csv", newline='') as c:
            reader = csv.reader(c)
            for row in reader:
                s = ','.join(row)
                f.write(s + "\n")
                print(s)
                # f.write(str(row))
                    # f.write(row)   
        f.close() 

    def part6(self):
        #write output to 'part6.txt'
        f = open('part6.txt', 'w') 
        with open("hours.csv", newline='') as c:
            reader = csv.reader(c)
            for row in reader:
                f.write(str(row))
                

    def part7(self):
        #write output to 'part7.txt'
        f = open('part7.txt', 'w') 
        with open("hours.csv", newline='') as c:
            reader = csv.reader(c)
            for row in reader:
                f.write(''.join(row))


if __name__ == '__main__':
    task = Task()

    task.part4()
    task.part5()
    task.part6()
    task.part7()