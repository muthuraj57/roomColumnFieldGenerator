# roomColumnFieldGenerator

Inspired from [RealmFieldNamesHelper](https://github.com/cmelchior/realmfieldnameshelper)

## What does it do?
This is an annotation processor which detects all Room Entites and generates a Kotlin class for each Entity with fields 
representing Column names. For example, for the following Entity,

```@Entity
class Test {
    var field1: String = ""
    var fieldTwo: Int = -1
}
```

following Kotlin class is generated.

```object TestFields {
    const val FIELD1: String = "field1"

    const val FIELD_TWO: String = "fieldTwo"
}
```
As you can see, the generated Kotlin class name would be `<EntityName>Fields` . If tableName is specified inside Entity 
annotation, that will be used. Else, Entitiy class name will be used.

## Why?
In Room, Columns of the Entities can be annotated with ColumnInfo annotation to provide extra details and change column name.
It is a best practice to provide column name through ColumnInfo annotation even though that name and the field name is same.

The reason is, Room uses the annotation value if available, else gets the column name from the field name through reflection. This might be a 
problem if proguard/r8 is enabled since Entity fields might be obfuscated and that obfuscated name will be used as column name.
This can be avoided by adding keep rules for all Entities or adding column name explicitly through @ColumnInfo annoation.

If both names are same, it would be a boilerplace to put raw string in all column names. With this library, you can do this
```
@ColumnInfo(name = TestFields.FIELD1)
var field1: String = ""
 ```
    
 instead of this
    
```
@ColumnInfo(name = "field1")
var field1: String = ""
 ```
 
 ## Installation
 
 **Step 1:** Add this in your root *build.gradle* at the end of repositories:
 
 ```
 allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
 **Step 2:** Add the dependency
 ```
 dependencies {
          ...
	        kapt 'com.github.muthuraj57:roomColumnFieldGenerator:0.2'
	}
  ```
  
  ### Note:
  This library creates Kotlin files, so Kotlin should be enabled in your project.
