create database rabbitmq_db;

create table msg_log
(
    id            bigint auto_increment comment '消息唯一标识'
        primary key,
    msg_id        bigint                              null comment '消息唯一标识',
    exchange_name varchar(50)                         null comment '交换机',
    routing_key   varchar(50)                         null comment '路由键',
    msg           text                                null comment '消息内容',
    msg_status    int       default 0                 null comment '投递状态
0：投递中
1：投递成功
2：投递失败
3：人工修复
4：消费成功
5：重复消费',
    try_count     int       default 0                 not null comment '消息投递重试次数',
    receive_count int       default 0                 not null comment '消费异常消息重新消费次数',
    next_try_time timestamp default CURRENT_TIMESTAMP not null comment '下次重新投递时间',
    create_time   timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    remark        varchar(20)                         null comment '备注'
)
    comment '消息日志表';

create index idx_next_try_time
    on msg_log (next_try_time);

create table stock
(
    id         int auto_increment
        primary key,
    product_id int null comment '产品id',
    stock_num  int null comment '库存数量'
)
    comment '库存表';

insert into stock(product_id, stock_num)
values (1, 10000);

