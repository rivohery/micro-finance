-- Liquibase formatted sql

-- changeset alibou:020-update-some-table-after-refactoring-code
ALTER TABLE accounts ADD COLUMN created_by uuid;
-- Mettre à jour les anciennes lignes avec un UUID par défaut (ex: un UUID zéro)
UPDATE accounts SET created_by = '00000000-0000-0000-0000-000000000000' WHERE created_by IS NULL;
ALTER TABLE accounts ALTER COLUMN created_by SET NOT NULL;
ALTER TABLE accounts ADD COLUMN last_modified_by uuid;
ALTER TABLE accounts ALTER COLUMN created_date TYPE date USING created_date::date;
ALTER TABLE accounts ALTER COLUMN last_modified_date TYPE date USING last_modified_date::date;

ALTER TABLE account_types ADD COLUMN created_by uuid;
UPDATE account_types SET created_by = '00000000-0000-0000-0000-000000000000' WHERE created_by IS NULL;
ALTER TABLE account_types ALTER COLUMN created_by SET NOT NULL;
ALTER TABLE account_types ADD COLUMN last_modified_by uuid;