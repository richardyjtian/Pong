# EE474Lab5

## Credits to Game Code School for the datastructures and algorithms used in a one-player Pong game

## Specifications:

### Bluetooth communication:
* All data will be sent and received scaled to the size of the TM4 LCD (x:320 by y:240)
* The Android app will act as a server and send data in the format of: 
* * #### Ball.xcoord, Ball.ycoord; BottomBat.xcoord, TopBat.xcoord!
* * ie) 48, 23; 12, 42!
* The TM4 will act as a client and send data in the format of:
* * #### LeftTouch (0) or RightTouch (1)
* * ie) 0
* * ie) 1


### Static sizes:
* Scaled to the size of the TM4 LCD (x:320 by y:240)
* Bat size:
* * #### Height: 6 pixels, Width: 40 pixels
* * #### TopBat YCoord: 3
* * #### BottomBat YCoord: 231, TopBat YCoord: 3
* Ball size:
* * #### Height: 6 pixels, Width: 6 pixels

### Other Specs:
* #### See picture