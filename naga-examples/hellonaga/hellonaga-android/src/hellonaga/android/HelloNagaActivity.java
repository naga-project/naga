package hellonaga.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import hellonaga.HelloNagaLogic;
import naga.core.spi.plat.android.AndroidPlatform;

public class HelloNagaActivity extends Activity {

    static {
        AndroidPlatform.register(); // using explicit registration as the ServiceLoader has an issue on Android (META-INF is excluded from the apk)
    }
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        new HelloNagaLogic(new HelloNagaLogic.MessageDisplayer() {
            @Override
            public void displayMessage(String message) {
                HelloNagaActivity.this.displayMessage(message);
            }
        }).run();
    }

    private void displayMessage(String message) {
        // Displaying the message
        TextView textView = (TextView) findViewById(R.id.helloNagaTextView);
        textView.setText(message);
    }
}
