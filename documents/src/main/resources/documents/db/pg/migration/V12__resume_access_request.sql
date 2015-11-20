create table if not exists documents.resume_access_request_notification(
	resume_access_request_notification_id bigserial primary key,
	user_id bigint not null, -- the owner of the notification 
	resume_request_user_id bigint not null, --the user that generated the notification
  	date_created timestamp not null
  	);