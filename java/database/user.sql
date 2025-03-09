-- ********************************************************************************
-- This script creates the database users and grants them the necessary permissions
-- ********************************************************************************

CREATE USER servease_owner
WITH PASSWORD 'serveasepassword';

GRANT ALL
ON ALL TABLES IN SCHEMA public
TO servease_owner;

GRANT ALL
ON ALL SEQUENCES IN SCHEMA public
TO servease_owner;

CREATE USER servease_appuser
WITH PASSWORD 'serveasepassword';

GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public
TO servease_appuser;

GRANT USAGE, SELECT
ON ALL SEQUENCES IN SCHEMA public
TO servease_appuser;

