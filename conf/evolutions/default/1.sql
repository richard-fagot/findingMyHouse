# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table distance (
  id                        integer primary key AUTOINCREMENT,
  origin                    varchar(255),
  destination               varchar(255),
  distance                  double,
  duration                  double,
  lon                       double,
  lat                       double,
  is_allowed                integer(1))
;

create table last_call (
  date                      timestamp primary key)
;

create table small_ads (
  url                       varchar(255),
  distance_id               integer)
;

alter table small_ads add constraint fk_small_ads_distance_1 foreign key (distance_id) references distance (id);
create index ix_small_ads_distance_1 on small_ads (distance_id);



# --- !Downs

PRAGMA foreign_keys = OFF;

drop table distance;

drop table last_call;

drop table small_ads;

PRAGMA foreign_keys = ON;

