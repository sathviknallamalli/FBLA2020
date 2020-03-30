package olyapps.sathv.fbla2020;

/**
 * Created by sathv on 7/9/2018.
 */

public class AnApproval {
    String approvalevent, approvalinfo;

    public AnApproval(String approvalevent, String approvalinfo) {
        this.approvalevent = approvalevent;
        this.approvalinfo = approvalinfo;
    }

    public String getApprovalevent() {
        return approvalevent;
    }

    public void setApprovalevent(String approvalevent) {
        this.approvalevent = approvalevent;
    }

    public String getApprovalinfo() {
        return approvalinfo;
    }

    public void setApprovalinfo(String approvalinfo) {
        this.approvalinfo = approvalinfo;
    }
}
