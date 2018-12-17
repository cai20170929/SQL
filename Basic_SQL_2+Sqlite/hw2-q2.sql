-- CSE414-cail4
-- HW2-Q2
-- Find all itineraries 
-- Seattle WA -> 1 stop -> Boston MA
-- on July 15th same day
-- total flight time < 7*60
-- return name, actual_time
-- f1_flight_num, f1_origin_city, f1_dest_city, f1_actual_time, 
-- f2_flight_num, f2_origin_city, f2_dest_city, f2_actual_time

select c.name as name,
       f1.flight_num as f1_flight_num,
       f1.origin_city as f1_origin_city,
       f1.dest_city as f1_dest_city,
       f1.actual_time as f1_actual_time,
       f2.flight_num as f2_flight_num, 
       f2.origin_city as f2_origin_city, 
       f2.dest_city as f2_dest_city, 
       f2.actual_time as f2_actual_time,
       (f1.actual_time + f2.actual_time) as actual_time 
 
from flights as f1, flights as f2,
     carriers as c, months as m

where m.month = 'July' AND
      f1.carrier_id = c.cid AND
      f2.carrier_id = c.cid AND
      f1.month_id = m.mid AND
      f2.month_id = m.mid AND
      f1.day_of_month = 15 AND
      f2.day_of_month = 15 AND
      f1.origin_city = 'Seattle WA' AND
      f2.dest_city = 'Boston MA' AND
      f1.dest_city = f2.origin_city AND
      f1.carrier_id = f2.carrier_id AND
      f1.carrier_id = c.cid AND
      (f1.actual_time + f2.actual_time) < 7*60;
