.table

DROP TABLE IF EXISTS Sales;

PRAGMA foreign_keys=ON;

--Q1
create table Sales (
    name varchar(100),
    discount varchar(10),
    month varchar(10),
    price int,
    primary key (name, month)
);

.separator "\t"
.import "submission/mrFrumbleData.txt" Sales

delete
from Sales
where name = 'name';

.nullvalue NULL
.headers on
.mode column

--select * from Sales;
--select distinct name from Sales;

-- Q2
-- Two main fds:
-- fd: name -> price 
-- fd: month -> discount

-- name -> price
-- 0
select count(*)
from Sales f1, Sales f2
where f1.name = f2.name AND
      f1.price != f2.price;

-- month -> discount
-- 0
select count(*)
from Sales f1, Sales f2
where f1.month = f2.month AND
      f1.discount != f2.discount;


-- Q3
DROP TABLE IF EXISTS R22;
DROP TABLE IF EXISTS R21;
DROP TABLE IF EXISTS R1;

create table R1(
    name varchar(100) primary key,
    price int
);

create table R21(
    month varchar(10) primary key,
    discount varchar(10)
);

create table R22(
    month varchar(10),
    name varchar(100),
    foreign key (month) references R21(month),
    foreign key (name) references R1(name),
    primary key (month, name)
);

-- Q4
insert into R1
select distinct name, price
from Sales;

-- 36
select count(*) as R1_C
from R1;

insert into R21
select distinct month, discount
from Sales;

-- 12
select count(*) as R21_C
from R21;

insert into R22
select distinct month, name
from Sales;

-- 426
select count(*) as R22_C
from R22;
