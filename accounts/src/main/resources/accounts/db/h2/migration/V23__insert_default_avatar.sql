insert into accounts.account_setting(user_id, name, value, privacy) 
select distinct u.user_id, 'avatar', 'gravatar', 'public' 
from accounts.users u
join accounts.account_setting ac
on u.user_id = ac.user_id 
where
u.user_id not in (select user_id from accounts.account_setting where name = 'avatar'); 