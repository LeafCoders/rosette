SET FOREIGN_KEY_CHECKS = 0;

delete from messages where is_useradded = 1;
delete from consents;

delete from slides;
delete from slideshows;

delete from podcasts;

delete from resourcerequirement_resources;
delete from resourcerequirements;

delete from events_table;

delete from article_authors;
delete from articles;
delete from articleseries;
delete from articletypes;

delete from resource_resourcetypes;
delete from resources;

delete from eventtype_resourcetypes;
delete from eventtypes;

delete from resourcetypes;

delete from permission_permissionsets;
delete from permissions;
delete from permissionsets;

delete from group_users;
delete from groups_table;

delete from users;

delete from assets;
delete from assetfolders;

SET FOREIGN_KEY_CHECKS = 1;
