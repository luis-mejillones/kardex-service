# --- !Ups
create table kardex (
  id               bigint auto_increment not null,
  date_            timestamp null,
  product_id       varchar(32),
  quantity         int,
  price            double,
  weighted_price   double,
  constraint pk_kardex primary key (id)
);

# --- !Downs
drop table if exists kardex;
