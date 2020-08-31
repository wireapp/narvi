# Narvi - Wire support bot

Bot used for backend/customer support team in Wire.
Its aim is to centralize issue tracking, by providing method how to open and comment
issues directly from the Wire.

## Behavior
* can create issue on some tracking system (currently only Github is supported)
* when it creates issue, it creates conversation in Wire with specific people
* every message in such conversation is then sent to the created issue in tracking system
* once issue is resolved, bot will close the issue on tracking system

## Known limitations
* bot is currently running in the user mode, because of Bot API limitations

## How To
Possible commands:
* **@template** - creates a new template for the issues
* **@create** - creates a new issue
* **@close** - closes issue associated with the conversation
* **@help** - displays this help

Commands parameters syntax:
```
@template <template name> <tracker> <repository>
@create <template> <issue name> with <mentions> <new line> <description>
```

Example of **@template**
```
@template backend github wireapp/test-repo
```
Example of **@create**
```
@create backend My Awesome Name with @JamesBond @MontyBern
Some Description of a very hard problem, we're solving.
```

## Required Env
```
WIRE_API_HOST
WIRE_WS_HOST
DB_DRIVER (default postgres)
DB_URL
DB_USER
DB_PASSWORD
EMAIL
PASSWORD
GITHUB_TOKEN
```
