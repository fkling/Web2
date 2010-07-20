package test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import rest.Mail;
import rest.MailManager;

public class TestMailManager {	 

	@Test
	public void testGetMail() {
		Mail mail = MailManager.getInstance().getMail("<Pine.LNX.4.50.0304241008150.14317-100000@dhalsim.dreamhost.com>");
		assertTrue("Excpected mail with id=<Pine.LNX.4.50.0304241008150.14317-100000@dhalsim.dreamhost.com>",
				mail.getId().equals("<Pine.LNX.4.50.0304241008150.14317-100000@dhalsim.dreamhost.com>"));
	}

	@Test
	public void testGetChildrens() {
		List<Mail> mails = MailManager.getInstance().getChildrens("<3EA8371D.6080901@cookiecrook.com>");
		Iterator<Mail> i = mails.iterator();
		Mail mail = i.next();
		assertTrue("Excpected 1 children with id=<Pine.LNX.4.50.0304241233110.14317-100000@dhalsim.dreamhost.com>",
				mails.size()==1 && mail.getId().equals("<Pine.LNX.4.50.0304241233110.14317-100000@dhalsim.dreamhost.com>"));
	}

	@Test
	public void testSearchByField() {
		List<Mail> mails = MailManager.getInstance().searchByField("content", "bug");
		assertTrue("Excpected 7 results", mails.size()==7);
		mails = MailManager.getInstance().searchByField("subject", "bug");
		assertTrue("Excpected 0 results", mails.size()==0);
	}

	
	@Test
	public void testSearchByAuthor() {
		List<Mail> mails = MailManager.getInstance().searchByAuthor("George");
		assertTrue("Excpected 2 results", mails.size()==2);
	}

	@Test
	public void testSearchByDate() {
		List<Mail> mails = MailManager.getInstance().searchByDate("2003-04-24", "2003-04-24");
		assertTrue("Excpected 28 results", mails.size()==28);
	}

	@Test
	public void testSearch() {
		List<Mail> mails = MailManager.getInstance().search(null, null, null, null, null, "bug");
		assertTrue("Excpected 7 results", mails.size()==7);
	}

	@Test
	public void testNextMessageInThread() {
		Mail mail = MailManager.getInstance().nextMessageInThread("<005301c30a8f$a31f3b80$6502a8c0@Dreamfire>");
		assertTrue("Excpected mail with id=<001601c30a43$4e8a29f0$210110ac@BigGuy>", mail.getId().equals("<001601c30a43$4e8a29f0$210110ac@BigGuy>"));
	}

	@Test
	public void testPreviousMessageInThread() {
		Mail mail = MailManager.getInstance().previousMessageInThread("<Pine.LNX.4.50.0304241558520.15423-100000@dhalsim.dreamhost.com>");
		assertTrue("Excepcted mail with id=<3EA83B4C.2060708@latchman.org>", mail.getId().equals("<3EA83B4C.2060708@latchman.org>"));
	}

	@Test
	public void testGetStatistics() {
		String[] str = MailManager.getInstance().getStatistics();
		assertEquals(222, (int)Integer.valueOf(str[2]));
		assertEquals("2003-04-24 05:07:26.0", str[0]);
		assertEquals("2003-04-29 09:59:56.0", str[1]);
	}

	@Test
	public void testGetWholeThread() {
		List<Mail> mails = MailManager.getInstance().getWholeThread("<3EA83B4C.2060708@latchman.org>");
		assertTrue("Excpected 4 results",mails.size()==4);
	}

	@Test
	public void testGetRoot() {
		String root = MailManager.getInstance().getRoot("<3EA83B4C.2060708@latchman.org>");
		assertEquals("<3EA828AA.50506@f2o.org>", root);
	}

}
