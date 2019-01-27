-- find out of sync
select id, number,
       row_number() over (),
       distance, sum_distance,
       sum(distance) over (order by id) as sum2,
       text, edit_reason, last_update
from post
where distance is not null order by id;



-- sum distance
select sum(distance) from post;
-- start date
select date from post where number is not null order by date limit 1;
-- end date
select date from post where number is not null order by date desc limit 1;
-- days count all
select '2015-09-24 10:52:34'::timestamp - '2015-09-01 03:56:09'::timestamp;
-- training count
select count(number) from post;
-- distance max
select from_id, sum(distance) as sm, count(distance) as tc from post
where distance is not null group by from_id order by sm desc limit 5;
-- training max
select from_id, count(distance) as tc, sum(distance) as sm from post
where distance is not null group by from_id order by tc desc limit 5;
-- runners
select count(distinct from_id) from post where number is not null;
-- new runners
SELECT array_to_string(
           array(
               select from_id
               from post left join profile pr on from_id = pr.id
               where number is not null and join_date >= '2015-09-01 03:56:09' and join_date <= '2015-09-24 10:52:34'
               group by from_id, join_date
               order by join_date
               limit 25
             ), ', '
         );
-- top runners
SELECT array_to_string(
           array(
               select row(from_id, count(distance), sum(distance))
               from post
               where distance is not null
               group by from_id
               order by sum(distance) desc
               limit 5
             ), ', '
         );


