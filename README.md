# ObamaBot

A poorly concieved bot self described as "funny"


## Docker setup (recommended):
### Requires:
* Docker
* Docker compose (optionally)

### Docker Compose

```bash
git clone https://github.com/antaxiom/ObamaBot.git
```
Then create a file in the current directory called `config.json` with the following contents:
```json
{
  "token": "DISCORD_BOT_TOKEN_HERE"
}
```

and finally run 
```bash
docker-compose -p obamabot up -d
```


### Docker image
To install with Docker, run the following:

Create a file in the current directory called `config.json` with the following contents:
```json
{
  "token": "DISCORD_BOT_TOKEN_HERE"
}
```

```shell script
touch config.json
echo "{}" >> config.json
docker run \
-d -it \
--env token=DISCORD_TOKEN_HERE \
-v "$(pwd)/config.json:/config.json" \
--name eminembot \
antaxiom/eminembot
```

Use Watchtower for automatic updates

## SystemD setup:
### Requires:
* systemd

### Setup
Install using `install.sh` (Works for Ubuntu, not tested on other Debian based distros)
