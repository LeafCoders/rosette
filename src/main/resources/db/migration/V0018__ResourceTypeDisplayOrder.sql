alter table resourcetypes add column display_order bigint not null;

set @index = 0;
update resourcetypes set display_order = (@index := @index + 1);