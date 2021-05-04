----------------------------------------
--- Sample Solution to ASSIGNMENTS #2 ---
----------------------------------------

----------------------------------------
-- Name: Sample Student
-- PittID: pitt01
----------------------------------------

----------------------------------------
--- Sample solution for Question #4  ---
----------------------------------------


INSERT INTO TECH_PERSONNEL values(1110001, 'Bob','Hoffman','bh1','Unix systems','412-624-8404',15, 1110001);
INSERT INTO TECH_PERSONNEL values(1110002, 'Terry','Wood','tw1','Hardware','412-624-8831',14, 1110001);
INSERT INTO TECH_PERSONNEL values(1110003, 'Russ','Howard','rh1','Windows Systems','412-624-8834',9, 1110001);
INSERT INTO TECH_PERSONNEL values(1110004, 'Christian','Mason','cm1','Web Technology','412-624-9181',5, 1110001);

INSERT INTO USERS values(5550001, 'Shenoda','Guirguis','shg18','412-624-8443');
INSERT INTO USERS values(5550002, 'Lory','Almoakar','la1','412-624-8443');
INSERT INTO USERS values(5550003, 'Thao','Pham','tp1','412-624-8850');
INSERT INTO USERS values(5550004, 'Michel','Hanna','mh1','412-624-8402');

INSERT INTO CATEGORIES values(1,'Printing Problem','problems have to do with printers and printing');
INSERT INTO CATEGORIES values(2,'Network Problem','problems have to do with the network and/or internet');
INSERT INTO CATEGORIES values(3,'Windows OS problem','problems have to do with Windows OS maintenance');
INSERT INTO CATEGORIES values(4,'Unix/Linux OS problem','problems have to do with Unix/Linux OS maintenance');
INSERT INTO CATEGORIES values(5,'Hardware Problem','HW maintenance issues');
INSERT INTO CATEGORIES values(6,'Software Problem','SW maintenance issues');
INSERT INTO CATEGORIES values(7,'Server Problem','Any problem has to do with the dept. servers');
INSERT INTO CATEGORIES values(8,'Software install','Requests to install additional SW');
INSERT INTO CATEGORIES values(9,'Web Problem','Any problem has to do with the dept. web sites');
INSERT INTO CATEGORIES values(10,'Windows Help',' Questions about Windows (hotline support');
INSERT INTO CATEGORIES values(11,'Unix/Linux Help','Questions about Unix/Linux (hotline support)');
INSERT INTO CATEGORIES values(12,'SW Help','Questions about installed SW (hotline support)');
INSERT INTO CATEGORIES values(13,'AFS problem', 'Prolbmes related to the AFS system.');

INSERT INTO LOCATIONS values(1, '6th floor', 'SENSQ','db research lab');
INSERT INTO LOCATIONS values(2, '6th floor', 'SENSQ', 'os research lab');
INSERT INTO LOCATIONS values(3, '5th floor', 'SENSQ', 'arch research lab');
INSERT INTO LOCATIONS values(4, '6th floor', 'SENSQ', 'java lab');
INSERT INTO LOCATIONS values(5, '5th floor', 'SENSQ', 'reading room');

INSERT INTO INVENTORY values('acta.cs.pitt.edu', '102.168.222.226', 'sq6-52', '01:23:45:67:89:ab', 1);
INSERT INTO INVENTORY values('kaly.cs.pitt.edu', '102.168.222.227', 'sq5-14', '01:23:45:67:89:ac', 3);
INSERT INTO INVENTORY values('elements.cs.pitt.edu', '102.168.222.228', 'sq5-23', '01:23:45:67:89:ad', 5);
INSERT INTO INVENTORY values('kaso.cs.pitt.edu', '102.168.222.229', 'sq6-22', '01:23:45:67:89:ae', 2);
INSERT INTO INVENTORY values('oxgyn.cs.pitt.edu', '102.168.212.216', 'sq5-17', '01:23:45:67:89:af', 3);
INSERT INTO INVENTORY values('rodi.cs.pitt.edu', '102.168.212.217', 'sq6-51', '01:23:45:67:89:ba', 1);
INSERT INTO INVENTORY values('java lab machine', '102.168.212.220', 'sq6-41', '01:23:45:67:89:bb', 4);


INSERT INTO TICKETS values(000000567841,5550001,'20-DEC-19','28-DEC-19',9,5, 'acta.cs.pitt.edu',  'The fan is noisy.');
INSERT INTO TICKETS values(000000567842,5550002,'20-DEC-19','22-DEC-19',3,1, 'kaly.cs.pitt.edu',  'Can not print any PDF file!');
INSERT INTO TICKETS values(000000567843,5550002,'23-DEC-19',NULL,NULL,6,'elements.cs.pitt.edu',  'Seems that the server is down?!');
INSERT INTO TICKETS values(000000567844,5550001,'28-DEC-19','31-DEC-19',4,6,'acta.cs.pitt.edu', 'The AFS client crashes all the time!');
INSERT INTO TICKETS values(000000567845,5550003,'01-JAN-20','05-JAN-20',5,4,'kaso.cs.pitt.edu','The mouse responds in a weird way.');
INSERT INTO TICKETS values(000000567846,5550002,'02-JAN-20','02-JAN-20',1,2,'oxgyn.cs.pitt.edu',  'can not connect to the oxgyn server!');
INSERT INTO TICKETS values(000000567847,5550001,'05-JAN-20','06-JAN-20',2,8,'acta.cs.pitt.edu', 'I failed to login to the evaluation site.');
INSERT INTO TICKETS values(000000567848,5550003,'05-JAN-20','12-JAN-20',8,7,'kaso.cs.pitt.edu', 'May I have SigmaPlot installed on my machine?');
INSERT INTO TICKETS values(000000567849,5550003,'13-JAN-20',NULL,NULL,5,'kaso.cs.pitt.edu','Some keys on the keyboard does not function. ');
INSERT INTO TICKETS values(000000567850,5550004,'14-JAN-20',NULL,NULL,10,'rodi.cs.pitt.edu','The sound card is unrecognized.');
INSERT INTO TICKETS values(000000567851,5550001,'14-JAN-20','23-JAN-20',10,5,'acta.cs.pitt.edu','The harddrive got bad sector.');
INSERT INTO TICKETS values(000000567852,5550003,'14-JAN-20','17-JAN-20',4,3,'kaso.cs.pitt.edu','Windows crashed!');
INSERT INTO TICKETS values(000000567853,5550003,'15-JAN-20','21-JAN-20',7,12,'kaso.cs.pitt.edu','can not reach AFS-home directory from home.');
INSERT INTO TICKETS values(000000567854,5550001,'16-JAN-20','17-JAN-20',2,6,'acta.cs.pitt.edu','I have trouble configuring Outlook.');
INSERT INTO TICKETS values(000000567855,5550003,'20-JAN-20',NULL,NULL,3,'kaso.cs.pitt.edu','Windows crashed!');
INSERT INTO TICKETS values(000000567856,5550004,'25-JAN-20',NULL,NULL,8,'java lab machine','Can not submit my h/w using the submission site.');

INSERT INTO ASSIGNMENTS values(000000567841,1110001,'20-DEC-19','delegated',NULL);
INSERT INTO ASSIGNMENTS values(000000567841,1110002,'24-DEC-19','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567842,1110002,'20-DEC-19','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567843,1110001,'23-DEC-19','assigned',NULL);
INSERT INTO ASSIGNMENTS values(000000567844,1110003,'28-DEC-19','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567845,1110003,'01-JAN-20','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567846,1110002,'02-JAN-20','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567847,1110001,'05-JAN-20','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567848,1110003,'05-JAN-20','delegated',NULL);
INSERT INTO ASSIGNMENTS values(000000567848,1110001,'08-JAN-20','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567849,1110003,'13-JAN-20','in_progress',NULL);
INSERT INTO ASSIGNMENTS values(000000567850,1110004,'14-JAN-20','in_progress',NULL);
INSERT INTO ASSIGNMENTS values(000000567851,1110001,'14-JAN-20','delegated',NULL);
INSERT INTO ASSIGNMENTS values(000000567851,1110002,'20-JAN-20','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567852,1110003,'14-JAN-20','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567853,1110003,'15-JAN-20','delegated',NULL);
INSERT INTO ASSIGNMENTS values(000000567853,1110001,'18-JAN-20','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567854,1110001,'16-JAN-20','closed_successful',NULL);
INSERT INTO ASSIGNMENTS values(000000567855,1110001,'20-JAN-20','assigned',NULL);
INSERT INTO ASSIGNMENTS values(000000567856,1110003,'25-JAN-20','delegated',NULL);
INSERT INTO ASSIGNMENTS values(000000567856,1110004,'27-JAN-20','assigned',NULL);


