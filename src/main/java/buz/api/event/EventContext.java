package buz.api.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface EventContext extends Cloneable{
    <T> Optional<T> get(ContextKey<T> key);

    default <T> T get(ContextKey<T> key, T defaultValue) {
        return get(key).orElse(defaultValue);
    }

    @Nullable
    <T> T put(ContextKey<T> key, T value);

    /**
     * @return a mutable map view of this context.
     */
    @NotNull
    Map<String, Object> asMap();

    record ContextKey<T>(String identifier, Class<T> typeOfValue) {
    }
}
