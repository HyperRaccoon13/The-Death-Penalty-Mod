# The Death Penalty
Fabric 1.18.2

A simple, configurable Fabric mod that lets modpack authors control what happens to players on respawn after death.

### Match fields:
- when - DamageSource names (inFire, onFire, lava, player, fall, modded IDs).
- where - Dimension registry IDs (minecraft:overworld, minecraft:the_nether, minecraft:the_end).
- from - Attacker entity type IDs (minecraft:skeleton, mymod:boss).
- using - either the direct source entity id (minecraft:arrow, minecraft:trident, mod:plasma_bolt) or the projectile’s item id (minecraft:arrow, minecraft:splash_potion).

A rule matches if all provided filters match (empty arrays mean “any”).
By default, the first matching rule is applied; if none match, defaultPenalties are applied.

### Penalties:

``{"type":"set_food","value":int(0..20),"saturationValue":float}``

``{"type":"set_health","valueF":float} (absolute health; 20.0 = full)``

``{"type":"add_effect","id":"minecraft:weakness","amplifier":0,"durationTicks":600,"ambient":false,"showParticles":true,"showIcon":true}``

``{"type":"xp_percent","valueF":0.25} (remove 25% of total XP)``

Example:

```json
{
  "global": {
    "enabled": true,
    "onlyWithKeepInventory": true
  },
  "rules": [
    {
      "name": "Overworld fire",
      "when": ["inFire","onFire"],
      "where": ["minecraft:overworld"],
      "from": [],
      "using": [],
      "penalties": [
        { "type": "set_food", "value": 1, "saturationValue": 0 }
      ]
    }
  ],
  "defaultPenalties": []
}

```

### Commands:
Running: ``/deathpenalty reload`` or ``/dpreload`` will reload the json so you dont have to restart the game or reopen the world

### Other:
Adding: ``"dev: true"`` to ``"global"`` will enable in chat logging
```json
  "global": {
    "enabled": true,
    "onlyWithKeepInventory": true,
    "dev": true
  },
```
