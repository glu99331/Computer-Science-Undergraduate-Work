
--Recitation 4
--SQL is case insensitive, I will alternate case throughout this file


--Consider the following relation
DROP TABLE IF EXISTS TEACHING_STAFF CASCADE;

CREATE table TEACHING_STAFF (
    staff_id integer primary key,
    fname varchar(30) NOT NULL,
    lname VARCHAR(30)
    --maybe someone doesn't have a last name, who am I to assume that?
);

INSERT INTO TEACHING_STAFF VALUES (1, 'Rakan', 'Alseghayer');
insert into TEACHING_STAFF values (2, 'Brian', 'Nixon');
INSERT INTO teaching_Staff VALUES (3, 'Brian', 'Smith');

COMMIT;

--Check to see what is in the table TEACHING_STAFF
SELECT *
FROM TEACHING_STAFF;

--Question: How many rows will be returned after the following statements?
--Does this differ from in relational algebra?
select fname
from TEACHING_STAFF;


--To remove duplicate rows in a projection
SELECT DISTINCT FNAME
FROM teaching_staff;

-------------------------------------------
--DDL PRACTICE
-------------------------------------------

--Create a table t1 with a primary key attribute a1 of type varchar(10)
--before creating the table, please use the drop table statement to avoid pre-existing tables with the same name and
--cascade


drop Table IF exists T1 CASCADE;

CREATE TABLE T1 (
    a1 varchar(10) primary key
);

--See that our table exists and what the contents are
SELECT *
FROM T1;

--add a2 attribute to the table, type is varchar(5)

ALTER TABLE T1 add a2 varchar(5);

--change the type of a2 from varchar(5) to type integer
--table you want to alter, the action - alter column , what about the column we're changing, the type, and expression to cast

alter table T1 alter column a2 type integer USING A2::integer;

--:: a shorthand form of the CAST()
--check that we can insert into table with 2 columns
INSERT INTO T1 VALUES ('hello', 5);

--set the default of a2 to be 1
alter table T1 alter column a2 set default 1;



--check that default works
INSERT INTO T1 (A1) VALUES ('brian');

--Remove the default value of a2
alter table T1 alter column a2 DROP DEFAULT;


--a2 will be <null> here since default is dropped
INSERT INTO T1 (a1) VALUES ('costa');

--Add domain so that a2 is in the set {1, 2, 3, 4, 5}
CREATE DOMAIN a2_domain as integer check(value in (1, 2, 3, 4, 5));

alter table T1 alter column a2 type a2_domain;


--7 is not in the domain --> will give an ERROR
INSERT INTO T1 VALUES ('John', 7);

--3 is in the domain
INSERT INTO T1 VALUES ('John', 3);

--Double check the content of T1 to see the results of our statements
SELECT *
FROM T1;

--Adding a Domain for a2 will still allow NULL values
--We have to set a2 as not null to prevent inserting null values
--We also have to remove (costa, null) from the table before setting not null
DELETE from T1 Where T1.a1 = 'costa';

alter table T1 alter column a2 set NOT NULL;

--Check that inserting a null for A2 will fail --> Gives an ERROR
INSERT INTO T1 (a1) VALUES ('Panos');


--Check that Panos wasn't added
SELECT *
FROM T1;