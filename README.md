# Better-Freecam

A Minecraft freecam mod based on [Freecam](https://github.com/MinecraftFreecam/Freecam). Press `F4` to move the camera away from your player. While freecam is active, use the mouse wheel to change flight speed. Settings are available through Mod Menu.

The current Better FC target is Fabric for Minecraft 1.21.11. The source also contains builds for other Minecraft versions and loaders, but those have not been tested for Better FC yet.

## Build

Use the included Gradle wrapper. On Windows:

```powershell
.\gradlew.bat :fabric:1.21.11:build
```

On Linux or macOS:

```sh
./gradlew :fabric:1.21.11:build
```

Install Fabric API alongside the mod. Mod Menu is optional. Check your server's rules before using freecam in multiplayer.

There is no release download yet. Please test a build in game before distributing it.

## Contributing

Bug reports and pull requests are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md) and [SECURITY.md](SECURITY.md).

## License

[MIT](LICENSE). The original Freecam copyright notice is retained; see [NOTICE.md](NOTICE.md).
