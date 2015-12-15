CREATE TABLE patient (
    id INT COMMENT 'id',
    name VARCHAR(16) COMMENT '姓名',
    gender VARCHAR(4) COMMENT '性别',
    birth VARCHAR(40) COMMENT '出生日期',
    address VARCHAR(16) COMMENT '地址',
    PRIMARY KEY (id)
)ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO patient VALUES(1,'张三','男','1986-06-15','浙江省杭州市');
INSERT INTO patient VALUES(2,'李四','女','1976-07-25','浙江省杭州市');
INSERT INTO patient VALUES(3,'王五','男','1981-02-21','浙江省杭州市');