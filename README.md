# Better-Freecam (Better FC)

Better-Freecam is a client-side Minecraft free-camera mod. It lets you move the camera away from your player and adjust camera flight speed with the mouse wheel while freecam is active. The mod settings are available through Mod Menu.

This repository contains the source supplied by HexoForge. It is based on [MinecraftFreecam/Freecam](https://github.com/MinecraftFreecam/Freecam); see [NOTICE.md](NOTICE.md) for attribution. The current source tree includes Fabric, Forge and NeoForge projects for several Minecraft versions. These projects have not all been verified as working Better FC releases. The initial target described by the maintainer is Fabric for Minecraft 1.21.11.

## Use

Install the build that matches your Minecraft version and mod loader, together with that loader's required dependencies. For the Fabric target, install Fabric API; Mod Menu provides the settings screen. The default freecam key in the underlying project is `F4`. While freecam is enabled, use the mouse wheel to adjust flight speed. Check your server's rules before using freecam in multiplayer.

No downloadable release is published by this repository yet. Build artifacts from this source should be treated as development builds until they are tested in game.

## Build

Install a Java version supported by the selected Minecraft target. This source uses the included Gradle wrapper:

```powershell
.\gradlew.bat help
.\gradlew.bat build
```

On Linux or macOS, use `./gradlew` instead. The multi-version build downloads Gradle plugins and Minecraft dependencies, so the first run can take time. Build output is written under the Gradle project build directories and is ignored by Git.

## Contribute

Bug reports and pull requests are welcome in this repository. See [CONTRIBUTING.md](CONTRIBUTING.md) before contributing. For private security reports, see [SECURITY.md](SECURITY.md).

## License

The project source is available under the [MIT License](LICENSE). The original Freecam copyright and license notice are retained. Dependencies and build tools may carry their own licenses.
