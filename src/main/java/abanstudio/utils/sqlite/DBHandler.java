/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */ 
package abanstudio.utils.sqlite;

import abanstudio.utils.sqlite.DBConn;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import net.proteanit.sql.DbUtils;


/**
 *
 * @author Reetoo
 */
public class DBHandler {
    
    public static void main(String[] args){
    
    
    }

    public static int addClip(String name, int start, int duration, String source, String ownerID){
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        
        
        String query = "INSERT into CLIPS (Name, Start, Duration, Source, OwnerID, Volume) "
                    + "VALUES (?,?,?,?,?,?);"
                    ;
            
            newDB.openConn();
            newDB.setSQL(query);
            newDB.prepStatement();
            


            newDB.pstSetString(1, name);
            newDB.pstSetInt(2, start);
            newDB.pstSetInt(3, duration); 
            newDB.pstSetString(4, source);
            newDB.pstSetString(5, ownerID);
            newDB.pstSetString(6, "1");

            newDB.executeN();
            
            query = "SELECT ID ID "
                    + "FROM CLIPS"
                    + " WHERE Name = '"+name+"';";
            newDB.setSQL(query);
            newDB.prepStatement();
            
            ResultSet rs = newDB.executeQ();

          
                        List<ArrayList<Integer>> list = DbUtils.resultSetToNestedList(rs);
                        newDB.closeConn();
                        
                        return list.get(0).get(0);
              
            
         //return list.get(0).get(0);
          //TODO 
        //Add correct parameters and code to add to database
        
    }
    public static int addClip(String name, double start, double duration, String source, String ownerID){
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        
        
        String query = "INSERT into CLIPS (Name, Start, Duration, Source, OwnerID, Volume) "
                    + "VALUES (?,?,?,?,?,?);"
                    ;
            
            newDB.openConn();
            newDB.setSQL(query);
            newDB.prepStatement();
            


            newDB.pstSetString(1, name);
            newDB.pstSetDouble(2, start);
            newDB.pstSetDouble(3, duration); 
            newDB.pstSetString(4, source);
            newDB.pstSetString(5, ownerID);
            newDB.pstSetString(6, "1");

            newDB.executeN();
            
            query = "SELECT ID ID "
                    + "FROM CLIPS"
                    + " WHERE Name = '"+name+"';";
            newDB.setSQL(query);
            newDB.prepStatement();
            
            ResultSet rs = newDB.executeQ();

          
                        List<ArrayList<Integer>> list = DbUtils.resultSetToNestedList(rs);
                        newDB.closeConn();
                        
                        return list.get(0).get(0);
              
            
         //return list.get(0).get(0);
          //TODO 
        //Add correct parameters and code to add to database
        
    }
    public static int addClip(String name, int start, int duration, String source, String ownerID, String tag){
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        
        
        String query = "INSERT into CLIPS (Name, Start, Duration, Source, OwnerID, Tags, Volume) "
                    + "VALUES (?,?,?,?,?,?,?);"
                    ;
            
            newDB.openConn();
            newDB.setSQL(query);
            newDB.prepStatement();
            


            newDB.pstSetString(1, name);
            newDB.pstSetInt(2, start);
            newDB.pstSetInt(3, duration); 
            newDB.pstSetString(4, source);
            newDB.pstSetString(5, ownerID);
            newDB.pstSetString(6, tag);
            newDB.pstSetString(7, "1");

            newDB.executeN();
            
            query = "SELECT ID ID "
                    + "FROM CLIPS"
                    + " WHERE Name = '"+name+"';";
            newDB.setSQL(query);
            newDB.prepStatement();
            
            ResultSet rs = newDB.executeQ();

          
                        List<ArrayList<Integer>> list = DbUtils.resultSetToNestedList(rs);
                        newDB.closeConn();
                        
                        return list.get(0).get(0);
              
            
         //return list.get(0).get(0);
          //TODO 
        //Add correct parameters and code to add to database
        
    }
    public static void syncFiles(){
        
        File folder = new File("assets/");
        
        for(File file : folder.listFiles()){

            if(!file.isDirectory()){
                
                String s = file.getName().split("\\.")[0];
                if(!clipExists(s)){
                    addClip(s,0,0,"disk","sync");
                }
            }
        }
        
        
    }
    public static ArrayList<String> getGuildInfo(String guildID){
        
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        newDB.openConn();
          String sql = "SELECT * "
                    +"FROM GUILDS "
                  + "WHERE ID = '"+guildID+"'"
                  + ";";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<String>> list = DbUtils.resultSetToNestedList(rs);

        ArrayList<String> row = list.get(0);
        
        
        
        
        newDB.closeConn();
         
        return row;
    }
    
    public static String getAdminRights(String userID){
          
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        newDB.openConn();
          String sql = "SELECT Admin Admin "
                    +"FROM USERS "
                  + "WHERE UserID = '"+userID+"'"
                  + ";";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<String>> list = DbUtils.resultSetToNestedList(rs);
        String admin = "null";
        
        for(ArrayList<String> sublist: list){
            admin = sublist.get(0);
        }
        
        newDB.closeConn();
         
        return admin;
    }
    public static void addAdminRights(String userID, String admin){
       DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        
        
           String query = "INSERT into USERS (UserID, Admin) "
                    + "VALUES (?,?);"
                    ;
            
            newDB.openConn();
            newDB.setSQL(query);
            newDB.prepStatement();

            newDB.pstSetString(1, userID);
            newDB.pstSetString(2, admin);
            


            newDB.executeN();
          
              
            newDB.closeConn();
    }
    public static void removeAdminRights(String userID, String admin){
          
          
         DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
       // System.out.println(bookingID);
        String sql = "DELETE FROM Users WHERE UserID = '"+userID+"';";
       // String sql = "UPDATE repair_bookings SET customer_id ='100000' WHERE IDENTITY = '"+bookingID+"';";
            newDB.openConn();
            newDB.setSQL(sql);
            newDB.prepStatement();
            newDB.executeN();
            newDB.closeConn();
    }
    public static void removeClip(int clipID){
       
         
         DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
       // System.out.println(bookingID);
        String sql = "DELETE FROM CLIPS WHERE ID = '"+clipID+"';";
       // String sql = "UPDATE repair_bookings SET customer_id ='100000' WHERE IDENTITY = '"+bookingID+"';";
            newDB.openConn();
            newDB.setSQL(sql);
            newDB.prepStatement();
            newDB.executeN();
            newDB.closeConn();
    
   
    }
    public static void addUser(String userID, String nickname, int parlay){
          DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        
        
           String query = "INSERT into personlist (USERID, Nickname, Parlay) "
                    + "VALUES (?,?,?);"
                    ;
            
            newDB.openConn();
            newDB.setSQL(query);
            newDB.prepStatement();

            newDB.pstSetString(1, userID);
            newDB.pstSetString(2, nickname);
            newDB.pstSetInt(3, parlay);


            newDB.executeN();
          
              
            newDB.closeConn();
    }

    public static void addClipToUser(int clipID, String userID){
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        
        
           String query = "INSERT into USERSTOCLIPS (UserID, ClipID) "
                    + "VALUES (?,?);"
                    ;
            
            newDB.openConn();
            newDB.setSQL(query);
            newDB.prepStatement();

            newDB.pstSetString(1, userID);
            newDB.pstSetInt(2, clipID);
            


            newDB.executeN();
          
              
            newDB.closeConn();
        
        
    }
    public static boolean clipExists(String clipName){
        
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        newDB.openConn();
          String sql = "SELECT Name Name "
                    +"FROM CLIPS "
                  + "WHERE Name = '"+clipName+"'"
                  + ";";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<String>> list = DbUtils.resultSetToNestedList(rs);
        boolean b = list.size()>0;
         newDB.closeConn();
         
         return b;
    }
    public static ArrayList<String[]> getClips(){
             DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          newDB.openConn();
          String sql = "SELECT Name Name, Volume Volume "
                    +"FROM CLIPS "
                  + ";";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<Object>> list = DbUtils.resultSetToNestedList(rs);
        ArrayList<String[]> newList = new ArrayList<>();
        for(ArrayList<Object> i : list){
            String[] data = {(String) i.get(0),i.get(1)+""};
            newList.add(data);
        }
         newDB.closeConn();
         
         return newList;
    }
    public static ArrayList<String> getClipNames(){
        
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        newDB.openConn();
        String sql = "SELECT Name Name "
                    +"FROM CLIPS "
                  + ";";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<String>> list = DbUtils.resultSetToNestedList(rs);
        ArrayList<String> newList = new ArrayList<>();
        for(ArrayList<String> i : list){
           
            newList.add(i.get(0));
        }
         newDB.closeConn();
         
         return newList;
    }
    public static ArrayList<String[]> getClipsAndTags(){
             DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          newDB.openConn();
          String sql = "SELECT Name Name, Tags Tags, Volume Volume "
                    +"FROM CLIPS "
                  + ";";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<Object>> list = DbUtils.resultSetToNestedList(rs);
        ArrayList<String[]> newList = new ArrayList<>();
        for(ArrayList<Object> i : list){

            
            String[] n = {(String) i.get(0), (String) i.get(1), i.get(2)+""};


            newList.add(n);
        }
         newDB.closeConn();
         
         return newList;
    }
    public static void setVolume(String clipID, float volume){
            DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          newDB.openConn();
          String sql = "UPDATE CLIPS SET Volume="+volume+" WHERE Name='"+clipID+"'";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        newDB.executeN();
         
        

         newDB.closeConn();
         

    }
    public static ArrayList<String> getBanners(String clip){
              
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");          
        newDB.openConn();          
        String sql = "SELECT UserID UserID "
                    +"FROM BANVETO "
                  + "WHERE BanOrVeto = 'ban' AND ClipName = '"+clip+"';";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<Object>> list = DbUtils.resultSetToNestedList(rs);
        ArrayList<String> newList = new ArrayList<>();
        for(ArrayList<Object> i : list){

            newList.add(i.get(0).toString());
            
            
        }
         newDB.closeConn();
         
         return newList;
    }
    
    public static void vetoClip(String clip, String userID){
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        
        
        String query = "INSERT into BANVETO (UserID, ClipName, BanOrVeto) "
                    + "VALUES (?,?,?);"
                    ;
            
            newDB.openConn();
            newDB.setSQL(query);
            newDB.prepStatement();
            


            newDB.pstSetString(1, userID);
            newDB.pstSetString(2, clip);
            newDB.pstSetString(3, "veto"); 

            newDB.executeN();
            newDB.closeConn();
            
    }
    public static void banClip(String clip, String userID){
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        
        
        String query = "INSERT into BANVETO (UserID, ClipName, BanOrVeto) "
                    + "VALUES (?,?,?);"
                    ;
            
            newDB.openConn();
            newDB.setSQL(query);
            newDB.prepStatement();
            


            newDB.pstSetString(1, userID);
            newDB.pstSetString(2, clip);
            newDB.pstSetString(3, "ban"); 

            newDB.executeN();
            newDB.closeConn();
            
    }
    public static void unvetoClip(String clip, String userID){
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        
        String query = "DELETE "
                + "FROM BANVETO "
                + "WHERE ClipName ='"+clip+"' AND UserID = '"+userID+"';";
        
        newDB.openConn();
        newDB.setSQL(query);
        newDB.prepStatement();
        newDB.executeN();
        newDB.closeConn();
        
    }
    public static void unbanClip(String clip, String userID){
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
        
        String query = "DELETE "
                + "FROM BANVETO "
                + "WHERE ClipName ='"+clip+"' AND UserID = '"+userID+"';";
        
        newDB.openConn();
        newDB.setSQL(query);
        newDB.prepStatement();
        newDB.executeN();
        newDB.closeConn();
        
    }
    public static ArrayList<String[]> getClips(String[] tags){
        
        ArrayList<String[]> allClips = getClipsAndTags();
        ArrayList<String[]> clips = new ArrayList<>();

        
        for(String[] s: allClips){
            
            if(s[1]!=null){
                String[] data = new String[2];
                String[] t = s[1].split(",");
            
                boolean tagged = false;
            
                for(String tag: tags){
                    for(String tg: t){
                        if(tag.equals(tg)){
                            tagged = true;
                            break;
                        }
                    }
                    if(tagged)
                        break;
                }
            

                if(tagged){

                    data[0] = s[0];
                    data[1] = s[2];
                    clips.add(data);
                }
            }

        }
        
        return clips;
        
    }
    public static ArrayList<Integer> getClips(String UserID) {
                 DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          newDB.openConn();
          String sql = "SELECT ClipID ClipID "
                    +"FROM USERSTOCLIPS "
                  + " WHERE UserID = '"+UserID+"';";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<Integer>> list = DbUtils.resultSetToNestedList(rs);
        ArrayList<Integer> newList = new ArrayList<>();
        for(ArrayList<Integer> i : list){
            newList.add(i.get(0));
        }
         newDB.closeConn();
         
         return newList;
    }
    
    
    public static String getClipID(String name){
        System.out.println("Hello");
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          newDB.openConn();
          String sql = "SELECT ID ID "
                    +"FROM CLIPS "
                  + " WHERE Name = '"+name+"';";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<Integer>> list = DbUtils.resultSetToNestedList(rs);
        String s = "";
        if(list.size()>0){
            
            s = list.get(0).get(0)+"";
        }
       
        newDB.closeConn();
        return s;
         
         
    }
        public static String getOwnerID(String name){
        System.out.println("Hello");
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          newDB.openConn();
          String sql = "SELECT OwnerID OwnerID "
                    +"FROM CLIPS "
                  + " WHERE Name = '"+name+"';";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<Integer>> list = DbUtils.resultSetToNestedList(rs);
        String s = "";
        if(list.size()>0){
            
            s = list.get(0).get(0)+"";
        }
       
        newDB.closeConn();
        return s;
         
         
    }
  
    public static void deleteClip(String id){
         DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          newDB.openConn();
          String sql = "DELETE "
                    +"FROM CLIPS "
                  + " WHERE ID = '"+id+"';";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        newDB.executeN();
        
        sql = "DELETE "
                    +"FROM USERSTOCLIPS "
                  + " WHERE ClipID = '"+id+"';";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        newDB.executeN();
        
        

        newDB.closeConn();

         
    }
    public static int getHeroID(String string) {
          DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          newDB.openConn();
          String sql = "SELECT ID "
                    +"FROM heroes "
                  + " WHERE name = '"+string+"';";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<Integer>> list = DbUtils.resultSetToNestedList(rs);
        
         newDB.closeConn();
         if(list.size()==0){
             return 0;
         }
         return list.get(0).get(0);
    }
    public static String getURL(int ID) {
                 DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          newDB.openConn();
          String sql = "SELECT link "
                    +"FROM banlist "
                  + " WHERE ID = '"+ID+"';";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<String>> list = DbUtils.resultSetToNestedList(rs);
        
         newDB.closeConn();
         if(list.size()==0){
             return "null";
         }
         return list.get(0).get(0);
    }
    public static void deleteClip(int clipID){
          DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          String sql = "DELETE FROM USERSTOCLIPS WHERE ID = '"+clipID+"';";
       // String sql = "UPDATE repair_bookings SET customer_id ='100000' WHERE IDENTITY = '"+bookingID+"';";
            newDB.openConn();
            newDB.setSQL(sql);
            newDB.prepStatement();
            newDB.executeN();
            newDB.closeConn();
            
            sql = "DELETE FROM CLIPS WHERE ID = '"+clipID+"';";
       // String sql = "UPDATE repair_bookings SET customer_id ='100000' WHERE IDENTITY = '"+bookingID+"';";
            newDB.openConn();
            newDB.setSQL(sql);
            newDB.prepStatement();
            newDB.executeN();
            newDB.closeConn();
            
    }
    public static void removeFool(int foolID){
         
         DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
       // System.out.println(bookingID);
        String sql = "DELETE FROM personlist WHERE ID = '"+foolID+"';";
       // String sql = "UPDATE repair_bookings SET customer_id ='100000' WHERE IDENTITY = '"+bookingID+"';";
            newDB.openConn();
            newDB.setSQL(sql);
            newDB.prepStatement();
            newDB.executeN();
            newDB.closeConn();
    
    }
    public static String getReason(int ID){
           DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:Database.db");
          newDB.openConn();
          String sql = "SELECT reason "
                    +"FROM banlist "
                  + " WHERE ID = '"+ID+"';";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<String>> list = DbUtils.resultSetToNestedList(rs);
        
         newDB.closeConn();
         if(list.size()==0){
             return "null";
         }
         return list.get(0).get(0);
    }
    public static List<ArrayList<Object>> pokemonAtGen(int gen){
        
        int[][] gens = {{1,151},{152,251},{252,386},{387,493},{494,649},{650,721}};
        
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:pokedex.db");
        newDB.openConn();
        String sql = "SELECT identifier, id "
                    +"FROM pokemon "
               //     + "INNER JOIN pokemon_form_generations "
               //     + "ON pokemon_form_generations.pokemon_form_id = pokemon.id "
                  + "WHERE id > '"+(gens[gen-1][0]-1)+"' AND id < '"+(gens[gen-1][1]+1)+"' ;";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<Object>> list = DbUtils.resultSetToNestedList(rs);
        
        newDB.closeConn();
        
        return list;
    }
    public static List<ArrayList<Object>> pokemonIncGen(int gen){
        
        int[][] gens = {{1,151},{152,251},{252,386},{387,493},{494,649},{650,721}};
        
        DBConn newDB = new DBConn("org.sqlite.JDBC", "jdbc:sqlite:pokedex.db");
        newDB.openConn();
        String sql = "SELECT identifier, id "
                    +"FROM pokemon "
               //     + "INNER JOIN pokemon_form_generations "
               //     + "ON pokemon_form_generations.pokemon_form_id = pokemon.id "
                  + "WHERE id < '"+(gens[gen-1][1]+1)+"' ;";
        
        newDB.setSQL(sql);
        newDB.prepStatement();
        ResultSet rs = newDB.executeQ();
         
        
        List<ArrayList<Object>> list = DbUtils.resultSetToNestedList(rs);
        
        newDB.closeConn();
        
        return list;
    }
}
