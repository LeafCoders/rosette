alter table podcasts add column author_email varchar(200) not null;
alter table podcasts change link author_link varchar(200);
alter table podcasts add column articles_link varchar(200) not null;
