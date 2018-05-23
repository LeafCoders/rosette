alter table events_table add column is_public bit not null default 1;
alter table eventtypes add column is_public bit not null default 1;
