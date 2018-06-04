create table forgottenpassword (
	id bigint not null auto_increment,
	version integer not null,
	user_id bigint not null,
	token varchar(1000) not null,

	primary key (id)	
);
