# TESTING
This document showcases a few of the tests for my personal notes as well as to act as a secondary developer log.

---
## DATABASE
The main goal to begin my testing phase is the ability to expose my postgres database to an API natively.\
I do not want to use any of the solutions already available, but instead wish to learn API programming.

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

