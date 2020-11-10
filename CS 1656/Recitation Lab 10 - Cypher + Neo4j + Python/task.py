from neo4j import GraphDatabase, basic_auth
import time
import datetime
#Lu, Gordon: CS 1656 Fall 2020 Recitation Lab 10 -> Passes Gradescope!
def fromTimestamp(timestamp):
    if isinstance(timestamp, str):
        timestamp = int(timestamp)
    return (datetime.datetime(1970, 1, 1) + datetime.timedelta(milliseconds=timestamp)).strftime("%m/%d/%Y %H:%M:%S")
        
def toTimestamp(dateval):
    return time.mktime(datetime.datetime.strptime(dateval, "%m/%d/%Y").timetuple())*1000

class Task(object):
    def __init__(self, password):
        self.driver = GraphDatabase.driver("bolt://localhost:7687", auth=("neo4j", password), encrypted=False)
        self.session = self.driver.session()
        self.transaction = self.session.begin_transaction()

    def q1(self):
        result = self.transaction.run("""MATCH (tom:Actor {name: 'Tom Hanks'})
            RETURN tom.name, tom.birthday, tom.birthplace""")
        return [(r[0], fromTimestamp(r[1]), r[2]) for r in result]

    def q2(self):
        result = self.transaction.run("""
            MATCH (avatar:Movie {title: 'Avatar'})
            RETURN avatar.studio, avatar.releaseDate
        """)
        return [(r[0], fromTimestamp(r[1])) for r in result]
        
    def q3(self):
        start = time.mktime(datetime.datetime.strptime("01/01/1990", "%m/%d/%Y").timetuple())*1000
        end = time.mktime(datetime.datetime.strptime("12/31/1999", "%m/%d/%Y").timetuple())*1000
        result = self.transaction.run("""
            MATCH (nineties:Movie)
            WHERE toFloat(nineties.releaseDate) > {}
            AND toFloat(nineties.releaseDate) < {}
            RETURN nineties.title
        """.format(start, end))
        return [r[0] for r in result]

    def q4(self):
        result = self.transaction.run("""
            MATCH (tom:Actor {name:'Tom Hanks'})-[:ACTS_IN]->(TH_Movies)
            RETURN TH_Movies.title, TH_Movies.studio, TH_Movies.releaseDate
        """)
        return [(r[0], r[1], fromTimestamp(r[2])) for r in result]

    def q5(self):
        result = self.transaction.run("""
            MATCH (avatar {title: 'Avatar'}) <- [:DIRECTED]-(directors)
            return directors.name
        """)
        return [(r[0]) for r in result]

    def q6(self):
        result = self.transaction.run("""
            MATCH (tom:Actor {name:'Tom Hanks'})-[:ACTS_IN]->(m)<-[:ACTS_IN]-(coActors)
            RETURN coActors.name 
        """)
        return [(r[0]) for r in result]

    def q7(self):
        result = self.transaction.run("""
            MATCH (people:Person)-[relatedTo]-(:Movie {title:'Avatar'})
            RETURN people.name, Type(relatedTo), relatedTo
        """)
        return [(r[0], r[1]) for r in result]

    def q8(self):
        result = self.transaction.run("""
            MATCH (tom:Actor {name:'Tom Hanks'})-[:ACTS_IN]->(m)<-[:ACTS_IN]-(coActors),
            (coActors)-[:ACTS_IN]->(m2)<-[:ACTS_IN]-(cocoActors)
            WHERE NOT (tom)-[:ACTS_IN]->(m2)
            RETURN DISTINCT cocoActors.name AS Recommended, count(*) AS Strength
            ORDER BY Strength DESC
        """)
        return [(r[0]) for r in result]

    def q9(self):
        result = self.transaction.run("""
            MATCH (tom:Actor {name:'Tom Hanks'})-[:ACTS_IN]->(m)<-[:ACTS_IN]-(coActors),
            (coActors)-[:ACTS_IN]->(m2)<-[:ACTS_IN]-(cruise:Actor {name:'Tom Cruise'})
            RETURN DISTINCT coActors.name
        """)
        return [(r[0]) for r in result]

if __name__ == "__main__":
    sol = Task("neo4jpass")
    print("---------- Q1 ----------")
    print(sol.q1())
    print("---------- Q2 ----------")
    print(sol.q2())
    print("---------- Q3 ----------")
    print(sol.q3())
    print("---------- Q4 ----------")
    print(sol.q4())
    print("---------- Q5 ----------")
    print(sol.q5())
    print("---------- Q6 ----------")
    print(sol.q6())
    print("---------- Q7 ----------")
    print(sol.q7())
    print("---------- Q8 ----------")
    print(sol.q8())
    print("---------- Q9 ----------")
    print(sol.q9())
    sol.transaction.close()
    sol.session.close()
    sol.driver.close()