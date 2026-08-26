ALTER TABLE login_log DROP COLUMN login_from;
alter table if exists login_log add column login_from varchar(255) check ((login_from in ('WEB','IOS','ANDROID')));