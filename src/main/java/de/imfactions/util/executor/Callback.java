package de.imfactions.util.executor;

public class Callback {

    private Runnable run;
    private boolean asyncCallback;
    //  private boolean returntrue;

    protected Callback(boolean asyncCallback) {
        this.asyncCallback = asyncCallback;
    }

    public void onDone(Runnable run) {
        this.run = run;
    }

    /*
    public void returnTrue() {
        this.returntrue = true;
    }

    public boolean isReturntrue() {
        return returntrue;
    }
    */

    public Runnable getCallback() {
        return run;
    }

    public void done() {
        if (run != null) {
            if (asyncCallback == ThreadExecutor.getExecutor().isAsyncThread()) {
                run.run();
            } else {
                if (asyncCallback) {
                    ThreadExecutor.executeAsync(run);
                } else {
                    ThreadExecutor.execute(run);
                }
            }
        }
    }

    public boolean isAsyncCallback() {
        return asyncCallback;
    }

    public void setAsyncCallback(boolean asyncCallback) {
        this.asyncCallback = asyncCallback;
    }
}