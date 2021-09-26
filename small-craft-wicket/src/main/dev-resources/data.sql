insert into account (id, create_date, password, id_unit)
values ('admin', now(), 'zx80spectrum', null)
on conflict do nothing;
insert into account_roles(account_id, roles)
VALUES ('admin', 'ROLE_ADMIN') on conflict do nothing ;