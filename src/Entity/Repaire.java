package Entity;

import java.time.LocalDate;

public class Repaire {
    private int rep_id;
    private int book_id;
    private String damage;
    private LocalDate repaire_date;
    private String repaireStatus;
    private double repairCost;
    private int severity;
   

    public Repaire() {
    }


    public void setRep_id(int rep_id) {
        this.rep_id = rep_id;
    }


    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }


    public void setDamage(String damage) {
        this.damage = damage;
    }


    public void setRepaire_date(LocalDate repaire_date) {
        this.repaire_date = repaire_date;
    }


    public void setRepaireStatus(String repaireStatus) {
        this.repaireStatus = repaireStatus;
    }


    public void setRepairCost(double repairCost) {
        this.repairCost = repairCost;
    }


    public void setSeverity(int severity) {
        this.severity = severity;
    }


    public int getRep_id() {
        return rep_id;
    }


    public int getBook_id() {
        return book_id;
    }


    public String getDamage() {
        return damage;
    }


    public LocalDate getRepaire_date() {
        return repaire_date;
    }


    public String getRepaireStatus() {
        return repaireStatus;
    }


    public double getRepairCost() {
        return repairCost;
    }


    public int getSeverity() {
        return severity;
    }


    @Override
    public String toString() {
        return "Repaire [rep_id=" + rep_id + ", book_id=" + book_id + ", damage=" + damage + ", repaire_date="
                + repaire_date + ", repaireStatus=" + repaireStatus + ", repairCost=" + repairCost + ", severity="
                + severity + "]";
    }


    public Repaire(int rep_id, int book_id, String damage, LocalDate repaire_date, String repaireStatus,
            double repairCost, int severity) {
        this.rep_id = rep_id;
        this.book_id = book_id;
        this.damage = damage;
        this.repaire_date = repaire_date;
        this.repaireStatus = repaireStatus;
        this.repairCost = repairCost;
        this.severity = severity;
    }


    public Repaire(int rep_id, int book_id, String damage, LocalDate repaire_date, String repaireStatus) {
        this.rep_id = rep_id;
        this.book_id = book_id;
        this.damage = damage;
        this.repaire_date = repaire_date;
        this.repaireStatus = repaireStatus;
    }
    

}