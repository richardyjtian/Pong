package io.github.richardyjtian.pong;

import android.graphics.RectF;

public class Bat {

    // RectF is an object that holds four coordinates - just what we need
    private RectF mRect;

    // How long and high our mBat will be
    private float mLength;
    private float mHeight;

    // X is the far left of the rectangle which forms our mBat
    private float mXCoord;

    // Y is the top coordinate
    private float mYCoord;

    private float mYPadding;

    // This will hold the pixels per second speed that
    // the mBat will move
    private float mBatSpeed;

    // Which ways can the mBat move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the mBat moving and in which direction
    private int mBatMoving = STOPPED;

    // The screen length and width in pixels
    private int mScreenX;
    private int mScreenY;

    // This is the constructor method
    // When we create an object from this class we will pass
    // in the screen width and mHeight
    public Bat(int x, int y, boolean bottom){

        mScreenX = x;
        mScreenY = y;

        // mHeight is relative to mScreenX
        mLength = mScreenX / 8;

        // mHeight is relative to mScreenY
        mHeight = mScreenY / 40;

        // Start mBat in roughly the screen centre
        mXCoord = mScreenX / 2;

        // YPadding is space between bat and the walls
        mYPadding = 18;
        if(bottom) {
            mYCoord = mScreenY - mYPadding - mHeight;
            mRect = new RectF(mXCoord, mYCoord, mXCoord + mLength, mYCoord + mHeight);
        }
        else {
            mYCoord = mYPadding;
            mRect = new RectF(mXCoord, mYCoord, mXCoord + mLength, mYCoord + mHeight);
        }

        // How fast is the mBat in pixels per second
        mBatSpeed = mScreenX;
        // Cover entire screen in 1 second
    }

    // This is a getter method to make the rectangle that
    // defines our bat available in PongView class
    public RectF getRect(){
        return mRect;
    }

    // This method will be used to change/set if the mBat is going
    // left, right or nowhere (Use with LEFT, RIGHT, or STOPPED)
    public void setMovementState(int state){
        mBatMoving = state;
    }

    // This update method will be called from update in PongView
    // It determines if the Bat needs to move and changes the coordinates
    // contained in mRect if necessary
    public void update(long fps){

        if(mBatMoving == LEFT){
            mXCoord = mXCoord - mBatSpeed / fps;
        }

        if(mBatMoving == RIGHT){
            mXCoord = mXCoord + mBatSpeed / fps;
        }

        // Make sure it's not leaving screen
        if(mRect.left < 0){ mXCoord = 0; } if(mRect.right > mScreenX){
            mXCoord = mScreenX -
                    // The width of the Bat
                    (mRect.right - mRect.left);
        }

        // Update the Bat graphics
        mRect.left = mXCoord;
        mRect.right = mXCoord + mLength;
    }
}
