FROM python:3.11-slim

WORKDIR /app
COPY app/ /app
COPY .env /app/.env

RUN pip install --no-cache-dir -r requirements.txt

CMD ["gunicorn", "-w", "4", "-k", "uvicorn.workers.UvicornWorker", "main:app", "--bind", "0.0.0.0:8000"]

