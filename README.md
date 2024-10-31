**Minecraft Bukkit/SpigotMC Plugin**

# ReturnToSpawn
Players can return to the world spawn or their home point with commands.

## Commands and Permissions
### For Players
- `/spawn` : Return to the Spawn Point of the world.
  - Required Permission: `returntospawn.spawn`
  - Default: `true` _(Any Player)_
- `/sethome` : Set Player's Home Point in the world.
  - Required Permission: `returntospawn.sethome`
  - Default: `true` _(Any Player)_
- `/home` : Return to Player's Home Point of the world.
  - Required Permission: `returntospawn.home`
  - Default: `true` _(Any Player)_

### For Operators
- `/rts addworld spawn`: Add world(s) to the list for `/spawn` command.
  - Required Permission: `returntospawn.addworld.spawn`
  - Default: `op`
- `/rts addworld home`: Add world(s) to the list for `/sethome` and `/home` command.
  - Required Permission: `returntospawn.addworld.home`
  - Default: `op`
- `/rts removeworld spawn`: Remove world(s) from the list for `/spawn` command.
  - Required Permission: `returntospawn.removeworld.spawn`
  - Default: `op`
- `/rts removeworld home`: Remove world(s) from the list for `/sethome` and `/home` command.
  - Required Permission: `returntospawn.removeworld.home`
  - Default: `op`
