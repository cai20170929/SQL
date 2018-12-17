-- CSE414-cail4
-- HW2-Q4
-- Find names of all airlines
-- flew > 1000 flights one day
-- return distinct name

select distinct c.name as name

from flights as f, carriers as c

where f.carrier_id = c.cid

group by f.carrier_id, f.month_id, f.day_of_month

having count(name) > 1000;
