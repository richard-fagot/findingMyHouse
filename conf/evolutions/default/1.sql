# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table distance (
  id                        bigint auto_increment not null,
  origin                    varchar(255),
  destination               varchar(255),
  distance                  double,
  duration                  double,
  lon                       double,
  lat                       double,
  is_allowed                boolean,
  constraint uq_distance_1 unique (origin,destination),
  constraint pk_distance primary key (id))
;

create table last_call (
  date                      timestamp not null,
  constraint pk_last_call primary key (date))
;

create table small_ads (
  id                        bigint auto_increment not null,
  url                       varchar(255),
  distance_id               bigint,
  surface                   double,
  price                     double,
  is_accepted               boolean,
  small_ads_site            varchar(255),
  constraint pk_small_ads primary key (id))
;

create sequence last_call_seq;

alter table small_ads add constraint fk_small_ads_distance_1 foreign key (distance_id) references distance (id) on delete restrict on update restrict;
create index ix_small_ads_distance_1 on small_ads (distance_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists distance;

drop table if exists last_call;

drop table if exists small_ads;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists last_call_seq;

