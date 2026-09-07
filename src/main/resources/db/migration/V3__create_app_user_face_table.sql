create table app_user_face
(
    id           bigint not null,
    created_date timestamp(6),
    created_user bigint,
    updated_date timestamp(6),
    updated_user bigint,
    embedding    vector(512) not null,
    app_user     bigint not null,
    file_manager bigint,
    primary key (id)
);
comment on column app_user_face.file_manager is 'FK -> Ref table: file_manager (id). Reference to user image file';
comment
on table app_user_face is 'Table for storing user face data.';
comment
on column app_user_face.id is 'Primary Key';
comment
on column app_user_face.created_date is 'Timestamp when this record was created';
comment
on column app_user_face.created_user is 'Identifier of the user who created this record';
comment
on column app_user_face.updated_date is 'Timestamp when this record was last modified';
comment
on column app_user_face.updated_user is 'Identifier of the user who last updated this record';
alter table if exists app_user_face add constraint FKjqy5hop0me7x8wg5tu8wdha4d foreign key (app_user) references app_user;
alter table if exists app_user_face add constraint FKrye9xx1tqsdyt59tfx2mqgi96 foreign key (file_manager) references file_manager;

INSERT INTO public.permission (id, code, module, description, operation_type)
VALUES (481290671508951040, 'app_user_face_list', 'app_user_face', 'Permission for app_user_face_list',
        'CRUD') ON CONFLICT (id) DO NOTHING;
INSERT INTO public.permission (id, code, module, description, operation_type)
VALUES (481290671647363072, 'app_user_face_view', 'app_user_face', 'Permission for app_user_face_view',
        'CRUD') ON CONFLICT (id) DO NOTHING;
INSERT INTO public.permission (id, code, module, description, operation_type)
VALUES (481290671664140288, 'app_user_face_add', 'app_user_face', 'Permission for app_user_face_add',
        'CRUD') ON CONFLICT (id) DO NOTHING;
INSERT INTO public.permission (id, code, module, description, operation_type)
VALUES (481290671676723200, 'app_user_face_edit', 'app_user_face', 'Permission for app_user_face_edit',
        'CRUD') ON CONFLICT (id) DO NOTHING;
INSERT INTO public.permission (id, code, module, description, operation_type)
VALUES (481290671693500416, 'app_user_face_delete', 'app_user_face', 'Permission for app_user_face_delete',
        'CRUD') ON CONFLICT (id) DO NOTHING;