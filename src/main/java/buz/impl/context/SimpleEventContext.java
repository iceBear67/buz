package buz.impl.context;

import buz.api.event.EventContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleEventContext implements EventContext {
    protected final Map<String, Object> value;

    public SimpleEventContext(){
        this(new HashMap<>());
    }

    private SimpleEventContext(Map<String, Object> value) {
        this.value = value;
    }

    @Override
    public <T> Optional<T> get(ContextKey<T> key) {
        return (Optional<T>) Optional.ofNullable(value.get(key.identifier()));
    }

    @Override
    public <T> @Nullable T put(ContextKey<T> key, T value) {
        return (T) this.value.put(key.identifier(), value);
    }

    @Override
    public @NotNull Map<String, Object> asMap() {
        return value;
    }


}
