public class Ticket {
    private String name;
    private String description;
    private String resolution;
    private String openDate;
    private String daysOpen;
    private String closedDate;
    private String status;
    private String priority;
    private String severity;
    private String client;
    private String assignedTo;

    public void setName(String n){
        this.name = n;
    }

    public String getName(){
        return name;
    }

    public void setDescription(String d){
        this.description = d;
    }

    public String getDescription(){
        return description;
    }

    public void setStatus(String s){
        this.status = s;
    }

    public String getStatus(){
        return status;
    }

    public void setPriority(String p){
        this.priority = p;
    }

    public String getPriority(){
        return priority;
    }

    public void setSeverity(String s){
        this.severity = s;
    }

    public String getSeverity(){
        return severity;
    }

    public void setClient(String c){
        this.client = c;
    }

    public String getClient(){
        return client;
    }

    public void setAssignedTo(String a){
        this.assignedTo = a;
    }

    public String getAssignedTo(){
        return assignedTo;
    }
}
