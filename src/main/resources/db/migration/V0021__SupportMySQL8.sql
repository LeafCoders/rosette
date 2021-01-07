-- MySQL 8.0.2 has added "groups" as reserved word.
alter table groups rename groups_table;

-- MySQL 5.7 didn't warn when we exceded to row size of 16K. We added two columns with varchar(10000).
-- MySQL 8 fails with an error.
alter table articles modify content_html text;
alter table articles modify content_raw text;

alter table articleseries modify content_html text;
alter table articleseries modify content_raw text;
