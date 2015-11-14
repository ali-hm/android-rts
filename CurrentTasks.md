# Current Tasks to undertake #

## World Scaling ##
The game is run on both tablets with large screens and phones with small screens.

We should determine how to best handle the display on the game world in both cases. For example: keep the world dimensions may remain the same but then we would have to scale the camera so the entire world (or some % thereof) would be visible on smaller screens.

For example: The world is 3000x3000.
On a small phone screen, we could initially zoom out so either the entire world is visible on the screen or at least 90% of it is on the screen. On a tablet, we wouldn't need to zoom out as much.

This task involves deciding on how best to scale and determining the right amount to scale for different devices.


## New Enemy Ships ##
Creating new types of enemy ships that will attack in waves.


## Distortion effects ##
Have the map of the world distort when an explosion occurs.

We may be able to do this by:
1: Having the background be a mesh with multiple points then are moved around when explosions occure below them
2: have texture mappings with more points that distort in different ways

An example: http://www.youtube.com/watch?v=eVi6ThY3LRs


## The main menu ##
Create a menu system that lets the user either:
a: start a new game (selecting from a list of levels)
b: continue their existing game