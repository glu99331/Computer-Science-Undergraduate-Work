import sqlite3 as lite
import csv
import re
import pandas as pd
import argparse
import collections
import json
import glob
import math
import os
import requests
import string
import sqlite3
import sys
import time
import xml

#Lu, Gordon: CS 1656 Fall 2020 Assignment 2 -> Passes Gradescope!
class Movie_auto(object):
    def __init__(self, db_name):
        #db_name: "cs1656-public.db"
        self.con = lite.connect(db_name)
        self.cur = self.con.cursor()
    
    #q0 is an example 
    def q0(self):
        query = '''SELECT COUNT(*) FROM Actors'''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q1(self):

        # Actors (aid, fname, lname, gender)
        # Movies (mid, title, year, rank)
        # Directors (did, fname, lname)
        # Cast (aid, mid, role)
        # Movie_Director (did, mid)

        #List all the actors (first and last name) who acted in at least one film in the 80s 
        #(1980-1990, both ends inclusive) and in at least one film in the 21st century (>=2000).
        
        #Sort alphabetically, by the actor's last and first name.
        #IDEA:
        #1) We need to find the intersection between Actors and Cast, this will tell us 
        query = '''
        SELECT a.fname, a.lname
        FROM Cast AS c
        INNER JOIN Actors AS a ON c.aid = a.aid
        WHERE c.aid in (SELECT c.aid
                        FROM Cast AS c
                        INNER JOIN Movies AS m ON m.mid = c.mid
                        WHERE m.year <= 1990 AND m.year >= 1980)
        AND c.aid in (SELECT c.aid
                        FROM Cast AS c
                        INNER JOIN Movies AS m ON m.mid = c.mid
                        WHERE m.year >= 2000)
        GROUP BY c.aid
        '''
        # query = '''
        #     SELECT fname, lname 
        #     FROM ACTORS a, CAST c1, CAST c2, MOVIES m1, MOVIES m2
        #     WHERE a.aid = c1.aid
        #     AND c1.mid = m1.mid
        #     AND (m1.year >= 1980 AND m1.year <= 1990)
        #     AND a.aid = c2.aid
        #     AND c2.mid = m2.mid
        #     AND m2.year >= 2000
        #     ORDER BY lname ASC, fname ASC
        #     '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows
        

    def q2(self):
        # List all the movies (title, year) that were released in the same year as the movie entitled 
        # "Rogue One: A Star Wars Story", but had a better rank
        
        # (Note: the higher the value in the rank attribute, the better the rank of the movie). 
        # Sort alphabetically, by movie title.
        # query = '''
        # '''
        query = '''
         SELECT Movies.title, Movies.year
         FROM Movies
         INNER JOIN Movies m1 on m1.title = "Rogue One: A Star Wars Story"
         WHERE m1.year = Movies.year AND Movies.rank > m1.rank AND Movies.title != "Rogue One: A Star Wars Story"
         ORDER BY Movies.title ASC
        '''
        # query = '''
        #     SELECT m_in.title, m_in.year
        #     FROM Movies m_in
        #     INNER JOIN Movies m1 on m1.title = "Rogue One: A Star Wars Story"
        #     WHERE m1.year = m_in.year AND m_in.rank > m1.rank
        #     ORDER BY m_in.title ASC
        # '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q3(self):
        # List all the actors (first and last name) who played in a Star Wars movie (i.e., title like '%Star Wars%') 
        # in decreasing order of how many Star Wars movies they appeared in. 
        
        # If an actor plays multiple roles in the same movie, count that still as one movie. 
        # If there is a tie, use the actor's last and first name to generate a full sorted order.

        # Actors (aid, fname, lname, gender)
        # Movies (mid, title, year, rank)
        # Directors (did, fname, lname)
        # Cast (aid, mid, role)
        # Movie_Director (did, mid)
       
        query = '''
            SELECT fname, lname, COUNT(DISTINCT mid) as ct
            FROM ACTORS
            NATURAL JOIN CAST NATURAL JOIN MOVIES m
            WHERE m.title LIKE '%Star Wars%'
            GROUP BY fname, lname
            ORDER BY ct DESC, lname ASC, fname ASC
        '''
        # query = '''
        #     SELECT fname, lname, COUNT(DISTINCT mid
        #     FROM Actors 
        #     NATURAL JOIN (SELECT aid, mid FROM CAST NATURAL JOIN Movies WHERE title like '%Star Wars%')
        #     GROUP BY fname, lname
        #     ORDER BY COUNT(DISTINCT mid) DESC
        # '''
        # query = '''
        #     SELECT fname, lname
        #     FROM Actors 
        #     NATURAL JOIN (SELECT aid, mid FROM CAST NATURAL JOIN Movies WHERE title like '%Star Wars%')
        #     GROUP BY fname, lname
        #     ORDER BY COUNT(DISTINCT mid) DESC
        # '''
        # query = '''
        #     SELECT fname, lname
        #     FROM Actors 
        #     NATURAL JOIN (SELECT aid, mid FROM CAST NATURAL JOIN Movies WHERE title like '%Star Wars%')
        #     GROUP BY fname, lname
        #     ORDER BY COUNT(DISTINCT mid) DESC      
        # '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows


    def q4(self):
        # Find the actor(s) (first and last name) who only acted in films released before 1980. 
        
        # Sort alphabetically, by the actor's last and first name.

        # Actors (aid, fname, lname, gender)
        # Movies (mid, title, year, rank)
        # Directors (did, fname, lname)
        # Cast (aid, mid, role)
        # Movie_Director (did, mid)
        query = '''
        SELECT fname, lname
        FROM Actors natural join(SELECT aid
        FROM (SELECT aid FROM Movies natural join Cast WHERE year < 1980)
        
        EXCEPT 
        
        SELECT aid
        FROM (SELECT aid FROM Movies natural join Cast WHERE year > 1979))
        ORDER BY lname, fname asc
        '''
        # query = '''
        #     SELECT fname, lname
        #     FROM Actors 
        #     NATURAL JOIN Cast
        #     NATURAL JOIN Movies
        #     WHERE year < 1980
        #     GROUP BY lname, fname
        #     ORDER BY lname ASC, fname ASC
        # '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q5(self):
        # List the top 10 directors in descending order of the number of films they directed 
        # (first name, last name, number of films directed). 
        
        # For simplicity, feel free to ignore ties at the number 10 spot (i.e., always show up to 10 only).

        # Actors (aid, fname, lname, gender)
        # Movies (mid, title, year, rank)
        # Directors (did, fname, lname)
        # Cast (aid, mid, role)
        # Movie_Director (did, mid)
        query = '''
        SELECT d.fname, d.lname, total.num 
        FROM Directors AS d JOIN
            (SELECT did, count(did) AS num
            FROM Movie_Director
            GROUP BY did) AS total ON d.did = total.did
        ORDER BY total.num DESC, d.lname ASC, d.fname ASC
        LIMIT 10
        '''
        # query = '''
        #     SELECT Directors.fname, Directors.lname, COUNT(Movies.title) FROM Directors
        #     LEFT JOIN Movie_Director ON Movie_Director.did = Directors.did
        #     LEFT JOIN Movies ON Movies.mid = Movie_Director.mid
        #     GROUP BY Directors.fname,Directors.lname
        #     ORDER BY COUNT(Movies.title) DESC
        #     LIMIT 10
        # '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q6(self):
        query = '''
            SELECT m.title, COUNT(DISTINCT c.aid) AS num_cast
            FROM Movies AS m
            INNER JOIN Cast AS c ON c.mid = m.mid
            GROUP BY m.mid
            HAVING num_cast >= (SELECT MIN(num_cast2)
                                FROM (SELECT COUNT(c2.aid) AS num_cast2
                                    FROM Movies AS m2
                                    INNER JOIN Cast AS c2 ON c2.mid = m2.mid
                                    GROUP BY m2.mid
                                    ORDER BY num_cast2 DESC
                                    LIMIT 10))
            ORDER BY num_cast DESC
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q7(self):
        query = '''
            SELECT m.title, IFNULL(WT.num_women_wt, 0) AS num_women, IFNULL(MT.num_men_mt, 0) AS num_men
            FROM Movies AS m
            INNER JOIN Cast AS c ON c.mid = m.mid
            INNER JOIN Actors AS a ON c.aid = a.aid
            LEFT JOIN (SELECT m2.mid, COUNT(*) AS num_men_mt
                    FROM Movies AS m2
                    INNER JOIN Cast AS c2 ON c2.mid = m2.mid
                    INNER JOIN Actors AS a2 ON c2.aid = a2.aid
                    WHERE a2.gender = "Male"
                    GROUP BY m2.mid) MT ON MT.mid = m.mid
            LEFT JOIN (SELECT m3.mid, COUNT(*) AS num_women_wt
                    FROM Movies AS m3
                    INNER JOIN Cast AS c3 ON c3.mid = m3.mid
                    INNER JOIN Actors AS a3 ON c3.aid = a3.aid
                    WHERE a3.gender = "Female"
                    GROUP BY m3.mid) WT ON WT.mid = m.mid
            WHERE num_women > num_men
            GROUP BY m.mid
            ORDER BY m.title ASC
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q8(self):
        query = '''
            SELECT a.fname, a.lname, COUNT(DISTINCT did) as numDirectors
            FROM Actors as a join ((Movie_Director NATURAL JOIN Directors )NATURAL JOIN Cast) as m on a.aid = m.aid
            WHERE (a.fname != m.fname) AND (a.lname != m.lname)
            GROUP BY a.aid
            HAVING numDirectors >= 7
            ORDER BY COUNT(DISTINCT did) DESC
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q9(self):
        query = '''
        SELECT fname, lname, COUNT(mid) as counts
        FROM (Movies NATURAL JOIN Cast) 
                NATURAL JOIN((Actors)
                NATURAL JOIN(SELECT  a.aid, MIN(year) as year
        FROM (SELECT * FROM Actors WHERE UPPER(fname) like 'D%')a NATURAL JOIN (cast NATURAL JOIN Movies)
            GROUP BY fname, lname))t
        GROUP BY t.aid
        ORDER BY counts DESC
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q10(self):
        query = '''
             Select t.lname, title
             FROM Movies Natural join((Actors natural join(cast))a join 
                             (Movie_Director natural join Directors)m on a.lname = m.lname AND a.mid=m.mid)t
             Order by t.lname asc
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows
    #PASSES -> IS THIS DUMB LUCK OR BIG BRAINED???
    def q11(self):
        query = '''
         SELECT fname, lname
        FROM (CAST natural join Actors) natural join
    
            (SELECT mid
            FROM Cast natural join
                (SELECT distinct b.aid
                 FROM Cast a join
                     (SELECT f.aid, f.mid, fname, lname
                      FROM (Cast natural join Actors)f join (Cast natural Join(Select aid from actors where fname = 'Kevin' and lname = 'Bacon'))v on f.mid = v.mid 
                WHERE fname != 'Kevin' AND lname != 'Bacon')b on a.mid = b.mid)
            Except
            
            SELECT mid
            FROM (CAST natural join (Select aid from actors where fname = 'Kevin' and lname = 'Bacon')))
        EXCEPT
    
            SELECT b.fname, b.lname
            FROM Cast a join
            (SELECT f.aid, f.mid, fname, lname
            FROM (Cast natural join Actors)f join (Cast natural Join(Select aid from actors where fname = 'Kevin' and lname = 'Bacon'))v on f.mid = v.mid 
            WHERE fname != 'Kevin' AND lname != 'Bacon')b on a.mid = b.mid
        
        ORDER BY lname, fname
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q12(self):
        query = '''
            SELECT a.fname, a.lname, COUNT(m.mid), AVG(m.rank) AS popularity
            FROM Actors AS a
            INNER JOIN Cast AS c ON a.aid = c.aid
            INNER JOIN Movies AS m ON c.mid = m.mid
            GROUP BY a.aid
            ORDER BY popularity DESC
            LIMIT 20
        '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

if __name__ == "__main__":
    task = Movie_auto("cs1656-public.db")
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
    rows = task.q8()
    print(rows)
    print()
    rows = task.q9()
    print(rows)
    print()
    rows = task.q10()
    print(rows)
    print()
    rows = task.q11()
    print(rows)
    print()
    rows = task.q12()
    print(rows)
    print()