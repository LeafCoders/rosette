alter table assets add constraint uk_assets_fileid unique (file_id);

alter table assetfolders add column static_file_key bit default 0;