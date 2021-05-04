CREATE SCHEMA R2;

CREATE SCHEMA IF NOT EXISTS R2;

-- NOTE: wait till later for
-- 1) ALTER TABLE
-- 2) VIEW
-- 3) DOMAIN

-- Q: how can i create a table:

CREATE TABLE RECITATION2 (
    id int
);

-- Q: how can I create a table under a certain schema?

CREATE TABLE R2.RECITATION2 (
    id int
);

-- Q: how can I drop a table?

DROP TABLE RECITATION2;

-- Q: how can I drop a table under a certain schema?

DROP TABLE R2.RECITATION2;

------------- numeric types section: -------------

-- recreate a table to work with:

CREATE TABLE R2.RECITATION2 (
    id int PRIMARY KEY
);

-- what is a PK?? (difference b/w no PK and a PK)

-- max of int
INSERT INTO R2.RECITATION2 VALUES (2147483647);
INSERT INTO R2.RECITATION2 VALUES (2147483648);


-- max of smallint

DROP TABLE R2.RECITATION2;
CREATE TABLE R2.RECITATION2 (
    id smallint PRIMARY KEY
);

INSERT INTO R2.RECITATION2 VALUES (32767);
INSERT INTO R2.RECITATION2 VALUES (32768);

-- ask about dropping schema?

DROP SCHEMA R2 RESTRICT;
DROP SCHEMA R2 CASCADE;

-------------------------

-- recreate schema to work:

CREATE SCHEMA IF NOT EXISTS R2;

-- create a table with decimal:
CREATE TABLE R2.RECITATION2 (
    id real PRIMARY KEY
);

-- insert values

INSERT INTO R2.RECITATION2 VALUES (4.1638568467867);

-- drop table to recreate:

drop table R2.RECITATION2;
CREATE TABLE R2.RECITATION2 (
    id decimal(7,6) PRIMARY KEY
);

-- insert values

INSERT INTO R2.RECITATION2 VALUES (1234567.123456);

-- why it didn't work? How to fix it?

INSERT INTO R2.RECITATION2 VALUES (1.1234567);

-- why did it go through :) ??
-- because it will truncate the precision and maintain the left side size as possible
-- proof:
INSERT INTO R2.RECITATION2 VALUES (9);
INSERT INTO R2.RECITATION2 VALUES (10);

-- How do we create new tables? what about with the same name?

------------- character types section: -------------

-- fresh start for char:
DROP TABLE R2.RECITATION2;
CREATE TABLE R2.RECITATION2 (
    name char(4) PRIMARY KEY
);

-- Q: how do we represent a char in SQL? (single quotes)

INSERT INTO R2.RECITATION2 VALUES ('hello');

-- Q: why error?

-- Q: how can we fix it? :)  (hell joke)

INSERT INTO R2.RECITATION2 VALUES ('hell');

-- fresh start for varchar:
drop table R2.RECITATION2;
CREATE TABLE R2.RECITATION2 (
    name varchar(4) PRIMARY KEY
);

INSERT INTO R2.RECITATION2 VALUES ('hello');

-- why error eventhough varchar?????? (blank padded)
-- why use char to start with?? (optimization if known size)

-- fresh start for CLOBS in PostgreSQL "text"
drop table R2.RECITATION2;
CREATE TABLE R2.RECITATION2 (
    name text PRIMARY KEY
);

INSERT INTO R2.RECITATION2 VALUES ('very long article that has no ending :)');

-- try to test its max size :) :) (show web page)

-- fresh start for National:
drop table R2.RECITATION2;
CREATE TABLE R2.RECITATION2 (
    name national char(30) PRIMARY KEY
);

-- national varchar is not supported in PostgreSQL
-- ALSO national char is not mandatory ... tie it to the locale

INSERT INTO R2.RECITATION2 VALUES ('راكان الصغير');

-- fresh start for bits:
drop table R2.RECITATION2;
CREATE TABLE R2.RECITATION2 (
    name bit(4) PRIMARY KEY
);

INSERT INTO R2.RECITATION2 VALUES (B'1');
-- why not able to insert '1'? (fixed no padding)

INSERT INTO R2.RECITATION2 VALUES (B'1111');

-- fresh start for bit varying:
drop table R2.RECITATION2;
-- OR varbit(n)
CREATE TABLE R2.RECITATION2 (
    name bit varying(4) PRIMARY KEY
);

INSERT INTO R2.RECITATION2 VALUES (B'1');
INSERT INTO R2.RECITATION2 VALUES (B'1111');

------------- TEMPORAL types section: -------------

-- fresh start for date:
drop table R2.RECITATION2;
CREATE TABLE R2.RECITATION2 (
    pid int PRIMARY KEY,
    admit_date date
);

INSERT INTO R2.RECITATION2 VALUES (1, '2020-08-21');

-- we all know that harcoding for the current moment is bad, so?
INSERT INTO R2.RECITATION2 VALUES (2, current_date);

-- same if i have time stamp with its functions

------------- How to query the database? -------------

select * from R2.RECITATION2;

select admit_date from R2.RECITATION2;

-- Q: why not excel?? why not files?? why the hassle?
-- Why build a system, a language, bla bla bla?????
-- YOU WILL BE ABLE TO ANSWER THAT SOON ENOUGH
-- but the short answer is ACID

------------- Some date and interval functions -------------

drop table R2.RECITATION2;
CREATE TABLE R2.RECITATION2 (
    tid int PRIMARY KEY,
    duration_of_stay date
);

INSERT INTO R2.RECITATION2 VALUES  (1, current_date - integer '6');

select * from R2.RECITATION2;


-- ##################################################################################################

-- for a reference on numeric types : https://www.postgresql.org/docs/10/datatype-numeric.html
-- for a reference on char types: https://www.postgresql.org/docs/8.4/datatype-character.html
-- for a reference on bit strings type: https://www.postgresql.org/docs/8.3/datatype-bit.html
-- for a reference on temporal data types: https://www.postgresql.org/docs/9.1/functions-datetime.html
