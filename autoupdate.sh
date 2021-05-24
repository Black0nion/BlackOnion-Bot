#!/bin/bash

echo Resetting the changes
git reset --hard
echo Done!
curl --location --request POST 'https://discord.com/api/webhooks/820986057658859530/Bc1vNkkh3KRb8l3EOuYI1S4F7bzBUSL9DjqzMGwk9KnDA0ZlK-t8mH7ZPMGUw3lVVoyK' \
		--header 'content-type: application/json' \
		--data '{
					"content": null,
					"embeds": [
						{
							"title": "Updating the GitHub Repo!",
							"description": "Successfully started the download process!",
							"color": 7405312,
							"footer": {
								"text": "https://github.com/black0nion/BlackOnion-Bot",
								"icon_url": "https://www.black-onion.com/res/img/logo/logo_showcase.png"
							}
						}
					]
				}'

echo Starting the download loop...
for (( i=0; i<10; ++i)); do
	echo Pulling the code.
	answer=$(git pull)
	echo Successfully pulled the code.
	if [[ $answer == *"Already up to date."* ]]; 
	then
		echo Already up to date. > /dev/stderr
		curl --location --request POST 'https://discord.com/api/webhooks/820986057658859530/Bc1vNkkh3KRb8l3EOuYI1S4F7bzBUSL9DjqzMGwk9KnDA0ZlK-t8mH7ZPMGUw3lVVoyK' \
		--header 'content-type: application/json' \
		--data '{
					"content": null,
					"embeds": [
						{
							"title": "Already up to date!",
							"description": "Why did this get called?",
							"color": 7405312,
							"footer": {
								"text": "https://github.com/black0nion/BlackOnion-Bot",
								"icon_url": "https://www.black-onion.com/res/img/logo/logo_showcase.png"
							}
						}
					]
				}'
		exit
	elif [[ $answer == *"changed"* ]];
	then
		echo Pulling the code finished!
		curl --location --request POST 'https://discord.com/api/webhooks/820986057658859530/Bc1vNkkh3KRb8l3EOuYI1S4F7bzBUSL9DjqzMGwk9KnDA0ZlK-t8mH7ZPMGUw3lVVoyK' \
		--header 'content-type: application/json' \
		--data '{
					"content": null,
					"embeds": [
						{
							"title": "Successfully pulled new code!",
							"description": "Successfully updated to a new version of the repo!",
							"color": 7405312,
							"footer": {
								"text": "https://github.com/black0nion/BlackOnion-Bot",
								"icon_url": "https://www.black-onion.com/res/img/logo/logo_showcase.png"
							}
						}
					]
				}'


		# WARNING: PSEUDOCODE!
		screen -sname BlackOnionBot -command shutdown
		screen -sname BlackOnionBot -command java -jar BlackOnion-Bot
		echo All done!
		# all done, exiting completely
		exit
	else
		echo Updating the code failed! > /dev/stderr
		curl --location --request POST 'https://discord.com/api/webhooks/820986057658859530/Bc1vNkkh3KRb8l3EOuYI1S4F7bzBUSL9DjqzMGwk9KnDA0ZlK-t8mH7ZPMGUw3lVVoyK' \
		--header 'content-type: application/json' \
		--data '{
					"content": null,
					"embeds": [
						{
							"title": "Pulling the Code from GitHub failed!",
							"description": "Another run failed! Error: '${answer}'",
							"color": 16711680,
							"footer": {
								"text": "https://github.com/black0nion/BlackOnion-Bot",
								"icon_url": "https://www.black-onion.com/res/img/logo/logo_showcase.png"
							}
						}
					]
				}'
	fi
	sleep 10
done
echo Updating the code fully failed! > /dev/stderr
curl --location --request POST 'https://discord.com/api/webhooks/820986057658859530/Bc1vNkkh3KRb8l3EOuYI1S4F7bzBUSL9DjqzMGwk9KnDA0ZlK-t8mH7ZPMGUw3lVVoyK' \
		--header 'content-type: application/json' \
		--data '{
					"content": null,
					"embeds": [
						{
							"title": "Pulling the Code from GitHub failed!",
							"description": "Could not update to the newest version of the repo!",
							"color": 16711680,
							"footer": {
								"text": "https://github.com/black0nion/BlackOnion-Bot",
								"icon_url": "https://www.black-onion.com/res/img/logo/logo_showcase.png"
							}
						}
					]
				}'