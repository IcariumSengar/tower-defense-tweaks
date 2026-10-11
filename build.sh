#!/usr/bin/env bash
# Builds build/tdtweaks-<version>.jar with plain javac - no Gradle.
#
# There is no refmap: every mixin uses remap = false and names Minecraft members by their SRG
# names, which is what Forge 1.20.1 runs with. So the build compiles against the SRG-named client
# jar, the Forge 47.4.10 libraries, and the exact jars of the mods the mixins target (deps.txt,
# downloaded once into deps/ and checked by SHA-1).
#
#   MC_CLIENT_SRG  client-1.20.1-20230612.114412-srg.jar (any installed Forge 1.20.1 client has it)
#   FORGE_LIBS     the libraries/ folder of an installed Forge 1.20.1-47.4.10 server
#   JAVA_HOME      a JDK 17
# Set them in the environment or in local.env beside this script (gitignored, sourced if present).
#
# Usage: ./build.sh   (Git Bash on Windows, or any POSIX shell)
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=/dev/null
[ -f "$ROOT/local.env" ] && . "$ROOT/local.env"
: "${MC_CLIENT_SRG:?set MC_CLIENT_SRG (see the top of build.sh)}"
: "${FORGE_LIBS:?set FORGE_LIBS (see the top of build.sh)}"
: "${JAVA_HOME:?set JAVA_HOME to a JDK 17}"
VERSION=$(sed -n 's/^version = "\(.*\)"/\1/p' "$ROOT/src/main/resources/META-INF/mods.toml")
OUT="$ROOT/build/tdtweaks-$VERSION.jar"

# Windows javac wants Windows paths and ';' between classpath entries.
if command -v cygpath >/dev/null; then
  native() { cygpath -w "$1"; }
  SEP=';'
else
  native() { printf '%s' "$1"; }
  SEP=':'
fi

echo "Fetching target mod jars..."
mkdir -p "$ROOT/deps"
while read -r name sha url; do
  [ -z "$name" ] || [ "${name:0:1}" = "#" ] && continue
  jar="$ROOT/deps/$name"
  [ -f "$jar" ] || curl -sfL -o "$jar" "$url"
  actual=$(sha1sum "$jar" | cut -d' ' -f1)
  if [ "$actual" != "$sha" ]; then
    echo "  error: $name has SHA-1 $actual, expected $sha" >&2
    exit 1
  fi
done < "$ROOT/deps.txt"

# The server's own Minecraft jars are left out: the client SRG jar has the same classes plus the
# client-only ones the Mob Dismemberment mixin needs.
cp="$(native "$MC_CLIENT_SRG")"
while IFS= read -r jar; do
  cp="$cp$SEP$(native "$jar")"
done < <(find "$FORGE_LIBS" -name '*.jar' ! -path '*/net/minecraft/server/*' ! -name 'forge-*-server.jar' | sort; find "$ROOT/deps" -name '*.jar' | sort)

echo "Compiling..."
rm -rf "$ROOT/build"
mkdir -p "$ROOT/build/classes"
{
  echo "--release 17"
  echo "-encoding UTF-8"
  echo "-Xlint:-options"
  echo "-proc:none"
  echo "-cp \"${cp//\\/\\\\}\""
  echo "-d \"$(native "$ROOT/build/classes" | sed 's/\\/\\\\/g')\""
  find "$ROOT/src/main/java" -name '*.java' | while IFS= read -r src; do
    echo "\"$(native "$src" | sed 's/\\/\\\\/g')\""
  done
} > "$ROOT/build/javac.args"
"$JAVA_HOME/bin/javac" "@$(native "$ROOT/build/javac.args")"

echo "Packaging..."
cat > "$ROOT/build/MANIFEST.MF" <<EOF
Manifest-Version: 1.0
Implementation-Title: tdtweaks
Implementation-Version: $VERSION
MixinConfigs: tdtweaks.mixins.json
EOF
"$JAVA_HOME/bin/jar" --create --file "$(native "$OUT")" --manifest "$(native "$ROOT/build/MANIFEST.MF")" \
  -C "$(native "$ROOT/build/classes")" . -C "$(native "$ROOT/src/main/resources")" . -C "$(native "$ROOT")" LICENSE
echo "Done: build/$(basename "$OUT")"
