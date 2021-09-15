# 使用FreeMarker在Maven專案上透過資料庫中的表屬性自動生成Model(Entity)和Repository。

### 索引 
 - 安裝
 - 設定連線資料庫和檔案位置
 - 尚未完成的部分

#安裝
需要先下載JDK和安裝Maven依賴，分別使用的版本為 : JDK16 , Maven 3.8.2
### 設定連線資料庫和檔案位置
 	///連線資料庫的資料  44行
 	//jdbc:mysql://localhost:資料庫連接的PORT/資料庫名稱?serverTimezone=UTC&useSSL=false
        String url = "jdbc:mysql://localhost:3306/sys?serverTimezone=UTC&useSSL=false";
        String user = "root";	//資料庫中User的帳號
        String pass = "****";	//User的密碼
        Connection conn = null; //用來連線的物件
        //將全部變數和物件記住的MAP，最後使用這個MAP去和FTL做對應操作
		Map<String,Object> root = new HashMap<String,Object>();
		//輸入Table Name
        Scanner input = new Scanner(System.in);
        String tablename = input.next();

        //用來找出欄位名稱、型別、是否可以為空、是否為key
        StringBuilder sqlPK = new StringBuilder("select column_name, is_nullable, data_type,column_comment, column_key, extra, column_default from information_schema.columns where table_name = '");
        sqlPK.append(tablename).append("'");

        //找出與目標表格有關係的表格
		//select * from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where table_schema =  '資料庫名稱' and table_name = 
        StringBuilder keystr = new StringBuilder("select * from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where table_schema =  'sys' and table_name = '") .append(tablename).append("'");

		// FTL檔案位置  用來抓取freemarker的Template 191行
		File a = new File("C:/Users/...../demo/src/templates");
		//要儲存Model的檔案位置，若沒有該檔案，自行創建一個
        File dir = new File("C:/Users/Ray Chen/Desktop/Model/");
        if(!dir.exists()){
            dir.mkdirs();
        }
### 使用方式
**執行專案後，可以輸入資料庫中的目標表格名字，程式就會連線到資料庫抓取屬性名並建立出Model。**
### 尚未完成的部分
現在可以透過抓取資料庫中的屬性來建立一個基本的Model, 但無法製作出一個完整擁有關聯性的Model, 目前可以做到加上**Id,Column,GeneratedValue,Notnull,creationTimestamp**等標籤，也能透過**Value**來設定屬性的Default值,這個值是來
自於資料庫建立時是否有下Default value,接下來就是關於關聯性的問題,目前已經可以取出foreign key和ParentKey,但卻無
法判斷兩個表究竟是one to one,one to many等關係,有寫了一些關於建立關聯性, 我原本想說用一張表直接記住全部表的相
互關係,但發現這樣的方法可能會太過浪費且還是有機率會有錯誤, 但我覺得應該不會是正確的判斷方式, 所以還需要增加許多
邏輯運算才能夠真的做出一個完整的Model。
Repository是使用繼承JpaRepository的方法去實作的，所以就沒有多寫額外的程式碼。

