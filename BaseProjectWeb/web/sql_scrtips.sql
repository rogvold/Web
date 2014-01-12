--alter table users add column userinfoupdatecount integer;
--update users set userinfoupdatecount = 0;

-- alter table users add column sessionsupdatecount bigint;

alter table sessionshistory add column updatedsessions varchar(50000);

ALTER TABLE sessionshistory
DROP COLUMN updatedsessions