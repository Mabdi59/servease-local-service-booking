-- **************************************************************
-- This script destroys the database and associated users
-- **************************************************************

-- The following line terminates any active connections to the database so that it can be destroyed
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'servease_db';

DROP DATABASE servease_db;

DROP USER servease_owner;
DROP USER servease_appuser;

