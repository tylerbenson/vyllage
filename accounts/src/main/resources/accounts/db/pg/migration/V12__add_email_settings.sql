insert into ACCOUNTS.account_setting(user_id, name, value, privacy) values (
( select user_id from accounts.users where user_name = 'advisor1@vyllage.com')
, 'email', 'advisor1@vyllage.com', 'public');

insert into ACCOUNTS.account_setting(user_id, name, value, privacy) values (
( select user_id from accounts.users where user_name = 'advisor2@vyllage.com')
, 'email', 'advisor2@vyllage.com', 'public');