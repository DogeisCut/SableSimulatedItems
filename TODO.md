- [ ] Fix Sable collision particles for `SubLevelItemBlock`.
  - Currently, `SubLevelItemBlock` uses `TerrainParticleSpriteMixin` to make its
breaking/block break particles dynamic, as to match its item stack. This mixin, relies
on a `BlockPos` being provided to the `updateSprite` method, but Sable sub level
collisions don't provide one for that function, and supposedly don't even call `updateSprite`
for ground collisions. Right now, items produce missing texture particles when colliding,
but otherwise look fine for vanilla Minecraft interactions.
- [ ] Tag `simulated_items:sub_level_item` with a custom physics config.
  - `SubLevelItemBlock`s currently use the default block physics properties.
Follow https://github.com/ryanhcode/sable/wiki/Block-Physics-Properties and
create something that works well for this block. Likely a mass and volume reduction.
- [ ] Refine `SubLevelItemBlockEntity` collision logic.
  - `SubLevelItemBlockEntity` updates the state of its `SubLevelItemBlock` to modify
its collision (for sub levels and block outline). Currently, it does this automatically
based on if its item stack inherits `BlockItem` or not. Ideally, we'd check the baked model
and use `isGui3d` instead as the current logic produces bad results (such as with `minecraft:redstone`).
We'd also create the ability to use data to define what items use what collision to override default
behavior, as something like `create:wrench` would benefit from using the `ITEM` shape or even a new one.
- [ ] Split up `ItemSubLevelEntityWatcher` into several classes.
  - This class just subscribes to too many events within the same file. It's just
kind of ugly and bothersome is all. 
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