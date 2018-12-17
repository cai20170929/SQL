-- CSE414-cail4
-- HW2-Q7
-- Find total capacity 
-- Seattle WA <-> San Francisco CA
-- July 10th
-- return capacity

select sum(f.capacity) as capacity

from flights as f, carriers as c, months as m

where f.carrier_id = c.cid AND
      f.month_id = m.mid AND
      (f.origin_city = 'Seattle WA' OR
      f.origin_city = 'San Francisco CA') AND
      (f.dest_city = 'Seattle WA' OR
      f.dest_city = 'San Francisco CA') AND
      m.month = 'July' AND
      f.day_of_month = 10;
