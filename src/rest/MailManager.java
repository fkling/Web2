package rest;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MailManager {
	
	private static MailManager instance = new MailManager();
	private static final Map<String, Mail> mails = Collections
    .synchronizedMap(new HashMap<String, Mail>());

	/* Initialize some mails with hard-coded values */
	static {
	mails.put("1", new Mail("1","a@a.com","b@b.com","test","testtesttesttest"));
	mails.put("2", new Mail("2", "b@b.com","c@c.com", "test2", "testtesttesttest"));
	};

	private MailManager() {
	}
	
	public static MailManager getInstance() {
		return instance;
	}
	
	public Collection<Mail> getMails() {
		return Collections.unmodifiableCollection(mails.values());
	}
	
	public Mail getMail(String id) {
		return mails.get(id);
	}
}
