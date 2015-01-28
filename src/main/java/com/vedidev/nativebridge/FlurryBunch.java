package com.vedidev.nativebridge;

import android.content.Context;
import com.flurry.android.FlurryAgent;
import com.vedidev.nativebridge.Bunch;
import com.vedidev.nativebridge.ProcessorEngine;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author vedi
 *         date 05/10/14
 */
@SuppressWarnings("UnusedDeclaration")
public class FlurryBunch implements Bunch {
    private Context context;

    private static class FlurryBunchException extends RuntimeException {
        public final Map<String, String> errorParams;

        public FlurryBunchException(Map<String, String> errorParams) {
            this.errorParams = errorParams;
        }
    }

    public FlurryBunch() {
        registerProcessor("startSession", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                String apiKey = params.getString("apiKey");
                startSession(apiKey);
            }
        });

        registerProcessor("endSession", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                endSession();
            }
        });

        registerProcessor("setAppVersion", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                String appVersion = params.getString("appVersion");
                setAppVersion(appVersion);
            }
        });

        registerProcessor("logEvent", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                String eventId = params.getString("eventId");
                JSONObject eventParameters = params.getJSONObject("parameters");
                logEvent(eventId, eventParameters);
            }
        });

        registerProcessor("logError", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                String errorId = params.getString("errorId");
                String message = params.getString("message");
                JSONObject errorParameters = params.getJSONObject("parameters");
                logError(errorId, message, errorParameters);
            }
        });

        registerProcessor("logPageView", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                logPageView();
            }
        });

        registerProcessor("logTimedEventBegin", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                String eventId = params.getString("eventId");
                JSONObject eventParameters = params.getJSONObject("parameters");
                logTimedEventBegin(eventId, eventParameters);
            }
        });

        registerProcessor("logTimedEventEnd", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                String eventId = params.getString("eventId");
                JSONObject eventParameters = params.getJSONObject("parameters");
                logTimedEventEnd(eventId, eventParameters);
            }
        });

        registerProcessor("setContinueSessionMillis", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                long milliseconds = params.getLong("milliseconds");
                setContinueSessionMillis(milliseconds);
            }
        });

        registerProcessor("setDebugLogEnabled", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                boolean enabled = params.getBoolean("enabled");
                setDebugLogEnabled(enabled);
            }
        });

        registerProcessor("setUserId", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                String userId = params.getString("userId");
                setUserId(userId);
            }
        });

        registerProcessor("setAge", new ProcessorEngine.CallHandler() {
            @Override
            public void handle(JSONObject params, JSONObject retParams) throws Exception {
                int age = params.getInt("age");
                setAge(age);
            }
        });
    }

    private void startSession(String apiKey) {
        FlurryAgent.onStartSession(context, apiKey);
    }

    private void endSession() {
        FlurryAgent.onEndSession(context);
    }

    private void setAppVersion(String appVersion) {
        // skip it for Android?
    }

    private void logEvent(String eventId, JSONObject eventParameters) {
        FlurryAgent.logEvent(eventId, toMap(eventParameters));
    }

    private void logError(String errorId, String message, JSONObject errorParameters) {
        FlurryAgent.onError(errorId, message, new FlurryBunchException(toMap(errorParameters)));
    }

    private void logPageView() {
        FlurryAgent.onPageView();
    }

    private void logTimedEventBegin(String eventId, JSONObject eventParameters) {
        FlurryAgent.logEvent(eventId, toMap(eventParameters), true);
    }

    private void logTimedEventEnd(String eventId, JSONObject eventParameters) {
        FlurryAgent.endTimedEvent(eventId, toMap(eventParameters));
    }

    private void setContinueSessionMillis(long milliseconds) {
        FlurryAgent.setContinueSessionMillis(milliseconds);
    }

    private void setDebugLogEnabled(boolean enabled) {
        FlurryAgent.setLogEnabled(enabled);
    }

    private void setUserId(String userId) {
        FlurryAgent.setUserId(userId);
    }

    private void setAge(int age) {
        FlurryAgent.setAge(age);
    }


    private Map<String, String> toMap(JSONObject object) {
        Map<String, String> map = new HashMap<String, String>();
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, object.optString(key));
        }
        return map;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    private void registerProcessor(String key, ProcessorEngine.CallHandler callHandler) {
        ProcessorEngine.getInstance().registerProcessor("FlurryBunch", key, callHandler);
    }
}
