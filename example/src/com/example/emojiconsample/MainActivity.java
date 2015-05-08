package com.example.emojiconsample;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView lv = (ListView) findViewById(R.id.lv);
		final ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.listview_row_layout);
		lv.setAdapter(mAdapter);
		final EmojiconEditText emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
		final View rootView = findViewById(R.id.root_view);
		final ImageView emojiButton = (ImageView) findViewById(R.id.emoji_btn);
		final ImageView submitButton = (ImageView) findViewById(R.id.submit_btn);

		// Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
		final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);

		//Will automatically set size according to the soft keyboard size        
		popup.setSizeForSoftKeyboard();

		//If the emoji popup is dismissed, change emojiButton to smiley icon
		popup.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
			}
		});

		//If the text keyboard closes, also dismiss the emoji popup
		popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

			@Override
			public void onKeyboardOpen(int keyBoardHeight) {

			}

			@Override
			public void onKeyboardClose() {
				if(popup.isShowing())
					popup.dismiss();
			}
		});

		//On emoji clicked, add it to edittext
		popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

			@Override
			public void onEmojiconClicked(Emojicon emojicon) {
	            if (emojiconEditText == null || emojicon == null) {
	                return;
	            }

	            int start = emojiconEditText.getSelectionStart();
	            int end = emojiconEditText.getSelectionEnd();
	            if (start < 0) {
	                emojiconEditText.append(emojicon.getEmoji());
	            } else {
	                emojiconEditText.getText().replace(Math.min(start, end),
	                        Math.max(start, end), emojicon.getEmoji(), 0,
	                        emojicon.getEmoji().length());
	            }
	        }
		});

		//On backspace clicked, emulate the KEYCODE_DEL key event
		popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

			@Override
			public void onEmojiconBackspaceClicked(View v) {
				KeyEvent event = new KeyEvent(
						0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				emojiconEditText.dispatchKeyEvent(event);
			}
		});
		
		// To toggle between text keyboard and emoji keyboard keyboard(Popup)
		emojiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//If popup is not showing => emoji keyboard is not visible, we need to show it
				if(!popup.isShowing()){
					
					//If keyboard is visible, simply show the emoji popup
					if(popup.isKeyBoardOpen()){
						popup.showAtBottom();
						changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
					}
					
					//else, open the text keyboard first and immediately after that show the emoji popup
					else{
						emojiconEditText.setFocusableInTouchMode(true);
						emojiconEditText.requestFocus();
						popup.showAtBottomPending();
						final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
						changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
					}
				}
				
				//If popup is showing, simply dismiss it to show the undelying text keyboard 
				else{
					popup.dismiss();
				}
			}
		});	

		//On submit, add the edittext text to listview and clear the edittext
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String newText = emojiconEditText.getText().toString();
				emojiconEditText.getText().clear();
				mAdapter.add(newText);
				mAdapter.notifyDataSetChanged();

			}
		});
	}

	private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
		iconToBeChanged.setImageResource(drawableResourceId);
	}

}
