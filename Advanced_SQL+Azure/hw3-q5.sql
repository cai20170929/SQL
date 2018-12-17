-- hw3-q5
-- List all cities(NOT Seattle) <- 2 stop(NOT Seattle) <- Seattle WA
-- output column city
-- collection of origin_city
select distinct f3.dest_city as city

from flights as f3

where f3.dest_city not in (select distinct a.dest_city
                           from flights a
                           where a.origin_city='"Seattle WA"'
                           UNION
                           select distinct b.dest_city
                           from flights as a, flights as b
                           where a.origin_city='"Seattle WA"' AND
                                 a.dest_city!='"Seattle WA"' AND
                                 a.dest_city=b.origin_city)
order by city;

-- collection of dest_city                                 
--select distinct f1.origin_city as city

--from flights as f1

--where f1.origin_city not in (select distinct a.origin_city
--                             from flights a
--                             where a.dest_city='"Seattle WA"'
--                             UNION
--                             select distinct b.dest_city
--                             from flights as a, flights as b
--                             where a.origin_city='"Seattle WA"' AND
--                                   a.dest_city!='"Seattle WA"' AND
--                                   a.dest_city=b.origin_city)
--order by city;
     