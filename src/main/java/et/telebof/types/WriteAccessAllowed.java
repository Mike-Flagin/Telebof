package et.telebof.types;


import java.io.Serializable;

public class WriteAccessAllowed extends JsonSerializable implements Serializable {
    public String web_app_name;
    public Boolean from_request, from_attachment_menu;

}
