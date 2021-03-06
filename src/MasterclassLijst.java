import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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



    public class MasterclassLijst extends JFrame implements ActionListener {

// initializing all the components

        DefaultTableModel model = new DefaultTableModel();
        Container cnt = this.getContentPane();
        JTable jtbl = new JTable(model);

        private TableRowSorter<TableModel> rowSorter = new TableRowSorter(jtbl.getModel());
        private JTextField jtfFilter = new JTextField();
        private JButton verwijderButten = new JButton("Verwijderen");
        private JButton wijzigButton = new JButton("Wijzigen");
        private JButton terugButton = new JButton ("Terug");
        private JLabel searchLabel = new JLabel("search: ");
        private JPanel searchPanel = new JPanel(new BorderLayout());
        private JPanel buttonPanel = new JPanel(new BorderLayout());

       // constructor
        public MasterclassLijst(){
            jtbl.setRowSorter(rowSorter);

            buttonPanel.add(terugButton, BorderLayout.LINE_START);
            buttonPanel.add(verwijderButten, BorderLayout.CENTER);
            buttonPanel.add(wijzigButton, BorderLayout.LINE_END);

            searchPanel.add(jtfFilter,BorderLayout.CENTER);
            searchPanel.add(searchLabel, BorderLayout.LINE_START);
            searchPanel.add(buttonPanel, BorderLayout.LINE_END);
            cnt.setLayout(new BorderLayout());
            cnt.add(searchPanel,BorderLayout.SOUTH);

            setTitle("Masterclass Lijst");
            setPreferredSize(new Dimension(1000, 500));

            setVisible(true);
            setResizable(false);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            // methods for the search functionality
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
            showLijst();
            addActionlisteners();
            JScrollPane pg = new JScrollPane(jtbl);
            cnt.add(pg);
            this.pack();
        }

        // method to show the table that holds the data for the user to view
        public void showLijst(){
            model.addColumn("MasterclassCode");
            model.addColumn("Datum");
            model.addColumn("Begintijd");
            model.addColumn("Eindtijd");
            model.addColumn("Kosten");
            model.addColumn("Max ranking");
            model.addColumn("Bekende speler");
            model.addColumn("Max aantal spelers");
            model.addColumn("Aantal spelers");

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://meru.hhs.nl/18095240", "18095240", "Ene3shaise");
                PreparedStatement pstm = con.prepareStatement("SELECT * FROM Masterclass");
                ResultSet Rs = pstm.executeQuery();
                while(Rs.next()){
                    model.addRow(new Object[]{Rs.getString(1), Rs.getString(2),Rs.getString(3),Rs.getString(4),Rs.getString(5),Rs.getString(6), Rs.getString(7), Rs.getString(8), Rs.getString(9)});
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        // method to alter data in the masterclass table
        public void wijzigMasterclass(JTable table, int row){
            try{
                Connection con= Main.getConnection();
                PreparedStatement update = con.prepareStatement("UPDATE Masterclass SET datum = ?, begintijd = ?, eindtijd = ?, kosten = ?, max_ranking = ?, bekende_speler = ?, max_aantal_spelers = ?, aantal_spelers = ? WHERE MasterclassCode = ?");
                update.setDate(1,java.sql.Date.valueOf(jtbl.getValueAt(row,1).toString()));
                update.setTime(2,java.sql.Time.valueOf(jtbl.getValueAt(row,2).toString()));
                update.setTime(3,java.sql.Time.valueOf(jtbl.getValueAt(row,3).toString()));
                update.setDouble(4,Double.parseDouble(jtbl.getValueAt(row,4).toString()));
                update.setInt(5,Integer.parseInt(jtbl.getValueAt(row,5).toString()));
                update.setString(6,jtbl.getValueAt(row,6).toString());
                update.setInt(7,Integer.parseInt(jtbl.getValueAt(row,7).toString()));
                update.setInt(8,Integer.parseInt(jtbl.getValueAt(row,8).toString()));
                update.setString(9,jtbl.getValueAt(row,0).toString());
                update.executeUpdate();
                update.close();

            }catch(Exception e) {
                System.out.println(e);
            }
        }

        //method to delete data from the masterclass table
        public void verwijderMasterclass(){

            int row = jtbl.getSelectedRow();
            try{
                Connection con = Main.getConnection();
                PreparedStatement verwijder = con.prepareStatement("DELETE FROM Masterclass WHERE MasterclassCode = ?");
                verwijder.setInt(1,Integer.parseInt(jtbl.getValueAt(row,0).toString()));
                verwijder.executeUpdate();
            }catch(Exception e){
                System.out.println(e);
            }
        }
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == verwijderButten) {
                verwijderMasterclass();
                JOptionPane.showMessageDialog(this, "Masterclass verwijderd");
                dispose();
                MasterclassLijst refresh = new MasterclassLijst();
            }
            if(e.getSource() == terugButton) {
                dispose();
                MasterclassMenu menu = new MasterclassMenu();
            }
            if(e.getSource() == wijzigButton) {
                wijzigMasterclass(jtbl,jtbl.getSelectedRow());
                JOptionPane.showMessageDialog(this, "Masterclass gewijzigd");
                dispose();
                MasterclassLijst refresh = new MasterclassLijst();
            }
        }

        // adding action listeners to the buttons
        public void addActionlisteners(){
            verwijderButten.addActionListener(this);
            terugButton.addActionListener(this);
            wijzigButton.addActionListener(this);
        }

    }


