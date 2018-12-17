-- q4
-- List all cities(NOT Seattle) <- 1 stop(NOT Seattle) <- Seattle WA
-- output column city

select distinct f2.dest_city as city

from flights as f1, flights as f2

where f1.origin_city = '"Seattle WA"' AND
      f1.dest_city != '"Seattle WA"' AND
      f1.dest_city = f2.origin_city AND
      f2.dest_city != '"Seattle WA"' AND
      f2.dest_city NOT IN
      
      (select distinct a.dest_city
      from flights as a
      where a.origin_city = '"Seattle WA"')

order by city;
