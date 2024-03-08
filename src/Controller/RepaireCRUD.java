package Controller;

import Entity.Repaire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import Api.App;

public class RepaireCRUD {

    int budget = 60;
    App a = new App();
    Connection con = a.DbConnection();

    public String addRepaire(Repaire repaire) {
        try {
            if (!bookExists(repaire.getBook_id())) {
                String errorMessage = "Book with ID " + repaire.getBook_id() + " does not exist";

                return errorMessage;
            }
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO repaire (book_id, damage, repaire_date, repaireStatus) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, repaire.getBook_id());
            stmt.setString(2, repaire.getDamage());
            stmt.setDate(3, java.sql.Date.valueOf(repaire.getRepaire_date()));
            stmt.setString(4, repaire.getRepaireStatus());
            // stmt.executeUpdate();
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return null;
            } else {
                return "Failed to add book.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error occurred.";
        }
    }

    public boolean bookExists(int bookId) {

        PreparedStatement stmt;
        try {
            stmt = con.prepareStatement("SELECT book_id FROM books WHERE book_id = ?");
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Repaire> getAllRepaire() {
        List<Repaire> repaires = new ArrayList<>();
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM repaire");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int rep_id = rs.getInt("rep_id");
                int book_id = rs.getInt("book_id");
                String damage = rs.getString("damage");
                LocalDate repaire_date = rs.getDate("repaire_date").toLocalDate();
                String repaireStatus = rs.getString("repaireStatus");
                Repaire repaire = new Repaire(rep_id, book_id, damage, repaire_date, repaireStatus);
                repaires.add(repaire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repaires;
    }

    public void updateRepaire(Repaire repaire) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE repaire SET book_id=?, damage=?, repaire_date=?, repaireStatus=? WHERE rep_id=?");
            stmt.setInt(1, repaire.getBook_id());
            stmt.setString(2, repaire.getDamage());
            stmt.setDate(3, java.sql.Date.valueOf(repaire.getRepaire_date()));
            stmt.setString(4, repaire.getRepaireStatus());
            stmt.setInt(5, repaire.getRep_id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRepaire(int rep_id) {
        try {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM repaire WHERE rep_id=?");
            stmt.setInt(1, rep_id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Repaire getRepaireById(int rep_id) {
        Repaire repaire = null;
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM repaire WHERE rep_id = ?");
            stmt.setInt(1, rep_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int book_id = rs.getInt("book_id");
                String damage = rs.getString("damage");
                LocalDate repaire_date = rs.getDate("repaire_date").toLocalDate();
                String repaireStatus = rs.getString("repaireStatus");
                repaire = new Repaire(rep_id, book_id, damage, repaire_date, repaireStatus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repaire;
    }

    /// book Repaire
    public List<Repaire> getRepairList() {
        List<Repaire> repaireList = new ArrayList<>();
        try {
            String query = "SELECT * FROM repaire WHERE damage !='no damage'";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int rep_id = rs.getInt("rep_id");
                int book_id = rs.getInt("book_id");
                String damage = rs.getString("damage");
                LocalDate repairDate = rs.getDate("repaire_date").toLocalDate();
                String repairStatus = rs.getString("repaireStatus");
                double repairCost = getRepairCost(damage);
                int severity = getSeverity(damage);

                Repaire repaire = new Repaire(rep_id, book_id, damage, repairDate, repairStatus, repairCost, severity);
                repaireList.add(repaire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repaireList;
    }

    public double getRepairCost(String damage) {
        double repairCost = 0;
        try {
            String query = "SELECT repair_cost FROM damages WHERE damage_type = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, damage);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                repairCost = rs.getDouble("repair_cost");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repairCost;
    }

    public int getSeverity(String damage) {
        int severity = 0;
        try {
            String query = "SELECT severity FROM damages WHERE damage_type = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, damage);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                severity = rs.getInt("severity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return severity;
    }

    public List<Repaire> manageRepair() throws SQLException {
        List<Repaire> repaireList = new ArrayList<>();
        List<Repaire> updatedRepaireList = new ArrayList<>();
        repaireList = sortRepaires(getRepairList());

        double totalCost = 0;
        boolean statusUpdated = false; 

        for (Repaire repaire : repaireList) {
            if (!repaire.getRepaireStatus().equals("Completed")) {
                double repairCost = repaire.getRepairCost();
                totalCost += repairCost;
                if (repaire.getRepaireStatus().equals("In Progress")) {
                    LocalDate currentDate = LocalDate.now();
                    LocalDate repaireDate = repaire.getRepaire_date();
                    long daysDifference = ChronoUnit.DAYS.between(repaireDate, currentDate);

                    if (daysDifference > 15 && totalCost <= budget && !statusUpdated) {
                        repaire.setRepaireStatus("Completed");
                        updatedRepaireList.add(repaire);
                        statusUpdated = true;
                    } else {
                        repaire.setRepaireStatus("In Progress");
                        updatedRepaireList.add(repaire);
                    }
                } else {
                    if (totalCost <= budget) {
                        repaire.setRepaireStatus("Completed");
                        updatedRepaireList.add(repaire);
                    } else {
                        repaire.setRepaireStatus("In Progress");
                        updatedRepaireList.add(repaire);
                    }
                }
            }
        }
        updateRepaireStatus(updatedRepaireList);
        return updatedRepaireList;
    }

    public List<Repaire> sortRepaires(List<Repaire> repaireList) throws SQLException {
        repaireList.sort((r1, r2) -> {
            return r2.getSeverity() - r1.getSeverity();
        });
        return repaireList;
    }

    public void updateRepaireStatus(List<Repaire> repaireList) {
        try {
            for (Repaire repaire : repaireList) {
                String query = "UPDATE repaire SET repaireStatus = ? WHERE rep_id = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, repaire.getRepaireStatus());
                stmt.setInt(2, repaire.getRep_id());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
