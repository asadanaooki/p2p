delete from item
where kind is null;

alter table item alter kind set not null;