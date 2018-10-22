update events_table set start_time = DATE_SUB(start_time, INTERVAL 1 HOUR);
update events_table set start_time = DATE_SUB(start_time, INTERVAL 1 HOUR) where start_time between "2014-03-30 02:00" and "2014-10-26 03:00";
update events_table set start_time = DATE_SUB(start_time, INTERVAL 1 HOUR) where start_time between "2015-03-29 02:00" and "2015-10-25 03:00";
update events_table set start_time = DATE_SUB(start_time, INTERVAL 1 HOUR) where start_time between "2016-03-27 02:00" and "2016-10-30 03:00";
update events_table set start_time = DATE_SUB(start_time, INTERVAL 1 HOUR) where start_time between "2017-03-26 02:00" and "2017-10-29 03:00";
update events_table set start_time = DATE_SUB(start_time, INTERVAL 1 HOUR) where start_time between "2018-03-25 02:00" and "2018-10-28 03:00";
update events_table set start_time = DATE_SUB(start_time, INTERVAL 1 HOUR) where start_time between "2019-03-31 02:00" and "2019-10-27 03:00";

update events_table set end_time = DATE_SUB(end_time, INTERVAL 1 HOUR);
update events_table set end_time = DATE_SUB(end_time, INTERVAL 1 HOUR) where end_time between "2014-03-30 02:00" and "2014-10-26 03:00";
update events_table set end_time = DATE_SUB(end_time, INTERVAL 1 HOUR) where end_time between "2015-03-29 02:00" and "2015-10-25 03:00";
update events_table set end_time = DATE_SUB(end_time, INTERVAL 1 HOUR) where end_time between "2016-03-27 02:00" and "2016-10-30 03:00";
update events_table set end_time = DATE_SUB(end_time, INTERVAL 1 HOUR) where end_time between "2017-03-26 02:00" and "2017-10-29 03:00";
update events_table set end_time = DATE_SUB(end_time, INTERVAL 1 HOUR) where end_time between "2018-03-25 02:00" and "2018-10-28 03:00";
update events_table set end_time = DATE_SUB(end_time, INTERVAL 1 HOUR) where end_time between "2019-03-31 02:00" and "2019-10-27 03:00";

update slides set start_time = DATE_SUB(start_time, INTERVAL 1 HOUR);
update slides set end_time = DATE_SUB(start_time, INTERVAL 1 HOUR);

update articles set time = DATE_SUB(time, INTERVAL 1 HOUR);
