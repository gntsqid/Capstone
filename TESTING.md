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

My first result:\
![image](https://github.com/user-attachments/assets/cf5bd4ee-6689-4a6c-83fa-aac30f433820)

This keeps failing because of the reliance on netcat, switching to raw networking C:
```C
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <time.h>
#include <libpq-fe.h>

#define PORT 8080
#define MAX_TOKENS 100

typedef struct {
    char token[33];  // 32-char token + null terminator
} AuthToken;

// Array to store auth tokens (simple in-memory storage)
AuthToken tokens[MAX_TOKENS];
int token_count = 0;

// Generate a random authentication token
void generate_token(char *buffer, size_t length) {
    static const char charset[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    for (size_t i = 0; i < length - 1; i++) {
        buffer[i] = charset[rand() % (sizeof(charset) - 1)];
    }
    buffer[length - 1] = '\0';
}

// Validate if token exists
int is_valid_token(const char *token) {
    for (int i = 0; i < token_count; i++) {
        if (strcmp(tokens[i].token, token) == 0) {
            return 1;
        }
    }
    return 0;
}

// Handle authentication request
void handle_auth(int client_socket) {
    char new_token[33];
    generate_token(new_token, 33);

    // Store the token
    if (token_count < MAX_TOKENS) {
        strcpy(tokens[token_count++].token, new_token);
    }

    char response[256];
    snprintf(response, sizeof(response),
        "HTTP/1.1 200 OK\r\n"
        "Content-Type: application/json\r\n"
        "Content-Length: %lu\r\n"
        "\r\n"
        "{\"auth_token\": \"%s\"}\n",
        strlen(new_token) + 20, new_token);

    write(client_socket, response, strlen(response));
}

// Fetch data from PostgreSQL
void handle_get_thing(int client_socket, char *token, char *thing) {
    if (!is_valid_token(token)) {
        const char *error_json = "{\"error\": \"Invalid token\"}\n";
        dprintf(client_socket,
            "HTTP/1.1 403 Forbidden\r\n"
            "Content-Type: application/json\r\n"
            "Content-Length: %lu\r\n"
            "\r\n"
            "%s",
            strlen(error_json), error_json);
        return;
    }

    // Connect to PostgreSQL
    PGconn *conn = PQconnectdb("user=user password=password dbname=stuff");
    if (PQstatus(conn) != CONNECTION_OK) {
        const char *error_json = "{\"error\": \"Database connection failed\"}\n";
        dprintf(client_socket, "HTTP/1.1 500 Internal Server Error\r\n"
                               "Content-Type: application/json\r\n"
                               "Content-Length: %lu\r\n"
                               "\r\n"
                               "%s",
                               strlen(error_json), error_json);
        PQfinish(conn);
        return;
    }

    // Query database
    char query[256];
    snprintf(query, sizeof(query), "SELECT * FROM table_1 WHERE description='%s'", thing);
    PGresult *res = PQexec(conn, query);

    if (PQresultStatus(res) != PGRES_TUPLES_OK) {
        const char *error_json = "{\"error\": \"Query failed\"}\n";
        dprintf(client_socket, "HTTP/1.1 500 Internal Server Error\r\n"
                               "Content-Type: application/json\r\n"
                               "Content-Length: %lu\r\n"
                               "\r\n"
                               "%s",
                               strlen(error_json), error_json);
        PQclear(res);
        PQfinish(conn);
        return;
    }

    // Dynamically allocate a buffer for JSON response
    size_t json_size = 512 + (PQntuples(res) * 256); // Estimate response size
    char *json_response = malloc(json_size);
    if (!json_response) {
        const char *error_json = "{\"error\": \"Memory allocation failed\"}\n";
        dprintf(client_socket, "HTTP/1.1 500 Internal Server Error\r\n"
                               "Content-Type: application/json\r\n"
                               "Content-Length: %lu\r\n"
                               "\r\n"
                               "%s",
                               strlen(error_json), error_json);
        PQclear(res);
        PQfinish(conn);
        return;
    }

    strcpy(json_response, "[");
    for (int i = 0; i < PQntuples(res); i++) {
        char row[256];
        snprintf(row, sizeof(row),
                 "{\"id\": \"%s\", \"description\": \"%s\", \"created_at\": \"%s\"}%s",
                 PQgetvalue(res, i, 0),
                 PQgetvalue(res, i, 1),
                 PQgetvalue(res, i, 2),
                 (i == PQntuples(res) - 1) ? "" : ",");
        strcat(json_response, row);
    }
    strcat(json_response, "]\n");

    // Allocate buffer for HTTP response
    size_t response_size = 512 + strlen(json_response);
    char *response = malloc(response_size);
    if (!response) {
        const char *error_json = "{\"error\": \"Memory allocation failed\"}\n";
        dprintf(client_socket, "HTTP/1.1 500 Internal Server Error\r\n"
                               "Content-Type: application/json\r\n"
                               "Content-Length: %lu\r\n"
                               "\r\n"
                               "%s",
                               strlen(error_json), error_json);
        free(json_response);
        PQclear(res);
        PQfinish(conn);
        return;
    }

    snprintf(response, response_size,
             "HTTP/1.1 200 OK\r\n"
             "Content-Type: application/json\r\n"
             "Content-Length: %lu\r\n"
             "\r\n"
             "%s",
             strlen(json_response), json_response);

    // Send the response and free memory
    write(client_socket, response, strlen(response));

    free(json_response);
    free(response);
    PQclear(res);
    PQfinish(conn);
}

// Handle HTTP requests
void handle_client(int client_socket) {
    char buffer[4096];
    int read_size = read(client_socket, buffer, sizeof(buffer) - 1);
    if (read_size <= 0) return;

    buffer[read_size] = '\0';
    printf("Received request:\n%s\n", buffer);

    // Extract HTTP method & path
    char method[8], path[64];
    sscanf(buffer, "%s %s", method, path);

    // Handle authentication request
    if (strcmp(method, "POST") == 0 && strcmp(path, "/api/auth") == 0) {
        handle_auth(client_socket);
    }
    // Handle get-thing request
    else if (strcmp(method, "POST") == 0 && strcmp(path, "/api/stuff/get-thing") == 0) {
        char *auth_header = strstr(buffer, "auth: ");
        char *thing_header = strstr(buffer, "thing: ");

        if (auth_header && thing_header) {
            char token[33], thing[64];
            sscanf(auth_header, "auth: \"%32[^\"]\"", token);
            sscanf(thing_header, "thing: \"%63[^\"]\"", thing);
            handle_get_thing(client_socket, token, thing);
        } else {
            const char *error_json = "{\"error\": \"Invalid request format\"}\n";
            write(client_socket, error_json, strlen(error_json));
        }
    }
    // Unknown request
    else {
        const char *error_json = "{\"error\": \"Resource not found\"}\n";
        write(client_socket, error_json, strlen(error_json));
    }

    close(client_socket);
}

// Start server
int main() {
    srand(time(NULL)); // Seed for token generation

    int server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket < 0) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    struct sockaddr_in server_addr = {0};
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    if (bind(server_socket, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("Bind failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    if (listen(server_socket, 10) < 0) {
        perror("Listen failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d\n", PORT);

    while (1) {
        int client_socket = accept(server_socket, NULL, NULL);
        if (client_socket < 0) {
            perror("Accept failed");
            continue;
        }
        handle_client(client_socket);
    }

    close(server_socket);
    return 0;
}
```

New result after trying the C file:\
![image](https://github.com/user-attachments/assets/8fc138f7-71ae-43bc-8eda-c4cfaab34f9c)\
*Sorry, image a bit zoomed out...*\
here is server side:
```Bash
user@slag:~$ ./api-server
Server listening on port 8080
Received request:
POST /api/auth HTTP/1.1
Host: localhost:8080
User-Agent: curl/8.5.0
Accept: */*


Received request:
POST /api/stuff/get-thing HTTP/1.1
Host: localhost:8080
User-Agent: curl/8.5.0
Accept: */*
Content-Length: 43
Content-Type: application/x-www-form-urlencoded

auth: "abc123def456ghi789" thing: "thing_2"
Received request:
POST /api/stuff/get-thing HTTP/1.1
Host: localhost:8080
User-Agent: curl/8.5.0
Accept: */*
Content-Length: 57
Content-Type: application/x-www-form-urlencoded

auth: "e5HhtO7SIOVn51rmwIX1ubYdKbLwfj8j" thing: "thing_2"
Received request:
POST /api/stuff/get-thing HTTP/1.1
Host: localhost:8080
User-Agent: curl/8.5.0
Accept: */*
Content-Length: 57
Content-Type: application/x-www-form-urlencoded

auth: "e5HhtO7SIOVn51rmwIX1ubYdKbLwfj8j" thing: "thing_2"
```
Client side:
```Bash
user@slag:~$ curl -X POST http://localhost:8080/api/auth
{"auth_token": "e5HhtO7SIOVn51rmwIX1ubYdKbLwfj8j"}
curl: (18) transfer closed with 1 bytes remaining to read
user@slag:~$ curl -X POST http://localhost:8080/api/stuff/get-thing -d 'auth: "abc123def456ghi789" thing: "thing_2"'
{"error": "Invalid token"}
user@slag:~$ curl -X POST http://localhost:8080/api/stuff/get-thing -d 'auth: "e5HhtO7SIOVn51rmwIX1ubYdKbLwfj8j" thing: "thing_2"'
[{"id": "2", "description": "thing_2", "created_at": "2025-01-31 18:29:23.148847"}]
user@slag:~$ curl -X POST http://localhost:8080/api/stuff/get-thing -d 'auth: "e5HhtO7SIOVn51rmwIX1ubYdKbLwfj8j" thing: "thing_2"'
[{"id": "2", "description": "thing_2", "created_at": "2025-01-31 18:29:23.148847"}]
```
![image](https://github.com/user-attachments/assets/6a5cba03-a4f4-4497-9fbc-5a0b64840686)

> NOTE: Auth auto expires on the service restarting!
>> This is because it stored them in the servers memory...lol

> TODO: **Require credentials to get an auth token** 

















