-- CSE414-cail4
-- HW2-Q6
-- Find max price 
-- Seattle WA <-> New York NY
-- return carrier and max_price

select c.name as carrier, 
       max(f.price) as max_price

from flights as f, carriers as c

where f.carrier_id = c.cid AND
      (f.origin_city = 'Seattle WA' OR
      f.origin_city = 'New York NY') AND
      (f.dest_city = 'Seattle WA' OR
      f.dest_city = 'New York NY')

group by carrier

order by max_price;
