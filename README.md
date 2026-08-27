# Sable: Simulated Items

Makes dropped items physics simulated through Sable!

### How does it work?
Sable: Simulated Items creates a new block that handles the rendering and collision
of the physics item. This block is the sole block inside of a sub level that gets
created for every dropped item. Every item entity only exists as collision checks
for blocks, events, and other entities (such as hoppers, or damage), the block 
handles all the rendering and the sub level handles all of the movement.

### Completion
Sable: Simulated Items is currently incomplete, there's lots of polish that needs
to be done before a full release. See the following for a todo list:
- [Code TODOs](https://github.com/search?q=repo%3ADogeisCut%2FSableSimulatedItems+%22TODO%3A%22&type=code)
- [TODO.md](https://github.com/DogeisCut/SableSimulatedItems/blob/master/TODO.md)

Pull requests and code suggestions would be greatly appreciated.
