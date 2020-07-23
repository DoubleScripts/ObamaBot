# ServerStartupScripts
This is a repo that contains some useful scripts for Minecraft servers and is fairly customizable

From [Fern's ServerStartupScripts](https://github.com/Fernthedev/ServerStartupScripts)

There are two files: 
- ServerStart.sh (the script for starting the server)
- settings.sh (the variables or settings that can be customized)

Requirements to use:
- jq (for parsing json) (`sudo apt install jq`)
- Java
- Netcat (for checking if server is running.) (`sudo apt install netcat`)
- bash

Features:
- Automatic updating by using REST API
- Checks if the server is already running on a defined port. If it is, it will shutdown.
- Checks if EULA is false at startup greatly reducing the time to wait until you realzie EULA is false. This can be turned off in settings.
