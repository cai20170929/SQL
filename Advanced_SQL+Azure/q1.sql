-- q1
-- Find orig->dest city with longest direct flight in time
-- no duplicates of same origin/destination city pair
-- output columns origin_city, dest_city, time 
-- order by origin_city, dest_city

select distinct f1.orig as origin_city,
                f2.dest as dest_city, 
                f1.time as time

from (select a.origin_city as orig, 
             max(a.actual_time) as time
     from flights as a
     group by a.origin_city) as f1

    join
    
    (select b.origin_city as orig,
            b.dest_city as dest,
            b.actual_time as time
    from flights as b) as f2

    on f1.orig=f2.orig AND
       f1.time=f2.time
   
order by origin_city, dest_city;
