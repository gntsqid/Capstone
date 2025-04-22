from fastapi import FastAPI, Request, HTTPException, Depends, status
from fastapi.security import HTTPBasic, HTTPBasicCredentials
import mysql.connector
import os
from dotenv import load_dotenv
from typing import Annotated, Optional
from pydantic import BaseModel

app = FastAPI()

# Load environment variables
dotenv_path = "/app/.env"
if os.path.exists(dotenv_path):
    load_dotenv(dotenv_path)

API_SECRET = os.getenv("")
DB_OWNER_USERNAME = os.getenv("")
DB_OWNER_PASSWORD = os.getenv("")

security = HTTPBasic()

# ------------------
# Models
# ------------------

class MachineCreate(BaseModel):
    hostname: str
    online: int = 0
    parking_lot: str = "unknown"
    parking_space: str = "unknown"
    type: str = "unknown"
    parking_space_available: int = 0

class MachineUpdate(BaseModel):
    online: Optional[int] = None
    parking_lot: Optional[str] = None
    parking_space: Optional[str] = None
    type: Optional[str] = None
    parking_space_available: Optional[int] = None

# ------------------
# Dependencies
# ------------------

def require_api_key(request: Request):
    token = request.headers.get("X-API-Key")
    if token != API_SECRET:
        raise HTTPException(status_code=403, detail="Forbidden: Invalid API Key")

def get_db(user=None, password=None):
    return mysql.connector.connect(
        host=os.getenv(""),
        user=user or os.getenv(""),
        password=password or os.getenv(""),
        database=os.getenv("")
    )

def require_shogun_auth(credentials: Annotated[HTTPBasicCredentials, Depends(security)]):
    if credentials.username != DB_OWNER_USERNAME or credentials.password != DB_OWNER_PASSWORD:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid admin credentials")

# ------------------
# Routes
# ------------------

@app.get("/<table_neme>", dependencies=[Depends(require_api_key)])
def get_<table_neme>():
    db = get_db()
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT * FROM <table_neme>")
    <table_neme> = cursor.fetchall()
    db.close()
    return <table_neme>

@app.post("/<table_neme>", dependencies=[Depends(require_shogun_auth)])
def add_machine(machine: MachineCreate):
    db = get_db(user=DB_OWNER_USERNAME, password=DB_OWNER_PASSWORD)
    cursor = db.cursor()
    query = """
        INSERT INTO <table_neme> (hostname, online, parking_lot, parking_space, type, parking_space_available)
        VALUES (%s, %s, %s, %s, %s, %s)
    """
    values = (
        machine.hostname,
        machine.online,
        machine.parking_lot,
        machine.parking_space,
        machine.type,
        machine.parking_space_available
    )
    cursor.execute(query, values)
    db.commit()
    db.close()
    return {"message": "Machine added successfully"}

@app.patch("/<table_neme>/{hostname}", dependencies=[Depends(require_shogun_auth)])
def update_machine(hostname: str, update: MachineUpdate):
    db = get_db(user=DB_OWNER_USERNAME, password=DB_OWNER_PASSWORD)
    cursor = db.cursor()

    fields = []
    values = []

    if update.online is not None:
        fields.append("online = %s")
        values.append(update.online)
    if update.parking_lot is not None:
        fields.append("parking_lot = %s")
        values.append(update.parking_lot)
    if update.parking_space is not None:
        fields.append("parking_space = %s")
        values.append(update.parking_space)
    if update.type is not None:
        fields.append("type = %s")
        values.append(update.type)
    if update.parking_space_available is not None:
        fields.append("parking_space_available = %s")
        values.append(update.parking_space_available)

    if not fields:
        db.close()
        raise HTTPException(status_code=400, detail="No fields to update.")

    values.append(hostname)

    query = f"UPDATE <table_neme> SET {', '.join(fields)} WHERE hostname = %s"
    cursor.execute(query, tuple(values))
    db.commit()

    if cursor.rowcount == 0:
        db.close()
        raise HTTPException(status_code=404, detail=f"No machine found with hostname '{hostname}'")

    db.close()
    return {"message": f"Machine '{hostname}' updated successfully."}

