package com.raghulj.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.raghulj.android.widgets.MailCheckEditText;

public class MailCheckTestAppActivity extends Activity {
	private MailCheckEditText mEmailTextView;
	private Button mSubmitButton;
	private String mSuggestedMailId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setContentView(R.layout.main);

		mEmailTextView = (MailCheckEditText) findViewById(R.id.editText1);
		mSubmitButton = (Button) findViewById(R.id.button1);

		mSubmitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mSuggestedMailId = mEmailTextView.suggest();
				if (!mSuggestedMailId.equals(mEmailTextView.getText()
						.toString())) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MailCheckTestAppActivity.this);
					builder.setMessage("Do you mean " + mSuggestedMailId + " ?")
							.setPositiveButton("Yes", dialogClickListener)
							.setNegativeButton("No", dialogClickListener)
							.show();
				}
			}
		});

	}

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				mEmailTextView.setText(mSuggestedMailId);
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				break;
			}
		}
	};
}