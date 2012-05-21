package events.notifier;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class EventEdit extends Activity {

    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private EventsDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper=new EventsDbAdapter(this);
        mDbHelper.open();
        setContentView(R.layout.event_edit);
        setTitle(R.string.edit_event);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId=(savedInstanceState==null) ? null :
        	(Long) savedInstanceState.getSerializable(EventsDbAdapter.KEY_ROWID);
        if(mRowId == null) {
        	Bundle extras=getIntent().getExtras();
        	mRowId = extras != null ? extras.getLong(EventsDbAdapter.KEY_ROWID)
        			:null;
        }
        populateFields();
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }
    
    private void populateFields() {
        if (mRowId != null) {
            Cursor event = mDbHelper.fetchEvent(mRowId);
            startManagingCursor(event);
            mTitleText.setText(event.getString(
    	            event.getColumnIndexOrThrow(EventsDbAdapter.KEY_TITLE)));
            mBodyText.setText(event.getString(
                    event.getColumnIndexOrThrow(EventsDbAdapter.KEY_BODY)));
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(EventsDbAdapter.KEY_ROWID, mRowId);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if (mRowId == null) {
            long id = mDbHelper.createEvent(title, body);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateEvent(mRowId, title, body);
        }
    }
    
    
}

