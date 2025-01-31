# TESTING
This document showcases a few of the tests for my personal notes as well as to act as a secondary developer log.

---
## DATABASE
The main goal to begin my testing phase is the ability to expose my postgres database to an API natively.\
I do not want to use any of the solutions already available, but instead wish to learn API programming.

### SETUP
Install postgres:
```Bash
sudo apt install -y postgresql postgresql-contrib
```
start the service:
```Bash
sudo systemctl start postgresql
sudo systemctl enable postgresql
sudo systemctl status postgresql
```
Switch to the *postgres* user:
```Bash
sudo -i -u postgres
```
Create a test database.\
I am going to call it *stuff*:
```Bash
createdb stuff
```

### ACCESS
Log in to the database:
```Bash
psql
```
Let's now add a simple user:
```Bash
CREATE USER "user" WITH ENCRYPTED PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE stuff TO "user";
ALTER DATABASE stuff OWNER TO "user";
```
quit and switch back to our normal user:
```Bash
\q
```

> Impartant step below:

We now want to *enable remote connections* so that we can sign in to the database from other machines:
```Bash
sudo vim /etc/postgresql/<VERSION NUMBER>/main/postgresql.conf
```
Then un-comment the following line and replace it to listen on all interfaces:
```Bash
#listen_addresses = 'localhost'

# replace with
listen_addresses = '*'
```
Now, we wanmt to actually enable the connections:
```Bash
sudo vim /etc/postgresql/14/main/pg_hba.conf
```
Add the following line to allow conenction from *ANYWHERE*:
```Bash
host    all             all             0.0.0.0/0               md5
```

Restart the service to keep these changes:
```Bash
sudo systemctl restart postgresql
```
### Basic Maneuvering
Log in as our new user:
```Bash
psql -U user -d stuff -W
```
We will be logged in to our "stuff" database.\
Now we want to make a table:
```Bash
CREATE TABLE table_1 (
    id SERIAL PRIMARY KEY,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
See the table you made:
```Bash
# diplay tables in database
\dt
```
then display the table itself:
```Bash
# display table columns
\d <table name>
```
Let's add a few quick things to it:
```Bash
insert into table_1(description) values('thing_1');
```
Then view it:
```Bash
select * from table_1;
```












