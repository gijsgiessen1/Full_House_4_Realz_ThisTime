import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.*;

public class Inschrijven extends JFrame implements ActionListener {

    private int currentMaxRanking = -1;
    //Labels
    private JLabel spelerIDLabel = new JLabel("Spelercode: ");
    private JLabel typeLabel = new JLabel("Type inschrijving: ");
    private JLabel codeLabel = new JLabel ("Code: ");
    private JLabel heeftBetaaldLabel = new JLabel("Heeft betaald: ");

    //Textfields
    private JTextField spelerIDField= new JTextField();
    private JTextField typeField = new JTextField();
    private JTextField codeField = new JTextField();
    private JTextField heeftBetaaldField = new JTextField();

    //Buttons
    private JButton terugButton = new JButton("Terug");
    private JButton klaarButton = new JButton("Klaar");

    // constructor

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

    // this empties all the textfields active on this frame
    public void emptyTextField(){
        spelerIDField.setText("");
        typeField.setText("");
        codeField.setText("");
        heeftBetaaldField.setText("");
    }

    // this places the components on the frame and sets their size
    public void setComponentBounds(){
        spelerIDLabel.setBounds(40,10,100,40);
        typeLabel.setBounds(40,60,100,40);
        codeLabel.setBounds(40,110,200,40);
        heeftBetaaldLabel.setBounds(40, 160, 200, 40);

        spelerIDField.setBounds(250, 10, 100, 40);
        typeField.setBounds(250, 60, 100, 40);
        codeField.setBounds(250, 110, 100, 40);
        heeftBetaaldField.setBounds(250, 160, 100, 40);

        terugButton.setBounds(300,260,75,40);
        klaarButton.setBounds(200,260,100,40);




    }
// this adds the components to this JFrame
    public void addComponents(){
        add(spelerIDLabel);
        add(typeLabel);
        add(codeLabel);
        add(heeftBetaaldLabel);

        add(spelerIDField);
        add(typeField);
        add(codeField);
        add(heeftBetaaldField);

        add(klaarButton);
        add(terugButton);

    }
// this adds the action listeners to the buttons
    public void addActionListeners(){
        terugButton.addActionListener(this);
        klaarButton.addActionListener(this);

    }
// this counts the number of players in a particular toernooi
    public void countSpelers() {
        if (typeField.getText().equals("Toernooi")) {
            try {
                Connection con = Main.getConnection();
                PreparedStatement st = con.prepareStatement("SELECT COUNT (*) as geteld FROM Inschrijvingen where toernooi = ?");
                st.setInt(1, Integer.parseInt(codeField.getText()));
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    int geteld = rs.getInt("geteld");
                    try {
                        Connection con2 = Main.getConnection();
                        PreparedStatement update = con2.prepareStatement("UPDATE Toernooi SET aantal_spelers = ? where TC = ?");
                        update.setInt(1, geteld);
                        update.setInt(1, Integer.valueOf(codeField.getText()));
                        update.executeUpdate();
                    } catch (Exception e) {
                        System.out.println(e);
                        System.out.println("ERROR: er ging iets mis met de database(updateAantalSpelers)");
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ERROR: er ging iets mis met de database(updateAantalSpelers)");
            }
        } else if (typeField.getText().equals("Masterclass")) {
            try {
                Connection con = Main.getConnection();
                PreparedStatement st = con.prepareStatement("SELECT COUNT (*) as geteld FROM Inschrijvingen where toernooi = ?");
                st.setInt(1, Integer.parseInt(codeField.getText()));
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    int geteld = rs.getInt("geteld");
                    try {
                        Connection con2 = Main.getConnection();
                        PreparedStatement update = con2.prepareStatement("UPDATE Masterclass SET aantal_spelers = ? where MasterclassCode = ?");
                        update.setInt(1, geteld);
                        update.setInt(2, Integer.parseInt(codeField.getText()));
                        update.executeUpdate();
                    } catch (Exception e) {
                        System.out.println(e);
                        System.out.println("ERROR: er ging iets mis met de database(updateAantalSpelers)");
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ERROR: er is een probleem met de database (countSpelers)");
            }
        }
    }

// this adds an inschrijving to a toernooi
    public void addInschrijving(){
            if(typeField.getText().equalsIgnoreCase("Toernooi")){
            try {
                Connection con = Main.getConnection();
                PreparedStatement add = con.prepareStatement("INSERT INTO Inschrijvingen (speler,  toernooi, heeft_betaald) VALUES (?,?,?);");
                add.setInt(1, Integer.valueOf(spelerIDField.getText()));
                add.setInt(2, Integer.valueOf(codeField.getText()));
                add.setString(3, heeftBetaaldField.getText());
                add.executeUpdate();
            } catch (Exception e) {
                System.out.println(e);
            }}
            else if(typeField.getText().equalsIgnoreCase("Masterclass")){
                try {
                    Connection con = Main.getConnection();
                    PreparedStatement add = con.prepareStatement("INSERT INTO Inschrijvingen (speler,  masterclass, heeft_betaald) VALUES (?,?,?);");
                    add.setInt(1, Integer.valueOf(spelerIDField.getText()));
                    add.setInt(2, Integer.valueOf(codeField.getText()));
                    add.setString(3, heeftBetaaldField.getText());
                    add.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e);}
            }
        }

// this checks if the masterclass or toernooi exists.
    public boolean inschrijfControle(){
        if(typeField.getText().equals("Toernooi")) {
            try {
                Connection con = Main.getConnection();
                PreparedStatement st = con.prepareStatement("SELECT COUNT (*) as aantal FROM Inschrijvingen WHERE speler = ? AND toernooi = ?");
                st.setInt(1, Integer.parseInt(spelerIDField.getText()));
                st.setInt(2, Integer.parseInt(codeField.getText()));
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("aantal");
                    if (id < 1) {
                        return false;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ERROR: er is een probleem met de database(inschrijfControleToernooi)");
            }
        }else if(typeField.getText().equals("Masterclass")){
            try{
                Connection con = Main.getConnection();
                PreparedStatement st = con.prepareStatement("SELECT COUNT (*) as aantal FROM Inschrijvingen WHERE speler = ? AND masterclass = ?");
                st.setInt(1, Integer.parseInt(spelerIDField.getText()));
                st.setInt(2, Integer.parseInt(codeField.getText()));
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("aantal");
                    if (id < 1) {
                        return false;
                    }
                }
            }catch(Exception e){
                System.out.println(e);
                System.out.println("ERROR: er is een probleem met de database(inschrijfControleMasterclass)");
            }
        }
        return true;
    }

// this gets the sex of the person you are registering to a toernooi or masterclass
    public String getGeslacht(){
        try {
            Connection con = Main.getConnection();
            PreparedStatement st = con.prepareStatement("SELECT geslacht FROM Spelers WHERE idcode = ?");
            st.setInt(1, Integer.parseInt(spelerIDField.getText()));
            ResultSet rs = st.executeQuery();
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

   //this gets the type of toernooi
    public String getToernooiSoort(){
        if(typeField.getText().equals("Toernooi")){
        try {
            Connection con = Main.getConnection();
            PreparedStatement st = con.prepareStatement("SELECT soort_toernooi FROM Toernooi WHERE TC = ?");
            st.setInt(1, Integer.parseInt(codeField.getText()));
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                String toernooiSoort = rs.getString("soort_toernooi");
                System.out.println(toernooiSoort);
                return toernooiSoort;
            }

        }catch(Exception e){
            System.out.println(e);
            System.out.println("ERROR: er is een probleem met de database(getToernooiSoort)");
        }
        }

        return "poepieScheetje";}

        // this checks if the sex of the player is consistent with the sex constraints of the type of toernooi

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

//        public int getRanking() {
//            int spelerRanking = -1;
//
//            try {
//                Connection conn = Main.getConnection();
//                PreparedStatement ps = conn.prepareStatement("SELECT idcode, ranking FROM Spelers WHERE idcode = ?");
//                ps.setInt(1, Integer.parseInt(codeField.getText()));
//                ResultSet rs = ps.executeQuery();
//                if (rs.next()) {
//                    int result = rs.getInt("idcode");
//                    if (result == Integer.parseInt(codeField.getText())) {
//                        PreparedStatement check = conn.prepareStatement("SELECT ranking FROM Spelers WHERE idcode = ?");
//                        check.setInt(1, Integer.parseInt(codeField.getText()));
//                        ResultSet resultaat = check.executeQuery();
//                        if (resultaat.next()) {
//                             spelerRanking = resultaat.getInt("ranking");
//                        }
//                    }
//                }
//
//            }catch (Exception e) {
//                e.printStackTrace();
//
//            }
//            System.out.println("SpelerRanking: "+spelerRanking);
//       return spelerRanking; }

// checks if a player can register for a masterclass based on ranking
    public boolean checkMaxRanking(){
        if(typeField.getText().equalsIgnoreCase("Masterclass")){
        try {
            Connection conn = Main.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT MasterclassCode, max_ranking FROM Masterclass WHERE MasterclassCode = ?");
            ps.setInt(1, Integer.parseInt(codeField.getText()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                int result = rs.getInt("MasterclassCode");
                if (result == Integer.parseInt(codeField.getText())){
                    PreparedStatement check = conn.prepareStatement("SELECT max_ranking FROM Masterclass WHERE MasterclassCode = ?");
                    check.setInt(1, Integer.parseInt(codeField.getText()));
                    ResultSet resultaat = check.executeQuery();
                    if (resultaat.next()){
                        this.currentMaxRanking = resultaat.getInt("max_ranking");
                        System.out.println("De max ranking is: " + currentMaxRanking);
                        System.out.println("De ranking van de speler is: " + getRanking());
                        if (currentMaxRanking <= getRanking() ){
                            System.out.println("Max ranking is: "+ currentMaxRanking +" De ranking van deze speler is: " + getRanking());
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }}
        return true;
    }

    // this checks if  all the textfields are being written on
    public boolean checkInput(){
        if(spelerIDField.getText().equals("") || typeField.getText().equals("") || codeField.getText().equals("") || heeftBetaaldField.getText().equals("")){
            return true;
        } else { return false; }
        }


// this is an old method that may come in handy some day, as such it is not yet deleted.

    public int getRanking(){
        int spelerID = Integer.valueOf(spelerIDField.getText());
        int ranking=0;
        try {
            Connection con = Main.getConnection();
            PreparedStatement st = con.prepareStatement("SELECT ranking FROM Spelers WHERE idcode = ?");
            st.setInt(1, spelerID);
            ResultSet rs= st.executeQuery();
            if(rs.next()) {
                return rs.getInt("ranking");
            }
        }catch(Exception e) {
            System.out.println(e);
        }
        return ranking;
    }



// this gets the maximum amount of players that can register for a particular toernooi

    public int getMaxAantalInschrijvingen(){
        if(typeField.getText().equalsIgnoreCase("Toernooi")) {
            try {
                Connection con = Main.getConnection();
                PreparedStatement st = con.prepareStatement("SELECT COUNT (*) as aantal from Inschrijvingen where toernooi = ?");
                st.setInt(1, Integer.parseInt(codeField.getText()));
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    int aantal = rs.getInt("aantal");
                    return aantal;
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ERROR: er is een probleem met de database (getMaxAantalInschrijvingenToernooi)");
            }
        }
        else if(typeField.getText().equalsIgnoreCase("Masterclass")){
            try {
                Connection con = Main.getConnection();
                PreparedStatement st = con.prepareStatement("SELECT COUNT (*) as aantal from Inschrijvingen where masterclass = ?");
                st.setInt(1, Integer.parseInt(codeField.getText()));
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    int aantal = rs.getInt("aantal");
                    return aantal;
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ERROR: er is een probleem met de database (getMaxAantalInschrijvingenMasterclass)");
            }
        }
        return 0;
    }

    public int getMaxAantal(){
        if(typeField.getText().equals("Toernooi")) {
            try {
                Connection con = Main.getConnection();
                PreparedStatement st = con.prepareStatement("SELECT maximaal_aantal_spelers as max FROM Toernooi WHERE TC = ?");
                st.setInt(1, Integer.parseInt(codeField.getText()));
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    int max = rs.getInt("max");
                    return max;
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ERROR: er is iets mis met de database(getMaxAantalToernooi)");
            }
        }

        else if(typeField.getText().equals("Masterclass")){
             try {
                 Connection con = Main.getConnection();
                 PreparedStatement st = con.prepareStatement("SELECT max_aantal_spelers AS max FROM Masterclass WHERE MasterclassCode = ?");
                 st.setInt(1, Integer.parseInt(codeField.getText()));
                 ResultSet rs = st.executeQuery();
                 if (rs.next()) {
                     int max = rs.getInt("max");
                     return max;
                 }
             }catch (Exception e){
                System.out.println(e);
                System.out.println("ERROR: er is iets mis met de database (getMaxAantalMasterclass)");
            }
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
            else if (checkInput()) {
                JOptionPane.showMessageDialog(this, "Niet alles is ingevuld!");
            }
            else if (!checkMaxRanking()){
                JOptionPane.showMessageDialog(this, "Ranking is hoger dan toegestaan in deze Masterclass, de maximale ranking is: " + currentMaxRanking
                + " De speler heeft een ranking van: "+ getRanking());
            } else {
                addInschrijving();
                countSpelers();
                JOptionPane.showMessageDialog(this, "Inschrijving toegevoegd");
                emptyTextField();
            }
        }


    }
}
