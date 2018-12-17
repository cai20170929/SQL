-- CSE414-cail4
-- HW1-Q4

.headers ON
-- comma-separated form
.mode csv
select * from MyRestaurants;

-- list form
.mode list
select * from MyRestaurants;

-- column form
.mode column
.width 15 15 15 15 15
select * from MyRestaurants;

.headers OFF
-- comma-separated form
.mode csv
select * from MyRestaurants;

-- list form
.mode list
select * from MyRestaurants;

-- column form
.mode column
.width 15 15 15 15 15
select * from MyRestaurants;