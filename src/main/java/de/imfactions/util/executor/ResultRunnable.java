package de.imfactions.util.executor;

/**
 * Verfügt über einen boolean Wert success, der während der run-Methode auf true gesetzt werden muss,
 * damit der ThreadExecutor den Callback ausführt.
 */
public class ResultRunnable implements Runnable {
    private boolean success = false;

    @Override
    public void run() {

    }

    protected void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean hadSuccess() {
        return success;
    }
}
