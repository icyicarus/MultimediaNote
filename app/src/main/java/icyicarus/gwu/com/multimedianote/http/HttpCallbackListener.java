package icyicarus.gwu.com.multimedianote.http;

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
