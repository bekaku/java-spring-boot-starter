--
-- PostgreSQL database dump
--

\restrict gp1bnKNuFJapN5U6BMuGQhEe92siD1hz7dyf2a2F4hSZKdIh7BxRyroVv6zR6UZ

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: access_token; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.access_token (
                                     id bigint NOT NULL,
                                     created_date timestamp(6) without time zone,
                                     expires_at timestamp(6) without time zone,
                                     fcm_enable boolean,
                                     fcm_token character varying(255),
                                     lastest_active timestamp(6) without time zone,
                                     logouted_date timestamp(6) without time zone,
                                     revoked boolean NOT NULL,
                                     service smallint NOT NULL,
                                     token character varying(100),
                                     api_client bigint,
                                     app_user bigint,
                                     login_log bigint,
                                     CONSTRAINT access_token_service_check CHECK (((service >= 0) AND (service <= 1)))
);


ALTER TABLE public.access_token OWNER TO postgres_user;

--
-- Name: TABLE access_token; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.access_token IS 'Table for storing login token data.';


--
-- Name: COLUMN access_token.created_date; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.created_date IS 'Timestamp when the token was created';


--
-- Name: COLUMN access_token.expires_at; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.expires_at IS 'Expiration timestamp of the token';


--
-- Name: COLUMN access_token.fcm_enable; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.fcm_enable IS 'Flag indicating if FCM notifications are enabled for this session';


--
-- Name: COLUMN access_token.fcm_token; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.fcm_token IS 'Firebase Cloud Messaging token for device push notifications';


--
-- Name: COLUMN access_token.lastest_active; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.lastest_active IS 'Timestamp of the most recent activity with this token';


--
-- Name: COLUMN access_token.logouted_date; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.logouted_date IS 'Timestamp when the session was logged out';


--
-- Name: COLUMN access_token.revoked; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.revoked IS 'Flag indicating whether this token has been revoked';


--
-- Name: COLUMN access_token.service; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.service IS 'Service type of the token (e.g., LOGIN, REFRESH)';


--
-- Name: COLUMN access_token.token; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.token IS 'Hashed access token (SHA-256)';


--
-- Name: COLUMN access_token.api_client; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.api_client IS 'FK -> Ref table: api_client (id). The client application';


--
-- Name: COLUMN access_token.app_user; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.app_user IS 'FK -> Ref table: app_user (id). The user who owns this token';


--
-- Name: COLUMN access_token.login_log; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.access_token.login_log IS 'FK -> Ref table: login_log (id). The login activity log';


--
-- Name: ai_chat; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.ai_chat (
                                id bigint NOT NULL,
                                created_date timestamp(6) without time zone,
                                updated_date timestamp(6) without time zone,
                                title character varying(255),
                                created_user bigint,
                                updated_user bigint,
                                pin boolean DEFAULT false
);


ALTER TABLE public.ai_chat OWNER TO postgres_user;

--
-- Name: TABLE ai_chat; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.ai_chat IS 'Table for storing AI chat sessions and conversation topics.';


--
-- Name: COLUMN ai_chat.title; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_chat.title IS 'Title or topic name of the AI chat session';


--
-- Name: COLUMN ai_chat.pin; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_chat.pin IS 'Flag indicating whether this chat session is pinned to the top';


--
-- Name: ai_chat_messages; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.ai_chat_messages (
                                         id bigint NOT NULL,
                                         role character varying(50) NOT NULL,
                                         content text NOT NULL,
                                         created_date timestamp(6) without time zone,
                                         ai_chat bigint NOT NULL,
                                         CONSTRAINT ai_chat_messages_role_check CHECK (((role)::text = ANY (ARRAY[('user'::character varying)::text, ('assistant'::character varying)::text, ('system'::character varying)::text])))
);


ALTER TABLE public.ai_chat_messages OWNER TO postgres_user;

--
-- Name: TABLE ai_chat_messages; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.ai_chat_messages IS 'Table for storing individual messages within AI chat sessions.';


--
-- Name: COLUMN ai_chat_messages.role; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_chat_messages.role IS 'Role of the message sender (e.g., USER, ASSISTANT, SYSTEM)';


--
-- Name: COLUMN ai_chat_messages.content; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_chat_messages.content IS 'Content payload of the message (user prompt or AI response)';


--
-- Name: COLUMN ai_chat_messages.created_date; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_chat_messages.created_date IS 'Timestamp when the message was sent/created';


--
-- Name: COLUMN ai_chat_messages.ai_chat; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_chat_messages.ai_chat IS 'FK -> Ref table: ai_chat (id). The parent AI chat session';


--
-- Name: ai_document_meta; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.ai_document_meta (
                                         id bigint NOT NULL,
                                         deleted boolean DEFAULT false,
                                         created_date timestamp(6) without time zone,
                                         created_user bigint,
                                         updated_date timestamp(6) without time zone,
                                         updated_user bigint,
                                         file_name character varying(255),
                                         active boolean NOT NULL,
                                         file_mime bigint
);


ALTER TABLE public.ai_document_meta OWNER TO postgres_user;

--
-- Name: TABLE ai_document_meta; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.ai_document_meta IS 'Table for storing AI document metadata, vector mappings, and properties.';


--
-- Name: COLUMN ai_document_meta.file_name; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_document_meta.file_name IS 'Name of the document file';


--
-- Name: COLUMN ai_document_meta.active; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_document_meta.active IS 'Flag indicating whether this document is active for AI processing/retrieval';


--
-- Name: COLUMN ai_document_meta.file_mime; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_document_meta.file_mime IS 'FK -> Ref table: file_mime (id). MIME type reference for the document';


--
-- Name: ai_document_metadata; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.ai_document_metadata (
                                             document_id bigint NOT NULL,
                                             meta_value character varying(255),
                                             meta_key character varying(255) NOT NULL
);


ALTER TABLE public.ai_document_metadata OWNER TO postgres_user;

--
-- Name: COLUMN ai_document_metadata.document_id; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_document_metadata.document_id IS 'FK -> Ref table: ai_document_meta (id). Document identifier';


--
-- Name: COLUMN ai_document_metadata.meta_value; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_document_metadata.meta_value IS 'Metadata value content';


--
-- Name: ai_document_vector_ids; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.ai_document_vector_ids (
                                               document_id bigint NOT NULL,
                                               vector_id character varying(255)
);


ALTER TABLE public.ai_document_vector_ids OWNER TO postgres_user;

--
-- Name: COLUMN ai_document_vector_ids.document_id; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_document_vector_ids.document_id IS 'FK -> Ref table: ai_document_meta (id). Document identifier';


--
-- Name: COLUMN ai_document_vector_ids.vector_id; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.ai_document_vector_ids.vector_id IS 'Vector ID / Chunk ID stored in the Vector Database';


--
-- Name: api_client; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.api_client (
                                   id bigint NOT NULL,
                                   created_date timestamp(6) without time zone,
                                   created_user bigint,
                                   updated_date timestamp(6) without time zone,
                                   updated_user bigint,
                                   api_name character varying(100) NOT NULL,
                                   api_token character varying(255),
                                   by_pass boolean,
                                   status boolean
);


ALTER TABLE public.api_client OWNER TO postgres_user;

--
-- Name: api_client_ip; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.api_client_ip (
                                      id bigint NOT NULL,
                                      created_date timestamp(6) without time zone,
                                      created_user bigint,
                                      updated_date timestamp(6) without time zone,
                                      updated_user bigint,
                                      ip_address character varying(50),
                                      status boolean,
                                      api_client bigint NOT NULL
);


ALTER TABLE public.api_client_ip OWNER TO postgres_user;

--
-- Name: app_role; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.app_role (
                                 id bigint NOT NULL,
                                 deleted boolean,
                                 created_date timestamp(6) without time zone,
                                 created_user bigint,
                                 updated_date timestamp(6) without time zone,
                                 updated_user bigint,
                                 active boolean,
                                 name character varying(125) NOT NULL
);


ALTER TABLE public.app_role OWNER TO postgres_user;

--
-- Name: TABLE app_role; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.app_role IS 'Table for storing user roles and permissions.';


--
-- Name: COLUMN app_role.active; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_role.active IS 'Flag indicating if the role is active';


--
-- Name: COLUMN app_role.name; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_role.name IS 'Role name (e.g., ADMIN, USER)';


--
-- Name: app_user; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.app_user (
                                 id bigint NOT NULL,
                                 deleted boolean,
                                 created_date timestamp(6) without time zone,
                                 created_user bigint,
                                 updated_date timestamp(6) without time zone,
                                 updated_user bigint,
                                 active boolean NOT NULL,
                                 default_locale smallint,
                                 email character varying(125) NOT NULL,
                                 password character varying(255),
                                 salt character varying(255),
                                 username character varying(100),
                                 avatar_file_id bigint,
                                 cover_file_id bigint,
                                 CONSTRAINT app_user_default_locale_check CHECK (((default_locale >= 0) AND (default_locale <= 1)))
);


ALTER TABLE public.app_user OWNER TO postgres_user;

--
-- Name: TABLE app_user; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.app_user IS 'Table for storing user account information.';


--
-- Name: COLUMN app_user.active; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_user.active IS 'Account status flag (true = active, false = disabled/suspended)';


--
-- Name: COLUMN app_user.default_locale; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_user.default_locale IS 'Default language/locale setting for the user';


--
-- Name: COLUMN app_user.email; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_user.email IS 'Unique email address of the user';


--
-- Name: COLUMN app_user.password; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_user.password IS 'Hashed password for user authentication';


--
-- Name: COLUMN app_user.salt; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_user.salt IS 'Cryptographic salt used for password hashing';


--
-- Name: COLUMN app_user.username; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_user.username IS 'Unique username for user authentication';


--
-- Name: COLUMN app_user.avatar_file_id; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_user.avatar_file_id IS 'FK -> Ref table: file_manager (id). Reference to user avatar image file';


--
-- Name: COLUMN app_user.cover_file_id; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_user.cover_file_id IS 'FK -> Ref table: file_manager (id). Reference to user cover image file';


--
-- Name: app_user_role; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.app_user_role (
                                      app_user bigint NOT NULL,
                                      app_role bigint NOT NULL
);


ALTER TABLE public.app_user_role OWNER TO postgres_user;

--
-- Name: COLUMN app_user_role.app_user; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_user_role.app_user IS 'FK -> Ref table: app_user (id). User identifier';


--
-- Name: COLUMN app_user_role.app_role; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.app_user_role.app_role IS 'FK -> Ref table: app_role (id). Role identifier';


--
-- Name: audit_log; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.audit_log (
                                  id bigint NOT NULL,
                                  action character varying(255),
                                  details text,
                                  entity_id bigint,
                                  entity_name character varying(255),
                                  ip_address character varying(255),
                                  "timestamp" timestamp(6) without time zone,
                                  username character varying(255)
);


ALTER TABLE public.audit_log OWNER TO postgres_user;

--
-- Name: TABLE audit_log; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.audit_log IS 'Table for storing system audit logs and tracking user activities.';


--
-- Name: COLUMN audit_log.action; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.audit_log.action IS 'Action performed (e.g., CREATE, UPDATE, DELETE)';


--
-- Name: COLUMN audit_log.details; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.audit_log.details IS 'Detailed description or payload of the audit log';


--
-- Name: COLUMN audit_log.entity_id; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.audit_log.entity_id IS 'Identifier of the entity affected';


--
-- Name: COLUMN audit_log.entity_name; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.audit_log.entity_name IS 'Name of the entity or domain object affected';


--
-- Name: COLUMN audit_log.ip_address; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.audit_log.ip_address IS 'IP address of the client that performed the action';


--
-- Name: COLUMN audit_log."timestamp"; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.audit_log."timestamp" IS 'Timestamp when the audit event occurred';


--
-- Name: COLUMN audit_log.username; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.audit_log.username IS 'Username of the user who performed the action';


--
-- Name: district; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.district (
                                 id bigint NOT NULL,
                                 deleted boolean,
                                 created_date timestamp(6) without time zone,
                                 created_user bigint,
                                 updated_date timestamp(6) without time zone,
                                 updated_user bigint,
                                 name character varying(255) NOT NULL,
                                 name_en character varying(255),
                                 province bigint NOT NULL
);


ALTER TABLE public.district OWNER TO postgres_user;

--
-- Name: favorite_menu; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.favorite_menu (
                                      id bigint NOT NULL,
                                      url character varying(255),
                                      app_user bigint
);


ALTER TABLE public.favorite_menu OWNER TO postgres_user;

--
-- Name: TABLE favorite_menu; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.favorite_menu IS 'Table for storing user''s favorite menu items.';


--
-- Name: COLUMN favorite_menu.url; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.favorite_menu.url IS 'The menu item that is favorited';


--
-- Name: COLUMN favorite_menu.app_user; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.favorite_menu.app_user IS 'FK -> Ref table: app_user (id). The user who owns this token';


--
-- Name: file_manager; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.file_manager (
                                     id bigint NOT NULL,
                                     deleted boolean,
                                     created_date timestamp(6) without time zone,
                                     created_user bigint,
                                     file_name character varying(255),
                                     file_path character varying(255),
                                     file_size bigint,
                                     hidden boolean NOT NULL,
                                     locked boolean NOT NULL,
                                     original_file_name character varying(125),
                                     readable boolean NOT NULL,
                                     writeable boolean NOT NULL,
                                     file_mime_id bigint,
                                     files_directory_id bigint,
                                     owner bigint,
                                     description text,
                                     duration integer DEFAULT 0,
                                     title character varying(125),
                                     use_thumbnail boolean DEFAULT false,
                                     thumbnail_file bigint,
                                     updated_date timestamp(6) without time zone,
                                     updated_user bigint
);


ALTER TABLE public.file_manager OWNER TO postgres_user;

--
-- Name: TABLE file_manager; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.file_manager IS 'Table for storing uploaded files and metadata.';


--
-- Name: COLUMN file_manager.file_name; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.file_name IS 'Stored system file name on server or storage';


--
-- Name: COLUMN file_manager.file_path; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.file_path IS 'Storage path or directory where the file is stored';


--
-- Name: COLUMN file_manager.file_size; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.file_size IS 'File size in bytes';


--
-- Name: COLUMN file_manager.hidden; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.hidden IS 'Flag indicating if the file is hidden from standard view';


--
-- Name: COLUMN file_manager.locked; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.locked IS 'Flag indicating if the file is locked to prevent modification';


--
-- Name: COLUMN file_manager.original_file_name; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.original_file_name IS 'Original uploaded file name';


--
-- Name: COLUMN file_manager.readable; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.readable IS 'Flag indicating if the file has read permission';


--
-- Name: COLUMN file_manager.writeable; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.writeable IS 'Flag indicating if the file has write permission';


--
-- Name: COLUMN file_manager.file_mime_id; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.file_mime_id IS 'FK -> Ref table: file_mime (id). MIME type reference of the file';


--
-- Name: COLUMN file_manager.files_directory_id; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.files_directory_id IS 'FK -> Ref table: files_directory (id). Directory containing this file';


--
-- Name: COLUMN file_manager.owner; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.owner IS 'FK -> Ref table: app_user (id). The user who owns this file';


--
-- Name: COLUMN file_manager.description; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.description IS 'Detailed description or notes about the file';


--
-- Name: COLUMN file_manager.duration; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.duration IS 'Media duration in seconds (for audio/video files)';


--
-- Name: COLUMN file_manager.title; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.title IS 'Display title or custom label for the file';


--
-- Name: COLUMN file_manager.use_thumbnail; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.use_thumbnail IS 'Flag indicating whether a thumbnail is generated/used';


--
-- Name: COLUMN file_manager.thumbnail_file; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_manager.thumbnail_file IS 'FK -> Ref table: file_manager (id). Self-reference to thumbnail file';


--
-- Name: file_mime; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.file_mime (
                                  id bigint NOT NULL,
                                  name character varying(125)
);


ALTER TABLE public.file_mime OWNER TO postgres_user;

--
-- Name: TABLE file_mime; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.file_mime IS 'Table for storing supported file MIME types.';


--
-- Name: COLUMN file_mime.name; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.file_mime.name IS 'MIME type identifier string (e.g., image/png, application/pdf)';


--
-- Name: files_directory; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.files_directory (
                                        id bigint NOT NULL,
                                        created_date timestamp(6) without time zone,
                                        created_user bigint,
                                        updated_date timestamp(6) without time zone,
                                        updated_user bigint,
                                        active boolean NOT NULL,
                                        name character varying(125),
                                        files_directory_parent bigint,
                                        file_size bigint DEFAULT 0 NOT NULL,
                                        latest_updated timestamp(6) without time zone,
                                        owner bigint,
                                        file_count bigint DEFAULT 0 NOT NULL,
                                        deleted boolean DEFAULT false
);


ALTER TABLE public.files_directory OWNER TO postgres_user;

--
-- Name: TABLE files_directory; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.files_directory IS 'Table for storing file directory and folder hierarchy structure.';


--
-- Name: COLUMN files_directory.active; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.files_directory.active IS 'Flag indicating if the directory is active';


--
-- Name: COLUMN files_directory.name; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.files_directory.name IS 'Directory or folder name';


--
-- Name: COLUMN files_directory.files_directory_parent; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.files_directory.files_directory_parent IS 'FK -> Ref table: files_directory (id). Self-reference to parent directory';


--
-- Name: COLUMN files_directory.file_size; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.files_directory.file_size IS 'Total aggregated file size in bytes inside this directory';


--
-- Name: COLUMN files_directory.latest_updated; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.files_directory.latest_updated IS 'Timestamp of the latest update to this directory';


--
-- Name: COLUMN files_directory.owner; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.files_directory.owner IS 'FK -> Ref table: app_user (id). The user who owns this directory';


--
-- Name: COLUMN files_directory.file_count; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.files_directory.file_count IS 'Total count of files stored inside this directory';


--
-- Name: files_directory_path; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.files_directory_path (
                                             files_directory bigint NOT NULL,
                                             files_directory_parent bigint NOT NULL,
                                             level integer NOT NULL
);


ALTER TABLE public.files_directory_path OWNER TO postgres_user;

--
-- Name: TABLE files_directory_path; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.files_directory_path IS 'Closure table for storing directory tree paths and hierarchy depth.';


--
-- Name: COLUMN files_directory_path.level; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.files_directory_path.level IS 'Hierarchy distance/depth between ancestor and descendant (0 = self, 1 = direct child, etc.)';


--
-- Name: identity_group; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.identity_group (
                                       id bigint NOT NULL,
                                       created_date timestamp(6) without time zone,
                                       created_user bigint
);


ALTER TABLE public.identity_group OWNER TO postgres_user;

--
-- Name: TABLE identity_group; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.identity_group IS 'Table for storing identity groups.';


--
-- Name: identity_link; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.identity_link (
                                      id bigint NOT NULL,
                                      deleted boolean DEFAULT false,
                                      created_date timestamp(6) without time zone,
                                      created_user bigint,
                                      app_user bigint NOT NULL,
                                      identity_group bigint NOT NULL
);


ALTER TABLE public.identity_link OWNER TO postgres_user;

--
-- Name: TABLE identity_link; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.identity_link IS 'Table for linking user accounts to identity groups.';


--
-- Name: COLUMN identity_link.app_user; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.identity_link.app_user IS 'FK -> Ref table: app_user (id). The associated user account';


--
-- Name: COLUMN identity_link.identity_group; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.identity_link.identity_group IS 'FK -> Ref table: identity_group (id). The associated identity group';


--
-- Name: login_log; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.login_log (
                                  id bigint NOT NULL,
                                  created_at timestamp(6) without time zone,
                                  device_id character varying(125),
                                  host_name character varying(100),
                                  ip character varying(50),
                                  login_from smallint,
                                  app_user bigint,
                                  user_agent bigint,
                                  CONSTRAINT login_log_login_from_check CHECK (((login_from >= 0) AND (login_from <= 2)))
);


ALTER TABLE public.login_log OWNER TO postgres_user;

--
-- Name: TABLE login_log; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.login_log IS 'Table for storing user login activity logs and device metadata.';


--
-- Name: COLUMN login_log.created_at; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.login_log.created_at IS 'Timestamp when the login event occurred';


--
-- Name: COLUMN login_log.device_id; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.login_log.device_id IS 'Unique hardware/app identifier of the login device';


--
-- Name: COLUMN login_log.host_name; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.login_log.host_name IS 'Resolved hostname from the client IP';


--
-- Name: COLUMN login_log.ip; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.login_log.ip IS 'Client IP address used during login';


--
-- Name: COLUMN login_log.login_from; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.login_log.login_from IS 'Login source/platform type (e.g., WEB, MOBILE)';


--
-- Name: COLUMN login_log.app_user; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.login_log.app_user IS 'FK -> Ref table: app_user (id). The user who performed the login';


--
-- Name: COLUMN login_log.user_agent; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.login_log.user_agent IS 'FK -> Ref table: user_agent (id). Browser/client user agent information';


--
-- Name: permission; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.permission (
                                   id bigint NOT NULL,
                                   code character varying(125) NOT NULL,
                                   module character varying(100),
                                   description text,
                                   operation_type character varying(50),
                                   CONSTRAINT permission_operation_type_check CHECK (((operation_type)::text = ANY (ARRAY[('CRUD'::character varying)::text, ('REPORT'::character varying)::text, ('OTHER'::character varying)::text, ('FEATURE'::character varying)::text])))
);


ALTER TABLE public.permission OWNER TO postgres_user;

--
-- Name: TABLE permission; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON TABLE public.permission IS 'Table for storing system access permissions and operations.';


--
-- Name: COLUMN permission.code; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.permission.code IS 'Unique permission code (e.g., USER_READ, ROLE_MANAGE)';


--
-- Name: COLUMN permission.module; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.permission.module IS 'Module or domain group this permission belongs to';


--
-- Name: COLUMN permission.description; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.permission.description IS 'Detailed description of what this permission grants';


--
-- Name: COLUMN permission.operation_type; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.permission.operation_type IS 'Type of operation (e.g., CRUD, ACTION)';


--
-- Name: province; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.province (
                                 id bigint NOT NULL,
                                 deleted boolean,
                                 created_date timestamp(6) without time zone,
                                 created_user bigint,
                                 updated_date timestamp(6) without time zone,
                                 updated_user bigint,
                                 name character varying(255),
                                 name_en character varying(255)
);


ALTER TABLE public.province OWNER TO postgres_user;

--
-- Name: role_permission; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.role_permission (
                                        app_role bigint NOT NULL,
                                        permission bigint NOT NULL
);


ALTER TABLE public.role_permission OWNER TO postgres_user;

--
-- Name: COLUMN role_permission.app_role; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.role_permission.app_role IS 'FK -> Ref table: app_role (id). Role identifier';


--
-- Name: COLUMN role_permission.permission; Type: COMMENT; Schema: public; Owner: postgres_user
--

COMMENT ON COLUMN public.role_permission.permission IS 'FK -> Ref table: permission (id). Permission identifier';


--
-- Name: sub_district; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.sub_district (
                                     id bigint NOT NULL,
                                     deleted boolean,
                                     created_date timestamp(6) without time zone,
                                     created_user bigint,
                                     updated_date timestamp(6) without time zone,
                                     updated_user bigint,
                                     latitude double precision,
                                     longitude double precision,
                                     name character varying(255) NOT NULL,
                                     name_en character varying(255),
                                     zip_code integer,
                                     district bigint NOT NULL
);


ALTER TABLE public.sub_district OWNER TO postgres_user;

--
-- Name: system_activity_logs; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.system_activity_logs (
                                             id bigint NOT NULL,
                                             action_date_time timestamp(6) without time zone,
                                             description character varying(255),
                                             user_id bigint
);


ALTER TABLE public.system_activity_logs OWNER TO postgres_user;

--
-- Name: user_agent; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.user_agent (
                                   id bigint NOT NULL,
                                   agent character varying(255) NOT NULL
);


ALTER TABLE public.user_agent OWNER TO postgres_user;

--
-- Data for Name: access_token; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.access_token (id, created_date, expires_at, fcm_enable, fcm_token, lastest_active, logouted_date, revoked, service, token, api_client, app_user, login_log) FROM stdin;
\.


--
-- Data for Name: ai_chat; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.ai_chat (id, created_date, updated_date, title, created_user, updated_user, pin) FROM stdin;
\.


--
-- Data for Name: ai_chat_messages; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.ai_chat_messages (id, role, content, created_date, ai_chat) FROM stdin;
\.


--
-- Data for Name: ai_document_meta; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.ai_document_meta (id, deleted, created_date, created_user, updated_date, updated_user, file_name, active, file_mime) FROM stdin;
\.


--
-- Data for Name: ai_document_metadata; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.ai_document_metadata (document_id, meta_value, meta_key) FROM stdin;
\.


--
-- Data for Name: ai_document_vector_ids; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.ai_document_vector_ids (document_id, vector_id) FROM stdin;
\.


--
-- Data for Name: api_client; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.api_client (id, created_date, created_user, updated_date, updated_user, api_name, api_token, by_pass, status) FROM stdin;
350921408848597000	\N	350921408848597000	\N	350921408848597000	default	0198e501-1193-7ac8-80d4-70faab88f9bb	f	t
\.


--
-- Data for Name: api_client_ip; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.api_client_ip (id, created_date, created_user, updated_date, updated_user, ip_address, status, api_client) FROM stdin;
\.


--
-- Data for Name: app_role; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.app_role (id, deleted, created_date, created_user, updated_date, updated_user, active, name) FROM stdin;
350888314967953409	f	2025-08-26 13:24:53.476566	350885844724224000	2026-08-11 11:45:36.065403	350885844724224000	t	Developer
\.


--
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.app_user (id, deleted, created_date, created_user, updated_date, updated_user, active, default_locale, email, password, salt, username, avatar_file_id, cover_file_id) FROM stdin;
350885844724224000	f	\N	\N	2026-08-01 22:53:55.715975	350885844724224000	t	0	admin@mydomain.com	$2a$10$Z/GTNq9afOxTcBpOCWb43eeAwaAc1xiEoGTkWAv5BkgCBhAQD7PKO	0198e504-0a26-7d15-b748-fbd96785b929	admin	\N	\N
\.


--
-- Data for Name: app_user_role; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.app_user_role (app_user, app_role) FROM stdin;
350885844724224000	350888314967953409
\.


--
-- Data for Name: audit_log; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.audit_log (id, action, details, entity_id, entity_name, ip_address, "timestamp", username) FROM stdin;
\.


--
-- Data for Name: district; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.district (id, deleted, created_date, created_user, updated_date, updated_user, name, name_en, province) FROM stdin;
\.


--
-- Data for Name: favorite_menu; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.favorite_menu (id, url, app_user) FROM stdin;
\.


--
-- Data for Name: file_manager; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.file_manager (id, deleted, created_date, created_user, file_name, file_path, file_size, hidden, locked, original_file_name, readable, writeable, file_mime_id, files_directory_id, owner, description, duration, title, use_thumbnail, thumbnail_file, updated_date, updated_user) FROM stdin;
477715963752484864	f	2026-08-11 12:52:41.292382	350885844724224000	\N	files/202608/1786427560934_345444391306924032.md	5740	f	f	9999_66201010001.md	t	t	477374803011964928	\N	350885844724224000	\N	0	\N	f	\N	2026-08-11 12:52:41.292382	350885844724224000
\.


--
-- Data for Name: file_mime; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.file_mime (id, name) FROM stdin;
474234311600508928	image/jpeg
477374803011964928	text/markdown
477657177876598784	video/mp4
477749132849582080	application/pdf
\.


--
-- Data for Name: files_directory; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.files_directory (id, created_date, created_user, updated_date, updated_user, active, name, files_directory_parent, file_size, latest_updated, owner, file_count, deleted) FROM stdin;
\.


--
-- Data for Name: files_directory_path; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.files_directory_path (files_directory, files_directory_parent, level) FROM stdin;
\.


--
-- Data for Name: identity_group; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.identity_group (id, created_date, created_user) FROM stdin;
\.


--
-- Data for Name: identity_link; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.identity_link (id, deleted, created_date, created_user, app_user, identity_group) FROM stdin;
\.


--
-- Data for Name: login_log; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.login_log (id, created_at, device_id, host_name, ip, login_from, app_user, user_agent) FROM stdin;
\.


--
-- Data for Name: permission; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.permission (id, code, module, description, operation_type) FROM stdin;
350945166250479600	login	\N	\N	OTHER
350897401642356736	api_client_list	api_client		CRUD
350897732065431552	api_client_view	api_client		CRUD
350898678438825984	api_client_add	api_client		CRUD
350898898232938496	permission_list	permission		CRUD
350898930604576768	permission_view	permission	Permission(View)	CRUD
350898947750891521	permission_add	permission		CRUD
350898969737433089	app_role_list	app_role		CRUD
350898990360825856	app_role_view	app_role		CRUD
350899010636091393	app_role_add	app_role		CRUD
350899032308060160	app_user_list	app_user		CRUD
350899050880438273	app_user_view	app_user		CRUD
350899073227689985	app_user_add	app_user		CRUD
351972435173576704	files_directory_list	files_directory	\N	CRUD
351972466005905408	files_directory_view	files_directory	\N	CRUD
351972492660707328	files_directory_add	files_directory	\N	CRUD
351972526756204545	file_manager_list	file_manager_	\N	CRUD
351972551611650049	file_manager_view	file_manager_	\N	CRUD
351972582251040768	file_manager_add	file_manager_	\N	CRUD
477697499725828096	api_client_edit	api_client		CRUD
477697604222717953	api_client_delete	api_client		CRUD
477697671881035777	permission_edit	permission		CRUD
477697705791983617	permission_delete	permission		CRUD
477697765690839041	app_role_edit	app_role		CRUD
477697794476347393	app_role_delete	app_role		CRUD
477697849186848768	app_user_edit	app_user		CRUD
477697882833555457	app_user_delete	app_user		CRUD
477697980963491841	files_directory_edit	files_directory		CRUD
477698007068839937	files_directory_delete	files_directory		CRUD
477698051360690177	file_manager_edit	file_manager		CRUD
477698075498909696	file_manager_delete	file_manager		CRUD
477698172982923265	ai_document_meta_list	ai_document_meta		CRUD
477698206868705281	ai_document_meta_add	ai_document_meta		CRUD
477698242813890560	ai_document_meta_view	ai_document_meta		CRUD
477698285058920449	ai_document_meta_edit	ai_document_meta		CRUD
477698323562631168	ai_document_meta_delete	ai_document_meta		CRUD
\.


--
-- Data for Name: province; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.province (id, deleted, created_date, created_user, updated_date, updated_user, name, name_en) FROM stdin;
\.


--
-- Data for Name: role_permission; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.role_permission (app_role, permission) FROM stdin;
350888314967953409	477698323562631168
350888314967953409	350898947750891521
350888314967953409	351972492660707328
350888314967953409	477698075498909696
350888314967953409	477697849186848768
350888314967953409	477698172982923265
350888314967953409	351972551611650049
350888314967953409	477697765690839041
350888314967953409	350898990360825856
350888314967953409	351972582251040768
350888314967953409	350898930604576768
350888314967953409	477698051360690177
350888314967953409	477698206868705281
350888314967953409	350898898232938496
350888314967953409	350899032308060160
350888314967953409	350899050880438273
350888314967953409	477697499725828096
350888314967953409	350899073227689985
350888314967953409	477697604222717953
350888314967953409	351972435173576704
350888314967953409	477698285058920449
350888314967953409	350897732065431552
350888314967953409	477697705791983617
350888314967953409	477697980963491841
350888314967953409	350898678438825984
350888314967953409	477697882833555457
350888314967953409	477698007068839937
350888314967953409	350899010636091393
350888314967953409	350897401642356736
350888314967953409	350898969737433089
350888314967953409	477698242813890560
350888314967953409	477697671881035777
350888314967953409	351972466005905408
350888314967953409	351972526756204545
350888314967953409	350945166250479600
350888314967953409	477697794476347393
\.


--
-- Data for Name: sub_district; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.sub_district (id, deleted, created_date, created_user, updated_date, updated_user, latitude, longitude, name, name_en, zip_code, district) FROM stdin;
\.


--
-- Data for Name: system_activity_logs; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.system_activity_logs (id, action_date_time, description, user_id) FROM stdin;
\.


--
-- Data for Name: user_agent; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.user_agent (id, agent) FROM stdin;
\.


--
-- Name: access_token access_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.access_token
    ADD CONSTRAINT access_token_pkey PRIMARY KEY (id);


--
-- Name: ai_chat_messages ai_chat_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.ai_chat_messages
    ADD CONSTRAINT ai_chat_messages_pkey PRIMARY KEY (id);


--
-- Name: ai_chat ai_chat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.ai_chat
    ADD CONSTRAINT ai_chat_pkey PRIMARY KEY (id);


--
-- Name: ai_document_meta ai_document_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.ai_document_meta
    ADD CONSTRAINT ai_document_meta_pkey PRIMARY KEY (id);


--
-- Name: ai_document_metadata ai_document_metadata_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.ai_document_metadata
    ADD CONSTRAINT ai_document_metadata_pkey PRIMARY KEY (document_id, meta_key);


--
-- Name: api_client_ip api_client_ip_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.api_client_ip
    ADD CONSTRAINT api_client_ip_pkey PRIMARY KEY (id);


--
-- Name: api_client api_client_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.api_client
    ADD CONSTRAINT api_client_pkey PRIMARY KEY (id);


--
-- Name: app_role app_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.app_role
    ADD CONSTRAINT app_role_pkey PRIMARY KEY (id);


--
-- Name: app_user app_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (id);


--
-- Name: app_user_role app_user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.app_user_role
    ADD CONSTRAINT app_user_role_pkey PRIMARY KEY (app_user, app_role);


--
-- Name: audit_log audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.audit_log
    ADD CONSTRAINT audit_log_pkey PRIMARY KEY (id);


--
-- Name: district district_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.district
    ADD CONSTRAINT district_pkey PRIMARY KEY (id);


--
-- Name: favorite_menu favorite_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.favorite_menu
    ADD CONSTRAINT favorite_menu_pkey PRIMARY KEY (id);


--
-- Name: file_manager file_manager_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.file_manager
    ADD CONSTRAINT file_manager_pkey PRIMARY KEY (id);


--
-- Name: file_mime file_mime_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.file_mime
    ADD CONSTRAINT file_mime_pkey PRIMARY KEY (id);


--
-- Name: files_directory_path files_directory_path_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.files_directory_path
    ADD CONSTRAINT files_directory_path_pkey PRIMARY KEY (files_directory, files_directory_parent);


--
-- Name: files_directory files_directory_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.files_directory
    ADD CONSTRAINT files_directory_pkey PRIMARY KEY (id);


--
-- Name: identity_group identity_group_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.identity_group
    ADD CONSTRAINT identity_group_pkey PRIMARY KEY (id);


--
-- Name: identity_link identity_link_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.identity_link
    ADD CONSTRAINT identity_link_pkey PRIMARY KEY (id);


--
-- Name: login_log login_log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.login_log
    ADD CONSTRAINT login_log_pkey PRIMARY KEY (id);


--
-- Name: permission permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.permission
    ADD CONSTRAINT permission_pkey PRIMARY KEY (id);


--
-- Name: province province_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.province
    ADD CONSTRAINT province_pkey PRIMARY KEY (id);


--
-- Name: role_permission role_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.role_permission
    ADD CONSTRAINT role_permission_pkey PRIMARY KEY (app_role, permission);


--
-- Name: sub_district sub_district_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.sub_district
    ADD CONSTRAINT sub_district_pkey PRIMARY KEY (id);


--
-- Name: system_activity_logs system_activity_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.system_activity_logs
    ADD CONSTRAINT system_activity_logs_pkey PRIMARY KEY (id);


--
-- Name: access_token uk1djybee0iap4odfl91gkxoxem; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.access_token
    ADD CONSTRAINT uk1djybee0iap4odfl91gkxoxem UNIQUE (token);


--
-- Name: app_user uk1j9d9a06i600gd43uu3km82jw; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT uk1j9d9a06i600gd43uu3km82jw UNIQUE (email);


--
-- Name: app_user uk3k4cplvh82srueuttfkwnylq0; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT uk3k4cplvh82srueuttfkwnylq0 UNIQUE (username);


--
-- Name: permission uka7ujv987la0i7a0o91ueevchc; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.permission
    ADD CONSTRAINT uka7ujv987la0i7a0o91ueevchc UNIQUE (code);


--
-- Name: api_client ukqi9faagnfpyh5wky24ma2hbr6; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.api_client
    ADD CONSTRAINT ukqi9faagnfpyh5wky24ma2hbr6 UNIQUE (api_token);


--
-- Name: access_token ukt05x1jr0mk2n2se3ogxi1rt59; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.access_token
    ADD CONSTRAINT ukt05x1jr0mk2n2se3ogxi1rt59 UNIQUE (login_log);


--
-- Name: identity_link ukvfksfaep0ifm857igdfjvolq; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.identity_link
    ADD CONSTRAINT ukvfksfaep0ifm857igdfjvolq UNIQUE (app_user);


--
-- Name: user_agent user_agent_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.user_agent
    ADD CONSTRAINT user_agent_pkey PRIMARY KEY (id);


--
-- Name: idx1cta4hbkdlh0j3f3f4k7efof7; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idx1cta4hbkdlh0j3f3f4k7efof7 ON public.ai_chat USING btree (created_user);


--
-- Name: idx1g886n9ijc3v1kn2ja05c61gx; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idx1g886n9ijc3v1kn2ja05c61gx ON public.login_log USING btree (device_id);


--
-- Name: idx3kxn2twewmof6sqvng1j56ach; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idx3kxn2twewmof6sqvng1j56ach ON public.identity_link USING btree (deleted);


--
-- Name: idx3tvwfi9ein6ptfl2tlb4a373q; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idx3tvwfi9ein6ptfl2tlb4a373q ON public.file_manager USING btree (created_user);


--
-- Name: idx6auvrj4vq887v2k2xeloqmydk; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idx6auvrj4vq887v2k2xeloqmydk ON public.files_directory USING btree (created_user);


--
-- Name: idx7dnv5tcu6inbpsg2biiwky9ih; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idx7dnv5tcu6inbpsg2biiwky9ih ON public.app_user USING btree (created_user);


--
-- Name: idx7i22j43748d8cnciffepm0jk8; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idx7i22j43748d8cnciffepm0jk8 ON public.access_token USING btree (fcm_enable);


--
-- Name: idx8po4lxsgivw9m6ohl7qi37cxs; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idx8po4lxsgivw9m6ohl7qi37cxs ON public.user_agent USING btree (agent);


--
-- Name: idx9b2hqhmteavbi90n9d839p2b5; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idx9b2hqhmteavbi90n9d839p2b5 ON public.api_client_ip USING btree (created_user);


--
-- Name: idxago4re6d8ldeib4w1ceru2mwy; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxago4re6d8ldeib4w1ceru2mwy ON public.app_user USING btree (active);


--
-- Name: idxd93y6baq0w10c9de2kd8fp7bf; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxd93y6baq0w10c9de2kd8fp7bf ON public.app_role USING btree (created_user);


--
-- Name: idxe851maef0ogkl5s4g3l56u4ff; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxe851maef0ogkl5s4g3l56u4ff ON public.api_client USING btree (created_user);


--
-- Name: idxgrvp22cs4h9terj94b281fll5; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxgrvp22cs4h9terj94b281fll5 ON public.app_role USING btree (updated_user);


--
-- Name: idxi8vvu91hco9k5ymwafnff27jo; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxi8vvu91hco9k5ymwafnff27jo ON public.access_token USING btree (fcm_token);


--
-- Name: idxji7nkobmpein30otij9lrotu0; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxji7nkobmpein30otij9lrotu0 ON public.identity_group USING btree (created_user);


--
-- Name: idxkf29lomp4g8kwqr49239nfpjo; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxkf29lomp4g8kwqr49239nfpjo ON public.access_token USING btree (revoked);


--
-- Name: idxlic6tl97u7idgejjj3jev541y; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxlic6tl97u7idgejjj3jev541y ON public.file_manager USING btree (deleted);


--
-- Name: idxlpwkjxebftjm60wu46pck58p4; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxlpwkjxebftjm60wu46pck58p4 ON public.app_user USING btree (updated_user);


--
-- Name: idxmkmxwfmdb7gbn689c2dhges20; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxmkmxwfmdb7gbn689c2dhges20 ON public.files_directory USING btree (updated_user);


--
-- Name: idxnm0gxcgi9ue456vnjcriy6tks; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxnm0gxcgi9ue456vnjcriy6tks ON public.api_client_ip USING btree (updated_user);


--
-- Name: idxo7jki9ikxwuc4m0542gnhj4dq; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxo7jki9ikxwuc4m0542gnhj4dq ON public.app_user USING btree (deleted);


--
-- Name: idxohkk0fataetw36doj0cbn6wf3; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxohkk0fataetw36doj0cbn6wf3 ON public.api_client USING btree (updated_user);


--
-- Name: idxrmhyitswekfi6kp10q7stq5ac; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxrmhyitswekfi6kp10q7stq5ac ON public.app_role USING btree (deleted);


--
-- Name: idxs2vq59h0rbe4abafu72vay7bl; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idxs2vq59h0rbe4abafu72vay7bl ON public.access_token USING btree (lastest_active);


--
-- Name: ai_document_vector_ids fk1qe9bvn6u1jeuj1pdvevhwql6; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.ai_document_vector_ids
    ADD CONSTRAINT fk1qe9bvn6u1jeuj1pdvevhwql6 FOREIGN KEY (document_id) REFERENCES public.ai_document_meta(id);


--
-- Name: app_user_role fk3xcgg4e44bx37j6oa7p1lfgp8; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.app_user_role
    ADD CONSTRAINT fk3xcgg4e44bx37j6oa7p1lfgp8 FOREIGN KEY (app_user) REFERENCES public.app_user(id);


--
-- Name: sub_district fk4x2ucm4k68w993pg9cpvbi535; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.sub_district
    ADD CONSTRAINT fk4x2ucm4k68w993pg9cpvbi535 FOREIGN KEY (district) REFERENCES public.district(id);


--
-- Name: access_token fk5kmvrg6uuo55il7lx84mimu4f; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.access_token
    ADD CONSTRAINT fk5kmvrg6uuo55il7lx84mimu4f FOREIGN KEY (api_client) REFERENCES public.api_client(id);


--
-- Name: api_client_ip fk5pu9gbj8rvr9gdx27uwua7ug9; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.api_client_ip
    ADD CONSTRAINT fk5pu9gbj8rvr9gdx27uwua7ug9 FOREIGN KEY (api_client) REFERENCES public.api_client(id) ON DELETE CASCADE;


--
-- Name: role_permission fk8dbhyr3cvowlp4r0cuc578uqn; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.role_permission
    ADD CONSTRAINT fk8dbhyr3cvowlp4r0cuc578uqn FOREIGN KEY (permission) REFERENCES public.permission(id);


--
-- Name: files_directory fk8g9rqrbcuspfvhbp49e8w0j9x; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.files_directory
    ADD CONSTRAINT fk8g9rqrbcuspfvhbp49e8w0j9x FOREIGN KEY (owner) REFERENCES public.app_user(id);


--
-- Name: access_token fk9adhg4bm3rvd167xpgg38aqfs; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.access_token
    ADD CONSTRAINT fk9adhg4bm3rvd167xpgg38aqfs FOREIGN KEY (login_log) REFERENCES public.login_log(id);


--
-- Name: access_token fka5o1n8cul4rf2wihkmh6agkwi; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.access_token
    ADD CONSTRAINT fka5o1n8cul4rf2wihkmh6agkwi FOREIGN KEY (app_user) REFERENCES public.app_user(id);


--
-- Name: files_directory fkaisbmg4sw7vpvjjjrbedfropt; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.files_directory
    ADD CONSTRAINT fkaisbmg4sw7vpvjjjrbedfropt FOREIGN KEY (files_directory_parent) REFERENCES public.files_directory(id);


--
-- Name: role_permission fkc13ryj6yfrhcvdak6k4fngtyf; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.role_permission
    ADD CONSTRAINT fkc13ryj6yfrhcvdak6k4fngtyf FOREIGN KEY (app_role) REFERENCES public.app_role(id);


--
-- Name: ai_document_metadata fkc4k2n2e4n4o8hb5o9rh9wr19w; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.ai_document_metadata
    ADD CONSTRAINT fkc4k2n2e4n4o8hb5o9rh9wr19w FOREIGN KEY (document_id) REFERENCES public.ai_document_meta(id);


--
-- Name: app_user_role fkcprhx6mpypdwshju5p7pi971y; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.app_user_role
    ADD CONSTRAINT fkcprhx6mpypdwshju5p7pi971y FOREIGN KEY (app_role) REFERENCES public.app_role(id);


--
-- Name: file_manager fked7fai3fug3jnfglyc5syus3y; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.file_manager
    ADD CONSTRAINT fked7fai3fug3jnfglyc5syus3y FOREIGN KEY (owner) REFERENCES public.app_user(id);


--
-- Name: app_user fkf6hgftbo89mgus3gpsy29wj99; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT fkf6hgftbo89mgus3gpsy29wj99 FOREIGN KEY (cover_file_id) REFERENCES public.file_manager(id);


--
-- Name: file_manager fkfiem69xic5ix94962iep9i407; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.file_manager
    ADD CONSTRAINT fkfiem69xic5ix94962iep9i407 FOREIGN KEY (thumbnail_file) REFERENCES public.file_manager(id);


--
-- Name: file_manager fkfntevcv1jorjk5fnxqb4knkg3; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.file_manager
    ADD CONSTRAINT fkfntevcv1jorjk5fnxqb4knkg3 FOREIGN KEY (files_directory_id) REFERENCES public.files_directory(id);


--
-- Name: ai_document_meta fkhb7n6g7hrevnnyd8pk6vmuw49; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.ai_document_meta
    ADD CONSTRAINT fkhb7n6g7hrevnnyd8pk6vmuw49 FOREIGN KEY (file_mime) REFERENCES public.file_mime(id);


--
-- Name: district fkhjqjc8lokbb2jv09gvsifl8mm; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.district
    ADD CONSTRAINT fkhjqjc8lokbb2jv09gvsifl8mm FOREIGN KEY (province) REFERENCES public.province(id);


--
-- Name: login_log fkidqrwi0ocgnexw3vyu9d8gk7n; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.login_log
    ADD CONSTRAINT fkidqrwi0ocgnexw3vyu9d8gk7n FOREIGN KEY (app_user) REFERENCES public.app_user(id);


--
-- Name: file_manager fkiq28e5ahmqo1pc8yniixp0r6w; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.file_manager
    ADD CONSTRAINT fkiq28e5ahmqo1pc8yniixp0r6w FOREIGN KEY (file_mime_id) REFERENCES public.file_mime(id);


--
-- Name: app_user fkk6uvdlrab91uwu4lfsw9hndcd; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT fkk6uvdlrab91uwu4lfsw9hndcd FOREIGN KEY (avatar_file_id) REFERENCES public.file_manager(id);


--
-- Name: identity_link fkmv65r8wt1o0gh6f6cbocxbu67; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.identity_link
    ADD CONSTRAINT fkmv65r8wt1o0gh6f6cbocxbu67 FOREIGN KEY (app_user) REFERENCES public.app_user(id);


--
-- Name: ai_chat_messages fkq44i5ff388kmb1vw34hbdmfmv; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.ai_chat_messages
    ADD CONSTRAINT fkq44i5ff388kmb1vw34hbdmfmv FOREIGN KEY (ai_chat) REFERENCES public.ai_chat(id);


--
-- Name: system_activity_logs fkq4jtoesk8ed9fe9ivyrmet71c; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.system_activity_logs
    ADD CONSTRAINT fkq4jtoesk8ed9fe9ivyrmet71c FOREIGN KEY (user_id) REFERENCES public.app_user(id);


--
-- Name: login_log fkqegw0bjfp1kh6o349sbls6qm3; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.login_log
    ADD CONSTRAINT fkqegw0bjfp1kh6o349sbls6qm3 FOREIGN KEY (user_agent) REFERENCES public.user_agent(id);


--
-- Name: identity_link fkrk90ed9l23pvmqlulqo8fi7fr; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.identity_link
    ADD CONSTRAINT fkrk90ed9l23pvmqlulqo8fi7fr FOREIGN KEY (identity_group) REFERENCES public.identity_group(id);


--
-- Name: favorite_menu fksul1w41buaq90mmjqtngiqjrk; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.favorite_menu
    ADD CONSTRAINT fksul1w41buaq90mmjqtngiqjrk FOREIGN KEY (app_user) REFERENCES public.app_user(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT USAGE ON SCHEMA public TO ai_readonly_user;


--
-- Name: TABLE access_token; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.access_token TO ai_readonly_user;


--
-- Name: TABLE ai_chat; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.ai_chat TO ai_readonly_user;


--
-- Name: TABLE ai_chat_messages; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.ai_chat_messages TO ai_readonly_user;


--
-- Name: TABLE ai_document_meta; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.ai_document_meta TO ai_readonly_user;


--
-- Name: TABLE ai_document_metadata; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.ai_document_metadata TO ai_readonly_user;


--
-- Name: TABLE ai_document_vector_ids; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.ai_document_vector_ids TO ai_readonly_user;


--
-- Name: TABLE api_client; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.api_client TO ai_readonly_user;


--
-- Name: TABLE api_client_ip; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.api_client_ip TO ai_readonly_user;


--
-- Name: TABLE app_role; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.app_role TO ai_readonly_user;


--
-- Name: TABLE app_user; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.app_user TO ai_readonly_user;


--
-- Name: TABLE app_user_role; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.app_user_role TO ai_readonly_user;


--
-- Name: TABLE audit_log; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.audit_log TO ai_readonly_user;


--
-- Name: TABLE district; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.district TO ai_readonly_user;


--
-- Name: TABLE favorite_menu; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.favorite_menu TO ai_readonly_user;


--
-- Name: TABLE file_manager; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.file_manager TO ai_readonly_user;


--
-- Name: TABLE file_mime; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.file_mime TO ai_readonly_user;


--
-- Name: TABLE files_directory; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.files_directory TO ai_readonly_user;


--
-- Name: TABLE files_directory_path; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.files_directory_path TO ai_readonly_user;


--
-- Name: TABLE identity_group; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.identity_group TO ai_readonly_user;


--
-- Name: TABLE identity_link; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.identity_link TO ai_readonly_user;


--
-- Name: TABLE login_log; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.login_log TO ai_readonly_user;


--
-- Name: TABLE permission; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.permission TO ai_readonly_user;


--
-- Name: TABLE province; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.province TO ai_readonly_user;


--
-- Name: TABLE role_permission; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.role_permission TO ai_readonly_user;


--
-- Name: TABLE sub_district; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.sub_district TO ai_readonly_user;


--
-- Name: TABLE system_activity_logs; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.system_activity_logs TO ai_readonly_user;


--
-- Name: TABLE user_agent; Type: ACL; Schema: public; Owner: postgres_user
--

GRANT SELECT ON TABLE public.user_agent TO ai_readonly_user;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres_user
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres_user IN SCHEMA public GRANT SELECT ON TABLES TO ai_readonly_user;


--
-- PostgreSQL database dump complete
--

\unrestrict gp1bnKNuFJapN5U6BMuGQhEe92siD1hz7dyf2a2F4hSZKdIh7BxRyroVv6zR6UZ

