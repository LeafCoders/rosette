alter table articles add column recording_status integer not null default 0;

alter table articletypes add column default_recording_status integer not null default 0;
