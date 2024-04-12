package Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelReportData {

    @SerializedName("Report_Name")
    @Expose
    private String reportName;
    @SerializedName("Report_Value")
    @Expose
    private String reportValue;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportValue() {
        return reportValue;
    }

    public void setReportValue(String reportValue) {
        this.reportValue = reportValue;
    }
}
