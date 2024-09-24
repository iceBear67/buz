package buz.api.event;

import java.util.Optional;
import java.util.function.Function;

public interface EventContext {
    <T> Optional<T> get(String key);

    <T> void put(String key, T value);

    void remove(String key);

    void contains(String key);

    void putIfAbsent(String key, Object value);

    void putIfPresent(String key, Object value);

    void computeIfAbsent(String key, Function<String, Object> get);
}
