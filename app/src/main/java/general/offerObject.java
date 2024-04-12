package general;

/**
 * Created by Hitesh on 14-03-2016.
 */
public class offerObject {
    private String offer_date;
    private String offer_detail;
    private String offer_sender;


    offerObject(){

    }

    public offerObject(String offerdate, String offerdetail, String offersender){
        this.offer_date = offerdate;;
        this.offer_detail = offerdetail;
        this.offer_sender = offersender;

    }

    public String getOffer_date() {
        return offer_date;
    }

    public void setOffer_date(String offer_date) {
        this.offer_date = offer_date;
    }

    public String getOffer_detail() {
        return offer_detail;
    }

    public void setOffer_detail(String offer_detail) {
        this.offer_detail = offer_detail;
    }

    public String getOffer_sender() {
        return offer_sender;
    }

    public void setOffer_sender(String offer_sender) {
        this.offer_sender = offer_sender;
    }

    }


