## Changes
- Merge 1.3 codebase with 2.0
- Added Creative-Only Barrier.
- Replace the LootContextAware system with [Silicate](https://modrinth.com/mod/silicate).
- Non creative players may now reach through predicated barriers that do not apply to them.
- Renamed /assets/barricade/textures/item/barricade/entity/ directory to /assets/barricade/textures/item/barricade/icon/.
- Removed `barricade:blocked_entities` component in favour of `barricade:advanced_barriers` component.
- Added `barricade:advanced_barrier` dynamic/data pack registry. Which is now used to attach default directions, names, icons, and predicates to advanced barriers.
- Changed the modified Advanced Barrier directional state to a block state.

## Bugfixes
- Fixed `inGround` projectile behavior with directional and predicated barriers.
- Fixed culling with directional barrier and advanced barrier faces.
- Fixed NeoForge seams fix hook not running on advanced barriers.
- Fixed placement contexts being inconsistent with the barrier at hand.
- Fixed barriers not turning invisible under certain circumstances when quick swapping items.
- Fixed larger entities bugging on the corners of horizontal barriers.