package cswt;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static cswt.Ticket.convertToTicket;

public class TicketSnapshot {
    private Ticket ticket;
    private String dateModified;
    private String modifier;
    private String whatModified;

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getWhatModified() {
        return whatModified;
    }

    public void setWhatModified(String whatModified) {
        this.whatModified = whatModified;
    }

    /**
     * @return The ticket as a JSON Object
     */
    public JSONObject toJSON() {
        Map<String, Object> map = new HashMap<>();
        map.put("ticket", ticket);
        map.put("dateModified", dateModified);
        map.put("modifier", modifier);
        map.put("whatModified", whatModified);
        return new JSONObject(map);
    }

    public static synchronized TicketSnapshot convertToSnapshot(JSONObject obj) {
        try {
            TicketSnapshot history = new TicketSnapshot();
            Ticket ticket = convertToTicket(obj.getJSONObject("ticket"));
            history.setTicket(ticket);
            String dateModified = obj.getString("dateModified");
            history.setDateModified(dateModified);
            String modifier = obj.getString("modifier");
            history.setModifier(modifier);
            String whatModified = obj.getString("whatModified");
            history.setWhatModified(whatModified);
            return history;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

