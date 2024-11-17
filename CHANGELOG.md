## Changes
- Renamed the `creative_only` model loader type to `operator`.
- Added a new directory for specifying which items will trigger a re-render of `operator` models. `barricade/operator_items`. `required_item` fields starting with `#` will instead denote these instead of tags in operator models.
  - Please note that the above values must be items for a block item that uses an operator model.
- Removed culling from directional faces.
- Allowed the mod's rendering code to work even without Barricade on the server.

## Bugfixes
- Fixed operator models always being visible when Sodium is on the client.
- Fixed incorrect visibility refreshing of the `barricade:operator` loader type.
- [FABRIC] Fixed block display not rendering operator models.