package de.imfactions.util.executor;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class IExecutor {

    public abstract Callback execute(Runnable run, Callback callback);

    public abstract Callback executeAsync(Runnable runnable, Callback callback);

    public abstract Callback repeat(Consumer<Integer> consumer, int times, long interval, TimeUnit timeUnit, Callback callback);

    public abstract Callback repeatAsync(Consumer<Integer> consumer, int times, long interval, TimeUnit timeUnit, Callback callback);

    public abstract Callback executeLater(Runnable run, long delay, TimeUnit timeUnit, Callback callback);

    public abstract Callback executeLaterAsync(Runnable run, long delay, TimeUnit timeUnit, Callback callback);

    public abstract Callback executeResult(ResultRunnable run, Callback callback);

    public abstract Callback executeResultAsync(ResultRunnable runnable, Callback callback);

    public abstract boolean isAsyncThread();
}