echo Adding SystemD service
sudo cp ./obamabot.service /etc/systemd/system
sudo systemctl daemon-reload
sudo systemctl enable obamabot.service
echo Enabled Obamabot service

echo Installing libraries
sudo apt install openjdk-11-jre jq curl bash maven

echo Adding cronjob
cronjob="*/5 * * * * bash '$(pwd)/cronscript.sh'"
(crontab -u root -l; echo "$cronjob" ) | crontab -u root -
echo Finished adding cronjob

./cronscript.sh
echo Run with "sudo service start obamabot"