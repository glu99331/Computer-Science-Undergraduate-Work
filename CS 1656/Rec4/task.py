import sqlite3 as lite

import csv
import re
import pandas as pd
import pandas
from sqlalchemy import create_engine

#Lu, Gordon: CS 1656 Fall 2020 Recitation Lab 4 -> Passes Gradescope!
class Task(object):
    def __init__(self, db_name, students, grades, courses, majors):
        self.con = lite.connect(db_name)
        self.cur = self.con.cursor()

        self.cur.execute('DROP TABLE IF EXISTS Courses')
        self.cur.execute("CREATE TABLE Courses(cid INT, number INT, professor TEXT, major TEXT, year INT, semester TEXT)")

        self.cur.execute('DROP TABLE IF EXISTS Majors')
        self.cur.execute("CREATE TABLE Majors(sid INT, major TEXT)")

        self.cur.execute('DROP TABLE IF EXISTS Grades')
        self.cur.execute("CREATE TABLE Grades(sid INT, cid INT, credits INT, grade INT)")

        self.cur.execute('DROP TABLE IF EXISTS Students')
        self.cur.execute("CREATE TABLE Students(sid INT, firstName TEXT, lastName TEXT, yearStarted INT)")

        engine = create_engine("sqlite:///"+db_name)
        df1 = pd.read_csv(students)
        df1.to_sql('students', engine, if_exists='append', index=False)

        df2 = pd.read_csv(grades)
        df2.to_sql('grades', engine, if_exists='append', index=False)

        df3 = pd.read_csv(courses)
        df3.to_sql('courses', engine, if_exists='append', index=False)

        df4 = pd.read_csv(majors)
        df4.to_sql('majors', engine, if_exists='append', index=False)

        self.cur.execute("DROP VIEW IF EXISTS allgrades")
        self.cur.execute("""
        create view allgrades as
        SELECT s.firstName, s.lastName, m.major as ms, 
               c.number, c.major as mc, g.grade 
        FROM students as s, majors as m, grades as g, courses as c
        WHERE s.sid = m.sid AND g.sid = s.sid AND g.cid = c.cid
        """)


    #q0 is an example  
    def q0(self):
        query = '''
            SELECT * FROM students
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q1(self):
        #Students: (sid, firstName, lastName, yearStarted)
        #Majors: (sid, major)
        #Grades: (sid, cid, credits, grade)
        #Courses: (cid, number, progressor, major, year, semester)

        #SUBQUERY:
        #First get the students with courses, and grade 
        # self.cur.execute("DROP VIEW IF EXISTS q1")
        # self.cur.execute( '''
        #     CREATE VIEW q1 AS
        #     SELECT * 
        #     FROM STUDENTS 
        #     NATURAL JOIN GRADES 
        #     NATURAL JOIN COURSES
        # ''')
        # query = ''' 
        # SELECT sid, year, semester, COUNT(grade)
        # FROM q1
        # WHERE grade > 0
        # GROUP BY sid, semester, year
        # ORDER BY sid , semester , year DESC
        # '''
        query = '''
        SELECT sid, year, semester, COUNT(*) 
        FROM COURSES NATURAL JOIN GRADES 
        WHERE grade > 0
        GROUP BY sid, year, semester
        ORDER BY sid, year, semester
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows
    
    def q2(self):
        #Students: (sid, firstName, lastName, yearStarted)
        #Majors: (sid, major)
        #Grades: (sid, cid, credits, grade)
        #Courses: (cid, number, progressor, major, year, semester)
        query = '''
            SELECT firstName, lastName, year, semester, count(*) as ct
            FROM STUDENTS NATURAL JOIN COURSES NATURAL JOIN GRADES 
            WHERE grade > 0
            GROUP BY firstName, lastName, sid, year, semester
            HAVING ct >= 2
            ORDER BY firstName, lastName, year, semester

        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q3(self):
        query = '''
            SELECT firstName, lastName, mc as major, number as courseNumber
            FROM allgrades
            WHERE ms = mc and grade = 0
            ORDER BY firstName, lastName, major, courseNumber
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows
        
    def q4(self):
        #Students: (sid, firstName, lastName, yearStarted)
        #Majors: (sid, major)
        #Grades: (sid, cid, credits, grade)
        #Courses: (cid, number, profressor, major, year, semester)
        query = '''
            SELECT firstName, lastName, m.major, c.number
            FROM STUDENTS NATURAL JOIN MAJORS m
            NATURAL JOIN GRADES NATURAL JOIN COURSES c1 
            INNER JOIN COURSES c ON m.major == c.major
            WHERE grade == 0
            ORDER BY firstName, lastName, m.major, c.number
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q5(self):

        query = '''
            SELECT professor, COUNT(sid) as success
            FROM GRADES NATURAL JOIN COURSES
            WHERE grade >= 2
            GROUP BY PROFESSOR
            ORDER BY success DESC, professor ASC
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q6(self):
        #Students: (sid, firstName, lastName, yearStarted)
        #Majors: (sid, major)
        #Grades: (sid, cid, credits, grade)
        #Courses: (cid, number, profressor, major, year, semester)
        query = '''
            SELECT number, group_concat(firstName || ' ' || lastName, ', ') AS student_names, AVG(grade) AS avg_grade
            FROM STUDENTS NATURAL JOIN GRADES NATURAL JOIN COURSES
            WHERE grade >= 2
            GROUP BY number
            HAVING avg_grade > 3
            ORDER BY avg_grade DESC, student_names ASC, number ASC
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows
        
if __name__ == "__main__":
    task = Task("database.db", 'students.csv', 'grades.csv', 'courses.csv', 'majors.csv')
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
