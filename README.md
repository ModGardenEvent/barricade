# Barricade
Barricade is a mod that implements more barrier types meant for use within Mod Garden events. Although it is free for other mapmakers to use too.

This mod also changes barrier and light block rendering, to be friendlier to directional based barriers.

## The Barriers
This mod adds three barrier block types, Directional, Entity, and Advanced Barrier blocks.
Directional barriers block movement from specific directions, whereas entity barriers block specific types of entities.

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

### Default Entity Barriers
- `barricade:hostile_barrier` - Blocks out mobs considered hostile, and neutral mobs that are more-so hostile.
- `barricade:mob_barrier` - Blocks out all non player entities.
- `barricade:passive_barrier` - Blocks out mobs considered passive, and neutral mobs that are more-so passive.
- `barricade:player_barrier` - Exclusively blocks out players.

### Advanced Barrier
The Advanced Barrier is a block entity based barrier that can be both an entity barrier and a directional barrier.
What an Advanced Barrier will do can be modified through the `barricade:blocked_directions` and `barricade:blocked_entities` components on the associated item.

#### Example /give commands.
`/give @s barricade:advanced_barrier[barricade:blocked_directions=[south,north]]` - Blocks south and north.

`/give @s barricade:advanced_barrier[barricade:blocked_directions={west,up}]` - Blocks west and up.

`/give @s barricade:advanced_barrier[barricade:blocked_directions={west,north,south}]` - Blocks all horizontal directions besides east.

`/give @s barricade:advanced_barrier[barricade:blocked_entities={entities:["#minecraft:skeletons"]}]` - Blocks all skeletons.

`/give @s barricade:advanced_barrier[barricade:blocked_entities={entities:["#minecraft:zombies","minecraft:player"],inverted:true}]` - Blocks everything but zombies and players.

`/give @s barricade:advanced_barrier[barricade:blocked_entities={backing_texture_location:"barricade:item/barricade/entity/player",entities:["minecraft:player"]},barricade:blocked_directions={west,north,south,east}]` - Blocks only players from all horizontal directions.