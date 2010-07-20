package rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

public class MailManager {
	private Session session;
	private static MailManager instance = new MailManager();
	
	
	/*----------------------
	 * 
	 * PRIVATE FUNCTIONS
	 * 
	 ----------------------*/
	
	/**
	 * 
	 * @param query
	 * @return List of Mails after executing the query.
	 */	
	private List<Mail> getResultForQuery(String query){
		Transaction t = getSession().beginTransaction();		
		List<Mail> result = session.createQuery(query)
		.list();
		t.commit();
		session.close();
		return result;
	}
	
	/**
	 * 
	 * @param query
	 * @param first
	 * @param max
	 * @return List of Mails (<i>max</i> results starting from <i>first</i>)
	 * 			after executing the query.
	 */
	private List<Mail> getResultForQuery(String query, int first, int max){
		Transaction t = getSession().beginTransaction();		
		List<Mail> result = session.createQuery(query)
		.setFirstResult(first)
		.setMaxResults(max)
		.list();
		t.commit();
		session.close();
		return result;
	}
	
	/**
	 * 
	 * @param id
	 * @return <i>True</i> if the mail with the give <i>id</i> has children.
	 * 			<i>False</i> otherwise.
	 */
	private boolean hasChildrens(String id){
		return !getChildrens(id).isEmpty();
	}
	
	/**
	 * 
	 * @param id
	 * @return The first child of the mail with the given <i>id</i>.
	 */
	private Mail getFirstChildren(String id){
		return getChildrens(id).get(0);
	}
	
	/**
	 * 
	 * @param id
	 * @return The next brother of the mail with the given <i>id</i>.
	 */
	private Mail nextBrother(String id){
		Mail thisMail = getMail(id);
		String queryString="from Mail mail where parentId ='"+thisMail.getParentId()+"' AND" +
							" date >= '"+thisMail.getDate().toString()+" ORDER BY date ASC'";
		List<Mail> result = getResultForQuery(queryString,0,1);
			if(result.size()>0) {
			Iterator<Mail> it=result.iterator();
			Mail mail=it.next();		
			return mail;
		}
		return null;
	}
	
	/**
	 * 
	 * @param id
	 * @return The previous brother of the mail with the given <i>id</i>.
	 */
	private Mail previousBrother(String id){
		Mail thisMail = getMail(id);
		String queryString="from Mail mail where parentId ='"+thisMail.getParentId()+"' AND" +
							" date < '"+thisMail.getDate().toString()+"' ORDER BY date DESC";
		List<Mail> result = getResultForQuery(queryString,0,1);
		if(result.size()>0){
			Iterator<Mail> it=result.iterator();
			Mail mail=it.next();		
			return mail;
		}
			else return null;
	}
	
	/**
	 * 
	 * @param id
	 * @return The next mail of the mail with the given <i>id</i>.
	 * 			Recursive function called inside nextMessageInThread.
	 */
	private Mail nextMail(String id){
		Mail thisMail = getMail(id);
		if(nextBrother(id) != null) return nextBrother(id);
		else if(!thisMail.getParentId().equals("NULL"))
			return nextMail(thisMail.getParentId());
		return null;				
	}
	
	/**
	 * 
	 * @return The date of the earliest mail.
	 */
	private String earliestDate(){
		String queryString="SELECT MIN(date) FROM Mail mail";
		Transaction t = getSession().beginTransaction();		
		List<java.sql.Timestamp> result = session.createQuery(queryString)
		.list();
		t.commit();
		session.close();		
		if(result.size()>0){
			Iterator<java.sql.Timestamp> it=result.iterator();					
			return it.next().toString();
		}
			else return "";
	}
	
	/**
	 * 
	 * @return The date of the latest mail.
	 */
	private String latestDate() {
		String queryString="SELECT MAX(date) FROM Mail mail";
		Transaction t = getSession().beginTransaction();		
		List<java.sql.Timestamp> result = session.createQuery(queryString)
		.list();
		t.commit();
		session.close();		
		if(result.size()>0){
			Iterator<java.sql.Timestamp> it=result.iterator();					
			return it.next().toString();
		}
			else return "";
	}
	
	/**
	 * 
	 * @return Number of all the mails.
	 */
	private int count(){
		String queryString="SELECT COUNT(*) FROM Mail mail";
		Transaction t = getSession().beginTransaction();		
		List<Long> result = session.createQuery(queryString)
		.list();
		t.commit();
		session.close();		
		if(result.size()>0){
			Iterator<Long> it=result.iterator();					
			return it.next().intValue();
		}
			else return 0;
	}
	
	/**
	 * 
	 * @param result
	 * @param id
	 * @return List of mails.
	 * 			Recursive function called inside getWholeThread.
	 */
	private List<Mail> getMailsInThread(List<Mail> result, String id){		
		result.add(getMail(id));
		if(hasChildrens(id)){
			List<Mail> l = getChildrens(id);
			Iterator<Mail> i = l.iterator();
			while(i.hasNext()){
				getMailsInThread(result, i.next().getId());
			}
		}
		return result;
	}
	
	
	
	
	/* ---------------------
	 * 
	 * PUBLIC FUNCTIONS  
	 *  
	 ---------------------*/	
	
	public Session getSession() {
		try {
			if (session == null || !session.isOpen()) {				
				session = HibernateUtil.getSessionFactory().openSession();
			}
		}
		catch (Exception e) {
			session = HibernateUtil.getSessionFactory().openSession();
		}

		return session;
	}
	
	public static MailManager getInstance() {
		return instance;
	}	
	
	/**
	 * 
	 * @param mailId
	 * @return The mail with the given <i>id</i>.
	 */
	public Mail getMail(String mailId){		
		String queryString="from Mail mail where mail.id ='"+mailId+"'";
		List<Mail> result = getResultForQuery(queryString);
		Iterator<Mail> it=result.iterator();
		Mail mail=it.next();		
		return mail;
	}
	
	/**
	 * 
	 * @param id
	 * @return The children of the mail with the given <i>id</id>.
	 */
	public List<Mail> getChildrens(String id){		
		String queryString="from Mail mail where mail.parentId = '"+id+"' ORDER BY date ASC";
		return getResultForQuery(queryString);
	}	
	
	/**
	 * 
	 * @param field
	 * @param value
	 * @return List of mails which fulfill the condition of containing the given <i>value</i>
	 * 			in the given <i>field</i>.
	 */
	public List<Mail> searchByField(String field,String value){
		List<Mail> result=new ArrayList<Mail>();
		FullTextSession fullTextSession = Search.getFullTextSession(getSession());
		try {
			fullTextSession.createIndexer().startAndWait();
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
			QueryParser parser = new QueryParser(Version.LUCENE_29, field, analyzer);
			// create native Lucene query
			org.apache.lucene.search.Query query = parser.parse(value);
			// wrap Lucene query in a org.hibernate.Query
			org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(query, Mail.class);
			// execute search
			result = hibQuery.list();			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return result;		
	}
	
	/**
	 * 
	 * @param fields
	 * @param value
	 * @return List of mails which fulfill the condition of containing the given <i>value</i>
	 * 			in the given <i>fields</i>.
	 */
	public List<Mail> searchByFields(String[] fields,String value){
		List<Mail> result=new ArrayList<Mail>();
		FullTextSession fullTextSession = Search.getFullTextSession(getSession());
		try {
			fullTextSession.createIndexer().startAndWait();
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
			MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, fields, analyzer);
			// create native Lucene query
			org.apache.lucene.search.Query query = parser.parse(value);
			// wrap Lucene query in a org.hibernate.Query
			org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(query, Mail.class);
			// execute search
			result = hibQuery.list();			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
		
	}
	
	/**
	 * 
	 * @param author
	 * @return List of mails which fulfill the condition of having the given <i>author</i> as author.
	 */
	public List<Mail> searchByAuthor(String author){		
		String queryString="from Mail mail where mail.author like '%"+author+"%'";
		return getResultForQuery(queryString);
	}
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return List of mails which fulfill the condition of being sent between <i>fromDate</i>
	 * 			and <i>toDate</i>.
	 */
	public List<Mail> searchByDate(String fromDate, String toDate){		
		String queryString="from Mail mail where DATE(mail.date) between '"+fromDate+"' and '"+toDate+"'";
		return getResultForQuery(queryString);
	}
	
	/**
	 * 
	 * @param subject
	 * @param author
	 * @param content
	 * @param fromDate
	 * @param toDate
	 * @param query
	 * @return List of mails which fulfill the condition of containing the given parameters in their fields.
	 */
	public List<Mail> search(String subject,String author, String content, String fromDate, String toDate, String query){
		List<Mail> result=new ArrayList<Mail>();
		boolean first=true;
		ArrayList<String> fields = new ArrayList<String>();		
		
		if(subject!=null){			
			result=searchByField("subject", subject);
			first=false;
		}	
		else if(query!=null) fields.add("subject");
		
		if(author!=null){
			if(first){
				result=searchByAuthor(author);
				first=false;
			}
			else
				result.retainAll(searchByAuthor(author));
		}
		else if(query!=null) fields.add("author");
		
		if(content!=null){
			if(first){
				result=searchByField("content",content);
				first=false;
			}
			else
				result.retainAll(searchByField("content",content));
		}
		else if(query!=null) fields.add("content");
		
		if(fromDate!=null && toDate!=null ){
			if(first){
				result=searchByDate(fromDate,toDate);
				first=false;
			}
			else
				result.retainAll(searchByDate(fromDate,toDate));
		}
		
		if(query!=null) {
			if(fields.isEmpty()) fields.add("content");
			String[] f = new String[fields.size()];
			for(int i=0;i<fields.size();i++){
				f[i]=(String)fields.get(i);
			}
			if(first){
				result = searchByFields(f, query);
			}
			else {				
				result.retainAll(searchByFields(f, query));				
			}
		}
		
		return result;
		
	}	
	
	/**
	 * 
	 * @param id
	 * @return The mail which follows the mail with the given <i>id</i> in a thread view.
	 */
	public Mail nextMessageInThread(String id){
		if(hasChildrens(id)) return getFirstChildren(id);
		else return nextMail(id);
	}
	
	/**
	 * 
	 * @param id
	 * @return The mail which precedes the mail with the given <i>id</i> in a thread view.
	 */
	public Mail previousMessageInThread(String id){
		Mail thisMail = getMail(id);
		if(previousBrother(id) != null) return previousBrother(id);
		else if (!thisMail.getParentId().equals("NULL"))
			return getMail(thisMail.getParentId());
		return null;
	}		
	
	/**
	 * 
	 * @return An array of String with size 3 containing statistics as follows:<br><br>
	 * 			s[0] = <i>the date of the earliest mail</i>.<br>
	 * 			s[1] = <i>the date of the latest mail</i>.<br>
	 * 			s[2] = <i>the number of all mails</i>.
	 */
	public String[] getStatistics() {
		String[] s=new String[3];
		s[0] = earliestDate();
		s[1] = latestDate();
		s[2] = String.valueOf(count());
		
		return s;
	}
	
	/**
	 * 
	 * @param id
	 * @return List of mails which consists of all mails that form the thread,
	 * in which is part the mail with the given <i>id</i>.
	 */
	public List<Mail> getWholeThread(String id){
		return getMailsInThread(new ArrayList<Mail>(), getRoot(id));		
	}	
	
	/**
	 * 
	 * @param id
	 * @return The <i>id</i> of the mail which is the root of the mail with the given <i>id</i> 
	 */
	public String getRoot(String id){
		Mail thisMail = getMail(id);
		if(!thisMail.getParentId().equals("NULL"))
			return getRoot(thisMail.getParentId());
		return thisMail.getId();
	}
}
