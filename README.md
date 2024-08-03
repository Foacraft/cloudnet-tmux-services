# cloudnet-tmux-services
Make the CloudNet Service running the Tmux environment.

> Require CloudNet v4 and above than not support v3.

## Module Configuration
```json
{
  "factoryName": "tmux-jvm",
  "stopTimeout": 20,
  "messages": {
    "service-stop-timeout": "CloudService [uniqueId=%service_uniqueid% task=%service_task% name=%service_name%] will be killed cause timeout right now."
  }
}
```

## Usage
Edit your task config, for example:
```json
{
  ...
  "name": "lobby",
  "runtime": "tmux-jvm",
  "hostAddress": null,
  "javaCommand": null,
  "nameSplitter": "-",
  ...
}
```
You can see the runtime is `tmux-jvm` via config 
that same of node `factoryName` in config `config.json`.

Then you can control and manage services for CloudNet on tmux,
use shortcut keys of `Ctrl+B` and `D` to toggle between different screen of services.

## Unsupported features
* service <*> screen