insert into users (email, first_name, last_name, password, is_active, version)
	values ("admin@user", "Admin", "User", "$2a$10$839AeSCQGBff8C6dPeGbG.bgmWR0pRkzjcEjLzLMAgfvkkd5Wd8aS", 1, 0);

insert into permissions (name, level, patterns, version)
	values ("Admin permissions", 1, "*", 0);
