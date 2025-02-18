from fastapi import FastAPI
import pymysql
from typing import List, Dict

app = FastAPI()

# Database connection
def get_db():
    return pymysql.connect(
        host="127.0.0.1",
        user="",
        password="",
        database="capstone",
        cursorclass=pymysql.cursors.DictCursor
    )

# API Endpoint: Get all online machines
@app.get("/machines/online", response_model=List[Dict])
def get_online_machines():
    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT id, name FROM machines WHERE parking_space_available = TRUE;") # change this to fit the need
    machines = cursor.fetchall()
    db.close()
    return machines
