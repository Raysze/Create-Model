package com.example.OutputStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.File;

public class MyObjectOutputStream extends ObjectOutputStream {

    private static File f;  

    public static MyObjectOutputStream newInstance(File file, OutputStream out)  throws IOException {  
        
        f    = file; 
        
        return new MyObjectOutputStream(out, f);  
    }  

    //因為每次寫入檔案時，OutputStream都會寫入一個標頭檔(AC ED 00 05)，所以使用複寫來先判斷是否為第一次寫入，若不是就使用RESET()
    //不加的話會有 AC Exception丟出，且無法讀出第二次開始新增的資料。
    @Override
    protected void writeStreamHeader() throws IOException {  
        if (!f.exists() || (f.exists() && f.length() == 0)) {  
            super.writeStreamHeader();  
        } else {  
            super.reset();  
        }  
    }  

    public MyObjectOutputStream(OutputStream out, File f) throws IOException {  
        super(out);  
    }
}
