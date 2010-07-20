package rest;

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.server.utils.LinkBuilders;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import logging.Logging;

@Path("mails")
public class MailResource {
	private static final String MAIL = "mails";
    private static final String ITEM_PATH = "{mails}";
    private Logging log = new Logging();

    
    /**
     * 
     * @param mail
     * @return JSONObject representing a mail object.
     * @throws JSONException
     */
    private JSONObject createJSONObject(Mail mail) throws JSONException {
        JSONObject obj = new JSONObject();
        if(mail !=null){
	        obj.put("id", mail.getId());
	        obj.put("author", mail.getAuthor());        
	        obj.put("subject", mail.getSubject());        
	        obj.put("content", mail.getContent());
	        obj.put("date", mail.getDate());
	        obj.put("parentId", mail.getParentId());
        }
        return obj;
    }
    
    /**
     *
     * @param mails
     * @return JSONObject containing the <i>count</i> of the results and the results
     *  (which consist of JSONobjects).
     * @throws JSONException
     */
    private JSONObject createMainJSONObject(List<Mail> mails) throws JSONException {
    	JSONObject obj = new JSONObject();
    	obj.put("count", mails.size());
    	obj.put("mails", createMailsJSONObject(mails));
    	return obj;
    }
    
    /**
     * 
     * @param mails
     * @return JSONObject that contains the mail objects.
     * @throws JSONException
     */
    private JSONObject createMailsJSONObject (List<Mail> mails) throws JSONException {
    	JSONObject obj = new JSONObject();
    	for (Mail mail: mails) {
    		obj.put(mail.getId(), createJSONObject(mail));
    	}
    	return obj;
    }
    
    
    /**
     * 
     * @param link
     * @param uri
     * @param mailId
     * @return The mail with the given <i>id</i>, as JSONObject.
     */
    @Path(ITEM_PATH)
    @GET
    @Produces( { MediaType.APPLICATION_JSON })
    public JSONObject getMail(@Context LinkBuilders link, @Context UriInfo uri,
            @PathParam(MAIL) String mailId) {

        Mail mail = MailManager.getInstance().getMail(mailId);
        log.info("Get mail called");
        
        if (mail == null) {
        	log.warn("Mail not found");
        	throw new WebApplicationException(Response.Status.NOT_FOUND);
            
        }

        JSONObject result;
        try {
            result = createJSONObject(mail);
        } catch (JSONException e) {
        	log.error("Internal server error");
        	e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);            
        }
        
        log.info("Get mail succeeded");
        return result;
    }
    
    /**
     * 
     * @param link
     * @param uri
     * @param subject
     * @param author
     * @param content
     * @param fromDate
     * @param toDate
     * @param query
     * @return List of mails from the search.
     */
    @Path("search")
    @GET
    @Produces( { MediaType.APPLICATION_JSON })
    public JSONObject search(@Context LinkBuilders link, @Context UriInfo uri,
            @QueryParam("subject") String subject, @QueryParam("author") String author,
            @QueryParam("content") String content, @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate, @QueryParam("query") String query) {
    	
    		log.info("Search called");
    	
    		JSONObject result;
    	    
	        List<Mail> mails = MailManager.getInstance().search(subject, author, content, fromDate, toDate, query);        
	    
	        try {
	            result = createMainJSONObject(mails);
	        } catch (JSONException e) {
	        	log.error("Internal server error");
	            e.printStackTrace();
	            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
	        }
	        
	        log.info("Search succeeded");
	        return result;    	
    }
    
    
    /**
     * 
     * @param link
     * @param uri
     * @param id
     * @return The next mail of the mail with the given <i>id</id>.
     */
    @Path("getNext")
    @GET
    @Produces( { MediaType.APPLICATION_JSON })
    public JSONObject getNextMessageInThread(@Context LinkBuilders link, @Context UriInfo uri,
            @QueryParam("id") String id) {
    	
    		log.info("Get next message in thread called");
    	
    		JSONObject result;
    	
	        Mail mail = MailManager.getInstance().nextMessageInThread(id);        
	    
	        try {
	            result = createJSONObject(mail);
	        } catch (JSONException e) {
	        	log.error("Internal server error");
	            e.printStackTrace();
	            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
	        }
	        
	        log.info("Get next message in thread succeeded");
	        return result;    	
    }    
    
    /**
     * 
     * @param link
     * @param uri
     * @param id
     * @return The previous mail of the mail with the given <i>id</id>.
     */
    @Path("getPrevious")
    @GET
    @Produces( { MediaType.APPLICATION_JSON })
    public JSONObject getPreviousMessageInThread(@Context LinkBuilders link, @Context UriInfo uri,
            @QueryParam("id") String id) {
    	
    		log.info("Get previous message in thread called");
    	
    		JSONObject result;
    	
	        Mail mail = MailManager.getInstance().previousMessageInThread(id);        
	    
	        try {
	            result = createJSONObject(mail);
	        } catch (JSONException e) {
	        	log.error("Internal server error");
	            e.printStackTrace();
	            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
	        }
	        
	        log.info("Get previous message in thread succeeded");
	        return result;    	
    }
    
    /**
     * 
     * @param link
     * @param uri
     * @return Statistics for mails in the following format:<br><br>
     * 			count: <i>the number of all mails</i>.<br>
     * 			earliestDate: <i>the date of the earliest mail</i>.<br>
     * 			latestDate: <i>the date of the latest mail</i>.
     */
    @Path("getStatistics")
    @GET
    @Produces( { MediaType.APPLICATION_JSON })
    public JSONObject getStatistics(@Context LinkBuilders link, @Context UriInfo uri) {
    	
    		log.info("Get statistics called");
    	
    		JSONObject result = new JSONObject();
    	
	        String s[]  = MailManager.getInstance().getStatistics();
	        
	        try {
		        result.put("earliestDate", s[0]);
		        result.put("latestDate", s[1]);
		        result.put("count", s[2]);
	        }
	        catch(Exception e){
	        	log.error("Internal server error");
	        	e.printStackTrace();
	        }
	        log.info("Get statistics succeeded");
	        return result;    	
    }
    
    /**
     * 
     * @param link
     * @param uri
     * @param id
     * @return All the mails which are part of the thread in which the mail with the given <i>id</i> is part.
     */
    @Path("getThread")
    @GET
    @Produces( { MediaType.APPLICATION_JSON })
    public JSONObject getWholeThread(@Context LinkBuilders link, @Context UriInfo uri,
    		@QueryParam("id") String id) {
    		
    		log.info("Get whole thread called");
    		
    		JSONObject result = new JSONObject();    	
	        List<Mail> mails = MailManager.getInstance().getWholeThread(id);
	        
	        try {
		        result.put("count", mails.size());
		        result.put("root",MailManager.getInstance().getRoot(id));
		        
		        JSONObject mailsObject = new JSONObject();
		        
		        Iterator<Mail> i = mails.iterator();
		        
		        while (i.hasNext()) {
		        	Mail mail = i.next();	        	
		        	JSONObject obj = new JSONObject();
		        	obj.put("id", mail.getId());
			        obj.put("author", mail.getAuthor());        
			        obj.put("subject", mail.getSubject());        
			        obj.put("content", mail.getContent());
			        obj.put("date", mail.getDate());
			        obj.put("parentId", mail.getParentId());
			        
			        JSONArray childrens = new JSONArray();		        
			        List<Mail> childrensList = MailManager.getInstance().getChildrens(mail.getId());
			        
			        Iterator<Mail> childIterator = childrensList.iterator();
			        
			        while (childIterator.hasNext()) {
			        	childrens.put(childIterator.next().getId());
			        }
			        
			        obj.put("childrens", childrens);
			        
			        mailsObject.put(mail.getId(), obj);
		        }
		        result.put("mails", mailsObject);
	        }
	        catch(Exception e) {
	        	log.error("Internal server error");
	        	e.printStackTrace();
	        }
	        log.info("Get whole thread succeeded");	        
	        return result;
    }
    
}
