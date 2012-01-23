package com.alien_roger.court_deadlines.entities;

import java.util.Calendar;

/**
 * CourtCase class
 *
 * @author alien_roger
 * @created at: 25.12.11 21:59
 */
public class CourtCase {
    private String customer = "";
    private String caseName = "";
    private Calendar courtDate;
    private Calendar proposalDate;
    private String notes = "";

    public String getCourtType() {
        return courtType;
    }

    public void setCourtType(String courtType) {
        this.courtType = courtType;
    }

    private String courtType = "";

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public Calendar getCourtDate() {
        return courtDate;
    }

    public long getCourtDateLong() {
        return courtDate.getTimeInMillis();
    }

    public void setCourtDate(Calendar courtDate) {
        this.courtDate = courtDate;
    }

    public void setCourtDate(long courtDate) {
        this.courtDate.setTimeInMillis(courtDate);
    }

    public Calendar getProposalDate() {
        return proposalDate;
    }

    public long getProposalDateLong() {
        return proposalDate.getTimeInMillis();
    }

    public void setProposalDate(Calendar proposalDate) {
        this.proposalDate = proposalDate;
    }

    public void setProposalDate(long proposalDate) {
        this.proposalDate.setTimeInMillis(proposalDate);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
