package org.telegram.hamrahgram.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;


public class Alert extends AlertDialog.Builder {
    private CheckBox checkBox;
    private TextView messageTextView;

    public Alert(Context context) {
        super(context);
        View dialog = LayoutInflater.from(context).inflate(R.layout.alertdialog, null);
        checkBox = (CheckBox) dialog.findViewById(R.id.checkbox);
        messageTextView = (TextView) dialog.findViewById(R.id.message);
        checkBox.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        messageTextView.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        setView(dialog);
    }

    public void addActionListener(String text, ActionListener actionListener) {
        setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                actionListener.doAction(true);
            }
        });
    }

    public void setMessage(String text) {
        messageTextView.setText(text);
    }

    public void setCheckBoxText(String text) {
        checkBox.setText(text);
    }

    public void addCheckBoxActionListener(ActionListener actionListener) {
        checkBox.setOnClickListener(view -> {
            actionListener.doAction(checkBox.isChecked());

        });

    }

    public interface ActionListener {
        void doAction(boolean enable);
    }

}
