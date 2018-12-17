-- hw3-q2
-- Find all origin distinct cities 
-- flights(null) < 3 hr
-- output column city
-- order by city and sort them

select distinct f1.origin_city as city

from flights as f1

group by f1.origin_city

having max(isnull(f1.actual_time, 0)) < 180

order by city;
