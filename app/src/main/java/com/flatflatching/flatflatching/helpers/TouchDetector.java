package com.flatflatching.flatflatching.helpers;

public class TouchDetector {

    private static final int MIN_DISTANCE = 50;
    
    public enum SwipeDirection  { leftSwipe , rightSwipe, noSwipe } 
    
    /**Constructor is empty since we dont need any static information.
     * 
     */
    public TouchDetector() {
        /**Maybe we'll need some logic if it is improved.
         * 
         */
    }
    

    /** This method checks if the user did a swipe on the screen and if so,
     *  determines the direction of the swipe.
     * @param coordinateTouch X-coordinate of the touchdown
     * @param coordinateLeave X-coordinate of the liftoff
     * @return SwipeDirection the swipedirection
     */
    public final SwipeDirection getHorizontalSwipeDirection(final float coordinateTouch, final float coordinateLeave) {
        SwipeDirection result = SwipeDirection.noSwipe;
        if (Math.abs(coordinateTouch - coordinateLeave) > MIN_DISTANCE) {
            if (coordinateLeave > coordinateTouch) {
                result = SwipeDirection.leftSwipe;
            } else {
                result = SwipeDirection.rightSwipe;
            } 
        }
        return result;
    }
    
    public final boolean isDownSwipe(final float coordinateTouch, final float coordinateLeave) {
        return coordinateLeave - coordinateTouch > MIN_DISTANCE ;
    }
}
