## Changes
- Changed the advanced barrier icon format to specify where the file is in relation to the /assets/<namespace>/barricade/icon/ folder.
- Added `barricade:grass_block`, `barricade:horse`, `barricade:iron_sword`, `barricade:standing_steve` icons.
- Renamed `barricade:player` icon to `barricade:steve`.
- Adjusted placement conditions of each barrier item.
  - Directional: Can be placed if an entity is inside it.
  - Entity: Can be placed if none of the entities within it meet the condition.
  - Advanced: Same as entity.
- Non creative players may now reach through barrier blocks that they don't collide with.

## Bugfixes
- Fixed culling with directional barrier and advanced barrier faces.
- Fixed NeoForge seams fix hook not running on advanced barriers.
- Fixed placement contexts being inconsistent with the barrier at hand.
- Fixed barriers not turning invisible under certain circumstances when quick swapping items.
- Fixed larger entities bugging on the corners of horizontal barriers.