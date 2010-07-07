package rest;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.server.utils.LinkBuilders;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Path("mails")
public class MailResource {
	private static final String MAIL = "mails";
    private static final String ITEM_PATH = "{mails}";        

    private JSONObject createJSONObject(Mail mail) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("from", mail.getFrom());
        obj.put("to", mail.getTo());
        obj.put("subject", mail.getSubject());
        obj.put("text", mail.getText());
        obj.put("id", mail.getId());
        return obj;
    }

    @GET
    @Produces( { MediaType.APPLICATION_JSON })
    public JSONArray getMails() {
        JSONArray result = new JSONArray();

        Collection<Mail> mails = MailManager.getInstance().getMails();

        for (Mail mail : mails) {
            try {
                result.put(createJSONObject(mail));
            } catch (JSONException e) {
                e.printStackTrace();
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        return result;
    }

    @Path(ITEM_PATH)
    @GET
    @Produces( { MediaType.APPLICATION_JSON })
    public JSONObject getMail(@Context LinkBuilders link, @Context UriInfo uri,
            @PathParam(MAIL) String mailId) {

        Mail mail = MailManager.getInstance().getMail(mailId);

        if (mail == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        JSONObject result;
        try {
            result = createJSONObject(mail);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return result;
    }    
}
