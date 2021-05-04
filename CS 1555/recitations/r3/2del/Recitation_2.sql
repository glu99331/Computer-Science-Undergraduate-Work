DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS student_dir CASCADE;
DROP TABLE IF EXISTS course CASCADE;
DROP TABLE IF EXISTS course_taken CASCADE;

CREATE TABLE student
(
  sid   char(5)  NOT NULL,
  name  varchar(15) NOT NULL,
  class int,
  major varchar(10),
  CONSTRAINT pk_student PRIMARY KEY (sid)
);

CREATE TABLE student_Dir
(
  sid     char(5) NOT NULL,
  address varchar(100),
  phone   varchar(20),
  CONSTRAINT pk_student_Dir PRIMARY KEY (sid),
  CONSTRAINT fk_student_Dir FOREIGN KEY (sid) REFERENCES student (sid)
);

CREATE TABLE course
(
  course_no    varchar(10) NOT NULL,
  name         varchar(100),
  course_level varchar(10),
  CONSTRAINT pk_Course PRIMARY KEY (course_no)
);

CREATE TABLE course_taken
(
  course_no varchar(10) NOT NULL,
  term      varchar(15) NOT NULL,
  sid       char(5)  NOT NULL,
  grade     real,
  CONSTRAINT pk_course_taken PRIMARY KEY (course_no, sid, term),
  CONSTRAINT fk_1_course_taken FOREIGN KEY (sid) REFERENCES student (sid),
  CONSTRAINT fk_2_course_taken FOREIGN KEY (course_no) REFERENCES Course (course_no)
);

--insert data into student table
INSERT INTO student(sid, name, class, major)
VALUES ('123', 'John', 3, 'CS');

INSERT INTO student(sid, name, class, major)
VALUES ('124', 'Mary', 3, 'CS');

INSERT INTO student(sid, name, class, major)
VALUES ('126', 'Sam', 2, 'CS');

INSERT INTO student(sid, name, class, major)
VALUES ('129', 'Julie', 2, 'Math');

--insert data into student_dir table
INSERT INTO student_dir(sid, address, phone)
VALUES ('123', '333 Library St', '555-535-5263');

INSERT INTO student_dir(sid, address, phone)
VALUES ('124', '219 Library St', '555-963-9653');

INSERT INTO student_dir(sid, address, phone)
VALUES ('129', '555 Library St', '555-123-4567');

--insert data into course
INSERT INTO course(course_no, name, course_level)
VALUES ('CS1520', 'Web Applications', 'UGrad');

INSERT INTO course(course_no, name, course_level)
VALUES ('CS1555', 'Database Management Systems', 'UGrad');

INSERT INTO course(course_no, name, course_level)
VALUES ('CS1550', 'Operating Systems', 'UGrad');

INSERT INTO course(course_no, name, course_level)
VALUES ('CS2550', 'Database Management Systems', 'Grad');

INSERT INTO course(course_no, name, course_level)
VALUES ('CS1655', 'Secure Data Management and Web Applications', 'UGrad');

--INSERT INTO course_taken
INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS1520', '123', 'Fall 18', 3.75);

INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS1520', '124', 'Fall 18', 4);

INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS1520', '126', 'Fall 18', 3);

INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS1555', '123', 'Fall 18', 4);

INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS1555', '124', 'Fall 18', null);

INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS1550', '123', 'Spring 19', null);

INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS1550', '124', 'Spring 19', null);

INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS1550', '126', 'Spring 19', null);

INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS1550', '129', 'Spring 19', null);

INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS2550', '124', 'Spring 19', null);

INSERT INTO course_taken(course_no, sid, term, grade)
VALUES ('CS1520', '126', 'Spring 19', null);

--show effect of different statements

-- select *
-- from student;
-- select *
-- from student_dir;
-- select *
-- from course;
-- select *
-- from course_taken;

-- INSERT INTO course_taken(course_no, sid, term, grade) VALUES ('CS1550','130','Spring 19',null);
-- delete from course_taken where course_no='CS1520' and term='Spring 19' and sid='126';
-- delete from student where sid='123';
--delete from student_dir where sid='123';
-- update course set name='Java Programming' where course_no='CS1520';
-- update course set course_no='CS6666' where course_no='CS1520';

-- select *
-- from student;
-- select *
-- from student_dir;
-- select *
-- from course;
-- select *
-- from course_taken;


