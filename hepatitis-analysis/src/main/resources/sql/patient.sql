CREATE TABLE patient (
    id INT COMMENT 'id',
    name VARCHAR(16) COMMENT '����',
    gender VARCHAR(4) COMMENT '�Ա�',
    birth VARCHAR(40) COMMENT '��������',
    address VARCHAR(16) COMMENT '��ַ',
    PRIMARY KEY (id)
)ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO patient VALUES(1,'����','��','1986-06-15','�㽭ʡ������');
INSERT INTO patient VALUES(2,'����','Ů','1976-07-25','�㽭ʡ������');
INSERT INTO patient VALUES(3,'����','��','1981-02-21','�㽭ʡ������');