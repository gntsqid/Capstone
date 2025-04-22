# API

## CURL COMMANDS

### GET

```BASH
curl -H "X-API-Key: $API_SECRET" \
     http://localhost:8000/<table_name>
```

## POST
Adds a new machine:
```Bash
curl -X POST http://localhost:8000/<table_name> \
  -H "Authorization: Basic $<db_owner>_API" \
  -H "Content-Type: application/json" \
  -d '{
    "hostname": "",
    "online": 1,
    "parking_lot": "",
    "parking_space": "",
    "type": "",
    "parking_space_available": 1
  }'
 ```

## PATCH
Updates existing machine:
```Bash
curl -X PATCH https://api.capstone.sqid.ink/<table_name>/<hostname> \
            -H "Authorization: Basic $<db_owner>_API" \
            -H "Content-Type: application/json" \
            -d '{
                    "parking_space_available": 1
                }'
```
