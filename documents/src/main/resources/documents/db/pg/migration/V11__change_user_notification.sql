drop table documents.user_notification;

create table if not exists documents.comment_notification(
	comment_notification_id bigserial primary key,
	user_id bigint not null, -- the owner of the document
	comment_user_id bigint not null, -- the owner of the comment
	comment_id bigint not null,
	section_title varchar(50),
  	date_created timestamp not null
  	);

