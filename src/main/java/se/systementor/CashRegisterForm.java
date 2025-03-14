package se.systementor;

import se.systementor.CloudStorage.CloudStorage;
import se.systementor.db.Database;
import se.systementor.model.Product;
import se.systementor.model.SaleDetails;
import se.systementor.model.Sales;
import se.systementor.model.Statistics;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class CashRegisterForm {
    private JPanel panel1;
    private JPanel panelRight;
    private JPanel panelLeft;
    private JTextArea receiptArea;
    private JPanel buttonsPanel;
    private JTextField productName;
    private JTextField quantity;
    private JButton addButton;
    private JButton payButton;
    private JButton button1;
    private Database database = new Database();
    private Product currentProduct=null;
    private int antal;
    private  float totalValue=0;
    float vat1=0;
    float vat2=0;
    float val1=0;
    float val2=0;
    private LocalDateTime dTime;
    private int receiptId;


    public CashRegisterForm() {

        ArrayList<SaleDetails> saleDetailsList=new ArrayList<>();


        for (Product product : database.activeProducts()) {
            String name=product.getProductName();
            JButton button = new JButton(name);
            buttonsPanel.add(button);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentProduct=product;
                    productName.setText(name);
                }
            });
        }


        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!quantity.getText().isEmpty()){
                    antal=Integer.parseInt(quantity.getText());
                }

                if(handleException()){
                    SaleDetails saleDetails = new SaleDetails();

                    dTime= LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    String formattedDateTime = formatter.format(dTime);
                    if(receiptArea.getText().isEmpty()){
                        receiptId= database.getReceiptId()+1;
                        receiptArea.append("                     DAILY SUPERSHOP\n");
                        receiptArea.append("----------------------------------------------------\n");
                        receiptArea.append("\n");
                        receiptArea.append("Kvittonummer:"+receiptId+"         "+"Datum:"+formattedDateTime+"\n");
                        receiptArea.append("----------------------------------------------------\n");
                    }
                    saleDetails.setSaleID(receiptId);
                    saleDetails.setProductId(currentProduct.getId());
                    saleDetails.setProductName(currentProduct.getProductName());
                    saleDetails.setProductPrice(currentProduct.getProductPrice());
                    saleDetails.setQuantity(antal);
                    saleDetails.setSaleDate(dTime);
                    //LocalDateTime.parse(formattedDateTime)

                    float value=(antal* currentProduct.getProductPrice());
                    saleDetails.setTotalValue(value);
                    totalValue+=value;
                    if(currentProduct.getVat()==12){
                        float vat= (float) (value*0.12);
                        vat1+=vat;
                        saleDetails.setTaxValue(vat);
                        val1+=value;
                    }
                    else if(currentProduct.getVat()==25){
                        float vat= (float) (value*0.25);
                        vat2+=vat;
                        saleDetails.setTaxValue(vat);
                        val2+=value;
                    }
                    receiptArea.append(currentProduct.getProductName() + "        " + currentProduct.getProductPrice() + "*" + antal+"                         "+value+ "\n");
                    saleDetailsList.add(saleDetails);
                }

                }
            }
        );
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(totalValue!=0) {
                    receiptArea.append("Total                                       ------\n");
                    receiptArea.append("                                            " + totalValue + "\n");
                    receiptArea.append(" MOMs%       moms            Netto         brutto" + "\n");
                    receiptArea.append("12%           " + Math.round(vat1 * 100) / 100f + "            " + (val1 - vat1) + "       " + (val1) + "\n");
                    receiptArea.append("25%           " + Math.round(vat2 * 100) / 100f + "            " + (val2 - vat2) + "           " + (val2) + "\n");

                    Sales sales=new Sales();
                    sales.setId(receiptId);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    String formattedDateTime = formatter.format(dTime);
                    sales.setDateTime(LocalDateTime.parse(formattedDateTime));
                    sales.setTotalTax(vat1+vat2);
                    sales.setTotalSale(val1+val2);

                    String s = database.insertTotalValue(sales,saleDetailsList);
                    receiptArea.append(s + "\n");
                    totalValue = 0;
                    vat1 = vat2 = val1 = val2 = 0;
                    saleDetailsList.clear();
                    clearText();
                    addButton.setEnabled(false);
                    payButton.setEnabled(false);



                }
                else{
                    JOptionPane.showMessageDialog(null,"Please Select a product,enter a quantity and add in the caret");}
            }
        });
        button1.addActionListener(new ActionListener() {
            Statistics statistics = database.retrieveStatistics();
            String fileName = "DagensStatistik.xml";
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileWriter myfile = new FileWriter(fileName);
                    myfile.write("<xml>\n");
                    myfile.write("<SaleStatistics>\n");
                    myfile.write("<FirstOrderDateTime>" + statistics.getFirstOrderDateTime() + "</FirstOrderDateTime>\n");
                    myfile.write("<LastOrderDateTime>" + statistics.getLastOrderDateTime() + "</LastOrderDateTime>\n");
                    myfile.write("<TotalSalesInclVat>" + statistics.getTotalSaleInclVat() + "</TotalSalesInclVat>\n");
                    myfile.write("<TotalVat>" + statistics.getTotalVat() + "</TotalVat>\n");
                    myfile.write("<TotalNumberOfReceipts>" + statistics.getTotalNoOfReceipt() + "</TotalNumberOfReceipts>\n");
                    myfile.write("</SaleStatistics>\n");
                    myfile.write("</xml>\n");
                    myfile.close();

                    CloudStorage cloudStorage = new CloudStorage();
                    cloudStorage.storeInS3(fileName);
                    JOptionPane.showMessageDialog(null,"Successfully Saved the file");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,ex.getMessage());
                }
            }
        });
    }

    public void run() {
        JFrame frame = new JFrame("Cash Register");
        frame.setContentPane(new CashRegisterForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize( 1000, 700 ) ;
        frame.setVisible(true);
    }

    private void createUIComponents() {
    }

    private void clearText() {

        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                receiptArea.setText("");
                addButton.setEnabled(true);
                payButton.setEnabled(true);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    private boolean handleException(){
        if(productName.getText().isEmpty() || quantity.getText().isEmpty() || antal<=0){
            if (productName.getText().isEmpty() && !quantity.getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please enter product name");
                return false;
            }
            else if (quantity.getText().isEmpty() && !productName.getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please enter a quantity");
                return false;
            }
            else if (productName.getText().isEmpty() && quantity.getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please select a product and add quantity");
                return false;
            }
            else if(!productName.getText().isEmpty() && !quantity.getText().isEmpty() && antal<=0){
                JOptionPane.showMessageDialog(null,"Quantity should be greater than 0");
                return false;
            }
        }
        return true;
    }
}
