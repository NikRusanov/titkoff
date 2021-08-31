package com.rusanov.titkoff.bot;

import com.rusanov.titkoff.api.AbstractVisitorViewer;
import com.rusanov.titkoff.managers.HandlersManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

public abstract class Bot extends AbstractVisitorViewer<Object, Void> {

    @Autowired
    private HandlersManager handlersManager;

    @Override
    @Async
    public Void handle(Object... params) {
        return super.handle(params);
    }
}
