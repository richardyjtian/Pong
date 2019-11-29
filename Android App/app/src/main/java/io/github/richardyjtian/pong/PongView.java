package io.github.richardyjtian.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

class PongView extends SurfaceView implements Runnable {
    MainActivity.SendReceive mSendReceive;

    // This is our thread
    Thread mGameThread = null;

    // We need a SurfaceHolder object
    // We will see it in action in the draw method soon.
    SurfaceHolder mOurHolder;

    // A boolean which we will set and unset, when the game is running or not
    // It is volatile because it is accessed from inside and outside the thread
    volatile boolean mPlaying;

    // Game is mPaused at the start
    boolean mPaused = true;

    // A Canvas and a Paint object
    Canvas mCanvas;
    Paint mPaint;

    // This variable tracks the game frame rate
    long mFPS;

    // The size of the screen (on the phone) we want to play on in pixels
    int mScreenX;
    int mScreenY;

    // The size of the actual phone screen in pixels (used to draw black in unplayable area)
    int mPhoneScreenX;
    int mPhoneScreenY;

    // The phone player's mBat
    Bat mBottomBat;
    // The TM4 player's mBat
    Bat mTopBat;

    // A mBall
    Ball mBall;

    // For sound FX
    SoundPool sp;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;

    // The phone player's mScore
    int mBottomScore = 0;
    // The TM4 player's mBat
    int mTopScore = 0;

    public PongView(Context context, int x, int y, int PhoneX, int PhoneY) {

        // Ask the SurfaceView class to set up our object
        super(context);

        // Set the screen width and height
        mScreenX = x;
        mScreenY = y;

        // Set the actual phone screen width and height
        mPhoneScreenX = PhoneX;
        mPhoneScreenY = PhoneY;

        // Initialize mOurHolder and mPaint objects
        mOurHolder = getHolder();
        mPaint = new Paint();

        // Bottom mBat
        mBottomBat = new Bat(mScreenX, mScreenY, true);
        // Top mBat
        mTopBat = new Bat(mScreenX, mScreenY, false);

        // Create a mBall
        mBall = new Ball(mScreenX, mScreenY);

        // Instantiate our sound pool (dependent upon version of Android)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            sp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("beep1.ogg");
            beep1ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.ogg");
            beep2ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep3.ogg");
            beep3ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("loseLife.ogg");
            loseLifeID = sp.load(descriptor, 0);

        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
        setupAndRestart();
    }

    public void setupAndRestart(){
        // Put the mBall back to the start
        mBall.reset(mBottomBat.getRect());

        // Reset scores and mLives
        mBottomScore = 0;
        mTopScore = 0;
    }

    @Override
    public void run() {
        while (mPlaying) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if(!mPaused){
                update();
            }

            // Draw the frame
            draw();

            // Calculate the FPS this frame
            // Result is used to time animations in the update methods
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                mFPS = 1000 / timeThisFrame;
            }
        }
    }

    // Everything that needs to be updated goes in here
    // Movement, collision detection etc.
    public void update() {
        // Move the mBats if required
        mBottomBat.update(mFPS);
        mTopBat.update(mFPS);

        mBall.update(mFPS);

        // Check for mBall colliding with mBottomBat
        if(RectF.intersects(mBottomBat.getRect(), mBall.getRect())) {
            mBall.setRandomXVelocity();
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mBottomBat.getRect().top - 2);

            mBall.increaseVelocity();

            sp.play(beep1ID, 1, 1, 0, 0, 1);
        }

        // Check for mBall colliding with mTopBat
        if(RectF.intersects(mTopBat.getRect(), mBall.getRect())) {
            mBall.setRandomXVelocity();
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mTopBat.getRect().bottom + 2);

            mBall.increaseVelocity();

            sp.play(beep2ID, 1, 1, 0, 0, 1);
        }

        // Reset the mBall when it hits the bottom of screen
        if(mBall.getRect().bottom > mScreenY){
            // TM4 player gets a point
            mTopScore++;
            // Put the mBall back to the start
            mBall.reset(mBottomBat.getRect());
        }

        // Reset the mBall when it hits the top of screen
        if(mBall.getRect().top < 0){
            // Phone player gets a point
            mBottomScore++;
            // Put the mBall back to the start
            mBall.reset(mBottomBat.getRect());
        }

        // If the mBall hits left wall bounce
        if(mBall.getRect().left < 0){
            mBall.reverseXVelocity();
            mBall.clearObstacleX(2);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }

        // If the mBall hits right wall bounce
        if(mBall.getRect().right > mScreenX){
            mBall.reverseXVelocity();
            mBall.clearObstacleX(mScreenX - 22);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }

        // Send data to the TM4
        mSendReceive.write("hehe".getBytes());
    }

    // Draw the newly updated scene
    public void draw() {
        // Make sure our drawing surface is valid or we crash
        if (mOurHolder.getSurface().isValid()) {
            // Draw everything here

            // Lock the mCanvas ready to draw
            mCanvas = mOurHolder.lockCanvas();

            // Clear the screen with my favorite color
            mCanvas.drawColor(Color.argb(255, 120, 197, 87));

            // Set unreachable area as black (float left, float top, float right, float bottom)
            mPaint.setColor(Color.argb(255, 0, 0, 0));
            mCanvas.drawRect(mScreenX, 0, mPhoneScreenX, mPhoneScreenY, mPaint);

            // Choose the brush color for drawing
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mBats
            mCanvas.drawRect(mBottomBat.getRect(), mPaint);
            mCanvas.drawRect(mTopBat.getRect(), mPaint);

            // Draw the mBall
            mCanvas.drawRect(mBall.getRect(), mPaint);

            // Change the drawing color to white
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mScores
            mPaint.setTextSize(60);
            mCanvas.drawText("Player Score: " + mBottomScore + ", Opponent Score: " + mTopScore, 10, mScreenY/2, mPaint);

            // Draw everything to the screen
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    // If the Activity is paused/stopped, shutdown our thread.
    public void pause() {
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    // If the Activity starts/restarts, start our thread.
    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // Cannot play if not connected through bluetooth
        if(mSendReceive != null) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    mPaused = false;
                    float x = motionEvent.getX();
                    // Is the touch on the right or left?
                    if (x > mScreenX / 2 && x < mScreenX) {
                        mBottomBat.setMovementState(mBottomBat.RIGHT);
                    } else if (x <= mScreenX / 2) {
                        mBottomBat.setMovementState(mBottomBat.LEFT);
                    }
                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:
                    mBottomBat.setMovementState(mBottomBat.STOPPED);
                    break;
            }
            return true;
        }
        else{
            return false;
        }
    }
}
