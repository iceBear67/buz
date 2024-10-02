package buz.api.event;

public interface ResultListener<E extends Event<?,?>> {
    void onResult(E event, boolean cancelled, Exception exception);
}
