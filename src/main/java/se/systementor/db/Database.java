package se.systementor.db;

import se.systementor.model.Product;
import se.systementor.model.SaleDetails;
import se.systementor.model.Sales;
import se.systementor.model.Statistics;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {
    String url = "jdbc:mysql://localhost:3306/productdb";
    String user = "root";
    String password = "todo";


    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }
    public List<Product> activeProducts(){
        ArrayList<Product> products = new ArrayList<Product>();

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id,productName,productPrice,vat FROM products");

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setProductName(rs.getString("productName"));
                product.setProductPrice(rs.getFloat("productPrice"));
                product.setVat(rs.getFloat("vat"));
                products.add(product);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return products;
    }
    public int getReceiptId(){

        try{
            Connection con=getConnection();
            PreparedStatement ps=con.prepareStatement("select count(id) as  TOTAL_COUNT FROM sales");
            ResultSet rs=ps.executeQuery();
            if(rs.next())
                    {
                        return rs.getInt("TOTAL_COUNT");
                    }
            rs.close();
            ps.close();
            con.close();

        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return 0;
    }
    public String insertTotalValue(Sales sales,ArrayList<SaleDetails> saleDetailsList){
        try{
            Connection con=getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps=con.prepareStatement("insert into sales (saleDate,totalTax,totalSale) values (?,?,?)");
            ps.setObject(1,sales.getDateTime());
            ps.setFloat(2, sales.getTotalTaxTax());
            ps.setFloat(3, sales.getTotalSale());
            int i=ps.executeUpdate();
            con.commit();
            for(SaleDetails saleDetails:saleDetailsList){
                PreparedStatement ps2=con.prepareStatement("INSERT INTO saledetails (saleId,productId,productName,productPrice,quantity,saleDate,taxValue,totalValue) VALUES (?,?,?,?,?,?,?,?) ");
                ps2.setInt(1,sales.getId());
                ps2.setInt(2, saleDetails.getProductId());
                ps2.setString(3, saleDetails.getProductName());
                ps2.setFloat(4, saleDetails.getProductPrice());
                ps2.setInt(5, saleDetails.getQuantity());
                ps2.setObject(6, saleDetails.getSaleDate());
                ps2.setFloat(7, saleDetails.getTaxValue());
                ps2.setFloat(8, saleDetails.getTotalValue());
                ps2.executeUpdate();
                con.commit();
            }
            if(i>0){
                return "Tack för ditt köp";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    return "Transaction error";
    }
    public Statistics retrieveStatistics(){
        Statistics statistics = new Statistics();
        try(Connection con=getConnection()){
            int totalNoOfReceipt = getReceiptId();
            statistics.setTotalNoOfReceipt(totalNoOfReceipt);
            try(PreparedStatement ps = con.prepareStatement("select saleDate from sales where id=1");
                ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    statistics.setFirstOrderDateTime((LocalDateTime) rs.getObject(1));
                }
            }
            try(PreparedStatement ps2 = con.prepareStatement("select saleDate from sales where id=" + totalNoOfReceipt);
                ResultSet rs1 = ps2.executeQuery();) {

                if (rs1.next()) {
                    statistics.setLastOrderDateTime((LocalDateTime) rs1.getObject(1));
                }
            }
            try(PreparedStatement ps3 = con.prepareStatement("select sum(totalTax) as sum_total from sales");
                ResultSet rs2 = ps3.executeQuery();) {

                if (rs2.next()) {
                    statistics.setTotalVat(rs2.getFloat("sum_total"));
                }
            }
            try(PreparedStatement ps4 = con.prepareStatement("select sum(totalSale) as sum_sale from sales");
                ResultSet rs3 = ps4.executeQuery();) {

                if (rs3.next()) {
                    statistics.setTotalSaleInclVat(rs3.getFloat("sum_sale"));
                }
            }
            return statistics;

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

}
