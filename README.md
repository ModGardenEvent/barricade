# Barricade
Barricade is a mod that implements more barrier types meant for use within Mod Garden events. Although it is free for other mapmakers to use too.

## The Barriers
This mod adds two barrier types, Directional and Entity Type barriers.
Directional barriers block movement from specific directions, whereas entity type barriers block specific types of entities.

All items may be accessed through the /give command or the Operator Utilities creative menu tab.

### Default Directional Barriers
- `barricade:down_barrier` - Blocks out entities from below.
- `barricade:up_barrier` - Blocks out entities from above.
- `barricade:east_barrier` - Blocks out entities from the east.
- `barricade:west_barrier` - Blocks out entities from the west.
- `barricade:north_barrier` - Blocks out entities from the north.
- `barricade:south_barrier` - Blocks out entities from the south.
- `barricade:horizontal_barrier` - Blocks out entities from all horizontal directions.
- `barricade:vertical_barrier` - Blocks out entities from all vertical directions.

### Default Entity Type Barriers
- `barricade:hostile_barrier` - Blocks out mobs considered hostile, and neutral mobs that are more-so hostile.
- `barricade:mob_barrier` - Blocks out all non player entities.
- `barricade:passive_barrier` - Blocks out mobs considered passive, and neutral mobs that are more-so passive.
- `barricade:player_barrier` - Exclusively blocks out players.

You may access and modify either type of barrier by changing the components of one of the pre-existing barriers of said type.

### Example /give commands.
`/give @s barricade:directional_barrier[barricade:blocked_directions={south:true,north:true}] - Blocks south and north.
`
`/give @s barricade:directional_barrier[barricade:blocked_directions={west:true,up:true}]` - Blocks west and up.

`/give @s barricade:directional_barrier[barricade:blocked_directions={west:true,north:true,south:true}]` - Blocks all horizontal directions besides east.

`/give @s barricade:entity_barrier[barricade:blocked_entities={entities:["#minecraft:skeletons"]}]` - Blocks all skeletons.

`/give @s barricade:entity_barrier[barricade:blocked_entities={entities:["#minecraft:zombies"],inverted:true}]` - Blocks everything but zombies.

`/give @s barricade:entity_barrier[barricade:blocked_entities={backing_texture_location:"barricade:item/barricade/entity/player",entities:["minecraft:player"]},barricade:blocked_directions={west:true,north:true,south:true,east:true}]` - Blocks only players from all horizontal directions.