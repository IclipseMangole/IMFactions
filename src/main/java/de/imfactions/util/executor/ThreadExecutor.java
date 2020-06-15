package de.imfactions.util.executor;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ThreadExecutor {

    private static IExecutor executor;

    public static boolean reportMainThread(String info) {
        if (getExecutor().isAsyncThread()) {
            return true;
        } else {
            try {
                throw new SyncExecutionException(info);
            } catch (SyncExecutionException e) {
                e.printInfo();
            }
            return false;
        }
    }

    /**
     * Führt run() Synchron aus. Weder in Bukkits Mainthread noch in Bungees IO-Threads sollten
     * blockende Operationen ausgeführt werden. Der Callback wird standartmäßig Asynchron ausgeführt.
     * Kein nutzen bei BungeeCord, da dieser komplett Threadsafe ist.
     */
    public static Callback execute(Runnable runnable) {
        return execute(runnable, true);
    }

    /**
     * Führt run() Synchron aus. Weder in Bukkits Mainthread noch in Bungees IO-Threads sollten
     * blockende Operationen ausgeführt werden. Der Callback Thread kann selbst festgelegt werden.
     * Kein nutzen bei BungeeCord, da dieser komplett Threadsafe ist.
     */
    public static Callback execute(Runnable runnable, boolean asyncCallback) {
        if (executor != null) {
            Callback callback = new Callback(asyncCallback);
            executor.execute(runnable, callback);
            return callback;
        }
        return null;
    }

    /**
     * Führt run() Asynchron aus. Weder in Bukkits Mainthread noch in Bungees IO-Threads sollten
     * blockende Operationen ausgeführt werden. Der Callback wird standartmäßig im Mainthread ausgeführt (Bukkit).
     */
    public static Callback executeAsync(Runnable runnable) {
        return executeAsync(runnable, false);
    }

    /**
     * Führt run() Asynchron aus. Weder in Bukkits Mainthread noch in Bungees IO-Threads sollten
     * blockende Operationen ausgeführt werden. Der Callback Thread kann selbst festgelegt werden (Bukkit).
     */
    public static Callback executeAsync(Runnable runnable, boolean asyncCallback) {
        if (executor != null) {
            Callback callback = new Callback(asyncCallback);
            executor.executeAsync(runnable, callback);
            return callback;
        }
        return null;
    }

    /**
     * Führt run() mehrmals synchron aus.
     *
     * @param count    wie oft run ausgeführt werden soll
     * @param interval Zeit zwischen den Ausführungen - Bukkit: Zeit in Ticks, Bungee: {@code null} nicht erlaubt
     * @deprecated use {@link ThreadExecutor#repeat(Consumer, int, long)}
     */
    @Deprecated
    public static void repeat(Runnable runnable, int count, long interval) {
        repeat(runnable, count, interval, null);
    }

    /**
     * Führt run() mehrmals synchron aus.
     * <p>
     * Um kleinere Zeiteinheiten zu wählen, bitte {@link ThreadExecutor#repeat(Consumer, int, long, TimeUnit)} benutzen
     *
     * @param count    wie oft run ausgeführt werden soll
     * @param interval Platformspezifische Zeit zwischen den Ausführungen
     */
    public static Callback repeat(Consumer<Integer> consumer, int count, long interval) {
        return repeat(consumer, count, interval, null);
    }

    /**
     * Führt run() mehrmals synchron aus.
     * <p>
     * Achtung: Bei manchen Systemen wird die Zeit auf die kleinstmögliche Einheit gerundet (Beispiel Bukkit -> Minimum 1 Tick = 50ms)
     *
     * @param count    wie oft run ausgeführt werden soll
     * @param interval Zeit zwischen den Ausführungen - Bukkit: Nur {@code null} erlaubt, Bungee: {@code null} nicht erlaubt
     * @param timeUnit Zeiteinheit des Intervalls (Nur bei BungeeCord benutzbar)
     * @deprecated use {@link ThreadExecutor#repeat(Consumer, int, long, TimeUnit)}
     */
    @Deprecated
    public static void repeat(Runnable runnable, int count, long interval, TimeUnit timeUnit) {
        repeat(i -> runnable.run(), count, interval, timeUnit);
    }

    /**
     * Führt run() mehrmals synchron aus.
     * <p>
     * Achtung: Bei manchen Systemen wird die Zeit auf die kleinstmögliche Einheit gerundet (Beispiel Bukkit -> Minimum 1 Tick = 50ms)
     *
     * @param count    wie oft run ausgeführt werden soll
     * @param interval Intervall zwischen den Ausführungen
     * @param timeUnit Zeiteinheit des Intervalls
     */
    public static Callback repeat(Consumer<Integer> consumer, int count, long interval, TimeUnit timeUnit) {
        if (executor != null) {
            return executor.repeat(consumer, count, interval, timeUnit, new Callback(false));
        }
        return null;
    }

    /**
     * Führt run() mehrmals asynchron aus.
     *
     * @param count    wie oft run ausgeführt werden soll
     * @param interval Ticks zwischen den Ausführungen - Bukkit: Zeit in Ticks, Bungee: {@code null} nicht erlaubt
     * @deprecated use {@link ThreadExecutor#repeatAsync(Consumer, int, long)}
     */
    @Deprecated
    public static void repeatAsync(Runnable runnable, int count, long interval) {
        repeatAsync(i -> runnable.run(), count, interval, null);
    }

    /**
     * Führt run() mehrmals asynchron aus.
     * <p>
     * Um kleinere Zeiteinheiten zu wählen, bitte {@link ThreadExecutor#repeatAsync(Consumer, int, long, TimeUnit)} benutzen
     *
     * @param count    wie oft run ausgeführt werden soll
     * @param interval Platformspezifische Zeit zwischen den Ausführungen
     */
    public static Callback repeatAsync(Consumer<Integer> consumer, int count, long interval) {
        return repeatAsync(consumer, count, interval, null);
    }

    /**
     * Führt run() mehrmals asynchron aus.
     * <p>
     * Achtung: Bei manchen Systemen wird die Zeit auf die kleinstmögliche Einheit gerundet (Beispiel Bukkit -> Minimum 1 Tick = 50ms)
     *
     * @param times    wie oft run ausgeführt werden soll
     * @param interval Intervall zwischen den Ausführungen
     * @param timeUnit Zeiteinheit des Intervalls
     * @deprecated use {@link ThreadExecutor#repeatAsync(Consumer, int, long, TimeUnit)}
     */
    @Deprecated
    public static void repeatAsync(Runnable runnable, int times, long interval, TimeUnit timeUnit) {
        repeat(i -> runnable.run(), times, interval, timeUnit);
    }

    /**
     * Führt run() mehrmals asynchron aus.
     * <p>
     * Achtung: Bei manchen Systemen wird die Zeit auf die kleinstmögliche Einheit gerundet (Beispiel Bukkit -> Minimum 1 Tick = 50ms)
     *
     * @param times    wie oft run ausgeführt werden soll
     * @param interval Intervall zwischen den Ausführungen
     * @param timeUnit Zeiteinheit des Intervalls
     */
    public static Callback repeatAsync(Consumer<Integer> consumer, int times, long interval, TimeUnit timeUnit) {
        if (executor != null) {
            return executor.repeatAsync(consumer, times, interval, timeUnit, new Callback(true));
        }
        return null;
    }

    /**
     * Führt run() verzögert aus.
     * <p>
     * Achtung: Bei manchen Systemen wird die Zeit auf die kleinstmögliche Einheit gerundet (Beispiel Bukkit -> Minimum 1 Tick = 50ms)
     *
     * @param delay    Verzögerung, bis run() ausgeführt wird
     * @param timeUnit Zeiteinheit des Intervalls
     */
    public static Callback executeLater(Runnable runnable, int delay, TimeUnit timeUnit) {
        if (executor != null) {
            return executor.executeLater(runnable, delay, timeUnit, new Callback(false));
        }
        return null;
    }

    /**
     * Führt run() verzögert asynchron aus.
     * <p>
     * Um kleinere Zeiteinheiten zu wählen, bitte {@link ThreadExecutor#repeatAsync(Consumer, int, long, TimeUnit)} benutzen
     *
     * @param delay Verzögerung, bis run() ausgeführt wird
     */
    public static Callback executeLater(Runnable runnable, int delay) {
        return executeLater(runnable, delay, null);
    }

    /**
     * Führt run() verzögert asynchron aus.
     * <p>
     * Achtung: Bei manchen Systemen wird die Zeit auf die kleinstmögliche Einheit gerundet (Beispiel Bukkit -> Minimum 1 Tick = 50ms)
     *
     * @param delay    Verzögerung, bis run() ausgeführt wird
     * @param timeUnit Zeiteinheit des Intervalls
     */
    public static Callback executeLaterAsync(Runnable runnable, int delay, TimeUnit timeUnit) {
        if (executor != null) {
            return executor.executeLater(runnable, delay, timeUnit, new Callback(true));
        }
        return null;
    }

    /**
     * Führt run() verzögert asynchron aus.
     * <p>
     * Um kleinere Zeiteinheiten zu wählen, bitte {@link ThreadExecutor#repeatAsync(Consumer, int, long, TimeUnit)} benutzen
     *
     * @param delay Verzögerung, bis run() ausgeführt wird
     */
    public static Callback executeLaterAsync(Runnable runnable, int delay) {
        return executeLaterAsync(runnable, delay, null);
    }


    /**
     * Führt run() Synchron aus. Weder in Bukkits Mainthread noch in Bungees IO-Threads sollten
     * blockende Operationen ausgeführt werden. Der Callback wird standartmäßig Asynchron ausgeführt.
     * Kein nutzen bei BungeeCord, da dieser komplett Threadsafe ist.
     */
    public static Callback executeResult(ResultRunnable runnable) {
        return executeResult(runnable, true);
    }

    /**
     * Führt run() Synchron aus. Weder in Bukkits Mainthread noch in Bungees IO-Threads sollten
     * blockende Operationen ausgeführt werden. Der Callback Thread kann selbst festgelegt werden.
     * Kein nutzen bei BungeeCord, da dieser komplett Threadsafe ist.
     */
    public static Callback executeResult(ResultRunnable runnable, boolean asyncCallback) {
        if (executor != null) {
            Callback callback = new Callback(asyncCallback);
            executor.executeResult(runnable, callback);
            return callback;
        }
        return null;
    }

    /**
     * Führt run() Asynchron aus. Weder in Bukkits Mainthread noch in Bungees IO-Threads sollten
     * blockende Operationen ausgeführt werden. Der Callback wird standartmäßig im Mainthread ausgeführt (Bukkit).
     */
    public static Callback executeResultAsync(ResultRunnable runnable) {
        return executeResultAsync(runnable, false);
    }

    /**
     * Führt run() Asynchron aus. Weder in Bukkits Mainthread noch in Bungees IO-Threads sollten
     * blockende Operationen ausgeführt werden. Der Callback Thread kann selbst festgelegt werden (Bukkit).
     */
    public static Callback executeResultAsync(ResultRunnable runnable, boolean asyncCallback) {
        if (executor != null) {
            Callback callback = new Callback(asyncCallback);
            executor.executeResultAsync(runnable, callback);
            return callback;
        }
        return null;
    }

    public static IExecutor getExecutor() {
        return executor;
    }

    public static void setExecutor(IExecutor executor) {
        ThreadExecutor.executor = executor;
    }
}