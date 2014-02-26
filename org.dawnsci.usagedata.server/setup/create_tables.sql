# mysql -u root -p < create_tables.sql

create database if not exists udc;
use udc;

#create user 'udc_user'@'localhost' identified by 'udc_password';
#grant ALL on *.* to 'udc_user'@'localhost';

# geoip table contains the mappings from ip address to country code (ccode).
drop table if exists geoip;
create table geoip (start int unsigned, end int unsigned, ccode char(2));

# usagedata_profile table contains an entry for each userid/workstationid pair.
drop table if exists usagedata_profile;
create table usagedata_profile (
	id int unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, 
	userId char(40) NOT NULL,
	workspaceId char(40) NOT NULL
) Engine=MyISAM;

# usagedata_upload table contains an entry for each upload event.
drop table if exists usagedata_upload;
create table usagedata_upload (
	id int unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
	profileId int unsigned NOT NULL,
	ccode char(2) NULL,
	time timestamp DEFAULT CURRENT_TIMESTAMP
) Engine=MyISAM;
CREATE INDEX IDX_profileId ON usagedata_upload(profileId);
CREATE INDEX IDX_ccode ON usagedata_upload(ccode);

#usagedata_record table contains a record for each record of usage data.
drop table if exists usagedata_record;
create table usagedata_record (
	id int unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
	uploadId int unsigned NOT NULL,
	what varchar(256),
	kind varchar(256),
	bundleId varchar(256),
	bundleVersion varchar(256),
	description varchar(256),
	time bigint unsigned NOT NULL
)  Engine=MyISAM;
CREATE INDEX IDX_uploadId ON usagedata_record(uploadId);
CREATE INDEX IDX_kind_bundleId ON usagedata_record(kind,bundleId);
CREATE INDEX IDX_bundleId ON usagedata_record(bundleId);
