create table article_authors (
	article_id bigint not null,
	authors_id bigint not null,
	
	constraint uk_article_authors unique (article_id, authors_id)
);

create table articles (
	id bigint not null auto_increment,
	version integer not null,
	last_modified_time datetime not null,
	articletype_id bigint not null,
	articleserie_id bigint not null,
	time datetime not null,
	title varchar(200) not null,
	content_raw text,
	content_html text,
	event_id bigint,
	recording_id bigint,
	
	primary key (id)
);

create table articleseries (
	id bigint not null auto_increment,
	version integer not null,
	id_alias varchar(32) not null,
	articletype_id bigint not null,
	content_raw text,
	content_html text,
	image_id bigint not null,
	title varchar(200) not null,

	primary key (id),
	constraint uk_articleseries_idalias unique (id_alias)
);

create table articletypes (
	id bigint not null auto_increment,
	version integer not null,
	id_alias varchar(32) not null,
	articles_title varchar(200) not null,
	article_series_title varchar(200) not null,
	new_article_title varchar(200) not null,
	new_article_serie_title varchar(200) not null,
	author_resourcetype_id bigint not null,
	imagefolder_id bigint not null,
	recordingfolder_id bigint not null,

	primary key (id),
	constraint uk_articletypes_idalias unique (id_alias)
);

create table assetfolders (
	id bigint not null auto_increment,
	version integer not null,
	id_alias varchar(32) not null,
	name varchar(200) not null,
	description varchar(200),
	allowed_mime_types varchar(255),

	primary key (id),
	constraint uk_assetfolders_idalias unique (id_alias),
	constraint uk_assetfolders_name unique (name)
);


create table assets (
	id bigint not null auto_increment,
	version integer not null,
	type integer not null,
	folder_id bigint,
	file_id varchar(220),
	file_name varchar(200),
	file_size bigint,
	mime_type varchar(200) not null,
	url varchar(200),
	width bigint,
	height bigint,
	duration bigint,

	primary key (id)
);

create table events_table (
	id bigint not null auto_increment,
	version integer not null,
	eventtype_id bigint not null,
	title varchar(200) not null,
	description varchar(200),
	start_time datetime not null,
	end_time datetime,

	primary key (id)
);

create table eventtype_resourcetypes (
	eventtype_id bigint not null,
	resourcetype_id bigint not null,
	
	constraint uk_eventtype_resourcetypes unique (eventtype_id, resourcetype_id)
);

create table eventtypes (
	id bigint not null auto_increment,
	version integer not null,
	id_alias varchar(32) not null,
	name varchar(200) not null,
	description varchar(200),

	primary key (id),
	constraint uk_eventtypes_idalias unique (id_alias),
	constraint uk_eventtypes_name unique (name)
);

create table group_users (
	group_id bigint not null,
	user_id bigint not null,

	constraint uk_group_users unique (group_id, user_id)
);

create table groups_table (
	id bigint not null auto_increment,
	version integer not null,
	id_alias varchar(32) not null,
	name varchar(200) not null,
	description varchar(200),

	primary key (id),
	constraint uk_groups_idalias unique (id_alias),
	constraint uk_groups_name unique (name)
);

create table permissions (
	id bigint not null auto_increment,
	version integer not null,
	name varchar(200) not null,
	level integer not null,
	entity_id bigint,
	patterns varchar(200),

	primary key (id)
);

create table podcasts (
	id bigint not null auto_increment,
	version integer not null,
	id_alias varchar(32) not null,
	articletype_id bigint not null,
	title varchar(200) not null,
	sub_title varchar(200) not null,
	author_name varchar(200) not null,
	copyright varchar(200) not null,
	description varchar(4000) not null,
	main_category varchar(200) not null,
	sub_category varchar(200) not null,
	language varchar(200) not null,
	link varchar(200) not null,
	image_id bigint not null,
	changed_date datetime,

	primary key (id)	,
	constraint uk_podcasts_idalias unique (id_alias)
);

create table resource_resourcetypes (
	resource_id bigint not null,
	resourcetype_id bigint not null,

	constraint uk_resource_resourcetypes unique (resource_id, resourcetype_id)
);

create table resourcerequirement_resources (
	resourcerequirement_id bigint not null,
	resource_id bigint not null,

	constraint uk_resourcerequirement_resources unique (resourcerequirement_id, resource_id)
);

create table resourcerequirements (
	id bigint not null auto_increment,
	version integer not null,
	event_id bigint not null,
	resourcetype_id bigint not null,

	primary key (id),
	constraint uk_resourcerequirements unique (event_id, resourcetype_id)
);

create table resources (
	id bigint not null auto_increment,
	version integer not null,
	name varchar(200) not null,
	description varchar(200),
	user_id bigint,
	
	primary key (id),
	constraint uk_resources_name unique (name)
);

create table resourcetypes (
	id bigint not null auto_increment,
	version integer not null,
	id_alias varchar(32) not null,
	name varchar(200) not null,
	description varchar(200),

	primary key (id),
	constraint uk_resourcetypes_idalias unique (id_alias),
	constraint uk_resourcetypes_name unique (name)
);

create table slides (
	id bigint not null auto_increment,
	version integer not null,
	slideshow_id bigint not null,
	title varchar(200) not null,
	start_time datetime not null,
	end_time datetime,
	duration integer not null,
	image_id bigint not null,

	primary key (id)
);

create table slideshows (
	id bigint not null auto_increment,
	version integer not null,
	assetfolder_id bigint not null,
	id_alias varchar(32) not null,
	name varchar(200) not null,
	
	primary key (id),
	constraint uk_slideshows_idalias unique (id_alias),
	constraint uk_slideshows_name unique (name)
);

create table users (
	id bigint not null auto_increment,
	version integer not null,
	email varchar(200) not null,
	first_name varchar(200) not null,
	last_name varchar(200) not null,
	password varchar(60) not null,
	is_active bit not null,
	last_login_time datetime,

	primary key (id),
	constraint uk_users_email unique (email)
);


alter table article_authors add constraint fk_article_authors_authorsid foreign key (authors_id) references resources (id);
alter table article_authors add constraint fk_article_authors_articleid foreign key (article_id) references articles (id);
alter table articles add constraint fk_articles_articleserieid foreign key (articleserie_id) references articleseries (id);
alter table articles add constraint fk_articles_articletypeid foreign key (articletype_id) references articletypes (id);
alter table articles add constraint fk_articles_eventid foreign key (event_id) references events_table (id);
alter table articles add constraint fk_articles_recordingid foreign key (recording_id) references assets (id);
alter table articleseries add constraint fk_articleseries_articletypeid foreign key (articletype_id) references articletypes (id);
alter table articleseries add constraint fk_articleseries_imageid foreign key (image_id) references assets (id);
alter table articletypes add constraint fk_articletypes_authorresourcetypeid foreign key (author_resourcetype_id) references resourcetypes (id);
alter table articletypes add constraint fk_articletypes_imagefolderid foreign key (imagefolder_id) references assetfolders (id);
alter table articletypes add constraint fk_articletypes_recordingfolderid foreign key (recordingfolder_id) references assetfolders (id);

alter table events_table add constraint fk_events_table_eventtypeid foreign key (eventtype_id) references eventtypes (id);
alter table eventtype_resourcetypes add constraint fk_eventtype_resourcetypes_resourcetypeid foreign key (resourcetype_id) references resourcetypes (id);
alter table eventtype_resourcetypes add constraint fk_eventtype_resourcetypes_eventtypeid foreign key (eventtype_id) references eventtypes (id);

alter table group_users add constraint fk_group_users_userid foreign key (user_id) references users (id);
alter table group_users add constraint fk_group_users_groupid foreign key (group_id) references groups_table (id);

alter table podcasts add constraint fk_podcasts_articletypeid foreign key (articletype_id) references articletypes (id);
alter table podcasts add constraint fk_podcasts_imageid foreign key (image_id) references assets (id);

alter table resource_resourcetypes add constraint fk_resource_resourcetypes_resourcetypeid foreign key (resourcetype_id) references resourcetypes (id);
alter table resource_resourcetypes add constraint fk_resource_resourcetypes_resourceid foreign key (resource_id) references resources (id);
alter table resourcerequirement_resources add constraint fk_resourcerequirement_resources_resourceid foreign key (resource_id) references resources (id);
alter table resourcerequirement_resources add constraint fk_resourcerequirement_resources_resourcerequirementid foreign key (resourcerequirement_id) references resourcerequirements (id);
alter table resourcerequirements add constraint fk_resourcerequirements_eventid foreign key (event_id) references events_table (id);
alter table resourcerequirements add constraint fk_resourcerequirements_resourcetypeid foreign key (resourcetype_id) references resourcetypes (id);
alter table resources add constraint fk_resources_userid foreign key (user_id) references users (id);

alter table slides add constraint fk_slides_imageid foreign key (image_id) references assets (id);
alter table slides add constraint fk_slides_slideshowid foreign key (slideshow_id) references slideshows (id);
alter table slideshows add constraint fk_slideshows_assetfolderid foreign key (assetfolder_id) references assetfolders (id);
