
create table evdbelge.parametreler
(
    name  varchar(150) not null,
    value varchar(150)
);



create table evdbelge.belge
(
    id              bigint generated always as identity
        constraint belge_pk
            primary key,
    vkn             varchar(10) not null,
    tckn            varchar(11) not null,
    org_oid         varchar(14) not null,
    belge_no        varchar(20) not null,
    belge_turu      smallint not null,
    durum           smallint not null,
    metadata        jsonb not null,
    usernodeid      varchar(20) not null
);

create index belge_metadata_index
    on evdbelge.belge using gin(metadata jsonb_path_ops);

create unique index belge_oid_uindex
    on evdbelge.belge (id);

create unique index belge_org_oid_belge_no_uindex
    on evdbelge.belge (org_oid, belge_no);

create index belge_usernodeid_index
    on evdbelge.belge (usernodeid);



create table evdbelge.belge_akis
(
    id              bigint generated always as identity
        constraint belge_akis_pk
            primary key,
    belge_id        bigint not null,
    kullanici_kodu  varchar(30) not null,
    usernodeid      varchar(20) not null,
    aciklama        text,
    optime          timestamp default current_timestamp
);

create unique index belge_akis_oid_uindex
    on evdbelge.belge_akis (id);
create index belge_akis_belgeid_index
    on evdbelge.belge_akis (belge_id);





create table evdbelge.belge_hedef
(
    id              bigint generated always as identity
        constraint belge_hedef_pk
            primary key,
    belge_id        bigint not null,
    belge_akis_id   bigint not null,
    hedef_id        smallint not null,
    metadata        jsonb
);

create unique index belge_hedef_uindex
    on evdbelge.belge_hedef (id);
create index belge_hedef_belgeid_index
    on evdbelge.belge_hedef (belge_id);
create index belge_hedef_belge_akisid_index
    on evdbelge.belge_hedef (belge_akis_id);





create table evdbelge.belge_hedef_sonuc
(
    id              bigint generated always as identity
        constraint belge_hedef_sonuc_pk
            primary key,
    belge_hedef_id  bigint not null,
    sonuc_oid    	varchar(14) not null,
    sonuc        	jsonb not null
);

create unique index belge_hedef_sonuc_uindex
    on evdbelge.belge_hedef_sonuc (id);
create index belge_hedef_sonucid_index
    on evdbelge.belge_hedef_sonuc (belge_hedef_id);
