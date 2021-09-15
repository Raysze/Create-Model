package ${business_package}.model;

import javax.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Date;
import com.sun.istack.NotNull;

@Entity
@Table(name = "${tablename}")
@Data
@Getter
@Setter
public class ${classname} implements Serializable{

    <#list attrs as attr>
        <#if attr.columnKey == "PRI">
            @Id
            <#if attr.Extra == "auto_increment">
            @GeneratedValue(strategy = GenerationType.AUTO)
            </#if>
        </#if>
        <#if attr.isNull == "NO">
            @NotNull
        </#if>
            @Column(name = "${attr.columnName}")
        <#if attr.columnType == "Varchar">
            private String ${attr.columnName};
        <#elseif attr.columnType == "Datetime">
            <#if attr.default == "CURRENT_TIMESTAMP">
            @creationTimestamp   //自動填入預設值目前時間
            <#elseif attr.default != "null" && attr.default !="" &&  attr.default !="NULL">
            @Values("${attr.default}")
            </#if>
            private Date ${attr.columnName};
        <#elseif attr.columnType == "Tinyint"||attr.columnType == "Bigint"||attr.columnType == "Smallint"||attr.columnType == "Int">
            <#if attr.default != "NULL" && attr.default != "" && attr.default != "null">
            @Value("# { T(java.lang.Integer).parseInt('${attr.default!}') }")         //建立預設值
            </#if>
            private int ${attr.columnName};
        <#elseif attr.columnType == "Longtext" || attr.columnType == "Text">
            private String ${attr.columnName};
        <#else>
            <#if attr.default != "NULL" && attr.default != "" && attr.default != "null">
            @Values("${attr.default}")
            </#if>
            private ${attr.columnType} ${attr.columnName};
        </#if>
    </#list>
    
    <#list keyattrs as keyattr>
        <#if keyattr.constraint_name != "PRIMARY" && keyattr.method??>
            @ManyToMany(mapped by "${keyattr.referenced_column_name}")
            private List<${keyattr.referenced_table_name}>  ${keyattr.referenced_table_name}list;
        <#elseif keyattr.constraint_name != "PRIMARY" && keyattr.referenced_column_name??>
            @ManyToOne
            @joinColumn(name = "belong${keyattr.column_name}", referencedColumnName = "${keyattr.referenced_column_name}")
            private ${keyattr.referenced_table_name} belong${keyattr.column_name};
        </#if>
    </#list>
}