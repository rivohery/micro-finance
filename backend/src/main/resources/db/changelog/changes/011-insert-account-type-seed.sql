-- Liquibase formatted sql

-- changeset alibou:011-insert-account-type-seed
INSERT INTO account_types (
    id,
    name,
    code,
    account_fee,
    interest_rate,
    minimum_balance,
    created_date,
    last_modified_date
) VALUES
-- 1. Compte Courant (Code: CC)
(
    'a3b8c2e4-1234-4bc1-bc89-123456789abc',
    'Compte Courant',
    '10',        -- '10' pour le compte courant
    5000.0000,   -- Frais de tenue de compte par exemple
    0.0000,      -- Pas d'intérêts sur un compte courant
    0.0000,      -- Pas de solde minimum obligatoire
    CURRENT_DATE,
    CURRENT_DATE
),
-- 2. Compte Épargne (Code: CE)
(
    'f4e7d3c2-5678-4de2-ad90-987654321def',
    'Compte Épargne',
    '20',        -- '20' pour le compte épargne
    0.0000,      -- Généralement gratuit pour encourager l'épargne
    3.5000,      -- Taux d'intérêt annuel de 3.5%
    10000.0000,  -- Solde minimum requis à l'ouverture (ex: 10 000 Ariary ou FCFA)
    CURRENT_DATE,
    CURRENT_DATE
);
