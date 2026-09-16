-- Support X-API-KEY authentication on api_client.
-- api_token already stores a SHA-256 hash of the raw key (see ApiClientServiceImpl.generateKey).

-- 1) api_token_mask exists on the entity but was never added to the schema.
ALTER TABLE api_client
    ADD COLUMN IF NOT EXISTS api_token_mask character varying(255);

COMMENT ON COLUMN api_client.api_token_mask IS 'Display-only mask of the raw key; never the key itself';

-- 2) Optional owning app_user. Required only to authenticate this client with X-API-KEY.
ALTER TABLE api_client
    ADD COLUMN IF NOT EXISTS app_user bigint;

COMMENT ON COLUMN api_client.app_user IS 'FK -> app_user(id). Optional. Required only to authenticate this client with X-API-KEY';

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM api_client client
        LEFT JOIN app_user owner ON owner.id = client.app_user
        WHERE client.app_user IS NOT NULL AND owner.id IS NULL
    ) THEN
        RAISE EXCEPTION 'Cannot add api_client app_user FK: orphan app_user rows exist';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_api_client_app_user'
    ) THEN
        ALTER TABLE api_client
            ADD CONSTRAINT fk_api_client_app_user
            FOREIGN KEY (app_user) REFERENCES app_user(id) ON DELETE RESTRICT;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_api_client_app_user ON api_client (app_user);

-- 3) Optional key expiry. NULL never expires.
ALTER TABLE api_client
    ADD COLUMN IF NOT EXISTS expires_at timestamp(6) with time zone;

COMMENT ON COLUMN api_client.expires_at IS 'Optional X-API-KEY expiry. Null never expires';

-- 4) api_name identifies the calling VM and is cross-checked against the Accept-Apiclient
--    header, so it must be unique. Uniqueness was only enforced in the controller before.
DO $$
DECLARE
    duplicated text;
BEGIN
    SELECT string_agg(api_name, ', ')
    INTO duplicated
    FROM (
        SELECT api_name FROM api_client GROUP BY api_name HAVING count(*) > 1
    ) AS dup;

    IF duplicated IS NOT NULL THEN
        RAISE EXCEPTION 'Cannot add api_client api_name unique index: duplicate api_name values exist (%)', duplicated;
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uk_api_client_api_name ON api_client (api_name);
