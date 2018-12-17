-- CSE414-cail4
-- HW2-Q1
-- List distinct flight numbers 
-- Seattle WA -> Boston MA
-- by Alaska Airlines Inc.
-- on Mondays
-- output flight_num

select distinct F.flight_num as flight_num
--     ,W.day_of_week,C.name,F.origin_city,F.dest_city 

from flights as F, 
     carriers as C,
     weekdays as W

where F.carrier_id = C.cid AND
      F.day_of_week_id = W.did AND
      W.day_of_week = 'Monday' AND
      C.name = 'Alaska Airlines Inc.' AND
      F.origin_city = 'Seattle WA' AND
      F.dest_city = 'Boston MA';
