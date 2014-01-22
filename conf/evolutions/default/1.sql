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
  constraint pk_distance primary key (id))
;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists distance;

SET REFERENTIAL_INTEGRITY TRUE;

