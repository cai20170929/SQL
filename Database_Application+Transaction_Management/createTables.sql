-- add all your SQL setup statements here. 

-- You can assume that the following base table has been created with data loaded for you when we test your submission 
-- (you still need to create and populate it in your instance however),
-- although you are free to insert extra ALTER COLUMN ... statements to change the column 
-- names / types if you like.

--FLIGHTS (fid int, 
--         month_id int,        -- 1-12
--         day_of_month int,    -- 1-31 
--         day_of_week_id int,  -- 1-7, 1 = Monday, 2 = Tuesday, etc
--         carrier_id varchar(7), 
--         flight_num int,
--         origin_city varchar(34), 
--         origin_state varchar(47), 
--         dest_city varchar(34), 
--         dest_state varchar(46), 
--         departure_delay int, -- in mins
--         taxi_out int,        -- in mins
--         arrival_delay int,   -- in mins
--         canceled int,        -- 1 means canceled
--         actual_time int,     -- in mins
--         distance int,        -- in miles
--         capacity int, 
--         price int            -- in $             
--         )
-- create table Carriers(cid varchar(7) primary key, name varchar(83));

-- DROP TABLE IF EXISTS Rese;
-- DROP TABLE IF EXISTS ReseNum;
-- DROP TABLE IF EXISTS Users;
-- DROP TABLE IF EXISTS Capa;

create table Users(
    username varchar(20) not null primary key,
    password varchar (20),
    balance int
);

create table Rese(
    rid int not null primary key,
    username varchar(20) references users (username),
    paid int, -- 1 means paid, 0 means not paid
    cost int,
    ffid int, -- first flight id
    sfid int,  -- second flight id
    day int,
    cancel int -- 1 means canceled, 0 means not canceled
);

create table Capa(
    fid int not null primary key,
    capacity int
);

create table ReseNum(
    rid int not null primary key
);