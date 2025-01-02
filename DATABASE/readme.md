# Database
I have implemeneted a postgresql database to store and access the live data. 

## Notes
### Administrative Accessing
```Bash
psql --host sqid.ink --username <username> --password --dbname capstone
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
