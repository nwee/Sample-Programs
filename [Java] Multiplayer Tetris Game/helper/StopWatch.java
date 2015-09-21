package helper;

public class StopWatch {
	private long timeLimitInNanos = 0;
	
    // Constructor
    public StopWatch() {
    }
    
    public StopWatch(int timeLimitInSeconds) {
    	this.timeLimitInNanos = (long) timeLimitInSeconds * (long) 1E9;
    }

    // Public API
    public void start() {
        if (!_isRunning) {
            _startTime = System.nanoTime();
            _isRunning = true;
        }
    }

    public void stop() {
        if (_isRunning) {
            _elapsedTime += System.nanoTime() - _startTime;
            _isRunning = false;
        }
    }

    public void reset() {
        _elapsedTime = 0;
        if (_isRunning) {
            _startTime = System.nanoTime();
        }
    }

    public boolean isRunning() {
        return _isRunning;
    }

    public long getElapsedTimeNanos() {
        if (_isRunning) {
            return _elapsedTime + System.nanoTime() - _startTime;
        }
        return _elapsedTime;
    }

    public long getElapsedTimeMillis() {
        return getElapsedTimeNanos() / (long) 1E6;
    }
    
    private long getTimeLimitInMillis() {
    	return timeLimitInNanos / (long) 1E6;
    }
    
    public long getRemainingMillis() {
    	return Math.max(0, getTimeLimitInMillis() - getElapsedTimeMillis()); 
    }
    
    /**
     * Prints the time remaining if the stop watch is constructed with a time limit.
     * Otherwise prints the time elapsed since the start.
     */
    public String toString() {
    	long millis;
    	if (timeLimitInNanos > 0)
    		millis = getRemainingMillis();
    	else
    		millis = getElapsedTimeMillis();
    	
    	long minutes = (long) (millis / 60E3);
    	millis %= 60E3;
    	long seconds = (long) (millis / 1E3);
    	millis %= 1E3;
    	
    	return String.valueOf(minutes) + "m " + String.valueOf(seconds) + "." + millis/100 + "s";
    }

    // Private Members
    private boolean _isRunning = false;
    private long _startTime = 0;
    private long _elapsedTime = 0;
}
