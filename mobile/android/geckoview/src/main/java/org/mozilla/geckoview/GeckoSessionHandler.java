/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * vim: ts=4 sw=4 expandtab:
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.geckoview;

import org.mozilla.gecko.EventDispatcher;
import org.mozilla.gecko.util.BundleEventListener;
import org.mozilla.gecko.util.EventCallback;
import org.mozilla.gecko.util.GeckoBundle;
import org.mozilla.geckoview.GeckoSession;

import android.util.Log;

/* package */ abstract class GeckoSessionHandler<Listener>
    implements BundleEventListener {

    private static final String LOGTAG = "GeckoSessionHandler";
    private static final boolean DEBUG = false;

    private Listener mListener;
    private final boolean mAlwaysListen;
    private final String mModuleName;
    private final String[] mEvents;


    /* package */ GeckoSessionHandler(final String module,
                                      final GeckoSession session,
                                      final String[] events) {
        this(module, session, events, /* alwaysListen */ false);
    }

    /* package */ GeckoSessionHandler(final String module,
                                      final GeckoSession session,
                                      final String[] events,
                                      final boolean alwaysListen) {
        mAlwaysListen = alwaysListen;
        mModuleName = module;
        mEvents = events;

        if (alwaysListen) {
            register(session.getEventDispatcher());
        }
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(final Listener listener, final GeckoSession session) {
        final EventDispatcher eventDispatcher = session.getEventDispatcher();
        if (mListener == listener) {
            return;
        }

        if (!mAlwaysListen && mListener != null) {
            unregister(eventDispatcher);
        }

        mListener = listener;

        if (!mAlwaysListen && mListener != null) {
            register(eventDispatcher);
        }
    }

    private void unregister(final EventDispatcher eventDispatcher) {
        final GeckoBundle msg = new GeckoBundle(1);
        msg.putString("module", mModuleName);
        eventDispatcher.dispatch("GeckoView:Unregister", msg);
        eventDispatcher.unregisterUiThreadListener(this, mEvents);
    }

    private void register(final EventDispatcher eventDispatcher) {
        final GeckoBundle msg = new GeckoBundle(1);
        msg.putString("module", mModuleName);
        eventDispatcher.dispatch("GeckoView:Register", msg);
        eventDispatcher.registerUiThreadListener(this, mEvents);
    }

    @Override
    public void handleMessage(final String event, final GeckoBundle message,
                              final EventCallback callback) {
        if (DEBUG) {
            Log.d(LOGTAG, mModuleName + " handleMessage: event = " + event);
        }

        if (mListener != null) {
            handleMessage(mListener, event, message, callback);
        } else {
            callback.sendError("No listener registered");
        }
    }

    protected abstract void handleMessage(final Listener listener,
                                          final String event,
                                          final GeckoBundle message,
                                          final EventCallback callback);
}
