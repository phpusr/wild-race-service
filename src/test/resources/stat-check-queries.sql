-- find out of sync
select from_id, distance, sum_distance,
       sum(distance) over (partition by from_id order by date) as sum2,
       text
from post
where distance is not null and id >= 78 and id <= 175
order by sum2 desc;

-- running
select id, date, from_id, distance, sum_distance, text from post
where number is not null and sum_distance - distance >= 200 and sum_distance - distance < 500
order by date;

-- start date: 2015-09-01 03:56:09
-- end date:   2015-09-24 10:52:34

-- sum distance
select sum(distance) from post where id <= 175;

-- start date
select date from post where number is not null
--and date >= '2015-09-10'
and sum_distance - distance >= 200
order by date limit 1;

-- end date
select date from post where number is not null
--and date < '2015-09-21'
and sum_distance - distance < 500
order by date desc limit 1;

-- days count all
select '2015-09-13 05:43:22.000000'::timestamp - '2015-09-06 12:00:29.000000'::timestamp;

-- training count
select count(number) from post;

-- distance max
select from_id, sum(distance) as sm, count(distance) as tc from post
where distance is not null group by from_id order by sm desc limit 5;
-- training max
select from_id, count(distance) as tc, sum(distance) as sm from post
where distance is not null group by from_id order by tc desc limit 5;
-- runners
select count(distinct from_id) from post
where
      id >= 78 and id <= 175 and
      number is not null;
-- new runners
SELECT array_to_string(
           array(
               select from_id
               from post left join profile pr on from_id = pr.id
               where number is not null and join_date >= '2015-09-06 12:00:29.000000' and join_date <= '2015-09-13 05:43:22.000000'
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
               and id >= 78 and id <= 175
               group by from_id
               order by sum(distance) desc
               limit 5
             ), ', '
         );


