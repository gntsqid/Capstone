#!/bin/bash

database="capstone"
table="machines"
user=""
password=""


echo -e "\nACCESSING DATABASE: CAPSTONE"

MENU()
{
    echo "What would you like to do?"
    echo "1. Create a user"
    echo "2. Create a table"
    echo "3. Add data"
    echo "4. Restore"
    echo "5. Query"
    echo "6. Exit"
    echo ""
    read CHOICE

    # PLACEHOLDER DATA
    case $CHOICE in
            1) echo "you chose 1" ;;
            2) echo "you chose 2" ;;
            3) echo "you chose 3" ;;
            4) echo "you chose 4" ;;
            5) echo "you chose 5" ;;
            6) EXIT ;;
            *) MENU "Please enter a valid option." ;;
    esac
}
EXIT() {
  echo -e "Goodbye\n"
}

#echo -e "DISPLAYING TABLE: PEOPLE\n"

query="select * from $table;"
access="mariadb -u $user --password=$password $database --execute "
#$access "select * from machines;"

#####################
##############
#######
MENU
