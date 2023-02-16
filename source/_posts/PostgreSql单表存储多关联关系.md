---
title: PostgreSql单表存储多关联关系
date: 2023-02-16 09:56:00
cover: https://oss.note.dreamccc.cn/note/images/posts/PostgreSql单表存储多关联关系/title.png?x-oss-process=style/blog_title
categories:
- [我永远爱学习]
tags:
- Database
---
## 单表存储多关联关系

需求是`product`、`project`、`pipeline`三个实体各自和互相都会互相关联，用于存储多个键值对。
如果是标准数据库设计就需要6张表来存储：
 - product_kv
 - project_kv
 - pipeline_kv
 - product_project_kv
 - project_pipeline_kv
 - pipeline_product_kv
  
### 使用bit存储对象关联关系

这里采用单表+二进制运算的方式来存储这一数据:

```sql
CREATE TABLE "public"."variable" (
  "type" bit(3) NOT NULL,
  "1_product_id" varchar(255) NOT NULL DEFAULT ''::character varying,
  "2_project_id" varchar(255) NOT NULL DEFAULT ''::character varying,
  "3_pipeline_id" varchar(255) NOT NULL DEFAULT ''::character varying,
  "v_key" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "v_value" text COLLATE "pg_catalog"."default" NOT NULL,
  CONSTRAINT "variable_pkey" PRIMARY KEY ("type", "1_product_id", "2_project_id", "3_pipeline_id")
);
```
用type字段来标识具体是那两个对象产生了关联,
```
bit(3)
0 product关联位
0 project关联位
0 pipeline关联位
```
- type=b'100'时，该kv值属于product
- type=b'010'时，该kv值属于project
- type=b'001'时，该kv值属于pipeline
- type=b'110'时，该kv值属于product与project的关联
- 以此类推...

### 查询方式
**准备数据**  
```sql
INSERT INTO "public"."variable" ("type", "1_product_id", "2_project_id", "3_pipeline_id", "v_key", "v_value") VALUES ('000', '', '', '', '1', '1');
INSERT INTO "public"."variable" ("type", "1_product_id", "2_project_id", "3_pipeline_id", "v_key", "v_value") VALUES ('010', '', '1', '', '1', '1');
INSERT INTO "public"."variable" ("type", "1_product_id", "2_project_id", "3_pipeline_id", "v_key", "v_value") VALUES ('001', '', '', '1', '1', '1');
INSERT INTO "public"."variable" ("type", "1_product_id", "2_project_id", "3_pipeline_id", "v_key", "v_value") VALUES ('111', '1', '1', '1', '1', '1');
INSERT INTO "public"."variable" ("type", "1_product_id", "2_project_id", "3_pipeline_id", "v_key", "v_value") VALUES ('110', '1', '1', '', '1', '1');
INSERT INTO "public"."variable" ("type", "1_product_id", "2_project_id", "3_pipeline_id", "v_key", "v_value") VALUES ('100', '1', '', '', '1', '1');

```

**查询**  
- 单查某一关联关系的值: `SELECT * FROM "variable" WHERE "type"=b'100' AND "1_product_id" = '1'`
- 单查与某个实体产生关系的所有值: `SELECT * FROM "variable" WHERE "type"&b'100'=b'100' AND "1_product_id" = '1'`
- 单查与多个个实体产生关系的所有值: `SELECT * FROM "variable" WHERE "type"&b'110'=b'110' AND "1_product_id" = '1' AND "2_project_id" = '1'`

### 扩展方式

如果之后出现第四个实体`4_tag`，也需要与前几个字段建立关联关系，那么只需要简单扩展`type`字段的长度就可以了，当然也要同时变更下查询的SQL。  
需要注意的是，类型为`bit`的字段长度是不可变的，无论是增加长度还是缩减长度都需要现将字段类型改为`varbit`后再改回`bit`，例如`bit(3)`->`varbit(5)`->`bit(5)`。  
此时，字段长度增加后原数据都将向左移并补0，虽然上其数据值会增大，但是并不影响我们对关联关系的查询。