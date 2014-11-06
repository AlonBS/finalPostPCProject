package com.dna.radius.infrastructure;
import java.util.HashMap;

import com.dna.radius.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;


public class MyApp extends Application {
    public enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER,
        E_COMMERCE_TRACKER,
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {

        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.global_tracker);
                    
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
    
}