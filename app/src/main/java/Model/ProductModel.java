package Model;

/**
 * Created by Aamir on 12/12/2017.
 */

public class ProductModel {
    public String getItem_ID() {
        return Item_ID;
    }

    public void setItem_ID(String item_ID) {
        Item_ID = item_ID;
    }

    public String getItem_Code() {
        return Item_Code;
    }

    public void setItem_Code(String item_Code) {
        Item_Code = item_Code;
    }

    public String getItem_Desc() {
        return Item_Desc;
    }

    public void setItem_Desc(String item_Desc) {
        Item_Desc = item_Desc;
    }

    public String getItem_Price() {
        return Item_Price;
    }

    public void setItem_Price(String item_Price) {
        Item_Price = item_Price;
    }

    public String getItem_Available() {
        return Item_Available;
    }

    public void setItem_Available(String item_Available) {
        Item_Available = item_Available;
    }

    private String Item_ID="";
    private String Item_Code="";
    private String Item_Desc="";
    private String Item_Price="";
    private String Item_Available="";

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    private int qty=0;
    private float price=0;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
