// Hospital management system
package hospital_system;

import com.mysql.cj.protocol.x.SyncFlushDeflaterOutputStream;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
class Patient{
    public Scanner sc;
    public Connection conn;
    public Patient(Scanner sc,Connection conn){
        this.sc=sc;
        this.conn=conn;
    }
    
    public void addPatient(){
        sc.nextLine();
        System.out.println("Enter the name of patient:");
        String pname=sc.nextLine();
        System.out.println("Enter the age of patient:");
        int page=Integer.parseInt(sc.nextLine());
        System.out.println("Enter the gender of patient:");
        String pgender=sc.nextLine();
        String insert_query="Insert into patients(name,age,gender) VALUES (?,?,?)";
        try{
            PreparedStatement ps=conn.prepareStatement(insert_query);
            ps.setString(1, pname);
            ps.setInt(2, page);
            ps.setString(3, pgender);
            int affectedRows=ps.executeUpdate();
            if(affectedRows>0){
                System.out.println("Patient is added successfully");
            }
            else{
                System.out.println("Failed to add patient!");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void viewPatients(){
       String display_query= "Select * from patients";
       try{
           PreparedStatement ps=conn.prepareStatement(display_query);
           ResultSet rs=ps.executeQuery();
          System.out.println("+------------+--------------+------+--------+");
          System.out.println("| Patient Id | Patient Name | Age  | Gender |");
          System.out.println("+------------+--------------+------+--------+");
          while(rs.next()){
              System.out.printf("|%-12s|%-14s|%-6s|%-7s |\n",rs.getInt("id"),rs.getString("name"),rs.getInt("age"),rs.getString("gender"));
               
          }
          System.out.println("+------------+--------------+------+--------+");
         
       }
       catch(SQLException e){
           e.printStackTrace();
       }
    }
    
    public boolean findPatientById(int pid){
        String findQuery="Select * from patients where id=?";
        try{
            PreparedStatement ps=conn.prepareStatement(findQuery);
            ps.setInt(1, pid);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                return true;
            }
            else{
                return false;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
} 

class Doctor{
    public Scanner sc;
    public Connection conn;
    public Doctor(Scanner sc,Connection conn){
        this.sc=sc;
        this.conn=conn;
    }

    
    public void viewDoctors(){
       String display_query= "Select * from doctors";
       try{
           PreparedStatement ps=conn.prepareStatement(display_query);
           ResultSet rs=ps.executeQuery();
          System.out.println("+------------+--------------+----------------+");
          System.out.println("| Doctor Id | Doctor Name   | specialization |");
          System.out.println("+------------+--------------+----------------+");
          while(rs.next()){
              System.out.printf("|%-12s|%-14s| %-15s|\n",rs.getInt("id"),rs.getString("name"),rs.getString("specialization"));
               
          }
          System.out.println("+------------+--------------+----------------+");
         
       }
       catch(SQLException e){
           e.printStackTrace();
       }
    }
    
    public boolean findDoctorById(int pid){
        String findQuery="Select * from doctors where id=?";
        try{
            PreparedStatement ps=conn.prepareStatement(findQuery);
            ps.setInt(1, pid);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                return true;
            }
            else{
                return false;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}

public class Hospital_System {
private static final String url ="jdbc:mysql://localhost:3306/hospital_db";
 private static final String username="root";
 private static final String password="siddha@123";

    public static void main(String[] args) throws ClassNotFoundException,SQLException {
        Scanner sc=new Scanner(System.in);
         Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn=DriverManager.getConnection(url, username, password);
        Doctor d=new Doctor(sc, conn);
        Patient p=new Patient(sc, conn);
        int choice;
        do{
            System.out.println("Enter your choice:");
            System.out.println("/*********************************************");
            System.out.println("1.Add patient:");
            System.out.println("2.View patient:");
            System.out.println("3.View Doctors:");
            System.out.println("4.Book appointments:");
            System.out.println("5.Exit:");
             System.out.println("/*********************************************");
            choice=sc.nextInt();
         switch(choice){
             case 1:
                 p.addPatient();
                 break;
             case 2:
                 p.viewPatients();
                 break;
             case 3:
                 d.viewDoctors();
                 break;
             case 4:
                 bookAppointments(sc, conn, p, d);
                 break;
             case 5:
                 return;
             default:
                 System.out.println("Enter valid choice:");
         }
        }while(true);
    }
    
    static void bookAppointments(Scanner sc,Connection conn,Patient p,Doctor d){
        System.out.println("Enter patient id:");
        int pid=sc.nextInt();
        System.out.println("Enter doctor id:");
        int did=sc.nextInt();
        System.out.println("Enter appointment date(YYYY-MM-DD):");
        String adate=sc.next();
        if(p .findPatientById(pid) && d .findDoctorById(did)){
            if(checkAvailability(adate,did,conn)){
                String sql="Insert into appointments(patient_id,doctor_id,appointment_date) values(?,?,?)";
                try{
                PreparedStatement ps=conn.prepareStatement(sql);
                ps.setInt(1, pid);
                ps.setInt(2, did);
                ps.setString(3, adate);
                int affectedRows=ps.executeUpdate();
                if(affectedRows>0){
                    System.out.println("Sucessfully booked appointment:");
                }
                else{
                    System.out.println("Error booking appointment");
                }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                
            }
            else{
                System.out.println("Doctor is not available");
            }
        }
        else{
            System.out.println("Either doctor or patient is not present:");
        }
    }
    public static boolean checkAvailability(String adate,int did,Connection conn){
        String sql="Select COUNT(*) from appointments where appointment_date=? and doctor_id=? ";
        try{
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, adate);
            ps.setInt(2, did);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                if(rs.getInt(1)==0){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    
}
