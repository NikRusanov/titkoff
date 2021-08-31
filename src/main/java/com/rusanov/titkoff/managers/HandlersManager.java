package com.rusanov.titkoff.managers;

import com.rusanov.titkoff.api.Handler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class HandlersManager {

    private Map<String, List<Handler>> handlersMap = new HashMap<>();

    public void publish(String figi, Object... objects) {
        List<Handler> handlers = this.handlersMap.get(figi);
        if (handlers == null || handlers.isEmpty())
            return;
        for (Handler handler : handlers)
            handler.handle(objects);
    }

    public void subscribe(String identifier, Handler handler) {
        List<Handler> handlers = this.handlersMap.get(identifier);
        if (handlers == null) {
            handlers = new LinkedList<>();
            handlersMap.put(identifier, handlers);
        }
        handlers.add(handler);
    }

    public void unsubscribe(String identifier, Handler handler) {
        List<Handler> handlers = handlersMap.get(identifier);
        if(handlers == null || handlers.isEmpty())
            return;
        handlers.remove(handler);
    }

    public boolean isSubscribed(String identifier, Handler handler) {
        List<Handler> handlers = handlersMap.get(identifier);
        if (handlers == null || handlers.isEmpty())
            return false;
        return handlers.contains(handler);
    }
}
