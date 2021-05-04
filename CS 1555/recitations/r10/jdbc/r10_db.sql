drop table if exists users cascade;
create table users
(
  username varchar(50) primary key,
  password varchar(50) not null
);

INSERT INTO users (username, password) VALUES ('costas', 'secretpassword');
INSERT INTO users (username, password) VALUES ('panos', 'strongpassword');

drop table if exists student cascade;
create table student
(
    sid   int primary key,
    name  varchar(15) not null,
    class integer,
    major varchar(10)
);

INSERT INTO student (sid, name, class, major) VALUES (123, 'John', 3, 'CS');
INSERT INTO student (sid, name, class, major) VALUES (124, 'Mary', 3, 'CS');
INSERT INTO student (sid, name, class, major) VALUES (126, 'Sam', 2, 'CS');
INSERT INTO student (sid, name, class, major) VALUES (129, 'Julie', 2, 'Math');
INSERT INTO student (sid, name, class, major) VALUES (11, 'costa', 3, 'CS');

drop table if exists class cascade;
create table class
(
  classid          int primary key ,
  max_num_students int,
  cur_num_students int
);

drop table if exists register cascade;
create table register
(
  student_name    varchar(10),
  classid         int,
  date_registered date,
  constraint PK primary key (student_name, classid),
  constraint FK foreign key (classid) references class (classid)
);

insert into class values (1, 2, 1);
insert into class values (2, 4, 0);






CREATE OR REPLACE FUNCTION get_upper_case(param VARCHAR(50))
RETURNS VARCHAR(50)
AS $$
    BEGIN
        RETURN upper(param);
    END;
$$ LANGUAGE plpgsql;





DROP FUNCTION returnning_new_student_table;
CREATE OR REPLACE FUNCTION returnning_new_student_table(p_sid INT)
RETURNS TABLE (
   sid_r INT,
   name_r VARCHAR(15)
)
AS $$
    DECLARE
        temp_name VARCHAR(15);
    BEGIN

        SELECT name INTO temp_name FROM student WHERE student.sid= p_sid;

        CREATE TEMPORARY TABLE temp_table (
            sid_t INT,
            name_t VARCHAR(15)
        );

        INSERT INTO temp_table VALUES (p_sid, temp_name);

        RETURN QUERY SELECT * FROM temp_table;
    END;
$$ LANGUAGE plpgsql;


SELECT * FROM returnning_new_student_table(123);
