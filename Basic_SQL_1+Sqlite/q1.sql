-- Q1

create table Edges(
   Source int,
   Destination int);

insert into Edges values(10,5);
insert into Edges values(6,25);
insert into Edges values(1,3);
insert into Edges values(4,4);

select * from Edges;

select Source from Edges;

select * from Edges where Source > Destination;

insert into Edges values('-1','2000'); 
-- This insert is ok: '-1' is integer, because INTEGER is a signed -- integer type, negative int is still integer;
-- the string 'int' is assigned integer affinity. 
-- Both are integer.



