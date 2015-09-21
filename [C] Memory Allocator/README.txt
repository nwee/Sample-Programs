COMP1927 Computing 2: Assignment 1

Suballocator
---------------------

This assignment, focused on development of a suballocator, which at initialisation requests a large
amount of memory from the operating system using C’s malloc, and then provides memory allocation
functions (like alloc and free) that subdivide and partition this memory for use in user programs.

This is achieved using a circular doubly linked list as well as algorithims to subdivide and merge 
free regions of memory.