# Barricade
Barricade is a mod that implements more barrier types meant for use within Mod Garden events. Although it is free for other mapmakers to use too.

This mod may also change barrier and light block rendering, to be friendlier to directional based barriers.

You can enable/disable the above by applying/removing the Barricade Rendering resource pack.

## Client-side Only
This mod is able to run on clients without having it on the server. When run like this, the mod will only apply the rendering functionalities to any block with the `barricade:operator` loader json.
If you wish to implement this model type for your own barriers in a resource pack, copy from the models found in the mod's source.

You may specify which items activate rendering by adding/modifying json found in `assets/<namespace>/barricade/operator_items`.

## The Barriers
This mod adds three barrier block types, Directional, Predicate, and Advanced Barrier blocks.

Directional barriers block movement from specific directions, whereas entity barriers block entities that meet the predicate specified by the block.

All items may be accessed through the /give command or through the Operator Utilities creative menu tab.

### Default Directional Barriers
- `barricade:down_barrier` - Blocks out entities from below.
- `barricade:up_barrier` - Blocks out entities from above.
- `barricade:east_barrier` - Blocks out entities from the east.
- `barricade:west_barrier` - Blocks out entities from the west.
- `barricade:north_barrier` - Blocks out entities from the north.
- `barricade:south_barrier` - Blocks out entities from the south.
- `barricade:horizontal_barrier` - Blocks out entities from all horizontal directions.
- `barricade:vertical_barrier` - Blocks out entities from all vertical directions.

### Default Predicate Barriers
- `barricade:hostile_barrier` - Blocks out mobs considered hostile, and neutral mobs that are more-so hostile.
- `barricade:mob_barrier` - Blocks out all non player entities.
- `barricade:passive_barrier` - Blocks out mobs considered passive, and neutral mobs that are more-so passive.
- `barricade:player_barrier` - Exclusively blocks out players.
- `barricade:creative_only_barrier` - Blocks out non-creative players and entities that can't be in creative.

### Advanced Barrier

The Advanced Barrier is a block entity based barrier that can be both an entity barrier and a directional barrier.

What an Advanced Barrier will do can be modified by specifying an Advanced Barrier JSON inside `/data/<namespace>/barricade/advanced_barrier/<path>.json`.

You may add this advanced barrier to your JSON with the id as the value of the `barricade:advanced_barrier` component.