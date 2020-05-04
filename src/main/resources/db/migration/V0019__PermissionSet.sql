create table permissionsets (
	id bigint not null auto_increment,
	version integer not null,
	name varchar(200) not null,
	patterns varchar(200),

	primary key (id)
);
