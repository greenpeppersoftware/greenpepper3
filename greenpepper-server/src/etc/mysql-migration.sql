--------------------------------------------------------------------------------
-- MySql migration from <= 1.3 to next release
--------------------------------------------------------------------------------
-- Drop constraints 
ALTER TABLE EXECUTION DROP FOREIGN KEY FK65111AF8FCEAA1D7;
ALTER TABLE REFERENCE DROP FOREIGN KEY FK6EF34F2BFCEAA1D7;
ALTER TABLE RUNNER_classpaths DROP FOREIGN KEY FKDEF546E52522970F;
ALTER TABLE SYSTEM_UNDER_TEST DROP FOREIGN KEY FK60BA29C9FE6FA665;
ALTER TABLE SYSTEM_UNDER_TEST DROP FOREIGN KEY FK60BA29C92522970F;
ALTER TABLE SYSTEM_UNDER_TEST_sutClasspaths DROP FOREIGN KEY FK9CEBA672FCEAA1D7;
ALTER TABLE SYSTEM_UNDER_TEST_fixtureClasspaths DROP FOREIGN KEY FKD04AD469FCEAA1D7;
ALTER TABLE SUT_SPECIFICATION DROP FOREIGN KEY FK6176AD96FCEAA1D7;

-- Drop indexes
ALTER TABLE EXECUTION DROP INDEX FK65111AF8FCEAA1D7;
ALTER TABLE REFERENCE DROP INDEX FK6EF34F2BFCEAA1D7;
ALTER TABLE RUNNER_classpaths DROP INDEX FKDEF546E52522970F;
ALTER TABLE SYSTEM_UNDER_TEST DROP INDEX FK60BA29C92522970F;
ALTER TABLE SYSTEM_UNDER_TEST DROP INDEX FK60BA29C9FE6FA665;
ALTER TABLE SYSTEM_UNDER_TEST_sutClasspaths DROP INDEX FK9CEBA672FCEAA1D7;
ALTER TABLE SYSTEM_UNDER_TEST_fixtureClasspaths DROP INDEX FKD04AD469FCEAA1D7;
ALTER TABLE SUT_SPECIFICATION DROP INDEX FK6176AD96FCEAA1D7;

-- Changing not nullable columns to nullable
-- Changing TEXT to LONGTEXT (for Execution table)
ALTER TABLE EXECUTION MODIFY SECTIONS VARCHAR(50);
ALTER TABLE EXECUTION MODIFY RESULTS LONGTEXT;
ALTER TABLE EXECUTION MODIFY ERRORID LONGTEXT;
ALTER TABLE REFERENCE MODIFY SECTIONS VARCHAR(50);
ALTER TABLE REPOSITORY MODIFY USERNAME VARCHAR(15);
ALTER TABLE REPOSITORY MODIFY PASSWORD VARCHAR(15);
ALTER TABLE REPOSITORY_TYPE MODIFY DOCUMENT_URL_FORMAT VARCHAR(255);
ALTER TABLE REPOSITORY_TYPE MODIFY TEST_URL_FORMAT VARCHAR(255);
ALTER TABLE REPOSITORY_TYPE MODIFY REPOSITORY_CLASS VARCHAR(255);
ALTER TABLE RUNNER MODIFY SERVER_NAME VARCHAR(255);
ALTER TABLE RUNNER MODIFY SERVER_PORT VARCHAR(8);
ALTER TABLE RUNNER MODIFY CMD_LINE_TEMPLATE TEXT;
ALTER TABLE RUNNER MODIFY MAIN_CLASS VARCHAR(255);

-- Renaming column UID to UIDENT
ALTER TABLE REPOSITORY CHANGE UID UIDENT VARCHAR(255) NOT NULL;
ALTER TABLE EXECUTION CHANGE SYSTEM_UNDER_TEST_ID SUT_ID bigint(20) NOT NULL;
ALTER TABLE REFERENCE CHANGE SYSTEM_UNDER_TEST_ID SUT_ID bigint(20) NOT NULL;
ALTER TABLE SUT_SPECIFICATION CHANGE SYSTEM_UNDER_TEST_ID SUT_ID bigint(20) NOT NULL;
ALTER TABLE SYSTEM_UNDER_TEST_fixtureClasspaths CHANGE SYSTEM_UNDER_TEST_ID SUT_ID bigint(20) NOT NULL;
ALTER TABLE SYSTEM_UNDER_TEST_sutClasspaths CHANGE SYSTEM_UNDER_TEST_ID SUT_ID bigint(20) NOT NULL;

-- Renaming tables SYSTEM_UNDER_TEST* to SUT*
ALTER TABLE RUNNER_classpaths RENAME TO RUNNER_CLASSPATHS;
ALTER TABLE SYSTEM_UNDER_TEST RENAME TO SUT;
ALTER TABLE SYSTEM_UNDER_TEST_fixtureClasspaths RENAME TO SUT_FIXTURE_CLASSPATHS;
ALTER TABLE SYSTEM_UNDER_TEST_sutClasspaths RENAME TO SUT_CLASSPATHS;

-- Reset empty string to null
UPDATE EXECUTION SET SECTIONS = NULL WHERE SECTIONS = '';
UPDATE EXECUTION SET RESULTS = NULL WHERE RESULTS = '';
UPDATE EXECUTION SET ERRORID = NULL WHERE ERRORID = '';
UPDATE REFERENCE SET SECTIONS = NULL WHERE SECTIONS = '';
UPDATE REPOSITORY SET USERNAME = NULL WHERE USERNAME = '';
UPDATE REPOSITORY SET PASSWORD = NULL WHERE PASSWORD = '';
UPDATE REPOSITORY_TYPE SET DOCUMENT_URL_FORMAT = NULL WHERE DOCUMENT_URL_FORMAT = '';
UPDATE REPOSITORY_TYPE SET TEST_URL_FORMAT = NULL WHERE TEST_URL_FORMAT = '';
UPDATE REPOSITORY_TYPE SET REPOSITORY_CLASS = NULL WHERE REPOSITORY_CLASS = '';
UPDATE RUNNER SET SERVER_NAME = NULL WHERE SERVER_NAME = '';
UPDATE RUNNER SET SERVER_PORT = NULL WHERE SERVER_PORT = '';
UPDATE RUNNER SET CMD_LINE_TEMPLATE = NULL WHERE CMD_LINE_TEMPLATE = '';
UPDATE RUNNER SET MAIN_CLASS = NULL WHERE MAIN_CLASS = '';
UPDATE SUT SET FIXTURE_FACTORY = NULL WHERE FIXTURE_FACTORY = '';
UPDATE SUT SET FIXTURE_FACTORY_ARGS = NULL WHERE FIXTURE_FACTORY_ARGS = '';

