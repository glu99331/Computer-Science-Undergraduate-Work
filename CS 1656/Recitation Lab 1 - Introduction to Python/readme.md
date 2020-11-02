# Repository: template.rec01
# Recitation #1: Python Tutorial

> Course: **[CS 1656 - Introduction to Data Science](http://cs1656.org)** (CS 2056) -- Fall 2020  
> Instructors: [Alexandros Labrinidis](http://labrinidis.cs.pitt.edu) 
> Teaching Assistant: Evangelos Karageorgos
>
> Assignment: #1. Python Tutorial
> Released: September 3, 2020  
> **Due: September 8th, 2020**

### Description
This is the **first lab** for the CS 1656 -- Introduction to Data Science (CS 2056) class, for the Fall 2020 semester.

### Goal
The goal of this assignment is to familiarize you with the Python programming language.

### What to do -- task.py
You are asked to implement parts #4, #5, #6 and #7 from the recitation handout. The program skeleton is already in place, you just need to implement the methods.


### def part4(self)
For this part, you must use `self.hours` we defined in part 3, and store its data as a CSV file (hours.csv), with the fields `name`, `day` and `time`. You should look up the `csv` module and the `writer()` function in particular. The command to open the csv file for writing is already in the template, so, don't change it.

### def part5(self)
For this part, you must open the CSV file created from part 4, read its contents, and write them in the file `part5.txt`.

### def part6(self)
For this part, you must open the CSV file again, but this time you must parse it using `csv.reader()`, and write only the rows, one row at a time, in the file `part6.txt`.

### def part7(self)
For this part, you must open the CSV file again, parse it using `csv.reader()`, iterate through the rows, and write every cell, one cell at a time, in the file `part7.txt`.


### Important notes about grading
It is absolutely imperative that your python program:  
* runs without any syntax or other errors (using Python 3)  
* strictly adheres to the format specifications for input and output, as explained above.     

Failure in any of the above will result in **severe** point loss.


### Allowed Python Libraries (Updated)
You are allowed to use the following Python libraries (although a fraction of these will actually be needed):
```
argparse
collections
csv
json
glob
math
os
pandas
re
requests
string
sys
time
xml
```
If you would like to use any other libraries, you must ask permission within a maximum of one week after the assignment was released, using [canvas](http://cs1656.org).


### How to submit your assignment
We are going to use Gradescope to submit and grade your assignments. 

To submit your assignment:
* login to Canvas for this class <https://cs1656.org>  
* click on Gradescope from the menu on the left  
* select "Assignment #0" from the list of active assignments in Gradescope
* follow the instructions to submit your assignment and have it automatically graded.

### What to submit
For this test assignment you only need to submit `task.py` to "Assignment #0" and see if you get all 100 points. In case of an error or wrong result, you can modify the file and resubmit it as many times as you want until the deadline of **Tuesday, September 8, 11:59 pm**.

### Late submissions
For full points, we will consider the version submitted to Gradescope 
* the day of the deadline **Tuesday, September 8, 11:59 pm**  
* 24 hours later (for submissions that are one day late / -5 points), and  
* 48 hours after the first deadline (for submissions that are two days late / -15 points).

Our assumption is that everybody will submit on the first deadline. If you want us to consider a late submission, you need to email us at `cs1656-staff@cs.pitt.edu`

### Important notes about grading
It is absolutely imperative that your python program:  
* runs without any syntax or other errors (using Python 3)  
* strictly adheres to the format specifications for input and output, as explained above.     
Failure in any of the above will result in **severe** point loss. 

### About your github account
Since we will utilize the github classroom feature to distribute the assignments it is very important that your github account can do **private** repositories. If this is not already enabled, you can do it by visiting <https://education.github.com/>  