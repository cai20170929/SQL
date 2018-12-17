-- CSE414-cail4
-- HW1-Q6

select * from MyRestaurants
   where Like = 1 AND Date >= date('now', '-3 month');
