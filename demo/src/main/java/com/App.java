package com;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.example.OutputStream.MyObjectOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
//import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class App {
    public static void main( String[] args ){
        try{
            new App().gen();
        }catch(IOException e){
            e.printStackTrace();
        }catch(TemplateException e){
            e.printStackTrace();
        }
    }
    public void gen() throws IOException, TemplateException{

        ///連線資料庫的資料
        //String url = "jdbc:mysql://localhost:3306/echomart?serverTimezone=UTC&useSSL=false";
        String url = "jdbc:mysql://localhost:3306/sys?serverTimezone=UTC&useSSL=false";
        String user = "root";
        String pass = "password";
        Connection conn = null; //用來連線的物件
        Map<String,Object> root = new HashMap<String,Object>();//將全部變數和物件記住的MAP，最後使用這個MAP去和FTL做對應操作
        //Map<String,Object> root2 = new HashMap<String,Object>();

        Scanner input = new Scanner(System.in);
        String tablename = input.next();

        //用來找出欄位名稱、型別、是否可以為空、是否為key
        StringBuilder sqlPK = new StringBuilder("select column_name, is_nullable, data_type,column_comment, column_key, extra, column_default from information_schema.columns where table_name = '");
        sqlPK.append(tablename).append("'");

        

        //找出與目標表格有關係的表格
        StringBuilder keystr = new StringBuilder("select * from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where table_schema =  'sys' and table_name = '")
                               .append(tablename).append("'");
        /*StringBuilder keystr = new StringBuilder("select *  from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where table_schema =  'echomart' and table_name = '")
                               .append(tablename).append("'");*/

        //將三個變數(名稱 , 值) 存到MAP中
        root.put("business_package","com.example");
        root.put("tablename",tablename);
        root.put("classname",tablename+"Model");

        //PreparedStatement這個物件可以利用內建函式直接對資料庫做操作，e.g. stmt.executeQuery(T-sql) , stmt.executeUpdate()
        PreparedStatement stmt;
        PreparedStatement stmtkey;

        try{
            //Class.forName("com.mysql.jdbc.Driver");  //已經自動連接
            conn = DriverManager.getConnection(url,user,pass);
        } catch (SQLException e){
            e.printStackTrace();
        }

        //用來記住table中各個屬性的名字和型態
        List<Map<String,String>> col = new ArrayList<>();
        //用來記住table中的KEY
        List<Map<String,String>> Keylist = new ArrayList<>();

        try{
            stmt = conn.prepareStatement(sqlPK.toString());
            ResultSet rscolumnattribute = stmt.executeQuery(sqlPK.toString());
            stmtkey = conn.prepareStatement(keystr.toString());
            ResultSet rsKeyAttribute = stmtkey.executeQuery(keystr.toString());

            //找到欄位名稱和其型別跟default值和能否空值
            while(rscolumnattribute.next()){

                Map<String,String> tabhash = new HashMap<>();

                String Na = rscolumnattribute.getString("column_name");
                String IsNu  = rscolumnattribute.getString("is_nullable");
                String Ty = rscolumnattribute.getString("data_type");
                String PK = rscolumnattribute.getString("column_key");
                String Ex = rscolumnattribute.getString("extra"); //判斷是否有auto_increment
                String de = rscolumnattribute.getString("column_default");

                if(de == null )
                {
                    de = "null";
                }

                //找到ID的型別，想說可以用在JpaRepository<String,放在這裡>的宣告中
                if(PK == "PRI")
                {
                    root.put("idType",Ty);
                }

                Ty = Ty.substring(0, 1).toUpperCase() + Ty.substring(1);
                
                tabhash.put("columnName", Na);
                tabhash.put("columnType", Ty);
                tabhash.put("isNull",IsNu);
                tabhash.put("columnKey", PK);
                tabhash.put("Extra", Ex);
                tabhash.put("default",de);

                System.out.println(Na + " " + IsNu + " " + de);
                System.out.println(Ty + " " + PK + " " + Ex  );
                col.add(tabhash);
            }
            //找到目前表格中的Foreign Key和其所在的表格
            int fk = 0;
            int prepos = 0;
            int nowpos = 0;
            while(rsKeyAttribute.next()){

                Map<String,String> keymap = new HashMap<>();

                String TableN = rsKeyAttribute.getString("table_name");
                String ColN = rsKeyAttribute.getString("column_name");
                String refTableN = rsKeyAttribute.getString("referenced_table_name");
                String refColN = rsKeyAttribute.getString("referenced_column_name");
                String Cons = rsKeyAttribute.getString("constraint_name");
                
                if(refTableN != null){
                    if(fk == 0){
                        prepos = nowpos;
                    }
                    else if(fk == 1){
                        Map<String,String> changekey = new HashMap<>();
                        changekey = (Map<String,String>)Keylist.get(prepos);
                        changekey.put("method", "many to many");
                        System.out.println("----------我是分隔線-----------");
                        System.out.println(changekey);
                        Keylist.set(prepos,changekey);
                        keymap.put("method", "many to many");
                    }
                    fk++;
                    
                }
                
                keymap.put("table_name",TableN);
                keymap.put("column_name",ColN);
                keymap.put("referenced_table_name",refTableN);
                keymap.put("referenced_column_name",refColN);
                keymap.put("constraint_name",Cons);
                if(fk > 1 ){
                    keymap.put("method", "many to many");
                }

                System.out.println(Cons);
                System.out.println(TableN + " || " + ColN);
                System.out.println(refTableN + " || " + refColN);
                System.out.println();
                Keylist.add(keymap);

                nowpos++;
            }
            if(fk > 1){
                System.out.println("這是一個many to many 的關係表");
            }
            root.put("attrs", col);
            root.put("keyattrs",Keylist);
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        input.close();

        //連接到templates資料夾中的FTL檔案
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        File a = new File("C:/Users/Ray Chen/IdeaProjects/com/demo/src/templates");  // FTL檔案位置
        try{
        cfg.setDirectoryForTemplateLoading(a);
        } catch(IOException e){
            e.printStackTrace();
        }

        ///--------------產生Model.java--------------
        //抓取 Model.ftl這個Template
        Template t = cfg.getTemplate("model.ftl");

        //要儲存的檔案位置，若沒有該資料夾，自行創建一個
        File dir = new File("C:/Users/Ray Chen/Desktop/Model/");
        if(!dir.exists()){
            dir.mkdirs();
        }
        //在檔案位置下建立一個java檔，命名的方式為表格名加上Model.java
        OutputStream fos = new FileOutputStream(new File(dir,tablename + "Model.java"));
        //可以用來寫轉換之後的位元組到檔案中
        Writer out = new OutputStreamWriter(fos);

        //將Map中儲存的值寫到template中，再存入指定的JAVA檔案中
        t.process(root, out);

        ///--------------產生Reposity.java--------------

        t = cfg.getTemplate("Repository.ftl");

        //在檔案位置下建立一個java檔，命名的方式為表格名加上Reposity.java
        fos = new FileOutputStream(new File(dir,tablename + "Repository.java"));

        out = new OutputStreamWriter(fos);

        t.process(root, out);

        ///--------------產生目前抓到的KEY關係--------------
        File filepath = new File("C:/Users/Ray Chen/Desktop/Model/" + "test.txt");
        if(!filepath.exists()){
            filepath.createNewFile();
        }
        fos = new FileOutputStream(filepath,true);  //True 表示不會覆蓋過上次寫入的資料
        try{
            writeObject(filepath,fos,Keylist);
        }catch(Exception e){
            e.printStackTrace();
        }

        //flush 將記憶體緩衝區的位元組重新整理到檔案中。
        //之後就可以釋放空間。
        //重整後關閉，關閉前必須重整(flush)。

        fos.flush();
        fos.close();

        ///--------------讀取目前抓到的KEY關係--------------
        FileInputStream freader = new FileInputStream(filepath);
        ObjectInputStream Mapread = new ObjectInputStream(freader);
        List<Map<String,String>> maplist = new ArrayList<>(); 

        try{
            while(true){

                Object item = new Object();
                item = Mapread.readObject();

                if(item == null)  break;

                maplist.add((Map<String,String>)item);
            }  
        }catch(Exception e){
            e.printStackTrace();
        }

        for(Map<String,String> i : maplist){
            System.out.println(i);
            System.out.println("--------我乃分隔線---------");
        }
        for(Map<String,String> i : maplist){
            if( i.get("referenced_table_name") != null)
                for(Map<String,String> j  : maplist){
                    if(i == j)
                        continue;
                    
                }
        }
        Mapread.close();

        System.out.println("process success");
    }


    //將KEY的資訊寫入文件檔
    public static void writeObject(File f, OutputStream os, List<Map<String,String>> Keylist) throws Exception{
       
        MyObjectOutputStream mapwrite = MyObjectOutputStream.newInstance(f, os);

        for(Map<String,String> i : Keylist){
            mapwrite.writeObject(i);
        }
        mapwrite.close();
    }


    //---------------打算將邏輯運算函式化，目前尚未用到
    public List<Map<String, String>> getkey(Connection conn, String tablename ) throws IOException, TemplateException{

        PreparedStatement stmt;
        List<Map<String,String>> finalkeylist = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select *  from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where table_schema =  'echomart' and table_name = '")
                                .append(tablename).append("'");

        try{
            stmt = conn.prepareStatement(sql.toString());
            ResultSet rs = stmt.executeQuery(sql.toString());

            while(rs.next()){
                Map<String, String> keymap = new HashMap<>();
                String TableN = rs.getString("table_name");
                String ColN = rs.getString("column_name");
                String refTableN = rs.getString("referenced_table_name");
                String refColN = rs.getString("referenced_column_name");
                String Cons = rs.getString("constraint_name");

                keymap.put("table_name",TableN);
                keymap.put("column_name",ColN);
                keymap.put("referenced_table_name",refTableN);
                keymap.put("referenced_column_name",refColN);
                keymap.put("constraint_name",Cons);
                finalkeylist.add(keymap);
            }
            conn.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
        return finalkeylist;
    }
}



//--------------想試著利用show create table的方式 抓出foreign key和關係表格---------------/--/
                /*stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery(sql);
                ResultSetMetaData data = rs.getMetaData();*/

                /*String Cr = rsKeyAttribute.getString("create table");
                System.out.println(Cr);
                int keyoffset = Cr.indexOf("PRIMARY KEY");
                int keyoffset2 = Cr.indexOf("FOREIGN KEY");
                int endoffset = Cr.lastIndexOf(")");
                String keystring = new String();

                if(keyoffset < keyoffset2)
                    keystr = Cr.substring(keyoffset,endoffset);
                else
                    keystr = Cr.substring(keyoffset2,endoffset);

                String[] keyarr = keystr.split(",");*/
                //---------------------------分隔線-----------------------------//

//-------------原本使用ResultSetMetaData方法來抓欄位名稱，唯一可惜的是無法抓出Primary Key
                //String sqlshow = new String("show create table ");
            /*for(int i = 1;i < data.getColumnCount();i++){
                Map<String,String> colhash = new HashMap<>();
                String Na = data.getColumnName(i);
                String Ty = data.getColumnTypeName(i);
                
                Ty = Ty.substring(0 , 1) + Ty.substring(1).toLowerCase();
                boolean Auinc = data.isAutoIncrement(i);
                int Isnull = data.isNullable(i);
                System.out.println(Na + "||" + Ty );
                colhash.put("columnName",Na);
                colhash.put("columnType",Ty);
                col.add(colhash);
            }*/