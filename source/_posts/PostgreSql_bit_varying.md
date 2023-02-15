---
title: PostgreSql bit varying
date: 2023-02-15 15:32:00
categories:
- [我永远爱学习]
tags:
- Database
---
# PostgreSql bit varying

## 应用场景
- 存储和可视化位掩码

## 参考
- [Bit String Types](https://www.postgresql.org/docs/15/datatype-bit.html)
- [Bit String Functions and Operators](https://www.postgresql.org/docs/15/functions-bitstring.html)

是动态长度的bit，以可读二进制数的方式展示

```sql
SELECT B'10001' as num；
```

| num |
| --- |
|10001|

## 运算
- `&`位与运算
- `|`位或运算
- `#`异或运算
- `~`非运算
- `<<`位左移
- `>>`位右移
- `||`位拼接

```sql
SELECT
    B'10001' & B'01101' as "and",
    B'10001' | B'01101' as "or",
	B'10001' # B'01101' as "eor ",
	~B'10001'  as "not",
	B'11111' >> 2  as "shiftL",
	B'11111' << 2  as "shiftR",
    B'101' || B'01' as "cancat ";
```
| and | or  | eor | not | shiftL | shiftR | cancat |
| --- | ---  | --- | --- | --- | --- | --- |
| 00001 | 11101 | 11100 | 01110 | 00111 | 11100 | 10101 |


## 转换

```sql
SELECT 
    B'10001' as "varbit",
	cast(B'10001' as bit(3)) as "bit3",
    cast(B'10001' as bit(5)) as "bit",
    cast(B'10001' as bit(7)) as "bit7",
    cast(cast(B'10001' as bit(5)) as  integer)  as  "int",
    B'10001'::bit(5)::integer as "intLambda";
```
| varbit|  bit3 |  bit  |  bit7 |  int  | intLambda | 
|  ---  |  ---  |  ---  |  ---  |  ---  |    ---    |
| 10001 |  100  | 10001 |1000100|  17   |     17    |