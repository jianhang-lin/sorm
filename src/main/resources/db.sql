create table if not exists sorm.dept
(
	id int(10) not null,
	dname varchar(30) null,
	address varchar(100) null
);

create table if not exists sorm.emp
(
	id int(10) not null,
	empname varchar(20) null,
	salary double null,
	birthday date null,
	age int null,
	deptId int null
);