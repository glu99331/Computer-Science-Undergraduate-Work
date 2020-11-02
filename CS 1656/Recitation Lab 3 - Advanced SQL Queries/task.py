import sqlite3 as lite
import csv
import re
import pandas as pd

#Lu, Gordon: CS 1656 Fall 2020 Recitation Lab 3 -> Passes Gradescope!
class Task(object):
    def __init__(self, db_name):
        self.con = lite.connect(db_name)
        self.cur = self.con.cursor()

    #q0 is an example 
    def q0(self):
        query = '''
        SELECT COUNT(*) FROM students
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows
    
    def q1(self):
        query = '''
        SELECT firstName, lastName 
        FROM STUDENTS
        WHERE yearStarted = 2019
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows
        

    def q2(self):
        query = '''
        SELECT s.firstName, s.lastName
        FROM STUDENTS s 
        NATURAL JOIN MAJORS m 
        WHERE m.major == 'CS' or m.major == 'COE'
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q3(self):
        query = '''
        SELECT COUNT(sid)
        FROM MAJORS m
        WHERE m.major == 'ASTRO'
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows


    def q4(self):
        query = '''
        SELECT firstName, lastName, yearStarted, COUNT(credits)
        FROM STUDENTS s
        NATURAL JOIN GRADES g
        WHERE g.grade >= 1
        GROUP BY sid
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q5(self):
        query = '''
        SELECT professor, COUNT(number)
        FROM Courses c
        GROUP BY c.professor
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q6(self):
        query = '''
        SELECT cid, grade, count(g.grade) as c
        FROM Grades g
        GROUP BY cid, g.grade   
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q7(self):
    
        query = '''
        SELECT * FROM(
        SELECT cid, grade, count(g.grade) as c
        FROM Grades g
        GROUP BY cid, g.grade) ta
        WHERE ta.grade == 4
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

if __name__ == "__main__":
    task = Task("students.db")
    rows = task.q0()
    print(rows)
    print()
    rows = task.q1()
    print(rows)
    print()
    rows = task.q2()
    print(rows)
    print()
    rows = task.q3()
    print(rows)
    print()
    rows = task.q4()
    print(rows)
    print()
    rows = task.q5()
    print(rows)
    print()
    rows = task.q6()
    print(rows)
    print()
    rows = task.q7()
    print(rows)
    print()