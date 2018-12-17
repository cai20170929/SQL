-- q3
-- Find percentage of departing flights < 3 hr or not NULL
-- output columns origin_city and percentage
-- order by percentage value
-- treat origin(without any flights < 3 hr) -> NULL
-- treat actual_time(NULL) -> 181 min

select distinct f2.orig as origin_city, 
       (f1.nom * 100.0 / f2.denom) as percentage

from (select a.origin_city as orig,
             count(a.fid) as nom
      from flights as a
      where isnull(a.actual_time, 181) < 180
      group by a.origin_city) as f1 

	right outer join

    (select b.origin_city as orig,
            count(b.fid) as denom
     from flights as b
     group by b.origin_city) as f2
     
     on f1.orig=f2.orig

order by percentage;



