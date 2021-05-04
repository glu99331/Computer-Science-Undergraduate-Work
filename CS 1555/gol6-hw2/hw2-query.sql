----------------------------------------
--- Sample Solution to Assignment #2 ---
----------------------------------------

----------------------------------------
-- Name: Sample Student
-- PittID: pitt01
----------------------------------------

-- List the contents of each table

SELECT *
FROM TECH_PERSONNEL;

SELECT *
FROM USERS;

SELECT *
FROM CATEGORIES;

SELECT *
FROM INVENTORY;

SELECT *
FROM LOCATIONS;

SELECT *
FROM TICKETS;

SELECT *
FROM ASSIGNMENT;

----------------------------------------
--- Sample solution for Question #5  ---
----------------------------------------

--(a) Retrieve all the ticket numbers and status of the tickets that are 
--    closed successfully.

SELECT ticket_number, status 
FROM ASSIGNMENT
WHERE status='closed_successful';

--(b) Retrieve all the ticket numbers and descriptions that belong 
--    to the machine ’kaso.cs.pitt.edu’.

SELECT ticket_number, description 
FROM TICKETS
WHERE machine_name='kaso.cs.pitt.edu';