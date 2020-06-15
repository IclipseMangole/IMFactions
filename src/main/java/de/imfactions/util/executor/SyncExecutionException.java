package de.imfactions.util.executor;

public class SyncExecutionException extends Exception {
    private final String info;

    public SyncExecutionException(String info) {
        this.info = info;
    }

    public void printInfo() {
        //TODO
        System.out.println("[Factions] Reporting... " + info + " Stack Trace is next");
        printStackTrace();
        System.out.println("[Factions] Finished reporting");
    }
}
