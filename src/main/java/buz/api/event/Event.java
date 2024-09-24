package buz.api.event;

import buz.api.event.EventContext;
import buz.api.event.EventSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class Event<S extends EventSource> {
    protected final EventContext context;
    protected final S source;
}
