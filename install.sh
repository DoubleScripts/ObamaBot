#!/bin/bash

echo Adding SystemD service
sudo cp ./obamabot.service /etc/systemd/system
sudo systemctl daemon-reload
sudo systemctl enable obamabot.service
echo Enabled Obamabot service

echo Installing libraries
sudo apt install openjdk-11-jre-headless jq curl bash maven screen

echo Adding cronjob
cronjob="*/5 * * * * bash '$(pwd)/cronscript.sh'"

# Cron requires a root user to restart the service.
(sudo crontab -u root -l; echo "$cronjob" ) | sudo crontab -u root -
echo Finished adding cronjob

bash ./cronscript.sh
echo Run with "sudo service obamabot start"