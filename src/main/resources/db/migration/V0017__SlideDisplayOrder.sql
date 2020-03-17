alter table slides add column display_order bigint not null;

set @index = 0;
update slides set display_order = (@index := @index + 1);