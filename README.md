# Tower Defense Tweaks

A small Forge 1.20.1 mod for the Tower Defense modpack. It changes a few other mods at runtime
with mixins, so the pack can ship their stock files instead of edited copies of their jars.

| Mod | Change |
|---|---|
| Immersive Engineering | Gun and Chemthrower Turrets fire without ammo: an empty Gun Turret fires a free Casull Cartridge that leaves no casing, and an empty Chemthrower Turret sprays free creosote. Ammo or fluid a player loads is used first, as usual. Power and the redstone signal are still required. |
| Immersive Engineering | The Tesla Coil never targets or shocks players, SecurityCraft Sentries or armor stands, and makes no idle arcs when nothing is in range. |
| Realistic Airdrop | The plane flies at a third of its speed and lives long enough to reach its drop point. A landed crate never deletes itself. |
| Mob Dismemberment [Unofficial Modern Port] | An error while making gibs is logged once instead of crashing the client. |

Each change applies only when its mod is installed. The Mob Dismemberment one is client-only.

Tested against Immersive Engineering 1.20.1-10.2.0-183, Realistic Airdrop 1.1.0-1.20.1-beta and
Mob Dismemberment 1.20.1-8.0.1 (`deps.txt`). Every injection is required, so if one of those mods
changes the code a mixin targets, the game fails at load with a mixin error naming it, instead of
silently losing the change.

## Building

`./build.sh` (Git Bash on Windows, or any POSIX shell) writes `build/tdtweaks-<version>.jar`. It
uses plain `javac`, with no Gradle. Every mixin is `remap = false` and names Minecraft members by
their SRG names, which is what Forge 1.20.1 runs with, so no refmap is needed. The build compiles
against:

- `MC_CLIENT_SRG`: the SRG-named client jar from an installed Forge 1.20.1 client
- `FORGE_LIBS`: the `libraries/` folder of an installed Forge 1.20.1-47.4.10 server
- the target mods' jars, downloaded into `deps/` and checked against the SHA-1s in `deps.txt`

Set `JAVA_HOME` to a JDK 17. All three paths can go in a `local.env` file beside `build.sh`
(gitignored), which the script sources if present.

## License

MIT. Immersive Engineering is by BluSunrize and Damien A.W. Hazard, Realistic Airdrop by Ian,
and the Mob Dismemberment port by ThatSoulyGuy (from iChun's original). This mod contains none of
their code. It only patches their classes while the game runs.
