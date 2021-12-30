package ec.sekai.moramon;

import android.os.AsyncTask;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class services_moramon extends WearableListenerService {
    private String f_text;

    @Override
    public void onMessageReceived(MessageEvent messageEvent){
        if(messageEvent.getPath().equals("/path/message")){
            f_text = new String(messageEvent.getData());
            System.out.println(f_text);
            new ActualizaTask().execute();
        }
    }

    private class ActualizaTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... fvoid) {
            return null;
        }

        protected void onPostExecute(Void result){
            if(aw_moramon.mTextView != null)
                aw_moramon.mTextView.setText(f_text);
        }
    }
}
