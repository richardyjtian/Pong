# Things to note (Board Side)

* I was unable to find all dependencies to make a separate project file, so to download the project onto the board, 
  you'll have to replace the freertos_demo.c file in its original location, and add my tiDefs.h file in the same folder.
  
* RX pin from bluetooth goes into PC5

* TX pin from bluetooth goes into PD6

* Any data recieved through the bluetooth should not crash the board. However unexpected characters or bad format will 
  result in weird things (i.e. the bottom bat or ball jumping from one side of the screen to the other)
  
* The board moves its bat 1 pixel for every 10 ms a button is held.

* If both buttons are pressed at the same time, the bat does not move (could change if needed)

* Virtual buttons will be developed soon (I was thinking of just making half the screen the "right" button and the 
  other half the "left" button. If the andriod app buttons are implemented differently, let me know)
  
* Data sent from the board is in "bat.xCoord!" format, other README specificed "bat.xCoord, bat.xCoord!", wondering 
  if this was a typo?

* X coordinate sent from board is always 4 characters long (i.e. 001! 100! 054!)

* On first download onto the board, if it gets stuck in a faultloop, you may have to redownload it twice (pretty sure I'm missing a delay 
  somewhere, just not sure where)
