# CSV-Random-Data-Generator
Generates random data for a SQL database.

# Usage

## 1. Clone
```
git clone https://github.com/kcheeeung/CSV-Random-Data-Generator.git && cd CSV-Random-Data-Generator
```

## 2. Compile
```
javac DataGen.java
```

## 3. Run
Insert your DDL. Pass in the number of desired records. 
```
java DataGen 10
```

## 4. Run in Advanced Mode (Optional)
The `show create table MYTABLE` is formatted like below and needs clean up.
```
CREATE TABLE `mytable` (
    `firstname` string,
    `lastname` string,
    `age` int,
    `height` float
)
```
The argument `clean` does a basic cleanup, allowing you to just paste the columns of the DDL.
```
java DataGen 10 clean
```

# Supported Types
- tinyint
- smallint
- int
- bigint
- float
- double
- string
- date
- timestamp
- boolean
