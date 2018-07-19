package jsip_ua;

import jsip_ua.impl.SipEvent;

import java.util.EventListener;

public interface ISipEventListener extends EventListener {

	public void onSipMessage(SipEvent sipEvent);
}
