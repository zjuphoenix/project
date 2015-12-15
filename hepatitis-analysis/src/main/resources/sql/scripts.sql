create table USER (USERNAME varchar(255), PASSWORD varchar(255), PASSWORD_SALT varchar(255), FIRST_NAME varchar(50), LAST_NAME varchar(50), ADDRESS varchar(255) );
create table USER_ROLES (USERNAME varchar(255), ROLE varchar(255));
create table ROLES_PERMS (ROLE varchar(255), PERM varchar(255));
insert into USER values ('admin', '915FB5C6B0A19BADF7022FE7AE57B1F1F4488AF2741800D8CC21D266E47A89F6442091285C13FD61EDD3B4F0C365CD3DD0D51FA5AD2AA6538F9883ACB61C848D', 'C7AD44CBAD762A5DA0A452F9E854FDC1E0E7A52A38015F23F3EAB1D80B931DD472634DFAC71CD34EBC35D16AB7FB8A90C81F975113D6C7538DC69DD8DE9077EC', 'Super', 'Admin', 'zju');
insert into USER values ('wuhaitao', '2B98E191BE33333CCA55EB7C51646E20B3B759285321E8B835CBFD62F9564DFD614199271FEB93185260FCA66263730910251F632078698DDEE11D799CEAA8D2', 'B14361404C078FFD549C03DB443C3FEDE2F3E534D73F78F77301ED97D4A436A9FD9DB05EE8B325C0AD36438B43FEC8510C204FC1C1EDB21D0941C00E9E2C1CE2', 'Wu', 'Haitao', 'zju');
insert into USER_ROLES values ('admin', 'admin');
insert into USER_ROLES values ('wuhaitao', 'user');
insert into ROLES_PERMS values ('user', 'operate');
insert into ROLES_PERMS values ('admin', 'manage_user');
insert into ROLES_PERMS values ('admin', 'operate');