--Lu, Gordon: <gol6@pitt.edu>
--CS 1555: Assignment IV
/*
ASSUMPTIONS:
For query 3b), I am assuming that the tech_personnel can only delegate one
ticket at a time.
*/
/*
Question 2a:
List in ascending order of their full names, as “UserNames” (i.e., a single
attribute), users whose office phone number is 412-624-8443.
*/

SELECT fname || ' ' || lname as UserNames
FROM USERS
WHERE office_phone = '412-624-8443'
ORDER BY UserNames ASC;

/*
Question 2b:
List only once (no duplicates) the first and last name of users who have at
least one ticket with status ‘in progress’ since January 2020.

Need to get rid of duplicates....
*/
SELECT DISTINCT fname, lname
FROM (USERS u INNER JOIN TICKETS t on u.pplSoft = t.owner_pplSoft
    INNER JOIN ASSIGNMENT a ON t.ticket_number = a.ticket_number)
WHERE a.status = 'in_progress' AND a.date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20';

/*
Question 2c:
List the full names as “Staff” (i.e., a single attribute) of Tech Staff along with
the full name as “Supervisor” of their supervisor. Note that a Tech Staff who does not
have a supervisor, such as a supervisor, has NULL in supervisor attribute.

ISSUE: Need to print out tech_personnel with NULL supervisor
*/

SELECT t1.fname || ' ' || t1.lname as Staff, CASE
        WHEN t1.supervisor IS NULL THEN ''
        ELSE t2.fname || ' ' || t2.lname
        END AS Supervisor
FROM (TECH_PERSONNEL t1 LEFT OUTER JOIN TECH_PERSONNEL t2 ON t1.supervisor = t2.pplsoft);
-- SELECT t1.fname || ' ' || t1.lname as Staff,
--     CASE
--         WHEN t1.supervisor IS NULL THEN 'N/A'
--         ELSE t2.fname || ' ' || t2.lname
--     END AS Supervisor, t1.pplsoft
-- FROM TECH_PERSONNEL t1, TECH_PERSONNEL t2
-- WHERE t1.supervisor = t2.pplSoft;

/*
Question 2d:
List the pplSoft number of users who submitted more than 5 tickets during
the month of January 2020.
*/

SELECT pplSoft
FROM (USERS u JOIN TICKETS t ON u.pplSoft = t.owner_pplSoft)as u_ticks
WHERE date_submitted BETWEEN '01-JAN-20' AND '31-JAN-20'
GROUP BY u_ticks.pplSoft
HAVING COUNT(*) > 5;

/*
Question 2e:
Display the average number of days each ticket is being worked on as
AVERAGE DAYS WORKED ON, for tickets submitted during the month of January
2020. Note that a newly submitted ticket without days worked on (NULL) should be
listed as 0
*/

SELECT ticket_number, CASE
            WHEN days_worked_on IS NOT NULL THEN AVG(days_worked_on)
            ELSE 0
            END AS AVERAGE_DAYS_WORKED_ON
FROM TICKETS
WHERE date_submitted BETWEEN '01-JAN-20' AND '31-JAN-20'
GROUP BY ticket_number
ORDER BY ticket_number ASC;

/*
Question 2f:
 Display the full name and number of tickets of the user who submitted the
least number of tickets.

Make nested query for this one
*/

-- SELECT user_ticks.fname || ' ' || user_ticks.lname as FullName, COUNT(user_ticks.ticket_number) as count
-- FROM (USERS u JOIN TICKETS t ON u.pplSoft = t.owner_pplSoft) as user_ticks
-- GROUP BY user_ticks.pplSoft
-- ORDER BY count ASC LIMIT 1;

SELECT user_ticks.fname || ' ' || user_ticks.lname as FullName, COUNT(user_ticks.ticket_number) as num_ticks
         FROM (USERS u JOIN TICKETS t ON u.pplSoft = t.owner_pplSoft) as user_ticks
         GROUP BY user_ticks.pplSoft
HAVING COUNT(user_ticks.ticket_number) = ( --because having is evaluated before the select
SELECT MIN(num_ticks)
FROM (
         SELECT user_ticks.fname || ' ' || user_ticks.lname as FullName, COUNT(user_ticks.ticket_number) as num_ticks
         FROM (USERS u JOIN TICKETS t ON u.pplSoft = t.owner_pplSoft) as user_ticks
         GROUP BY user_ticks.pplSoft
) t);
/*

Question 2g:
List the 3 machines located on the fifth floor of Sennott with the most problems
(i.e., Top 3 number of tickets) along with the number of problems.
*/

SELECT i.machine_name, COUNT(i.machine_name) as Num_Problems
FROM (TICKETS t INNER JOIN INVENTORY i on t.machine_name = i.machine_name
    INNER JOIN LOCATIONS l ON i.location_id = l.location_id)
WHERE l.building = 'SENSQ' AND l.location = '5th floor'
GROUP BY i.machine_name
ORDER BY Num_Problems DESC
FETCH FIRST 3 ROWS ONLY;

/*
Question 2h:
List the days ranked third and fifth with the most number of submitted
tickets in December 2019. Hint: This is based on ranking.

1. try using count of date submitted in rank function or
2. try to use a nested query*/

--one probably works
-- */
-- SELECT date_submitted, RANK() OVER (
--     ORDER BY (SELECT COUNT(ticket_number)
--               FROM TICKETS t
--               WHERE t.date_submitted BETWEEN '01-DEC-19' AND '31-DEC-19'
--               GROUP BY t.date_submitted)
--     ) AS rk
-- FROM TICKETS;

SELECT date_submitted
FROM(
    SELECT date_submitted, RANK() OVER (
    ORDER BY COUNT(ticket_number) DESC) AS rk
    FROM TICKETS
    WHERE date_submitted BETWEEN '01-DEC-19' AND '31-DEC-19'
    GROUP BY date_submitted
) t
WHERE rk = 3 OR rk = 5;



/*
Question 2i:
For tickets submitted during the month of January 2020, calculate the top
two categories that tickets were submitted under. That is, list the two categories with
the most tickets along with the number of tickets submitted Lunder the category, in
descending order
*/

SELECT category, COUNT(ticket_number) as num_ticks
FROM TICKETS t, CATEGORIES c
WHERE date_submitted BETWEEN '01-JAN-20' AND '31-JAN-20'
AND t.category_id = c.category_id
GROUP BY t.category_id, category
ORDER BY num_ticks DESC
FETCH FIRST 2 ROWS ONLY;

/*
Question 3a)
For tickets submitted during the month of January 2020, calculate the top
two categories that tickets were submitted under. That is, list the two categories with
the most tickets along with the number of tickets submitted under the category, in
descending order.
-Order what by descending?
*/
CREATE OR REPLACE VIEW COUNT_TICKS (category, num_ticks)
    AS SELECT category, COUNT(ticket_number) as num_ticks
    FROM TICKETS t, CATEGORIES c
    WHERE date_submitted BETWEEN '01-JAN-20' AND '31-JAN-20'
    AND t.category_id = c.category_id
    GROUP BY t.category_id, c.category;
CREATE OR REPLACE VIEW TOP_TWO (category, num_ticks)
    AS SELECT *
    FROM COUNT_TICKS
    ORDER BY num_ticks DESC
    FETCH FIRST 2 ROWS ONLY;
SELECT * FROM TOP_TWO;

/*
Question 3b)
For each tech personnel, calculate the total number of days spent on resolving
tickets during the month of January 2020. List them in an ascending order.
-List what in ascending??
*/
--First retrieve the tech staff who have been assigned a ticket
CREATE OR REPLACE VIEW TECH_WORKING(status, pplSoft, date_closed, date_assigned, ticket_number)
    AS SELECT status, pplSoft, date_closed, date_assigned, a.ticket_number as ticket_number
    FROM (TECH_PERSONNEL tp INNER JOIN ASSIGNMENT a on tp.pplSoft = a.tech_pplSoft
    INNER JOIN TICKETS t ON a.ticket_number = t.ticket_number);
--WHERE date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20' AND status IN ('assigned', 'delegated', 'in_progress');
--Now retrieve the distances from the date_closed, and the date_assigned.
--NULL date_closed implies that the ticket still has not been resolved yet, so the date closed should be Jan 31 2020
--include delegated now, then join with TECH_DISTANCES
CREATE OR REPLACE VIEW TECH_DISTANCES_SUMS_NONDELEGATED(pplSoft, distance)
AS SELECT pplSoft, SUM(CASE
                        WHEN date_closed IS NULL AND date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20' THEN TO_DATE('2020-01-31', 'YYYY-MM-DD')  - date_assigned --not resolved yet, assign date in January
                        WHEN date_closed IS NULL AND date_assigned < '01-JAN-20' THEN TO_DATE('2020-01-31', 'YYYY-MM-DD')  - TO_DATE('2020-01-01', 'YYYY-MM-DD') + 1 --not resolved yet, assign date outside January
                        WHEN date_closed > '31-JAN-20' AND date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20' THEN TO_DATE('2020-01-31', 'YYYY-MM-DD')  - date_assigned --date closed outside 1/31
                        WHEN date_assigned < '01-JAN-20' AND date_closed BETWEEN '01-JAN-20' AND '31-JAN-20' THEN date_closed - TO_DATE('2020-01-01', 'YYYY-MM-DD') --date_assigned outside 1/1/20
                        WHEN date_assigned < '01-JAN-20' AND date_closed > '31-JAN-20' THEN TO_DATE('2020-01-31', 'YYYY-MM-DD') - TO_DATE('2020-01-01', 'YYYY-MM-DD') + 1 --date closed and date assigned both outside
                        WHEN date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20'AND date_closed BETWEEN '01-JAN-20' AND '31-JAN-20'
                            AND date_closed = date_assigned THEN 1 --date_assigned and date_closed both in january and closed on same day, return 1
                        WHEN date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20'
                            AND date_closed BETWEEN '01-JAN-20' AND '31-JAN-20' THEN date_closed - date_assigned
                        --ELSE date_closed  - date_assigned --date_assigned and date_closed both in january, take difference
                    END) AS distance
FROM TECH_WORKING
WHERE status IN ('assigned','in_progress', 'closed_successful', 'closed_unsuccesful')
GROUP BY pplSoft;

--handle delegated now

--Get Non-Delegated Tickets without Sum
CREATE OR REPLACE VIEW TECH_DISTANCES_NONDELEGATED(status, pplSoft, date_assigned, date_closed, ticket_number)
AS SELECT status, pplSoft, date_assigned, date_closed, ticket_number
FROM TECH_WORKING
WHERE status IN ('assigned', 'in_progress', 'closed_successful', 'closed_unsuccesful');

--Get Delegated Tickets without Distance
CREATE OR REPLACE VIEW TECH_DISTANCES_DELEGATED(status, pplSoft, date_assigned, date_closed, ticket_number)
AS SELECT status, pplSoft, date_assigned, date_closed, ticket_number
FROM TECH_WORKING
WHERE status = 'delegated';
--Compute Delegated Ticket sums
CREATE OR REPLACE VIEW TECH_DISTANCES_DELEGATED_SUMS(pplSoft, distance)
AS SELECT d.pplSoft, SUM(CASE
                        --WHEN nd.date_assigned IS NULL AND d.date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20' THEN TO_DATE('2020-01-31', 'YYYY-MM-DD')  - d.date_assigned --not resolved yet, assign date in January
                        --WHEN nd.date_assigned IS NULL AND d.date_assigned < '01-JAN-20' THEN TO_DATE('2020-01-31', 'YYYY-MM-DD')  - TO_DATE('2020-01-01', 'YYYY-MM-DD') + 1 --not resolved yet, assign date outside January
                        WHEN nd.date_assigned > '31-JAN-20' AND d.date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20' THEN TO_DATE('2020-01-31', 'YYYY-MM-DD')  - d.date_assigned --date closed outside 1/31
                        WHEN d.date_assigned < '01-JAN-20' AND nd.date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20' THEN nd.date_assigned - TO_DATE('2020-01-01', 'YYYY-MM-DD') --date_assigned outside 1/1/20
                        WHEN d.date_assigned < '01-JAN-20' AND nd.date_assigned > '31-JAN-20' THEN TO_DATE('2020-01-31', 'YYYY-MM-DD') - TO_DATE('2020-01-01', 'YYYY-MM-DD') + 1 --date closed and date assigned both outside
                        WHEN d.date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20'AND nd.date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20'
                            AND nd.date_assigned = d.date_assigned THEN 1 --date_assigned and date_closed both in january and closed on same day, return 1
                        WHEN d.date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20'
                            AND nd.date_assigned BETWEEN '01-JAN-20' AND '31-JAN-20' THEN nd.date_assigned - d.date_assigned
                        --ELSE date_closed  - date_assigned --date_assigned and date_closed both in january, take difference
                    END) AS distance
FROM TECH_DISTANCES_DELEGATED d INNER JOIN TECH_DISTANCES_NONDELEGATED nd ON d.ticket_number = nd.ticket_number
GROUP BY d.pplSoft;
--Now sum them all up by tech_pplSoft
--Compute the Delegated Days Worked by joining on
CREATE OR REPLACE VIEW DELEGATED_DAYS_WORKED(pplSoft, distance)
AS SELECT d.pplSoft, (nd.distance + d.distance) AS distance
FROM TECH_DISTANCES_SUMS_NONDELEGATED nd INNER JOIN TECH_DISTANCES_DELEGATED_SUMS d ON nd.pplSoft = d.pplSoft
ORDER BY distance ASC;

--At this point, we're only missing the tuples from the Non-delegated
--Left-join with Previous table to get all things that PSIDs in original table that match
--The second table should have the sums for DELEGATED TICKETS
--The non-delegated ticket sums are the tuples that don't match are maintained in the original table
--Do a conditional based on whether PSIDs match. If they do match, then output the distance computed from the
--second table. Otherwise, output the distance computed by the first table.
CREATE OR REPLACE VIEW TOTAL_DAYS_SPENT_FOR_ALL(pplSoft, sum_days_worked)
AS SELECT DISTINCT tdgs.pplSoft as pplSoft, CASE
                                                WHEN tdgs.pplSoft = tds.pplSoft THEN tds.distance
                                                ELSE tdgs.distance
                                            END AS sum_days_worked
FROM TECH_DISTANCES_SUMS_NONDELEGATED tdgs LEFT OUTER JOIN DELEGATED_DAYS_WORKED tds on tdgs.pplSoft = tds.pplSoft
ORDER BY sum_days_worked ASC;
SELECT * FROM TOTAL_DAYS_SPENT_FOR_ALL;
