import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InschrijvingenLijst extends JFrame implements ActionListener {
    DefaultTableModel model = new DefaultTableModel();
    Container cnt = this.getContentPane();
    JTable jtbl = new JTable(model);

// all the components are initialized
    private TableRowSorter<TableModel> rowSorter = new TableRowSorter(jtbl.getModel());
    private JTextField jtfFilter = new JTextField();
    private JButton verwijderButton = new JButton("Verwijderen");
    private JButton wijzigButton = new JButton("Wijzigen");
    private JButton terugButton = new JButton("Terug");
    private JLabel searchLabel = new JLabel("search: ");
    private JPanel searchPanel = new JPanel(new BorderLayout());
    private JPanel buttonPanel = new JPanel(new BorderLayout());

  // constructor
    public InschrijvingenLijst() {
        jtbl.setRowSorter(rowSorter);

        buttonPanel.add(terugButton, BorderLayout.LINE_START);
        buttonPanel.add(verwijderButton, BorderLayout.CENTER);
        buttonPanel.add(wijzigButton, BorderLayout.LINE_END);

        searchPanel.add(jtfFilter, BorderLayout.CENTER);
        searchPanel.add(searchLabel, BorderLayout.LINE_START);
        searchPanel.add(buttonPanel, BorderLayout.LINE_END);
        cnt.setLayout(new BorderLayout());
        cnt.add(searchPanel, BorderLayout.SOUTH);

        setTitle("Inschrijvingenlijst");
        setPreferredSize(new Dimension(1000, 500));
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // the search function
        jtfFilter.getDocument().addDocumentListener(new DocumentListener() {

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

// showing the table
    public void showLijst() {
        model.addColumn("Inschrijving");
        model.addColumn("SpelerID");
        model.addColumn("Toernooi");
        model.addColumn("Masterclass");
        model.addColumn("Heeft betaald");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://meru.hhs.nl/18095240", "18095240", "Ene3shaise");
            PreparedStatement pstm = con.prepareStatement("SELECT DISTINCT * FROM Inschrijvingen");
            ResultSet Rs = pstm.executeQuery();
            while (Rs.next()) {
                model.addRow(new Object[]{Rs.getString(1), Rs.getString(2), Rs.getString(3), Rs.getString(4), Rs.getString(5)});
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
// deleting a registration from the table
    public void verwijderRegistratie() {
        int row = jtbl.getSelectedRow();
        try {
            Connection con = Main.getConnection();
            PreparedStatement verwijder = con.prepareStatement("DELETE FROM Inschrijvingen WHERE Inschrijving = ?");
            verwijder.setInt(1,Integer.parseInt(jtbl.getValueAt(row,0).toString()));
            verwijder.executeUpdate();
            countSpelers();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    // alter the registrations

    public void wijzigRegistratie(JTable table, int row){
        try{
            Connection con= Main.getConnection();
            if(jtbl.getValueAt(row,2) == null ){
                PreparedStatement update = con.prepareStatement("UPDATE Inschrijvingen SET speler = ?, masterclass = ?, heeft_betaald = ? WHERE Inschrijving = ?");
                update.setInt(1, Integer.parseInt(jtbl.getValueAt(row, 1).toString()));
                update.setInt(2, Integer.parseInt(jtbl.getValueAt(row, 3).toString()));
                update.setString(3, jtbl.getValueAt(row, 4).toString());
                update.setInt(4, Integer.parseInt((jtbl.getValueAt(row, 0).toString())));
                update.executeUpdate();
                update.close();
            }
            else {

                PreparedStatement update = con.prepareStatement("UPDATE Inschrijvingen SET speler = ?, toernooi = ?, heeft_betaald = ? WHERE Inschrijving = ?");
                update.setInt(1, Integer.parseInt(jtbl.getValueAt(row,1).toString()));
                update.setInt(2,Integer.parseInt(jtbl.getValueAt(row,2).toString()));
                update.setString(3,jtbl.getValueAt(row,4).toString());
                update.setInt(4,Integer.parseInt(jtbl.getValueAt(row,0).toString()));
                update.executeUpdate();
                update.close();
                            }
        }catch(Exception e) {
            System.out.println(e);
        }
    }

    // adding action listeners to the buttons
    public void addActionlisteners() {
        verwijderButton.addActionListener(this);
        terugButton.addActionListener(this);
        wijzigButton.addActionListener(this);
    }

//counting the number of registered players in the toernooien
    public void countSpelers(){
            int nummercodeKolom = 0;
            int toernooiKolom = 4;
            int typeKolom = 3;
            int row = jtbl.getSelectedRow();
            int inschrijvingNummer = Integer.parseInt(jtbl.getModel().getValueAt(row, nummercodeKolom).toString());
            int toernooiNummer = Integer.parseInt(jtbl.getModel().getValueAt(row, toernooiKolom).toString());
            String typeNummer = (jtbl.getModel().getValueAt(row, typeKolom).toString());
            System.out.println(typeNummer);
            try {
                Connection con = Main.getConnection();
                PreparedStatement st = con.prepareStatement("SELECT COUNT (*) as geteld FROM Inschrijvingen where Inschrijving = ? AND nummercode = ? and type_inschrijving = ?");
                st.setInt(1,inschrijvingNummer);
                st.setInt(2,toernooiNummer);
                st.setString(3,typeNummer);
                ResultSet rs = st.executeQuery();
                if(rs.next()){
                    if (typeNummer.equals ("Toernooi")) {
                        try {
                            Connection con2 = Main.getConnection();
                            PreparedStatement add = con2.prepareStatement("UPDATE Toernooi SET aantal_spelers = (aantal_spelers - 1) where TC = ?");
                            add.setInt(1,toernooiNummer);
                            System.out.println(toernooiNummer);
                            add.executeUpdate();
                        } catch (Exception e) {
                            System.out.println(e);
                            System.out.println("ERROR: er ging iets mis met de database(updateAantalSpelers)");
                        }
                    }
                    else if (typeNummer.equals("Masterclass")){
                        try {
                            Connection con2 = Main.getConnection();
                            PreparedStatement add = con2.prepareStatement("UPDATE Masterclass SET aantal_spelers = (aantal_spelers - 1) where MasterclassCode = ?");
                            add.setInt(1,toernooiNummer);
                            System.out.println(toernooiNummer);
                            add.executeUpdate();
                        } catch (Exception e) {
                            System.out.println(e);
                            System.out.println("ERROR: er ging iets mis met de database(updateAantalSpelers)");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ERROR: er is een probleem met de database (countSpelers)");
            }
        }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == terugButton) {
            dispose();
            RegistratieMenu menu = new RegistratieMenu();
        }
        if(e.getSource() == verwijderButton){
            verwijderRegistratie();
            JOptionPane.showMessageDialog(this, "Inschrijving verwijderd");
            dispose();
            InschrijvingenLijst nieuw = new InschrijvingenLijst();
        }
        if(e.getSource() == wijzigButton){
            wijzigRegistratie(jtbl,jtbl.getSelectedRow());
            JOptionPane.showMessageDialog(this, "Inschrijving gewijzigd");
            dispose();
            InschrijvingenLijst nieuw = new InschrijvingenLijst();
        }
    }
}
