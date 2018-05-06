/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bank;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.table.*;

/**
 *
 * @author Kevin
 */
public class BankingDatabase extends javax.swing.JFrame {
    String userName="root";
    String passWord="biggie5941";
    String url="jdbc:mysql://localhost/banking_system";
    Connection conn;
    Statement stmt;
    PreparedStatement pst;
    ResultSet set;
    String accountNumber;
    String pin;
    String staffId;
    String password;

    /**
     * Creates new form BankingDatabase
     */
   
    public BankingDatabase() {
        connectToDatabase();
        initComponents();
        time();
        pack();
        frameStaffLogin.pack();
        frameStaffLogin.setLocationRelativeTo(null);
        frameStaff.pack();
        frameStaff.setLocationRelativeTo(null);
        frameCreate.pack();
        frameCreate.setLocationRelativeTo(null);
        frameEnter.pack();
        frameEnter.setLocationRelativeTo(null);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void connectToDatabase(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection(url,userName,passWord);
            stmt=conn.createStatement();
            pst=conn.prepareStatement("insert into accounts (acc_no,name,type,balance,time,pin) values (?,?,?,?,?,?)");
        }catch(ClassNotFoundException|SQLException cnfe){
            JOptionPane.showMessageDialog(null,cnfe.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    private void time(){
        new Thread(){
            @Override
            public void run(){
                while(true){
                    String timeStampp="";
                    timeStampp+=Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"/";
                    timeStampp+=Calendar.getInstance().get(Calendar.MONTH)+1+"/";
                    timeStampp+=Calendar.getInstance().get(Calendar.YEAR)+"    ";
                    timeStampp+=Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":";
                    timeStampp+=Calendar.getInstance().get(Calendar.MINUTE)+":";
                    timeStampp+=Calendar.getInstance().get(Calendar.SECOND);
                    lblTime.setText(timeStampp);
                    lblTimeStaff.setText(timeStampp);
                    lblTimeLogin.setText(timeStampp);
                }
            }
        }.start();
    }
    
    private void checkViabilityForLogin(){
        try{
            boolean viable=false;
            set=stmt.executeQuery("SELECT * FROM Accounts");
            while(set.next()){
                if(set.getObject(1).toString().equalsIgnoreCase(accountNumber)&&set.getObject(6).toString().equals(pin)){
                    viable=true;
                    JOptionPane.showMessageDialog(null,"Successful Login","Information",JOptionPane.INFORMATION_MESSAGE);
                    txtAccount.setText("");
                    txtPin.setText("");
                    txtAcc.setText(set.getObject(1).toString());
                    txtNm.setText(set.getObject(2).toString());
                    txtType.setText(set.getObject(3).toString());
                    comboStatus.setSelectedItem(set.getObject(3));
                    txtBalance.setText(set.getObject(4).toString());
                    setVisible(false);
                    frameEnter.setVisible(true);
                    txtTransaction.setText("");
                    txtTransaction.requestFocus();
                }
            }
            if(!viable){
                JOptionPane.showMessageDialog(null,"Login Denied\nInvalid Credentials","Error",JOptionPane.ERROR_MESSAGE);
                txtPin.setText("");
                txtPin.requestFocus();
            }
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null,sqle.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    private void checkViabilityForStaff(){
        try{
            boolean viable=false;
            set=stmt.executeQuery("Select * from staff");
            while(set.next()){
                if(set.getObject(1).toString().equalsIgnoreCase(staffId)&&set.getObject(3).toString().equalsIgnoreCase(password)){
                    viable=true;
                    JOptionPane.showMessageDialog(null,"Successful Login","Information",JOptionPane.INFORMATION_MESSAGE);
                    txtStaffId.setText("");
                    txtPassword.setText("");
                    setData();
                    frameStaffLogin.setVisible(false);
                    frameStaff.setVisible(true);
                    txtFilter.requestFocus();
                }
            }
            if(!viable){
                JOptionPane.showMessageDialog(null,"Login Failed\nInvalid Credentials","Error",JOptionPane.ERROR_MESSAGE);
            }
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null,sqle.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    private void setData(){
        try{
            set=stmt.executeQuery("Select * from "+comboStaff.getSelectedItem().toString());
            ResultSetMetaData temp=set.getMetaData();
            DefaultTableModel tm=(DefaultTableModel)tableStaff.getModel();
            int columns=countColumns(set)-1;
            for(int i=0;i<columns;i++){
                tm.addColumn(temp.getColumnLabel(i+1));
            }
            set.beforeFirst();
            while(set.next()){
                String[] data=new String[columns];
                for(int i=1;i<=columns;i++){
                    data[i-1]=set.getObject(i).toString();
                }
                tm.addRow(data);
            }
            sorter();
            setModel();
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null,sqle.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    private int countColumns(ResultSet data){
        int temp=0;
        try{
            if(data.next()){
                for(temp=1;;temp++){
                    data.getObject(temp);
                }
            }
        }catch(SQLException sqle){}
        return temp;
    }
    
    private void sorter(){
        int columnCount=tableStaff.getColumnCount();
        TableRowSorter<TableModel> temp=new TableRowSorter<>(tableStaff.getModel());
        for(int i=0;i<columnCount;i++){
            try{
                int one=Integer.parseInt((String)tableStaff.getModel().getValueAt(1,i));
                temp.setComparator(i,new Comparator(){
                    @Override
                    public int compare(Object o1,Object o2){
                        int i1=Integer.parseInt(o1.toString());
                        int i2=Integer.parseInt(o2.toString());
                        if(i1>i2){
                            return 1;
                        }else if(i1<i2){
                            return -1;
                        }
                        return 0;
                    }
                });
            }catch(NumberFormatException nfe){
                temp.setComparator(i,new Comparator(){
                    @Override
                    public int compare(Object o1,Object o2){
                        String s1=o1.toString();
                        String s2=o2.toString();
                        return s1.compareToIgnoreCase(s2);
                    }
                });
            }
        }
        tableStaff.setRowSorter(temp);
    }

    private void setModel(){
        int temp=tableStaff.getColumnCount();
        String[] model=new String[temp];
        for(int i=0;i<temp;i++){
            model[i]=String.valueOf(i+1);
        }
        comboColumn.setModel(new DefaultComboBoxModel<>(model));
        comboColumn.setSelectedIndex(0);
    }
    
    private void filter(){
        TableRowSorter temp=(TableRowSorter)tableStaff.getRowSorter();
        RowFilter<TableModel,Object> filTer=new RowFilter<TableModel,Object>() {
            @Override
            public boolean include(RowFilter.Entry entry) {
                String temp=txtFilter.getText().trim();
                try{
                return (entry.getValue(Integer.valueOf(comboColumn.getSelectedIndex())).toString().toLowerCase().contains(temp.toLowerCase()));
                }catch(ArrayIndexOutOfBoundsException aiob){
                    txtFilter.setText("");
                    String[] variable={"Not Selected"};
                    comboColumn.setModel(new DefaultComboBoxModel<>(variable));
                    comboColumn.setSelectedIndex(0);
                }
                return false;
            }
        };
        temp.setRowFilter(filTer);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frameStaff = new javax.swing.JFrame();
        jLabel4 = new javax.swing.JLabel();
        lblTimeStaff = new javax.swing.JLabel();
        comboStaff = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        comboColumn = new javax.swing.JComboBox<>();
        scrollStaff = new javax.swing.JScrollPane();
        tableStaff = tableStaff=new JTable(){
            public boolean isCellEditable(int i1,int i2){
                return false;
            }
        };
        btnLogOut = new javax.swing.JButton();
        frameStaffLogin = new javax.swing.JFrame();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtStaffId = new javax.swing.JTextField();
        btnLoginStaff = new javax.swing.JButton();
        lblTimeLogin = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblCaps = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        frameCreate = new javax.swing.JFrame();
        btncreate = new javax.swing.JButton();
        txtpin2 = new javax.swing.JPasswordField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtpin = new javax.swing.JPasswordField();
        comboType = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtAccountNumber = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        frameEnter = new javax.swing.JFrame();
        jLabel17 = new javax.swing.JLabel();
        txtAcc = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtNm = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtType = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtBalance = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtTransaction = new javax.swing.JTextField();
        btnDeposit = new javax.swing.JButton();
        btnWithdraw = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        comboStatus = new javax.swing.JComboBox<>();
        btnBack1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        btnLogin = new javax.swing.JButton();
        btnCreate = new javax.swing.JButton();
        btnStaff = new javax.swing.JButton();
        lblTime = new javax.swing.JLabel();
        txtPin = new javax.swing.JPasswordField();

        frameStaff.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frameStaff.setSize(new java.awt.Dimension(600, 400));

        jLabel4.setFont(new java.awt.Font("Courier New", 3, 40)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("STAFF");

        lblTimeStaff.setFont(new java.awt.Font("DigifaceWide", 3, 30)); // NOI18N
        lblTimeStaff.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTimeStaff.setText("jLabel5");

        comboStaff.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        comboStaff.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Log", "Accounts" }));
        comboStaff.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                comboStaffPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        jLabel5.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("TABLE");

        jLabel6.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("FILTER");

        txtFilter.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("COLUMN");

        tableStaff.setFont(new java.awt.Font("Palatino Linotype", 3, 16)); // NOI18N
        tableStaff.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableStaff.setFillsViewportHeight(true);
        scrollStaff.setViewportView(tableStaff);

        btnLogOut.setFont(new java.awt.Font("Courier New", 3, 16)); // NOI18N
        btnLogOut.setText("Log Out");
        btnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogOutActionPerformed(evt);
            }
        });
        btnLogOut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnLogOutKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout frameStaffLayout = new javax.swing.GroupLayout(frameStaff.getContentPane());
        frameStaff.getContentPane().setLayout(frameStaffLayout);
        frameStaffLayout.setHorizontalGroup(
            frameStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, frameStaffLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(frameStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTimeStaff, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, frameStaffLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(frameStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollStaff, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(frameStaffLayout.createSequentialGroup()
                                .addGroup(frameStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(comboStaff, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnLogOut, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(frameStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(frameStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboColumn, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 167, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        frameStaffLayout.setVerticalGroup(
            frameStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameStaffLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTimeStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(frameStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboStaff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frameStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(frameStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(comboColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnLogOut, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(scrollStaff, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addContainerGap())
        );

        frameStaffLogin.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frameStaffLogin.setResizable(false);

        jLabel8.setFont(new java.awt.Font("Courier New", 3, 24)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Login Staff");

        jLabel9.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        jLabel9.setText("Staff_ID");

        jLabel10.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        jLabel10.setText("Password");

        txtStaffId.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        txtStaffId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnLoginStaffKeyReleased(evt);
            }
        });

        btnLoginStaff.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        btnLoginStaff.setText("Login");
        btnLoginStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginStaffActionPerformed(evt);
            }
        });
        btnLoginStaff.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnLoginStaffKeyReleased(evt);
            }
        });

        lblTimeLogin.setFont(new java.awt.Font("DigifaceWide", 3, 14)); // NOI18N
        lblTimeLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        txtPassword.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnLoginStaffKeyReleased(evt);
            }
        });

        lblCaps.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        lblCaps.setForeground(new java.awt.Color(255, 0, 0));
        lblCaps.setText("CAPS key is On");

        btnBack.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        btnBack.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnBackKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout frameStaffLoginLayout = new javax.swing.GroupLayout(frameStaffLogin.getContentPane());
        frameStaffLogin.getContentPane().setLayout(frameStaffLoginLayout);
        frameStaffLoginLayout.setHorizontalGroup(
            frameStaffLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameStaffLoginLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(frameStaffLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTimeLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(frameStaffLoginLayout.createSequentialGroup()
                        .addGroup(frameStaffLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(frameStaffLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtStaffId, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                            .addComponent(txtPassword)))
                    .addGroup(frameStaffLoginLayout.createSequentialGroup()
                        .addComponent(lblCaps, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLoginStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        frameStaffLoginLayout.setVerticalGroup(
            frameStaffLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameStaffLoginLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(frameStaffLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtStaffId, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                .addGap(7, 7, 7)
                .addGroup(frameStaffLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(frameStaffLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLoginStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCaps, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBack))
                .addGap(6, 6, 6)
                .addComponent(lblTimeLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addContainerGap())
        );

        frameCreate.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frameCreate.setResizable(false);

        btncreate.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        btncreate.setText("CREATE");
        btncreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncreateActionPerformed(evt);
            }
        });
        btncreate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btncreateKeyReleased(evt);
            }
        });

        txtpin2.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        txtpin2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btncreateKeyReleased(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Repeat Pin");

        jLabel12.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Pin");

        txtpin.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        txtpin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btncreateKeyReleased(evt);
            }
        });

        comboType.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        comboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Business", "Personal", "Other" }));
        comboType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btncreateKeyReleased(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Acc. Type");

        jLabel14.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Name");

        txtName.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btncreateKeyReleased(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Acc. Number");

        txtAccountNumber.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        txtAccountNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btncreateKeyReleased(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Kristen ITC", 3, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("CREATE ACCOUNT");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        btnCancel.setFont(new java.awt.Font("Courier New", 3, 14)); // NOI18N
        btnCancel.setText("CANCEL");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        btnCancel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnCancelKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout frameCreateLayout = new javax.swing.GroupLayout(frameCreate.getContentPane());
        frameCreate.getContentPane().setLayout(frameCreateLayout);
        frameCreateLayout.setHorizontalGroup(
            frameCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameCreateLayout.createSequentialGroup()
                .addGroup(frameCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(frameCreateLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(btncreate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(frameCreateLayout.createSequentialGroup()
                        .addGroup(frameCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(frameCreateLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(frameCreateLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtAccountNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(frameCreateLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(frameCreateLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(frameCreateLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtpin, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(frameCreateLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtpin2, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 1, Short.MAX_VALUE)))
                .addContainerGap())
        );
        frameCreateLayout.setVerticalGroup(
            frameCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameCreateLayout.createSequentialGroup()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(frameCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(frameCreateLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtAccountNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frameCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(frameCreateLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(frameCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(frameCreateLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(frameCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(frameCreateLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtpin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(frameCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(frameCreateLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtpin2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(frameCreateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btncreate)
                    .addComponent(btnCancel))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        frameEnter.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frameEnter.setResizable(false);

        jLabel17.setFont(new java.awt.Font("Kristen ITC", 3, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("STATUS CHECK");

        txtAcc.setEditable(false);
        txtAcc.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        txtAcc.setForeground(new java.awt.Color(0, 0, 0));
        txtAcc.setFocusable(false);

        jLabel18.setFont(new java.awt.Font("Courier New", 3, 11)); // NOI18N
        jLabel18.setText("Acc. No");

        jLabel19.setFont(new java.awt.Font("Courier New", 3, 11)); // NOI18N
        jLabel19.setText("Name");

        txtNm.setEditable(false);
        txtNm.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        txtNm.setForeground(new java.awt.Color(0, 0, 0));
        txtNm.setFocusable(false);

        jLabel20.setFont(new java.awt.Font("Courier New", 3, 11)); // NOI18N
        jLabel20.setText("Type");

        txtType.setEditable(false);
        txtType.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        txtType.setForeground(new java.awt.Color(0, 0, 0));
        txtType.setFocusable(false);

        jLabel21.setFont(new java.awt.Font("Courier New", 3, 11)); // NOI18N
        jLabel21.setText("Balance");

        txtBalance.setEditable(false);
        txtBalance.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        txtBalance.setForeground(new java.awt.Color(0, 0, 0));
        txtBalance.setFocusable(false);

        jLabel22.setFont(new java.awt.Font("Courier New", 3, 11)); // NOI18N
        jLabel22.setText("Transaction");

        txtTransaction.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N

        btnDeposit.setText("Deposit");
        btnDeposit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepositActionPerformed(evt);
            }
        });
        btnDeposit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnDepositKeyReleased(evt);
            }
        });

        btnWithdraw.setText("Withdraw");
        btnWithdraw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepositActionPerformed(evt);
            }
        });
        btnWithdraw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnDepositKeyReleased(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Courier New", 3, 11)); // NOI18N
        jLabel23.setText("Change Type");

        comboStatus.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        comboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Business", "Personal", "Other" }));
        comboStatus.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                comboStatusPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        btnBack1.setText("Log Out");
        btnBack1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBack1ActionPerformed(evt);
            }
        });
        btnBack1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnBack1KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout frameEnterLayout = new javax.swing.GroupLayout(frameEnter.getContentPane());
        frameEnter.getContentPane().setLayout(frameEnterLayout);
        frameEnterLayout.setHorizontalGroup(
            frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameEnterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(frameEnterLayout.createSequentialGroup()
                        .addGroup(frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAcc)
                            .addComponent(txtNm)
                            .addComponent(txtType)
                            .addComponent(txtBalance)
                            .addGroup(frameEnterLayout.createSequentialGroup()
                                .addComponent(txtTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeposit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnWithdraw)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(comboStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, frameEnterLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnBack1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        frameEnterLayout.setVerticalGroup(
            frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameEnterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeposit)
                    .addComponent(btnWithdraw))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frameEnterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(comboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnBack1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Courier New", 3, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Login");

        jLabel2.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        jLabel2.setText("Account No.");

        jLabel3.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        jLabel3.setText("Pin");

        txtAccount.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        txtAccount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnLoginKeyReleased(evt);
            }
        });

        btnLogin.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        btnLogin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnLoginKeyReleased(evt);
            }
        });

        btnCreate.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        btnCreate.setText("Create");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });
        btnCreate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnCreateKeyReleased(evt);
            }
        });

        btnStaff.setBackground(new Color(0,0,0,64));
        btnStaff.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        btnStaff.setForeground(new java.awt.Color(204, 0, 0));
        btnStaff.setText("Staff");
        btnStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStaffActionPerformed(evt);
            }
        });
        btnStaff.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnStaffKeyReleased(evt);
            }
        });

        lblTime.setFont(new java.awt.Font("DigifaceWide", 3, 14)); // NOI18N
        lblTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        txtPin.setFont(new java.awt.Font("Courier New", 3, 12)); // NOI18N
        txtPin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnLoginKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAccount)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtPin))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(103, 103, 103))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCreate, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnStaff)
                .addGap(18, 18, 18)
                .addComponent(lblTime, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        if(txtAccount.getText().isEmpty()||txtPin.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"No field can be Empty","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        accountNumber=txtAccount.getText().trim();
        pin=txtPin.getText();
        checkViabilityForLogin();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnLoginKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnLoginKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            btnLogin.doClick();
        }else if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
    }//GEN-LAST:event_btnLoginKeyReleased

    private void btnStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStaffActionPerformed
        lblCaps.setVisible(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK));
        setVisible(false);
        frameStaffLogin.setVisible(true);
        txtStaffId.setText("");
        txtStaffId.requestFocus();
        txtPassword.setText("");
    }//GEN-LAST:event_btnStaffActionPerformed

    private void btnLoginStaffKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnLoginStaffKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            btnLoginStaff.doClick();
        }else if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
        }else if(evt.getKeyCode()==KeyEvent.VK_CAPS_LOCK){
            lblCaps.setVisible(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK));
        }
    }//GEN-LAST:event_btnLoginStaffKeyReleased

    private void btnLoginStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginStaffActionPerformed
        if(txtStaffId.getText().isEmpty()||txtPassword.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"No field can be Empty","Error",JOptionPane.ERROR_MESSAGE);
            txtStaffId.setText("");
            txtPassword.setText("");
            txtStaffId.requestFocus();
            return;
        }
        staffId=txtStaffId.getText().trim();
        password=txtPassword.getText();
        checkViabilityForStaff();
    }//GEN-LAST:event_btnLoginStaffActionPerformed

    private void btnStaffKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnStaffKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            btnStaff.doClick();
        }else if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
    }//GEN-LAST:event_btnStaffKeyReleased

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        frameStaffLogin.setVisible(false);
        setVisible(true);
        txtAccount.setText("");
        txtPin.setText("");
        txtAccount.requestFocus();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnBackKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnBackKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            btnBack.doClick();
        }else if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
        }else if(evt.getKeyCode()==KeyEvent.VK_CAPS_LOCK){
            lblCaps.setVisible(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK));
        }
    }//GEN-LAST:event_btnBackKeyReleased

    private void comboStaffPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_comboStaffPopupMenuWillBecomeInvisible
        DefaultTableModel temp=(DefaultTableModel)tableStaff.getModel();
        temp.setRowCount(0);
        temp.setColumnCount(0);
        setData();
    }//GEN-LAST:event_comboStaffPopupMenuWillBecomeInvisible

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if(comboColumn.getSelectedItem().toString().equalsIgnoreCase("not Selected")){
            setModel();
        }
        filter();
    }//GEN-LAST:event_txtFilterKeyReleased

    private void btnLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogOutActionPerformed
        frameStaff.setVisible(false);
        DefaultTableModel temp=(DefaultTableModel)tableStaff.getModel();
        temp.setColumnCount(0);
        temp.setRowCount(0);
        frameStaffLogin.setVisible(true);
        txtStaffId.setText("");
        txtPassword.setText("");
        txtStaffId.requestFocus();
    }//GEN-LAST:event_btnLogOutActionPerformed

    private void btnLogOutKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnLogOutKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            btnLogOut.doClick();
        }else if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
    }//GEN-LAST:event_btnLogOutKeyReleased

    private void btncreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncreateActionPerformed
        try{
            String account=txtAccountNumber.getText().trim();
            String name=txtName.getText().trim();
            String type=comboType.getSelectedItem().toString();
            String Pin=txtpin.getText();
            String Pin2=txtpin2.getText();
            if(name.isEmpty()||type.isEmpty()||Pin.isEmpty()||Pin2.isEmpty()){
                JOptionPane.showMessageDialog(null,"A required field is empty","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(!Pin.equals(Pin2)){
                JOptionPane.showMessageDialog(null,"Pins do not match","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(Pin.length()!=4){
                JOptionPane.showMessageDialog(null,"Pin has to be 4 characters long","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(account.isEmpty()){
                set=stmt.executeQuery("select * from accounts order by acc_no asc");
                set.last();
                int temp=Integer.parseInt(set.getObject(1).toString());
                temp++;
                account=String.valueOf(temp);
                JOptionPane.showMessageDialog(null,"Your Account Number is"+account,"Information",JOptionPane.INFORMATION_MESSAGE);
            }
            if(account.length()!=6 && !txtAccountNumber.getText().equals("")){
                JOptionPane.showMessageDialog(null,"Account Number must be 6 digits","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            Integer.parseInt(account);
            Integer.parseInt(Pin);
            pst.setInt(1,Integer.parseInt(account));
            pst.setObject(2,name);
            pst.setObject(3,type);
            pst.setInt(4,0);
            pst.setObject(5,lblTime.getText());
            pst.setInt(6,Integer.parseInt(Pin));
            pst.execute();
            frameCreate.setVisible(false);
            setVisible(true);
            txtAccount.setText(account);
            txtPin.setText("");
            txtPin.requestFocus();
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null,"Account Number and Pin Number have to be numbers!!","Error",JOptionPane.ERROR_MESSAGE);
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null,sqle.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btncreateActionPerformed

    private void btncreateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btncreateKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            btncreate.doClick();
        }else if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
    }//GEN-LAST:event_btncreateKeyReleased

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        setVisible(false);
        txtAccountNumber.setText("");
        txtAccountNumber.requestFocus();
        txtName.setText("");
        txtpin.setText("");
        txtpin2.setText("");
        frameCreate.setVisible(true);
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnCreateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCreateKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            btnCreate.doClick();
        }else if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
    }//GEN-LAST:event_btnCreateKeyReleased

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        frameCreate.setVisible(false);
        setVisible(true);
        txtAccount.setText("");
        txtPin.setText("");
        txtAccount.requestFocus();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnCancelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCancelKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            btnCancel.doClick();
        }else if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
    }//GEN-LAST:event_btnCancelKeyReleased

    private void btnDepositActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDepositActionPerformed
        if(evt.getSource().equals(btnDeposit)){
            String amount=txtTransaction.getText().trim();
            try{
                set=stmt.executeQuery("select * from accounts where acc_no="+Integer.parseInt(accountNumber));
                set.next();
                int initial=Integer.parseInt(set.getObject(4).toString());
                int amt=Integer.parseInt(amount);
                int balance=initial+amt;
                stmt.executeUpdate("update accounts set balance="+balance+" where acc_no="+Integer.parseInt(accountNumber));
                txtBalance.setText(String.valueOf(balance));
                txtTransaction.setText("");
                set=stmt.executeQuery("select * from log order by log_id asc");
                set.last();
                int temp=Integer.parseInt(set.getObject(1).toString());
                temp++;
                int logID=temp;
                int accno=Integer.parseInt(accountNumber);
                stmt.executeUpdate("insert into log(log_id,acc_no,transactions,amount,time) values ("+logID+","+accno+",'Deposit',"+amt+",'"+lblTime.getText()+"')");
            }catch(NumberFormatException nfe){
                JOptionPane.showMessageDialog(null,"Use Numbers for Transactions","Error",JOptionPane.ERROR_MESSAGE);
            }catch(SQLException sqle){
                JOptionPane.showMessageDialog(null,sqle.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            String amount=txtTransaction.getText().trim();
            try{
                set=stmt.executeQuery("select * from accounts where acc_no="+Integer.parseInt(accountNumber));
                set.next();
                int initial=Integer.parseInt(set.getObject(4).toString());
                int amt=Integer.parseInt(amount);
                int balance=initial-amt;
                if(balance<0){
                    JOptionPane.showMessageDialog(null,"You cannot withdraw more than you have","Error",JOptionPane.ERROR_MESSAGE);
                    txtTransaction.setText("");
                    return;
                }
                stmt.executeUpdate("update accounts set balance="+balance+" where acc_no="+Integer.parseInt(accountNumber));
                txtBalance.setText(String.valueOf(balance));
                txtTransaction.setText("");
                set=stmt.executeQuery("select * from log order by log_id asc");
                set.last();
                int temp=Integer.parseInt(set.getObject(1).toString());
                temp++;
                int logID=temp;
                int accno=Integer.parseInt(accountNumber);
                stmt.executeUpdate("insert into log(log_id,acc_no,transactions,amount,time) values ("+logID+","+accno+",'Withdraw',"+amt+",'"+lblTime.getText()+"')");
            }catch(NumberFormatException nfe){
                JOptionPane.showMessageDialog(null,"Use Numbers for Transactions","Error",JOptionPane.ERROR_MESSAGE);
            }catch(SQLException sqle){
                JOptionPane.showMessageDialog(null,sqle.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnDepositActionPerformed

    private void btnDepositKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDepositKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            JButton temp=(JButton)evt.getSource();
            temp.doClick();
        }else if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
    }//GEN-LAST:event_btnDepositKeyReleased

    private void comboStatusPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_comboStatusPopupMenuWillBecomeInvisible
        String type=comboStatus.getSelectedItem().toString();
        try{
            stmt.executeUpdate("update accounts set type='"+type+"' where acc_no="+Integer.parseInt(accountNumber));
            txtType.setText(type);
            txtTransaction.requestFocus();
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null,sqle.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_comboStatusPopupMenuWillBecomeInvisible

    private void btnBack1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBack1ActionPerformed
        frameEnter.setVisible(false);
        txtAccount.setText("");
        txtPin.setText("");
        txtAccount.requestFocus();
        setVisible(true);
    }//GEN-LAST:event_btnBack1ActionPerformed

    private void btnBack1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnBack1KeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            JButton temp=(JButton)evt.getSource();
            temp.doClick();
        }else if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
    }//GEN-LAST:event_btnBack1KeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BankingDatabase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BankingDatabase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BankingDatabase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BankingDatabase.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BankingDatabase();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnBack1;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDeposit;
    private javax.swing.JButton btnLogOut;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnLoginStaff;
    private javax.swing.JButton btnStaff;
    private javax.swing.JButton btnWithdraw;
    private javax.swing.JButton btncreate;
    private javax.swing.JComboBox<String> comboColumn;
    private javax.swing.JComboBox<String> comboStaff;
    private javax.swing.JComboBox<String> comboStatus;
    private javax.swing.JComboBox<String> comboType;
    private javax.swing.JFrame frameCreate;
    private javax.swing.JFrame frameEnter;
    private javax.swing.JFrame frameStaff;
    private javax.swing.JFrame frameStaffLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblCaps;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTimeLogin;
    private javax.swing.JLabel lblTimeStaff;
    private javax.swing.JScrollPane scrollStaff;
    private javax.swing.JTable tableStaff;
    private javax.swing.JTextField txtAcc;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtAccountNumber;
    private javax.swing.JTextField txtBalance;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNm;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtPin;
    private javax.swing.JTextField txtStaffId;
    private javax.swing.JTextField txtTransaction;
    private javax.swing.JTextField txtType;
    private javax.swing.JPasswordField txtpin;
    private javax.swing.JPasswordField txtpin2;
    // End of variables declaration//GEN-END:variables
}
