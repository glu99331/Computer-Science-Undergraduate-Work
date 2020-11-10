from neo4j import GraphDatabase, basic_auth
import socket

#Lu, Gordon: CS 1656 Fall 2020 Assignment 4 -> Passes Gradescope!
class Movie_queries(object):
    def __init__(self, password):
        self.driver = GraphDatabase.driver("bolt://localhost", auth=("neo4j", password), encrypted=False)
        self.session = self.driver.session()
        self.transaction = self.session.begin_transaction()

    def q0(self):
        result = self.transaction.run("""
            MATCH (n:Actor) RETURN n.name, n.id ORDER BY n.birthday ASC LIMIT 3
        """)
        return [(r[0], r[1]) for r in result]
    # [Q1] List the first 20 actors in descending order of the number of films they acted in. 
    # Sort by the number of films in descending order, and the actor's name in ascending order.
    # OUTPUT: actor_name, number_of_films_acted_in
    def q1(self):
        result = self.transaction.run("""
            MATCH (n:Actor)-[:ACTS_IN]->(m:Movie) 
            RETURN n.name AS actor_name, COUNT(m) AS number_of_films_acted_in
            ORDER BY number_of_films_acted_in DESC, actor_name ASC 
            LIMIT 20
        """)
        return [(r[0], r[1]) for r in result]
    # [Q2] Find the movie with the largest cast, out of the list of movies that have a review.
    # OUTPUT: movie_title, number_of_cast_members
    def q2(self):
        result = self.transaction.run("""
            MATCH (m:Movie) <-[r:RATED]- (p:Person)
            WITH m, collect(m) AS rated_movies
            MATCH (a:Actor) -[:ACTS_IN]-> (m:Movie)
            WHERE m in rated_movies
            RETURN m.title AS movie_title, count(a) AS number_of_cast_members
            ORDER BY number_of_cast_members DESC LIMIT 1
        """)
        return [(r[0], r[1]) for r in result]
    # [Q3] Show which directors have directed movies in at least 2 different genres. 
    # Sort by the number of genres in descending order, and the director's name in ascending order.
    # OUTPUT: director name, number of genres
    def q3(self):
        result = self.transaction.run("""
            MATCH (d:Director)-[:DIRECTED]->(m:Movie) 
            WITH d, COUNT(DISTINCT m.genre) AS number_of_genres
            WHERE number_of_genres >= 2
            RETURN d.name as director_name, number_of_genres
            ORDER BY number_of_genres DESC, director_name ASC
        """)
        return [(r[0], r[1]) for r in result]
    # [Q4] The Bacon number of an actor is the length of the shortest path between the actor and Kevin Bacon 
    # in the "co-acting" graph. That is, Kevin Bacon has Bacon number 0; all actors who acted in the same movie 
    # as him have Bacon number 1; all actors who acted in the same film as some actor with Bacon number 1 have 
    # Bacon number 2, etc. 
    
    # List all actors whose Bacon number is exactly 2 (first name, last name). 
    # You can familiarize yourself with the concept, by visiting The Oracle of Bacon. 
    # Don't return duplicates. Sort by the Actor's name.
    # OUTPUT: actor_name
    def q4(self):
        result = self.transaction.run("""
            MATCH (bacon_strip:Actor{name: "Kevin Bacon"}) -[:ACTS_IN]-> (m:Movie) <-[:ACTS_IN]- (bacon_strip1:Actor)
            MATCH (bacon_strip1:Actor)-[:ACTS_IN]->(m2:Movie)<-[:ACTS_IN]-(bacon_strip2:Actor)
            WHERE bacon_strip2 <> bacon_strip AND NOT (bacon_strip)-[:ACTS_IN]->()<-[:ACTS_IN]-(bacon_strip2)
            RETURN DISTINCT bacon_strip2.name AS actor_name
            ORDER BY actor_name
        """)
        return [(r[0]) for r in result]

if __name__ == "__main__":
    sol = Movie_queries("neo4jpass")
    print("---------- Q0 ----------")
    print(sol.q0())
    print("---------- Q1 ----------")
    print(sol.q1())
    print("---------- Q2 ----------")
    print(sol.q2())
    print("---------- Q3 ----------")
    print(sol.q3())
    print("---------- Q4 ----------")
    print(sol.q4())
    sol.transaction.close()
    sol.session.close()
    sol.driver.close()