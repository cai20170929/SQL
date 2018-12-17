-- CSE414-cail4
-- HW2-Q8
-- Compute total departure delay
-- return name and delay

select c.name as name,
       sum(f.departure_delay) as delay

from flights as f, carriers as c

where f.carrier_id = c.cid

group by name

order by name;
