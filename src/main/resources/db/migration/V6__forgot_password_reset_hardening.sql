ALTER TABLE access_token
    ADD COLUMN failed_attempts integer NOT NULL DEFAULT 0,
    ADD COLUMN verified_at timestamp(6) without time zone,
    ADD COLUMN consumed_at timestamp(6) without time zone;

COMMENT ON COLUMN access_token.failed_attempts IS 'Consecutive failed verification attempts for this recovery code';
COMMENT ON COLUMN access_token.verified_at IS 'Timestamp this recovery code was last successfully verified';
COMMENT ON COLUMN access_token.consumed_at IS 'Timestamp this recovery token was consumed by a completed password reset';
