create table permissionsets (
	id bigint not null auto_increment,
	version integer not null,
	name varchar(200) not null,
	patterns varchar(200),

	primary key (id),
	constraint uk_permissionsets_name unique (name)
);

create table permission_permissionsets (
	permission_id bigint not null,
	permissionset_id bigint not null,

	constraint uk_permission_permissionsets unique (permission_id, permissionset_id)
);

alter table permission_permissionsets add constraint fk_permission_permissionsets_permissionsetid foreign key (permissionset_id) references permissionsets (id);
alter table permission_permissionsets add constraint fk_permission_permissionsets_permissionid foreign key (permission_id) references permissions (id);
