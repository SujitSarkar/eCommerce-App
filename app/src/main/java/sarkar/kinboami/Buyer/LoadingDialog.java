package sarkar.kinboami.Buyer;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import static sarkar.kinboami.R.layout.activity_loading_bar;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity myActivity){
        activity = myActivity;
    }
    public void start(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(activity_loading_bar,null));

        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();

    }
}
