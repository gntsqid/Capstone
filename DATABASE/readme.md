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
use capstone;
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

---
#### Firewall
This gets its own section so I don't forget.
mariadb uses port 3306.
```Bash
sudo ufw allow 3306/tcp
```
```Bash
sudo ufw reload
```

---
### Administrative Accessing
```Bash
mariadb -u user -p --database=capstone
```
We can also execute commands
```Bash
mariadb -u <username> -p --database=capstone --execute='describe machines;'
```

---
### Tables
> dev-note: 2/11/2025
```SQL
MariaDB [capstone]> show tables;
+--------------------+
| Tables_in_capstone |
+--------------------+
| machines           |
+--------------------+
1 row in set (0.000 sec)

MariaDB [capstone]> describe machines;
+-------------------------+--------------+------+-----+---------+----------------+
| Field                   | Type         | Null | Key | Default | Extra          |
+-------------------------+--------------+------+-----+---------+----------------+
| machine_id              | int(11)      | NO   | PRI | NULL    | auto_increment |
| hostname                | varchar(100) | NO   |     | NULL    |                |
| online                  | tinyint(1)   | NO   |     | 0       |                |
| parking_lot             | varchar(50)  | NO   |     | unknown |                |
| parking_space           | varchar(50)  | NO   |     | unknown |                |
| type                    | varchar(50)  | NO   |     | unknown |                |
| parking_space_available | tinyint(1)   | NO   |     | 0       |                |
+-------------------------+--------------+------+-----+---------+----------------+
7 rows in set (0.001 sec)
```

---
#### Machines
> dev-note: 2/11/2025
```Bash
MariaDB [capstone]> select * from machines;
+------------+-----------+--------+-------------+---------------+--------+-------------------------+
| machine_id | hostname  | online | parking_lot | parking_space | type   | parking_space_available |
+------------+-----------+--------+-------------+---------------+--------+-------------------------+
|          1 | oni       |      0 | unknown     | unknown       | relay  |                       0 |
|          2 | kitsune   |      0 | unknown     | unknown       | sensor |                       0 |
|          3 | jotunheim |      0 | N/A         | N/A           | server |                       0 |
+------------+-----------+--------+-------------+---------------+--------+-------------------------+
3 rows in set (0.000 sec)
```

---
## Modifications
```SQL
update machines set <field>=<new value> where <field>=<describor value>;
```

---
## API
I gave up on exposing the API via raw C web sockets.\
We will be using gunicorn and uvicorn
```Bash
pip3 install gunicorn uvicorn
```
```Bash
gunicorn -w 4 -k uvicorn.workers.UvicornWorker db-api:app --bind 0.0.0.0:8000
```
Be sure to set the mariadb to allow connections from anywhere:
```Bash
# replace bind-address with 0.0.0.0
sudo vim /etc/mysql/mariadb.conf.d/50-server.cnf
```
