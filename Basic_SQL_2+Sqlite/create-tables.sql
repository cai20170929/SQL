-- CSE414-cail4
-- HW2-Q0
-- Create tables for flights, carriers, 
-- months and weekdays.

.table

PRAGMA foreign_keys=ON;

create table FLIGHTS (
   fid int primary key,
   month_id int references Months,        -- 1-12
   day_of_month int,    -- 1-31
   day_of_week_id int references Weekdays,  -- 1-7, 1 = Monday, 2 = Tuesday, etc
   carrier_id varchar(7) references Carriers,
   flight_num int,
   origin_city varchar(34),
   origin_state varchar(47),
   dest_city varchar(34),
   dest_state varchar(46),
   departure_delay int, -- in mins
   taxi_out int,        -- in mins
   arrival_delay int,   -- in mins
   canceled int,        -- 1 means canceled
   actual_time int,     -- in mins
   distance int,        -- in miles
   capacity int,
   price int            -- in $
);

create table CARRIERS (cid varchar(7) primary key, name varchar(83));

create table MONTHS (mid int primary key, month varchar(9));

create table WEEKDAYS (did int primary key, day_of_week varchar(9));

.mode csv
.import ../starter-code/carriers.csv carriers
.import ../starter-code/months.csv months
.import ../starter-code/weekdays.csv weekdays

.import ../starter-code/flights-small.csv flights

.nullvalue NULL
.headers on
.mode column
