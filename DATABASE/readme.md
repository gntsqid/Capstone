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
> dev-note: updated 4/7/2025
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
| lng                     | decimal(9,6) | YES  |     | NULL    |                |
| lat                     | decimal(9,6) | YES  |     | NULL    |                |
| type                    | varchar(50)  | NO   |     | unknown |                |
| parking_space_available | tinyint(1)   | NO   |     | 0       |                |
+-------------------------+--------------+------+-----+---------+----------------+
9 rows in set (0.001 sec)
```

---
#### Machines
> dev-note: 2/11/2025
```SQL
insert into machines (hostname, online, parking_lot, parking_space, type, parking_space_available) values ('oni', false,'unknown','unknown','relay', false);
```
```SQL
INSERT INTO machines (hostname, online, parking_lot, parking_space, lng, lat, type, parking_space_available) VALUES
('oni',      1, 'N/A', 'N/A',        -119.041125, 34.1589012, 'relay',  0),
('kitsune',  0, 'N/A', 'N/A',        -119.041125, 34.1589012, 'sensor', 1),
('fake-1',   1, 'A1',  'AA1',        -119.0427, 34.1635, 'sensor', 1),
('fake-2',   1, 'A1',  'AA2',        -119.0427, 34.1635, 'sensor', 1),
('fake-3',   1, 'A10', 'B01',        -119.0482936, 34.1609174, 'sensor', 0),
('fake-4',   1, 'A10', 'C15',        -119.0482936, 34.1609174, 'sensor', 0),
('fake-5',   1, 'A7',  'AA1',        -119.0417317, 34.1608505, 'sensor', 1);
```
> above is from after the GPS update
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

```Bash
odin@jotunheim:~/DATABASE$ curl -X GET https://api.capstone.sqid.ink/machines -H "X-API-key: $CAPSTONE_API_SECRET" | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   402  100   402    0     0   3956      0 --:--:-- --:--:-- --:--:--  3980
[
  {
    "machine_id": 1,
    "hostname": "oni",
    "online": 1,
    "parking_lot": "N/A",
    "parking_space": "N/A",
    "type": "relay",
    "parking_space_available": 0
  },
  {
    "machine_id": 2,
    "hostname": "kitsune",
    "online": 0,
    "parking_lot": "A0",
    "parking_space": "A01",
    "type": "sensor",
    "parking_space_available": 0
  },
  {
    "machine_id": 3,
    "hostname": "jotunheim",
    "online": 1,
    "parking_lot": "N/A",
    "parking_space": "N/A",
    "type": "server",
    "parking_space_available": 0
  }
]
```

---
# AUTO UPDATE TABLES
After making a new table, I can fill it with stuff from my current one
```SQL
CREATE TABLE sensor_table (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    parking_lot VARCHAR(50) NOT NULL DEFAULT 'unknown',
    parking_space VARCHAR(50) NOT NULL DEFAULT 'unknown',
    parking_space_available TINYINT(1) NOT NULL DEFAULT 0
);

INSERT INTO sensor_table (parking_lot, parking_space, parking_space_available)
SELECT parking_lot, parking_space, parking_space_available
FROM machines
WHERE type = 'sensor';
```
**OR**

Here is an attempt from chatGPT on how to do that which I have not tried yet:
```SQL
-- Create the separate table with an auto-increment primary key
CREATE TABLE sensor_table (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    parking_lot VARCHAR(50) NOT NULL DEFAULT 'unknown',
    parking_space VARCHAR(50) NOT NULL DEFAULT 'unknown',
    parking_space_available TINYINT(1) NOT NULL DEFAULT 0
);

-- Trigger to handle INSERT operations on machines
DELIMITER //
CREATE TRIGGER sensor_after_insert
AFTER INSERT ON machines
FOR EACH ROW
BEGIN
  IF NEW.type = 'sensor' THEN
    INSERT INTO sensor_table (parking_lot, parking_space, parking_space_available)
    VALUES (NEW.parking_lot, NEW.parking_space, NEW.parking_space_available);
  END IF;
END;//
DELIMITER ;

-- Trigger to handle UPDATE operations on machines
DELIMITER //
CREATE TRIGGER sensor_after_update
AFTER UPDATE ON machines
FOR EACH ROW
BEGIN
  IF NEW.type = 'sensor' THEN
    UPDATE sensor_table
    SET parking_lot = NEW.parking_lot,
        parking_space = NEW.parking_space,
        parking_space_available = NEW.parking_space_available
    WHERE parking_lot = OLD.parking_lot AND parking_space = OLD.parking_space;
    IF ROW_COUNT() = 0 THEN
      INSERT INTO sensor_table (parking_lot, parking_space, parking_space_available)
      VALUES (NEW.parking_lot, NEW.parking_space, NEW.parking_space_available);
    END IF;
  ELSE
    DELETE FROM sensor_table
    WHERE parking_lot = OLD.parking_lot AND parking_space = OLD.parking_space;
  END IF;
END;//
DELIMITER ;

-- Trigger to handle DELETE operations on machines
DELIMITER //
CREATE TRIGGER sensor_after_delete
AFTER DELETE ON machines
FOR EACH ROW
BEGIN
  IF OLD.type = 'sensor' THEN
    DELETE FROM sensor_table
    WHERE parking_lot = OLD.parking_lot AND parking_space = OLD.parking_space;
  END IF;
END;//
DELIMITER ;
```

