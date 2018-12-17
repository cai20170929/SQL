-- CSE414-cail4
-- HW2-Q3
-- Find the day of the week
-- the longest average arrival delay
-- return day_of_week and delay

select w.day_of_week as day_of_week, 
       avg(f.arrival_delay) as delay

from flights as f, weekdays as w

where f.day_of_week_id = w.did

group by f.day_of_week_id

order by delay desc

limit 1;
