import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.lang.*;

/**
 * Runs queries against a back-end database
 */
public class Query{
    private String configFilename;
    private Properties configProps = new Properties();
    
    private String jSQLDriver;
    private String jSQLUrl;
    private String jSQLUser;
    private String jSQLPassword;
    
    // DB Connection
    private Connection conn;
    
    // Logged In User
    private String username; // customer username is unique
    
    // Available itinerary list
    private ArrayList<Itinerary> itiList = new ArrayList<Itinerary>();
    
    // Canned queries
    private static final String GET_FLIGHT = "select * from flights where fid = ?;";
    private PreparedStatement getFlightStatement;

    private static final String CHECK_FLIGHT_CAPACITY = "SELECT capacity FROM Flights WHERE fid = ?;";
    private PreparedStatement checkFlightCapacityStatement;
    
    private static final String GET_DIRECT = "select top (?) * from flights " +
                                             "where origin_city = ? and dest_city = ? and " +
                                             "day_of_month = ? and canceled != 1 " +
                                             "order by actual_time asc, fid asc";
    
    private PreparedStatement getDirectStatement;
    
    private static final String GET_INDIRECT =  "select top (?) f1.fid as f1_fid, " +
                                                "f1.day_of_month as f1_dayOfMonth, " +
                                                "f1.carrier_id as f1_carrier_id, " +
                                                "f1.flight_num as f1_flight_num, " +
                                                "f1.origin_city as f1_origin_city, " +
                                                "f1.dest_city as f1_dest_city, " +
                                                "f1.actual_time as f1_time, " +
                                                "f1.capacity as f1_capacity, " +
                                                "f1.price as f1_price, " +
                                                "f2.fid as f2_fid, " +
                                                "f2.day_of_month as f2_dayOfMonth, " +
                                                "f2.carrier_id as f2_carrier_id, " +
                                                "f2.flight_num as f2_flight_num, " +
                                                "f2.origin_city as f2_origin_city, " +
                                                "f2.dest_city as f2_dest_city, " +
                                                "f2.actual_time as f2_time, " +
                                                "f2.capacity as f2_capacity, " +
                                                "f2.price as f2_price " +
                                                "from flights as f1, flights as f2 " +
                                                "where f1.day_of_month = f2.day_of_month AND " +
                                                "f1.origin_city = ? AND " +
                                                "f2.dest_city = ? AND " +
                                                "f1.day_of_month = ? AND " +
                                                "f1.canceled != 1 AND " +
                                                "f2.canceled != 1 AND " +
                                                "f1.dest_city = f2.origin_city " +
                                                "order by (f1.actual_time + f2.actual_time);";
    
    private PreparedStatement getInDirectStatement;

    private static final String DELE_RESE = "delete from rese; ";
    private PreparedStatement deleReseStatement;
    
    private static final String DELE_USER = "delete from users; ";
    private PreparedStatement deleUserStatement;   

    private static final String DELE_CAPA = "delete from capa; ";
    private PreparedStatement deleCapaStatement;

    private static final String DELE_RESE_NUM = "delete from resenum; ";
    private PreparedStatement deleReseNumStatement;
    
    private static final String GET_USER = "select * from users where username = ? and password = ?;";
    private PreparedStatement getUserStatement;
    
    private static final String GET_USER_EXIST = "select * from users where username = ?;";
    private PreparedStatement getUserExistStatement;
    
    private static final String SET_USER = "insert into users values (?,?,?);";
    private PreparedStatement setUserStatement;
    
    private static final String GET_BALA = "select balance from users where username = ?;";
    private PreparedStatement getBalaStatement;
    
    private static final String SET_BALA = "update users set balance = ? where username = ?;";
    private PreparedStatement setBalaStatement;
    
    private static final String GET_COST = "select cost from rese where rid = ?;";
    private PreparedStatement getCostStatement;

    private static final String GET_PAID = "select paid from rese where rid = ?;";
    private PreparedStatement getPaidStatement;

    private static final String SET_PAID = "update rese set paid = ? where rid = ?;";
    private PreparedStatement setPaidStatement;
    
    private static final String INI_CAPA = "insert into capa select f.fid, f.capacity " + 
                                           "from flights f where f.fid = ? and " +
                                           "not exists (select * from capa c where c.fid = f.fid);";
    private PreparedStatement iniCapaStatement;

    private static final String GET_CAPA = "select * from capa where fid = ?;";
    private PreparedStatement getCapaStatement;

    private static final String SET_CAPA = "update capa set capacity = " +
                                           "((select capacity from capa where fid = ?) - 1) " +
                                           "where fid = ?;";
    private PreparedStatement setCapaStatement;

    private static final String ADD_CAPA = "update capa set capacity = " +
                                           "((select capacity from capa where fid = ?) + 1) " +
                                           "where fid = ?;";
    private PreparedStatement addCapaStatement;
    
    private static final String GET_CANCEL = "select * from rese where rid = ?;";
    private PreparedStatement getCancelStatement;

    private static final String SET_CANCEL = "update rese set cancel = 1 where rid = ?;";
    private PreparedStatement setCancelStatement;

    private static final String UP_RESE = "delete from rese where rid = ?";
    private PreparedStatement upReseStatement;

    private static final String GET_RESE = "select * from rese where username = ?;";
    private PreparedStatement getReseStatement;

    private static final String SET_RESE = "insert into rese values (?,?,?,?,?,?,?,?);";
    private PreparedStatement setReseStatement;

    private static final String INI_RESE_NUM = "insert into resenum values (0);";
    private PreparedStatement iniReseNumStatement;

    private static final String GET_RESE_NUM = "select * from resenum;";
    private PreparedStatement getReseNumStatement;

    private static final String SET_RESE_NUM = "update resenum set rid = ((select rid from resenum) + 1);";
    private PreparedStatement setReseNumStatement;
    
    // transactions
    private static final String BEGIN_TRANSACTION_SQL = "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION;";
    private PreparedStatement beginTransactionStatement;
    
    private static final String COMMIT_SQL = "COMMIT TRANSACTION";
    private PreparedStatement commitTransactionStatement;
    
    private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";
    private PreparedStatement rollbackTransactionStatement;
    
    class Flight {
        
        public int fid;
        public int dayOfMonth;
        public String carrierId;
        public String flightNum;
        public String originCity;
        public String destCity;
        public int time;
        public int capacity;
        public int price;
        
        public Flight(int fid, int dayOfMonth, String carrierId, String flightNum, String originCity,
                      String destCity, int time, int capacity, int price) {
            
            this.fid = fid;
            this.dayOfMonth = dayOfMonth;
            this.carrierId = carrierId;
            this.flightNum = flightNum;
            this.originCity = originCity;
            this.destCity = destCity;
            this.time = time;
            this.capacity = capacity;
            this.price = price;
        }
        
        @Override
        public String toString()
        {
            return "ID: " + fid + " Day: " + dayOfMonth + " Carrier: " + carrierId +
            " Number: " + flightNum + " Origin: " + originCity + " Dest: " + destCity + " Duration: " + time +
            " Capacity: " + capacity + " Price: " + price;
        }
    }
    
    class Itinerary implements Comparable<Itinerary> {
        public Flight f1;
        public Flight f2;
        public int cost;
        public int day;
        public int total_time;
        
        public Itinerary(Flight f1, Flight f2, int cost, int day, int total_time) {
            this.f1 = f1;
            this.f2 = f2;
            this.cost = cost;
            this.day = day;
            this.total_time = total_time;
        }
        
        //@Override
        public int compareTo(Itinerary other) {
            if (total_time == other.total_time) {
                if (f1.fid == other.f1.fid) {
                    return Integer.compare(f2.fid, other.f2.fid);
                }
                return Integer.compare(f1.fid, other.f1.fid);
            }
            return Integer.compare(total_time, other.total_time);
        }
        
        public String toString() {
            if (f2 == null) {
                return f1.toString();
            }
            return f1.toString() + "\n" + f2.toString();
        }
    }
    
    public Query(String configFilename)
    {
        this.configFilename = configFilename;
    }
    
    /* Connection code to SQL Azure.  */
    public void openConnection() throws Exception
    {
        configProps.load(new FileInputStream(configFilename));
        
        jSQLDriver = configProps.getProperty("flightservice.jdbc_driver");
        jSQLUrl = configProps.getProperty("flightservice.url");
        jSQLUser = configProps.getProperty("flightservice.sqlazure_username");
        jSQLPassword = configProps.getProperty("flightservice.sqlazure_password");
        
        /* load jdbc drivers */
        Class.forName(jSQLDriver).newInstance();
        
        /* open connections to the flights database */
        conn = DriverManager.getConnection(jSQLUrl, // database
                                           jSQLUser, // user
                                           jSQLPassword); // password
        
        conn.setAutoCommit(true); //by default automatically commit after each statement
        
        /* You will also want to appropriately set the transaction's isolation level through:
         conn.setTransactionIsolation(...)
         See Connection class' JavaDoc for details.
         */
    }
    
    public void closeConnection() throws Exception
    {
        conn.close();
    }
    
    /**
     * Clear the data in any custom tables created. Do not drop any tables and do not
     * clear the flights table. You should clear any tables you use to store reservations
     * and reset the next reservation ID to be 1.
     */
    public void clearTables () throws SQLException
    {
        try {      
            beginTransaction();
            deleReseStatement.clearParameters();
            deleReseStatement.executeUpdate();

            deleReseNumStatement.clearParameters();
            deleReseNumStatement.executeUpdate();

            deleCapaStatement.clearParameters();
            deleCapaStatement.executeUpdate();

            deleUserStatement.clearParameters();
            deleUserStatement.executeUpdate();
            commitTransaction();
           
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    /**
     * prepare all the SQL statements in this method.
     * "preparing" a statement is almost like compiling it.
     * Note that the parameters (with ?) are still not filled in
     */
    public void prepareStatements() throws Exception
    {
        beginTransactionStatement = conn.prepareStatement(BEGIN_TRANSACTION_SQL);
        commitTransactionStatement = conn.prepareStatement(COMMIT_SQL);
        rollbackTransactionStatement = conn.prepareStatement(ROLLBACK_SQL);
        
        getFlightStatement = conn.prepareStatement(GET_FLIGHT);

        deleUserStatement = conn.prepareStatement(DELE_USER);
        deleCapaStatement = conn.prepareStatement(DELE_CAPA);
        deleReseStatement = conn.prepareStatement(DELE_RESE);
        deleReseNumStatement = conn.prepareStatement(DELE_RESE_NUM);

        getUserStatement = conn.prepareStatement(GET_USER);
        setUserStatement = conn.prepareStatement(SET_USER);
        getUserExistStatement = conn.prepareStatement(GET_USER_EXIST);

        getBalaStatement = conn.prepareStatement(GET_BALA);
        setBalaStatement = conn.prepareStatement(SET_BALA);

        addCapaStatement = conn.prepareStatement(ADD_CAPA);
        iniCapaStatement = conn.prepareStatement(INI_CAPA);
        getCapaStatement = conn.prepareStatement(GET_CAPA);
        setCapaStatement = conn.prepareStatement(SET_CAPA);

        getPaidStatement = conn.prepareStatement(GET_PAID);
        setPaidStatement = conn.prepareStatement(SET_PAID);

        getDirectStatement = conn.prepareStatement(GET_DIRECT);
        getInDirectStatement = conn.prepareStatement(GET_INDIRECT);        

        upReseStatement = conn.prepareStatement(UP_RESE);
        setReseStatement = conn.prepareStatement(SET_RESE);
        getReseStatement = conn.prepareStatement(GET_RESE);
        getReseNumStatement = conn.prepareStatement(GET_RESE_NUM);
        iniReseNumStatement = conn.prepareStatement(INI_RESE_NUM);
        setReseNumStatement = conn.prepareStatement(SET_RESE_NUM);

        getCancelStatement = conn.prepareStatement(GET_CANCEL);
        setCancelStatement = conn.prepareStatement(SET_CANCEL);

        getCostStatement = conn.prepareStatement(GET_COST);

        
        /* add here more prepare statements for all the other queries you need */
        /* . . . . . . */
    }
    
    /**
     * Takes a user's username and password and attempts to log the user in.
     *
     * @param username
     * @param password
     *
     * @return If someone has already logged in, then return "User already logged in\n"
     * For all other errors, return "Login failed\n".
     *
     * Otherwise, return "Logged in as [username]\n".
     */
    public String transaction_login(String username, String password) {
        if (this.username != null) {
            return "User already logged in\n";
        }
        try {
            beginTransaction();
            
            getUserStatement.clearParameters();
            getUserStatement.setString(1, username);
            getUserStatement.setString(2, password);
            ResultSet userR = getUserStatement.executeQuery();
            
            if (userR.next()) {
                this.username = username;
                userR.close();  
                commitTransaction(); // success login
                itiList = new ArrayList<Itinerary>();
                return "Logged in as " + this.username + "\n"; 
            }           
            userR.close();
            rollbackTransaction(); // fail login
        } catch (SQLException e) { e.printStackTrace(); return "Login failed\n";}
        return "Login failed\n";
    }
    
    /**
     * Implement the create user function.
     *
     * @param username new user's username. User names are unique the system.
     * @param password new user's password.
     * @param initAmount initial amount to deposit into the user's account, should be >= 0 (failure otherwise).
     *
     * @return either "Created user {@code username}\n" or "Failed to create user\n" if failed.
     */
    public String transaction_createCustomer (String username, String password, int initAmount) {
        if (initAmount >= 0) {
            
            try {
                //conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                
                beginTransaction();
                getUserExistStatement.clearParameters();
                getUserExistStatement.setString(1, username);
                ResultSet userR = getUserExistStatement.executeQuery();
                
                if (!userR.next()) { // not existed user 
                    
                    setUserStatement.clearParameters();
                    setUserStatement.setString(1, username);
                    setUserStatement.setString(2, password);
                    setUserStatement.setInt(3, initAmount);
                    setUserStatement.executeUpdate(); // success create 
                    userR.close();
                    commitTransaction();                                                             
                    return "Created user " + username + "\n";
                }

                userR.close();
                rollbackTransaction();
            } catch (SQLException e) { e.printStackTrace(); return "Failed to create user\n";}        
        }
        return "Failed to create user\n";
    }
    
    /**
     * Implement the search function.
     *
     * Searches for flights from the given origin city to the given destination
     * city, on the given day of the month. If {@code directFlight} is true, it only
     * searches for direct flights, otherwise is searches for direct flights
     * and flights with two "hops." Only searches for up to the number of
     * itineraries given by {@code numberOfItineraries}.
     *
     * The results are sorted based on total flight time.
     *
     * @param originCity
     * @param destinationCity
     * @param directFlight if true, then only search for direct flights, otherwise include indirect flights as well
     * @param dayOfMonth
     * @param numberOfItineraries number of itineraries to return
     *
     * @return If no itineraries were found, return "No flights match your selection\n".
     * If an error occurs, then return "Failed to search\n".
     *
     * Otherwise, the sorted itineraries printed in the following format:
     *
     * Itinerary [itinerary number]: [number of flights] flight(s), [total flight time] minutes\n
     * [first flight in itinerary]\n
     * ...
     * [last flight in itinerary]\n
     *
     * Each flight should be printed using the same format as in the {@code Flight} class. Itinerary numbers
     * in each search should always start from 0 and increase by 1.
     *
     * @see Flight#toString()
     */
    public String transaction_search(String originCity, String destinationCity, boolean directFlight,
                                     int dayOfMonth, int numberOfItineraries) {
        
        return transaction_search_safe(originCity, destinationCity, directFlight, dayOfMonth, numberOfItineraries);
        //return transaction_search_unsafe(originCity, destinationCity, directFlight, dayOfMonth, numberOfItineraries);
    }
    
    private String transaction_search_safe(String originCity, String destinationCity,
                                           boolean directFlight, int dayOfMonth, int numberOfItineraries) {
        StringBuffer sb = new StringBuffer();
        itiList = new ArrayList<Itinerary>();
        
        try {
            int count = numberOfItineraries;
            
            // get sql result for direct
            ResultSet sqlD = getDirect(originCity, destinationCity,
                                       dayOfMonth, numberOfItineraries);

            // store flight data line by line
            while (count > 0 && sqlD.next()) {
                int price = sqlD.getInt("price");
                int total_time = sqlD.getInt("actual_time");
                
                Flight f1 = new Flight(sqlD.getInt("fid"), sqlD.getInt("day_of_month"),
                                       sqlD.getString("carrier_id"), sqlD.getString("flight_num"),
                                       sqlD.getString("origin_city"), sqlD.getString("dest_city"),
                                       total_time, sqlD.getInt("capacity"),
                                       price);
                Flight f2 = null;

                Itinerary i = new Itinerary(f1,f2,price,dayOfMonth,total_time);

                this.itiList.add(i);

                count--;
            }
            sqlD.close();
            
            if (count > 0 && !directFlight) {
            
                // get sql result for direct
                ResultSet sqlID = getIndirect(originCity, destinationCity,
                                              dayOfMonth, count);
                
                // store flight data line by line
                int idCount = 0;
                while (count > 0 && sqlID.next()) {
                    int price = sqlID.getInt("f1_price") + sqlID.getInt("f2_price");
                    int total_time = sqlID.getInt("f1_time") + sqlID.getInt("f2_time");
                    
                    Flight f1 = new Flight(sqlID.getInt("f1_fid"), sqlID.getInt("f1_dayOfMonth"),
                                           sqlID.getString("f1_carrier_id"), sqlID.getString("f1_flight_num"),
                                           sqlID.getString("f1_origin_city"), sqlID.getString("f1_dest_city"),
                                           sqlID.getInt("f1_time"), sqlID.getInt("f1_capacity"),
                                           sqlID.getInt("f1_price"));
                    Flight f2 = new Flight(sqlID.getInt("f2_fid"), sqlID.getInt("f2_dayOfMonth"),
                                           sqlID.getString("f2_carrier_id"), sqlID.getString("f2_flight_num"),
                                           sqlID.getString("f2_origin_city"), sqlID.getString("f2_dest_city"),
                                           sqlID.getInt("f2_time"), sqlID.getInt("f2_capacity"),
                                           sqlID.getInt("f2_price"));
                    
                    Itinerary i = new Itinerary(f1,f2,price,dayOfMonth,total_time);
                    
                    this.itiList.add(i);
                    count--;
                    idCount++;
                }
                sqlID.close();
            
            }
            
            Collections.sort(itiList);
            for (int i = 0; i < Math.min(numberOfItineraries, itiList.size()); i++) {
                Itinerary res = itiList.get(i);
                if (res.f2 == null) {
                    sb.append("Itinerary " + i + ": 1 flight(s), " + res.total_time + " minutes\n");
                    sb.append(res.f1 + "\n");
                } else {
                    sb.append("Itinerary " + i + ": 2 flight(s), " + res.total_time + " minutes\n");
                    sb.append(res.f1 + "\n");
                    sb.append(res.f2 + "\n");
                }
            }

        } catch (SQLException e) { e.printStackTrace(); return "Failed to search\n";}
        if (sb.toString().equals("")) {
            return "No flights match your selection\n";
        }
        return sb.toString();
    }
    
    private ResultSet getIndirect(String originCity, String destinationCity,
                                int dayOfMonth, int numberOfItineraries) throws SQLException {
        ResultSet results = null;
        try {
            getInDirectStatement.clearParameters();
            getInDirectStatement.setInt(1, numberOfItineraries);
            getInDirectStatement.setString(2, originCity);
            getInDirectStatement.setString(3, destinationCity);
            getInDirectStatement.setInt(4, dayOfMonth);
            
            results = getInDirectStatement.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }
    
    private ResultSet getDirect(String originCity, String destinationCity,
                                int dayOfMonth, int numberOfItineraries) throws SQLException {
        ResultSet results = null;
        try {
            getDirectStatement.clearParameters();
            getDirectStatement.setInt(1, numberOfItineraries);
            getDirectStatement.setString(2, originCity);
            getDirectStatement.setString(3, destinationCity);
            getDirectStatement.setInt(4, dayOfMonth);

            results = getDirectStatement.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }
    
    /**
     * Same as {@code transaction_search} except that it only performs single hop search and
     * do it in an unsafe manner.
     *
     * @param originCity
     * @param destinationCity
     * @param directFlight
     * @param dayOfMonth
     * @param numberOfItineraries
     *
     * @return The search results. Note that this implementation *does not conform* to the format required by
     * {@code transaction_search}.
     */
    private String transaction_search_unsafe(String originCity, String destinationCity, boolean directFlight,
                                             int dayOfMonth, int numberOfItineraries)
    {
        StringBuffer sb = new StringBuffer();
        
        try
        {
            // one hop itineraries
            String unsafeSearchSQL =
            "SELECT TOP (" + numberOfItineraries + ") day_of_month,carrier_id,flight_num,origin_city,dest_city,actual_time,capacity,price "
            + "FROM Flights "
            + "WHERE origin_city = \'" + originCity + "\' AND dest_city = \'" + destinationCity + "\' AND day_of_month =  " + dayOfMonth + " "
            + "ORDER BY actual_time ASC";
            
            Statement searchStatement = conn.createStatement();
            ResultSet oneHopResults = searchStatement.executeQuery(unsafeSearchSQL);
            
            while (oneHopResults.next())
            {
                int result_dayOfMonth = oneHopResults.getInt("day_of_month");
                String result_carrierId = oneHopResults.getString("carrier_id");
                String result_flightNum = oneHopResults.getString("flight_num");
                String result_originCity = oneHopResults.getString("origin_city");
                String result_destCity = oneHopResults.getString("dest_city");
                int result_time = oneHopResults.getInt("actual_time");
                int result_capacity = oneHopResults.getInt("capacity");
                int result_price = oneHopResults.getInt("price");
                
                sb.append("Day: " + result_dayOfMonth + " Carrier: " + result_carrierId + " Number: " + result_flightNum + " Origin: " + result_originCity + " Destination: " + result_destCity + " Duration: " + result_time + " Capacity: " + result_capacity + " Price: " + result_price + "\n");
            }
            oneHopResults.close();
        } catch (SQLException e) { e.printStackTrace(); }
        
        return sb.toString();
    }
    
    /**
     * Implements the book itinerary function.
     *
     * @param itineraryId ID of the itinerary to book. This must be one that is returned by search in the current session.
     *
     * @return If the user is not logged in, then return "Cannot book reservations, not logged in\n".
     * If try to book an itinerary with invalid ID, then return "No such itinerary {@code itineraryId}\n".
     * If the user already has a reservation on the same day as the one that they are trying to book now, then return
     * "You cannot book two flights in the same day\n".
     * For all other errors, return "Booking failed\n".
     *
     * And if booking succeeded, return "Booked flight(s), reservation ID: [reservationId]\n" where
     * reservationId is a unique number in the reservation system that starts from 1 and increments by 1 each time a
     * successful reservation is made by any user in the system.
     */
    public String transaction_book(int itineraryId) {
        int reseNum = 1;

        if (this.username == null) {
            return "Cannot book reservations, not logged in\n";
        }

        int cur_size = this.itiList.size();
        if (cur_size == 0) {
            return "Booking failed\n";
        }

        if (itineraryId < 0 || itineraryId > cur_size - 1) {
            return "No such itinerary " + itineraryId + "\n";
        }
        
        Itinerary now = itiList.get(itineraryId);

        try {
            
            

            getReseStatement.clearParameters();
            getReseStatement.setString(1,this.username);
            ResultSet reseR = getReseStatement.executeQuery();
            while (reseR.next()) {
                if (now.day == reseR.getInt("day")) {
                    reseR.close();
                    return "You cannot book two flights in the same day\n";
                }
            }
            reseR.close();

            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);            
            beginTransaction();
            // getCapaStatement.clearParameters();
            // getCapaStatement.setInt(1,now.f1.fid);
            // ResultSet getCR = getCapaStatement.executeQuery();
            // if (getCR.next()) {
            //     setCapa(now.f1);
            // } else {
            //     getCR.close();
            //     int iniC1 = iniCapa(now.f1);
            //     if (iniC1 == -1) {
            //         rollbackTransaction();
            //         return "Booking failed\n";
            //     } 
            // }
            // getCR.close();

            // if (now.f2 != null) {
            //     getCapaStatement.clearParameters();
            //     getCapaStatement.setInt(1,now.f2.fid);
            //     ResultSet getCR2 = getCapaStatement.executeQuery();
            //     if (getCR2.next()) {
            //         setCapa(now.f2);
            //     } else {
            //         getCR2.close();
            //         int iniC2 = iniCapa(now.f2);
            //         if (iniC2 == -1) {
            //             rollbackTransaction();
            //             return "Booking failed\n";
            //         } 
            //     }
            //     getCR2.close();
            // }
            

            int iniC1 = iniCapa(now.f1);
            if (iniC1 == -1) {
                rollbackTransaction();
                return "Booking failed\n";
            } 

            if (now.f2 != null) {
                int iniC2 = iniCapa(now.f2);
                if (iniC2 == -1) {
                    rollbackTransaction();
                    return "Booking failed\n";
                } 
            }            

            setCapa(now.f1);
            if (now.f2 != null) {
                setCapa(now.f2);
            }
            
            getReseNumStatement.clearParameters();
            ResultSet reseNumR = getReseNumStatement.executeQuery();
            if (!reseNumR.next()) {
                reseNumR.close();
                iniReseNumStatement.clearParameters();
                iniReseNumStatement.executeUpdate();
            } else {
                reseNum = reseNumR.getInt("rid") + 1;
            }
            reseNumR.close();
            setReseNumStatement.clearParameters();
            setReseNumStatement.executeUpdate();

            setReseStatement.clearParameters(); 
            setReseStatement.setInt(1, reseNum);
            setReseStatement.setString(2, this.username);
            setReseStatement.setInt(3, 0);
            setReseStatement.setInt(4, now.cost);
            setReseStatement.setInt(5, now.f1.fid);

            if (now.f2 == null) {
                setReseStatement.setNull(6, -1);
            } else {
                setReseStatement.setInt(6, now.f2.fid);
            }
            
            setReseStatement.setInt(7, now.day);
            setReseStatement.setInt(8, 0); // not cancel
            setReseStatement.executeUpdate();

            commitTransaction();
            return "Booked flight(s), reservation ID: " + reseNum + "\n";

        } catch (SQLException e){ e.printStackTrace(); return "Booking failed\n";}
        
    }

    private void setCapa(Flight f) {
        try {
            if (f != null) {
                setCapaStatement.clearParameters();
                setCapaStatement.setInt(1,f.fid);
                setCapaStatement.setInt(2,f.fid);
                setCapaStatement.executeUpdate();
            }
        } catch (SQLException e){ e.printStackTrace(); }
    }

    private int iniCapa(Flight f) {
        try {         
            if (f != null) {

                iniCapaStatement.clearParameters();
                iniCapaStatement.setInt(1, f.fid);
                iniCapaStatement.executeUpdate();

                getCapaStatement.clearParameters();
                getCapaStatement.setInt(1, f.fid);
                ResultSet capaR = getCapaStatement.executeQuery();

                if (!capaR.next()) {
                    capaR.close();
                    //rollbackTransaction();
                    return -1;
                }

                int ava = capaR.getInt("capacity");

                capaR.close();

                if (ava == 0) {
                    //rollbackTransaction();
                    return -1;
                }  
            }
            
        } catch (SQLException e){ e.printStackTrace(); }
        return 1;        
    }

    
    /**
     * Implements the reservations function.
     *
     * @return If no user has logged in, then return "Cannot view reservations, not logged in\n"
     * If the user has no reservations, then return "No reservations found\n"
     * For all other errors, return "Failed to retrieve reservations\n"
     *
     * Otherwise return the reservations in the following format:
     *
     * Reservation [reservation ID] paid: [true or false]:\n"
     * [flight 1 under the reservation]
     * [flight 2 under the reservation]
     * Reservation [reservation ID] paid: [true or false]:\n"
     * [flight 1 under the reservation]
     * [flight 2 under the reservation]
     * ...
     *
     * Each flight should be printed using the same format as in the {@code Flight} class.
     *
     * @see Flight#toString()
     */
    public String transaction_reservations() {
        StringBuffer sb = new StringBuffer();
        if (this.username == null) {
            return "Cannot view reservations, not logged in\n";
        } 
        try {
            getReseStatement.clearParameters();
            getReseStatement.setString(1, this.username);
            ResultSet reseR = getReseStatement.executeQuery();
            // Retrieves whether the cursor is before the first row in this ResultSet object.
            if (reseR.isBeforeFirst()) {
                
                while (reseR.next()) {
                    int rid = reseR.getInt("rid");
                    String paid = "";

                    if (reseR.getInt("paid") == 1) {
                        paid = "true";
                    } else { // reseR.getInt("paid") == 0
                        paid = "false";
                    }

                    sb.append("Reservation " + rid + " paid: " + paid + ":\n");
                    sb.append(setFlight(reseR.getInt("ffid")) + "\n");
                    if (reseR.getInt("sfid") != 0) {
                        sb.append(setFlight(reseR.getInt("sfid")) + "\n");
                    }                   
                }
                reseR.close();
            } else {
                reseR.close();                
                return "No reservations found\n";
            }
            return sb.toString();
        } catch (SQLException e){ e.printStackTrace(); return "Failed to retrieve reservations\n";}
        //return "Failed to retrieve reservations\n";
    }
    
    private String setFlight(int fid) {
        try {
            getFlightStatement.clearParameters();
            getFlightStatement.setInt(1, fid);
            ResultSet fR = getFlightStatement.executeQuery();
            fR.next();

            Flight f = new Flight(fR.getInt("fid"), fR.getInt("day_of_month"),
                           fR.getString("carrier_id"), fR.getString("flight_num"),
                           fR.getString("origin_city"), fR.getString("dest_city"),
                           fR.getInt("actual_time"), fR.getInt("capacity"),
                           fR.getInt("price"));
            fR.close();
            return f.toString();
        } catch (SQLException e){ e.printStackTrace(); }
        return null;
    }

    /**
     * Implements the cancel operation.
     *
     * @param reservationId the reservation ID to cancel
     *
     * @return If no user has logged in, then return "Cannot cancel reservations, not logged in\n"
     * For all other errors, return "Failed to cancel reservation [reservationId]"
     *
     * If successful, return "Canceled reservation [reservationId]"
     *
     * Even though a reservation has been canceled, its ID should not be reused by the system.
     */
    public String transaction_cancel(int reservationId) {
        if (this.username == null) {
            return "Cannot cancel reservations, not logged in\n";
        }  
        try {
            
            getCancelStatement.clearParameters();
            getCancelStatement.setInt(1, reservationId);
            ResultSet getCR = getCancelStatement.executeQuery();

            if (getCR.next()) {
                if (getCR.getString("username").equals(this.username)) {
                    int cost = getCR.getInt("cost");
                    int cancel = getCR.getInt("cancel");
                    if (cancel == 0) { // not canceled

                        getPaidStatement.clearParameters();
                        getPaidStatement.setInt(1, reservationId);
                        ResultSet getPR = getPaidStatement.executeQuery();
  
                        if (getPR.next()) {
                            beginTransaction();
                            if (getPR.getInt("paid") == 1) { // paid, cancel and return cost

                                getBalaStatement.clearParameters();
                                getBalaStatement.setString(1, this.username);
                                ResultSet getBR = getBalaStatement.executeQuery();

                                if (getBR.next()) {                                    
                                    int balance = getBR.getInt("balance");
                                    getBR.close();
                                    
                                    setCancelStatement.clearParameters();
                                    setCancelStatement.setInt(1, reservationId);
                                    setCancelStatement.executeUpdate();

                                    setBalaStatement.clearParameters();
                                    setBalaStatement.setInt(1, balance + cost); // return cost
                                    setBalaStatement.setString(2, this.username);
                                    setBalaStatement.executeUpdate();

                                    setPaidStatement.clearParameters();
                                    setPaidStatement.setInt(1, 0); // paid->unpaid
                                    setPaidStatement.setInt(2, reservationId);
                                    setPaidStatement.executeUpdate();
                                }                               
                            } else { // unpaid, just cancel
                                setCancelStatement.clearParameters();
                                setCancelStatement.setInt(1, reservationId);
                                setCancelStatement.executeUpdate();
                                
                            }
                            upReseStatement.clearParameters();
                            upReseStatement.setInt(1, reservationId);
                            upReseStatement.executeUpdate();

                            int ffid = getCR.getInt("ffid");   
                            addCapaStatement.clearParameters();
                            addCapaStatement.setInt(1, ffid);
                            addCapaStatement.setInt(2, ffid);
                            addCapaStatement.executeUpdate();

                            int sfid = getCR.getInt("sfid");
                            if (sfid != 0) {
                                addCapaStatement.clearParameters();
                                addCapaStatement.setInt(1, sfid);
                                addCapaStatement.setInt(2, sfid);
                                addCapaStatement.executeUpdate();
                            }
                            commitTransaction();
                        }
                        getCR.close(); 
                        getPR.close();
                        return "Canceled reservation " + reservationId + "\n";
                    }
                }                              
            }
            getCR.close();            
        } catch (SQLException e){ e.printStackTrace(); return "Failed to cancel reservation " + reservationId + "\n";}

        // only implement this if you are interested in earning extra credit for the HW!
        return "Failed to cancel reservation " + reservationId + "\n";
    }
    
    /**
     * Implements the pay function.
     *
     * @param reservationId the reservation to pay for.
     *
     * @return If no user has logged in, then return "Cannot pay, not logged in\n"
     * If the reservation is not found / not under the logged in user's name, then return
     * "Cannot find unpaid reservation [reservationId] under user: [username]\n"
     * If the user does not have enough money in their account, then return
     * "User has only [balance] in account but itinerary costs [cost]\n"
     * For all other errors, return "Failed to pay for reservation [reservationId]\n"
     *
     * If successful, return "Paid reservation: [reservationId] remaining balance: [balance]\n"
     * where [balance] is the remaining balance in the user's account.
     */
    public String transaction_pay (int reservationId) {
        if (this.username == null) {
            return "Cannot pay, not logged in\n";
        }

        try {
            getReseStatement.clearParameters();
            getReseStatement.setString(1, this.username);
            ResultSet reseR = getReseStatement.executeQuery();           
            if (!reseR.next()) { // empty rese list
                reseR.close();
                return "Cannot find unpaid reservation " + 
                       reservationId + " under user: " + 
                       this.username + "\n";
            } 
            reseR.close();
            
            getPaidStatement.clearParameters();
            getPaidStatement.setString(1, this.username);
            getPaidStatement.setInt(1, reservationId);
            ResultSet paidR = getPaidStatement.executeQuery();
            if (paidR.next()) {
            
                int paid = paidR.getInt("paid");
                paidR.close();

                if (paid == 1) { // already paid
                    return "Cannot find unpaid reservation " + reservationId +
                           " under user: " + this.username + "\n";
                }
                
                getBalaStatement.clearParameters();
                getBalaStatement.setString(1, this.username);
                ResultSet getBaR = getBalaStatement.executeQuery();
                getBaR.next();
                int balance = getBaR.getInt("balance");
                getBaR.close();

                getCostStatement.clearParameters();
                getCostStatement.setInt(1, reservationId);
                ResultSet costR = getCostStatement.executeQuery();
                costR.next();
                int cost = costR.getInt("cost");
                costR.close();

                if (cost > balance) {     
                    return "User has only " + balance + 
                           " in account but itinerary costs " + cost + "\n";
                }

                beginTransaction();
                balance = balance - cost;
                setBalaStatement.clearParameters();
                setBalaStatement.setInt(1, balance);
                setBalaStatement.setString(2, this.username);
                setBalaStatement.executeUpdate();

                setPaidStatement.clearParameters();
                setPaidStatement.setInt(1, 1);
                setPaidStatement.setInt(2, reservationId);
                setPaidStatement.executeUpdate();

                commitTransaction();
                return "Paid reservation: " + reservationId + 
                       " remaining balance: " + balance + "\n";
            } 
            paidR.close(); 
            return "Cannot find unpaid reservation " + 
                       reservationId + " under user: " + 
                       this.username + "\n";
                                           
        } catch (SQLException e){ e.printStackTrace(); return "Failed to pay for reservation " + reservationId + "\n";}
   
        //return "Failed to pay for reservation " + reservationId + "\n";
    }
    
    /* some utility functions below */
    
    public void beginTransaction() throws SQLException
    {
        conn.setAutoCommit(false);
        beginTransactionStatement.executeUpdate();
    }
    
    public void commitTransaction() throws SQLException
    {
        commitTransactionStatement.executeUpdate();
        conn.setAutoCommit(true);
    }
    
    public void rollbackTransaction() throws SQLException
    {
        rollbackTransactionStatement.executeUpdate();
        conn.setAutoCommit(true);
    }
    
    /**
     * Shows an example of using PreparedStatements after setting arguments. You don't need to
     * use this method if you don't want to.
     */
    private int checkFlightCapacity(int fid) throws SQLException
    {
        checkFlightCapacityStatement.clearParameters();
        checkFlightCapacityStatement.setInt(1, fid);
        ResultSet results = checkFlightCapacityStatement.executeQuery();
        results.next();
        int capacity = results.getInt("capacity");
        results.close();
        
        return capacity;
    }
    

}
