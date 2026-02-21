# The Death Penalty
Fabric 1.18.2

## Overview

A simple, configurable Fabric mod that lets modpack authors control what happens to players on respawn after death.


# Config Location

`config/death_penalty.json`


# Global Options

enabled (boolean) - Enables or disables the mod entirely.

onlyWithKeepInventory (boolean) - If true, penalties only apply when the
keepInventory gamerule is enabled.

dev (boolean) - Enables debug logging in console and chat. - Recommended
while building a modpack.

matchMode (string) - FIRST (default) -> only highest priority rule
applies - ALL -> every matching rule applies



# Rule Structure

Each rule contains:

name (string), priority (number, higher runs first), when (array of
strings), where (array of strings), from (array of strings), using (array
of strings), penalties (array of penalty objects)

Any field can be omitted or empty to match everything.

# Matching Fields

### when

Matches the cause category

Example: mob, lava, fall, explosion,
fire, magic, projectile

### where

Matches the dimension ID.

Example: minecraft:overworld,
minecraft:the_nether, minecraft:\*

### from

Matches the attacker entity type.

Example: minecraft:zombie,
minecraft:\*\_dragon, #minecraft:skeletons

Supports: Exact IDs, Wildcards (\*), Entity tags (#namespace:tag)

### using

Matches: Attacker main-hand item, Projectile item, Damage source entity type

Supports: Exact IDs, Wildcards (\*), Item tags (#namespace:tag)


# Wildcard Matching

The '\*' character works as a glob wildcard.

Examples:

minecraft:\*_sword\
mymod:*_boss


# Tag Matching

Use the format:

#namespace:tagname

Examples:

#c:arrows\
#minecraft:skeletons

Tag matching works for:  Entity type tags (from) and Item tags (using)

# Penalty Types

### set_health

Sets player health.

Fields:, value (float)

### set_food

Sets hunger and saturation.

Fields: value (int), saturationValue (float)

### xp_percent

Keeps a percentage of total XP.

Fields: valueF (float between 0 and 1)

## effect

Applies a status effect.

Fields: effect (status effect ID), duration (ticks), amplifier
(int), ambient (boolean), showParticles (boolean), showIcon (boolean)


# Example Rule
```json
    {
      "name": "All zombie melee deaths hurt",
      "priority": 50,
      "from": ["minecraft:zombie"],
      "using": ["minecraft:*_sword"],
      "penalties": [
        { "type": "set_food", "value": 4, "saturationValue": 0.0 }
      ]
    }
```


# Debugging

Enable:\
"dev": true

You will see: Cause, Attacker, Held item, Source entity, Flags, UsingItem