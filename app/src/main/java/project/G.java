package project;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class G extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        Log.i("Test", "onCreate: from G");
    }
}
