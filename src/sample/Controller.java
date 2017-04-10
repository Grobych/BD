package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import oracle.sql.DATE;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Controller {

    @FXML
    Button showConcertsButton, addGroupButton, addPlaceButton, registerOrgButton, statisticButton, loginButton, logoutButton, addConcertButton;
    @FXML
    TextField groupNameTxt, groupStyleTxt, placeNameTxt, placeAddTxt, nameConcertTxt, groupConcertTxt, costTxt, placeConcertTxt, orgConcertTxt;
    @FXML
    TableView <Concert> table;
    @FXML
    TableColumn <Concert, String> tableDate;
    @FXML
    TableColumn <Concert, String> tableAdd;
    @FXML
    TableColumn <Concert, String> tablePlace;
    @FXML
    TableColumn <Concert, String> tableGroup;
    @FXML
    TableColumn <Concert, String> tableName;
    @FXML
    TableColumn <Concert, Integer> tableID;
    @FXML
    TableColumn <Concert, Integer> tableCost;
    @FXML
    PieChart orgChart;
    @FXML
    PasswordField passwordField;
    @FXML
    Label passLabel, reportLabel, groupPlaceLabel;
    @FXML
    Tab loginTab, concertTab, OrgTab, PlaceGroup;
    @FXML
    DatePicker datePicker;

    Connection connection;
    Statement statement;

    private ObservableList<Concert> concerts = FXCollections.observableArrayList();
    private ObservableList<Organizer> organizers = FXCollections.observableArrayList();

    @FXML
    public void loginButtonClick() {
        passwordField.setStyle("-fx-border-color: none;");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver Not Found!");
            e.printStackTrace();
            return;
        }
        try {
            Locale.setDefault(Locale.ENGLISH);
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","Globa",passwordField.getText());
            if (!connection.isClosed())
            {
                passLabel.setText("Connection has successfully established!");
                concertTab.setDisable(false);
                OrgTab.setDisable(false);
                PlaceGroup.setDisable(false);
            }

        } catch (SQLException e) {
            //e.printStackTrace();
            if (e.getMessage().compareTo("ORA-01017: invalid username/password")!=0)
            {
                passwordField.setStyle("-fx-border-color: red;");
                passLabel.setText("Wrong password!");
                return;
            }
        }

    }

    @FXML
    public void logoutButtonClick() {

        try {
            connection.close();
            if (connection.isClosed())
            {
                passLabel.setText("Connection has successfully closed!");
                concertTab.setDisable(true);
                OrgTab.setDisable(true);
                PlaceGroup.setDisable(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    @FXML
    public void showButtonClick()
    {
        table.refresh();
        concerts.clear();
        String selectTableSQL = "SELECT CONCERT.NAME, CONCERT.COST, CONCERT.\"Date\", CONCERT.CONCERT_ID, CONCERT.PLACE_ID from CONCERT";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            int i=0;

            while (rs.next()) {
                String CONCERT_NAME = rs.getString("NAME");
                String DATE = rs.getString("DATE");
                Integer COST = rs.getInt("COST");
                int concertID = rs.getInt("CONCERT_ID");
                int placeID = rs.getInt("PLACE_ID");
                String placeName = new String();
                String placeAddress = new String();
                String GROUPS = getGroups(concertID);
                List<String> place = getPlace(placeID,placeName,placeAddress);
                concerts.add(new Concert(i,CONCERT_NAME,GROUPS,DATE,place.get(0),place.get(1),COST));
                System.out.println(CONCERT_NAME);
                i++;
            }
            tableID.setCellValueFactory(new PropertyValueFactory<Concert,Integer>("ID"));
            tableName.setCellValueFactory(new PropertyValueFactory<Concert,String>("name"));
            tableGroup.setCellValueFactory(new PropertyValueFactory<Concert,String>("group"));
            tableAdd.setCellValueFactory(new PropertyValueFactory<Concert,String>("address"));
            tableDate.setCellValueFactory(new PropertyValueFactory<Concert,String>("date"));
            tablePlace.setCellValueFactory(new PropertyValueFactory<Concert,String>("place"));
            tableCost.setCellValueFactory(new PropertyValueFactory<Concert,Integer>("cost"));

            table.setItems(concerts);
            table.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getGroups(int concertID)
    {
        String result = new String("");

        System.out.println(concertID);
        String selectTableSQL = new String("SELECT \"Group\".NAME FROM \"Group\" " +
                "WHERE GROUP_ID IN" +
                "(SELECT GROUP_ID FROM CONCERT_GROUP" +
                " WHERE CONCERT_ID= "+concertID+" )");
        System.out.println(selectTableSQL);

        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                String name = rs.getString("NAME");
                System.out.println(name);
                result = result.concat(name+", ");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("result srting "+result);
        return result;
    }

    public List<String> getPlace(int placeID, String name, String address)
    {
        List result = new ArrayList<String>();
        String selectTableSQL = new String("SELECT PLACE.NAME, PLACE.ADDRESS FROM PLACE WHERE PLACE_ID = "+placeID);
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            rs.next();
            name = rs.getString("name");
            address = rs.getString("address");
            result.add(name);
            result.add(address);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean checkConcertName(String name)
    {
        String selectTableSQL = new String("SELECT CONCERT_ID FROM CONCERT WHERE CONCERT.NAME = '"+name+"'");
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if (rs.next()) {
                return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean checkGroupName(String name)
    {
        String selectTableSQL = new String("SELECT GROUP_ID FROM \"Group\" WHERE \"Group\".NAME = '"+name+"'");
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkPlaceName(String name)
    {
        String selectTableSQL = new String("SELECT PLACE_ID FROM PLACE WHERE PLACE.NAME = '"+name+"'");
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if (rs.next()) {
                return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean checkOganizer(String name)
    {
        String selectTableSQL = new String("SELECT ORGANIZER_ID FROM ORGANIZER WHERE ORGANIZER.NAME = '"+name+"'");
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if (rs.next()) {
                return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    public void statButtonClick()
    {
        ObservableList<PieChart.Data> result = FXCollections.observableArrayList();
        organizers.clear();
        String selectTableSQL = "SELECT OC.ORGANIZER_ID, SUM(OC.REVENUE) AS \"REVENUE\", O.NAME  FROM ORG_CONCERT OC \n" +
                "INNER JOIN ORGANIZER O ON O.ORGANIZER_ID = OC.ORGANIZER_ID\n" +
                "GROUP BY O.NAME, OC.ORGANIZER_ID";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            int i=0;

            while (rs.next()) {
                String name = rs.getString("NAME");
                Integer revenue = rs.getInt("REVENUE");
                organizers.add(new Organizer(i,name,revenue));
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Organizer org: organizers
             ) {
            result.add(new PieChart.Data(org.getName(),org.getRevenue()));
        }


        orgChart.setData(result);
    }


    @FXML
    public void addConcertButtonClick()
    {
        String concertName = nameConcertTxt.getText();
        String group = groupConcertTxt.getText();
        String organizer = orgConcertTxt.getText();
        String place = placeConcertTxt.getText();
        int cost;
        String date = new String();
        try
        {
            LocalDate localDate = datePicker.getValue();
            date = localDate.toString();
            if (localDate==null) throw new NullPointerException();
        }
        catch (NullPointerException e)
        {
            reportLabel.setText("Date not set!");
        }



        try
        {
            cost = Integer.parseInt(costTxt.getText());
            costTxt.setStyle("-fx-border-color: none;");
        }
        catch (NumberFormatException e)
        {
            costTxt.setStyle("-fx-border-color: red;");
            return;
        }
        if (checkConcertName(concertName))
        {
            reportLabel.setText("This Concert already in the DB!");
            return;
        }
        if (!checkGroupName(group))
        {
            reportLabel.setText("This group does not exist in DB!");
            return;
        }
        if (!checkOganizer(organizer))
        {
            reportLabel.setText("This organizer does not exist in DB!");
            return;
        }
        if (!checkPlaceName(place))
        {
            reportLabel.setText("This place does not exist in DB!");
            return;
        }
        reportLabel.setText("");
        addConcert(concertName,date,group,place,organizer,cost);

    }

    public void addConcert(String name, String date, String group, String place, String organizer, int cost)
    {
        int groupId = getGroupID(group);
        int placeId = getPlaceID(place);
        int concertId = nextConcertId();
        int orgId = getOrgID(organizer);
        System.out.println("Add Concert name: "+name+" date: "+date+" cost: "+ cost);
        String selectTableSQL = "INSERT INTO CONCERT \n" +
                "(\"Date\", COST, CONCERT_ID, PLACE_ID, NAME)\n" +
                "VALUES\n" +
                "(TO_DATE('"+date+"', 'YYYY-MM-DD HH24:MI:SS')," +
                " '"+cost+"', " +
                "'"+concertId+"', " +
                "'"+placeId+"', " +
                "'"+name+"')";

        System.out.println(selectTableSQL);
        try {
            statement.executeUpdate(selectTableSQL);
            addGroupInConcert(groupId,concertId);
            addOrganizerInConcert(orgId,concertId);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        reportLabel.setText("Adding success!");
    }

    public int getGroupID(String name)
    {
        int id=-1;
        String selectTableSQL = "SELECT \"Group\".GROUP_ID FROM \"Group\" WHERE NAME = '"+name+"'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if (rs.next()) id = rs.getInt("GROUP_ID");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }

    public int getPlaceID(String name)
    {
        int id=-1;
        String selectTableSQL = "SELECT PLACE.PLACE_ID FROM PLACE WHERE NAME = '"+name+"'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if (rs.next()) id = rs.getInt("PLACE_ID");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }
    public int getOrgID(String name)
    {
        int id=-1;
        String selectTableSQL = "SELECT ORGANIZER.ORGANIZER_ID FROM ORGANIZER WHERE NAME = '"+name+"'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if (rs.next()) id = rs.getInt("ORGANIZER_ID");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }

    public void addGroupInConcert(int groupId, int concertId)
    {
        String SQL = "INSERT INTO CONCERT_GROUP (CONCERT_ID, GROUP_ID) VALUES ("+concertId+","+groupId+")";
        try {
            statement.executeUpdate(SQL);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addOrganizerInConcert(int orgId, int concertId)
    {
        String SQL = "INSERT INTO ORG_CONCERT (CONCERT_ID, ORGANIZER_ID) VALUES ("+concertId+","+orgId+")";
        try {
            statement.executeUpdate(SQL);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int nextConcertId()
    {
        int id=-1;
        String SQL = "(select max(CONCERT.CONCERT_ID) AS \"MAX\"  from CONCERT) ";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SQL);
            rs.next();
            id = rs.getInt("MAX");
            id++;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }
    public int nextGroupId()
    {
        int id=-1;
        String SQL = "(select max(\"Group\".GROUP_ID) as \"MAX\" from \"Group\")";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SQL);
            if (rs.next()) id = rs.getInt("MAX");
            id++;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }
    public int nextPlaceId()
    {
        int id=-1;
        String SQL = "(select max(PLACE.PLACE_ID) as \"MAX\" from PLACE)";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SQL);
            if (rs.next()) id = rs.getInt("MAX");
            id++;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }

    @FXML
    public void addGroupButtonClick()
    {
        String name = groupNameTxt.getText();
        String style = groupStyleTxt.getText();
        int id = nextGroupId();
        String SQL = "INSERT INTO \"Group\" (GROUP_ID, NAME, STYLE) VALUES ("+id+", '"+name+"', +'"+style+"')";
        try {
            statement.executeUpdate(SQL);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        groupPlaceLabel.setText("Group added");
    }

    @FXML
    public void addPlaceButtonClick()
    {
        String name = placeNameTxt.getText();
        String address = placeAddTxt.getText();
        int id = nextPlaceId();
        String SQL = "INSERT INTO PLACE (PLACE_ID, NAME, ADDRESS) VALUES ("+id+", '"+name+"', +'"+address+"')";
        try {
            statement.executeUpdate(SQL);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        groupPlaceLabel.setText("Place added");
    }

}
