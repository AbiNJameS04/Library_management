package Entity;

public class Damages {
    private int id;
    private String type;
    private int severity;
    private double repairCost;

    public Damages(int id, String type, int severity, double repairCost) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.repairCost = repairCost;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public double getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(double repairCost) {
        this.repairCost = repairCost;
    }

    @Override
    public String toString() {
        return "Damage{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", severity=" + severity +
                ", repairCost=" + repairCost +
                '}';
    }
}
