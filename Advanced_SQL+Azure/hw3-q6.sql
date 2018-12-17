-- hw3-q6
-- List names of carriers 
-- Seattle WA -> San Francisco CA
-- Return distinct name
-- Use a nested query
-- output column carrier

select distinct c.name as carrier

from carriers as c

where c.cid in (select f.carrier_id
                from flights f
                where f.carrier_id=c.cid AND
                f.origin_city='"Seattle WA"' AND
                f.dest_city='"San Francisco CA"');