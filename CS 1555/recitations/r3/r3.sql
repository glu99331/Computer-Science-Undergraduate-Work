CREATE TABLE STUDENT (
	sid		int	NOT NULL,
	name		varchar(15)	NOT NULL,
	class		int,
	major		varchar(10),
	CONSTRAINT PK_STUDENT PRIMARY KEY(sid)
);

CREATE TABLE STUDENT_DIR (
	sid		int	NOT NULL,
	address	varchar(100),
	phone		varchar(20),
	CONSTRAINT PK_STUDENT_DIR PRIMARY KEY (sid),
	CONSTRAINT FK_STUDENT_DIR FOREIGN KEY (sid) REFERENCES STUDENT (sid) ON DELETE CASCADE
);

CREATE TABLE COURSE (
	course_no	varchar(10)	NOT NULL,
	name		varchar(100),
	course_level	varchar(10),
	CONSTRAINT PK_COURSE PRIMARY KEY (course_no)
);

CREATE TABLE COURSE_TAKEN (
	course_no	varchar(10) NOT NULL,
	term		varchar(15) NOT NULL,
	sid		int NOT NULL,
	grade		real,
	CONSTRAINT PK_course_TAKEN PRIMARY KEY (course_no, sid, term),
	CONSTRAINT FK1_COURSE_TAKEN FOREIGN KEY (sid) REFERENCES STUDENT (sid) ON DELETE CASCADE,
	CONSTRAINT FK2_COURSE_TAKEN FOREIGN KEY (course_no) REFERENCES COURSE (course_no)
);


INSERT INTO STUDENT (sid, class, name, major) 
	VALUES ('123', 3, 'John', 'CS');

INSERT INTO STUDENT (sid, name, class, major)
	VALUES('124', 'Mary', 3, 'CS');

INSERT INTO STUDENT (sid, name, class, major)
	VALUES('126', 'Sam', 2, 'CS');

INSERT INTO STUDENT (sid, name, class, major)
	VALUES('129', 'Julie', 2, 'Math');


--insert data into student_dir table

INSERT INTO STUDENT_DIR (sid, address, phone)
	VALUES('123', '333 Library St', '555-535-5263');

INSERT INTO STUDENT_DIR (sid, address, phone)
	VALUES('124', '219 Library St', '555-963-9653');

INSERT INTO STUDENT_DIR (sid, address, phone)
	VALUES('129', '555 Library St', '555-123-4567');

--insert data into course

INSERT INTO COURSE (course_no, name, course_level)
	VALUES('CS1520', 'Web Applications', 'UGrad');

INSERT INTO COURSE (course_no, name, course_level)
	VALUES('CS1555', 'Database Management Systems', 'UGrad');

INSERT INTO COURSE (course_no, name, course_level)
	VALUES('CS1550', 'Operating Systems', 'UGrad');

INSERT INTO COURSE (course_no, name, course_level)
	VALUES('CS2550', 'Database Management Systems', 'Grad');

INSERT INTO COURSE (course_no, name, course_level)
	VALUES('CS1655', 'Secure Data Management and Web Applications', 'UGrad');


--INSERT INTO course_taken
INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS1520', '123', 'Fall 19', 3.75);

INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS1520', '124', 'Fall 19', 4);

INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS1520', '126', 'Fall 19', 3);

INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS1555', '123', 'Fall 19', 4);

INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS1555', '124', 'Fall 19', null);

INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS1550', '123', 'Spring 20', null );

INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS1550', '124', 'Spring 20', null );

INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS1550', '126', 'Spring 20', null );

INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS1550', '129', 'Spring 20', null );

INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS2550', '124', 'Spring 20', null );

INSERT INTO COURSE_TAKEN (course_no, sid, term, grade)
	VALUES('CS1520', '126', 'Spring 20', null );

