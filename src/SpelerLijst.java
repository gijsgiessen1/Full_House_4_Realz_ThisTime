import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SpelerLijst extends JFrame implements ActionListener {

    // initialize all the components
    DefaultTableModel model = new DefaultTableModel();
    Container cnt = this.getContentPane();
    JTable jtbl = new JTable(model);

    private TableRowSorter<TableModel> rowSorter = new TableRowSorter(jtbl.getModel());
    private JTextField jtfFilter = new JTextField();
    private JButton verwijderButton = new JButton("Verwijderen");
    private JButton wijzigButton = new JButton("Wijzigen");
    private JButton terugButton = new JButton("Terug");
    private JLabel searchLabel = new JLabel("Search: ");
    private JPanel searchPanel = new JPanel(new BorderLayout());
    private JPanel buttonPanel = new JPanel(new BorderLayout());

// the constructor
    public SpelerLijst(){
        jtbl.setRowSorter(rowSorter);

        buttonPanel.add(terugButton, BorderLayout.LINE_START);
        buttonPanel.add(verwijderButton, BorderLayout.CENTER);
        buttonPanel.add(wijzigButton, BorderLayout.LINE_END);

        searchPanel.add(jtfFilter,BorderLayout.CENTER);
        searchPanel.add(searchLabel, BorderLayout.LINE_START);
        searchPanel.add(buttonPanel, BorderLayout.LINE_END);
        cnt.setLayout(new BorderLayout());
        cnt.add(searchPanel,BorderLayout.SOUTH);
        showLijst();
        setTitle("Speler Lijst");
        setPreferredSize(new Dimension(1500, 1000));
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // the method for the search functionality
        jtfFilter.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }


        });

        addActionListeners();
        JScrollPane pg = new JScrollPane(jtbl);
        cnt.add(pg);
        this.pack();
    }

// method that shows the table with all the data for the user to view.
    public void showLijst(){
        model.addColumn("idcode");
        model.addColumn("naam");
        model.addColumn("adres");
        model.addColumn("Postcode");
        model.addColumn("Woonplaats");
        model.addColumn("Telefoonnummer");
        model.addColumn("E-Mail");
        model.addColumn("Geboortedatum");
        model.addColumn("Geslacht");
        model.addColumn("Ranking");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://meru.hhs.nl/18095240", "18095240", "Ene3shaise");
            PreparedStatement pstm = con.prepareStatement("SELECT * FROM Spelers");
            ResultSet Rs = pstm.executeQuery();
            while(Rs.next()){
                model.addRow(new Object[]{Rs.getString(1), Rs.getString(2),Rs.getString(3),Rs.getString(4),Rs.getString(5),Rs.getString(6),Rs.getString(7),Rs.getString(8),Rs.getString(9), Rs.getString(10)});
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    // delete a player from the database

    public void verwijderSpeler(){
        int row = jtbl.getSelectedRow();
        try {
            Connection con = Main.getConnection();
            PreparedStatement verwijder = con.prepareStatement("DELETE FROM Spelers WHERE idcode = ?");
            verwijder.setInt(1,Integer.parseInt(jtbl.getValueAt(row,0).toString()));
            verwijder.executeUpdate();
            verwijder.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // alter a player in the database
    public void wijzigSpeler(JTable table, int row){
        try{
            Connection con= Main.getConnection();
            PreparedStatement update = con.prepareStatement("UPDATE Spelers SET naam = ?, adres = ?, postcode = ?, woonplaats = ?, telefoonnr = ?, email = ?, geboortedatum = ?, geslacht = ?, ranking = ? WHERE idcode = ?");
            update.setString(1,jtbl.getValueAt(row,1).toString());
            update.setString(2,jtbl.getValueAt(row,2).toString());
            update.setString(3,jtbl.getValueAt(row,3).toString());
            update.setString(4,jtbl.getValueAt(row,4).toString());
            update.setString(5,jtbl.getValueAt(row,5).toString());
            update.setString(6,jtbl.getValueAt(row,6).toString());
            update.setDate(7,java.sql.Date.valueOf(jtbl.getValueAt(row,7).toString()));
            update.setString(8,jtbl.getValueAt(row,8).toString());
            update.setInt(9,Integer.parseInt(jtbl.getValueAt(row,9).toString()));
            update.setInt(10,Integer.parseInt(jtbl.getValueAt(row,0).toString()));
            update.executeUpdate();
            update.close();
        }catch(Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == verwijderButton) {
            verwijderSpeler();
            JOptionPane.showMessageDialog(this, "Speler verwijderd");
            dispose();
            SpelerLijst refresh = new SpelerLijst();
        }
        if(e.getSource() == terugButton){
            dispose();
            SpelerMenu spelerMenu = new SpelerMenu();
        }
        if(e.getSource() == wijzigButton) {
            wijzigSpeler(jtbl, jtbl.getSelectedRow());
            JOptionPane.showMessageDialog(this, "Speler gewijzigd");
            dispose();
            SpelerLijst refresh = new SpelerLijst();
        }
    }

    // adding the action listeners to the buttons
    public void addActionListeners(){
        verwijderButton.addActionListener(this);
        terugButton.addActionListener(this);
        wijzigButton.addActionListener(this);
    }
}






