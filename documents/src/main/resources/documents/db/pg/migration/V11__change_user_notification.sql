drop table documents.user_notification;

create table if not exists documents.comment_notification(
	comment_notification_id bigserial primary key,
	user_id bigint not null, -- the owner of the document
	comment_id bigint not null,
  	date_created timestamp not null
  	);

