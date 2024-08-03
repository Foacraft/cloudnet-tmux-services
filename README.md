# cloudnet-tmux-services
> It's just a jvm provider and experimental now.

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
You can see the runtime is `tmux-jvm` via config.

## unsupported features
* service <*> screen