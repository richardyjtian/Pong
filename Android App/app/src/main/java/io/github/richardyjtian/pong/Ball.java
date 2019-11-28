package io.github.richardyjtian.pong;

import android.graphics.RectF;

import java.util.Random;

public class Ball {

    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;

    // The screen length and width in pixels
    private int mScreenX;
    private int mScreenY;

    public Ball(int x, int y){
        mScreenX = x;
        mScreenY = y;

        // Make the mBall size relative to the screen resolution
        mBallWidth = x / 80;
        mBallHeight = mBallWidth;

    /*
        Start the ball travelling straight up
        at a quarter of the screen height per second
    */
        mYVelocity = y / 4;
        mXVelocity = mYVelocity;

        // Initialize the Rect that represents the mBall
        mRect = new RectF();

    }

    // Give access to the Rect
    public RectF getRect(){
        return mRect;
    }

    // Give access to the Ball height
    public float getmBallHeight() { return mBallHeight; }

    // Change the position each frame
    public void update(long fps){
        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top - mBallHeight;
    }

    // Reverse the vertical heading
    public void reverseYVelocity(){
        mYVelocity = -mYVelocity;
    }

    // Reverse the horizontal heading
    public void reverseXVelocity(){
        mXVelocity = -mXVelocity;
    }

    public void setRandomXVelocity(){

        // Generate a random number either 0 or 1
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void increaseVelocity(){
        mXVelocity += mXVelocity / 100;
        mYVelocity += mYVelocity / 100;
    }

    public void clearObstacleY(float y){
        mRect.bottom = y;
        mRect.top = y - mBallHeight;
    }

    public void clearObstacleX(float x){
        mRect.left = x;
        mRect.right = x + mBallWidth;
    }

    public void reset(RectF bat){
        mRect.left = mScreenX / 2;
        mRect.top = bat.top - 1 - mBallHeight;
        mRect.right = mScreenX / 2 + mBallWidth;
        mRect.bottom = bat.top - 1;
    }
}
