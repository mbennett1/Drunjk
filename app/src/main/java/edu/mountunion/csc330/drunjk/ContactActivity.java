package edu.mountunion.csc330.drunjk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import android.Manifest;

public class ContactActivity extends AppCompatActivity {

    Database database;
    int contactId;
    String contactPhoneNumber;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        database = new Database(this);
        updateView();
    } // end of method onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    } // end of method onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_calculator:
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                finish();
                return true;
            case R.id.action_contacts:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } // end switch getting selected menu item
    } // end method onOptionsItemSelected

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, do the phone-related task

                } // end if permission was granted
                else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                } // end else permission was denied
                return;
            } // end case to see if permission was granted or not
        } // end switch to see if permission was granted or not
    } // end of method onRequestPermissionResult

    public void showCallConfirmationDialog(Context context, int id) {
        Contact contact = database.selectById(id);
        contactPhoneNumber = contact.getPhoneNum();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Call " + "(" + contact.getPhoneNum().substring(0, 3) + ") "
                       + contact.getPhoneNum().substring(3, 6) + "-"
                       + contact.getPhoneNum().substring(6) + "?");
        PlayDialog call = new PlayDialog();
        alert.setPositiveButton("YES", call);
        alert.setNegativeButton("NO", call);
        alert.show();
    } // end of method showCallConfirmationDialog

    private class PlayDialog implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int id) {
            phoneAFriend(id);
        }
    }

    public void phoneAFriend(int id) {
        if (id == -1)  /* YES */  {
            // Code to call the person
            // contactPhoneNumber stores the person's phone number of the button clicked

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                } // end if need to show permission request
                else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } // end else
            } // end if permission has not been granted yet

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + contactPhoneNumber));
            try {
                startActivity(callIntent);
            } catch (SecurityException se) {
                // do something in case they haven't given us permission
                Log.i("Help", "don't have permission");
            } // end catch

        } else if (id == -2) /* NO */  {
            // close the dialog box (leave in case we want to add something to do when they give this answer)
        } // end else if the user selected not to call the person
    } // end of method phoneAFriend

    public void updateView( ) {
        ArrayList<Contact> contacts = database.selectAll( );

        if( contacts.size( ) > 0 ) {
            // create ScrollView and GridLayout
            ScrollView scrollView = new ScrollView( this );
            GridLayout grid = new GridLayout( this );
            grid.setRowCount( contacts.size( ) + 2 );
            grid.setColumnCount( 3 );

            // create arrays of components
            TextView [] ids = new TextView[contacts.size( )];
            Button [] contactButtons = new Button[contacts.size( )];
            Button [] editButtons = new Button[contacts.size( )];
            ButtonHandler bh;

            // retrieve width of screen
            Point size = new Point( );
            getWindowManager( ).getDefaultDisplay( ).getSize( size );
            int width = size.x;

            TextView contactsTextView = new TextView(this);
            contactsTextView.setText("Contacts");
            contactsTextView.setGravity( Gravity.CENTER );
            contactsTextView.setTextSize(50);

            TextView emptyTextView = new TextView(this);
            TextView emptyTextView2 = new TextView(this);

            grid.addView( emptyTextView, width / 8,
                    ViewGroup.LayoutParams.WRAP_CONTENT );
            grid.addView( contactsTextView, ( int ) ( width * .65 ),
                    ViewGroup.LayoutParams.WRAP_CONTENT );
            grid.addView( emptyTextView2, ( int ) ( width / 4.8 ),
                    ViewGroup.LayoutParams.WRAP_CONTENT );

            int i = 0;
            for ( Contact contact : contacts ) {
                // create the TextView for the contact's id
                ids[i] = new TextView( this );
                ids[i].setGravity( Gravity.CENTER );
                ids[i].setText( "" + contact.getId( ) );
                ids[i].setTextSize(30);

                // create the button
                contactButtons[i] = new Button( this );
                contactButtons[i].setText( contact.getFirstName() + " " + contact.getLastName() );
                contactButtons[i].setId( contact.getId( ) );
                contactButtons[i].setTextSize(30);
                contactButtons[i].setTransformationMethod(null);

                editButtons[i] = new Button( this );
                editButtons[i].setText( "EDIT" );
                editButtons[i].setId( contact.getId( ) );
                editButtons[i].setTextSize(20);
                editButtons[i].setTransformationMethod(null);
                editButtons[i].setPadding(40, 50, 40, 30);
                editButtons[i].setGravity( Gravity.CENTER );

                // set up event handling
                bh = new ButtonHandler( contactButtons[i] );
                contactButtons[i].setOnClickListener( bh );

                // add the elements to grid
                grid.addView( ids[i], width / 8,
                        ViewGroup.LayoutParams.WRAP_CONTENT );
                grid.addView( contactButtons[i], ( int ) ( width * .65 ),
                        ViewGroup.LayoutParams.WRAP_CONTENT );
                grid.addView( editButtons[i], ( int ) ( width / 4.8 ),
                        ViewGroup.LayoutParams.WRAP_CONTENT );

                i++;
            } // end for running through contact list

            TextView newContactView = new TextView(this);
            newContactView.setGravity(Gravity.CENTER_HORIZONTAL);
            newContactView.setText(contacts.size() + 1 + "");
            newContactView.setTextSize(30);

            Button newContactButton = new Button(this);
            newContactButton.setText("New Contact");
            newContactButton.setTextSize(27);
            newContactButton.setTextColor(Color.rgb(125, 125, 125));
            newContactButton.setTransformationMethod(null);
            AddButtonHandler newButtonHandler = new AddButtonHandler(newContactButton);
            newContactButton.setOnClickListener(newButtonHandler);

            grid.addView(newContactView, width / 8,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            grid.addView(newContactButton, (int) (width * .65),
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            scrollView.addView( grid );

            setContentView( scrollView );
        } // end if there is at least 1 contact in the list

    } // end of method updateView

    public Context getContext() {
        return this;
    } // end of method getContext

    private class ButtonHandler implements View.OnClickListener {

        private Button myButton;

        public ButtonHandler(Button myButton) {
            this.myButton = myButton;
        } // end of ButtonHandler constructor

        @Override
        public void onClick(View v) {
            contactId = myButton.getId();
            showCallConfirmationDialog(getContext(), contactId);
        } // end of method onClick for the ButtonHandler class (for contact buttons)
    } // end of class ButtonHandler (for contact buttons)

    private class AddButtonHandler implements View.OnClickListener {

        private Button myButton;

        public AddButtonHandler(Button myButton) {
            this.myButton = myButton;
        } // end of ButtonHandler constructor

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), AddContactActivity.class);
            getContext().startActivity(intent);
            finish();
        } // end of method onClick for AddButtonHandler
    } // end of class AddButtonHandler

} // end of class ContactActivity