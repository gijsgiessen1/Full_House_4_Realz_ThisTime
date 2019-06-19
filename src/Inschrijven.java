import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.*;

public class Inschrijven extends JFrame implements ActionListener {
    //Labels
    private JLabel naamLabel = new JLabel("Naam: ");
    private JLabel rankingLabel = new JLabel("Ranking: ");
    private JLabel typeLabel = new JLabel("Type inschrijving: ");
    private JLabel codeLabel = new JLabel ("Code: ");
    private JLabel heeftBetaaldLabel = new JLabel("Heeft betaald: ");

    //Textfields
    private JTextField naamField = new JTextField();
    private JTextField rankingField = new JTextField();
    private JTextField typeField = new JTextField();
    private JTextField codeField = new JTextField();
    private JTextField heeftBetaaldField = new JTextField();

    //Buttons
    private JButton terugButton = new JButton("Terug");
    private JButton klaarButton = new JButton("Klaar");
    private JButton rankingButton = new JButton("Krijg Ranking");

    public Inschrijven(){
        setTitle("Inschrijven");
        setLayout(null);
        setVisible(true);
        setSize(400, 360);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setComponentBounds();
        addComponents();
        addActionListeners();

    }

    public void emptyTextField(){
        naamField.setText("");
        rankingField.setText("");
        typeField.setText("");
        codeField.setText("");
        heeftBetaaldField.setText("");
    }

    public void setComponentBounds(){
        naamLabel.setBounds(40,10,100,40);
        rankingLabel.setBounds(40,60,100,40);
        typeLabel.setBounds(40,110,100,40);
        codeLabel.setBounds(40,160,200,40);
        heeftBetaaldLabel.setBounds(40, 210, 200, 40);

        naamField.setBounds(250, 10, 100, 40);
        rankingField.setBounds(250, 60, 100, 40);
        typeField.setBounds(250, 110, 100, 40);
        codeField.setBounds(250, 160, 100, 40);
        heeftBetaaldField.setBounds(250, 210, 100, 40);

        terugButton.setBounds(275,260,75,40);
        klaarButton.setBounds(175,260,100,40);
        rankingButton.setBounds(50,260,125,40);



    }

    public void addComponents(){
        add(naamLabel);
        add(rankingLabel);
        add(typeLabel);
        add(codeLabel);
        add(heeftBetaaldLabel);

        add(naamField);
        add(rankingField);
        add(typeField);
        add(codeField);
        add(heeftBetaaldField);

        add(klaarButton);
        add(terugButton);
        add(rankingButton);
    }

    public void addActionListeners(){
        terugButton.addActionListener(this);
        klaarButton.addActionListener(this);
        rankingButton.addActionListener(this);
    }

    public void countSpelers(){
        try {
            Connection con = Main.getConnection();
            Statement st = con.createStatement();
            String sql = ("SELECT COUNT (*) as geteld FROM Inschrijvingen where nummercode like " + codeField.getText() + " and type_inschrijving like '" + typeField.getText() + "';" );
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()){
                int geteld = rs.getInt("geteld");
                if (typeField.getText().equals ("Toernooi")) {
                    try {
                        Connection con2 = Main.getConnection();
                        PreparedStatement add = con2.prepareStatement("UPDATE Toernooi SET aantal_spelers = " + geteld + " where TC = " + codeField.getText());
                        add.executeUpdate();
                    } catch (Exception e) {
                        System.out.println(e);
                        System.out.println("ERROR: er ging iets mis met de database(updateAantalSpelers)");
                    }
                }
                else if(typeField.getText().equals("Masterclass")){
                    try {
                        Connection con2 = Main.getConnection();
                        PreparedStatement add = con2.prepareStatement("UPDATE Masterclass SET aantal_spelers = " + geteld + " where MasterclassCode = " + codeField.getText());
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


    public boolean addInschrijving(){
        if(naamField.getText().equals("") || rankingField.getText().equals("") || typeField.getText().equals("") || codeField.getText().equals("") || heeftBetaaldField.getText().equals("")){
            return false;
        } else {
            try {
                Connection con = Main.getConnection();
                PreparedStatement add = con.prepareStatement("INSERT INTO Inschrijvingen (naam, ranking, type_inschrijving, nummercode, heeft_betaald) VALUES ('" + naamField.getText() + "', '" + rankingField.getText() + "', '" + typeField.getText() + "', '" + codeField.getText() + "', '" + heeftBetaaldField.getText() + "');");
                add.executeUpdate();
                return true;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return false;
    }

    public boolean inschrijfControle(){
        try {
            Connection con = Main.getConnection();
            PreparedStatement st = con.prepareStatement("SELECT COUNT (*) as aantal FROM Inschrijvingen WHERE naam LIKE '" + naamField.getText() + "' AND nummercode LIKE " + codeField.getText());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("aantal");
                if (id < 1) {
                    return false;
                }
            }
        }catch(Exception e){
            System.out.println(e);
            System.out.println("ERROR: er is een probleem met de database(inschrijfControle)");
        }
        return true;
    }

    public String getGeslacht(){
        try {
            Connection con = Main.getConnection();
            Statement st = con.createStatement();
            String sql = ("SELECT geslacht FROM Spelers WHERE naam LIKE '" + naamField.getText() + "'; ");
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                String geslacht = rs.getString("geslacht");
                System.out.println(geslacht);
                return geslacht;
                }

        }catch(Exception e){
            System.out.println(e);
            System.out.println("ERROR: er is een probleem met de database (geslacht)");
        }

   return "poepieScheetje";}

    public String getToernooiSoort(){
        try {
            Connection con = Main.getConnection();
            Statement st = con.createStatement();
            String sql = ("SELECT soort_toernooi FROM Toernooi WHERE TC LIKE '" + Integer.valueOf(codeField.getText()) + "'; ");
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                String toernooiSoort = rs.getString("soort_toernooi");
                System.out.println(toernooiSoort);
                return toernooiSoort;
            }

        }catch(Exception e){
            System.out.println(e);
            System.out.println("ERROR: er is een probleem met de database");
        }

        return "poepieScheetje";}

        public boolean validateGeslacht(){
        String geslacht = getGeslacht();
        String toernooiSoort = getToernooiSoort();

        if(!geslacht.equals("F") && toernooiSoort.equals("PinkRibbon")){
            return false;
        }
       else if(geslacht.equals("poepieScheetje") || toernooiSoort.equals("askjeBlap")){
            return true;
        }
        else{return true;}
        }

    public int getRanking(){
        String naam = naamField.getText();
        int ranking=0;
        try {
            Connection con = Main.getConnection();
            PreparedStatement state = con.prepareStatement("SELECT ranking FROM Spelers WHERE idcode = '"+naam+"'");
            ResultSet rs= state.executeQuery();
            if(rs.next()) {
                return rs.getInt("ranking");
            }
        }catch(Exception e) {
            System.out.println(e);
        }
        return ranking;
    }

    public int getMaxAantalInschrijvingen(){
        try {
            Connection con = Main.getConnection();
            Statement st = con.createStatement();
            PreparedStatement state = con.prepareStatement("SELECT COUNT (*) as aantal from Inschrijvingen where type_inschrijving like '" + typeField.getText() + "' and nummercode like "+ codeField.getText());
            ResultSet rs = state.executeQuery();
            if (rs.next()) {
                int aantal = rs.getInt("aantal");
                return aantal;
                }
            }
        catch(Exception e){
            System.out.println(e);
            System.out.println("ERROR: er is een probleem met de database (maxAantalInschrijvingen)");
        }
        return 0;
    }

    public int getMaxAantal(){
        try {
            Connection con = Main.getConnection();
            Statement st = con.createStatement();
            String sql = ("SELECT type_inschrijving from Inschrijvingen;");
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                String result = rs.getString("type_inschrijving");
                if (result.equals("Toernooi")){
                    Statement stT = con.createStatement();
                    String sqlT = ("SELECT maximaal_aantal_spelers as max FROM Toernooi WHERE TC LIKE " + codeField.getText());
                    ResultSet rsT = stT.executeQuery(sqlT);
                    if(rsT.next()){
                        int max = rsT.getInt("max");
                        return max;
                    }
                }
                if (result.equals("Masterclass")){
                    Statement stM = con.createStatement();
                    String sqlM = ("SELECT max_aantal_spelers AS max FROM Masterclass WHERE MasterclassCode LIKE" + codeField.getText());
                    ResultSet rsM = stM.executeQuery(sqlM);
                    if(rsM.next()){
                        int max = rsM.getInt("max");
                        return max;
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e);
            System.out.println("ERROR: er is iets mis met de database (maxAantal)");
        }
        return 0;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == terugButton){
            dispose();
            RegistratieMenu menu = new RegistratieMenu();
        }
        if(e.getSource() == klaarButton){
            if (inschrijfControle()){
                JOptionPane.showMessageDialog(this, "ERROR: deze speler is hiervoor al ingeschreven");
            }
            else if(!validateGeslacht()){
                JOptionPane.showMessageDialog(this, "Een man mag zich niet inschrijven voor een Pink Ribbon toernooi");
            }
            else if(getMaxAantalInschrijvingen() > getMaxAantal()){
                JOptionPane.showMessageDialog(this, "Het maximum aantal spelers is al ingeschreven voor dit toernooi");
            }
            else if (addInschrijving()){
                countSpelers();
                JOptionPane.showMessageDialog(this, "Inschrijving toegevoegd");
                emptyTextField();
            } else {
                JOptionPane.showMessageDialog(this, "Niet alles is ingevuld!");
            }
        }
        if(e.getSource() == rankingButton){
            rankingField.setText(Integer.toString(getRanking()));
        }

    }
}
