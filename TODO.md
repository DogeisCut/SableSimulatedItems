- [ ] Fix Sable collision particles for `SubLevelItemBlock`.
  - Currently, `SubLevelItemBlock` uses `TerrainParticleSpriteMixin` to make its
    breaking/block break particles dynamic, as to match its item stack. This mixin, relies
    on a `BlockPos` being provided to the `updateSprite` method, but Sable sub level
    collisions don't provide one for that function, and supposedly don't even call `updateSprite`
    for ground collisions. Right now, items produce missing texture particles when colliding,
    but otherwise look fine for vanilla Minecraft interactions.
- [ ] Find better solution for applying sub level velocity from item entity in `ItemSubLevelEntityWatcher`.
  - Current logic uses an instance var `pendingVelocity`, problem is, this value is not saved
    and is prone to leakage when saving and quitting as the variable is never cleared. You might
    think this isn't a problem, however with a tick perfect drop (dropping an item and leaving on
    the same tick, easier to test with `/tick freeze`), the item's spawned sub level never gets
    its velocity.
- [ ] Continue rendering items as they are being picked up in `ItemEntityRendererMixin`.
  - Items normally have a little flying pickup animation. This animation is hidden by our
    mixin. It's possible this mixin in its entirety is an awful solution for hiding items for
    `SubLevelItemBlock` to handle, but it was all I could think of.
  - I genuinely have no idea where this is handled. It's not in `ItemEntity` OR `ItemEntityRenderer`. All I know is that blindly canceling the entire `render` method causes it to stop working.
- [ ] Disable source entity/player collision for item sub levels when they are first thrown/dropped/spawned.
  - Currently, `SubLevelEntityCollisionMixin` doesn't do this as player movement is
    handled entirely on the client. The sub level's collision would need to be disabled
    until its no longer touching the player, or at least on a short timer.
- [ ] Prevent items from colliding with sub levels entirely.
  - As tempting as it is to disable physics on `ItemEntity` entierly, this removes interactions
    with some blocks, such as `create:chute`s. This was already experimented with via
    `ItemEntityPhysicsMixin`. Instead, sub levels should disable their collision with the `ItemEntity`
    they control, and only that one. `SubLevelEntityCollisionMixin` currently disables collisions with
    ALL entities (except players, see previous entries) if that sub level owns an item. This is incorrect
    behavior.
is not updated to the new item stack. Leading to the stack to visually look the same.
- [ ] Add config.
  - Having mod config options would be nice. Currently, the only thing I can think of for config is
disabling/enabling dropped item stacking/merging.

## Future
- [ ] `BlockSubLevelDynamicCollider` for `SubLevelItemBlock`
  - Actual collision shapes based on the item model itself. Can create custom collision shapes
in the data driven config too.

## Not Possible
- Refine `SubLevelItemBlockEntity` collision logic (Default Behavior)
  - `SubLevelItemBlockEntity` updates the state of its `SubLevelItemBlock` to modify
    its collision (for sub levels and block outline). Currently, it does this automatically
    based on if its item stack inherits `BlockItem` or not. Ideally, we'd check the baked model
    and use `isGui3d` instead as the current logic produces bad results (such as with `minecraft:redstone`).
  - Not really possible since isGui3d is a client thing exclusively.
  - Should really figure out a better solution for this because `should_use_item_shape_as_block_item.json` is huge, and it can't cover every mod item.

## Completed
- [X] ~~Split up `ItemSubLevelEntityWatcher` into several classes.~~
- [X] ~~Tag `simulated_items:sub_level_item` with a custom physics config.~~
- [X] ~~Update `SubLevelItemBlockEntity` when items merge.~~
- [X] ~~Data driven `SubLevelItemBlockEntity` collision enum.~~