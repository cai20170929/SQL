-- hw3-q7
-- List names of carriers 
-- Seattle WA -> San Francisco CA
-- Return distinct name
-- NO nested query
-- output column carrier

select distinct c.name as carrier

from flights as f, carriers as c

where f.carrier_id=c.cid AND
      f.origin_city='"Seattle WA"' AND
      f.dest_city='"San Francisco CA"';
