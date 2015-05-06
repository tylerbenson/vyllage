create table if not exists DOCUMENTS.user_notification(
	user_id bigint primary key,
  	date_created timestamp not null);