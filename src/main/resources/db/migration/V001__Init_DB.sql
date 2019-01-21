create table config
(
  id                   int8    not null,
  comment_access_token varchar(100),
  commenting           boolean not null,
  group_id             int8    not null,
  group_short_link     varchar(100),
  sync_posts           boolean not null,
  comment_from_group   boolean not null,
  sync_seconds         int4    not null,
  publish_stat         boolean not null,
  primary key (id)
);

create table post
(
  id           int8 not null,
  date         timestamp,
  distance     int4,
  edit_reason  varchar(255),
  last_update  timestamp,
  number       int4,
  status       int4,
  sum_distance int4,
  text         varchar(1000),
  text_hash    varchar(32),
  version      int8 not null,
  from_id      int8,
  primary key (id)
);

create table profile
(
  id             int8 not null,
  birth_date     varchar(255),
  city           varchar(100),
  country        varchar(100),
  domain         varchar(100),
  first_name     varchar(100),
  has_photo      boolean,
  join_date      timestamp,
  last_name      varchar(100),
  last_sync      timestamp,
  photo_100      varchar(255),
  photo_200      varchar(255),
  photo_200_orig varchar(255),
  photo_400_orig varchar(255),
  photo_50       varchar(255),
  photo_max      varchar(255),
  photo_max_orig varchar(255),
  sex            int4 check (sex >= 0 AND sex <= 2),
  version        int8 not null,
  primary key (id)
);

create table stat_log
(
  id           int8 not null,
  end_value    varchar(100),
  post_id      int8 not null,
  publish_date timestamp,
  start_value  varchar(100),
  stat_type    int4,
  version      int8 not null,
  primary key (id)
);

create table temp_data
(
  id             int8 not null,
  last_sync_date timestamp,
  primary key (id)
);

create table user_role
(
  user_id int8 not null,
  role_id int4
);

create table user_table
(
  id               int8    not null,
  account_expired  boolean not null,
  account_locked   boolean not null,
  enabled          boolean not null,
  password         varchar(255),
  password_expired boolean not null,
  username         varchar(255),
  primary key (id)
);

alter table if exists post
  add constraint FK_post___profile_id foreign key (from_id) references profile;

alter table if exists user_role
  add constraint FK_user_role___user_id foreign key (user_id) references user_table;
