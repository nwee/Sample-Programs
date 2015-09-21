COMP 3411 Artificial Intelligence, Session 1, 2013.

Adventure Game
---------------

This project is the implementation of an agent to play a simple text-based adventure game. 

Rules
-----

The agent is required to move around a rectangular environment, collecting tools and avoiding (or removing) obstacles along the way. The obstacles and tools within the environment are represented as follows:

Obstacles  Tools
T 	tree      	a 	axe
-	door	k	key
*	wall	d	dynamite
~	water	g	gold
The agent will be represented by one of the characters ^, v, <  or  >, depending on which direction it is pointing. The agent is capable of the following instructions:

L   turn left
R   turn right
F   (try to) move forward
C   (try to) chop down a tree, using an axe
O   (try to) open a door, using a key
B   (try to) blast a wall, door or tree, using dynamite

When it executes an L or R instruction, the agent remains in the same location and only its direction changes. When it executes an F instruction, the agent attempts to move a single step in whichever direction it is pointing. The F instruction will fail (have no effect) if there is a wall, tree or door directly in front of the agent; if the agent moves forward into the water, it will fall in and drown.

When the agent moves to a location occupied by a tool, it automatically picks up the tool. The agent may use a C, O or B instruction to remove an obstacle immediately in front of it, if it is carrying the appropriate tool. A tree may be removed with a C (chop) instruction, if an axe is held. A door may be removed with an O (open) instruction, if a key is held. A wall, tree or door may be removed with a B (blast) instruction, if dynamite is held.

To win the game, the agent must pick up the gold and then return to its initial location.

~~~~~~~~~~~~~~~~~~~~~
~~~~~~~~~~~~~~~~~~~~~
~~     T     T   k ~~
~~   ***     ***   ~~
~~*-*     v     *-*~~
~~  **         **  ~~
~~ g **   d   ** a ~~
~~    TT     TT    ~~
~~~~~~~~~~~~~~~~~~~~~
~~~~~~~~~~~~~~~~~~~~~

At each time step, the environment will send a series of 24 characters to the agent, constituting a scan of the 5-by-5 window it is currently seeing; the agent must send back a single character to indicate the action it has chosen.

The environment is no larger than 80 by 80, and that it is totally surrounded by water, so there is no confusion about where the environment begins and ends.