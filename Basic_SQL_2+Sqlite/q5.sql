-- Q5
-- Find all airlines
-- out of Seattle WA
-- canceled percent > 0.5
-- return name and percent asc

select c.name as name, 
       avg(f.canceled*1.0/100) as percent

from flights as f, carriers as c

where f.carrier_id = c.cid AND
      f.origin_city = 'Seattle WA'

group by name

having percent > 0.005

order by percent asc;
