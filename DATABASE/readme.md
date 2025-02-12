# Database
~~I have implemeneted a postgresql database to store and access the live data.~~\
I have switched from postgresql to mariadb.\
Please see initial commits for postgres commands if needed.

## Notes
### Setup
```Bash
sudo apt install mariadb-server -y
```
```Bash
sudo systemctl enable --now mariadb
```
```Bash
sudo mariadb -u root
```
```SQL
CREATE DATABASE capstone;
use database capstone;
```
```SQL
CREATE TABLE machines (
    machine_id INT AUTO_INCREMENT PRIMARY KEY,
    hostname VARCHAR(100) NOT NULL,
    online BOOLEAN NOT NULL DEFAULT FALSE,
    parking_lot varchar(50) not null default 'unknown',
    parking_space varchar(50) not null default 'unknown',
    type varchar(50) not null default 'unknown',
    parking_space_available boolean not null default false
);
```
```SQL
grant all privileges on capstone.* to '<username>'@'%' identified by '<password>';
flush privileges;
```
```Bash
sudo systemctl restart mariadb
```

### Administrative Accessing
```Bash
mariadb -u user -p --database=capstone
```
We can also execute commands
```Bash
mariadb -u user -p --database=capstone --execute='select * from machines;'
```

---
### Tables
> dev-note: 1/1/2025
```Bash
capstone=> \dt
         List of relations
 Schema |   Name   | Type  | Owner 
--------+----------+-------+-------
 public | machines | table | odin
(1 row)

capstone=> \d machines
                                                Table "public.machines"
         Column          |         Type          | Collation | Nullable |                   Default                    
-------------------------+-----------------------+-----------+----------+----------------------------------------------
 machine_id              | integer               |           | not null | nextval('machines_machine_id_seq'::regclass)
 hostname                | character varying(23) |           | not null | 
 online                  | boolean               |           | not null | false
 parking_lot             | character varying(7)  |           | not null | 'unknown'::character varying
 parking_space           | character varying(7)  |           | not null | 'unknown'::character varying
 type                    | character varying(50) |           | not null | 'unknown'::character varying
 parking_space_available | boolean               |           |          | false
Indexes:
    "machines_pkey" PRIMARY KEY, btree (machine_id)
```

---
#### Machines
> dev-note: 1/1/2025
```Bash
capstone=> select * from machines;
 machine_id | hostname  | online | parking_lot | parking_space |  type   | parking_space_available
------------+-----------+--------+-------------+---------------+---------+-------------------------
          1 | oni       | f      | unknown     | unknown       | unknown | f
          2 | kitsune   | f      | unknown     | unknown       | unknown | f
          3 | jotunheim | t      | N/A         | N/A           | server  |
(3 rows)
```
