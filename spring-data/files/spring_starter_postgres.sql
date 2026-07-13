--
-- PostgreSQL database dump
--

\restrict vVWOxoFdHyaGoORtewflTBvIbqBNasD7QeT9JIy3Kus1PaCdfNrF9U3CcqjPEhF

-- Dumped from database version 18.0
-- Dumped by pg_dump version 18.0

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

--
-- Name: create_monthly_partitions(text); Type: FUNCTION; Schema: public; Owner: postgres_user
--

CREATE FUNCTION public.create_monthly_partitions(table_name text) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
    start_date date;
    end_date date;
    partition_name text;
BEGIN
    start_date := date_trunc('month', CURRENT_DATE);
    end_date := start_date + interval '1 month';
    partition_name := table_name || '_' || to_char(start_date, 'YYYY_MM');
    
    EXECUTE format('CREATE TABLE IF NOT EXISTS %I PARTITION OF %I 
                    FOR VALUES FROM (%L) TO (%L)',
                   partition_name, table_name, start_date, end_date);
END;
$$;


ALTER FUNCTION public.create_monthly_partitions(table_name text) OWNER TO postgres_user;

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
-- Name: ai_document_meta; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.ai_document_meta (
    id bigint NOT NULL,
    deleted boolean,
    created_date timestamp(6) without time zone,
    created_user bigint,
    updated_date timestamp(6) without time zone,
    updated_user bigint,
    document_type character varying(255),
    file_name character varying(255),
    is_active boolean NOT NULL,
    CONSTRAINT ai_document_meta_document_type_check CHECK (((document_type)::text = ANY ((ARRAY['GENERAL'::character varying, 'FAQ'::character varying, 'USER_GUIDE'::character varying, 'WI'::character varying])::text[])))
);


ALTER TABLE public.ai_document_meta OWNER TO postgres_user;

--
-- Name: ai_document_vector_ids; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.ai_document_vector_ids (
    document_id bigint NOT NULL,
    vector_id character varying(255)
);


ALTER TABLE public.ai_document_vector_ids OWNER TO postgres_user;

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
-- Name: app_user_role; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.app_user_role (
    app_user bigint NOT NULL,
    app_role bigint NOT NULL
);


ALTER TABLE public.app_user_role OWNER TO postgres_user;

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
    thumbnail_file bigint,
    use_thumbnail boolean DEFAULT false,
    updated_date timestamp(6) without time zone,
    updated_user bigint
);


ALTER TABLE public.file_manager OWNER TO postgres_user;

--
-- Name: file_mime; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.file_mime (
    id bigint NOT NULL,
    name character varying(125)
);


ALTER TABLE public.file_mime OWNER TO postgres_user;

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
-- Name: files_directory_path; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.files_directory_path (
    files_directory bigint NOT NULL,
    files_directory_parent bigint NOT NULL,
    level integer NOT NULL
);


ALTER TABLE public.files_directory_path OWNER TO postgres_user;

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
-- Name: performance_dashboard; Type: VIEW; Schema: public; Owner: postgres_user
--

CREATE VIEW public.performance_dashboard AS
 SELECT 'Cache Hit Ratio'::text AS metric,
    (round(((100.0 * sum(pg_stat_database.blks_hit)) / sum((pg_stat_database.blks_hit + pg_stat_database.blks_read))), 2) || '%'::text) AS value,
        CASE
            WHEN (round(((100.0 * sum(pg_stat_database.blks_hit)) / sum((pg_stat_database.blks_hit + pg_stat_database.blks_read))), 2) > (99)::numeric) THEN 'Good'::text
            WHEN (round(((100.0 * sum(pg_stat_database.blks_hit)) / sum((pg_stat_database.blks_hit + pg_stat_database.blks_read))), 2) > (95)::numeric) THEN 'Warning'::text
            ELSE 'Critical'::text
        END AS status
   FROM pg_stat_database
UNION ALL
 SELECT 'Active Connections'::text AS metric,
    (count(*))::text AS value,
        CASE
            WHEN (count(*) < 100) THEN 'Good'::text
            WHEN (count(*) < 150) THEN 'Warning'::text
            ELSE 'Critical'::text
        END AS status
   FROM pg_stat_activity
  WHERE (pg_stat_activity.state = 'active'::text)
UNION ALL
 SELECT 'Deadlocks'::text AS metric,
    (sum(pg_stat_database.deadlocks))::text AS value,
        CASE
            WHEN (sum(pg_stat_database.deadlocks) = (0)::numeric) THEN 'Good'::text
            WHEN (sum(pg_stat_database.deadlocks) < (10)::numeric) THEN 'Warning'::text
            ELSE 'Critical'::text
        END AS status
   FROM pg_stat_database;


ALTER VIEW public.performance_dashboard OWNER TO postgres_user;

--
-- Name: permission; Type: TABLE; Schema: public; Owner: postgres_user
--

CREATE TABLE public.permission (
    id bigint NOT NULL,
    code character varying(125) NOT NULL,
    operation_type smallint,
    module character varying(255),
    description text,
    CONSTRAINT permission_operation_type_check CHECK (((operation_type >= 0) AND (operation_type <= 2)))
);


ALTER TABLE public.permission OWNER TO postgres_user;

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
467265176412884992	2026-07-13 16:44:59.423046	2026-08-12 16:44:59.423	t	\N	2026-07-13 16:44:59.872429	\N	f	1	019f5add-971f-70ba-a958-0f2ba5d71a25	350921408848597000	350885844724224000	467265176408690688
\.


--
-- Data for Name: ai_document_meta; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.ai_document_meta (id, deleted, created_date, created_user, updated_date, updated_user, document_type, file_name, is_active) FROM stdin;
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
350888314967953409	f	2025-08-26 13:24:53.476566	350885844724224000	2026-07-13 15:30:33.589819	350885844724224000	t	Developer
467255802483183617	f	2026-07-13 16:07:44.49853	350885844724224000	2026-07-13 16:43:19.911135	350885844724224000	t	Admin
\.


--
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.app_user (id, deleted, created_date, created_user, updated_date, updated_user, active, default_locale, email, password, salt, username, avatar_file_id, cover_file_id) FROM stdin;
350885844724224000	f	\N	\N	\N	\N	t	0	admin@mydomain.com	$2a$10$Z/GTNq9afOxTcBpOCWb43eeAwaAc1xiEoGTkWAv5BkgCBhAQD7PKO	0198e504-0a26-7d15-b748-fbd96785b929	admin	\N	\N
467262738419159040	f	2026-07-13 16:35:18.16159	350885844724224000	2026-07-13 16:35:18.16159	350885844724224000	t	0	super_user@mydomain.com	$2a$10$ecrd7G403QlSseoSUKDDReAtzR8GKJJCG/w75/1RI6KDP6RjWRdce	019f5ad4-b7ab-7a2d-966b-46863d3f6774	super_user	\N	\N
\.


--
-- Data for Name: app_user_role; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.app_user_role (app_user, app_role) FROM stdin;
350885844724224000	350888314967953409
467262738419159040	467255802483183617
\.


--
-- Data for Name: audit_log; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.audit_log (id, action, details, entity_id, entity_name, ip_address, "timestamp", username) FROM stdin;
351972435110662144	CREATE	Permission{code='files_directory_list', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.39	2025-08-29 13:12:47.860243	350885844724224000
351972465980739584	CREATE	Permission{code='files_directory_view', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.39	2025-08-29 13:12:55.221027	350885844724224000
351972492656513024	CREATE	Permission{code='files_directory_manage', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.39	2025-08-29 13:13:01.582505	350885844724224000
351972526756204544	CREATE	Permission{code='file_manager_list', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.39	2025-08-29 13:13:09.712709	350885844724224000
351972551611650048	CREATE	Permission{code='file_manager_view', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.39	2025-08-29 13:13:15.638051	350885844724224000
351972582246846464	CREATE	Permission{code='file_manager_manage', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.39	2025-08-29 13:13:22.942581	350885844724224000
351972752585920512	UPDATE	Role{name='Developer', active=true, id=350888314967953409}	350888314967953409	AppRole	192.168.7.39	2025-08-29 13:14:03.554057	350885844724224000
357097662681452544	CREATE	Permission{code='Test permit', description='Permit description', operationType=CRUD, id=null}	\N	Permission	192.168.7.39	2025-09-12 16:38:37.335543	350885844724224000
428493750956724224	CREATE	Permission{code='ai_document_meta_list', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.112	2026-03-28 17:01:11.256494	null
428493751619424256	CREATE	Permission{code='ai_document_meta_view', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.112	2026-03-28 17:01:11.414833	null
428493751757836288	CREATE	Permission{code='ai_document_meta_manage', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.112	2026-03-28 17:01:11.4485	null
467232497541844992	CREATE	Permission{code='api_client_add', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:35:08.172538	350885844724224000
467232539430359040	CREATE	Permission{code='api_client_edit', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:35:18.160505	350885844724224000
467232558166315008	CREATE	Permission{code='api_client_delete', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:35:22.627489	350885844724224000
467232582635884544	CREATE	Permission{code='permission_add', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:35:28.462139	350885844724224000
467232607294197760	CREATE	Permission{code='permission_edit', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:35:34.341409	350885844724224000
467232628777422848	CREATE	Permission{code='permission_delete', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:35:39.463019	350885844724224000
467232655738408960	CREATE	Permission{code='app_role_add', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:35:45.8908	350885844724224000
467232678135992320	CREATE	Permission{code='app_role_edit', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:35:51.231459	350885844724224000
467232697450762240	CREATE	Permission{code='app_role_delete', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:35:55.836814	350885844724224000
467232723019239424	CREATE	Permission{code='app_user_add', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:36:01.931541	350885844724224000
467232741205741568	CREATE	Permission{code='app_user_edit', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:36:06.267302	350885844724224000
467232760323379200	CREATE	Permission{code='app_user_delete', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:36:10.826315	350885844724224000
467232795056410624	CREATE	Permission{code='files_directory_add', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:36:19.106002	350885844724224000
467232816996814848	CREATE	Permission{code='files_directory_edit', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:36:24.337564	350885844724224000
467232842133278720	CREATE	Permission{code='files_directory_delete', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:36:30.330975	350885844724224000
467232864035934208	CREATE	Permission{code='file_manager_add', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:36:35.553896	350885844724224000
467232885200392192	CREATE	Permission{code='file_manager_edit', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:36:40.598616	350885844724224000
467232954750341120	CREATE	Permission{code='file_manager_delete', description='null', operationType=CRUD, id=null}	\N	Permission	192.168.7.120	2026-07-13 14:36:57.180842	350885844724224000
467246443770417152	UPDATE	Role{name='Developer', active=true, id=350888314967953409}	350888314967953409	AppRole	192.168.7.120	2026-07-13 15:30:33.210135	350885844724224000
467246445347475456	UPDATE	Role{name='Developer', active=true, id=350888314967953409}	350888314967953409	AppRole	192.168.7.120	2026-07-13 15:30:33.589819	350885844724224000
467247545882513408	DELETE	Permission{code='ai_document_meta_manage', description='null', operationType=CRUD, id=428493751757836289}	428493751757836289	Permission	192.168.7.120	2026-07-13 15:34:55.974749	350885844724224000
467250121831092224	DELETE	Permission{code='ai_document_meta_list', description='null', operationType=CRUD, id=428493751057387520}	428493751057387520	Permission	192.168.7.120	2026-07-13 15:45:10.131748	350885844724224000
467250132237160448	DELETE	Permission{code='ai_document_meta_view', description='null', operationType=CRUD, id=428493751619424257}	428493751619424257	Permission	192.168.7.120	2026-07-13 15:45:12.613058	350885844724224000
467254933171736576	DELETE	Permission{code='api_client_manage', description='', operationType=CRUD, id=350898678438825984}	350898678438825984	Permission	192.168.7.120	2026-07-13 16:04:17.244434	350885844724224000
467254948527083520	DELETE	Permission{code='app_role_manage', description='', operationType=CRUD, id=350899010636091393}	350899010636091393	Permission	192.168.7.120	2026-07-13 16:04:20.906002	350885844724224000
467254964989726720	DELETE	Permission{code='app_user_manage', description='', operationType=CRUD, id=350899073227689985}	350899073227689985	Permission	192.168.7.120	2026-07-13 16:04:24.831074	350885844724224000
467255257450156032	DELETE	Permission{code='permission_manage', description='', operationType=CRUD, id=350898947750891521}	350898947750891521	Permission	192.168.7.120	2026-07-13 16:05:34.559397	350885844724224000
467255275640852480	DELETE	Permission{code='files_directory_manage', description='null', operationType=CRUD, id=351972492660707328}	351972492660707328	Permission	192.168.7.120	2026-07-13 16:05:38.896034	350885844724224000
467255353596186624	DELETE	Permission{code='file_manager_manage', description='null', operationType=CRUD, id=351972582251040768}	351972582251040768	Permission	192.168.7.120	2026-07-13 16:05:57.482793	350885844724224000
467255802483183616	CREATE	Role{name='Admin', active=true, id=null}	\N	AppRole	192.168.7.120	2026-07-13 16:07:44.505343	350885844724224000
467261782780547072	UPDATE	Role{name='Admin', active=true, id=467255802483183617}	467255802483183617	AppRole	192.168.7.120	2026-07-13 16:31:30.317654	350885844724224000
467261783808151552	UPDATE	Role{name='Admin', active=true, id=467255802483183617}	467255802483183617	AppRole	192.168.7.120	2026-07-13 16:31:30.56392	350885844724224000
467264758341439488	UPDATE	Role{name='Admin', active=true, id=467255802483183617}	467255802483183617	AppRole	192.168.7.120	2026-07-13 16:43:19.74792	350885844724224000
467264759025111040	UPDATE	Role{name='Admin', active=true, id=467255802483183617}	467255802483183617	AppRole	192.168.7.120	2026-07-13 16:43:19.911735	350885844724224000
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
453139911315296256	/my-drive/folder/0	350885844724224000
\.


--
-- Data for Name: file_manager; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.file_manager (id, deleted, created_date, created_user, file_name, file_path, file_size, hidden, locked, original_file_name, readable, writeable, file_mime_id, files_directory_id, owner, description, duration, title, thumbnail_file, use_thumbnail, updated_date, updated_user) FROM stdin;
362136662941110272	f	2025-09-26 14:21:48.618986	350885844724224000	\N	images/202509/350885844724224000_1758871305107_019984e63b947933822c960e457b2221.jpg	378525	t	f	microsoft-edge-6CNB3iD8M4E-unsplash.jpg	t	t	359223407159545856	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:45	\N
362138464176574464	f	2025-09-26 14:28:58.067448	350885844724224000	\N	medias/202509/350885844724224000_1758871737365_019984ecd41575eaabbdafd8856a8a9a.mp4	13131188	f	f	12052b2d-a1ca-4bfe-9c4c-53ab47a2fcbf.mp4	t	t	362072620180443136	\N	350885844724224000	This is my son	55	my boy	362138459625754624	f	2026-04-10 14:21:48	\N
359223407708999680	f	2025-09-18 13:25:34.445353	350885844724224000	\N	images/202509/350885844724224000_1758176733310_01995b7fe8b77173a891df46c929e7c8.jpg	77530	f	f	121375.jpg	t	t	359223407159545856	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:09	\N
362138459625754624	f	2025-09-26 14:28:56.981401	350885844724224000	\N	images/202509/350885844724224000_1758871735876_019984ecce7678f8ba5e47565b622d89.jpg	94621	t	f	thumb_0.jpg	t	t	359223407159545856	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:50	\N
359620755450761216	f	2025-09-19 15:44:29.523119	350885844724224000	\N	images/202509/350885844724224000_1758271467692_01996125710a7283bd4d24182593a34a.jpg	213031	f	f	27196_0.jpg	t	t	359223407159545856	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:12	\N
359620757371752448	f	2025-09-19 15:44:29.988461	350885844724224000	\N	images/202509/350885844724224000_1758271469793_0199612578e170879eb5504e57d2c7e0.jpg	72862	f	f	29884.jpg	t	t	359223407159545856	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:15	\N
359620758734901248	f	2025-09-19 15:44:30.313353	350885844724224000	\N	images/202509/350885844724224000_1758271470064_0199612579f07e87abf74507d2b42b71.jpg	81098	f	f	29885_0.jpg	t	t	359223407159545856	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:18	\N
359620759988998144	f	2025-09-19 15:44:30.612291	350885844724224000	\N	images/202509/350885844724224000_1758271470386_019961257b327914a5667338d8ab0e35.jpg	80265	f	f	29886_0.jpg	t	t	359223407159545856	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:20	\N
362074448804712448	f	2025-09-26 10:14:35.613363	350885844724224000	\N	medias/202509/350885844724224000_1758856474614_01998403eff673c3aa5d1b15d3338b85.mp4	4382542	f	f	c0691557-5c2c-445b-b92c-0388bc2a4ccd.mp4	t	t	362072620180443136	\N	350885844724224000	This is video description	26	This is video title	362074442576171008	f	2026-04-10 14:21:23	\N
362076099674378240	f	2025-09-26 10:21:09.211619	350885844724224000	\N	images/202509/350885844724224000_1758856866883_01998409ec43779c97445ae57f0d4c54.jpg	430932	f	f	anamnesis33-aQBX9fUejQs-unsplash.jpg	t	t	359223407159545856	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:26	\N
362074442576171008	f	2025-09-26 10:14:34.122985	350885844724224000	\N	images/202509/350885844724224000_1758856471838_01998403e54d79d2b2dcad9dec84a5e8.jpg	91210	t	f	thumb_0.jpg	t	t	359223407159545856	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:28	\N
362098026635857920	f	2025-09-26 11:48:17.005048	350885844724224000	\N	files/202509/350885844724224000_1758862096213_01998459b7857beea31341358dc6436c.pptx	2893973	f	f	Springboot_Vue_Training.pptx	t	t	354547531843112960	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:31	\N
362098027478913024	f	2025-09-26 11:48:17.207345	350885844724224000	\N	files/202509/350885844724224000_1758862097133_01998459baed7a4e8033ca809936674e.xlsx	12994	f	f	แบบฟอร์มสำหรับให้เกรดพนักงาน.xlsx	t	t	354547531843112960	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:34	\N
362098846118973440	f	2025-09-26 11:51:32.386114	350885844724224000	\N	files/202509/350885844724224000_1758862292314_0199845cb55a70c09d1361c7d2f67407.docx	17900	f	f	JD-DEV-I-Software Development Manager-20230721-R02.docx	t	t	354547531843112960	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:37	\N
362098847515676672	f	2025-09-26 11:51:32.71943	350885844724224000	\N	files/202509/350885844724224000_1758862292466_0199845cb5f27bd590fbc46e21833aef.pdf	1533584	f	f	JD-DEV-I-Software Development Manager-20230831-R02.pdf	t	t	354547680808013824	\N	350885844724224000	\N	0	\N	\N	f	2026-04-10 14:21:39	\N
362136776816463872	f	2025-09-26 14:22:15.768866	350885844724224000	\N	medias/202509/350885844724224000_1758871308930_019984e64a827a0d8d25ba5ee9f552ab.MP4	222134352	f	f	2022_1204_140014.MP4	t	t	362136776397033472	\N	350885844724224000	acident from road can happen everytime. Becareful when you drive away/	120	Image from dash camera	362136662941110272	f	2026-04-10 14:21:42	\N
\.


--
-- Data for Name: file_mime; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.file_mime (id, name) FROM stdin;
354547531843112960	application/x-tika-ooxml
354547680808013824	application/pdf
354547831169617920	text/plain
359223407159545856	image/jpeg
362072620180443136	video/mp4
362136776397033472	video/quicktime
\.


--
-- Data for Name: files_directory; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.files_directory (id, created_date, created_user, updated_date, updated_user, active, name, files_directory_parent, file_size, latest_updated, owner, file_count, deleted) FROM stdin;
353433118524313600	2025-09-02 13:57:01.926794	350885844724224000	2025-09-02 13:57:01.926794	350885844724224000	t	My Kid	353432953566531584	0	2025-09-03 11:58:10	350885844724224000	0	f
353433203224088576	2025-09-02 13:57:22.120965	350885844724224000	2025-09-02 13:57:22.120965	350885844724224000	t	Family	353432953566531584	0	2025-09-03 11:58:14	350885844724224000	0	f
353433274615336960	2025-09-02 13:57:39.141123	350885844724224000	2025-09-02 13:57:39.141123	350885844724224000	t	Work	353432953566531584	0	2025-09-03 11:58:19	350885844724224000	0	f
353765864903806976	2025-09-03 11:59:14.844627	350885844724224000	2025-09-03 11:59:14.844627	350885844724224000	t	Documents	\N	0	2025-09-03 11:59:14.849357	350885844724224000	0	f
353432953566531584	2025-09-02 13:56:22.593865	350885844724224000	2025-09-03 17:05:58.35763	350885844724224000	t	Images	\N	0	2025-09-03 17:05:58.356483	350885844724224000	0	f
433159884750458880	2026-04-10 14:02:44.238209	350885844724224000	2026-04-10 14:02:44.238209	350885844724224000	t	code	353765864903806976	0	2026-04-10 14:02:44.250412	350885844724224000	0	f
433162205135900672	2026-04-10 14:11:57.467853	350885844724224000	2026-04-10 14:11:57.467853	350885844724224000	t	my work	353765864903806976	0	2026-04-10 14:11:57.469402	350885844724224000	0	f
433162585206951936	2026-04-10 14:13:28.085849	350885844724224000	2026-04-10 14:13:28.085849	350885844724224000	t	Videoes	\N	0	2026-04-10 14:13:28.085849	350885844724224000	0	f
\.


--
-- Data for Name: files_directory_path; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.files_directory_path (files_directory, files_directory_parent, level) FROM stdin;
353432953566531584	353432953566531584	0
353433118524313600	353432953566531584	0
353433118524313600	353433118524313600	1
353433203224088576	353432953566531584	0
353433203224088576	353433203224088576	1
353433274615336960	353432953566531584	0
353433274615336960	353433274615336960	1
353765864903806976	353765864903806976	0
433159884750458880	353765864903806976	0
433159884750458880	433159884750458880	1
433162205135900672	353765864903806976	0
433162205135900672	433162205135900672	1
433162585206951936	433162585206951936	0
\.


--
-- Data for Name: login_log; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.login_log (id, created_at, device_id, host_name, ip, login_from, app_user, user_agent) FROM stdin;
467221827618344960	2026-07-13 13:52:44.448	59fa4d75-6237-4564-a3d8-bf4cf670805d	bekaku	192.168.7.120	0	350885844724224000	467221827538653184
467264822560428032	2026-07-13 16:43:35.109	59fa4d75-6237-4564-a3d8-bf4cf670805d	bekaku	192.168.7.120	0	467262738419159040	467221827538653184
467265176408690688	2026-07-13 16:44:59.427	59fa4d75-6237-4564-a3d8-bf4cf670805d	bekaku	192.168.7.120	0	350885844724224000	467221827538653184
\.


--
-- Data for Name: permission; Type: TABLE DATA; Schema: public; Owner: postgres_user
--

COPY public.permission (id, code, operation_type, module, description) FROM stdin;
350897401642356736	api_client_list	0	\N	
350897732065431552	api_client_view	0	\N	
350898898232938496	permission_list	0	\N	
350898969737433089	app_role_list	0	\N	
350898990360825856	app_role_view	0	\N	
350899032308060160	app_user_list	0	\N	
350899050880438273	app_user_view	0	\N	
350945166250479600	login	0	\N	\N
350898930604576768	permission_view	0	\N	Permission(View)
351972435173576704	files_directory_list	0	\N	\N
351972466005905408	files_directory_view	0	\N	\N
351972526756204545	file_manager_list	0	\N	\N
351972551611650049	file_manager_view	0	\N	\N
467232497546039296	api_client_add	0	\N	\N
467232539430359041	api_client_edit	0	\N	\N
467232558166315009	api_client_delete	0	\N	\N
467232582640078848	permission_add	0	\N	\N
467232607294197761	permission_edit	0	\N	\N
467232628781617152	permission_delete	0	\N	\N
467232655738408961	app_role_add	0	\N	\N
467232678140186624	app_role_edit	0	\N	\N
467232697450762241	app_role_delete	0	\N	\N
467232723019239425	app_user_add	0	\N	\N
467232741209935872	app_user_edit	0	\N	\N
467232760323379201	app_user_delete	0	\N	\N
467232795056410625	files_directory_add	0	\N	\N
467232816996814849	files_directory_edit	0	\N	\N
467232842133278721	files_directory_delete	0	\N	\N
467232864040128512	file_manager_add	0	\N	\N
467232885200392193	file_manager_edit	0	\N	\N
467232954750341121	file_manager_delete	0	\N	\N
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
350888314967953409	467232954750341121
350888314967953409	467232760323379201
350888314967953409	467232628781617152
350888314967953409	467232842133278721
350888314967953409	351972526756204545
350888314967953409	350898898232938496
350888314967953409	467232655738408961
350888314967953409	350898969737433089
350888314967953409	467232723019239425
350888314967953409	350899032308060160
350888314967953409	351972435173576704
350888314967953409	350897401642356736
350888314967953409	467232885200392193
350888314967953409	467232539430359041
350888314967953409	350945166250479600
350888314967953409	467232607294197761
350888314967953409	467232741209935872
350888314967953409	467232816996814849
350888314967953409	467232795056410625
350888314967953409	467232558166315009
350888314967953409	350899050880438273
350888314967953409	467232678140186624
350888314967953409	350897732065431552
350888314967953409	467232497546039296
350888314967953409	350898930604576768
350888314967953409	350898990360825856
350888314967953409	467232582640078848
350888314967953409	351972551611650049
350888314967953409	351972466005905408
350888314967953409	467232864040128512
350888314967953409	467232697450762241
467255802483183617	467232864040128512
467255802483183617	350898990360825856
467255802483183617	467232842133278721
467255802483183617	350897732065431552
467255802483183617	467232954750341121
467255802483183617	467232497546039296
467255802483183617	467232795056410625
467255802483183617	467232558166315009
467255802483183617	467232885200392193
467255802483183617	467232655738408961
467255802483183617	350897401642356736
467255802483183617	350945166250479600
467255802483183617	350899032308060160
467255802483183617	350899050880438273
467255802483183617	351972526756204545
467255802483183617	351972435173576704
467255802483183617	467232723019239425
467255802483183617	351972466005905408
467255802483183617	467232816996814849
467255802483183617	351972551611650049
467255802483183617	350898969737433089
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
467221827538653184	Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36
\.


--
-- Name: access_token access_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.access_token
    ADD CONSTRAINT access_token_pkey PRIMARY KEY (id);


--
-- Name: ai_document_meta ai_document_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.ai_document_meta
    ADD CONSTRAINT ai_document_meta_pkey PRIMARY KEY (id);


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
-- Name: user_agent user_agent_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.user_agent
    ADD CONSTRAINT user_agent_pkey PRIMARY KEY (id);


--
-- Name: idx1g886n9ijc3v1kn2ja05c61gx; Type: INDEX; Schema: public; Owner: postgres_user
--

CREATE INDEX idx1g886n9ijc3v1kn2ja05c61gx ON public.login_log USING btree (device_id);


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
-- Name: favorite_menu fksul1w41buaq90mmjqtngiqjrk; Type: FK CONSTRAINT; Schema: public; Owner: postgres_user
--

ALTER TABLE ONLY public.favorite_menu
    ADD CONSTRAINT fksul1w41buaq90mmjqtngiqjrk FOREIGN KEY (app_user) REFERENCES public.app_user(id);


--
-- PostgreSQL database dump complete
--

\unrestrict vVWOxoFdHyaGoORtewflTBvIbqBNasD7QeT9JIy3Kus1PaCdfNrF9U3CcqjPEhF

