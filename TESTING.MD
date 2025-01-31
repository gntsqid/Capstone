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
After doing a few of these, we have a working simple table with a few things int it.\
The next step is to attempt to access it via an API.

---
## DATABASE API
First, for any API, it is suggested to grab the folllowing:
```Bash
sudo apt install -y jq ncat
```
suggested BASH based API Server:
```Bash
#!/bin/bash

DB_USER="user"
DB_PASS="password"
DB_NAME="stuff"
TOKEN_FILE="tokens.txt"
PORT=8080

# Function to generate a random token
generate_token() {
    echo "$(date +%s | sha256sum | head -c 16)"
}

# Function to authenticate user and generate token
authenticate_user() {
    read -r username password <<<"$1"
    if [[ "$username" == "user" && "$password" == "password" ]]; then
        token=$(generate_token)
        echo "$token" >> "$TOKEN_FILE"
        echo "{\"auth_token\": \"$token\"}"
    else
        echo "{\"error\": \"Invalid credentials\"}"
    fi
}

# Function to validate token
validate_token() {
    local token="$1"
    grep -q "$token" "$TOKEN_FILE" && echo "valid" || echo "invalid"
}

# Function to query the database
get_thing() {
    local thing="$1"
    psql -U "$DB_USER" -d "$DB_NAME" -t -A -c "SELECT * FROM table_1 WHERE description='$thing'" | jq -R -s 'split("\n")[:-1] | map(split("|") | {id: .[0], description: .[1], created_at: .[2]})'
}

# Start API Server
while true; do
    echo "Listening on port $PORT..."
    # Read input from netcat
    request=$(nc -l -p "$PORT" -q 1)
    
    # Parse the HTTP request method and body
    method=$(echo "$request" | head -n 1 | awk '{print $1}')
    url=$(echo "$request" | head -n 1 | awk '{print $2}')
    body=$(echo "$request" | tail -n 1)
    
    # Authentication request
    if [[ "$url" == "/api/auth" && "$method" == "POST" ]]; then
        auth_response=$(authenticate_user "$body")
        echo -e "HTTP/1.1 200 OK\nContent-Type: application/json\n\n$auth_response"

    # Get thing request
    elif [[ "$url" =~ "/api/stuff/get-thing" && "$method" == "POST" ]]; then
        auth_token=$(echo "$body" | grep -oP '(?<=auth: ")[^"]+')
        thing=$(echo "$body" | grep -oP '(?<=thing: ")[^"]+')

        if [[ "$(validate_token "$auth_token")" == "valid" ]]; then
            data=$(get_thing "$thing")
            echo -e "HTTP/1.1 200 OK\nContent-Type: application/json\n\n$data"
        else
            echo -e "HTTP/1.1 403 Forbidden\nContent-Type: application/json\n\n{\"error\": \"Invalid token\"}"
        fi

    else
        echo -e "HTTP/1.1 404 Not Found\nContent-Type: application/json\n\n{\"error\": \"Invalid endpoint\"}"
    fi
done
```
Then we run it and test:
```Bash
curl -X POST http://localhost:8080/api/auth -d "user password"
```
We should get a token back, then we can test further:
```Bash
curl -X POST http://localhost:8080/api/stuff/get-thing -d 'auth: "5f4dcc3b5aa765d6" thing: "thing_2"'
```
Ideally, we'll get a response like the following:
```JSON
[
  {
    "id": "2",
    "description": "thing_2",
    "created_at": "2025-01-31 18:29:23.148847"
  }
]
```





















