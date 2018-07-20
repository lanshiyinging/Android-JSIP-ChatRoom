package jsip_msg_demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rance.chatui.ui.activity.ChatActivity;

import java.util.HashMap;

import bupt.jsip_demo.R;
import jsip_ua.SipProfile;
import jsip_ua.impl.DeviceImpl;

public class MainActivity extends AppCompatActivity implements OnClickListener,
		SharedPreferences.OnSharedPreferenceChangeListener {

	EditText editTextTo;
	EditText editTextName;
	EditText editTextPort;
	SharedPreferences prefs;
	SipProfile sipProfile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);

		sipProfile = new SipProfile();
		HashMap<String, String> customHeaders = new HashMap<>();
		customHeaders.put("customHeader1","customValue1");
		customHeaders.put("customHeader2","customValue2");
		DeviceImpl.getInstance().Initialize(getApplicationContext(), sipProfile, customHeaders);

		editTextTo = (EditText) findViewById(R.id.editTextTo);
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextPort = (EditText) findViewById(R.id.editTextPort);

		// register preference change listener
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		initializeSipFromPreferences();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case (R.id.btnSend):
			String toSip = editTextTo.getText().toString();
			String remoteIp;
			String remotePort;
			if(toSip.split(":").length > 2) {
				remotePort = toSip.split(":")[2];
				remoteIp = toSip.split(":")[1];
				remoteIp = remoteIp.substring(remoteIp.indexOf("@") + 1);
			}else{
				remotePort = "5060";
				remoteIp	= toSip.substring(toSip.indexOf("@") + 1);
			}

			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("remote_ip",remoteIp);
			editor.putString("remote_port",remotePort);
			editor.putString("local_user", editTextName.getText().toString());
			editor.putString("local_port", editTextPort.getText().toString());
			editor.apply();

			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("sip", toSip);
			intent.putExtra("remote_ip", remoteIp);
			intent.putExtra("remote_port", remotePort);
			intent.putExtra("local_user", editTextName.getText().toString());
			intent.putExtra("local_port", editTextPort.getText().toString());
			startActivity(intent);
			//DeviceImpl.getInstance().SendMessage(editTextTo.getText().toString(), editTextMessage.getText().toString() );
			
			break;
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
										  String key) {
		if (key.equals("remote_ip")) {
			sipProfile.setRemoteIp((prefs.getString("remote_ip", "")));
		} else if (key.equals("remote_port")) {
			sipProfile.setRemotePort(Integer.parseInt(prefs.getString(
					"remote_port", "5060")));
		}  else if (key.equals("local_user")) {
			sipProfile.setSipUserName(prefs.getString("local_user",
					"alice"));
		} else if (key.equals("local_sip_password")) {
			sipProfile.setSipPassword(prefs.getString("local_sip_password",
					"1234"));
		} else if (key.equals("local_port")){
			sipProfile.setLocalPort(Integer.parseInt(prefs.getString("local_port","5080")));
		}

	}

	@SuppressWarnings("static-access")
	private void initializeSipFromPreferences() {
		sipProfile.setRemoteIp((prefs.getString("remote_ip", "")));
		sipProfile.setRemotePort(Integer.parseInt(prefs.getString(
				"remote_port", "5060")));
		sipProfile.setSipUserName(prefs.getString("local_user", "alice"));
		sipProfile.setSipPassword(prefs.getString("local_sip_password", "1234"));
		sipProfile.setLocalPort(Integer.parseInt(prefs.getString("local_port","5080")));

	}

}
