drop table documents.user_notification;

create table if not exists documents.comment_notification(
	comment_notification_id bigserial primary key,
	user_id bigint not null, -- the owner of the document
	comment_user_id bigint not null, -- the owner of the comment, the user that generated the notification
	comment_id bigint not null,
	section_title varchar(50),
  	date_created timestamp not null
  	);
  	
create table if not exists documents.feedback_request_notification(
	feedback_request_notification_id bigserial primary key,
	user_id bigint not null, -- the owner of the notification 
	resume_user_id bigint not null, --the user that generated the notification
	resume_id bigint not null,
  	date_created timestamp not null
  	);
  	
create table if not exists documents.reference_request_notification(
	reference_request_notification_id bigserial primary key,
	user_id bigint not null, -- the owner of the notification 
	reference_request_user_id bigint not null, --the user that generated the notification
  	date_created timestamp not null
  	);
