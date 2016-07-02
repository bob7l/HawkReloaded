package uk.co.oliwali.HawkEye.callbacks;

/**
 * @author bob7l
 */
public interface Callback<T> {

    /**
     * Called when the underlying process has finished
     *
     * @param t The object(s) returned from the process
     */
    void call(T t);

    /**
     * Called when the underlying process has failed
     *
     * @param throwable The error thrown from the process
     */
    void fail(Throwable throwable); //Todo: Implement default when we upgrade to Java 8

}
